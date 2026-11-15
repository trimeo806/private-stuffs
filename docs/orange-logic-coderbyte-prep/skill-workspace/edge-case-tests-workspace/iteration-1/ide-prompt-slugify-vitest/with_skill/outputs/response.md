Paste this into the Coderbyte agent. It asks for the table first, so you can check the cases before you approve anything. All the tests go into one file, so there is only one edit to approve.

```
Write edge-case tests for slugifyFileName in src/slugify.ts.

1. Read the requirement in TASK.md and the existing test in src/slugify.test.ts. Use the same framework (Vitest, `import { describe, expect, it } from "vitest"`) and the same style. Do not add any package and do not touch package.json.

2. Before editing any file, show me a table: case, input, expected, why. Take every expected value from TASK.md, not from what src/slugify.ts returns today. Mark anything TASK.md does not state as "assumed". Use these cases, and add at most 3 of your own:
   - extension case: "Photo.JPG" -> "photo.jpg"
   - accent fold: "Café.png" -> "cafe.png"
   - uppercase accents and a space: "ÉTÉ 2024.jpg" -> "ete-2024.jpg"
   - base that is only an accented letter: "é.txt" -> "e.txt"
   - Turkish dotted capital I: "İstanbul.png" -> "istanbul.png" (assumed: fold to ASCII)
   - mixed separators: "My\tFile__(v2)!.txt" (\t is a tab) -> "my-file-v2.txt"
   - leading and trailing hyphens: "--Draft--.md" -> "draft.md"
   - emoji: "📷 Holiday.png" -> "holiday.png"
   - several dots, the last one starts the extension: "archive.v1.TAR.GZ" -> "archive-v1-tar.gz"
   - dot file without extension: ".env" -> "env"
   - dot file with an extension: ".eslintrc.JSON" -> "eslintrc.json"
   - no dot: "README" -> "readme"
   - trailing dot: "notes." -> "notes" (assumed: an empty extension is dropped)
   - trailing space after the extension: "Report.PDF " -> "report.pdf" (assumed: surrounding whitespace is not part of the name)
   - throws Error: "", "   ", "!!!", ".", "...", "###.png" (assumed for "###.png": an extension alone is not a usable name)

3. Then add the tests to src/slugify.test.ts only, and keep the existing test. Write at most 10 tests: one it.each for the names that slugify (expect(slugifyFileName(input)).toBe(expected)) and one it.each for the names that throw (expect(() => slugifyFileName(input)).toThrow(Error)). Name each test by the behavior it checks. No comments, and no task or ticket IDs in test names.

4. Do not modify src/slugify.ts or any other production file. If an expected value disagrees with what the code returns, keep the expected value and list the case as a suspected bug at the end of your reply, with the line you think causes it. Never change an expectation to make a test pass.
```

## Edge cases: slugifyFileName

Framework: Vitest 2 (detected from package.json devDependencies and src/slugify.test.ts) | Test file: src/slugify.test.ts | Run: `npm test` or `npx vitest run src/slugify.test.ts`

Use this table to check the agent's table before you approve the edit.

| # | Case | Input | Expected | Why it matters | Source |
|---|------|-------|----------|----------------|--------|
| 1 | extension case | `Photo.JPG` | `photo.jpg` | extension left uppercase | spec |
| 2 | accent fold | `Café.png` | `cafe.png` | `[^a-z]` regex drops accented letters | spec |
| 3 | uppercase accents | `ÉTÉ 2024.jpg` | `ete-2024.jpg` | folding only after lowercasing | spec |
| 4 | base is only an accent | `é.txt` | `e.txt` | a valid name rejected as "nothing usable" | spec |
| 5 | Turkish dotted I | `İstanbul.png` | `istanbul.png` | lowercasing adds a combining dot (U+0307) | assumed |
| 6 | mixed separators | `My\tFile__(v2)!.txt` | `my-file-v2.txt` | runs not collapsed, tabs missed | spec |
| 7 | edge hyphens | `--Draft--.md` | `draft.md` | hyphens left at the start or end | spec |
| 8 | emoji | `📷 Holiday.png` | `holiday.png` | surrogate pair leaves stray hyphens | spec |
| 9 | several dots | `archive.v1.TAR.GZ` | `archive-v1-tar.gz` | split on the first dot instead of the last | spec |
| 10 | dot file | `.env` | `env` | `.env` treated as an extension | spec |
| 11 | dot file with an extension | `.eslintrc.JSON` | `eslintrc.json` | leading dot not handled when another dot follows | spec |
| 12 | no dot | `README` | `readme` | `lastIndexOf` returning -1 | spec |
| 13 | trailing dot | `notes.` | `notes` | output `notes.` with an empty extension | assumed |
| 14 | trailing space | `Report.PDF ` | `report.pdf` | whitespace kept inside the extension | assumed |
| 15 | nothing usable | `""`, `"   "`, `"!!!"`, `"."`, `"..."`, `"###.png"` | throws `Error` | empty slug returned instead of throwing | spec (`"###.png"` assumed) |

### Result

Vitest was not run because nothing is installed here. I ran a copy of the current function in Node on every input above: 10 rows match the requirement and 5 do not.

### Suspected bugs

Expect these rows to go red when the agent's tests run. That is what you want: the tests are right and the code is wrong. Don't let the agent change the expectations.

- Case 2: expected `cafe.png`, got `caf.png`.
- Case 3: expected `ete-2024.jpg`, got `t-2024.jpg`.
- Case 4: expected `e.txt`, but the function throws `Error`.
- Case 5: expected `istanbul.png`, got `i-stanbul.png`.
  - Likely cause for cases 2 to 5: src/slugify.ts lines 5-8 never fold accents. The regex on line 8 replaces `é` and the combining dot with `-`. The fix is a separate step: `.normalize("NFD").replace(/\p{M}/gu, "")` before the regex.
  - Same cause: `Ñandú.png` currently gives `and.png`.
- Case 14: expected `report.pdf`, got `report.pdf ` (trailing space kept). Likely cause: src/slugify.ts line 4. Only the base is trimmed, never the whole name or the extension.

Production code is unchanged.

### Assumptions

- Case 5: the requirement says to fold accented letters to ASCII, so I read `İ` as `i`.
- Case 13: the extension is "text after the last dot", which is empty here. I treated an empty extension as no extension, so there is no trailing dot.
- Case 14: I treated whitespace around the whole name as noise, not as part of the extension.
- Case 15, `"###.png"`: the base has nothing usable. I assumed a bare extension does not count as a usable name, so it throws.
- If you read any of these differently, change that row in the agent's table before you approve the edit.
