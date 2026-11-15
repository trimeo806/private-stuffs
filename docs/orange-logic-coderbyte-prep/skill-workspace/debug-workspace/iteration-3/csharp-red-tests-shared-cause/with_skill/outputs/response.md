## Diagnosis: 4 failures, 3 root causes

**Reproduced:** `dotnet test tests/Catalog.Tests` -> Failed 4, Passed 4, Total 8 (the repo root has no .sln, so point `dotnet test` at the test project)

| # | Failure(s) | Root cause (file:line) | Proof: input -> actual vs expected |
|---|---|---|---|
| 1 | `GetPage_FirstPage_ReturnsNewestFirst`, `GetPage_SecondPage_ReturnsNextNewest` | `AssetCatalog.cs:20`: `.OrderBy(a => a.Id)` starts a new sort that replaces the `OrderByDescending(UploadedAt)` on line 19, so pages come back in Id order. Should be `ThenBy` | FiveAssets, page 1 size 2 -> `[1, 2]` vs `[4, 2]`; page 2 -> `[3, 4]` vs `[5, 1]` |
| 2 | `GetPage_TotalPages_RoundsUp` | `AssetCatalog.cs:28`: `_assets.Count / pageSize` is integer division, so it rounds down instead of up | 5 assets, size 2 -> `TotalPages` 2 vs 3 |
| 3 | `GetSummary_EmptyCatalog_IsAllZero` | `AssetCatalog.cs:35`: `_assets.Max(...)` on an empty list throws. `Sum` on line 34 already returns 0 | empty catalog -> `InvalidOperationException: Sequence contains no elements` vs `(0, 0, 0)` |

**Trap:**
- Cause 1: deleting line 20 turns the two ordering tests green but breaks `GetPage_SameUploadTime_OrdersById`. That test passes now only by luck: the Id sort happens to put 7 before 9. Without line 20 the stable date sort keeps insertion order `[9, 7]`.
- Cause 2: `(int)Math.Ceiling(_assets.Count / pageSize)` still gives 2, because the division is done in integers before the ceiling.
- Cause 3: catching the exception or editing the test only hides the bug.

**Not verified:** the deleted-line trap is traced by hand, not run. The `grep` for `OrderBy`, `Max(` and `/ pageSize` turned up no other instances of these patterns. The repo is unchanged and build artefacts are removed.

**Fix prompts, one per cause, send in order, run `dotnet test tests/Catalog.Tests` between:**

1. > In src/Catalog/AssetCatalog.cs GetPage, line 20 `.OrderBy(a => a.Id)` replaces the UploadedAt-descending sort from line 19. Change it to `.ThenBy(a => a.Id)` so the order is newest UploadedAt first, ties by Id ascending. Do not delete the line: GetPage_SameUploadTime_OrdersById needs the tie-break. Make GetPage_FirstPage_ReturnsNewestFirst and GetPage_SecondPage_ReturnsNextNewest pass. Do not edit tests, signatures or records.
2. > In src/Catalog/AssetCatalog.cs GetPage, line 28 `_assets.Count / pageSize` uses integer division and rounds down. Compute the ceiling in integers, `(_assets.Count + pageSize - 1) / pageSize`. Do not use `Math.Ceiling` on the int quotient. An empty catalog must still give 0. Make GetPage_TotalPages_RoundsUp (5 assets, size 2 -> 3) pass and keep GetPage_EmptyCatalog_HasZeroPages green. Do not change tests or record shapes.
3. > In src/Catalog/AssetCatalog.cs GetSummary, line 35 `_assets.Max(a => a.SizeBytes)` throws "Sequence contains no elements" on an empty catalog. Return 0 for LargestBytes when there are no assets, for example `_assets.Count == 0 ? 0 : _assets.Max(...)`. No try/catch and no test edits. Make GetSummary_EmptyCatalog_IsAllZero pass (expects `new CatalogSummary(0, 0, 0)`) and keep GetSummary_SumsSizesAndFindsLargest green. Keep the CatalogSummary record and the method signature unchanged.
