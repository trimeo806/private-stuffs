I added 11 edge-case tests (23 cases with the data rows). They found **2 real bugs** in `GetPage` that hidden tests are likely to catch. I didn't change `AssetPager.cs`. Both fixes are one line each, shown under Suspected bugs, so you can decide whether to apply them now.

## Edge cases: `AssetPager.GetPage(assets, page, pageSize)`

Framework: xUnit 2.9.2 (from `AssetLibrary.Tests/AssetLibrary.Tests.csproj`) | Test file: `AssetLibrary.Tests/AssetPagerEdgeCaseTests.cs` | Run: `dotnet test AssetLibrary.Tests`

| # | Case | Input | Expected | Why it matters | Source |
|---|------|-------|----------|----------------|--------|
| 1 | null list | `assets = null` | `ArgumentNullException`, param `assets` | missing guard, or the wrong exception type | spec |
| 2 | page below 1 | page `0`, `-1`, `int.MinValue` | `ArgumentOutOfRangeException`, param `page` | page 0 quietly treated as page 1 | spec |
| 3 | pageSize out of range | `0`, `-1`, `101`, `int.MaxValue` | `ArgumentOutOfRangeException`, param `pageSize` | off-by-one on the 1..100 range | spec |
| 4 | pageSize at the limits | `1`, `100` (150 assets) | accepted; 1 and 100 items | `<=` vs `<` on the limit | spec |
| 5 | TotalPages rounds up | (count, size) = (0,10), (10,5), (11,5), (1,100), (5,10) | 0, 2, 3, 1, 1 | floor division drops the last partial page | spec |
| 6 | last partial page | 5 assets, page 3, size 2 | 1 item (Id 5) | wrong skip/take on the tail | spec |
| 7 | page past the end | 4 assets, page 3, size 2 | empty Items; TotalCount 4, TotalPages 2, Page 3 | throwing, or returning the last page instead | spec |
| 8 | huge page number | page `int.MaxValue`, size 2 or 100 | empty Items | `(page - 1) * pageSize` overflows and wraps back to page 1 | spec |
| 9 | case-insensitive order | `beta`, `Alpha`, `charlie` | Alpha, beta, charlie | ordinal sort puts uppercase first | spec |
| 10 | tie broken by Id | `logo`/`LOGO`/`Logo` with Ids 3, 1, 2 | Ids 1, 2, 3 | missing `ThenBy(Id)` | spec |
| 11 | input not modified | unsorted list | list unchanged after the call | in-place `List.Sort` | spec |

### Result
Ran with `DOTNET_ROLL_FORWARD=Major dotnet test AssetLibrary.Tests`, because this machine only has the .NET 11 runtime. **19 passed, 5 failed** out of 24 (23 new cases plus your existing test). All 5 failures trace back to the two bugs below.

### Suspected bugs
- **Case 5: TotalPages uses floor division.** (11, 5) should give 3 and returns 2. (1, 100) and (5, 10) should give 1 and return 0. Cause: `AssetPager.cs`, `var totalPages = ordered.Count / pageSize;`. Fix: `var totalPages = (ordered.Count + pageSize - 1) / pageSize;`. This can't overflow, because `pageSize` is at most 100.
- **Case 8: a huge page number returns page 1's items instead of an empty page.** For `page = int.MaxValue`, `(page - 1) * pageSize` wraps to a negative number. `Skip` treats a negative count as 0, so you get the first page back. Cause: `AssetPager.cs`, `.Skip((page - 1) * pageSize)`. Fix: compute the offset as a `long`, or return empty first when `page > totalPages`, e.g. `var skip = (long)(page - 1) * pageSize;` and `if (skip >= ordered.Count) items = new List<Asset>();`.

### Assumptions
- Case 7: `Page` in the result echoes the requested page even past the end. TASK.md only says the totals stay filled in.
- Case 8: TASK.md has no upper limit on `page`, so a huge page counts as "a page after the last page" and should return empty Items, not throw.
- Case 9: "case-insensitive" means ordinal ignore-case, which the code already uses. I left out culture-specific inputs such as the Turkish `i`, because the spec doesn't mention culture.

All the new tests are in the new file. Your existing test is unchanged.
