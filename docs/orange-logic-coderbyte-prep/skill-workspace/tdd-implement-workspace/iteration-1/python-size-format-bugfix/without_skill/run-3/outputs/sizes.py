UNITS = ["B", "KB", "MB", "GB", "TB"]


def format_size(num_bytes: int) -> str:
    if num_bytes < 0:
        raise ValueError("num_bytes must not be negative")
    size = float(num_bytes)
    unit = 0
    while size >= 1024 and unit < len(UNITS) - 1:
        size /= 1024
        unit += 1
    if unit == 0:
        return f"{num_bytes} B"
    return f"{size:.1f} {UNITS[unit]}"
