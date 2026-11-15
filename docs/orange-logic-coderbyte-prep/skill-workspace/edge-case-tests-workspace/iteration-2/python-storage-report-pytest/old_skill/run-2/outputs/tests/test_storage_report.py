import time

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


def test_reports_single_file_under_its_extension():
    assert summarize_by_extension([{"name": "report.pdf", "size": 42}]) == [("pdf", 42)]


def test_merges_extensions_case_insensitively_and_reports_lowercase():
    files = [
        {"name": "A.JPG", "size": 10},
        {"name": "b.jpg", "size": 5},
        {"name": "c.Jpg", "size": 1},
    ]

    assert summarize_by_extension(files) == [("jpg", 16)]


def test_uses_text_after_last_dot_as_extension():
    files = [{"name": "backup.tar.gz", "size": 7}]

    assert summarize_by_extension(files) == [("gz", 7)]


@pytest.mark.parametrize("name", ["Makefile", ".gitignore", "notes."])
def test_reports_names_without_extension_as_none(name):
    assert summarize_by_extension([{"name": name, "size": 3}]) == [("(none)", 3)]


def test_sorts_by_total_descending():
    files = [
        {"name": "a.txt", "size": 1},
        {"name": "b.mp4", "size": 300},
        {"name": "c.png", "size": 20},
    ]

    assert summarize_by_extension(files) == [("mp4", 300), ("png", 20), ("txt", 1)]


def test_breaks_total_ties_by_extension_ascending():
    files = [
        {"name": "a.zip", "size": 10},
        {"name": "b.csv", "size": 10},
        {"name": "c.md", "size": 10},
    ]

    assert summarize_by_extension(files) == [("csv", 10), ("md", 10), ("zip", 10)]


def test_keeps_zero_byte_files_in_report():
    files = [
        {"name": "a.log", "size": 0},
        {"name": "b.txt", "size": 5},
    ]

    assert summarize_by_extension(files) == [("txt", 5), ("log", 0)]


def test_raises_value_error_for_size_just_below_zero():
    with pytest.raises(ValueError):
        summarize_by_extension([{"name": "a.txt", "size": -1}])


def test_raises_value_error_when_negative_size_follows_valid_files():
    files = [
        {"name": "a.txt", "size": 10},
        {"name": "b.txt", "size": 20},
        {"name": "c.txt", "size": -5},
    ]

    with pytest.raises(ValueError):
        summarize_by_extension(files)


def test_summarizes_large_input_quickly():
    extensions = ["jpg", "png", "txt", "pdf", "csv"]
    files = [{"name": f"f{i}.{extensions[i % 5]}", "size": 1} for i in range(200_000)]

    started = time.perf_counter()
    result = summarize_by_extension(files)
    elapsed = time.perf_counter() - started

    assert result == [(ext, 40_000) for ext in sorted(extensions)]
    assert elapsed < 2.0
