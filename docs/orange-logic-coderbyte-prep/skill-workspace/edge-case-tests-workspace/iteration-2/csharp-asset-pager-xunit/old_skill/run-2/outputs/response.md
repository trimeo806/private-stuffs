The hidden tests will catch a bug: `TotalPages` is wrong whenever the last page is only partly full. 6 of the 21 new test cases fail because of it, and they all come from one line.

## The bug (fix this first, it's one line)

`AssetLibrary/AssetPager.cs`:

```csharp
var totalPages = ordered.Count / pageSize;
```

This rounds down. With 5 assets and a page size of 2 you get 2 pages instead of 3, and with 1 asset and a page size of 10 you get 0 instead of 1. Replace it with:

```csharp
var totalPages = (ordered.Count + pageSize - 1) / pageSize;
```

I didn't change your production code. I applied this line to a scratch copy and all 21 tests passed.

## Edge cases the hidden tests are likely to check

| # | Case | Input | Expected | Source |
|---|---|---|---|---|
| 1 | null list | `assets = null` | `ArgumentNullException` | explicit |
| 2 | page below 1 | page = 0, -1, int.MinValue | `ArgumentOutOfRangeException` | explicit |
| 3 | pageSize out of range | 0, -5, 101, int.MaxValue | `ArgumentOutOfRangeException` | explicit |
| 4 | pageSize at the limits | 1 and 100, with 150 items | accepted; 1 item / 150 pages, 100 items / 2 pages | explicit |
| 5 | empty list | `[]`, page 1 | empty Items, TotalCount 0, TotalPages 0 | explicit |
| 6 | TotalPages rounds up | 1/10, 4/2, 5/2, 9/10, 11/10 (count / pageSize) | 1, 2, 3, 1, 2 | explicit ("pages needed to show all of them") |
| 7 | partial last page | 5 items, page 3, size 2 | only item 5; Page and PageSize echoed back | explicit |
| 8 | page after the last page | 5 items, page 4, size 2 | empty Items, TotalCount 5, TotalPages 3 | explicit |
| 9 | case-insensitive order with Id tie-break | "Banana", "apple", "banana", "Cherry", "APPLE" | Ids 3, 5, 2, 4, 1 | explicit |
| 10 | input not modified | unsorted list | list order unchanged after the call | explicit |
| 11 | existing test | 3 items, page 1, size 2 | alpha, beta | explicit |

## Tests

These are in `AssetLibrary.Tests/AssetPagerTests.cs`. Your original test is kept, and I added 10 test methods (some are `[Theory]` with several rows), all using xUnit.

## Run result (current code)

```
Failed!  - Failed: 6, Passed: 15, Total: 21
```

These 6 fail:
- `Total_pages_rounds_up_to_include_partial_last_page`, with (1,10), (5,2), (9,10) and (11,10)
- `Accepts_page_size_at_both_limits(100, ...)`: expected 2 pages, got 1
- `Returns_empty_items_with_totals_for_page_after_last`: expected 3 pages, got 2

All 6 come from the `TotalPages` line above. After the one-line fix, all 21 pass.

**Note on running the tests:** this machine only has the .NET 11 runtime and the projects target net8.0, so I ran them with `DOTNET_ROLL_FORWARD=Major dotnet test AssetLibrary.Tests`. If your machine or Coderbyte has .NET 8, a plain `dotnet test` works.

**Assumption:** for "case-insensitive" ordering I expected `OrdinalIgnoreCase`, which is what your code uses. If the grader compares with culture-aware ordering, names with accents or other non-ASCII characters could come out in a different order. None of my tests depend on that.
