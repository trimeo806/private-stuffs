Fixed. The bug was a single off-by-one in `sizes.py`. Three tests were red before the fix and all 7 pass now.

## Step 1: sizes of exactly 1024 of a unit move up to the next unit

**Changed**
- `sizes.py` line 9: `while size > 1024` is now `while size >= 1024`.
- `tests/test_sizes.py`: added 3 boundary tests at the end of the file. The existing 4 tests are unchanged.

**Tests (red -> green)**
| Test | Kind | Red because | Now |
|---|---|---|---|
| `test_formats_one_megabyte` (existing) | unit | `'1024.0 KB' != '1.0 MB'`, the reported symptom | green |
| `test_formats_exactly_one_kilobyte_as_kilobytes` | unit | `'1024 B' != '1.0 KB'` | green |
| `test_formats_exactly_one_gigabyte_as_gigabytes` | unit | `'1024.0 MB' != '1.0 GB'` | green |
| `test_formats_one_byte_below_a_kilobyte_in_bytes` | unit | passed before the fix; it guards the lower side of the boundary | green |

**Suite:** `python -m pytest` (run as `python -B -m pytest -p no:cacheprovider`)
- Before the fix: 4 passed, 3 failed. Only `test_formats_one_megabyte` was red at baseline; the other 2 failures were the new boundary tests.
- After the fix: 7 passed, 0 failed.

**Root cause:** the loop on line 9 only moved to the next unit while the size was strictly greater than 1024. A value of exactly 1024 in any unit stayed where it was. This was a general rule bug, not something specific to 1 MB. The same cause gave 1024 B -> "1024 B", 1 MB -> "1024.0 KB", 1 GB -> "1024.0 MB" and 1 TB -> "1024.0 GB". Using `>=` fixes every unit boundary.

**Design notes**
- `format_size` is already a pure function, so I tested it directly and didn't restructure it.
- `storage_report.storage_line` only passes its value to `format_size`. I checked by hand that `storage_line('logo.png', 1048576)` now returns `"logo.png: 1.0 MB"`.
- No new dependencies and no signature changes.

**Edge cases handled**
- 1023 bytes stays "1023 B".
- Exactly 1024 of each unit becomes "1.0" of the next unit.
- Negative input still raises `ValueError`.
- At 1024 TB and above the output stays in TB (for example "1024.0 TB"), because TB is the largest unit.

**Open risks (not fixed, outside this bug)**
- **Rounding just below a boundary:** `format_size(1048575)` (1 byte under 1 MB) still returns "1024.0 KB". The value is 1023.999 KB and `:.1f` rounds it up.
  - Support could see the same string for a slightly smaller file.
  - Fixing it means rounding before choosing the unit. That changes the output format near every boundary, so I left it out of this fix.
  - **Question:** do you want that handled as well?

**Next:** if you have time left, review this one-line diff, then add edge-case tests for the rounding case above.
