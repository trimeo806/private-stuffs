# coderbyte-prep workflow

Copy of the plugin: `coderbyte-prep/` (snapshot of `~/.claude/skills/coderbyte-prep/`, 14 Nov 2026). Edits here do not sync back.

- Install on another machine: copy `coderbyte-prep/` to `~/.claude/skills/coderbyte-prep/`, restart Claude Code, call `/coderbyte-prep:<skill>`.
- `plugin.json` holds your name and email. Strip them before sharing the folder.
- `evals/` folders are tests for the skills only. Not needed to use them.

## The chain

```
task-recon → [ (debug) → tdd-implement → ai-diff-review ] × each plan step → edge-case-tests → ownership-writeup
   plan          root cause  write code       judge the diff                    harden tests       README + Section 2
```

| # | Skill | When | You give it | You get |
|---|---|---|---|---|
| 1 | `task-recon` | minute 0, before any code | task text verbatim, file tree, language, minutes left | goal, requirements, defaults, edge cases, timed plan with cut line, recon prompt + step 1 prompt |
| 1b | `debug` | a bug, red test, build error or ticket whose cause is unknown | failure output or ticket text, task file | root cause per row (file:line, proof), the tempting wrong fix, one fix prompt per cause |
| 2 | `tdd-implement` | each plan step | the plan row or bug | failing test → smallest fix → refactor, step report |
| 3 | `ai-diff-review` | after every agent diff, before approving | diff + requirement | ACCEPT / ACCEPT WITH FIX / REJECT + one-paragraph feedback prompt |
| 4 | `edge-case-tests` | after the last step above the cut line | function + requirement + existing tests | edge-case table, behaviour-named tests, suspected bugs |
| 5 | `ownership-writeup` | last 5 min of Section 1, then Section 2 | your notes / prompt log | SOLUTION.md under the word limit (measured), Section 2 answers from facts only |

## Time plan (80 min Coderbyte AI fluency)

| Min | Phase | Skill |
|---|---|---|
| 0-8 | Recon, confirm plan | 1 |
| 8-43 | Steps above cut line: test → code → review, one step per prompt | 2 + 3 loop |
| 43-50 | Edge-case tests, fix suspected bugs if time | 4 (fixes via 2) |
| 50-55 | SOLUTION.md, final test run, submit Section 1 | 5 |
| 55-80 | Section 2, ~3-5 min per answer | 5 |

Rule: hit the cut line state (tests green, README written) before anything optional. No going back after Section 1.

## Rehearsal (Claude Code, skills available)

1. `/coderbyte-prep:task-recon` + paste task and tree. Check the defaults, correct any wrong reading.
2. Bug with unknown cause: `/coderbyte-prep:debug` first. Per step: `/coderbyte-prep:tdd-implement step <n>`. Then `/coderbyte-prep:ai-diff-review` on the diff.
3. `/coderbyte-prep:edge-case-tests` on the touched functions.
4. `/coderbyte-prep:ownership-writeup` with `PROMPT-LOG.md` → SOLUTION.md, then again for each Section 2 question.
5. Grade: `dotnet test docs/orange-logic-coderbyte-prep/drills/csharp-asset-library-grader/AssetLibrary.GraderTests`.

Keep `PROMPT-LOG.md` going the whole time: each prompt + one line `kept / rejected / changed — why`.

## Exam day (Coderbyte IDE, no skills: paste these)

### 1. Recon (first prompt, before any edit)

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

Correct one wrong reading in your next prompt. A visible correction shows judgement.

### 2a. Feature step

```
Step <n> only: <behaviour in one sentence>.
1. Do not change production code yet. Add one failing test to <test file>,
   named after the behaviour, in the existing framework and style. Show it
   and say what failure you expect.
2. After I confirm it is red, make the smallest change in <source file> that
   makes it pass. Keep public signatures, existing tests and style unchanged.
   No new packages.
3. Put the decision logic in a pure function (values in, value out, no
   repository, IO or clock) that the test project can call, and keep
   loading and saving in <method>. Add one unit test that calls it directly.
4. No new interfaces, classes or files unless a test needs the seam.
5. End with: edge cases this handles, edge cases it does not.
```

Run the tests yourself and see red before approving the production edit.

### 2a'. Diagnose before a bugfix (cause unknown)

```
Do not edit or create any files. <Paste the failure: test output / build error / stack trace / ticket text.>
1. Restate the failure as: input -> actual vs expected, using the requirement in <task file>.
2. Group the failures by shared root cause.
3. For each cause, name the file and line where the wrong value first appears, not just where it throws, in two sentences.
4. Name the quick fix that would look right but break another test or the spec.
Do not propose code yet.
```

Then one fix prompt per cause, through 2b.

### 2b. Bugfix step

```
Bug: <symptom, with the exact input and output>.
1. Before changing any code, add a failing test to <test file> that reproduces
   it with the smallest input, named by the correct behaviour.
2. Explain the root cause in two sentences, naming the line, and which other
   inputs the same cause breaks.
3. Then fix the cause in <source file>, not the reported input. Do not edit,
   skip or loosen any existing test or assertion.
```

### 3. Review every diff

Self-review first:

```
Review your own change against the requirement: list any input that breaks it (empty, null, duplicates, negative, very large, different letter case), any API that may not exist in <language/version>, any signature, output-format or stored-data change, any caller that now behaves differently, and any test you modified. Do not rewrite yet, list issues first.
```

Reject (one paragraph):

```
Rejected: <problem> because <input> gives <actual> instead of <expected>. Fix only <function>, keep the signature, output format, stored data and existing tests unchanged, and add a test for <input>.
```

Your own checks before approving: test assertion edited? signature changed? `int` where sizes can exceed 2 GB? case-sensitive compare? fix only on save, not on compare? Small fix (3 lines or less): edit by hand and log `Accepted; I fixed <what> by hand`.

### 4. Edge-case tests

```
Write edge-case tests for <function> in <source file>.
1. Read the requirement in <task file or "the task description below"> and the existing tests in <test file>. Use the same framework (<framework>) and style; do not add any new package.
2. First show a table: case, input, expected, why. Take expected values from the requirement, not from the current code. Mark anything the requirement does not state as "assumed".
3. Cover: empty/null, boundary and off-by-one, duplicates including case variants, whitespace, invalid input with the exact exception type, large values and overflow, ordering ties, paging past the end, unicode. Concretely: <4-6 cases for this function>.
4. Then create the tests in one file, <test file>, max 10, parametrized where cases only differ in data, each named by the behavior it checks, no comments.
5. Do not modify <source file> or any production code. If an expectation disagrees with the code, keep the expectation and list it as a suspected bug.
```

A suspected bug goes back through 2b.

### 5. SOLUTION.md

```
Draft a README from my notes below. Sections: Approach, How I used AI (table:
prompt intent | kept/rejected/changed | reason), Verification, Trade-offs,
Known limits. First person, under <limit> words. Use only facts in the notes;
write [gap: ...] for anything missing and "Not verified" for anything not run.
No filler words.

Notes:
<paste PROMPT-LOG.md + test results>
```

Aim 10% under the limit. Count it yourself (`wc -w SOLUTION.md`) before submitting.

### 6. Section 2 answers

```
Answer this question in 60-120 words, first person, using only my notes:
example from the session, what I did, why, result. Add no events, habits or
checks that are not in the notes.

Question: <question>
Notes: <paste notes>
```

Pattern per answer: concrete example from the session → action → reason → result. Name the real rejection or correction from your log.

## Cheat sheet

- Plan first, code second. The first prompt never edits files.
- One step per prompt, one behaviour per test, test red before code.
- Fix code, never tests. Keep signatures. No new packages.
- Review every diff: callers, overflow, case, stored data, edited tests.
- Every ambiguity gets a default, written in SOLUTION.md.
- Measure word counts, never estimate. Write only what happened.
