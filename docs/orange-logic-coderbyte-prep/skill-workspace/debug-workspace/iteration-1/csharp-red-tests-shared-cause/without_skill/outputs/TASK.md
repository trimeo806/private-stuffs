# Task 1 - Bugfix: failing tests

Some visible tests in `tests/Catalog.Tests` fail. Fix the code, not the tests. Do not change public signatures or record shapes.

## Spec

### GetPage(pageNumber, pageSize)
- 1-based `pageNumber`; both arguments must be >= 1, else `ArgumentOutOfRangeException`.
- Order: newest `UploadedAt` first, ties by `Id` ascending.
- `TotalPages` = ceiling(TotalCount / pageSize); empty catalog -> 0.
- Page past the end -> empty `Items`, totals still correct.

### GetSummary()
- `Count` = number of assets, `TotalBytes` = sum of sizes, `LargestBytes` = largest single size.
- Empty catalog -> all zeros.
