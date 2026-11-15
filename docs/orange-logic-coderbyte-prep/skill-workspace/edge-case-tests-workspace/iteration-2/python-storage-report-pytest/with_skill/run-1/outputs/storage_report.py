from collections import defaultdict

NO_EXTENSION = "(none)"


def extension_of(name):
    stem, dot, ext = name.rpartition(".")
    if not dot or not stem:
        return NO_EXTENSION
    return ext


def summarize_by_extension(files):
    totals = defaultdict(int)
    for entry in files:
        size = entry["size"]
        if size < 0:
            raise ValueError(f"negative size for {entry['name']}")
        totals[extension_of(entry["name"])] += size
    return sorted(totals.items(), key=lambda item: (-item[1], item[0]))
