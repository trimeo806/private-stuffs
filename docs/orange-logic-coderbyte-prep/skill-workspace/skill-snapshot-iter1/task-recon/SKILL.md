---
name: task-recon
description: Break a coding task down before any code is written. Turns a task statement plus a repo file tree (and language, time limit) into a one-line goal, an explicit and implied requirements checklist, assumptions, a file map, edge cases that hidden tests are likely to probe, a step plan with a time budget and a cut line, and a first scoped prompt for the coding agent. Use this at the start of any timed coding assessment (Coderbyte, HackerRank, CodeSignal, AI-fluency round, take-home), when starting a bugfix or feature in an unfamiliar repo, and whenever the user pastes a task description or file tree and asks where to start, how to approach it, what the task needs, how to split the time or what to tell the AI agent first, even if they never say recon or plan. Also use it for 'recon', 'break down this task', 'plan before coding', 'scope this'. Produces a plan and a first prompt, never the implementation.
---

# Task Recon

Recon turns a task statement and a repo into the context a coding agent needs before it touches a file. Five minutes here saves twenty later: most lost time in a timed task comes from building the wrong reading of the requirements, editing a file the tests depend on, or running out of time with nothing submittable.

This skill writes no code. Code written before the plan is confirmed anchors everyone (the user and the agent) on a reading nobody checked. In an AI-fluency assessment the plan-first prompt is also part of what graders score, so the recon is a deliverable in itself.

## Gather the inputs

You need:

- the task statement, verbatim (paraphrases lose the words hidden tests are built from)
- the repo file tree, plus the README, existing tests and the entry point if you can see them
- language, framework and the test command
- the time available

If the repo is in the working directory, read it rather than asking: the README, the project file (`*.csproj`, `package.json`, `pyproject.toml`, `pom.xml`, `go.mod`), the files the task names and every existing test file. Existing tests show the naming, the error-handling style and the shape hidden tests will have.

If something is missing, ask once, in a single message, for only what blocks you. Usually that is the task statement itself. For everything else, pick a default, write it under Assumptions and carry on. A timed user cannot afford a back-and-forth.

Time defaults when the user does not say:

- Coderbyte AI-fluency assessment: 80 minutes total, with Section 2 (written answers) after Section 1 and no way back. Budget about 55 minutes of Section 1 work (recon about 8, implement and review about 35, test, polish and README about 10, submit buffer 2) and leave at least 20 for Section 2.
- Take-home or other task: use the stated limit and reserve about 15% for the final test run and the write-up.

## Steps

1. **Restate the goal in one sentence.** If one sentence will not hold it, the task has an ambiguity or two separate deliverables. Name both.

2. **Extract requirements** as a numbered checklist (R1, R2, ...). Mark each `explicit` (stated in the task) or `implied` (inferred). Implied requirements come from existing tests, method signatures and return types, the README, neighbouring code's error handling and conventions, and the fact that hidden tests compile against the current public API. Give each one a "done when" that a test or a manual run can check; a requirement nobody can check will not get built correctly.

3. **Record assumptions** for every ambiguity: `A1: <question> -> <chosen default> (<why it is the safer choice>)`. Safer means it matches existing behaviour and tests, or surprises a caller least. These go into the README later, so a grader sees the decision was deliberate.

4. **Map the files**: which to read, change, create, or leave alone. Leave alone the given test files (unless the task asks for tests), read-only or hidden files, generated code, lock files and any harness or stdin line at the bottom of a solution file. Hidden tests rely on these staying as they are.

5. **List edge cases** with the expected behaviour and the requirement each belongs to. Pick the ones that fit the input types rather than a generic list, and mark the few you think hidden tests most likely probe: every example in the task statement, every boundary word ("at most", "case-insensitive", "must not"), every error the task names. For the full catalogue by input type and per-language pitfalls, read `references/edge-cases.md`.

6. **Plan small steps**, each one prompt-sized: one bug or one function, so each agent diff is small enough to review properly. Order them for partial credit: what makes the existing tests pass or the core path work first, then the feature, then edge cases and polish. The README or write-up is short and graded, so place it above anything optional. Each step gets a "done when".

7. **Budget the time.** Give every step minutes, including the time to read and approve the agent's diff (every edit needs approval in an in-IDE agent, so review is not free). The total must fit the available time. Draw a cut line: the steps above it leave a submittable state (tests green, README written); the steps below it are dropped first if time runs short.

8. **Write the first prompt** for the coding agent, scoped to step 1 only. Give it the files to read, the one change, the constraints (keep public signatures, no new dependencies, follow the existing style), the edge cases for that step, and ask it to explain the change before or with the diff. For the anatomy of a good scoped prompt and the follow-ups for later steps, read `references/prompt-patterns.md`.

Keep the whole output short enough to read in two minutes. The user is on a clock.

## Output

Use this template, in this order:

```markdown
## Goal
<one sentence>

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | ... | explicit | ... |

## Assumptions
- A1: <question> -> <default> (<why>)

## Files
| File | Action | Why |
|---|---|---|
| ... | read / change / create / leave alone | ... |

Test command: `<command>`

## Edge cases
| Case | Expected | Req | Likely hidden test |
|---|---|---|---|

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|

Budget: <total> of <available> min, review time included. Cut line: below step <n> (<what gets dropped>).

## First prompt
<ready to paste, step 1 only>
```

Then ask the user to confirm or correct the plan before any code is written.

## Worked example

Input: "Fix `paginate(items, page, pageSize)`: the last page repeats the previous page's final item. Then add an optional `sortBy` field name. TypeScript, Vitest, 45 minutes." Tree: `src/paginate.ts`, `src/types.ts`, `test/paginate.test.ts`.

```markdown
## Goal
Make paginate return correct, non-overlapping pages and accept an optional sortBy field.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | Last page has no item from the previous page | explicit | test for 7 items, size 3, page 3 returns 1 item |
| R2 | Optional sortBy sorts ascending by that field before slicing | explicit | test with unsorted input |
| R3 | Existing call paginate(items, page, pageSize) unchanged | implied | existing tests still pass |
| R4 | Input array not mutated by sorting | implied | test compares input after call |

## Assumptions
- A1: page is 1-based? -> yes (existing test calls page 1 for the first items)
- A2: page past the end? -> empty array, no throw (matches current behaviour)

## Files
| File | Action | Why |
|---|---|---|
| src/paginate.ts | change | bug and feature |
| src/types.ts | change | add sortBy to options |
| test/paginate.test.ts | read | 1-based pages, style |

Test command: `npx vitest run`

## Edge cases
| Case | Expected | Req | Likely hidden test |
|---|---|---|---|
| items length an exact multiple of pageSize | no empty extra page | R1 | yes |
| empty items | [] | R1 | yes |
| sortBy on a missing field | stable, original order | R2 | maybe |

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 1 | fix slice bounds | 10 | R1 tests pass |
| 2 | add sortBy with a copied array | 15 | R2, R4 pass |
| 3 | README notes | 5 | A1, A2 written down |
| 4 | extra edge-case tests | 10 | table above covered |

Budget: 40 of 45 min, review time included. Cut line: below step 3 (extra edge-case tests get dropped).

## First prompt
Read src/paginate.ts and test/paginate.test.ts. Fix only the off-by-one in paginate: for 7 items with pageSize 3, page 3 must return exactly the 7th item, and an exact multiple of pageSize must not produce an extra page. Pages are 1-based. Keep the signature and style, no new dependencies, do not touch sorting yet. Explain the cause in one sentence before the diff.
```

## In-IDE prompt template

Coderbyte's in-IDE agent, and most coding agents outside Claude Code, do not have this skill, and in Coderbyte that agent's prompt log is what graders read. Have the user paste this, with the task statement and file tree attached, as the first prompt in the IDE:

```
Do not edit or create any files yet. First:
1. Restate the task in one sentence.
2. List requirements as a numbered checklist, each marked explicit or implied,
   each with how a test would check it.
3. List assumptions for anything ambiguous, with the default you pick and why.
4. List the files to read, change and leave alone.
5. List edge cases with expected behaviour, marking the ones hidden tests most
   likely check.
6. Propose a step plan where each step changes one function or fixes one bug,
   with minutes per step for a <N>-minute limit and a cut line.
Respect existing signatures and style; no new dependencies.
```

Replace `<N>` with the minutes left. When the agent answers, correct any wrong reading in the next prompt before asking for step 1: a visible correction is evidence of judgement.

## After recon

Once the user confirms the plan, implement one step at a time with `tdd-implement` (failing test first). Review each diff with `ai-diff-review`, add tests from the edge-case table with `edge-case-tests`, and hand the assumptions, the cut line and anything dropped to `ownership-writeup` for the README and written answers.
