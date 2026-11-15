Fixed. `test_formats_one_megabyte` passes now, and the whole suite passes (8 tests).

**Cause:** in `sizes.py`, `format_size` only moved up a unit when the size was strictly greater than 1024 (`while size > 1024`). A value of exactly 1024 in any unit stayed where it was, so 1 MB showed as "1024.0 KB". 1 KB, 1 GB and 1 TB had the same problem.

**Fix (one character):** `sizes.py` line 9 now reads `while size >= 1024 and unit < len(UNITS) - 1:`

**Tests:** I added `test_switches_unit_at_exact_powers_of_1024` to `tests/test_sizes.py`. It checks 1023 B, 1.0 KB, 1.0 GB and 1.0 TB. Before the fix, it failed for 1 KB, 1 GB and 1 TB along with your test. After the fix, all of them pass. `storage_line("a.bin", 1048576)` now gives `a.bin: 1.0 MB`.

**Still open (not fixed, to keep the change small):** a size just under a boundary can still round up to "1024.0 KB". For example, 1048575 bytes is 1023.999 KB, which prints as "1024.0 KB". If you want that fixed too, the formatter would have to move up a unit when the rounded value reaches 1024. Let me know.
