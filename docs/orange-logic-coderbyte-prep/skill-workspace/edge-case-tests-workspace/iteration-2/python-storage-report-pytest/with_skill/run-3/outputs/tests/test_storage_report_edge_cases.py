import pytest

from storage_report import summarize_by_extension


def test_returns_empty_list_for_empty_input():
    assert summarize_by_extension([]) == []


def test_groups_extensions_case_insensitively():
    files = [
        {"name": "a.JPG", "size": 10},
        {"name": "b.jpg", "size": 5},
        {"name": "c.Jpg", "size": 1},
    ]

    assert summarize_by_extension(files) == [("jpg", 16)]


def test_reports_extension_in_lowercase():
    assert summarize_by_extension([{"name": "Photo.PNG", "size": 7}]) == [("png", 7)]


@pytest.mark.parametrize(
    "name",
    ["README", ".gitignore", "notes."],
)
def test_reports_name_without_extension_as_none(name):
    assert summarize_by_extension([{"name": name, "size": 3}]) == [("(none)", 3)]


def test_merges_all_names_without_extension_into_one_none_group():
    files = [
        {"name": "Makefile", "size": 10},
        {"name": ".env", "size": 20},
        {"name": "draft.", "size": 5},
    ]

    assert summarize_by_extension(files) == [("(none)", 35)]


@pytest.mark.parametrize(
    "name, extension",
    [
        ("archive.tar.gz", "gz"),
        (".config.json", "json"),
    ],
)
def test_uses_text_after_last_dot(name, extension):
    assert summarize_by_extension([{"name": name, "size": 4}]) == [(extension, 4)]


@pytest.mark.parametrize(
    "files",
    [
        [{"name": "a.txt", "size": -1}],
        [{"name": "a.txt", "size": 10}, {"name": "b.txt", "size": -1}],
    ],
)
def test_rejects_negative_size(files):
    with pytest.raises(ValueError):
        summarize_by_extension(files)


def test_accepts_zero_size():
    assert summarize_by_extension([{"name": "empty.txt", "size": 0}]) == [("txt", 0)]


def test_sorts_by_total_descending():
    files = [
        {"name": "a.aaa", "size": 1},
        {"name": "b.mmm", "size": 3},
        {"name": "c.zzz", "size": 5},
    ]

    assert summarize_by_extension(files) == [("zzz", 5), ("mmm", 3), ("aaa", 1)]


def test_breaks_ties_by_extension_ascending():
    files = [
        {"name": "a.png", "size": 10},
        {"name": "b.gif", "size": 10},
        {"name": "README", "size": 10},
        {"name": "c.jpg", "size": 10},
    ]

    assert summarize_by_extension(files) == [
        ("(none)", 10),
        ("gif", 10),
        ("jpg", 10),
        ("png", 10),
    ]
