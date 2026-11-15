Here's a prompt to paste into the Coderbyte agent. The edge cases and expected values come from TASK.md, not from the current code, so the agent has nothing to guess. It only touches `src/slugify.test.ts`, so you approve one edit.

```
Edit only src/slugify.test.ts. Do not modify src/slugify.ts or any other file.

Add edge case tests for slugifyFileName using the existing Vitest setup in that file
(import { describe, expect, it } from "vitest"; import { slugifyFileName } from "./slugify").
Keep the existing test and add the new ones inside the same describe("slugifyFileName") block.

Requirement (from TASK.md):
- Lowercase the base name and fold accented letters to ASCII (Café -> cafe).
- Replace every run of characters other than a-z and 0-9 with a single hyphen; remove hyphens at the start and end.
- Keep the extension (text after the last dot) in lowercase. A name that starts with a dot and has no other dot (.env) has no extension.
- Throw an Error when nothing usable is left (empty, whitespace only, symbols only).

Write exactly these tests, one behavior per test, with these names and these input/expected values:

| # | Test name | Input | Expected |
|---|---|---|---|
| 1 | folds accented letters to ascii | "Résumé Final.DOCX" | "resume-final.docx" |
| 2 | collapses runs of other characters into one hyphen | "Q1 -- Sales__Report!!v2.csv" | "q1-sales-report-v2.csv" |
| 3 | trims leading and trailing hyphens and spaces | "  --Draft--  .md" | "draft.md" |
| 4 | uses only the text after the last dot as the extension | "archive.v2.tar.GZ" | "archive-v2-tar.gz" |
| 5 | treats a leading-dot name without another dot as having no extension | ".env" | "env" |
| 6 | returns the slug alone when there is no extension | "My Notes" | "my-notes" |
| 7 | throws when nothing usable is left (use it.each over the 3 inputs) | "", "   ", "@#$%&*" | throws Error |
| 8 | throws when the base name is symbols only even with an extension | "!!!.txt" | throws Error |
| 9 | drops a trailing dot with an empty extension | "notes." | "notes" |

Rules:
- Use expect(...).toBe(...) for values and expect(() => slugifyFileName(x)).toThrow(Error) for errors.
- Arrange / act / assert, no shared mutable state, no helper abstractions.
- No comments in the test file.
- Do not change the expected values to match the current output. If a test fails, leave it failing and tell me which one.
```

## Edge case table

| # | Case | Input | Expected | Source |
|---|---|---|---|---|
| 1 | accented letters plus uppercase extension | `"Résumé Final.DOCX"` | `"resume-final.docx"` | explicit |
| 2 | mixed runs of spaces, `-`, `_`, `!`; digits kept | `"Q1 -- Sales__Report!!v2.csv"` | `"q1-sales-report-v2.csv"` | explicit |
| 3 | leading/trailing spaces and hyphens around the base | `"  --Draft--  .md"` | `"draft.md"` | explicit |
| 4 | several dots, only the last one starts the extension | `"archive.v2.tar.GZ"` | `"archive-v2-tar.gz"` | explicit |
| 5 | dotfile | `".env"` | `"env"` | explicit (TASK.md says no extension, so the whole name gets slugified) |
| 6 | no extension | `"My Notes"` | `"my-notes"` | explicit |
| 7 | empty, whitespace only, symbols only | `""`, `"   "`, `"@#$%&*"` | throws `Error` | explicit |
| 8 | extension present but base is symbols only | `"!!!.txt"` | throws `Error` | assumed: a bare `.txt` is not a usable name |
| 9 | trailing dot, empty extension | `"notes."` | `"notes"` | assumed: no dangling dot |

## What to expect when you run it

I ran these same tests against your current `src/slugify.ts` (Vitest 2.1.9): **10 pass, 1 fails.**

- **#1 fails:** `"Résumé Final.DOCX"` gives `"r-sum-final.docx"`. The code never folds accents, so `é` becomes a hyphen. TASK.md says this outright (`Café` becomes `cafe`), so hidden tests will almost certainly catch it.
- Fix in `src/slugify.ts`. Apply it yourself or ask the agent in a separate prompt. Add the normalize step before `.toLowerCase()` on the base:

  ```ts
  const slug = base
    .normalize("NFD")
    .replace(/[̀-ͯ]/g, "")
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "");
  ```

  I checked that `"Résumé Final Café"` turns into `"resume final cafe"` with this fold. I did not edit your production file.

## Open questions (I used the defaults above)

- **#8 `"!!!.txt"`**: I assumed it should throw. If you'd rather it return `"txt"` or keep the extension, change that row before pasting.
- **#9 `"notes."`**: I assumed the result is `"notes"`.
- **Extensions with symbols** (for example `"a.P DF"`): TASK.md only says to lowercase the extension, and the current code keeps it as `"p df"`. I left this case out because the requirement doesn't say what should happen.
