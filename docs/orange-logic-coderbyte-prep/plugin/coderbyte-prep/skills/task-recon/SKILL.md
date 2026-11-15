---
name: task-recon
description: Break a coding task down before any code is written. Turns a task statement plus a repo or file tree into a goal, explicit and implied requirements, a chosen default for every ambiguity, a file map, edge cases with expected behaviour, a timed step plan with a cut line, and paste-ready prompts for the coding agent. Use this at the start of any timed coding assessment (Coderbyte, HackerRank, CodeSignal, AI-fluency round, take-home), when starting a bugfix or feature in an unfamiliar repo, and whenever the user pastes a task or file tree and asks where to start, how to approach it, what the task needs, how to split the time or what to type into the AI panel first, even if they never say recon or plan. Also for 'recon', 'break down this task', 'plan before coding', 'scope this'. Never writes the implementation.
---

# Task Recon

Recon gives a coding agent a checked reading of the task before it touches a file. Write no code and edit no files: code written before the plan is confirmed anchors everyone on a reading nobody checked, and in an AI-fluency assessment the plan-first prompt log is itself graded.

## Inputs

The task statement verbatim (hidden tests are built from its words), the file tree, language and test command, and the time available. If the repo is in the working directory, read it instead of asking: README, project file (`*.csproj`, `package.json`, `pyproject.toml`, `pom.xml`, `go.mod`), the files the task names, every existing test and any sample data. Existing tests show the shape of the hidden tests.

A missing input never stalls the recon. Produce the full recon now with a default for whatever the missing input would decide, and ask at most one consolidated question at the end.

Time defaults: Coderbyte AI-fluency is 80 minutes with Section 2 (written, no way back) after Section 1, so Section 1 gets about 55 (recon 8, implement and review 35, test, polish and README 10, submit 2) and Section 2 at least 20. Otherwise use the stated limit minus time already used, reserving about 15% for the final test run and write-up.

## Steps

1. **Goal** in one sentence. If it will not fit, there is an ambiguity or two deliverables; name both.

2. **Requirements** R1, R2, ..., each marked `explicit` (stated) or `implied` (from existing tests, signatures, the README, conventions, and hidden tests compiling against the current public API), each with a checkable "done when".

3. **Assumptions**: `A1: <question> -> <chosen default> (<why it is safer>)`. Every ambiguity gets a chosen default now, even one that depends on unread code: "default X; switch to Y only if <file> already does Y". "Decide later", "TBD" or "follow the existing code" with no default is not an assumption: an open question is where the agent invents behaviour and a hidden test fails. Safer means it matches existing behaviour and tests, or surprises a caller least.

4. **Files**: read, change, create, or leave alone. Leave alone given tests, hidden or generated files, lock files and any harness or stdin line.

5. **Edge cases**, each with a concrete expected behaviour (a value, an exception type, a no-op), its requirement and whether hidden tests likely probe it. When the behaviour comes from an assumption, write the behaviour and cite the An; "per A4" alone is not an expected behaviour. Mark as likely every example in the task, every boundary word ("at most", "case-insensitive"), every named error, and the result of any sample data. For cases by input type, task shape and language, read `references/edge-cases.md`.

6. **Plan** steps of one bug or one function each, so every diff is reviewable, ordered for partial credit: existing tests green or core path, then feature, then edge cases. The README or write-up (assumptions, what was cut) sits above anything optional. Each step has a "done when".

7. **Budget** minutes per step, diff review included, fitting the time. Cut line: above it is submittable (tests green, README written); below it is dropped first.

8. **Prompts**. Two kinds, never merged:
   - **Recon prompt**: only when the agent that will edit the code has no plan yet (an in-IDE assessment agent without this skill, or the user asks what to type first). Use the template below. It forbids edits, so it is never the implementation prompt.
   - **Step 1 prompt**: always. This is the first implementation prompt and it covers exactly one plan step, step 1: never two steps, both tasks, or "plan, then do step 1" in one message. Give the files to read, the one change, constraints (keep public signatures, no new dependencies, existing style, do not edit given tests), that step's edge cases with expected behaviour, and ask for the cause before the diff. For prompt anatomy and follow-ups, read `references/prompt-patterns.md`.

**Length budget.** The user reads this on a clock, so the whole reply stays under about 700 words and one screen per section:
- At most 8 requirements, 10 edge cases (likely-hidden ones first) and 7 plan steps.
- Show the 5 assumptions that most change the code as `A<n>` lines; put every other default on one final `Other defaults:` line, comma-separated. Every ambiguity still gets a default.
- One line per table cell, no prose between sections, no restating the task.

Measure with `wc -w` if you wrote it to a file; otherwise cut until each section fits the caps.

## Output

```markdown
## Goal
<one sentence>

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|

## Assumptions
- A1: <question> -> <chosen default> (<why safer>)

## Files
| File | Action | Why |
|---|---|---|

Test command: `<command>`

## Edge cases
| Case | Expected (chosen behaviour) | Req | Likely hidden test |
|---|---|---|---|

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|

Budget: <total> of <available> min, review time included. Cut line: below step <n> (<what gets dropped>).

## Recon prompt (paste first)
<only when the coding agent has no plan yet; otherwise leave this section out>

## Step 1 prompt (first implementation prompt)
<ready to paste, exactly one plan step>
```

End with the defaults most worth checking, at most one question, and a request to confirm before any code. For a filled-in example, read `references/examples.md`.

## In-IDE recon prompt template

For agents without this skill (Coderbyte's in-IDE agent, whose log graders read). Paste with the task and tree attached:

```
Do not edit or create any files yet. First:
1. Restate the task in one sentence.
2. List requirements as a numbered checklist, each marked explicit or implied,
   each with how a test would check it.
3. List assumptions for anything ambiguous, each with the default you pick and why.
4. List the files to read, change and leave alone.
5. List edge cases, each with the expected behaviour you chose, marking the ones
   hidden tests most likely check.
6. Propose a step plan where each step changes one function or fixes one bug,
   with minutes per step for a <N>-minute limit and a cut line.
Respect existing signatures and style; no new dependencies.
```

`<N>` is the minutes left. Correct any wrong reading in the agent's answer before sending the step 1 prompt: a visible correction is evidence of judgement.

## After recon

Once the plan is confirmed, implement one plan step per prompt with `tdd-implement` (failing test first). Review each diff with `ai-diff-review`, add tests from the edge-case table with `edge-case-tests`, and hand assumptions, cut line and anything dropped to `ownership-writeup`.
