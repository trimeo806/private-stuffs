## Edge cases: `AssetPager.GetPage`

Framework: xUnit 2.9 (detected from `AssetLibrary.Tests/AssetLibrary.Tests.csproj`) | Test file: `AssetLibrary.Tests/AssetPagerEdgeCaseTests.cs` | Run: `dotnet test AssetLibrary.Tests/AssetLibrary.Tests.csproj`

Short version: you have two real bugs that hidden tests are likely to catch. Both are one-line fixes in `AssetPager.cs`, lines 24 and 26.

| # | Case | Input | Expected | Why it matters | Source |
|---|------|-------|----------|----------------|--------|
| 1 | Total pages, partial last page | 5 assets, size 2 | `TotalPages` 3 | floor division drops the partial page | spec |
| 2 | Total pages, fewer than one page | 1 asset size 10; 99 assets size 100 | `TotalPages` 1 | floor gives 0 for a non-empty list | spec |
| 3 | Total pages, one over a multiple | 101 assets, size 100 | `TotalPages` 2 | floor gives 1 | spec |
| 4 | Total pages, exact multiple | 4 assets, size 2 | `TotalPages` 2 | catches an unconditional `+ 1` | spec |
| 5 | Huge page overflows the offset | page `int.MaxValue` with size 2 or 100; page 21474838, size 100 | empty `Items`, `TotalCount` 5 | `(page - 1) * pageSize` wraps negative, `Skip` of a negative number skips nothing, so page 1 comes back | spec |
| 6 | Page past the end | 4 assets, page 3, size 2 | empty `Items`, `TotalCount` 4, `TotalPages` 2, `Page` 3 | exception or last page returned instead | spec |
| 7 | Empty list | `[]`, page 1, size 10 | empty `Items`, `TotalCount` 0, `TotalPages` 0 | total pages 1 on empty | spec |
| 8 | Page below 1 | 0, -1, `int.MinValue` | `ArgumentOutOfRangeException`, `ParamName` "page" | page 0 treated as page 1 | spec |
| 9 | Page size out of range | 0, -1, 101 | `ArgumentOutOfRangeException`, `ParamName` "pageSize" | off-by-one on the 100 limit | spec |
| 10 | Page size at limits | 1 and 100 (150 assets) | no exception; 1 and 100 items | `>=` instead of `>` on the limit | spec |
| 11 | Null list | `null` | `ArgumentNullException`, `ParamName` "assets" | NRE instead of the right exception | spec |
| 12 | Case-insensitive order | `Zebra`, `apple`, `Mango` | `apple`, `Mango`, `Zebra` | ordinal sort puts capitals first | spec |
| 13 | Ties by Id, case variants | `logo`(9), `LOGO`(3), `Logo`(5) | Ids 3, 5, 9 | missing `ThenBy(Id)` or case-sensitive tie | spec |
| 14 | Last partial page | 5 assets, page 3, size 2 | only Id 5 | skip computed as `page * size` | spec |
| 15 | Input not modified | unsorted list | list unchanged after the call | in-place `Sort` | spec |

### Tests

Added `AssetLibrary.Tests/AssetPagerEdgeCaseTests.cs` beside your existing test (12 test methods, 23 cases with the `[Theory]` rows). Your existing `AssetPagerTests.cs` is unchanged and no package was added. Production code is untouched.

### Result

24 total: 17 passed, 7 failed. All 7 failures are real bugs (rows 1-3 and 5), not test mistakes.

Note: this machine only has the .NET 11 runtime, so I ran with `DOTNET_ROLL_FORWARD=Major`. Nothing in the project files was changed for this; on Coderbyte's .NET 8 a plain `dotnet test` works.

### Suspected bugs

- Rows 1-3: expected `TotalPages` 3 / 1 / 2, got 2 / 0 / 1. Cause: `AssetPager.cs:24`, `ordered.Count / pageSize` is floor division. Fix:
  ```csharp
  var totalPages = (ordered.Count + pageSize - 1) / pageSize;
  ```
  (No overflow risk here: `Count` cannot get near `int.MaxValue - 100` in practice. `(int)Math.Ceiling(ordered.Count / (double)pageSize)` also works.)
- Row 5: expected empty `Items` for a huge page, got the first page. Cause: `AssetPager.cs:26`, `(page - 1) * pageSize` is `int` math and wraps negative. Fix: do the multiplication in `long` and short-circuit:
  ```csharp
  var skip = (long)(page - 1) * pageSize;
  var items = skip >= ordered.Count
      ? new List<Asset>()
      : ordered.Skip((int)skip).Take(pageSize).ToList();
  ```

Both fixes are small; with ~10 minutes left I'd do line 24 first (most likely to be hidden-tested), then line 26, then rerun `dotnet test` until it is all green. I left production code alone so you can decide.

### Assumptions

- Row 13: "case-insensitive" compares names with `OrdinalIgnoreCase`, so `logo.png`, `LOGO.png` and `Logo.png` are equal names and Id decides. The task does not name a culture; I avoided inputs (accents, `_` vs letters) where ordinal and culture-aware comparison disagree, because the spec does not settle that.
- Row 6: `Page` in the result echoes the requested page (3), not the last real page.
