Start with a 5 to 8 minute recon, before the AI touches anything. I read every file and ran the tests: the 2 given tests pass. `data/movements.csv` crashes today with `ValueError: invalid literal for int() with base 10: '2.5'`, before it even gets to its trailing blank line. A blank line would crash with a different `ValueError` (not enough values to unpack). I didn't change any files. Assumed time left: about 115 of 120 minutes.

## Goal
Make `import_movements` handle the real export (trailing blank line, non-integer quantities skipped and counted) without changing its signature, and add `summarize(movements)` returning a `Summary` of net quantity per upper-case SKU plus the skipped count, with a `NOTES.md`.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | A CSV ending with a blank line imports without crashing | explicit | `"sku,quantity,reason\nAB-1,5,restock\n\n"` returns 1 movement |
| R2 | A row whose quantity is not a whole number is skipped, not imported, no crash | explicit | `CD-7,2.5,adjustment` is absent; the other rows import |
| R3 | `summarize(movements)` lives in `inventory/importer.py` and returns a `Summary` | explicit | `from inventory.importer import summarize, Summary` works |
| R4 | `Summary.net` maps SKU to net quantity; SKUs merged case-insensitively, reported in upper case, ordered by SKU | explicit | `AB-1` and `ab-1` sum to one `"AB-1"` key; `list(net)` is sorted |
| R5 | `Summary.skipped` is the number of rows not imported | explicit | the sample file gives `skipped == 1` |
| R6 | `import_movements(path: Path) -> list[Movement]` is unchanged: same signature, still returns a list, and the 2 given tests still pass | explicit | given tests green, `import_movements(p) == []` still true for a header-only file |
| R7 | No new runtime dependencies (stdlib only) | explicit | `pyproject.toml` has no `dependencies` added |
| R8 | `NOTES.md` with decisions and what you'd do with more time | explicit | file exists and lists A1 to A7 |
| R9 | `Summary` is a frozen dataclass next to `Movement`, also importable from `inventory.importer` | implied | both import paths work |
| R10 | Original SKU casing is kept on `Movement` (normalising happens only in `summarize`) | implied (given test expects `["AB-1", "AB-2"]`) | given test unchanged and green |

## Assumptions
- A1: How does `summarize(movements)` know about skipped rows when it only receives `Movement` objects and `import_movements` can't change its signature? -> `import_movements` returns a `list` subclass (e.g. `ImportedMovements`) that carries a `skipped` attribute. `summarize` reads `getattr(movements, "skipped", 0)`. (The signature and list behaviour stay the same, so the existing callers, the `== []` test and `summarize(import_movements(path))` all work. A plain list of movements gives `skipped == 0`. The alternative, an extra `skipped=` parameter, would give 0 on the most likely hidden test.)
- A2: Is a blank line (or a line with only whitespace) a "row that was not imported"? -> No. It is ignored and not counted. (The export always ends with one, so counting it would report `skipped == 1` on every clean file.)
- A3: What is a "whole number"? -> Whatever `int(value.strip())` accepts: `5`, `-2`, `+3`, ` 4 `. `2.5`, `3.0`, `1e3`, `abc` and an empty value are skipped and counted. (This matches the existing `int()` parsing and keeps `quantity` an `int`. Switch to accepting `3.0` as 3 only if you'd rather read the brief literally.)
- A4: A row with the wrong number of columns or an empty SKU? -> Skip it and count it in `skipped`. (The brief says bad rows must not crash the import, and this is the least surprising generalisation.)
- A5: Order of `net`? -> A plain `dict` with keys inserted in sorted order of the upper-cased SKU (Python's default string order), using `str.upper()`. (It compares equal to a dict literal in tests and iterates in SKU order.)
- A6: A SKU whose movements cancel out to 0? -> It stays in `net` with value 0. (It had movements; dropping it hides data.)
- A7: A completely empty file (no header) -> `[]`, no crash. The first line is always the header and is skipped as today; no header detection. (This keeps the current behaviour for header-only files.)

## Files
| File | Action | Why |
|---|---|---|
| `TASK.md` | read, leave alone | brief; its words are what hidden tests are built from |
| `src/inventory/importer.py` | change | blank-line fix, skip and count, `summarize` |
| `src/inventory/models.py` | change | add the frozen `Summary` dataclass |
| `src/inventory/__init__.py` | leave alone | empty, nothing needs exporting |
| `tests/test_importer.py` | read, leave alone | given tests; they define the regression contract |
| `tests/test_import_edge_cases.py` | create | blank lines, skipped rows, sample file |
| `tests/test_summarize.py` | create | `summarize` behaviour |
| `data/movements.csv` | read, leave alone | sample data; used as an end-to-end test input |
| `pyproject.toml` | leave alone | no new dependencies; pytest config is already correct |
| `NOTES.md` | create | required write-up |

Test command: `pytest` (from the repo root; `pyproject.toml` already sets `pythonpath = ["src"]`)

## Edge cases
| Case | Expected (chosen behaviour) | Req | Likely hidden test |
|---|---|---|---|
| Sample file `data/movements.csv` | imports 3 movements (`AB-1` 5, `ab-1` -2, `CD-7` 3), `skipped == 1`; `summarize` gives `net == {"AB-1": 3, "CD-7": 3}`, `skipped == 1` | R1, R2, R4, R5 | yes |
| Trailing blank line | ignored, not counted, no crash | R1, A2 | yes |
| Several blank lines, or one in the middle | all ignored, not counted | R1, A2 | maybe |
| Whitespace-only line (`"  "`) | ignored, not counted | R1, A2 | maybe |
| Quantity `2.5` | row skipped, `skipped` +1 | R2, R5 | yes |
| Quantity `abc` or empty | row skipped, `skipped` +1 | R2, A3 | yes |
| Quantity `3.0` | row skipped, `skipped` +1 (A3: only integer literals count as whole) | R2, A3 | maybe |
| Quantity `-2`, `0`, `+3`, ` 4 ` | imported as -2, 0, 3, 4 | R2, R6 | yes for negative (given test) |
| `AB-1` and `ab-1` | one key `"AB-1"` with the summed quantity | R4 | yes |
| Unsorted input SKUs (`ZZ-1` before `AB-1`) | `list(net) == ["AB-1", "ZZ-1"]` | R4 | yes |
| Movements cancelling out to 0 | key kept with value 0 (A6) | R4 | maybe |
| `summarize([])` | `Summary(net={}, skipped=0)` | R3 | yes |
| `summarize` of a plain list (not from the importer) | `skipped == 0` (A1) | R3, A1 | maybe |
| Header-only file | `[]` (given test), summary `net == {}`, `skipped == 0` | R6 | yes (given) |
| Wrong column count (`AB-1,5`) or empty SKU | row skipped, `skipped` +1 (A4) | R5, A4 | maybe |
| Empty file (0 bytes) | `[]`, no crash (A7) | R6, A7 | unlikely |
| CRLF line endings | same result as LF (already handled by `newline=""`) | R1 | unlikely |

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Recon (this), confirm the defaults | 8 | A1 and A3 accepted or changed |
| 1 | Bug: ignore blank and whitespace-only lines in `import_movements` (failing test first) | 12 | trailing-blank-line test and the 2 given tests pass |
| 2 | Skip and count rows whose quantity isn't an integer; return the list subclass with `.skipped` (A1, A3) | 15 | `CD-7,2.5` skipped, `.skipped == 1`, given tests still green |
| 3 | `Summary` dataclass and `summarize` `net`: upper-case, merged, sorted | 18 | `net == {"AB-1": 3, "CD-7": 3}` for sample movements, sorted-order test passes |
| 4 | `summarize` `skipped` from the movements (default 0) and an end-to-end sample-file test | 10 | `summarize(import_movements("data/movements.csv")).skipped == 1` |
| 5 | `NOTES.md`: A1 to A7, the trade-off in A1, what was cut | 10 | file written |
| 6 | Full `pytest`, read the whole diff once, remove stray files | 8 | all green, diff reviewed |
| 7 | Wrong column count, empty SKU and empty file: skip and count (A4, A7) | 12 | those rows don't crash and are counted |
| 8 | Extra edge-case tests: CRLF, whitespace-only lines, `+3`, net-zero SKU; update `NOTES.md` | 10 | edge-case table covered |

Budget: 103 of about 115 min, review time included, about 12 min buffer. Steps 5 and 6 (18 min) are the final test run and write-up reserve. Cut line: below step 6. Steps 7 and 8 get dropped first; if they are, list them in `NOTES.md` under "with more time".

Steps 1 and 2 are separate on purpose: the sample file only goes green after step 2. Step 1's test must use its own small CSV, not `data/movements.csv`.

## Step 1 prompt (first implementation prompt)
No recon prompt is needed: this recon is the plan. Give the AI one step per prompt:

```
Read TASK.md, src/inventory/importer.py, src/inventory/models.py and tests/test_importer.py.
Do only this one change: import_movements must not crash on blank lines. The warehouse export
always ends with a blank line. A blank line, or a line containing only whitespace, anywhere in the
file is ignored: it produces no Movement and is not counted as a skipped row.

Test first: create tests/test_import_edge_cases.py (same style as tests/test_importer.py, reuse a
tmp_path CSV writer) with tests named after behaviour:
- "sku,quantity,reason\nAB-1,5,restock\n\n" returns exactly one Movement (AB-1, 5)
- several blank lines, including one between rows, are all ignored
- a whitespace-only line is ignored
Run pytest, show me the new tests failing, then make the fix.

Constraints: keep the signature import_movements(path: Path) -> list[Movement] and keep returning a
list; keep the original SKU casing; no new dependencies; do not edit tests/test_importer.py,
data/movements.csv or pyproject.toml. Do not handle non-integer quantities or add summarize yet;
those are later steps (data/movements.csv will still fail on its 2.5 row after this step, which is
expected).

Before the diff, explain in one sentence why the current code crashes on a blank line.
```

Defaults most worth checking:
- A1: the skipped count travels on the list `import_movements` returns. This is the only way `summarize(movements)` can report skipped rows without changing the `import_movements` signature.
- A3: `3.0` counts as not whole and is skipped.
- A2: blank lines are not counted as skipped.

Question: is about 115 minutes left right? If you've used more than about 15, drop step 8 and keep everything above the cut line as is.

Confirm the plan (or change A1 or A3) before sending the step 1 prompt. No code has been written.
