I added 9 edge-case tests to `tests/test_storage_report.py`, next to your original test. Three of them fail. Each failure is a real bug where `storage_report.py` doesn't do what README.md says, so fix these before you submit. I didn't change the production code.

## Edge cases

Expected values come from README.md, not from the current code.

| # | Case | Input | Expected | Source | Result |
|---|---|---|---|---|---|
| 1 | empty list | `[]` | `[]` | explicit | pass |
| 2 | mixed-case extensions | `a.JPG` 10, `b.jpg` 20, `c.Jpg` 5 | `[("jpg", 35)]` | explicit | **FAIL**: returns `jpg`, `JPG` and `Jpg` as separate entries |
| 3a | no dot | `Makefile` | `[("(none)", 7)]` | explicit | pass |
| 3b | leading dot only | `.gitignore` | `[("(none)", 7)]` | explicit | pass |
| 3c | trailing dot | `notes.` | `[("(none)", 7)]` | explicit | **FAIL**: returns `[("", 7)]` |
| 4 | all extensionless names in one bucket | `Makefile` 1, `.gitignore` 2, `notes.` 4 | `[("(none)", 7)]` | explicit | **FAIL**: returns `[("", 4), ("(none)", 3)]` |
| 5 | several dots | `backup.tar.gz` | `[("gz", 30)]` | explicit (last dot) | pass |
| 6 | negative size | one valid file plus `size: -1` | `ValueError` | explicit | pass |
| 7 | zero size | `empty.log` 0 | `[("log", 0)]` | assumed: 0 is valid and still listed | pass |
| 8 | equal totals | zip 50, csv 50, txt 10, bin 99 | bin, csv, zip, txt | explicit | pass |
| 9 | large input | 100,000 files of 2^40 bytes plus one txt | `[("dat", 2^40 * 100000), ("txt", 1)]` | assumed: no size cap | pass |

## Run result

```
uv run --offline --no-project --with pytest python3 -m pytest -q
3 failed, 9 passed
FAILED test_merges_extensions_case_insensitively_and_reports_lowercase
FAILED test_reports_names_without_extension_as_none[trailing_dot]
FAILED test_combines_all_extensionless_names_into_one_none_bucket
```

## Bugs and fixes

Both bugs are in `extension_of`:

1. **Case is not normalized.** The extension is returned as written. Return `ext.lower()`.
2. **A trailing dot gives an empty extension.** `"notes.".rpartition(".")` returns `ext == ""`, but the code only checks for a missing dot or an empty stem. Add `or not ext` to that check.

```python
def extension_of(name):
    stem, dot, ext = name.rpartition(".")
    if not dot or not stem or not ext:
        return NO_EXTENSION
    return ext.lower()
```

With that change, all 12 tests should pass. I haven't run the suite against the fix.

## Open questions

The README doesn't cover these, so I didn't write tests for them:
- A name made only of dots, like `..hidden` (right now it reports `hidden`).
- A missing `name` or `size` key, or a `size` that isn't an int.
