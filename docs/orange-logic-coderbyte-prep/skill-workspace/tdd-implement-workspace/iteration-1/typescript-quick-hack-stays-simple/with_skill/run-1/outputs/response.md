Done. Public assets now load from `https://cdn.example.com`. Private assets build exactly as they did before. I wrote the test first and watched it fail before changing the code.

## CDN for public assets

**Changed**
- `src/assetUrl.ts`: added a `PUBLIC_ASSET_CDN_URL` constant. `buildAssetUrl` uses it as the root for public assets and keeps `baseUrl` for private ones. The signature and the path and encoding logic are unchanged.
- `src/assetUrl.test.ts`: added 2 tests. The existing 2 are untouched.

**Tests**
| Test | Kind | Before the fix | Now |
|---|---|---|---|
| serves public assets from the cdn instead of the app base url | unit | failed (red): expected `https://cdn.example.com/assets/a3/brand%20guide.pdf`, got `https://app.example.com/...` | passes |
| keeps private assets under the app base url | unit | passed already; it's there so the change can't break private assets | passes |

**Suite:** `npx vitest run` gives 4 passed, 0 failed. Before the change: 2 passed, 0 failed. `tsc --strict --noEmit` is clean.

**Design:** pure function, no I/O. One `if` picks the host, so it stays a one-line ternary. I didn't add a config, env var, interface or new file. The CDN address is a named constant beside its only caller.

**Edge cases handled**
- Public asset with a trailing slash on `baseUrl`: the slash is ignored and the CDN is used.
- File names with spaces in public assets: still encoded (`%20`).
- Private assets on any base URL: unchanged.

**Open risks / questions (no one could answer during the run, so I went with these defaults)**
- The CDN host is hard-coded, as you asked. If staging or other environments need a different CDN, it should become a parameter or config value after the demo.
- I assumed the CDN serves the same `/assets/<id>/<file>` path as the app. If it uses a different layout (say, no `/assets` prefix), only the template string needs to change.
- I didn't check the result in a running app; only the unit tests ran.

**Next:** review the diff before the demo. Afterwards, consider moving the CDN URL into environment config.
