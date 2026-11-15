## Edge cases: `summarize_by_extension`

Framework: pytest (detected from `pyproject.toml` and `tests/test_storage_report.py`) | Test file: `tests/test_storage_report_edge_cases.py` | Run: `python -m pytest -q`

| # | Case | Input | Expected | Why it matters | Source |
|---|------|-------|----------|----------------|--------|
| 1 | Uppercase extension | `Photo.JPG` (30) | `[("jpg", 30)]` | Extension reported as typed instead of lowercase | spec |
| 2 | Case-variant duplicates | `a.JPG`, `b.jpg`, `c.Jpg` (10 each) | `[("jpg", 30)]` | Grouping before normalizing splits one extension into three rows | spec |
| 3 | Trailing dot | `notes.` | `[("(none)", 10)]` | `rpartition` returns an empty extension, reported as `""` | spec |
| 4 | Leading dot only / no dot | `.gitignore`, `README` | `("(none)", 10)` | Dotfile read as extension `gitignore` | spec |
| 5 | Several dots | `archive.tar.gz`, `.env.local` | `gz`, `local` | Split on first dot instead of last; leading-dot rule over-applied | spec |
| 6 | Several names without extension | `README`, `Makefile`, `.gitignore` | `[("(none)", 15)]` | `(none)` rows not merged | spec |
| 7 | Negative size | size `-1`; and `-5` after two valid entries | `ValueError` | Negatives silently reduce totals; only the first item validated | spec |
| 8 | Zero size | `empty.txt` size 0 next to `a.png` 40 | `[("png", 40), ("txt", 0)]` | `<= 0` check rejecting zero, or zero totals dropped | assumed |
| 9 | Sort by total first | `avi` 10, `zip` 300, `mp4` 20 | `zip, mp4, avi` | Sorted by extension or ascending total | spec |
| 10 | Ties on total | `png`/`jpg`/`gif` all 50; `png` 20+30 vs `jpg` 50; `(none)` 10 vs `abc` 10 | ascending by extension: `gif, jpg, png`; `jpg, png`; `(none), abc` | Tie order left to input order, or reversed with the total | spec |
| 11 | Empty list | `[]` | `[]` | Crash or non-list return on empty input | spec |

### Tests

Added `tests/test_storage_report_edge_cases.py` (8 test functions, 16 cases, parametrized where only the data differs). The existing `tests/test_storage_report.py` is unchanged.

### Result

Whole suite: 14 passed, 3 failed. The 3 failures are real bugs, not test mistakes.

### Suspected bugs

- Cases 1 and 2: expected `[("jpg", 30)]`, got `[("JPG", 10), ("Jpg", 10), ("jpg", 10)]`. Likely cause: `storage_report.py:10` returns `ext` without `.lower()`, so extensions are neither grouped case-insensitively nor reported in lowercase.
- Case 3: expected `[("(none)", 10)]`, got `[("", 10)]`. Likely cause: `storage_report.py:8` only checks for a missing dot or an empty stem, not an empty extension (`not ext`).

Production code was left unchanged so the tests show what they found. Both fixes are one-liners in `extension_of`: `if not dot or not stem or not ext: return NO_EXTENSION` and `return ext.lower()`. Say the word and I will apply them and rerun the suite.

### Assumptions

- Case 8: the README only rejects negative sizes, so I assumed size 0 is valid and its extension still appears with total 0.
- Case 5: `.env.local` has a dot that is not leading, so I read its extension as `local`. The README only says a name with only a leading dot has no extension.
- Case 10: `(none)` sorts by plain string comparison like any other extension, so it comes before letters (`(` < `a`). The README does not give `(none)` a special place.
- I skipped overflow cases: Python ints do not overflow, so large byte totals cannot wrap.
- Missing `name`/`size` keys, `None` input and non-int sizes are not covered: the README does not say what should happen.
