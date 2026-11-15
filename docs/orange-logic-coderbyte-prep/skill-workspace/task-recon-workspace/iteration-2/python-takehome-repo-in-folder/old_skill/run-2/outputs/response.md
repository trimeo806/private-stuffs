I read the repo and ran the existing tests (2 pass). I didn't change any code. Here's the plan before the AI starts writing.

Two things I found first:
- `data/movements.csv` ends with `\n\n`, so the blank-line bug is real. But on that file the import fails earlier, at `CD-7,2.5`, with `ValueError: invalid literal for int()`. Once you fix the blank line you will hit task 3 straight away, so do tasks 1 and 3 together.
- **The trap:** `summarize(movements)` has to report `skipped`. But `import_movements(path) -> list[Movement]` can't change its signature, and the movements it returns don't include the rows it skipped. Decide how the skipped count reaches `summarize` before you prompt the AI (see A1).

## Goal
Make `import_movements` handle a trailing blank line and quantities that aren't whole numbers, and add `summarize(movements) -> Summary`. Keep the `import_movements` signature, add no runtime dependencies, and write `NOTES.md`.

## Requirements
| # | Requirement | Type |
|---|---|---|
| R1 | A CSV that ends with a blank line imports without crashing | explicit |
| R2 | Blank lines anywhere are ignored and don't count as `skipped` | implied |
| R3 | A row whose quantity isn't a whole number is skipped, not raised | explicit |
| R4 | Skipped rows are counted and that count reaches `Summary.skipped` | explicit |
| R5 | `summarize(movements)` is in `inventory/importer.py` and returns `Summary(net, skipped)` | explicit |
| R6 | `net` maps SKU to the summed quantity, with SKUs ordered | explicit |
| R7 | SKUs are compared case-insensitively and reported in upper case (`ab-1` and `AB-1` both become `AB-1`) | explicit |
| R8 | The `import_movements(path: Path) -> list[Movement]` signature doesn't change, and the existing tests still pass | explicit |
| R9 | No new runtime dependencies (stdlib only; pytest is dev-only) | explicit |
| R10 | `NOTES.md` covers your decisions and what you'd do with more time | explicit |
| R11 | New tests for every item above, run with `pytest` | implied |

## Assumptions
- A1: How the skipped count reaches `summarize`. Default: `import_movements` returns a `list` subclass (for example `ImportedMovements(list)`) that carries a `skipped: int` attribute. The return value is still a `list[Movement]`, so existing callers don't break. `summarize` reads `getattr(movements, "skipped", 0)`. The alternative is `summarize(movements, skipped=0)`. Record whichever you pick in NOTES.md.
- A2: "Whole number". Default: accept what `int(value.strip())` parses (`5`, `-2`, `+3`, ` 7 `). Skip `2.5`, `3.0`, `1e3`, `abc` and empty values. Accepting `3.0` would be a defensible choice too; just state it.
- A3: Other malformed rows (wrong column count, empty SKU). Default: skip them and count them in `skipped`, so the import never crashes on one bad row. Only blank lines are excluded from the count (R2).
- A4: `Summary` lives in `models.py` as a frozen dataclass, next to `Movement`. `net` is a `dict` built in sorted-key order, because a plain dict keeps insertion order.
- A5: The importer keeps the SKU exactly as it appears in the file. Upper-casing happens only in `summarize`, so other callers of `import_movements` see no change in behaviour.
- A6: A SKU whose net total is 0 stays in `net` with a value of `0`.
- A7: A header-only file and an empty file (0 bytes) both return `[]` with `skipped == 0`. Today an empty file raises `StopIteration` at `next(reader)`. Fix it cheaply with `next(reader, None)`.

## Files
| File | Action | Why |
|---|---|---|
| `TASK.md` | read | The brief. Re-check it before you submit |
| `src/inventory/importer.py` | change | Fix blank lines and bad quantities, add the skipped count, add `summarize` |
| `src/inventory/models.py` | change | Add the `Summary` dataclass, and the list subclass if you go with A1 |
| `tests/test_importer.py` | change (add only) | New tests. Leave the 2 existing tests untouched, they guard the signature |
| `NOTES.md` | create | Required |
| `data/movements.csv` | read only | Sample data. Use it in one end-to-end test |
| `pyproject.toml` | leave | Already sets `pythonpath=src`. No dependencies to add |
| `src/inventory/__init__.py` | leave | Empty, nothing needed |

## Edge cases
- File ends with `\n\n` → imports fine, `skipped == 0`
- Blank or whitespace-only line in the middle → ignored, not counted
- Empty file (0 bytes) → `[]` instead of `StopIteration`
- Header only → `[]` (an existing test already covers this)
- `2.5`, `3.0`, `abc`, an empty quantity → row skipped, `skipped += 1`
- ` 5 ` or `+5` → imported as 5
- Negative quantity (`-2`) → kept, counts in the net total (an existing test already covers this)
- Too many or too few columns → skipped and counted (A3)
- `ab-1` and `AB-1` → one key `AB-1` holding their sum
- SKU with spaces around it → stripped before the case fold
- Net total of 0 → key kept with value `0`
- SKUs in mixed input order → `list(summary.net)` comes out sorted
- `summarize([])` → `Summary(net={}, skipped=0)`
- A plain list passed to `summarize` (no `skipped` attribute) → `skipped == 0`
- `data/movements.csv` end to end → `net == {"AB-1": 3, "CD-7": 3}`, `skipped == 1`
- Unicode SKU → `.upper()` works; note `casefold` versus `upper` in NOTES.md if you care about it

## Plan
About 110 minutes are left. Commit after every step.

| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Recon (this) and confirm A1 to A3 | 5 | Decisions made |
| 1 | Write tests for a trailing blank line, a blank line mid-file and an empty file, then fix the loop in `import_movements` | 15 | The new tests and the 2 old ones pass |
| 2 | Write tests for `2.5`, `abc`, an empty quantity and a wrong column count, then skip those rows and track the count on the returned list | 20 | Bad rows are skipped, `result.skipped` is correct, the signature is unchanged |
| 3 | Add the `Summary` dataclass and `summarize`: upper-case keys, sum, sort, read `skipped` | 20 | Case-merge, ordering, empty and net-0 tests pass |
| 4 | End-to-end test on `data/movements.csv` | 10 | `{"AB-1": 3, "CD-7": 3}`, skipped 1 |
| 5 | Remaining edge-case tests (whitespace, `+5`, `3.0`, a plain list passed to `summarize`) | 10 | All tests pass |
| 6 | `NOTES.md`: decisions A1 to A7, trade-offs, what's next (logging skipped rows with line numbers, typed errors, streaming large files) | 10 | File committed |
| 7 | Final pass: re-read TASK.md, run `pytest`, diff review, no new dependencies | 10 | Clean run |
| — | Buffer | 10 | — |

Cut line: drop step 5 first, then shorten step 4 to a manual run. Never cut steps 1 to 3, 6 or 7: they cover the three graded items and the required NOTES.md.

## First prompt
```
Context: Python 3.11 package in src/inventory, tests in tests/, run with pytest.
Read TASK.md, src/inventory/importer.py, src/inventory/models.py, tests/test_importer.py.

Step 1 only. Fix import_movements so a CSV with a trailing blank line, a blank or
whitespace-only line mid-file, or a completely empty file does not crash.
Blank lines are ignored and are NOT counted as skipped rows.
Constraints: do not change the signature import_movements(path: Path) -> list[Movement];
no new dependencies; do not modify the two existing tests.
Write the failing tests first in tests/test_importer.py (use the existing write_csv
helper), run pytest to show them failing, then make the minimal fix and run pytest again.
Do not implement summarize or quantity validation yet.
```

Next prompts follow the plan: step 2 for the quantity skipping and the `skipped` count with the list subclass from A1, then step 3 for `Summary` and `summarize`.

One question for you: do you want `3.0` treated as a whole number (A2)? I've assumed no, so it gets skipped. Changing that is a one-line change in step 2.
