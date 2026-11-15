import { describe, expect, it } from "vitest";
import { slugifyFileName } from "./slugify";

describe("slugifyFileName", () => {
  it("lowercases and hyphenates spaces", () => {
    expect(slugifyFileName("Annual Report.PDF")).toBe("annual-report.pdf");
  });
});
