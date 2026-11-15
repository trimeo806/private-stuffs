import pytest

from sizes import format_size


def test_formats_small_sizes_in_bytes():
    assert format_size(512) == "512 B"


def test_formats_kilobytes_with_one_decimal():
    assert format_size(1536) == "1.5 KB"


def test_formats_one_megabyte():
    assert format_size(1048576) == "1.0 MB"


def test_rejects_negative_sizes():
    with pytest.raises(ValueError):
        format_size(-1)


def test_formats_exactly_one_kilobyte_as_kilobytes():
    assert format_size(1024) == "1.0 KB"


def test_formats_exactly_one_gigabyte_as_gigabytes():
    assert format_size(1024**3) == "1.0 GB"


def test_moves_to_next_unit_when_rounding_reaches_1024():
    assert format_size(1048575) == "1.0 MB"
