---
name: task-recon
description: Turn a coding task statement plus a repo file tree into a requirements checklist, assumptions, edge cases, a step plan and a time budget before any code is written. Use at the start of a timed coding assessment (Coderbyte, take-home, AI-fluency round), a bugfix or feature in an unfamiliar repo, or when the user says "recon", "break down this task", "what does this task need", "plan before coding".
---

# Task Recon

Goal: build the context an AI agent needs before it writes code. No implementation in this skill.

## Inputs

Ask once for whatever is missing, then proceed:
- Task statement (verbatim)
- File tree, plus entry point, existing tests and README if any
- Language / framework, how tests run
- Time available (default: 80 min total, Section 2 needs 20)

## Steps

1. Restate the goal in one sentence.
2. Extract requirements as a numbered checklist. Mark each `explicit` (stated) or `implied` (inferred).
3. List assumptions where the task is ambiguous. One line each, with the safer default chosen.
4. Map files: which to read, which to change, which to leave alone (read-only, hidden, generated).
5. Edge cases: empty, single item, duplicates, negative / zero, very large, null / missing fields, invalid format, unicode, ordering, concurrency if state is shared.
6. Plan: small steps, each one prompt-sized (one function or one bug). Order by risk: core path first, edge cases next, polish last.
7. Time budget per step. Flag the cut line: what to drop if time runs out.
8. Write the first prompt the user should send to the in-IDE AI.

## Output

```markdown
## Goal
<one sentence>

## Requirements
| # | Requirement | Type |
|---|---|---|

## Assumptions
- A1: <ambiguity> → <chosen default>

## Files
| File | Action | Why |
|---|---|---|

## Edge cases
- <case> → <expected behavior>

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
Cut line: <steps to drop under time pressure>

## First prompt
<ready-to-paste prompt>
```

## Rules

- No code. Code starts after the user confirms the plan.
- Every requirement and edge case must be checkable by a test or a manual run.
- Keep it short enough to read in 2 minutes.

## In-IDE prompt template (type into Coderbyte AI panel, attach file tree + task)

```
Before writing any code: restate the task in one sentence, list explicit and
implied requirements, list assumptions for anything ambiguous, list edge cases
with expected behavior, and propose a step plan where each step changes one
function. Respect existing signatures and style; no new dependencies.
```
