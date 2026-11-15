I added 10 edge-case tests to your existing pytest file. **3 of them fail, and they point to 2 real bugs in `summarize_by_extension`.** I didn't change `storage_report.py`. Fix these before you submit, because hidden tests that follow the README will probably catch them.

## Bugs found

1. **Extensions are not lowercased or merged across case.** The README says extensions are "compared case-insensitively and reported in lowercase". `a.JPG`, `b.jpg` and `c.Jpg` come back as three groups (`JPG`, `jpg`, `Jpg`) when they should be one `("jpg", 35)`.
2. **A name with a trailing dot gets an empty extension instead of `(none)`.** `notes.` returns `("", 7)` instead of `("(none)", 7)`. So a mix of no-extension files splits into a `""` group and a `(none)` group.

The fix belongs in `extension_of`. Return `NO_EXTENSION` when `ext` is empty too, and return `ext.lower()`:

```python
def extension_of(name):
    stem, dot, ext = name.rpartition(".")
    if not dot or not stem or not ext:
        return NO_EXTENSION
    return ext.lower()
```

## Edge-case table

| # | Case | Input | Expected | Source |
|---|---|---|---|---|
| 1 | empty list | `[]` | `[]` | explicit |
| 2 | mixed case | `a.JPG`/10, `b.jpg`/20, `c.Jpg`/5 | `[("jpg", 35)]` | explicit |
| 3 | no dot | `Makefile`/7 | `[("(none)", 7)]` | explicit |
| 4 | only a leading dot | `.gitignore`/7 | `[("(none)", 7)]` | explicit |
| 5 | trailing dot | `notes.`/7 | `[("(none)", 7)]` | explicit |
| 6 | all no-extension kinds together | `Makefile`/1, `.gitignore`/2, `notes.`/4 | `[("(none)", 7)]` | explicit |
| 7 | several dots | `backup.tar.gz`/30 | `[("gz", 30)]` | explicit (after the last dot) |
| 8 | negative size after a valid entry | `a.txt`/10, `b.txt`/-1 | raises `ValueError` | explicit |
| 9 | zero size | `empty.log`/0 | `[("log", 0)]` | assumed: 0 is valid because only negative sizes are rejected, and the extension is still listed |
| 10 | sort by total | totals 1, 300, 20 | descending by total | explicit |
| 11 | tie on total | `zip`, `csv`, `(none)` each 10 | `(none)`, `csv`, `zip` | explicit (ascending by extension; `(` sorts before letters) |
| 12 | large input | 100,000 files over 100 extensions | 100 groups of 1,000 each | assumed: finishes quickly in linear time |

Cases 3 to 5 are one parametrized test, so the file has 10 new test functions and 13 test cases in total, counting the original test.

## Test file: `tests/test_storage_report.py`

```python
import pytest

from storage_report import summarize_by_extension


def test_groups_sizes_by_extension():
    files = [
        {"name": "a.jpg", "size": 100},
        {"name": "b.png", "size": 50},
        {"name": "c.jpg", "size": 25},
    ]

    assert summarize_by_extension(files) == [("jpg", 125), ("png", 50)]


def test_returns_empty_list_for_no_files():
    assert summarize_by_extension([]) == []


def test_merges_extensions_case_insensitively_and_reports_lowercase():
    files = [
        {"name": "a.JPG", "size": 10},
        {"name": "b.jpg", "size": 20},
        {"name": "c.Jpg", "size": 5},
    ]

    assert summarize_by_extension(files) == [("jpg", 35)]


@pytest.mark.parametrize("name", ["Makefile", ".gitignore", "notes."])
def test_reports_name_without_extension_as_none(name):
    files = [{"name": name, "size": 7}]

    assert summarize_by_extension(files) == [("(none)", 7)]


def test_combines_all_names_without_extension_into_one_none_group():
    files = [
        {"name": "Makefile", "size": 1},
        {"name": ".gitignore", "size": 2},
        {"name": "notes.", "size": 4},
    ]

    assert summarize_by_extension(files) == [("(none)", 7)]


def test_uses_text_after_last_dot_for_multi_dot_names():
    files = [{"name": "backup.tar.gz", "size": 30}]

    assert summarize_by_extension(files) == [("gz", 30)]


def test_raises_value_error_for_negative_size():
    files = [
        {"name": "a.txt", "size": 10},
        {"name": "b.txt", "size": -1},
    ]

    with pytest.raises(ValueError):
        summarize_by_extension(files)


def test_keeps_extension_with_zero_total_size():
    files = [{"name": "empty.log", "size": 0}]

    assert summarize_by_extension(files) == [("log", 0)]


def test_sorts_by_total_descending():
    files = [
        {"name": "a.txt", "size": 1},
        {"name": "b.mp4", "size": 300},
        {"name": "c.pdf", "size": 20},
    ]

    assert summarize_by_extension(files) == [("mp4", 300), ("pdf", 20), ("txt", 1)]


def test_breaks_ties_by_extension_ascending():
    files = [
        {"name": "a.zip", "size": 10},
        {"name": "b.csv", "size": 10},
        {"name": "Makefile", "size": 10},
    ]

    assert summarize_by_extension(files) == [("(none)", 10), ("csv", 10), ("zip", 10)]


def test_handles_large_input():
    files = [{"name": f"f{i}.ext{i % 100}", "size": 1} for i in range(100_000)]

    result = summarize_by_extension(files)

    assert len(result) == 100
    assert all(total == 1_000 for _, total in result)
    assert result[0] == ("ext0", 1_000)
```

## Run result

Command: `pytest -q` (pytest 9.2.0, run through `uv run --with pytest` because pytest isn't installed in the system Python).

```
FAILED tests/test_storage_report.py::test_merges_extensions_case_insensitively_and_reports_lowercase
    assert [('jpg', 20), ..., ('Jpg', 5)] == [('jpg', 35)]
FAILED tests/test_storage_report.py::test_reports_name_without_extension_as_none[notes.]
    assert [('', 7)] == [('(none)', 7)]
FAILED tests/test_storage_report.py::test_combines_all_names_without_extension_into_one_none_group
    assert [('', 4), ('(none)', 3)] == [('(none)', 7)]
3 failed, 10 passed in 0.08s
```

The other 10 tests pass: empty input, no dot, leading dot, several dots, negative size, zero size, sort order, ties and large input. Once `extension_of` lowercases the extension and treats an empty one as `(none)`, all 13 should pass. I haven't run them against that fix.

## Assumptions to confirm

- **Zero size:** a size of `0` is valid and the extension still appears with total `0`.
- **Missing keys or non-int sizes:** I didn't test these. The README doesn't say what should happen, so I left them alone and didn't guess.
