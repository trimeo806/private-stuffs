## Diagnosis: 4 failures, 3 root causes

**Reproduced:** `dotnet test tests/Catalog.Tests` -> Failed: 4, Passed: 4, Total: 8

| # | Failure(s) | Root cause (file:line) | Proof: input -> actual vs expected |
|---|---|---|---|
| 1 | `GetPage_FirstPage_ReturnsNewestFirst`, `GetPage_SecondPage_ReturnsNextNewest` | `AssetCatalog.cs:20`: a second `.OrderBy(a => a.Id)` replaces the date sort instead of breaking ties, so pages come out sorted by Id only | `FiveAssets().GetPage(1,2)` -> `[1,2]` vs `[4,2]`; `GetPage(2,2)` -> `[3,4]` vs `[5,1]` |
| 2 | `GetPage_TotalPages_RoundsUp` | `AssetCatalog.cs:28`: `_assets.Count / pageSize` is integer division, so it rounds down | 5 assets, pageSize 2 -> `TotalPages` 2 vs 3 |
| 3 | `GetSummary_EmptyCatalog_IsAllZero` | `AssetCatalog.cs:35`: `Max()` on an empty list throws (`Sum` on line 34 is fine, it returns 0) | empty catalog -> `InvalidOperationException: Sequence contains no elements` vs `(0, 0, 0)` |

No other site has the same pattern (I searched `src/` for `OrderBy`, `Max(`, `Min(`, `First(` and `/`).

**Trap:**
- Cause 1: deleting line 20 turns both page tests green but breaks `GetPage_SameUploadTime_OrdersById`, which passes today only because the list is sorted by Id. `OrderByDescending` is stable, so ids 9 and 7 with the same time stay in insert order `[9,7]` vs `[7,9]`. Use `ThenBy`.
- Cause 2: `Math.Ceiling(_assets.Count / pageSize)` still divides integers and returns 2. `(count + pageSize - 1) / pageSize` overflows when `pageSize` is near `int.MaxValue`.

**Not verified:** the traps above are traced by hand, not run.

**Fix prompts, one per cause, send in order, run tests between:**

1. > In `src/Catalog/AssetCatalog.cs` line 20, `.OrderBy(a => a.Id)` re-sorts the whole list and discards the `UploadedAt` sort, so `GetPage_FirstPage_ReturnsNewestFirst` and `GetPage_SecondPage_ReturnsNextNewest` fail. Change it to `.ThenBy(a => a.Id)`. Do not delete the line, because `GetPage_SameUploadTime_OrdersById` needs the Id tiebreak. Change only that line. Keep the signatures and leave the tests as they are. Then run `dotnet test`.
2. > In `src/Catalog/AssetCatalog.cs` line 28, `_assets.Count / pageSize` is integer division and rounds down, so `GetPage_TotalPages_RoundsUp` fails (5 items with pageSize 2 gives 2 instead of 3). Compute the ceiling without overflow: `count / pageSize + (count % pageSize == 0 ? 0 : 1)`. Do not use `Math.Ceiling` on an int quotient or `count + pageSize - 1`. An empty catalog must still give 0. Do not change signatures or tests.
3. > In `src/Catalog/AssetCatalog.cs` line 35, `_assets.Max(...)` throws on an empty catalog, so `GetSummary_EmptyCatalog_IsAllZero` fails. Return 0 for `LargestBytes` when the list is empty, for example `_assets.Count == 0 ? 0 : _assets.Max(a => a.SizeBytes)`. Do not use try/catch. Keep `Sum` and `CatalogSummary` as they are, and do not change tests.

Each fix prompt goes through tdd-implement; the red test already exists for each one. Review each diff with ai-diff-review. If the AI panel suggests deleting line 20, log that you rejected it and why (the tie-break test). That correction is worth keeping for your write-up.
