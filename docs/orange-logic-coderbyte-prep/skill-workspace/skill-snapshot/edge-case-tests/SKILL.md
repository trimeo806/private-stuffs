---
name: edge-case-tests
description: Generate an edge-case table and runnable unit tests for one function or endpoint in the project's own test framework (xUnit/NUnit, JUnit, pytest, Jest/Vitest, Go testing), covering empty, boundary, duplicate, invalid and large inputs. Use when the user asks "add tests", "what edge cases", "cover hidden test cases", "test this function", or before submitting a timed coding assessment.
---

# Edge Case Tests

Goal: catch failures that hidden test cases would catch, and prove the solution with tests the grader can read.

## Inputs

- Function / endpoint code and its signature
- Requirement or recon checklist
- Language and test framework already used in the repo (detect from existing tests; never add a new framework)

## Steps

1. Identify input dimensions: each parameter, its type, its valid range, plus any state (DB rows, cache, file).
2. For each dimension pick cases from the catalogue below that apply.
3. Write expected output for each case from the requirement, not from the current code. If the requirement does not say, mark `assumed` and state the assumption.
4. Write the tests: one behavior per test, name by behavior, arrange / act / assert, no shared mutable state.
5. Run them. Report pass / fail. A failing test is a finding: hand it to `ai-diff-review` or fix it.

## Case catalogue

| Category | Cases |
|---|---|
| Size | empty, one item, two items, max size / large n (performance) |
| Values | zero, negative, min / max int, float precision, very long string |
| Shape | duplicates, all same, already sorted, reverse sorted, nested / deep |
| Missing | null, undefined, missing field, empty string vs whitespace |
| Format | invalid format, wrong type, unicode / emoji, mixed case, leading / trailing spaces |
| State | not found, already exists, concurrent update, idempotent repeat |
| Boundary | exactly at limit, one below, one above |

## Output

```markdown
| # | Case | Input | Expected | Source |
|---|---|---|---|---|
| 1 | empty list | [] | 0 | explicit / assumed |
```

Then the test file content, then the run result.

## Rules

- Test names describe behavior: `returns_zero_for_empty_list`, `it('rejects negative quantity')`. No ticket or step IDs.
- No comments in test code.
- Max ~10 tests per function in a timed setting: pick the highest-risk cases first.
- Do not change production code in this skill.

## In-IDE prompt template

```
List edge cases for <function> as a table (case, input, expected) based on the
requirement, not the current implementation. Then write tests using the
existing <framework> setup in <test file>, one behavior per test, named by
behavior. Do not modify production code.
```
