---
name: ai-diff-review
description: Review AI-generated code or a diff before accepting it - find bugs, missed edge cases, wrong assumptions, complexity, security issues and style drift, then give an accept / fix / reject verdict with a one-line reason the user can feed back to the agent. Use when the user pastes agent output, asks "is this AI code ok", "should I accept this", "review this diff", or during an AI-fluency coding assessment.
---

# AI Diff Review

Goal: show judgment over AI output. Catch what the agent got wrong and say why, in words the user can reuse in the next prompt.

## Inputs

- The diff or generated code
- The requirement it should satisfy (or the task-recon checklist)
- Surrounding code if signatures, callers or conventions matter

## Checks (in order, stop wasting time on style if correctness fails)

1. **Requirement fit**: does it do what was asked, no more, no less? Scope creep counts as a defect.
2. **Correctness**: off-by-one, wrong condition, mutation of input, wrong return type, unhandled null / empty, integer overflow, float compare, wrong sort / comparator, early return skipping cleanup.
3. **Edge cases**: run each recon edge case mentally against the code.
4. **Hallucination**: APIs, methods, packages or config that do not exist in this language version or repo.
5. **Contract**: changed signatures, renamed public symbols, broken callers, changed I/O format (stdin / stdout lines in Coderbyte challenges).
6. **Complexity**: time / space; flag O(n²) where O(n) or O(n log n) is easy.
7. **Security**: injection (SQL, command), unchecked input, secrets in code, unsafe deserialization.
8. **Consistency**: matches existing naming, error handling, logging and test style; no new dependency.
9. **Tests**: does it include or break tests? Are the tests asserting real behavior?

## Output

```markdown
Verdict: ACCEPT | ACCEPT WITH FIX | REJECT

| # | Severity | Line / part | Problem | Fix |
|---|---|---|---|---|

Feedback prompt for the agent:
"<rejected/changed X because Y; now do Z, keep W unchanged>"
```

Severity: `blocker` (wrong result / crash), `major` (edge case, complexity, contract), `minor` (naming, style).

## Rules

- Verdict REJECT if any blocker. ACCEPT WITH FIX if only majors / minors that a small manual edit or one prompt fixes.
- Each finding names a concrete input that breaks it, when one exists.
- Prefer a manual edit for tiny fixes: it shows ownership in the assessment log.
- Never rubber-stamp: if nothing is wrong, say which checks passed and which edge cases were traced.

## In-IDE prompt template (after the agent proposes code)

```
Review your own change against the requirement: list any input that breaks it
(empty, duplicates, negative, very large, null), any API you used that may not
exist in <language/version>, and any signature or output-format change.
Do not rewrite yet - list issues first.
```

Follow-up when rejecting:

```
Rejected: <problem> because <input> gives <wrong result>. Fix only <function>,
keep the signature and output format unchanged, and add a test for <input>.
```
