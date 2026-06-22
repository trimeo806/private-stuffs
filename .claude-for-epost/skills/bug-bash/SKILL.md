---
name: bug-bash
description: Run a recorded manual-QA / bug-bash session. Opens a real Chrome window the user drives by hand, records the whole session to video + a structured event log, collects bugs the user calls out live, then files ONE Jira Bug with every issue as an interactive checklist and assigns it. Invoke when the user says "manual test", "bug bash", "record a testing session", "let's QA", "open a browser and record it while I test", or similar. Don't wait for the exact word — any "I'll test by hand, you record + log the bugs" request triggers this.
user-invocable: true
argument-hint: "[start-url]   e.g. /bug-bash https://tap.stg-services.altovo.io/MHH/en"
allowed-tools:
  - Bash
  - Read
  - Write
  - Monitor
  - TaskStop
  - AskUserQuestion
metadata:
  keywords: [manual test, bug bash, qa, record session, playwright, video, jira bug, checklist]
  platforms: [all]
  triggers: ["/bug-bash", "manual test", "bug bash", "record a testing session", "QA session"]
---

# bug-bash — Recorded manual-QA session → Jira Bug with a checklist

## Purpose
Let the user test a running web app **by hand** while you (a) keep a real Chrome
window open, (b) record the session to **video + a structured event log**, (c)
collect every bug they call out, and (d) at the end file **one Jira Bug** whose
description is an **interactive checklist** of all the bugs found, assigned to a
person the user picks. This is the repeatable version of the 2026-06-20 bug-bash.

## The 4 phases

### Phase 1 — Open & record the Chrome session
1. **Start URL**: use `$ARGUMENTS` if given, else ask the user (or default to the
   project's staging URL — for TravelAgentPro that's
   `https://tap.stg-services.altovo.io/MHH/en`).
2. **Pick a session dir** under the scratchpad, e.g.
   `<scratchpad>/bugbash-<short-stamp>/` (you can't call `Date.now()`; reuse the
   session id or ask). Create it.
3. **Resolve Playwright**: the recorder is CommonJS so `require()` honours
   `NODE_PATH`. Point it at a `node_modules` that has `@playwright/test` — for
   TravelAgentPro that's `<repo>/apps/web/node_modules`. Confirm chromium is
   installed (`ls ~/Library/Caches/ms-playwright/chromium-*`).
4. **Launch in the background, wrapped in `caffeinate`** so a Mac sleep can't
   freeze the session (see lesson: `ERR_NETWORK_IO_SUSPENDED`):
   ```bash
   NODE_PATH=<repo>/apps/web/node_modules \
     caffeinate -i node ~/.claude/skills/bug-bash/recorder.cjs \
     "<START_URL>" "<SESSION_DIR>"
   ```
   Run it with **Bash `run_in_background: true`**. The recorder prints
   `READY session-dir=…` once Chrome is open, then streams `NET 4xx` /
   `CONSOLE-ERROR` / `PAGE-ERROR` / `NAV` lines and writes
   `<SESSION_DIR>/session-events.jsonl`.
5. **Optionally `Monitor`** the background output file for live signals
   (filter: `READY|CONSOLE-ERROR|PAGE-ERROR|NET [45]`) so you surface console/
   network errors to the user as they browse — these become corroborating
   evidence for their bug reports.
6. Tell the user: *"Chrome is open and recording. Test away — call out each bug
   as you hit it and I'll log it. Say **done** when you're finished."*

### Phase 2 — Collect bugs live
- Each time the user reports a bug, **append it to a running list** and echo back
  a one-line confirmation (`Logged #N: <title>`). Keep a numbered list in memory
  **and** write it to `<SESSION_DIR>/buglog.md` (title + their words + the
  current page URL from the latest `NAV` event + any matching `CONSOLE-ERROR` /
  `NET 4xx` you saw). Ask a clarifying question only if a report is ambiguous.
- Don't try to fix anything now — this phase is capture only.

### Phase 3 — Stop the recording (flush the video)
- When the user says **done**, **`TaskStop`** the background recorder. It traps
  SIGTERM and calls `context.close()` which **flushes the `.webm`** — do NOT
  `kill -9`, that loses the video.
- Confirm the artifacts exist: `ls <SESSION_DIR>` → expect a `*.webm`,
  `session-events.jsonl`, `buglog.md`.

### Phase 4 — File ONE Jira Bug with a checklist + assign it
1. **Ask who to assign** with `AskUserQuestion` (offer the usual suspects by name
   + an Other option). Then resolve the account id with
   `lookupJiraAccountId` (or reuse a known id).
2. **Create the Bug** with `createJiraIssue`:
   - `cloudId` = `3f39fb03-0bc2-4fd9-9e91-50c5ddfad1cd` (easygds), `projectKey` =
     `ZAI`, `issueTypeName` = `Bug`, `assignee_account_id` = the picked id.
   - Summary: `Manual QA bug-bash (<date>) — <env/tenant> — N issues`.
   - **Description MUST use `contentFormat: "adf"` with a native `taskList`** —
     markdown `- [ ]` gets escaped to literal `\[ \]` and is NOT tickable. One
     `taskItem` (`state: "TODO"`) per bug: bold title + the user's repro words +
     the page URL + any captured console/network error. Lead with an
     Environment line and a link to the session artifacts.
3. **Attach the evidence.** Use the **no-credential path first** — it's the same
   connector that just created the ticket:
   - **buglog.md + session-events.jsonl are TEXT** → post them onto the ticket
     with `addCommentToJiraIssue` (MCP, no extra token). Put the buglog as
     markdown and the events inside a fenced ```` ```json ```` code block. If the
     events file is large (>~25 KB), post a trimmed tail (the 4xx / console-error
     lines) and keep the full file local. **This covers 2 of the 3 artifacts with
     zero credentials** — always do this.
   - **The video `.webm` is binary** → the connector can't carry it. **Upload it
     to S3** (private) and put the **clickable HTTPS console URL** in the comment:
     ```bash
     ~/.claude/skills/bug-bash/upload-s3.sh "<SESSION_DIR>"
     ```
     Uploads the whole session (video + logs + screenshots) to
     `s3://afterbook-artifacts/bug-bash/<timestamp>/` (override the bucket with
     `$BUGBASH_S3_BUCKET` or arg 2). The bucket is in `ap-southeast-1` with public
     access blocked, so a raw `s3://` path or `https://<bucket>.s3…` object URL is
     **not clickable** in Jira — that's why pasting `s3://…` onto the story never
     worked. The script now prints a **`==> SESSION FOLDER`** console URL like
     `https://ap-southeast-1.console.aws.amazon.com/s3/buckets/afterbook-artifacts?region=ap-southeast-1&prefix=bug-bash/<ts>/&showversions=false`
     plus a per-object console URL each. **Put the SESSION FOLDER url on the story**
     (as an ADF link or in the comment) — it opens for any teammate logged into the
     AWS account (e.g. Tri). It also prints the raw `s3://` paths for CLI use.
     - **Do NOT generate a presigned (anyone-with-link) URL** — that's blocked as
       data exfiltration and **user approval can't clear it**. `upload-s3.sh` only
       does the private `cp` and *prints* the `aws s3 presign …` command; if the
       user wants a public (no-AWS-login) link they run it themselves
       (`! aws s3 presign …`).
     - Alternative: `attach.sh <ISSUE_KEY> "<SESSION_DIR>"/*.webm` puts the video
       on the ticket directly via Jira REST (needs `$JIRA_API_TOKEN`; warns over
       Jira's 10 MB cap). Don't prompt for the token or scrape the keychain — if
       it's unset, fall back to the S3 path above.
4. **Move it to manual-testing**: `getTransitionsForJiraIssue` then
   `transitionJiraIssue` to **`TEST HUMAN`** (the workflow's human-test column;
   id was `4`). Skip if the user wants it left in the default column.
5. **Report back**: the `webUrl` (e.g. `https://easygds.atlassian.net/browse/ZAI-NNN`),
   how many items, the assignee, which artifacts attached, and the session-dir path.

## ADF checklist shape (copy this)
```json
{ "type": "doc", "version": 1, "content": [
  { "type": "paragraph", "content": [
    { "type": "text", "text": "Environment: ", "marks": [{ "type": "strong" }] },
    { "type": "text", "text": "<url / tenant / build>" } ] },
  { "type": "heading", "attrs": { "level": 3 }, "content": [{ "type": "text", "text": "Bugs found" }] },
  { "type": "taskList", "attrs": { "localId": "bugs" }, "content": [
    { "type": "taskItem", "attrs": { "localId": "b1", "state": "TODO" }, "content": [
      { "type": "text", "text": "Title — ", "marks": [{ "type": "strong" }] },
      { "type": "text", "text": "repro + page URL + console/network evidence" } ] }
  ] } ] }
```

## Gotchas (learned the hard way)
- **Video flush** needs a graceful stop (`TaskStop` → SIGTERM). A hard kill loses it.
- **ESM can't see `NODE_PATH`** — the recorder is `.cjs` for exactly this reason.
- **`caffeinate -i`** every long session; Mac sleep freezes the page mid-test.
- **Checkout/auth-gated flows** (Clerk login on staging) can't be auto-driven, but
  the user is driving by hand here so that's fine — they log in themselves.
- **Markdown checkboxes don't work** in Jira descriptions — always ADF `taskList`.
- The recorder also exits cleanly if the **user just closes the Chrome window**.
- **Two different Jira auths**: the MCP (issue create/edit/transition) runs on an
  OAuth session you can't reach from the shell; **attachments** need a REST API
  token (`attach.sh` reads `$JIRA_API_TOKEN` / git-credential). Don't conflate them.
