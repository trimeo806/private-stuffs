I fixed it. All 6 tests pass, including `test_formats_one_megabyte`.

**Cause:** in `sizes.py`, the loop only moved up a unit when `size > 1024`. So a value of exactly 1024 stayed in the lower unit: 1 MB came out as "1024.0 KB", and 1024 bytes came out as "1024 B".

**Fix:** a one-character change in `sizes.py`, from `while size > 1024` to `while size >= 1024`.

**Tests:** I added two boundary tests to `tests/test_sizes.py`. The first one failed before the fix; the second already passed and is there to make sure the fix does not step up too early:
- `test_formats_one_kilobyte`: 1024 now gives "1.0 KB" (it used to give "1024 B")
- `test_keeps_bytes_just_below_one_kilobyte`: 1023 still gives "1023 B"

I also called `storage_line("a.bin", 1048576)` directly, and it returns "a.bin: 1.0 MB".

**Still open (I left this alone because of your time limit):** sizes just under a unit boundary can still show "1024.0". For example, 1048575 bytes shows "1024.0 KB" because 1023.999 rounds up when formatted. One way to fix it: after rounding, if the value would display as 1024.0, step up to the next unit. Tell me if you want that done too.
