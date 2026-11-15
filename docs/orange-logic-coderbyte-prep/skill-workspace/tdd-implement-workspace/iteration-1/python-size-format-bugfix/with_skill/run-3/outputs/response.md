I fixed it with a one-character change. Exactly 1 MB now shows as "1.0 MB", and all 6 tests pass.

## Step 1: a size of exactly 1024 of a unit moves up to the next unit

**Changed**
- `sizes.py` line 9: `while size > 1024` is now `while size >= 1024`
- `tests/test_sizes.py`: added two tests next to the existing ones. I did not change any existing test.

**Tests (red -> green)**
| Test | Kind | Red because | Now |
|---|---|---|---|
| `test_formats_one_megabyte` (existing) | unit | returned `'1024.0 KB'` instead of `'1.0 MB'` | green |
| `test_formats_exactly_one_kilobyte_in_kilobytes` | unit | returned `'1024 B'` instead of `'1.0 KB'` | green |
| `test_formats_exactly_one_gigabyte_in_gigabytes` | unit | returned `'1024.0 MB'` instead of `'1.0 GB'` | green |

**Suite:** `python -m pytest`
- Before the fix: 3 passed, 1 failed (the reported test).
- After adding the two new tests: 3 passed, 3 failed.
- After the fix: 6 passed, 0 failed.

**Root cause:** line 9 used a strict comparison, `size > 1024`. A value of exactly 1024 of a unit stayed in that unit instead of moving up. This was not specific to 1 MB. It broke every exact unit boundary: 1024 B showed as "1024 B", 1 MB as "1024.0 KB", 1 GB as "1024.0 MB" and 1 TB as "1024.0 GB". The fix is to the comparison itself, so it covers all of these inputs, not only 1 MB.

**Design notes**
- Model: unchanged. `format_size` is still a pure function (int in, str out), and `storage_line` calls it unchanged. I checked that `storage_line("a.png", 1048576)` returns `"a.png: 1.0 MB"`.
- Flow, boundaries and mutation: none involved. There is no IO and nothing to mock.

**Edge cases checked by hand:** 0 shows "0 B" and 1023 shows "1023 B" (both still bytes). 1024 shows "1.0 KB". Negative values still raise `ValueError`.

**Open risks (not fixed, to stay in scope):**
- **Values just below a boundary can still show "1024.0".** For example, 1,048,575 bytes shows "1024.0 KB" because the loop only checks the size before rounding to one decimal. The same symptom can appear for sizes a few bytes under 1 MB, GB or TB. The fix would be to move up a unit when the rounded value reaches 1024. Do you want that as a next step? It is about 3 lines plus one test.
- Sizes of 1024 TB or more show as "1024.0 TB" and larger, because there is no PB unit. I left this as is, since the README lists units only up to TB.

**Next:** have someone review this diff. Then decide on the rounding case above, and add edge-case tests around each unit boundary.
