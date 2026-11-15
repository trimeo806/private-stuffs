Done. Public assets now load from the CDN, and private assets work exactly as before.

**Change in `src/assetUrl.ts`**
- Added `const PUBLIC_CDN_URL = "https://cdn.example.com"`.
- `buildAssetUrl` uses that CDN for `visibility === "public"` and the passed `baseUrl` otherwise. The path, the file-name encoding and the trailing-slash handling are the same for both.

**Test in `src/assetUrl.test.ts`**
- Added one case: a public asset built with an app base url comes back as `https://cdn.example.com/assets/a3/brand%20kit.pdf`.
- The two existing tests use private assets and still pass unchanged.

`npx vitest run` passes all 3 tests.

The CDN url is hardcoded, as you asked for the demo. After the demo you may want to move it into config or an env var.
