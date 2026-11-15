export type Visibility = "public" | "private";

export interface Asset {
  id: string;
  fileName: string;
  visibility: Visibility;
}

export function buildAssetUrl(asset: Asset, baseUrl: string): string {
  const root = baseUrl.replace(/\/+$/, "");
  return `${root}/assets/${asset.id}/${encodeURIComponent(asset.fileName)}`;
}
