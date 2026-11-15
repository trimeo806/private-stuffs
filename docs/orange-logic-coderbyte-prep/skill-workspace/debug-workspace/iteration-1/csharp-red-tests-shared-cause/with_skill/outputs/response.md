The 4 red tests have 3 root causes, not 4. Two of the red tests come from one line. All three causes are in `src/Catalog/AssetCatalog.cs`. I ran the suite and a small throwaway program to confirm each cause. I changed no files: no production code and no tests.

## Diagnosis: 4 of 8 tests red in AssetCatalog (paging order, page count, empty summary)

**Reproduced:** `dotnet test tests/Catalog.Tests` -> `Failed: 4, Passed: 4, Total: 8`. The red ones are `GetPage_FirstPage_ReturnsNewestFirst`, `GetPage_SecondPage_ReturnsNextNewest`, `GetPage_TotalPages_RoundsUp` and `GetSummary_EmptyCatalog_IsAllZero`.
**Requirement (TASK.md):** "newest `UploadedAt` first, ties by `Id` ascending"; "`TotalPages` = ceiling(TotalCount / pageSize); empty catalog -> 0"; "Page past the end -> empty `Items`"; "Empty catalog -> all zeros".

| # | Failure(s) | Root cause (file:line) | Evidence: input -> actual vs expected | Confidence |
|---|---|---|---|---|
| 1 | `GetPage_FirstPage_ReturnsNewestFirst`, `GetPage_SecondPage_ReturnsNextNewest` | `AssetCatalog.cs:20`: `.OrderBy(a => a.Id)` comes after `.OrderByDescending(a => a.UploadedAt)`. A second `OrderBy` starts a new sort and throws away the date order. It should be `ThenBy`. The list ends up sorted by `Id` only. | The five test assets come back in order `1,2,3,4,5`. The spec order is `4,2,5,1,3`. `GetPage(1,2)` -> `[1,2]`, expected `[4,2]`. `GetPage(2,2)` -> `[3,4]`, expected `[5,1]` (the test output matches). Probe: an older asset id 1 and a newer asset id 2 -> `1,2`, spec says `2,1`. | proven |
| 2 | `GetPage_TotalPages_RoundsUp` | `AssetCatalog.cs:28`: `_assets.Count / pageSize` is integer division, so it rounds down. | 5 assets, pageSize 2 -> `2`, expected `3` (test output). Probe with 5 assets: pageSize 3 -> 1 (should be 2), pageSize 4 -> 1 (should be 2), pageSize 6 -> **0** (should be 1). The result is only right when pageSize divides the count exactly. | proven |
| 3 | `GetSummary_EmptyCatalog_IsAllZero` | `AssetCatalog.cs:35`: `_assets.Max(...)` on an empty list throws. LINQ `Max` over non-nullable `long` has no "empty" value. | Empty catalog -> `InvalidOperationException: Sequence contains no elements` thrown at `AssetCatalog.cs:35`. Expected `CatalogSummary(0, 0, 0)`. `Sum` on line 34 is fine: it returns 0 when empty (probe confirmed). | proven |

**Symptom site vs cause site:** For causes 1 and 2 the assertion fails in the tests at lines 24, 32 and 54, but the wrong value is created at lines 20 and 28 of `AssetCatalog.cs`. For cause 3 the two sites are the same line (35).
A symptom-level fix for cause 1 is a trap. Deleting line 20 to get "newest first" turns both red ordering tests green, but `GetPage_SameUploadTime_OrdersById` would go red. That test passes today only by accident, because the list is currently sorted by Id alone. With line 20 deleted, the two tied assets come back as `9,7` instead of `7,9` (probe: `OrderByDescending` alone keeps insertion order for ties). The correct fix makes all three ordering tests pass for the right reason: `OrderByDescending(UploadedAt).ThenBy(Id)` -> `7,9`.

**Blast radius:**
- Cause 1: every page is wrong, not just pages 1 and 2. `GetPage(3,2)` returns id 5 today but should return id 3. Any hidden test on a different page or a different data set will fail the same way.
- Cause 2: the page count is wrong for every count that is not an exact multiple of pageSize. When pageSize is larger than the count (e.g. 5 assets, pageSize 10) it reports **0 pages for a non-empty catalog**. The common replacement formula has a trap too. `(count + pageSize - 1) / pageSize` overflows when pageSize is near `int.MaxValue`: 5 assets with `pageSize = int.MaxValue` gives 0, but should give 1 (probe).
- Same method, not covered by the visible tests: `Skip((pageNumber - 1) * pageSize)` on line 24 overflows `int` when the page number and page size are both large. `GetPage(3, int.MaxValue)` on 5 assets returns **all 5 items**. The spec says a page past the end has empty `Items` (probe). None of the 4 red tests hit this, but a hidden "page past the end" test with a big page size would. Ordinary cases are fine: `GetPage(4,2)` returns 0 items and the totals are correct.
- Cause 3: only the empty catalog is affected. One asset gives `(1, 7, 7)`, which is correct.

**Ruled out:**
- That the tests are wrong. Each expected value matches TASK.md: hand-computing the newest-first order for the five assets gives `4,2,5,1,3`.
- That the argument checks (lines 15-16) are part of the problem. `GetPage_InvalidPageNumber_Throws` passes, and they match the spec.
- That `Sum` also fails on an empty catalog. It returns 0.
- That `GetPage_EmptyCatalog_HasZeroPages` is affected by cause 2. 0 / 2 = 0, which is correct; the bug only shows when the catalog has items.

**Fix direction (for tdd-implement):** All three changes go in `AssetCatalog`. Keep both method signatures, the `PagedResult<T>` and `CatalogSummary` record shapes, the argument checks, and every existing test unchanged.
1. `GetPage` ordering: change line 20 from `.OrderBy(a => a.Id)` to `.ThenBy(a => a.Id)`. Do not delete the line.
2. `GetPage` page count: compute the ceiling without overflow, e.g. `count == 0 ? 0 : (count - 1) / pageSize + 1`. While in this method, compute the skip amount in `long` (or check `pageNumber - 1 > (count - 1) / pageSize`) so a huge page number or page size returns empty `Items` instead of wrapping around.
3. `GetSummary`: return 0 for `LargestBytes` when the list is empty (`_assets.Count == 0 ? 0 : _assets.Max(...)`, or `DefaultIfEmpty(0)`). Leave `Count` and `Sum` as they are.

**Failing test to write first:** The 4 red tests already pin each cause, so no new test is needed to start. These would catch the blast-radius gaps (add them in `tests/Catalog.Tests/AssetCatalogTests.cs`):
- `GetPage_NewerAssetWithHigherId_ComesFirst`: id 1 on 2026-01-01, id 2 on 2026-01-09, `GetPage(1,10)` -> `[2,1]`.
- `GetPage_PageSizeLargerThanCount_HasOnePage`: five assets, `GetPage(1,10)` -> `TotalPages == 1`, 5 items.
- `GetPage_HugePageSizePastTheEnd_IsEmpty`: five assets, `GetPage(3, int.MaxValue)` -> empty `Items`, `TotalCount == 5`, `TotalPages == 1`.

**Not verified:** I did not see any hidden tests. The overflow cases are confirmed only by a throwaway program run against the current code; no test in the repo covers them.

**In-IDE fix prompt:**
> Root causes in `src/Catalog/AssetCatalog.cs` (there are 3; the two ordering tests share one):
> (1) line 20 uses `.OrderBy(a => a.Id)` after `.OrderByDescending(a => a.UploadedAt)`, which discards the date sort, so assets come back by Id only. This breaks `GetPage_FirstPage_ReturnsNewestFirst` and `GetPage_SecondPage_ReturnsNextNewest`, and makes `GetPage_SameUploadTime_OrdersById` pass only by accident.
> (2) line 28 `_assets.Count / pageSize` is integer division, so it rounds down: 5 assets with pageSize 2 gives 2 instead of 3, and 5 assets with pageSize 10 gives 0.
> (3) line 35 `_assets.Max(...)` throws `InvalidOperationException` on an empty catalog.
>
> Before changing code, add these failing tests to `tests/Catalog.Tests/AssetCatalogTests.cs`:
> - `GetPage_NewerAssetWithHigherId_ComesFirst`: id 1 dated 2026-01-01 and id 2 dated 2026-01-09, `GetPage(1,10)` expects ids `[2,1]`.
> - `GetPage_PageSizeLargerThanCount_HasOnePage`: the five standard assets, `GetPage(1,10)` expects `TotalPages` 1.
> - `GetPage_HugePageSizePastTheEnd_IsEmpty`: the five assets, `GetPage(3, int.MaxValue)` expects empty `Items`, `TotalCount` 5 and `TotalPages` 1.
>
> Then fix the causes:
> - Change line 20 to `.ThenBy(a => a.Id)`. Do not just delete it, or the tie test goes red.
> - Compute `TotalPages` as `count == 0 ? 0 : (count - 1) / pageSize + 1`. Do not use `(count + pageSize - 1) / pageSize`, which overflows when pageSize is near `int.MaxValue`.
> - Compute the skip offset so a large page number or page size gives an empty page instead of overflowing.
> - Return 0 for `LargestBytes` on an empty catalog.
>
> Keep both method signatures, the `PagedResult<T>` and `CatalogSummary` records, the argument checks, and every existing test and assertion unchanged, and add no packages. Then run `dotnet test` and show the result.
