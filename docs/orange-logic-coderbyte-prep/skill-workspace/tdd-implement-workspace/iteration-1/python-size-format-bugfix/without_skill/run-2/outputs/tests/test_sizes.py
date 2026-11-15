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


@pytest.mark.parametrize(
    ("num_bytes", "expected"),
    [
        (1023, "1023 B"),
        (1024, "1.0 KB"),
        (1024**3, "1.0 GB"),
        (1024**4, "1.0 TB"),
    ],
)
def test_switches_unit_at_exact_powers_of_1024(num_bytes, expected):
    assert format_size(num_bytes) == expected
