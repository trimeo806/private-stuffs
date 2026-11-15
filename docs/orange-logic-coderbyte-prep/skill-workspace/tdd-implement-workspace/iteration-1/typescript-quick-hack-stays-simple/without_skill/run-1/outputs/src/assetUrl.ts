export type Visibility = "public" | "private";

export interface Asset {
  id: string;
  fileName: string;
  visibility: Visibility;
}

const PUBLIC_CDN_URL = "https://cdn.example.com";

export function buildAssetUrl(asset: Asset, baseUrl: string): string {
  const source = asset.visibility === "public" ? PUBLIC_CDN_URL : baseUrl;
  const root = source.replace(/\/+$/, "");
  return `${root}/assets/${asset.id}/${encodeURIComponent(asset.fileName)}`;
}
