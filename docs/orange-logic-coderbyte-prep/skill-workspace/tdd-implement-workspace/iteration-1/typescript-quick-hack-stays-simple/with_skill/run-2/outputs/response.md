Done. Public assets now come from `https://cdn.example.com`, and private assets are built exactly as before. The whole change is 2 lines in `buildAssetUrl`, plus 2 new tests.

## Step 1: public assets load from the CDN

**Changed**
- `src/assetUrl.ts`: added `const CDN_BASE_URL = "https://cdn.example.com"`. `buildAssetUrl` uses it when `asset.visibility === "public"` and keeps `baseUrl` for everything else. The signature, path shape and file-name encoding are unchanged.
- `src/assetUrl.test.ts`: added 2 tests. The 2 existing tests are untouched.

**Tests (red -> green)**
| Test | Kind | Red because | Now |
|---|---|---|---|
| serves public assets from the cdn instead of the app base url | unit | expected `https://cdn.example.com/assets/a3/banner.png`, got `https://app.example.com/assets/a3/banner.png` | green |
| encodes the file name of a public asset served from the cdn | unit | expected `https://cdn.example.com/assets/a4/brand%20guide.pdf`, got the `app.example.com` url | green |

**Suite:** `npx vitest run` -> 4 passed, 0 failed (it was 2 passed, 0 failed before the change)

**Design notes**
- Model: the existing `Asset.visibility` field. No new type.
- Flow: `visibility -> pick host (CDN or baseUrl) -> trim trailing slash -> /assets/<id>/<encoded name>`.
- Boundary: none. This is a pure function, so it needs no test doubles.
- Mutation: none.
- I kept it as small as you asked: a named constant beside the function, with no config, environment variable, new file or extra parameter.

**Edge cases handled**
- Public asset -> served from the CDN, whatever `baseUrl` is, with or without a trailing slash.
- Private asset -> unchanged. The 2 existing private-asset tests still pass with the same assertions.
- File names with spaces -> still encoded on the CDN path.

**Open risks**
- The CDN URL is hard-coded. If staging or production need different CDN hosts, that would mean adding config later, which I did not do for the demo.
- I assumed the CDN uses the same path layout as the app (`/assets/<id>/<fileName>`). Please confirm that is how the CDN is set up.
- Any `visibility` value other than `"public"` falls through to the app base URL, so a bad value can never land on the CDN.

**Next:** review this diff before merging. After the demo, consider making the CDN host configurable per environment.
