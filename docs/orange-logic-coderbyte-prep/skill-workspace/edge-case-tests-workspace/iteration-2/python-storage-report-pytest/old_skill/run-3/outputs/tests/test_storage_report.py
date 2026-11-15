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


@pytest.mark.parametrize(
    "name",
    ["Makefile", ".gitignore", "notes."],
    ids=["no_dot", "leading_dot_only", "trailing_dot"],
)
def test_reports_names_without_extension_as_none(name):
    assert summarize_by_extension([{"name": name, "size": 7}]) == [("(none)", 7)]


def test_combines_all_extensionless_names_into_one_none_bucket():
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
        {"name": "ok.txt", "size": 10},
        {"name": "bad.txt", "size": -1},
    ]

    with pytest.raises(ValueError):
        summarize_by_extension(files)


def test_counts_zero_size_files_under_their_extension():
    files = [{"name": "empty.log", "size": 0}]

    assert summarize_by_extension(files) == [("log", 0)]


def test_sorts_by_total_descending_then_extension_ascending_on_ties():
    files = [
        {"name": "a.zip", "size": 50},
        {"name": "b.csv", "size": 50},
        {"name": "c.txt", "size": 10},
        {"name": "d.bin", "size": 99},
    ]

    assert summarize_by_extension(files) == [
        ("bin", 99),
        ("csv", 50),
        ("zip", 50),
        ("txt", 10),
    ]


def test_handles_large_input_with_large_sizes():
    big = 2**40
    files = [{"name": f"f{i}.dat", "size": big} for i in range(100_000)]
    files.append({"name": "one.txt", "size": 1})

    assert summarize_by_extension(files) == [("dat", big * 100_000), ("txt", 1)]
