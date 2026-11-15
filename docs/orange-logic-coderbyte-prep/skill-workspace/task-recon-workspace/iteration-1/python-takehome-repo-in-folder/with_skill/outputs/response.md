I read TASK.md and the whole repo. `data/movements.csv` already triggers both problems: it ends with `\n\n`, so `csv.reader` gives `[]` as its last row and `sku, quantity, reason = row` fails. It also has `CD-7,2.5,...`, which makes `int()` raise. Start with recon, not code. I've assumed you have about 110 of the 120 minutes left.

## Goal
Make `import_movements` survive trailing blank lines and non-whole quantities without changing its signature, and add `summarize(movements)`. It returns a `Summary` with per-SKU net quantities (SKUs case-insensitive, upper case, sorted) and a count of the rows that were skipped.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | Trailing blank line(s) don't crash `import_movements` | explicit | file ending `\n\n` imports all data rows |
| R2 | Rows with a non-whole quantity are skipped, not imported | explicit | `CD-7,2.5,...` is not in the result and nothing raises |
| R3 | Skipped rows are counted and reach `Summary.skipped` | explicit | `summarize(import_movements(data/movements.csv)).skipped == 1` |
| R4 | `summarize(movements)` returns `Summary(net, skipped)`, defined in `inventory/importer.py` | explicit | `from inventory.importer import summarize, Summary` works |
| R5 | `net` maps SKU to net quantity; SKUs compared case-insensitively, reported in upper case | explicit | `AB-1,5` + `ab-1,-2` gives `{"AB-1": 3}` |
| R6 | `net` ordered by SKU | explicit | `list(net) == sorted(net)` |
| R7 | `import_movements(path: Path) -> list[Movement]` unchanged | explicit | existing 2 tests pass, return value still compares equal to a list |
| R8 | No new runtime dependencies | explicit | `pyproject.toml` has no `dependencies` added |
| R9 | `NOTES.md` with decisions and what you'd do with more time | explicit | file exists, lists A1 to A6 |
| R10 | Existing import behaviour kept: header skipped, sku/reason stripped, sku case kept, negatives allowed | implied | existing tests and a sample with `-2` still pass |
| R11 | `Summary` is a frozen dataclass like `Movement` | implied | matches `models.py` style |

## Assumptions
- A1: How does `skipped` get from the import into `summarize(movements)` when the import signature can't change? -> `import_movements` returns a `list` subclass (e.g. `ImportedMovements(list)`) carrying `skipped: int`. `summarize` reads `getattr(movements, "skipped", 0)`. (The signature and the `== []` comparisons still hold, and the natural call `summarize(import_movements(path))` works. A plain list of `Movement` gives `skipped=0`.) **This is the one decision to confirm.**
- A2: Do blank or whitespace-only lines count as skipped? -> No. They are not rows. (The task calls them a normal property of every export, not bad data.)
- A3: Is `3.0` a whole number? -> Yes, import it as 3. `2.5`, `abc` and an empty value are skipped. Parse with `Decimal`, not `float`. (The task's wording is "not a whole number", so `3.0` qualifies.)
- A4: What about a row with the wrong number of columns? -> Skip it and count it in `skipped`. (It is a row "not imported", and a crash is the worst outcome for the caller.)
- A5: Does `import_movements` upper-case SKUs? -> No. Only `summarize` normalises (`strip().upper()`). (Changing import output could break callers and hidden regression tests.)
- A6: Does a SKU whose net is 0 stay in `net`? -> Yes, with 0. (It had movements, and dropping it would hide data.)
- A7: Where does `Summary` live? -> Define it in `models.py` next to `Movement` and import it in `importer.py`, so both import paths work.

## Files
| File | Action | Why |
|---|---|---|
| TASK.md | read | the spec. Its examples and boundary words are the hidden tests |
| src/inventory/importer.py | change | blank-line fix, skip and count, `summarize` |
| src/inventory/models.py | change | add frozen `Summary` dataclass |
| tests/test_importer.py | read, leave alone | given tests, style (`tmp_path` + `write_csv`) |
| tests/test_import_robustness.py, tests/test_summary.py | create | new tests, so the given file stays untouched |
| data/movements.csv | read, leave alone | real sample with both bugs, use as a fixture |
| NOTES.md | create | required write-up |
| pyproject.toml, src/inventory/__init__.py | leave alone | no deps, `pythonpath = ["src"]` already set |

Test command: `pytest`

## Edge cases
| Case | Expected | Req | Likely hidden test |
|---|---|---|---|
| `data/movements.csv` as given | net `{"AB-1": 3, "CD-7": 3}`, skipped 1 | R1-R6 | **yes** |
| file ends with one blank line | all data rows imported | R1 | **yes** |
| several trailing blank lines / whitespace-only line | ignored, not counted | R1, A2 | maybe |
| CRLF line endings | same as LF | R1 | maybe |
| quantity `2.5` | skipped, counted | R2, R3 | **yes** |
| quantity `abc` or empty | skipped, counted | R2, R3 | maybe |
| quantity `3.0` | imported as 3 | A3 | unlikely |
| `ab-1` and `AB-1` | one key `AB-1` | R5 | **yes** |
| SKUs given out of order (`CD-7` before `AB-1`) | keys sorted | R6 | **yes** |
| negative quantities cancel to 0 | key kept with 0 | R5, A6 | maybe |
| `summarize([])` | `Summary(net={}, skipped=0)` | R4 | **yes** |
| header-only file | `[]` (existing) | R7 | yes (exists) |
| row with 2 or 4 columns | skipped, counted | A4 | maybe |
| completely empty file (no header) | `[]` instead of `StopIteration` | R10 | unlikely |

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | recon (this), confirm A1 | 8 | plan confirmed |
| 1 | skip empty/whitespace rows in `import_movements` + tests | 10 | R1 rows green, given tests green |
| 2 | parse quantity with `Decimal`, skip non-whole or malformed rows, return list subclass with `skipped` | 20 | R2, R3 (import side), R7 green |
| 3 | `Summary` in models.py, `summarize` in importer.py | 20 | R4-R6, sample-file test green |
| 4 | tests for the remaining "yes"/"maybe" rows | 15 | table covered, `pytest` green |
| 5 | NOTES.md: approach, A1-A7, what's cut, with more time | 10 | R9 |
| 6 | final `pytest`, read the full diff once | 5 | green, no stray edits to given files |
| 7 | below the cut: empty-file guard, `3.0` and BOM tests, type hints polish | 10 | optional |

Budget: 88 of about 110 min above the cut, review time included, with about 20 min of buffer. Cut line: below step 6 (the empty-file guard and extra edge tests get dropped first). NOTES.md stays above the line because it's graded.

## First prompt
```
Read TASK.md, src/inventory/importer.py, src/inventory/models.py and tests/test_importer.py.
Fix only the crash in import_movements when the CSV ends with a blank line: csv.reader yields
an empty row there and `sku, quantity, reason = row` fails to unpack. Skip rows that are empty
or contain only whitespace, wherever they appear; they are not data rows and must not be counted
as skipped later.
Constraints: keep the signature `import_movements(path: Path) -> list[Movement]` and the current
behaviour (header skipped, sku and reason stripped, sku case preserved, negative quantities
allowed). No new dependencies. Do not edit tests/test_importer.py. Do not touch quantity parsing
or add summarize yet; those are the next steps.
Add tests in a new file tests/test_import_robustness.py, same style as the existing tests
(tmp_path + a write_csv helper): one trailing blank line, several trailing blank lines, a
whitespace-only line in the middle, CRLF line endings.
Explain the cause in one sentence before the diff.
```

For the next prompts, reuse the step and edge-case rows: "Step 2 of the plan: ... Same constraints. Edge cases: 2.5, abc, empty quantity, wrong column count." Reject any diff that edits the given test or changes the return type to a tuple. Give the reason in the prompt; that shows your judgement in the log.

**Before step 1, confirm A1.** Is returning a `list` subclass with a `skipped` attribute OK? The other option is a separate `import_movements_with_report(path)` that returns movements plus a skipped count, with `summarize` taking that result. Also confirm about 110 minutes left. If you don't answer, I'll go with the list subclass and 110 minutes.
