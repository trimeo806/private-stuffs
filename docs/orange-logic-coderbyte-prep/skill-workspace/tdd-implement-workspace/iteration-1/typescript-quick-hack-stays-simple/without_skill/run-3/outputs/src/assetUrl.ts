export type Visibility = "public" | "private";

export interface Asset {
  id: string;
  fileName: string;
  visibility: Visibility;
}

const CDN_BASE_URL = "https://cdn.example.com";

export function buildAssetUrl(asset: Asset, baseUrl: string): string {
  const base = asset.visibility === "public" ? CDN_BASE_URL : baseUrl;
  const root = base.replace(/\/+$/, "");
  return `${root}/assets/${asset.id}/${encodeURIComponent(asset.fileName)}`;
}
