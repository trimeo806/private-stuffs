Here's the prompt to paste into the Coderbyte agent. It uses Vitest, the framework already in `package.json` and `src/slugify.test.ts`, and it puts every new test in one file, so you approve one edit.

```
Write edge-case tests for slugifyFileName in src/slugify.ts.
1. Read the requirement in TASK.md and the existing tests in src/slugify.test.ts. Use the same framework (Vitest: import { describe, expect, it } from "vitest") and style; do not add any new package and do not touch package.json.
2. First show a table: case, input, expected, why. Take expected values from TASK.md, not from the current code. Mark anything TASK.md does not state as "assumed".
3. Cover: empty/whitespace-only/symbols-only input (must throw Error), accent folding, runs of symbols and spaces collapsing to one hyphen, leading/trailing hyphens removed, extension = text after the LAST dot, lowercased, and dotfiles with no extension. Concretely:
   - "Café.png" -> "cafe.png"
   - "ÉTÉ 2024.JPG" -> "ete-2024.jpg"
   - "  My   File!!.TXT" -> "my-file.txt"
   - "--draft--.md" -> "draft.md"
   - "report.tar.gz" -> "report-tar.gz" (only the last dot starts the extension)
   - ".env" -> "env" (starts with a dot and has no other dot, so there is no extension)
   - ".env.local" -> "env.local"
   - "", "   ", "!!!.png" -> each throws Error
   - "İstanbul.txt" -> "istanbul.txt" (assumed: İ is an accented I)
4. Then add the tests to src/slugify.test.ts below the existing test, max 10, using it.each for cases that differ only in data, each named by the behavior it checks (for example "folds accented letters to ASCII", "throws when nothing usable is left"), no comments. For the throwing cases use expect(() => slugifyFileName(input)).toThrow(Error). Keep the existing test unchanged.
5. Do not modify src/slugify.ts or any production code. If an expectation disagrees with the code, keep the expectation and list it as a suspected bug.
```

**Heads-up: the accent-folding tests will fail against the current code.** I ran the current logic on these inputs. `src/slugify.ts` lowercases and then replaces every character outside `a-z0-9` (line 8), but it never folds accents first, so accented letters turn into hyphens and then disappear:

| Input | TASK.md expects | Current code returns |
|---|---|---|
| `Café.png` | `cafe.png` | `caf.png` |
| `ÉTÉ 2024.JPG` | `ete-2024.jpg` | `t-2024.jpg` |
| `İstanbul.txt` | `istanbul.txt` (assumed) | `i-stanbul.txt` |

When the agent's tests go red on these rows, that is a real bug, not a wrong test, so don't let it "fix" the expectation. The usual fix is to normalize the base name before line 8 with `.normalize("NFD").replace(/[̀-ͯ]/g, "")`, then run the existing regex. Make that change yourself, or as a separate approved edit after the tests are in, and rerun `npm test`.

The other cases already pass with the current code: whitespace and symbol runs, trimmed hyphens, `report.tar.gz`, `.env` and `.env.local`, the uppercase extension, and the three throwing inputs.

**Assumptions in the prompt**
- `.env` becomes `env`. TASK.md says it has no extension, so the whole name is the base, and the hyphen rules remove the leading dot.
- `İstanbul` folds to `istanbul`. TASK.md only gives `Café`, so treating the Turkish dotted I as an accented letter is my reading.
- I left out a name ending in a dot (`file.`, currently `file`) and an extension that contains spaces or symbols. TASK.md doesn't say what should happen with either, and they would use up the 10-test budget on guesses.

I didn't change any files here. The test file is left for the IDE agent to write.
