# Inventory importer

The warehouse system exports stock movements as CSV (`sku,quantity,reason`). This package imports them.

1. Bug: `import_movements` crashes when the CSV ends with a blank line. Exports from the warehouse system always do. Fix it.
2. Feature: add `summarize(movements)` to `inventory/importer.py`. It returns a `Summary` with `net`, a mapping of SKU to net quantity ordered by SKU, and `skipped`, the number of rows that were not imported. SKUs are compared case-insensitively and reported in upper case.
3. Rows whose quantity is not a whole number must not crash the import. Skip them and count them in `skipped`.

Constraints:

- Do not change the signature of `import_movements`; other services already call it.
- No new runtime dependencies.
- Run the tests with `pytest`.
- Add a short `NOTES.md` describing your decisions and anything you would do with more time.
