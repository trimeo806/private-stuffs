Done. Public assets now load from the CDN; private assets are unchanged.

**Change in `src/assetUrl.ts`**: added a `CDN_BASE_URL = "https://cdn.example.com"` constant, and `buildAssetUrl` picks it as the base when `asset.visibility === "public"`. Private assets still use the `baseUrl` you pass in. Trailing-slash trimming and file-name encoding work the same for both.

**Test**: added one case to `src/assetUrl.test.ts`. A public asset with app base `https://app.example.com/` now resolves to `https://cdn.example.com/assets/a3/team%20photo.jpg`. The two existing tests already use private assets, so they confirm private URLs didn't change.

`npx vitest run`: 3/3 passing.

The CDN URL is hardcoded because you asked for a quick hack. After the demo, consider moving it to config or an env var so other environments (staging, local) don't all point at the production CDN.
