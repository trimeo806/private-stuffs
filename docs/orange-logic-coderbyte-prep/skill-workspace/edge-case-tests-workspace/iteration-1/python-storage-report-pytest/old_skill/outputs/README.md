# Storage report

`summarize_by_extension(files)` totals storage per file extension.

- `files` is a list of dicts with `name` (str) and `size` (int, bytes).
- The extension is the text after the last dot. Extensions are compared case-insensitively and reported in lowercase, without the dot.
- A name with no dot, only a leading dot (`.gitignore`) or a trailing dot (`notes.`) has no extension and is reported as `(none)`.
- A negative `size` raises `ValueError`.
- The result is a list of `(extension, total_bytes)` tuples, sorted by total descending, ties by extension ascending.
- An empty list returns `[]`.
