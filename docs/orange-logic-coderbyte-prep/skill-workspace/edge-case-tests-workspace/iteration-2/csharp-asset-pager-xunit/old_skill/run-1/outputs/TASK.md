# Task: page through the asset library

Implement `AssetPager.GetPage(assets, page, pageSize)`.

- `page` is 1-based. A `page` below 1 throws `ArgumentOutOfRangeException`.
- `pageSize` must be between 1 and 100 inclusive, otherwise `ArgumentOutOfRangeException`.
- A null `assets` list throws `ArgumentNullException`.
- Assets are ordered by `Name`, case-insensitive, ties broken by `Id` ascending.
- `TotalCount` is the number of assets. `TotalPages` is the number of pages needed to show all of them (0 when there are none).
- A page after the last page returns empty `Items`; `TotalCount` and `TotalPages` are still filled in.
- The input list is not modified.
