import pytest

from storage_report import summarize_by_extension


def test_returns_empty_list_for_empty_input():
    assert summarize_by_extension([]) == []


@pytest.mark.parametrize(
    "names, expected",
    [
        (["Photo.JPG"], [("jpg", 30)]),
        (["a.JPG", "b.jpg", "c.Jpg"], [("jpg", 30)]),
    ],
)
def test_groups_extensions_case_insensitively_and_reports_lowercase(names, expected):
    files = [{"name": name, "size": 30 // len(names)} for name in names]

    assert summarize_by_extension(files) == expected


@pytest.mark.parametrize(
    "name, expected_extension",
    [
        ("README", "(none)"),
        (".gitignore", "(none)"),
        ("notes.", "(none)"),
        ("archive.tar.gz", "gz"),
        (".env.local", "local"),
    ],
)
def test_reads_extension_after_last_dot_or_reports_none(name, expected_extension):
    assert summarize_by_extension([{"name": name, "size": 10}]) == [(expected_extension, 10)]


def test_merges_all_names_without_extension_into_one_none_row():
    files = [
        {"name": "README", "size": 5},
        {"name": "Makefile", "size": 7},
        {"name": ".gitignore", "size": 3},
    ]

    assert summarize_by_extension(files) == [("(none)", 15)]


@pytest.mark.parametrize(
    "files",
    [
        [{"name": "a.txt", "size": -1}],
        [{"name": "a.txt", "size": 10}, {"name": "b.png", "size": 20}, {"name": "c.jpg", "size": -5}],
    ],
)
def test_raises_value_error_for_negative_size(files):
    with pytest.raises(ValueError):
        summarize_by_extension(files)


def test_keeps_extension_whose_total_is_zero():
    files = [
        {"name": "a.png", "size": 40},
        {"name": "empty.txt", "size": 0},
    ]

    assert summarize_by_extension(files) == [("png", 40), ("txt", 0)]


def test_sorts_by_total_descending_before_extension():
    files = [
        {"name": "a.avi", "size": 10},
        {"name": "b.zip", "size": 300},
        {"name": "c.mp4", "size": 20},
    ]

    assert summarize_by_extension(files) == [("zip", 300), ("mp4", 20), ("avi", 10)]


@pytest.mark.parametrize(
    "files, expected",
    [
        (
            [
                {"name": "b.png", "size": 50},
                {"name": "a.jpg", "size": 50},
                {"name": "c.gif", "size": 50},
            ],
            [("gif", 50), ("jpg", 50), ("png", 50)],
        ),
        (
            [
                {"name": "x.png", "size": 20},
                {"name": "y.png", "size": 30},
                {"name": "z.jpg", "size": 50},
            ],
            [("jpg", 50), ("png", 50)],
        ),
        (
            [
                {"name": "data.abc", "size": 10},
                {"name": "LICENSE", "size": 10},
            ],
            [("(none)", 10), ("abc", 10)],
        ),
    ],
)
def test_breaks_total_ties_by_extension_ascending(files, expected):
    assert summarize_by_extension(files) == expected

