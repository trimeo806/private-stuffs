I added 15 edge-case tests. 4 of them fail, and they point to two real bugs in `summarize_by_extension`: extensions are not lowercased, and `notes.` is reported as `""` instead of `(none)`. The README requires both. Graders would very likely check these, so fix them before you submit. I didn't change any production code.

## Edge cases: `summarize_by_extension(files)`

Framework: pytest (found in `pyproject.toml` under `[tool.pytest.ini_options]`, plus the existing `tests/test_storage_report.py`) | Test file: `tests/test_summarize_by_extension_edge_cases.py` | Run: `pytest tests/test_summarize_by_extension_edge_cases.py -q`, then `pytest -q`

| # | Case | Input | Expected | What it would catch | Source |
|---|------|-------|----------|----------------|--------|
| 1 | empty list | `[]` | `[]` | code that assumes at least one file | spec |
| 2 | uppercase extension | `photo.JPG` (10) | `[("jpg", 10)]` | extension not lowercased | spec |
| 3 | case variants merge | `a.jpg` 1, `b.JPG` 2, `c.Jpg` 4 | `[("jpg", 7)]` | case-sensitive grouping | spec |
| 4 | no dot | `README` | `[("(none)", 10)]` | missing no-extension branch | spec |
| 5 | leading dot only | `.gitignore` | `[("(none)", 10)]` | `gitignore` reported as an extension | spec |
| 6 | trailing dot | `notes.` | `[("(none)", 10)]` | empty string reported as an extension | spec |
| 7 | several dots | `archive.tar.gz` | `[("gz", 10)]` | split on the first dot instead of the last | spec |
| 8 | leading dot plus an extension | `.env.local` | `[("local", 10)]` | leading-dot rule applied too broadly | spec (last-dot rule) |
| 9 | every extensionless kind together | `README` 1, `.gitignore` 2, `notes.` 4 | `[("(none)", 7)]` | `(none)` split into several groups | spec |
| 10 | sort by total | totals 1, 20, 300 in ascending input order | `png 300, csv 20, txt 1` | ascending sort or no sort | spec |
| 11 | tie on total | `z.txt`, `a.png`, `m.csv`, all size 5 | `csv, png, txt` | missing tie-breaker, or the tie-breaker sorted descending | spec |
| 12 | negative size | `size: -1` | `ValueError` | negative sizes accepted | spec |
| 13 | negative after valid files | `10`, then `-1` | `ValueError` | only the first file validated | spec |
| 14 | zero size | `empty.txt` 0 | `[("txt", 0)]` | `<= 0` used where `< 0` was meant | assumed |
| 15 | total over 32 bits | two files of `2**31` | `[("iso", 2**32)]` | overflow or truncation of the total | spec |

### Tests

`tests/test_summarize_by_extension_edge_cases.py`:

```python
import pytest

from storage_report import summarize_by_extension


def test_returns_empty_list_for_no_files():
    assert summarize_by_extension([]) == []


@pytest.mark.parametrize(
    ("name", "expected_extension"),
    [
        ("photo.JPG", "jpg"),
        ("README", "(none)"),
        (".gitignore", "(none)"),
        ("notes.", "(none)"),
        ("archive.tar.gz", "gz"),
        (".env.local", "local"),
    ],
    ids=[
        "uppercase-reported-lowercase",
        "no-dot",
        "leading-dot-only",
        "trailing-dot",
        "last-dot-wins",
        "leading-dot-with-extension",
    ],
)
def test_reports_extension_of_a_single_file(name, expected_extension):
    assert summarize_by_extension([{"name": name, "size": 10}]) == [(expected_extension, 10)]


def test_merges_case_variants_of_the_same_extension():
    files = [
        {"name": "a.jpg", "size": 1},
        {"name": "b.JPG", "size": 2},
        {"name": "c.Jpg", "size": 4},
    ]

    assert summarize_by_extension(files) == [("jpg", 7)]


def test_groups_every_extensionless_name_under_none():
    files = [
        {"name": "README", "size": 1},
        {"name": ".gitignore", "size": 2},
        {"name": "notes.", "size": 4},
    ]

    assert summarize_by_extension(files) == [("(none)", 7)]


def test_sorts_by_total_descending():
    files = [
        {"name": "a.txt", "size": 1},
        {"name": "b.csv", "size": 20},
        {"name": "c.png", "size": 300},
    ]

    assert summarize_by_extension(files) == [("png", 300), ("csv", 20), ("txt", 1)]


def test_breaks_total_ties_by_extension_ascending():
    files = [
        {"name": "z.txt", "size": 5},
        {"name": "a.png", "size": 5},
        {"name": "m.csv", "size": 5},
    ]

    assert summarize_by_extension(files) == [("csv", 5), ("png", 5), ("txt", 5)]


@pytest.mark.parametrize(
    "files",
    [
        [{"name": "a.txt", "size": -1}],
        [{"name": "a.txt", "size": 10}, {"name": "b.txt", "size": -1}],
    ],
    ids=["only-file", "after-valid-files"],
)
def test_rejects_negative_size(files):
    with pytest.raises(ValueError):
        summarize_by_extension(files)


def test_accepts_zero_size():
    assert summarize_by_extension([{"name": "empty.txt", "size": 0}]) == [("txt", 0)]


def test_totals_beyond_32_bits():
    files = [
        {"name": "a.iso", "size": 2**31},
        {"name": "b.iso", "size": 2**31},
    ]

    assert summarize_by_extension(files) == [("iso", 2**32)]
```

### Result

New file: 11 passed, 4 failed. Whole suite: 12 passed, 4 failed. Your original test still passes.

Failing tests:
- `test_reports_extension_of_a_single_file[uppercase-reported-lowercase]` returned `[('JPG', 10)]`
- `test_reports_extension_of_a_single_file[trailing-dot]` returned `[('', 10)]`
- `test_merges_case_variants_of_the_same_extension` returned three groups: `Jpg` 4, `JPG` 2 and `jpg` 1
- `test_groups_every_extensionless_name_under_none` returned `[('', 4), ('(none)', 3)]`

### Suspected bugs

- **Cases 2 and 3: extensions are not lowercased.** The README says extensions are compared case-insensitively and reported in lowercase. The cause is `storage_report.py:10`: `return ext` returns the raw casing. Because of this, `JPG`, `Jpg` and `jpg` become separate groups, and ties are broken by uppercase-first ASCII order. Likely fix: `return ext.lower()`. `casefold()` is the stricter choice if extensions can contain non-ASCII letters.
- **Cases 6 and 9: a trailing dot is not treated as "no extension".** For `notes.`, `rpartition(".")` returns `("notes", ".", "")`. That passes the check at `storage_report.py:8`, which only looks at `dot` and `stem`, so the file is grouped under `""` instead of `(none)`. Likely fix: `if not dot or not stem or not ext:`.

Production code is unchanged.

### Assumptions

- **Case 14:** the README only says a negative `size` raises an error, so a size of `0` is valid and its extension appears with a total of 0.
- **Case 11:** this test uses lowercase names only, so it checks ordering by itself. My first version included `m.CSV`, and it failed only because of the lowercasing bug above. That is already covered by cases 2 and 3, so I made the names lowercase. The expected order did not change.
- **Not covered:** the README doesn't define behavior for a `None` list, a missing `name` or `size` key, or a non-integer `size`. I left those out rather than guess.

**Your call:** should I apply the two one-line fixes to `storage_report.py` and rerun the suite? I left the code unchanged so you can see what the tests found first.
