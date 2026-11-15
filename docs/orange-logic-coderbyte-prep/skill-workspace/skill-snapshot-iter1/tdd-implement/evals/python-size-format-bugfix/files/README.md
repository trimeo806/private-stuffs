# asset-sizes

Formats byte counts for the storage page of the asset library.

- `sizes.format_size(num_bytes)` returns `"512 B"`, `"1.5 KB"`, `"1.0 MB"` and so on, using 1024 per step and one decimal above bytes.
- `storage_report.storage_line(name, num_bytes)` renders one line of the storage page.

Run the tests with `python -m pytest`.
