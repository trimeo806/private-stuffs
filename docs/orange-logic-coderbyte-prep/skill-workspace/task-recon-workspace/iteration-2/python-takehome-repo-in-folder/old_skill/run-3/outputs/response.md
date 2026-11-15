I read the repo and ran the tests before planning. The 2 existing tests pass. Running `import_movements("data/movements.csv")` crashes with `ValueError: invalid literal for int() ... '2.5'`. The sample file hits the quantity problem (item 3) before it reaches the blank line (item 1). The file ends in `\n\n`, so it has both problems. That makes it a good end-to-end check once you're done.

**Start here: decide how `skipped` reaches `summarize`.** `summarize(movements)` must report `skipped`, but `import_movements(path)` returns `list[Movement]` and you can't change its signature. A plain list has no way to carry the count. That is the one design decision in this task. Settle it before you let the AI write code, or it will make up its own answer.

## Goal
Make `import_movements` tolerate a trailing blank line and non-integer quantities. Then add `summarize(movements) -> Summary(net, skipped)`. Keep the `import_movements` signature, add no dependencies, and write a `NOTES.md`.

## Requirements
| # | Requirement | Type |
|---|---|---|
| R1 | A CSV ending with a blank line imports without error | explicit |
| R2 | A row whose quantity is not a whole number is skipped, not raised | explicit |
| R3 | Skipped rows are counted and show up as `Summary.skipped` | explicit |
| R4 | `summarize(movements)` lives in `inventory/importer.py` and returns a `Summary` | explicit |
| R5 | `Summary.net` maps SKU to net quantity, ordered by SKU | explicit |
| R6 | SKUs are compared case-insensitively and reported in upper case (`AB-1` + `ab-1` gives one `AB-1` key) | explicit |
| R7 | `import_movements(path: Path) -> list[Movement]` keeps its signature, so existing callers still get a list | explicit |
| R8 | No new runtime dependencies; `pytest` passes | explicit |
| R9 | `NOTES.md` covers your decisions and what you'd do with more time | explicit |
| R10 | Blank lines don't count as skipped rows (they are not rows) | implied |
| R11 | The 2 existing tests still pass | implied |
| R12 | `data/movements.csv` summarizes to `{"AB-1": 3, "CD-7": 3}` with `skipped == 1` | implied |

## Assumptions
- A1: How `skipped` gets from the import to `summarize`. Default: `import_movements` returns a `list` subclass (for example `ImportedMovements(list)`) with a `skipped` attribute. Callers still get a list, so the signature and behavior hold, and `summarize` reads `getattr(movements, "skipped", 0)`. A plain list passed in gives `skipped == 0`. Alternative: a new `read_movements(path) -> ImportResult` that `import_movements` wraps. Pick one, record it in NOTES.md, and test it.
- A2: What counts as a whole number. Default: `"5"`, `" 5 "`, `"-2"` and `"+3"` are accepted. `"2.5"`, `"2.0"`, `""`, `"abc"` and `"1e3"` are skipped. Also reject strings that Python's `int()` quietly accepts but a CSV shouldn't contain: `"1_000"` and full-width digits like `"５"`. A regex such as `^[+-]?\d+$` with ASCII digits only, or `str.isascii()`, handles that.
- A3: A row with the wrong number of columns, or an empty SKU, is not imported. Default: skip it and count it, don't crash.
- A4: A row that is only whitespace counts as blank. Default: ignore it, don't count it.
- A5: A SKU whose net is 0 still appears in `net`. Default: include it.
- A6: `net` is a plain `dict` built in sorted upper-case SKU order, and `Summary` is a frozen dataclass in `models.py`.
- A7: Encoding. Default: open with `encoding="utf-8-sig"` so a BOM from the export doesn't stick to the header. This is low priority and can go below the cut line.

## Files
| File | Action | Why |
|---|---|---|
| `TASK.md` | read | The brief |
| `src/inventory/importer.py` | change | Fix the import, add `summarize` |
| `src/inventory/models.py` | change | Add `Summary`, plus the list subclass if you take A1 |
| `tests/test_importer.py` | change | Add a red test for each requirement and edge case |
| `NOTES.md` | create | Required deliverable |
| `data/movements.csv` | read only | End-to-end check (R12) |
| `pyproject.toml` | leave | Already sets `pythonpath=src`; adding deps breaks R8 |
| `src/inventory/__init__.py` | leave | Empty, nothing to change |

## Edge cases
- File ends with `\n\n` → imports normally, `skipped == 0` for that line
- Header-only file → `[]`; `summarize` gives `net == {}` and `skipped == 0`
- `2.5`, `2.0`, `abc` or an empty quantity → row skipped, `skipped += 1`
- `1_000` or `５` → skipped (A2)
- ` 5 ` with surrounding spaces → imported as 5
- Negative quantity `-2` → imported (an existing test relies on this)
- `AB-1` and `ab-1` → one key `AB-1` holding the sum
- SKUs arrive unsorted (`CD-7` before `AB-1`) → `list(net)` is sorted
- Net sums to 0 → key kept with value 0 (A5)
- Row with 2 or 4 columns, or an empty SKU → skipped and counted (A3)
- `summarize([])` → empty `net`, `skipped == 0`
- `summarize` given a plain list (not from the import) → works, `skipped == 0`
- A completely empty file with no header → `[]` rather than `StopIteration` (cheap to handle: `next(reader, None)`)

## Plan
About 115 minutes remain.

| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Decide A1 and A2 (the plumbing for `skipped`, and the whole-number rule) | 5 | Decision written at the top of NOTES.md |
| 1 | Red test for the trailing blank line, then fix: skip empty or whitespace-only rows | 10 | New test passes, the 2 old tests pass |
| 2 | Red tests for non-whole quantities, then add a strict `_parse_quantity` helper and skip-and-count in the import loop | 15 | `2.5`, `2.0`, `abc`, `""` and `1_000` are skipped, no crash |
| 3 | Add `Summary` and the `skipped` carrier (A1); `import_movements` returns it | 10 | `import_movements(...).skipped` is correct; the result is still a `list` |
| 4 | `summarize`: upper-case SKUs, sum, sort into a dict, pass `skipped` through | 15 | Tests for R5, R6 and zero net pass |
| 5 | Malformed rows: wrong column count, empty SKU (A3), file with no header | 10 | Tests pass |
| 6 | End-to-end on `data/movements.csv` | 5 | `{"AB-1": 3, "CD-7": 3}`, `skipped == 1` |
| 7 | Write `NOTES.md`: decisions A1–A7, trade-offs, what you'd do with more time (logging skipped rows with line numbers, BOM handling, a CLI, streaming large files) | 10 | File exists and is short |
| 8 | Review: the `import_movements` signature is unchanged, no new deps, run the full `pytest`, read the diff | 10 | Everything green, the diff is clean |
| — | Buffer | 20 | |

Cut line: drop these in order. First A7 (BOM and encoding). Then the stricter `1_000` and `５` rejection (A2 extras; just note them in NOTES.md). Then step 5's column-count and no-header tests (wrap the unpacking in a `try` so the import still can't crash). Never cut steps 1–4, 6 or 7: they are the graded requirements.

## First prompt
```
Context: Python 3.11 package in src/inventory, tests in tests/ (run with pytest).
Read TASK.md, src/inventory/importer.py, src/inventory/models.py, tests/test_importer.py.

Do NOT write code yet. First:
1. Restate the task in one sentence.
2. List explicit and implied requirements.
3. import_movements(path) -> list[Movement] must keep its signature, but
   summarize(movements) must report how many rows were skipped. Propose how the
   skipped count gets from the import to summarize without changing that signature.
   My preferred option: import_movements returns a list subclass with a `skipped`
   attribute. Give the trade-offs against one alternative.
4. Define "whole number" for the quantity column: accept "5", " 5 ", "-2", "+3";
   reject "2.5", "2.0", "", "abc", "1e3", "1_000", and non-ASCII digits.
5. List edge cases with expected behavior (trailing blank line, header-only,
   case-insensitive SKUs, net of zero, wrong column count).
6. Propose a step plan where each step adds a failing test and then changes one function.
Respect existing signatures and style; no new dependencies.
```

## Your decision
Item A1 is the call that shapes the rest of the work. Should `skipped` travel as an attribute on the returned list (my default), or through a separate `read_movements` function? If you have no preference, go with the list subclass and justify it in NOTES.md.
