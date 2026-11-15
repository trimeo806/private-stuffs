---
name: debug
description: Find the root cause of a failure before anyone fixes it - red tests, build or compile errors, runtime exceptions with a stack trace, or a symptom-only bug ticket with no test and no trace. Reproduces, groups failures by shared cause, proves the cause with evidence (file:line, input -> actual vs expected), names every other input the same cause breaks, and hands off a fix direction plus a failing-test spec to tdd-implement; it does not edit production code. Has paste-ready diagnose-only prompts for an in-IDE agent. Use it whenever something is broken and the cause is not yet known, even if the user never says debug - "why are these tests failing", "build broke after I accepted the agent's edit", "what does this NullReferenceException mean", "support says X happens, where do I start", "the agent tried twice and it's still red", "dotnet test shows 4 failures" - and especially in timed AI-fluency coding assessments (Coderbyte, Orange Logic, take-homes) where a correct diagnosis before the fix prompt is graded. C#/.NET first; also Java, Python, TypeScript, Go. Not for writing the fix (tdd-implement) or judging a diff someone already wrote (ai-diff-review).
---

# Debug

Turn "it's broken" into a proven root cause and a hand-off a fix can be built on. In a timed assessment the cheapest mistake is fixing the symptom: the visible test goes green and the hidden tests beside it stay red. A diagnosis that names the line, the reason and the other inputs it breaks makes the fix prompt precise and shows the grader judgment, not trial and error.

Chain: task-recon, **debug**, tdd-implement, ai-diff-review, edge-case-tests, ownership-writeup. Debug sits before tdd-implement whenever the cause of a bug is not already obvious.

## Ground rules

- **Diagnose, do not fix.** Leave production code untouched. The fix belongs to tdd-implement, so it gets its own red test and its own reviewed diff.
- **Tests are presumed right.** In an assessment the task usually says "fix the code, not the tests". A test is wrong only if it contradicts the written requirement; quote both when you claim that.
- **Evidence over plausibility.** A cause is proven only by a run (test output, a scratch test, a printed value) or a step-by-step trace with concrete values. Anything else goes under "Not verified".
- **Timebox.** About 8 minutes per bug in an 80-minute assessment. If the cause is not proven by then, report the best hypothesis with what would confirm it, so the user can decide to move on.

## Inputs

- **The failure**: test output, build output, stack trace, or the ticket text verbatim.
- **The requirement**: task text, README or spec. It decides what "correct" means; the current code does not.
- **The repo**: the failing test, the code it reaches, and how data is saved and loaded.
- **Commands** to build, run the suite and run one test (`dotnet test --filter "FullyQualifiedName~Name"`, `pytest -k`, `npx vitest run -t`).

## The loop

1. **Reproduce.** Run the build or suite yourself and read the real output; a pasted log can be stale. Symptom-only ticket: rewrite it as one concrete case, `input -> actual (reported) vs expected (requirement)`, before touching code. Cannot run anything: say "not run", trace by hand, give the command.
2. **Classify and read the right part** (table below). Note every failure, then group them: several red tests often share one cause, and fixing that one cause is one step, not several.
3. **Narrow.** Follow the data from entry point to the failing value: where it is created, normalized, compared, stored, returned. The line where the wrong value first appears is the cause site; the line that throws or asserts is only the symptom site. These differ more often than not.
4. **Hypothesize, then rank.** Write two or three candidate causes, each with the observation that would separate it from the others. Check the cheapest discriminating observation first.
5. **Prove it.** Run an existing test filtered to the case, or write a scratch test with the smallest input, or trace values line by line. If you write a test, name it by the correct behaviour (`expired_link_is_invalid_with_fake_clock`), put it in the existing test project and leave it red: it becomes tdd-implement's red test. Do not change production code to "see if it helps".
6. **Blast radius.** Ask what else the same cause breaks: other inputs (empty, boundary, letter case, very large, ties, other time zones), other call sites using the same pattern, already stored data, hidden tests that probably check the neighbouring rule. Search for the pattern (`grep -n "DateTime.Now"`, every `OrderBy(`) rather than guessing.
7. **Hand off.** Fix direction in one or two sentences: which function changes, what stays (signature, output format, stored data, existing tests), and why fixing at the cause site beats the symptom site. Plus a failing-test spec per cause.

### Classify by failure type

| Type | Read first | Typical trap |
|---|---|---|
| Build / compile | The **first** error only; later ones often cascade from it. Error code, file:line, and what changed last (the agent's edit, `git diff`) | API that does not exist in this version (hallucinated), changed signature that breaks callers or hidden tests, missing `using` |
| Failing test | Test name, expected vs actual, then the test body: it is the clearest statement of the contract | Several failures from one cause; actual value hints at the cause (off by one page, wrong order, `1024.0 KB`) |
| Runtime exception | Exception type and message, then the **topmost frame in the project's own code**, not framework frames | `Sequence contains no elements` on empty data, null from a lookup, checked overflow, culture-sensitive parse |
| Symptom-only ticket | Ticket words that name a condition ("uppercase", "Vietnam", "large files", "after restart") | The condition in the ticket is the clue: case, time zone, size, ordering, state |

`references/failure-patterns.md` maps common error codes, exception messages and symptom words to their usual causes (C# first, then Python, TypeScript, Java). Read it when the failure matches nothing you recognize in a minute. `references/examples.md` shows a complete diagnosis.

## Output format

Use this template. Short answers to side questions go above it; the reply ends with the in-IDE prompt block, so the user copies the last block without hunting.

```markdown
## Diagnosis: <one-line symptom>

**Reproduced:** `<command>` -> <real result: n failed, error code, exception> | not run (<why>)
**Requirement:** <the rule the failure breaks, quoted or paraphrased from the task>

| # | Failure(s) | Root cause (file:line) | Evidence: input -> actual vs expected | Confidence |
|---|---|---|---|---|
| 1 | <test names / error code / ticket case> | <line and why it is wrong> | <concrete trace or run result> | proven / likely |

**Symptom site vs cause site:** <where it shows vs where it starts, and why fixing the symptom site would leave hidden tests red>
**Blast radius:** <other inputs, call sites or stored data the same cause breaks>
**Ruled out:** <hypotheses checked and why they are not it>
**Fix direction (for tdd-implement):** <per cause: change X in function Y; keep signature, output format, stored data, existing tests>
**Failing test to write first:** <per cause: behaviour name, input, expected> (or the repro test already written, left red)
**Not verified:** <what was not run or seen, or "nothing">

**In-IDE fix prompt:**
> <single paragraph, see below>
```

One row per root cause, not per failure. Several failures with one cause share a row.

## In-IDE prompt templates

In-IDE agents (Coderbyte's AI panel, Copilot Chat, Cursor) do not have this skill. These keep the agent diagnosing instead of editing, which matters because in Coderbyte every edit needs approval and every prompt is in the report.

Diagnose first (the agent must not edit):

```
Do not edit or create any files. <Paste the failure: test output / build error / stack trace / ticket text.>
1. Restate the failure as: input -> actual vs expected, using the requirement in <task file>.
2. If there are several failures, group them by shared root cause.
3. For each cause, name the file and line where the wrong value first appears (not just where it throws), and explain why in two sentences.
4. List two other inputs or call sites the same cause breaks.
5. Say how to prove it: which existing test to run, or the smallest new test.
Do not propose code yet.
```

Fix prompt, once the cause is proven (the closing block of the output fills this in; it is the bugfix prompt of tdd-implement with the diagnosis attached):

```
Root cause: <file:line> <why>, which also breaks <other inputs>. Before changing code, add a failing test to <test file> named <behaviour> with input <x> expecting <y>. Then fix the cause in <function>, not the reported input; keep the signature, output format, stored data and every existing test and assertion unchanged, and add no packages.
```

When the agent's diagnosis is wrong, correct it in the next prompt with the evidence ("No: <run> shows <value> at <line>, so the cause is <x>"). That visible correction is exactly what assessment graders look for, so log it.

## Hand-off

The fix direction and failing-test spec go to **tdd-implement**, whose diff goes through **ai-diff-review**. Blast-radius inputs seed **edge-case-tests**. Log the agent's wrong guesses and your corrections for **ownership-writeup**.
