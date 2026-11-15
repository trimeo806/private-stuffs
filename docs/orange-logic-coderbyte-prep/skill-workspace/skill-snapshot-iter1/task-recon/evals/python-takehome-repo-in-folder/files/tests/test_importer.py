from pathlib import Path

from inventory.importer import import_movements


def write_csv(tmp_path: Path, text: str) -> Path:
    path = tmp_path / "movements.csv"
    path.write_text(text)
    return path


def test_imports_each_row_as_a_movement(tmp_path):
    path = write_csv(tmp_path, "sku,quantity,reason\nAB-1,5,restock\nAB-2,-2,sale\n")
    movements = import_movements(path)
    assert [m.sku for m in movements] == ["AB-1", "AB-2"]
    assert movements[1].quantity == -2


def test_header_only_file_returns_no_movements(tmp_path):
    path = write_csv(tmp_path, "sku,quantity,reason\n")
    assert import_movements(path) == []
