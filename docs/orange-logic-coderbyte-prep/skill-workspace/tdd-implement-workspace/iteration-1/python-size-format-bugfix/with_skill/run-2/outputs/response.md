I fixed the bug. A file of exactly 1 MB now shows "1.0 MB" and `test_formats_one_megabyte` passes, along with the rest of the suite (7 passed).

**What was wrong:** in `sizes.py` the loop moved to the next unit only when the size was strictly greater than 1024 (`while size > 1024`). A value of exactly 1024 stayed in the smaller unit. That one line caused more than the reported case:
- 1024 bytes showed "1024 B" instead of "1.0 KB"
- 1 GB showed "1024.0 MB"
- 1,048,575 bytes is just under 1 MB, so it is less than 1024 KB, but it rounds to one decimal as "1024.0 KB". That is the same thing support saw.

**The fix (one line in `sizes.py`):**

```python
while round(size, 1) >= 1024 and unit < len(UNITS) - 1:
```

Using `>=` fixes the exact boundaries. Comparing the rounded value means a size that would display as "1024.0" moves up to the next unit instead.

**Tests:** I didn't change any existing test. I added three to `tests/test_sizes.py` and ran them before the fix to confirm they failed:

| Test | Failed before the fix with | Now |
|---|---|---|
| `test_formats_one_megabyte` (yours) | `'1024.0 KB'` | passes |
| `test_formats_exactly_one_kilobyte_as_kilobytes` | `'1024 B'` | passes |
| `test_formats_exactly_one_gigabyte_as_gigabytes` | `'1024.0 MB'` | passes |
| `test_moves_to_next_unit_when_rounding_reaches_1024` | `'1024.0 KB'` | passes |

I also checked these by hand: 0 → "0 B", 1023 → "1023 B", 1025 → "1.0 KB", 1 TB → "1.0 TB".

**Two things to know:**
- Anything of 1024 TB or more still shows in TB, for example "1024.0 TB", because TB is the largest unit in the list. That was already the behaviour and I left it alone.
- `storage_report.py` needed no change because it just calls `format_size`.

Run the tests with `python -m pytest`.
