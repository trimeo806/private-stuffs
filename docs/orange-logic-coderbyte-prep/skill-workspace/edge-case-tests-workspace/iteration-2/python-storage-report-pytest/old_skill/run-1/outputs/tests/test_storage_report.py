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
