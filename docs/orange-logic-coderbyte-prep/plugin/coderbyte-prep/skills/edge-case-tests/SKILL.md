---
name: edge-case-tests
description: Build an edge-case table (input, expected, why) and runnable unit tests for one function or endpoint, in the test framework the repo already uses (xUnit/NUnit, JUnit, pytest, Jest/Vitest, Go testing). Covers empty and null input, boundaries and off-by-one, duplicates and case variants, whitespace, invalid input and the exception it should throw, large values and overflow, ordering ties, pagination past the end, unicode and culture. Use this whenever the user asks to add or harden tests, asks what edge cases exist, "what could the hidden tests check", "harden this before I submit", "cover the hidden test cases", "test this function", "is this covered", or has just finished a bugfix or feature in a timed coding assessment (Coderbyte, HackerRank, take-home) and is about to submit, even if they do not say the word test. Also use it to write a paste-ready prompt that makes an in-IDE AI assistant write those tests.
---

# Edge Case Tests

Hidden tests in a graded assessment probe the same few places: empty input, the exact boundary, the case-variant duplicate, the invalid argument, the page past the end, the number that overflows. This skill turns one function or endpoint into a short edge-case table and a test file in the project's own framework.

It runs after `tdd-implement` and `ai-diff-review` and before `ownership-writeup`, which can reuse the table and the suspected bugs.

## Workflow

### 1. Detect the framework

Check the build files (`*.csproj`, `pom.xml` / `build.gradle`, `pyproject.toml` / `conftest.py`, `package.json`, `go.mod`) and open one existing test file. Copy its imports, naming, assertion library and setup. Add no new package: it can break the grader's build. Read your framework's section of `references/frameworks.md` only if the existing tests do not show how to parametrize or assert an exception.

### 2. Pin the contract

From the task statement, README or TASK file, doc comments, existing tests and signature, take every expected value, never from what the code returns: expectations copied from the code pass and freeze the bug. Where the contract is silent, pick the most defensible behavior, mark the row `assumed` and say why.

### 3. Scan the code for risky arithmetic

Hidden tests hit the implementation's arithmetic more often than the spec's wording, so read the code and add a row for each of these:

- Division or modulo: an exact multiple and one more than a multiple (floor where ceiling was meant).
- Multiplication or sum on fixed-size integers (offsets like `(page - 1) * size`, byte totals, counters): values near the type's maximum, with a factor above 1 so the product really wraps.
- Comparison against a limit `L`: `L - 1`, `L`, `L + 1`, plus `0` and `1`.
- Normalization (trim, lowercase, split, dedupe): an input where its order or absence changes the result.

Skip rows the types make impossible (Python ints never overflow).

### 4. Pick the cases

Walk this per parameter and per piece of state read:

| Category | Typical cases |
|---|---|
| Empty / null | empty collection, single item, null argument, whitespace-only string, missing field |
| Duplicates | exact, case variants (`JPG` vs `jpg`), equal after trimming |
| Invalid input | negative, out of range, wrong format, with the exact exception type |
| Large values | `int.MaxValue` in index math, sums over 2^31, many items |
| Ordering | ties on the sort key, mixed-case keys, reverse sorted |
| Paging | page 0, last partial page, past the end, size above the total, empty source |
| Unicode / culture | accented letters, emoji, Turkish `i`, locale number parsing |

For strings, money, dates, paging or HTTP, read the matching section of `references/case-catalogue.md` (concrete inputs, usual expectation, bug caught).

Rank by risk and keep about ten, boundary, invalid-input, overflow and duplicate rows first: ten sharp cases beat thirty shallow ones.

### 5. Write the tests

- One behavior per test, named by it: `returns_empty_items_when_page_is_past_the_end`, `it('rejects a negative size')`. No ticket or spec IDs.
- Cases that differ only in data share one parametrized test (`[Theory]`, `@ParameterizedTest`, `pytest.mark.parametrize`, `test.each`, table-driven Go).
- Assert the exact exception type, and the parameter name where the framework allows it.
- One bug per test: an ordering row uses inputs that do not also trip a casing or boundary bug.
- Put the tests beside the existing ones so the runner picks them up; never delete or weaken an existing test. No comments.
- Leave production code untouched: this skill measures the code, and changing it hides what the measurement found.

### 6. Run and report

Run the new tests, then the whole suite. For each failure, re-read the contract for that row:

- Expectation matches the contract: the code is wrong. Keep the test and list it under Suspected bugs with the likely line. Fixing it belongs to `tdd-implement`, ideally after the user decides.
- Expectation was a misreading: correct the test and say so under Assumptions.

Never edit an expectation just to make it pass; a red test against a real bug is the most valuable output. If the tests cannot run, say "not run" and why, and trace the risky rows through the code by hand so likely bugs are still reported.

## Output

```markdown
## Edge cases: <function or endpoint>

Framework: <framework> (detected from <file>) | Test file: <path> | Run: `<command>`

| # | Case | Input | Expected | Why it matters | Source |
|---|------|-------|----------|----------------|--------|
| 1 | <short name> | <input> | <value or exception type> | <the bug it would catch> | spec / assumed |

### Tests
<the new test file, or the block added to an existing one>

### Result
<n> passed, <n> failed | not run: <reason>

### Suspected bugs
- Case <#>: expected <x>, got <y>. Likely cause: <file:line>. Production code left unchanged; the fix goes to `tdd-implement`.

### Assumptions
- Case <#>: <what was assumed and why>
```

Drop empty sections. A filled-in example: `references/examples.md`.

## In-IDE prompt template

Assessment IDEs such as Coderbyte ship an AI assistant that can only create and edit files, needs approval per edit and has none of these skills. Fill in the placeholders and give the user this to paste:

```
Write edge-case tests for <function> in <source file>.
1. Read the requirement in <task file or "the task description below"> and the existing tests in <test file>. Use the same framework (<framework>) and style; do not add any new package.
2. First show a table: case, input, expected, why. Take expected values from the requirement, not from the current code. Mark anything the requirement does not state as "assumed".
3. Cover: empty/null, boundary and off-by-one, duplicates including case variants, whitespace, invalid input with the exact exception type, large values and overflow, ordering ties<, paging past the end><, unicode>. Concretely: <4-6 cases for this function>.
4. Then create the tests in one file, <test file>, max 10, parametrized where cases only differ in data, each named by the behavior it checks, no comments.
5. Do not modify <source file> or any production code. If an expectation disagrees with the code, keep the expectation and list it as a suspected bug.
```

The table first lets the user review cases before approving an edit; one file means one approval. Fill step 3 with concrete inputs (for a file name: empty string, `.gitignore`, `Photo.JPG`, `café.png`) so the assistant does not stop at generic ones. If you can already see a row the current code will fail, tell the user next to the prompt.
