---
name: ai-diff-review
description: Review AI-generated code, a diff or a PR before accepting it. Checks requirement fit, correctness, edge cases, hallucinated APIs, contract and stored-data changes, effects on existing callers, complexity, security and tests, including an agent that edited a test to make it pass. Returns a verdict of ACCEPT, ACCEPT WITH FIX or REJECT, a findings table graded blocker, major or minor with file:line, and ends with a single-paragraph feedback prompt to paste back to the agent. Use it whenever code came from Copilot, Cursor, Claude, ChatGPT or an in-IDE coding agent and the user is deciding whether to keep it, even if they never say review, for example "the agent wrote this, looks fine?", "should I approve this edit", "can I accept this change", "it says all tests pass, are we done?", "sanity-check this patch", "what is wrong with this AI fix", or a pasted diff right after an implement step. Also use it in timed AI-fluency coding assessments such as Coderbyte, where the accept and reject log is graded.
---

# AI Diff Review

AI diffs compile, read well and often pass the tests they touched; their mistakes are scope, skipped edge cases, the wrong layer, quiet contract changes and tests edited until green. Turn the diff into a decision plus a prompt the user pastes back. In an assessment, graders read the accept and reject log, so a rejection naming a concrete breaking input shows judgment.

Chain: task-recon, tdd-implement, ai-diff-review, edge-case-tests, ownership-writeup.

## Inputs

- **The diff**, plus the agent's claim ("all tests pass"), which is something to verify, not evidence.
- **The requirement**: task text, ticket or recon checklist; if absent, infer it from failing test names and state the inference.
- **Code the diff reaches**: callers of anything whose type, signature or behavior changed, the touched code's tests, and how data is saved and loaded.

Read the whole diff before judging any line; the trap is often in the second file.

## Steps

1. **Restate the requirement** in one line and list 3 to 6 edge cases (empty, null, duplicates, letter case, boundary and very large values, data that already exists).
2. **Sort hunks** into production, test and config. A test hunk that changes an expected value, removes an assertion, skips a test or loosens a matcher is presumed to hide a bug unless the requirement changed that behavior.
3. **Run the checks in order** (once correctness fails, style is noise):

| # | Check | Look for |
|---|---|---|
| 1 | Requirement fit | Exactly what was asked; no extra features, no partial fix |
| 2 | Correctness | Off-by-one, wrong condition, null or empty, overflow, float compare, mutated input |
| 3 | Edge cases | Trace each listed case through the new code |
| 4 | Hallucination | APIs, overloads or packages that do not exist in this version or repo |
| 5 | Contract | Changed signatures, public names, output format, stored data |
| 6 | Complexity | O(n²) where O(n log n) is easy; queries or enumeration inside loops |
| 7 | Security | Injection, unchecked input, secrets, path traversal |
| 8 | Consistency | Repo naming, error handling, test style; no new dependency |
| 9 | Tests | New tests assert real behavior; none edited to match the bug |

4. **Look one step past the diff**: a correct hunk can still leave the bug visible or break someone else. Check, or list under "Not verified" with what to search for:
   - callers that cast, narrow or store the changed value in a smaller or different type, which would bring the bug back;
   - library or aggregate APIs that behave differently from the hand-written code (checked arithmetic that throws, culture-sensitive compare, lazy enumeration);
   - behavior that existing callers, existing tests or already stored data now see differently.
5. **Trace, do not guess.** For each edge case write `input -> actual output` against the new code; run the tests when you can. A finding without a concrete breaking input goes under "Not verified", not in the table.

References: `references/ai-traps.md` when the diff touches string compare, sorting, file names, numeric sums, dates or collections (library behaviors agents get wrong, per language); `references/examples.md` for a filled-in review; `references/in-ide-prompts.md` for more paste-able prompts than the two below.

## Severity and verdict

- **blocker**: wrong result, crash, data corruption, security hole, or a test edited to hide a bug.
- **major**: missed edge case, contract or stored-data change, complexity that fails at realistic size, a fix in the wrong layer that still happens to work.
- **minor**: naming, style, readability.

Verdict:

- **REJECT**: any blocker, or a wrong approach (wrong layer, tests changed to pass) even if today's output looks right.
- **ACCEPT WITH FIX**: no blocker; a major or worthwhile minor that a small edit or one prompt resolves.
- **ACCEPT**: no blocker or major; minors are optional.

When the diff is fine, say so: invented problems read as poor judgment. Fill "Edge cases traced" and "Checks passed" so the acceptance is visibly earned.

## Output format

Use this template. Anything else for the user (a short answer, next steps, a log note, the manual edit) goes above the verdict or in "Next step". The reply ends with the feedback prompt block, nothing after it, so the user copies the last block without hunting.

```markdown
**Requirement:** <one line>
**Verdict:** ACCEPT | ACCEPT WITH FIX | REJECT - <one-sentence reason>

| # | Severity | Where | Problem | Breaking input: actual vs expected | Fix |
|---|---|---|---|---|---|
| 1 | blocker | path/File.cs:42 | ... | ... | ... |

**Edge cases traced:** <input> -> <output> (ok / wrong), ...
**Checks passed:** <check names>
**Not verified:** <caller or data effects not seen, needs a test run, or "nothing">
**Next step:** <one or two sentences: manual edit to make, what to log>

**Feedback prompt for the agent:**
> <single paragraph>
```

`Where` is `file:line` (new-file line from the hunk header; else `file` plus method). No findings: one row `| - | - | - | none | - | - |`.

## The feedback prompt

A single paragraph (no line breaks, no bullets) addressed to the agent, pasted back verbatim. It contains, in order:

1. the decision and the problem, with the input that proves it (`"A1B2" vs "a1b2" returns false`);
2. the fix: which function to change, and what to leave alone (signature, output format, stored data, existing tests);
3. the test to add for the breaking input.

For ACCEPT it is `none, accept as is`. For ACCEPT WITH FIX of about three lines or fewer, "Next step" tells the user to edit by hand (shows ownership in the log, saves a round trip) and the prompt is `Accepted; I fixed <what> by hand, no further changes.`

## In-IDE prompt templates

In-IDE agents (Coderbyte, Copilot Chat, Cursor) have no skills, so the user pastes these. Review each edit as it comes: every approval is logged.

Before reviewing, ask the agent to review itself:

```
Review your own change against the requirement: list any input that breaks it (empty, null, duplicates, negative, very large, different letter case), any API that may not exist in <language/version>, any signature, output-format or stored-data change, any caller that now behaves differently, and any test you modified. Do not rewrite yet, list issues first.
```

Rejection (the same shape as the feedback prompt):

```
Rejected: <problem> because <input> gives <actual> instead of <expected>. Fix only <function>, keep the signature, output format, stored data and existing tests unchanged, and add a test for <input>.
```

## Hand-off

Breaking inputs become tests in edge-case-tests; each rejection and manual fix, with its reason, feeds ownership-writeup.
