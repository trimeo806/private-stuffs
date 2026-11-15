I ran `dotnet test` and got 4 failed and 4 passed, matching what you saw. The 4 failures come from **3 bugs**, all in `src/Catalog/AssetCatalog.cs`. Two of the red tests share one cause. I haven't changed any code yet, so you can write the prompt first.

## Failures, grouped by cause

### Cause A: the date sort gets thrown away (2 tests)
`AssetCatalog.cs` lines 18-21:
```csharp
.OrderByDescending(a => a.UploadedAt)
.OrderBy(a => a.Id)
```
A second `OrderBy` does not add a tie-breaker. It starts a new sort and replaces the first one. So the list is sorted by `Id` ascending only, and `UploadedAt` is ignored. The tie-breaker should be `ThenBy(a => a.Id)`.

| Test | Expected | Actual | Why |
|---|---|---|---|
| `GetPage_FirstPage_ReturnsNewestFirst` | `[4, 2]` | `[1, 2]` | page 1 of the list sorted by Id (1,2,3,4,5) |
| `GetPage_SecondPage_ReturnsNextNewest` | `[5, 1]` | `[3, 4]` | page 2 of the same list |

**Watch out:** `GetPage_SameUploadTime_OrdersById` passes only by luck. With equal timestamps, sorting by Id alone gives `[7, 9]`, the expected answer. If the fix just deletes the `OrderBy(a => a.Id)` line, the two newest-first tests go green, but the tie case falls back to insertion order `[9, 7]` and this test turns red. The prompt has to ask for `ThenBy`, not a deletion.

### Cause B: `TotalPages` rounds down (1 test)
Line 28: `var totalPages = _assets.Count / pageSize;` is integer division, so it rounds down.

| Test | Expected | Actual |
|---|---|---|
| `GetPage_TotalPages_RoundsUp` | `3` | `2` (5 / 2 = 2) |

The spec says ceiling(TotalCount / pageSize). `GetPage_EmptyCatalog_HasZeroPages` passes because 0 / 2 = 0, and a correct ceiling also gives 0. That case has to keep giving 0 after the fix. Integer ceiling `(count + pageSize - 1) / pageSize` does that and avoids floating point.

### Cause C: `Max` throws on an empty catalog (1 test)
Line 35: `_assets.Max(a => a.SizeBytes)`. LINQ `Max` over an empty sequence of a value type throws.

| Test | Expected | Actual |
|---|---|---|
| `GetSummary_EmptyCatalog_IsAllZero` | `CatalogSummary(0, 0, 0)` | `InvalidOperationException: Sequence contains no elements` at `AssetCatalog.cs:35` |

`Sum` on an empty list already returns 0, so only `LargestBytes` needs to handle the empty case (for example `_assets.Count == 0 ? 0 : _assets.Max(...)`, or `DefaultIfEmpty()` before `Max`).

## Tests that pass and should stay green
`GetPage_SameUploadTime_OrdersById` (passes by luck, see Cause A), `GetPage_EmptyCatalog_HasZeroPages`, `GetPage_InvalidPageNumber_Throws`, `GetSummary_SumsSizesAndFindsLargest`.

## Suggested prompt for the AI panel
> In `src/Catalog/AssetCatalog.cs`, fix three bugs. Do not touch the tests or any public signature or record.
> 1. In `GetPage`, replace `.OrderBy(a => a.Id)` with `.ThenBy(a => a.Id)` so the order is newest `UploadedAt` first, then `Id` ascending for equal timestamps. Do not just remove the Id sort.
> 2. In `GetPage`, compute `TotalPages` as the integer ceiling `(TotalCount + pageSize - 1) / pageSize`, so 5 items at page size 2 gives 3 and an empty catalog gives 0.
> 3. In `GetSummary`, return `LargestBytes = 0` when the catalog is empty instead of calling `Max` on an empty sequence. `Count` and `TotalBytes` are already 0 in that case.
> Keep the argument validation and the Skip/Take paging as they are.

After the panel applies it, run `dotnet test`. You should see 8 out of 8 green, and `GetPage_SameUploadTime_OrdersById` in particular should still pass.

One more gap: no visible test covers a page past the end (spec: empty `Items` with correct totals). The current Skip/Take already returns an empty page there and the totals come from the full list, so it should already be right. A hidden test could still check it, and it would pass once Causes A and B are fixed.

I deleted the `bin/` and `obj/` folders the test run created. The source files are unchanged.
