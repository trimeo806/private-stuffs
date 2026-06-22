/**
 * bug-bash recorder — headed Chrome + video + structured event log for a
 * manual QA session. The user drives the browser by hand; this process keeps
 * it open and records everything until it receives SIGTERM/SIGINT (or the user
 * closes the window), at which point it flushes the video and exits.
 *
 * CommonJS (not ESM) on purpose: `require()` honours NODE_PATH, so this runs
 * from anywhere with NODE_PATH pointed at a node_modules that has @playwright/test.
 *
 *   NODE_PATH=/path/to/apps/web/node_modules \
 *     caffeinate -i node recorder.cjs <START_URL> <OUT_DIR>
 *
 * stdout streams NET / CONSOLE-ERROR / PAGE-ERROR / NAV lines (so a Monitor can
 * surface live failures); the same events + bodies land in <OUT_DIR>/session-events.jsonl.
 */
const { chromium } = require("@playwright/test");
const { mkdirSync, appendFileSync } = require("node:fs");
const path = require("node:path");

(async () => {
	const START_URL =
		process.env.BUGBASH_URL || process.argv[2] || "https://tap.stg-services.altovo.io/MHH/en";
	const OUT =
		process.env.BUGBASH_OUT || process.argv[3] || path.join(process.cwd(), "bugbash-session");
	mkdirSync(OUT, { recursive: true });
	const LOG = path.join(OUT, "session-events.jsonl");
	const ev = (o) => {
		try {
			appendFileSync(LOG, JSON.stringify({ t: new Date().toISOString(), ...o }) + "\n");
		} catch {}
	};

	const browser = await chromium.launch({ headless: false, args: ["--start-maximized"] });
	const ctx = await browser.newContext({
		viewport: null,
		recordVideo: { dir: OUT, size: { width: 1440, height: 900 } },
	});
	const page = await ctx.newPage();

	page.on("response", (r) => {
		const u = r.url();
		// app + versioned API calls only — skip static asset noise
		if (r.status() >= 400 && /\/api\/|\/v\d+\//.test(u)) {
			ev({ kind: "http", status: r.status(), url: u });
			console.log(`NET ${r.status()} ${u}`);
		}
	});
	page.on("console", (m) => {
		if (m.type() === "error") {
			const text = m.text().slice(0, 300);
			ev({ kind: "console-error", text });
			console.log("CONSOLE-ERROR " + text.slice(0, 200));
		}
	});
	page.on("pageerror", (e) => {
		const text = String(e).slice(0, 300);
		ev({ kind: "page-error", text });
		console.log("PAGE-ERROR " + text.slice(0, 200));
	});
	page.on("framenavigated", (f) => {
		if (f === page.mainFrame()) {
			ev({ kind: "nav", url: f.url() });
			console.log("NAV " + f.url());
		}
	});

	await page
		.goto(START_URL, { waitUntil: "domcontentloaded" })
		.catch((e) => console.log("goto failed: " + e.message));
	ev({ kind: "session-start", url: START_URL, out: OUT });
	console.log("READY session-dir=" + OUT);

	let closing = false;
	const shutdown = async () => {
		if (closing) return;
		closing = true;
		console.log("STOPPING — flushing video…");
		ev({ kind: "session-end" });
		try {
			await ctx.close();
		} catch {}
		try {
			await browser.close();
		} catch {}
		console.log("DONE session-dir=" + OUT);
		process.exit(0);
	};
	process.on("SIGTERM", shutdown);
	process.on("SIGINT", shutdown);
	browser.on("disconnected", shutdown); // user closed the window manually

	await new Promise(() => {}); // stay alive until a signal arrives
})();
