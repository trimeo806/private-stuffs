Start with the bug, because the other two tasks build on it. I read the whole repo (it's small) and here's the recon. No code yet: say go and the first prompt is at the bottom.

Two things I found in the repo:
- `data/movements.csv` does end with a blank line (`...restock\n\n`). But running `import_movements` on it fails sooner, on row 3 (`CD-7,2.5`): `ValueError: invalid literal for int()`. So fixing the blank line alone won't make the sample file import. Tasks 1 and 3 go together.
- `pytest` isn't installed in my Python here (`No module named pytest`). Check yours in the first minute (`pip install pytest` as a dev tool is fine; it isn't a runtime dependency).

## Goal
Make `import_movements` survive real warehouse exports (trailing blank line, non-integer quantities) without changing its signature, and add `summarize(movements)` that returns per-SKU net quantities plus a skipped-row count.

## Requirements
| # | Requirement | Type |
|---|---|---|
| R1 | A CSV ending with a blank line imports without crashing; the blank line is not a movement | explicit |
| R2 | `import_movements(path: Path) -> list[Movement]` keeps its signature (still returns a list, callers unchanged) | explicit |
| R3 | `summarize(movements)` lives in `inventory/importer.py` and returns a `Summary` | explicit |
| R4 | `Summary.net` maps SKU to net quantity (sum of signed quantities), ordered by SKU | explicit |
| R5 | SKUs are grouped case-insensitively and reported upper case (`AB-1` + `ab-1` → `AB-1`) | explicit |
| R6 | `Summary.skipped` = number of rows not imported | explicit |
| R7 | A row whose quantity isn't a whole number is skipped, not a crash, and counts in `skipped` | explicit |
| R8 | No new runtime dependencies; tests pass with `pytest` | explicit |
| R9 | `NOTES.md` with decisions and "with more time" items | explicit |
| R10 | `skipped` has to get from the import into `summarize`, so the import must record it somewhere without breaking R2 | implied |
| R11 | Existing two tests still pass; new tests cover R1, R4–R7 | implied |
| R12 | Sample file gives `net == {"AB-1": 3, "CD-7": 3}`, `skipped == 1` | implied |

## Assumptions
- A1: How does `summarize(movements)` know `skipped` when it only gets movements? → `import_movements` returns a `list` subclass (e.g. `MovementList(list)`) with a `skipped` attribute. The return type is still a list, so the signature and callers stay the same. `summarize` reads `getattr(movements, "skipped", 0)`, so a plain list also works. Put this in NOTES.md as the main decision. (Other option: a new `read_movements(path)` that returns `(movements, skipped)`, with `import_movements` calling it.)
- A2: Blank lines (trailing or in the middle) aren't rows, so they're ignored and not counted in `skipped`.
- A3: "Whole number" means a string that `int()` accepts after stripping whitespace (`"3"`, `"-2"`, `"+4"`). `"2.5"`, `"3.0"`, `"1e3"` and `""` are skipped. This is the stricter reading because `Movement.quantity` is an `int`. Say so in NOTES.md.
- A4: Other bad rows (wrong column count, empty SKU) are also skipped and counted, not crashed on. The brief only names quantity, but "rows that were not imported" covers them too.
- A5: Ordered by SKU means sorted by the upper-cased SKU string, returned as a plain `dict` built in sorted order.
- A6: SKUs that net to 0 stay in `net` with value 0.
- A7: The header row is always there and is never counted as skipped. `import_movements` still returns stripped original-case SKUs, and only `summarize` upper-cases them, so existing callers see no change.
- A8: `Summary` is a frozen dataclass in `models.py`, next to `Movement`, using the same style.

## Files
| File | Action | Why |
|---|---|---|
| `TASK.md` | read | brief; source of truth |
| `src/inventory/importer.py` | change | blank-line fix, skip/count bad rows, add `summarize` |
| `src/inventory/models.py` | change | add `Summary` (and `MovementList` if A1) |
| `tests/test_importer.py` | change (add only) | new tests; keep the 2 existing ones as they are |
| `data/movements.csv` | read only | real-world fixture; use it in one end-to-end test |
| `NOTES.md` | create | required deliverable |
| `pyproject.toml` | leave alone | no runtime deps to add |
| `src/inventory/__init__.py` | leave alone | empty; exporting from it isn't asked for |

## Edge cases
- Trailing blank line → ignored, `skipped` unchanged
- Blank line mid-file / whitespace-only line → ignored (A2)
- Header-only file → `[]`, `Summary(net={}, skipped=0)`
- Empty movements list into `summarize` → `net == {}`, `skipped == 0`
- `2.5`, `3.0`, `abc`, empty quantity → skipped, `skipped += 1`
- `-2`, `0`, `" 5 "` → imported as int
- Very large quantity (`99999999999999999999`) → imported; Python ints don't overflow
- Duplicate SKU in different case (`AB-1`, `ab-1`, ` ab-1 `) → one key `AB-1`, summed
- Net sums to 0 → key kept with 0 (A6)
- Wrong column count (2 or 4 fields) → skipped and counted (A4)
- Unicode SKU (`ß-1`) → `.upper()` gives `SS-1`; accept it and mention it in NOTES
- Order: input `CD-7, AB-1` → `list(net) == ["AB-1", "CD-7"]`
- Sample `data/movements.csv` → `{"AB-1": 3, "CD-7": 3}`, `skipped == 1`

## Plan
Budget: about 115 min left of 120.

| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Install/confirm `pytest`, run the 2 existing tests, reproduce both crashes | 5 | 2 green; trailing-blank and `2.5` failures reproduced |
| 1 | Failing test for the trailing blank line → fix in `import_movements` (skip empty rows) | 10 | test green, old tests green |
| 2 | Failing tests for non-int quantity and wrong column count → skip and count them; return the list with a `skipped` count (A1) | 20 | `import_movements(path)` is still a list; `.skipped == 1` on the sample file |
| 3 | Add `Summary` in `models.py` + `summarize` in `importer.py` (case-insensitive, upper case, sorted) | 20 | tests for grouping, order, zero-net and empty input are green |
| 4 | End-to-end test on `data/movements.csv` | 5 | `net == {"AB-1": 3, "CD-7": 3}`, `skipped == 1` |
| 5 | Remaining edge-case tests (whitespace, `3.0`, large int, mid-file blank) | 15 | all green |
| 6 | `NOTES.md`: A1, A2, A3, A4 + "with more time" (logging skipped rows with line numbers, BOM/`utf-8-sig`, a streaming iterator for big files, a type for the return value) | 10 | file exists, under 30 lines |
| 7 | Self-review: diff check that the signature is unchanged, no new deps, no stray prints; full `pytest` | 10 | clean diff, all green |
| Buffer | | 20 | |

Cut line: drop step 5 first (keep only the `3.0` case, because A3 is a judgment call a reviewer will check), then shorten step 6 to the four decisions. Never cut steps 1–4 or the NOTES.md file itself.

## First prompt
```
Repo: Python 3.11 package `inventory` (src/inventory/importer.py, models.py), tests in tests/test_importer.py, run with `pytest`. Brief is in TASK.md.

Before writing any code: restate the task in one sentence, list explicit and implied requirements, list assumptions for anything ambiguous, list edge cases with expected behavior, and propose a step plan where each step changes one function. Respect existing signatures and style; no new dependencies.

Then do ONLY step 1: add a failing test in tests/test_importer.py for a CSV that ends with a blank line ("sku,quantity,reason\nAB-1,5,restock\n\n"), run it to show it fails, then make the smallest fix in import_movements so blank rows are ignored. Do not change the signature `import_movements(path: Path) -> list[Movement]`. Keep the two existing tests unchanged and passing. Stop after step 1 and show me the diff.
```

Question for you (I went with the defaults above): is A1 acceptable, a `list` subclass that carries `skipped`? If you'd rather have a separate `read_movements()` that returns `(movements, skipped)`, change step 2 before you start.
