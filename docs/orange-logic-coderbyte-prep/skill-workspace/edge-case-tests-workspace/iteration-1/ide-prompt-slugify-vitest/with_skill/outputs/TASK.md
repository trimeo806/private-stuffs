# Task: safe file names for uploads

Implement `slugifyFileName(name)` in `src/slugify.ts`.

- Lowercase the base name and fold accented letters to ASCII (`Café` becomes `cafe`).
- Replace every run of characters other than `a-z` and `0-9` with a single hyphen, and remove hyphens at the start and end.
- Keep the extension (text after the last dot) in lowercase. A name that starts with a dot and has no other dot (`.env`) has no extension.
- Throw an `Error` when nothing usable is left (empty, whitespace only, symbols only).
