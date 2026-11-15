import { describe, expect, it } from "vitest";
import { buildAssetUrl } from "./assetUrl";

describe("buildAssetUrl", () => {
  it("builds the url under the app base url", () => {
    const url = buildAssetUrl(
      { id: "a1", fileName: "logo.png", visibility: "private" },
      "https://app.example.com/",
    );

    expect(url).toBe("https://app.example.com/assets/a1/logo.png");
  });

  it("encodes spaces in the file name", () => {
    const url = buildAssetUrl(
      { id: "a2", fileName: "team photo.jpg", visibility: "private" },
      "https://app.example.com",
    );

    expect(url).toBe("https://app.example.com/assets/a2/team%20photo.jpg");
  });

  it("serves public assets from the cdn instead of the app base url", () => {
    const url = buildAssetUrl(
      { id: "a3", fileName: "brand guide.pdf", visibility: "public" },
      "https://app.example.com/",
    );

    expect(url).toBe("https://cdn.example.com/assets/a3/brand%20guide.pdf");
  });

  it("keeps private assets under the app base url", () => {
    const url = buildAssetUrl(
      { id: "a4", fileName: "contract.pdf", visibility: "private" },
      "https://staging.example.com",
    );

    expect(url).toBe("https://staging.example.com/assets/a4/contract.pdf");
  });
});
