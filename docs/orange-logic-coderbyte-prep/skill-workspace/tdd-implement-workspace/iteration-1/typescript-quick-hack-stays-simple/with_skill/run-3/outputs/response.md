Done. Public assets now load from `https://cdn.example.com`, and private assets are built exactly as before. All 3 tests pass.

## Step 1: public assets are served from the CDN

**Changed**
- `src/assetUrl.ts`: added a `CDN_BASE_URL` constant. `buildAssetUrl` uses it in place of `baseUrl` when `visibility === "public"`. The signature is unchanged, and the path and filename encoding are shared by both branches.
- `src/assetUrl.test.ts`: added one test.

```ts
const CDN_BASE_URL = "https://cdn.example.com";

export function buildAssetUrl(asset: Asset, baseUrl: string): string {
  const host = asset.visibility === "public" ? CDN_BASE_URL : baseUrl;
  const root = host.replace(/\/+$/, "");
  return `${root}/assets/${asset.id}/${encodeURIComponent(asset.fileName)}`;
}
```

**Tests (red -> green)**
| Test | Kind | Red because | Now |
|---|---|---|---|
| serves public assets from the cdn instead of the app base url | unit | expected `https://cdn.example.com/assets/a3/team%20photo.jpg`, received `https://app.example.com/assets/a3/team%20photo.jpg` | green |

**Suite:** `npx vitest run`: 3 passed, 0 failed (the baseline was 2 passed). `tsc --strict --noEmit` is clean.

**Design notes**
- Model: the existing `Asset.visibility` decides the host. No new types.
- Flow: `asset.visibility -> pick host -> trim trailing slash -> /assets/<id>/<encoded name>`
- Boundary: none. This is a pure function, so it needs no test doubles.
- Where the change lives: one line in `buildAssetUrl`. I added no new file, config, env var or abstraction.

**Edge cases handled**
- A private asset still uses `baseUrl`. The two existing tests already cover this, both with private assets, and both stay green unchanged.
- A public asset still gets the same `/assets/<id>/<encoded file name>` path and the same filename encoding.
- For a public asset, the `baseUrl` argument is ignored.

**Open risks**
- The CDN host is hard-coded. If staging or prod need different CDN hosts, it should come from config after the demo.
- I assumed the CDN serves the same `/assets/<id>/<file>` path layout as the app. If its layout differs, the URLs will 404.
- I only ran the unit tests. Nothing was checked against the real CDN.

**Next:** review this diff before merging. After the demo, consider adding tests for a public asset with special characters in the name and for an unexpected `visibility` value.
