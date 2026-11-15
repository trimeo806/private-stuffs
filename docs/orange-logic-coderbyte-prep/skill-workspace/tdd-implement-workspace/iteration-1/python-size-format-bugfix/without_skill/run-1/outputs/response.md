Fixed. All 5 tests pass now (`python -m pytest`).

**Cause:** `sizes.py` only moved up a unit when the size was strictly greater than 1024 (`while size > 1024`). A file of exactly 1 MB (1,048,576 bytes) reached 1024.0 KB and stopped there. The same bug made exactly 1024 bytes show as "1024 B" instead of "1.0 KB".

**Fix:** one character in `sizes.py`, `>` changed to `>=`:

```python
while size >= 1024 and unit < len(UNITS) - 1:
```

**Tests:** `test_formats_one_megabyte` was red and is now green. I added `test_formats_exactly_one_kilobyte_as_kilobytes` in `tests/test_sizes.py` (`format_size(1024) == "1.0 KB"`). It failed before the fix and passes after it.

**Spot check:** 1023 gives "1023 B", 1024 gives "1.0 KB", 1048576 gives "1.0 MB", and 1073741824 gives "1.0 GB".

**Still open:** a size just under a unit boundary still rounds up in the display. For example, 1,048,575 bytes shows "1024.0 KB" because 1023.999 rounds to 1024.0 when shown with one decimal. That isn't the reported bug, so I left it alone given your time. If you want it fixed, the code can switch to the next unit when the rounded value reaches 1024.
