Here's a prompt you can paste into the Coderbyte assistant. It asks for the case table first so you can check it before you approve anything, and it puts every test in one file, so you only approve one edit.

```
Write edge-case tests for slugifyFileName in src/slugify.ts.

1. Read the requirement in TASK.md and the existing test in src/slugify.test.ts. Use the same framework (Vitest: `import { describe, expect, it } from "vitest"`, `import { slugifyFileName } from "./slugify"`) and the same style. Do not add any new package and do not change package.json.

2. Before editing anything, show me a table with these columns: case, input, expected, why. Take each expected value from TASK.md, not from what the current code returns. If TASK.md does not state a behavior, mark that row "assumed".

3. Cover these areas: empty and whitespace-only input, symbols only, accent folding, case, runs of separators, leading and trailing hyphens, which dot starts the extension, dot-files, and an Error thrown when nothing usable is left. Use these exact cases:
   - "Café.png" -> "cafe.png" (TASK.md example: accents fold to ASCII)
   - "ÉCOLE Notes.TXT" -> "ecole-notes.txt" (an uppercase accented first letter must fold, not be dropped)
   - "My.Photo.v2.JPG" -> "my-photo-v2.jpg" (only the text after the last dot is the extension; the other dots become hyphens)
   - "--Hello__World--.txt" -> "hello-world.txt" (each run becomes one hyphen, with no hyphen at the start or end)
   - ".env" -> "env" (a name that starts with a dot and has no other dot has no extension)
   - ".env.local" -> "env.local" (assumed: the second dot starts the extension)
   - "report." -> "report" (assumed: an empty extension is dropped, with no trailing dot)
   - "", "   ", "!!!", "!!!.png" -> each throws Error (assumed for "!!!.png": an extension alone is not usable)

4. Then add the tests to src/slugify.test.ts, keeping the existing test exactly as it is. Write at most 10 tests. Use `it.each` for cases that differ only in data: one for the valid name -> slug cases and one for the cases that throw. Assert throws with `expect(() => slugifyFileName(input)).toThrow(Error)`. Name each test by the behavior it checks. Write no comments.

5. Do not modify src/slugify.ts or any other production code. If an expected value disagrees with what the code does, keep the expected value and list the case under "Suspected bugs", with the line you think causes it.
```

### Rows that will fail against the current code

I ran the current `slugifyFileName` on these inputs. Expect two red rows. They are real bugs, not wrong tests:

- `"Café.png"` returns `"caf.png"`, but TASK.md expects `"cafe.png"`.
- `"ÉCOLE Notes.TXT"` returns `"cole-notes.txt"`, but it should return `"ecole-notes.txt"`.

The cause is the same for both. `src/slugify.ts` never folds accents: the chain at lines 5-9 lowercases and then runs `replace(/[^a-z0-9]+/g, "-")`, so `é` becomes a hyphen and is trimmed away. A minimal fix is to add `.normalize("NFD").replace(/[̀-ͯ]/g, "")` before the `.toLowerCase()` call. That also turns `İstanbul.jpg` into `istanbul.jpg` (today you get `i-stanbul.jpg`). The prompt above leaves production code alone on purpose, so make this fix yourself after the tests are in, or approve a separate edit for it.

All the other rows already pass: the dot-files, multiple dots, separator runs, and the four inputs that throw.

### One gap TASK.md doesn't cover

The current code does not trim the extension. `"  Report .pdf  "` becomes `"report.pdf  "`, with the trailing spaces kept. TASK.md doesn't say whether the extension should also be cleaned up, so I left that row out of the prompt. If you think the hidden tests might send names with surrounding whitespace, trim `name` first (`const trimmed = name.trim()`) and add the row `"  Report .pdf  " -> "report.pdf"`, marked "assumed".
