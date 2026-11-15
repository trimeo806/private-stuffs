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
