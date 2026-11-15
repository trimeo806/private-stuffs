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


def test_single_file_is_reported_alone():
    assert summarize_by_extension([{"name": "a.txt", "size": 7}]) == [("txt", 7)]


def test_merges_extensions_that_differ_only_in_case_and_reports_lowercase():
    files = [
        {"name": "a.JPG", "size": 100},
        {"name": "b.jpg", "size": 50},
        {"name": "c.Jpg", "size": 25},
    ]

    assert summarize_by_extension(files) == [("jpg", 175)]


def test_reports_uppercase_only_extension_in_lowercase():
    assert summarize_by_extension([{"name": "SCAN.PDF", "size": 10}]) == [("pdf", 10)]


@pytest.mark.parametrize("name", ["Makefile", ".gitignore", "notes."])
def test_name_without_extension_is_reported_as_none(name):
    assert summarize_by_extension([{"name": name, "size": 10}]) == [("(none)", 10)]


def test_names_without_extension_share_one_none_group():
    files = [
        {"name": "Makefile", "size": 1},
        {"name": ".gitignore", "size": 2},
        {"name": "notes.", "size": 4},
    ]

    assert summarize_by_extension(files) == [("(none)", 7)]


@pytest.mark.parametrize(
    "name, expected",
    [
        ("archive.tar.gz", "gz"),
        (".config.json", "json"),
        ("v1.2.final.txt", "txt"),
    ],
)
def test_extension_is_text_after_last_dot(name, expected):
    assert summarize_by_extension([{"name": name, "size": 3}]) == [(expected, 3)]


def test_negative_size_raises_value_error():
    with pytest.raises(ValueError):
        summarize_by_extension([{"name": "a.txt", "size": -1}])


def test_negative_size_after_valid_files_still_raises_value_error():
    files = [
        {"name": "a.txt", "size": 10},
        {"name": "b.txt", "size": 20},
        {"name": "c.txt", "size": -5},
    ]

    with pytest.raises(ValueError):
        summarize_by_extension(files)


def test_zero_size_file_is_allowed_and_reported():
    assert summarize_by_extension([{"name": "empty.log", "size": 0}]) == [("log", 0)]


def test_sorts_by_total_descending():
    files = [
        {"name": "a.gif", "size": 1},
        {"name": "b.png", "size": 30},
        {"name": "c.bmp", "size": 20},
    ]

    assert summarize_by_extension(files) == [("png", 30), ("bmp", 20), ("gif", 1)]


def test_ties_on_total_are_sorted_by_extension_ascending():
    files = [
        {"name": "a.zip", "size": 10},
        {"name": "b.doc", "size": 10},
        {"name": "c.mp4", "size": 10},
        {"name": "d.big", "size": 99},
    ]

    assert summarize_by_extension(files) == [
        ("big", 99),
        ("doc", 10),
        ("mp4", 10),
        ("zip", 10),
    ]


def test_tie_order_uses_lowercased_extension():
    files = [
        {"name": "a.Zip", "size": 10},
        {"name": "b.doc", "size": 10},
    ]

    assert summarize_by_extension(files) == [("doc", 10), ("zip", 10)]


def test_does_not_modify_the_input_list():
    files = [
        {"name": "b.png", "size": 1},
        {"name": "a.JPG", "size": 2},
    ]
    snapshot = [dict(entry) for entry in files]

    summarize_by_extension(files)

    assert files == snapshot
