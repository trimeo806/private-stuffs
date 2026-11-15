The new tests found a bug that hidden tests will almost certainly catch: `TotalPages` is wrong whenever the asset count is not an exact multiple of `pageSize`.

## The bug

`AssetLibrary/AssetPager.cs` uses integer division, which rounds down:

```csharp
var totalPages = ordered.Count / pageSize;
```

So 5 assets with `pageSize` 2 gives 2 pages instead of 3, and 1 asset with `pageSize` 10 gives 0 instead of 1. The one-line fix rounds up and still returns 0 for an empty list:

```csharp
var totalPages = (ordered.Count + pageSize - 1) / pageSize;
```

I did not edit `AssetPager.cs`. Apply that line yourself; the 3 failing tests should then pass. `Count + pageSize - 1` can't overflow here because `pageSize` is at most 100.

## Edge cases checked (taken from TASK.md, not from the code)

| # | Case | Input | Expected | Source |
|---|---|---|---|---|
| 1 | null list | `assets = null` | `ArgumentNullException` | explicit |
| 2 | page below 1 | page 0, -1, `int.MinValue` | `ArgumentOutOfRangeException` | explicit |
| 3 | pageSize out of range | 0, -1, 101 | `ArgumentOutOfRangeException` | explicit |
| 4 | pageSize at the limits | 1 and 100, with 150 assets | 1 and 100 items | explicit |
| 5 | empty list | `[]`, page 1 | no items, TotalCount 0, TotalPages 0 | explicit |
| 6 | TotalPages rounds up | (1,10), (4,2), (5,2), (101,100) | 1, 2, 3, 2 | explicit ("pages needed to show all") |
| 7 | last page is partial | 5 assets, page 3, size 2 | only asset 5 | explicit |
| 8 | page after the last | 4 assets, page 3, size 2 | no items, TotalCount 4, TotalPages 2 | explicit |
| 9 | case-insensitive order | beta, Gamma, alpha, Delta | alpha, beta, Delta, Gamma | explicit |
| 10 | name tie broken by Id | logo/LOGO/Logo with ids 7, 2, 5 | 2, 5, 7 | explicit |
| 11 | input list not modified | unsorted list | same order afterwards | explicit |

## Tests

All tests are in `AssetLibrary.Tests/AssetPagerTests.cs`, using the existing xUnit setup. Your original test is still there.

## Run result

The 20 cases (11 test methods) were run with `dotnet test`:

```
Failed!  - Failed: 3, Passed: 17, Skipped: 0, Total: 20
```

The 3 failures are all `Total_pages_rounds_up_to_cover_every_asset`:

| count, pageSize | Expected | Actual |
|---|---|---|
| 1, 10 | 1 | 0 |
| 5, 2 | 3 | 2 |
| 101, 100 | 2 | 1 |

Everything else passes: argument validation, the boundaries, empty input, the page after the last page, ordering, tie-breaking and leaving the input unchanged.

Note: this machine only has the .NET 11 runtime, so I ran the tests with `DOTNET_ROLL_FORWARD=Major`. On Coderbyte's net8.0 runner, a plain `dotnet test` is enough.
