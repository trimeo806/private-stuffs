I read the whole repo and ran the 2 existing tests. Both pass. I changed no files. Two things to know before you start:

- **`data/movements.csv` hits the quantity bug before the blank-line bug.** The import crashes on row `CD-7,2.5` with `ValueError: invalid literal for int()`, before it reaches the blank last line. So the sample file can't be the test for the blank-line fix. It's the end-to-end test once all three fixes are in.
- **The brief contradicts itself on `skipped`.** `summarize(movements)` only receives the movements that were imported, so on its own it can't know how many rows were not imported. You also can't change the signature of `import_movements`. A1 below picks how to get around this.

## Goal
Make `import_movements` survive trailing blank lines and quantities that aren't whole numbers, counting the rows it skips, and add `summarize(movements) -> Summary(net, skipped)`. Its signature stays the same.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | A CSV ending in a blank line imports without crashing | explicit | a file ending `\n\n` returns its rows |
| R2 | `summarize(movements)` returns a `Summary` whose `net` maps SKU to net quantity, ordered by SKU | explicit | `list(summary.net)` is sorted |
| R3 | SKUs are compared case-insensitively and reported in upper case | explicit | `AB-1` and `ab-1` merge into the key `AB-1` |
| R4 | `skipped` is the number of rows that were not imported | explicit | sample file gives `skipped == 1` |
| R5 | A row whose quantity isn't a whole number is skipped, doesn't crash, and is counted | explicit | `2.5` row is left out and counted |
| R6 | `import_movements(path: Path) -> list[Movement]` is unchanged | explicit | the 2 existing tests pass untouched |
| R7 | No new runtime dependencies | explicit | `pyproject.toml` has no `dependencies` added |
| R8 | `NOTES.md` covers your decisions and what you'd do with more time | explicit | file exists and lists A1 to A8 |
| R9 | `Summary` can be imported from `inventory.importer` | implied | `from inventory.importer import summarize, Summary` works |
| R10 | `import_movements` still returns a real list with the SKUs exactly as written in the file | implied | `== []` still works and `ab-1` stays `ab-1` |

## Assumptions
- **A1: How does `summarize` learn `skipped`?** -> `import_movements` returns a subclass of `list[Movement]` that carries a `skipped` int. `summarize` reads `getattr(movements, "skipped", 0)`, so a plain list gives 0. (The signature and return type stay the same for existing callers, and `summarize(import_movements(path))` reports the right count. The alternatives each break something: returning a tuple breaks callers, a module-level global goes stale between calls, and `summarize(path)` contradicts the brief.)
- **A2: Where does `Summary` live?** -> as a frozen dataclass next to `Movement` in `models.py`, imported into `importer.py`, with `net: dict[str, int]` and `skipped: int`. (It works with either import path and matches the existing `Movement` style.)
- **A3: Do blank or whitespace-only lines count in `skipped`?** -> No, they are ignored. (They aren't rows. Counting them would make every warehouse export report at least 1 skipped.)
- **A4: Is `"3.0"` a whole number?** -> No. Parse strictly with `int()` after stripping, so `2.5`, `3.0`, `abc` and an empty value are all skipped. `" 4 "` and `"+4"` are accepted. (This matches the current `int()` parsing and the `int` field. Switch only if you'd rather accept `3.0` as 3.)
- **A5: Is a SKU whose net is 0 kept?** -> Yes, as `0`. (It's still a SKU that had movements, and dropping it would hide data.)
- **A6: Is a row with the wrong number of columns counted?** -> It's skipped and counted. (`skipped` is defined as "rows not imported", and crashing is worse.)
- **A7: Does `import_movements` upper-case SKUs?** -> No. Only `summarize` upper-cases. (The existing test and other services rely on the raw SKU.)
- **A8: What does "ordered by SKU" mean?** -> A plain `dict` whose keys are inserted in ordinal string order of the upper-case SKU, so `AB-10` comes before `AB-2`. (`dict` equality tests still pass, and insertion order shows the sort.)
- A9: An empty file with no header line returns `[]` instead of raising `StopIteration`. (Least surprise. Low priority.)

## Files
| File | Action | Why |
|---|---|---|
| `TASK.md` | read, leave alone | the brief |
| `src/inventory/importer.py` | change | blank-line fix, skipping rows, `summarize` |
| `src/inventory/models.py` | change | add `Summary` and the list subclass that carries `skipped` |
| `src/inventory/__init__.py` | leave alone | empty, not needed |
| `tests/test_importer.py` | read, leave alone | given tests; copy their `tmp_path` / `write_csv` style |
| `tests/test_import_skipping.py` | create | tests for blank lines and skipped rows |
| `tests/test_summarize.py` | create | tests for `summarize`, plus the sample-file end-to-end test |
| `data/movements.csv` | read, leave alone | end-to-end fixture |
| `pyproject.toml` | leave alone | pytest config is already set; no new dependencies |
| `NOTES.md` | create | required write-up |

Test command: `pytest` from the repo root (`pythonpath = ["src"]` is already configured).

## Edge cases
| Case | Expected (chosen behaviour) | Req | Likely hidden test |
|---|---|---|---|
| `data/movements.csv` through `summarize(import_movements(...))` | `net == {"AB-1": 3, "CD-7": 3}` with keys in that order, `skipped == 1` | R1-R5 | yes |
| File ends with `\n\n` | rows imported, no exception, blank line not counted (A3) | R1 | yes |
| Quantity `2.5` | row left out, `skipped` goes up by 1, no exception | R5 | yes |
| `AB-1` and `ab-1` | one key `AB-1` whose net is the sum | R3 | yes |
| Negative quantity `-2` | imported and subtracted from net | R2, R6 | yes |
| Header-only file | `[]`, and summarize gives `Summary(net={}, skipped=0)` | R6 | yes (already covered) |
| `summarize([])` | `Summary(net={}, skipped=0)` | R2 | yes |
| Plain list of `Movement` passed to `summarize` | `skipped == 0` (A1) | R4 | maybe |
| Quantity `abc` or empty | skipped and counted (A4) | R5 | maybe |
| Quantity `3.0` | skipped and counted, because parsing is strict `int()` (A4) | R5 | maybe |
| Whitespace-only line or blank line mid-file | ignored, not counted (A3) | R1 | maybe |
| Row with 2 or 4 columns | skipped and counted (A6) | R4 | maybe |
| SKUs that net to 0 | kept, with value `0` (A5) | R2 | maybe |
| `AB-10` vs `AB-2` | `AB-10` first, by ordinal sort (A8) | R2 | low |
| `import_movements` on `ab-1` | SKU stays `ab-1` (A7) | R10 | yes (regression) |

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Recon (this) and confirming it | 8 | plan confirmed |
| 1 | Blank-line bug: failing test first, then ignore empty and whitespace-only rows | 12 | new test plus the 2 existing tests pass |
| 2 | Skip and count rows with a non-whole quantity or the wrong column count; return the list subclass carrying `skipped` (A1) | 20 | the `2.5` test passes; sample file imports 3 movements with `skipped == 1` |
| 3 | `Summary` plus `summarize` net: upper-case, sum, sorted keys, `skipped` read from the list (A1, A2, A8) | 20 | `summarize` tests pass, including a plain list giving `skipped == 0` |
| 4 | End-to-end test on `data/movements.csv` | 8 | `{"AB-1": 3, "CD-7": 3}` and `skipped == 1` |
| 5 | `NOTES.md`: A1 to A8, what was cut, ideas for more time (row-level skip reasons, `Decimal` quantities, BOM / encoding) | 10 | file written |
| 6 | Full `pytest` run plus a review of the whole diff (signature, no dependencies, given tests untouched) | 10 | all green, diff reviewed |
| -- | **cut line** | | |
| 7 | Extra edge-case tests: `abc`, empty value, `3.0`, whitespace lines, wrong column count, net 0, `AB-10`/`AB-2`, empty file | 15 | the "maybe" rows above are covered; re-run `pytest` |

Budget: 103 of about 115 minutes left, review time included, with about 12 minutes of buffer. Steps 5 and 6 are the final 20-minute reserve. Cut line: below step 6, so the extra edge-case tests are dropped first. Clock checkpoints from when you started: step 1 done by about 25 minutes, step 3 by about 65, step 6 by about 95. If step 3 isn't done by 75, skip step 7 and go straight to `NOTES.md`.

Start here: confirm A1 and A4, then paste the step 1 prompt. Send one prompt per plan step and review each diff before sending the next. This recon is the plan, so you don't need to ask the AI to plan from scratch. If you're using a different AI tool, paste this recon to it as context first.

## Step 1 prompt (first implementation prompt)
```
Read src/inventory/importer.py, src/inventory/models.py and tests/test_importer.py.
Fix only the trailing-blank-line crash in import_movements.

Write the failing test first, in a new file tests/test_import_skipping.py, in the same
tmp_path / write_csv style as tests/test_importer.py:
"sku,quantity,reason\nAB-1,5,restock\n\n" must return exactly one Movement(sku="AB-1", quantity=5, reason="restock").
Run pytest and show it failing, then make the fix.

Edge cases for this step:
- file ending in "\n\n" -> its rows, no exception
- blank line in the middle of the file -> ignored
- whitespace-only line ("   ") -> ignored
- header-only file -> still []
Blank and whitespace-only lines are not data rows. A later step adds a skipped count,
and they must not count toward it.

Constraints: keep `import_movements(path: Path) -> list[Movement]` exactly; stdlib only, no new
dependencies; follow the existing style; do not edit tests/test_importer.py or data/movements.csv;
do not touch quantity parsing or add summarize yet (those are later steps).

Before the diff, state the cause in one sentence (I expect: csv.reader yields [] for the empty
line and the 3-way unpack raises ValueError). After the fix, run pytest and show the output.
```

Defaults most worth checking: **A1** (`skipped` travels on the returned list), **A4** (`3.0` is skipped) and **A3** (blank lines aren't counted).

Question: is the AI doing the edits this Claude Code session, or another tool in the take-home environment? If it's another tool, I'll give you a short paste-first recon prompt for it. Confirm the plan, or correct A1 or A4, before any code is written.
