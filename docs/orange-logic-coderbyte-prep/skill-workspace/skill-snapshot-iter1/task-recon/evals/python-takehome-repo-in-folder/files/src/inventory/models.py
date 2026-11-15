from dataclasses import dataclass


@dataclass(frozen=True)
class Movement:
    sku: str
    quantity: int
    reason: str
