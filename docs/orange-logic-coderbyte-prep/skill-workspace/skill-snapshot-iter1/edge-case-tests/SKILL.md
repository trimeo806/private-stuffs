---
name: edge-case-tests
description: Build an edge-case table (input, expected, why) and runnable unit tests for one function or endpoint, in the test framework the repo already uses (xUnit/NUnit, JUnit, pytest, Jest/Vitest, Go testing). Covers empty and null input, boundaries and off-by-one, duplicates and case variants, whitespace, invalid input and the exception it should throw, large values and overflow, ordering ties, pagination past the end, unicode and culture. Use this whenever the user asks to add or harden tests, asks what edge cases exist, "what could the hidden tests check", "harden this before I submit", "cover the hidden test cases", "test this function", "is this covered", or has just finished a bugfix or feature in a timed coding assessment (Coderbyte, HackerRank, take-home) and is about to submit, even if they do not say the word test. Also use it to write a paste-ready prompt that makes an in-IDE AI assistant write those tests.
---

# Edge Case Tests

Hidden test cases in a graded assessment probe the same few places every time: empty input, the exact boundary, the duplicate that differs only in case, the invalid argument, the page past the end. Partial credit is per test, so each of those you cover is points you keep. Outside an assessment the same list catches the bugs that reach production. This skill turns one function or endpoint into a short table of those cases and a test file that runs in the project's own framework.

It sits after `tdd-implement` and `ai-diff-review` and before `ownership-writeup`. Its output (the table and any suspected bugs) is good material for the write-up.

## Workflow

### 1. Detect the framework from the repo

Look at the existing tests and build files before writing anything: `*.csproj` package references, `pom.xml` or `build.gradle`, `pyproject.toml` or `conftest.py`, `package.json` devDependencies, `*_test.go`. Open one existing test file and copy its namespace, imports, naming style, assertion library and setup. A test written in a framework the runner does not know is a test nobody runs, and in an assessment a new dependency can break the build the grader uses.

Read `references/frameworks.md` for the detection signals, the parametrized-test shape, exception assertions and the run command of the framework you found.

### 2. Pin the contract

Collect what the function is supposed to do: the task statement, the task-recon checklist, README or TASK file, doc comments, existing tests, the signature and types. Write every expected value from that contract, never from what the current code returns. If you derive expectations from the code, the tests pass and freeze the bug in place.

When the contract is silent on a case, choose the most defensible behavior, mark the row `assumed` and say why. A stated assumption is something a reviewer can agree with; a silent one looks like a guess.

### 3. List the input dimensions

For each parameter note its type and valid range. Add the state the function reads (collection contents, database rows, files, clock, culture) and the output properties a caller relies on (ordering, totals, exception type). Edge cases live where those ranges end and where two dimensions meet.

### 4. Pick cases from the catalogue

Walk this checklist per dimension and keep the ones that apply:

| Category | Typical cases |
|---|---|
| Empty / null | empty collection, null argument, empty string vs whitespace-only, missing field |
| Boundary | 0, 1, exactly at the limit, one below, one above, last full page vs partial page |
| Duplicates | exact duplicates, case variants (`JPG` vs `jpg`), same value after trimming |
| Whitespace | leading, trailing, internal runs, tabs and newlines |
| Invalid input | negative, out of range, wrong format, and the exact exception type expected |
| Large values | `int.MaxValue`, sums over 2^31, long strings, many items |
| Ordering | ties on the sort key, stable secondary order, already sorted, reverse sorted |
| Paging | page 0, first page, last page, page past the end, page size larger than the total |
| Unicode / culture | accented letters, emoji, Turkish `i`, culture-specific casing and number parsing |

The full catalogue in `references/case-catalogue.md` gives concrete inputs and the usual expected behavior for each category; read it when a dimension is unfamiliar or the function touches strings, money, dates or paging.

Rank by risk and keep about ten. In a timed setting, ten sharp cases beat thirty shallow ones, and the boundary, invalid-input and duplicate rows tend to be the ones hidden tests check first.

### 5. Write the tests

- One behavior per test, named by the behavior: `returns_empty_items_when_page_is_past_the_end`, `it('rejects a negative size')`. Names never cite ticket, task or spec IDs, because the reader of the test file has never seen the ticket.
- Group cases that differ only in data into one parametrized test (`[Theory]`, `@ParameterizedTest`, `pytest.mark.parametrize`, `test.each`, table-driven Go). It keeps the file short and makes a missing row easy to spot.
- Arrange, act, assert, with no shared mutable state between tests.
- Assert the specific exception type, and the parameter name when the framework allows it. "Throws something" passes for the wrong reason too often.
- Put the tests next to the existing ones, in the same project or folder, so the existing runner picks them up.
- No comments in the test code; the test name is the explanation.
- Leave production code untouched. This skill measures the code; changing it here hides what the measurement found.

### 6. Run and report

Run only the new tests first, then the whole suite. If a test fails, re-read the contract for that row:

- Expectation matches the contract: the code is wrong. Keep the test as it is and list it under Suspected bugs with the likely line. Fixing it belongs to `tdd-implement`, ideally after the user decides.
- Expectation was a misreading: correct the test and say so in Assumptions.

Never edit an expectation just to make it pass. A red test against a real bug is the most valuable output of this skill.

If the tests cannot be run (no SDK in the environment, no permission), say "not run" and why, and keep the table and tests anyway. Trace the suspicious rows through the code by hand so likely bugs still get reported.

## Output

Use this template:

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
- Case <#>: expected <x>, got <y>. Likely cause: <file:line>. Production code left unchanged.

### Assumptions
- Case <#>: <what was assumed and why>
```

Drop the Suspected bugs or Assumptions heading when it has nothing under it.

## Worked example

Function: `normalize_tags(tags: list[str]) -> list[str]`. Contract: trim each tag, lowercase it, drop empty ones, remove duplicates, return sorted. `None` raises `TypeError`. Repo has `pyproject.toml` with pytest.

| # | Case | Input | Expected | Why it matters | Source |
|---|------|-------|----------|----------------|--------|
| 1 | empty list | `[]` | `[]` | loops that assume one item | spec |
| 2 | case variants | `["Logo", "logo", "LOGO"]` | `["logo"]` | dedupe before lowercasing | spec |
| 3 | whitespace duplicates | `[" logo", "logo "]` | `["logo"]` | dedupe before trimming | spec |
| 4 | whitespace only | `["  ", ""]` | `[]` | empty tags kept after trim | spec |
| 5 | unicode | `["Café"]` | `["café"]` | ASCII-only lowercasing | assumed |
| 6 | null input | `None` | `TypeError` | missing guard | spec |

```python
import pytest

from tags import normalize_tags


@pytest.mark.parametrize(
    ("tags", "expected"),
    [
        ([], []),
        (["Logo", "logo", "LOGO"], ["logo"]),
        ([" logo", "logo "], ["logo"]),
        (["  ", ""], []),
        (["Café"], ["café"]),
    ],
    ids=["empty", "case-variants", "whitespace-duplicates", "whitespace-only", "unicode"],
)
def test_normalizes_tags(tags, expected):
    assert normalize_tags(tags) == expected


def test_rejects_none():
    with pytest.raises(TypeError):
        normalize_tags(None)
```

Result: 5 passed, 1 failed. Suspected bug: case 3 returns `["logo", "logo"]` because `tags.py` dedupes before `strip()`. Production code left unchanged.

## In-IDE prompt template

Assessment IDEs such as Coderbyte ship their own AI assistant. It can only create and edit files, every edit needs approval, and it has none of these skills. Fill in the placeholders and paste this so it follows the same discipline:

```
Write edge-case tests for <function> in <source file>.
1. Read the requirement in <task file or "the task description below"> and the existing tests in <test file>. Use the same framework (<framework>) and style; do not add any new package.
2. First show a table: case, input, expected, why. Take expected values from the requirement, not from the current code. Mark anything the requirement does not state as "assumed".
3. Cover: empty/null, boundary and off-by-one, duplicates including case variants, whitespace, invalid input with the exact exception type, large values, ordering ties<, paging past the end><, unicode>.
4. Then create the tests in one file, <test file>, max 10, parametrized where cases only differ in data, each named by the behavior it checks, no comments.
5. Do not modify <source file> or any production code. If an expectation disagrees with the code, keep the expectation and list it as a suspected bug.
```

Asking for the table first lets the user review the cases before approving any file edit, and one test file means one approval instead of many. Replace the placeholders with concrete cases for the function (for a file name: empty string, `.gitignore`, `Photo.JPG`, `café.png`) so the assistant does not stop at generic ones.
