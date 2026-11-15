Here's a prompt to paste into the Coderbyte agent. It changes only one file, `src/slugify.test.ts`, so you'll need to approve one edit. The expected values come from TASK.md, not from the current code.

```
Read TASK.md and src/slugify.test.ts. Add edge-case tests for slugifyFileName to
src/slugify.test.ts using the existing Vitest setup (import from "vitest", same
describe block). Keep the existing test. Do not modify src/slugify.ts or any other
file, and do not add dependencies or config.

Expected values come from the requirement in TASK.md, not from the current
implementation. Add exactly these cases, one behavior per test:

| # | Test name (it)                                         | Input                 | Expected               |
|---|--------------------------------------------------------|-----------------------|------------------------|
| 1 | folds accented letters to ascii                        | "Café Menu.txt"       | "cafe-menu.txt"        |
| 2 | drops emoji and keeps the words around them            | "🎉 Party Time.png"   | "party-time.png"       |
| 3 | collapses symbol runs and trims edge hyphens           | "--My__File--.TXT"    | "my-file.txt"          |
| 4 | treats only text after the last dot as the extension   | "archive.tar.gz"      | "archive-tar.gz"       |
| 5 | treats a leading-dot name without another dot as no extension | ".env"         | "env"                  |
| 6 | returns a lowercase slug when there is no extension    | "README"              | "readme"               |
| 7 | throws for an empty name                               | ""                    | throws Error           |
| 8 | throws for a whitespace-only name                      | "   "                 | throws Error           |
| 9 | throws for a symbols-only name                         | "@#$%!"               | throws Error           |
| 10| throws when only an extension is usable                | "!!!.txt"             | throws Error           |

Rules:
- Use expect(...).toBe(...) for values and expect(() => slugifyFileName(x)).toThrow(Error)
  for the throwing cases.
- Test names describe behavior exactly as in the table. No comments in the file.
- No shared mutable state, no beforeEach.
- When done, show me the full file. Do not change production code even if you
  think a test will fail.
```

Then run `npm test` in the Coderbyte terminal.

**Expect 1 failure.** I ran the current `slugifyFileName` logic against these inputs. Case 1 fails: `"Café Menu.txt"` comes out as `"caf-menu.txt"`. The code never folds accents, so `é` is treated as a symbol and replaced by a hyphen. The other 9 cases pass. TASK.md asks for accent folding explicitly (`Café` becomes `cafe`), so this is a real bug that a hidden test would likely catch, not a bad test. To fix it, ask the agent for this one edit in `src/slugify.ts`, right after `.trim()`:

```
.normalize("NFD")
.replace(/[̀-ͯ]/g, "")
```

Then run `npm test` again. All 11 tests should pass.

**Assumption to check:** Case 10 (`"!!!.txt"` throws) is my reading of "throw when nothing usable is left". The extension alone is not treated as a usable name. TASK.md doesn't say this directly. If you read it differently, delete that row before you paste the prompt.

I didn't run Vitest myself because nothing is installed here. The pass and fail results above come from running the same logic in plain Node.
