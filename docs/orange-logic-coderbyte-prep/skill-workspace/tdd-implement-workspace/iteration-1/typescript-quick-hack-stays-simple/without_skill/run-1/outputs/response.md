Done. Public assets now load from the CDN, and private assets work exactly as before.

Change in `src/assetUrl.ts`:

```ts
const PUBLIC_CDN_URL = "https://cdn.example.com";

export function buildAssetUrl(asset: Asset, baseUrl: string): string {
  const source = asset.visibility === "public" ? PUBLIC_CDN_URL : baseUrl;
  const root = source.replace(/\/+$/, "");
  return `${root}/assets/${asset.id}/${encodeURIComponent(asset.fileName)}`;
}
```

- `visibility: "public"` gives `https://cdn.example.com/assets/<id>/<encoded file name>`.
- `visibility: "private"` still uses the app base url, as before.
- I added one test to `src/assetUrl.test.ts` for a public asset. It also checks that the file name is encoded in the CDN url. `npx vitest run` passes: 3/3.

After the demo: the CDN url is hardcoded. If it should differ per environment, move it into config or an env var. I also assumed the CDN uses the same `/assets/<id>/<file>` paths as the app. If its paths are different, tell me and I'll change it.
