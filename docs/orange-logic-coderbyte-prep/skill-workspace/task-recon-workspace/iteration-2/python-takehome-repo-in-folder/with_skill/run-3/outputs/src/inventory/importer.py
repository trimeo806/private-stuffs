import csv
from pathlib import Path

from inventory.models import Movement


def import_movements(path: Path) -> list[Movement]:
    movements = []
    with open(path, newline="") as handle:
        reader = csv.reader(handle)
        next(reader)
        for row in reader:
            sku, quantity, reason = row
            movements.append(Movement(sku=sku.strip(), quantity=int(quantity), reason=reason.strip()))
    return movements
