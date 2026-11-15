from sizes import format_size


def storage_line(name: str, num_bytes: int) -> str:
    return f"{name}: {format_size(num_bytes)}"
