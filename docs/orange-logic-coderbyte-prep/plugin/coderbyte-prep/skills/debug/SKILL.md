---
name: debug
description: Find the root cause of a failure before anyone fixes it - red tests, build or compile errors, runtime exceptions, or a symptom-only bug ticket with no test or trace. Reproduces, groups failures by shared cause, proves each cause with a real run (file:line, input with actual vs expected), flags the tempting wrong fix, and hands off one short fix prompt per cause to tdd-implement; never edits production code. Short by design for timed work. Use whenever something is broken and the cause is unknown, even without the word debug - "why are these tests failing", "build broke after I accepted the agent's edit", "what does this exception mean", "support says X happens, where do I start", "the agent tried twice and it's still red" - and especially in timed AI-fluency assessments (Coderbyte, Orange Logic, take-homes). C#/.NET first; also Java, Python, TypeScript, Go. Not for writing the fix (tdd-implement) or judging an existing diff (ai-diff-review).
---

# Debug

Turn "it's broken" into a proven root cause and a fix prompt per cause. In a timed assessment the costly mistake is fixing the symptom: the visible test goes green and the hidden tests beside it stay red. The other costly mistake is a long investigation: the user is on a clock and reads every word you write. So prove the cause cheaply, say it briefly, and stop.

Chain: task-recon, **debug**, tdd-implement, ai-diff-review, edge-case-tests, ownership-writeup.

## Ground rules

- **Diagnose, do not fix.** Leave production code untouched; the fix belongs to tdd-implement so it gets its own red test and reviewed diff.
- **Tests are presumed right.** A test is wrong only if it contradicts the written requirement; quote both if you claim that.
- **Proof, then stop.** A cause is proven by one run (test output, a scratch test, a printed value) or a concrete value trace. Once each cause is proven, stop digging. Extra environments, extra probes and edge cases beyond the spec belong to edge-case-tests later.
- **Timebox about 8 minutes per bug.** Not proven by then: report the best hypothesis and the one check that would confirm it.

## The loop

1. **Reproduce.** Run the build or suite yourself; a pasted log can be stale. Symptom-only ticket: write it as one concrete case, `input -> actual (reported) vs expected (requirement)`, before reading code. Cannot run anything: say "not run" and trace by hand.
2. **Classify** (table below) and **group** the failures: several red tests often share one cause, and one cause is one fix step.
3. **Narrow.** Follow the data from entry point to the wrong value. The line where the wrong value first appears is the cause site; the line that throws or asserts is the symptom site.
4. **Prove** each cause with the cheapest discriminating run: an existing test filtered to the case, or one scratch test with the smallest input. A repro test you keep is named by the correct behaviour, sits in the existing test project and stays red; it becomes tdd-implement's red test. Never change production code "to see".
5. **Check the obvious fix.** Ask what a hurried agent would change (delete the line, special-case the input, edit the test, swap in `DateTime.UtcNow` instead of the injected clock). If that fix would leave a test or the spec broken, say so in one line: this is the trap the fix prompt must rule out.
6. **Search for the same pattern** once (`grep -n "DateTime.Now"`, every `OrderBy(`) and add any other site to the cause's row. Other inputs the cause breaks: at most two, and only ones the written requirement covers.

### Classify by failure type

| Type | Read first | Typical trap |
|---|---|---|
| Build / compile | The **first** error only; later ones often cascade. Error code, file:line, what changed last (the agent's edit, `git diff`). Errors in a referenced project can hide errors in the test project. | API that does not exist (hallucinated), changed signature that breaks callers or hidden tests, missing `using` |
| Failing test | Test name, expected vs actual, then the test body: the clearest statement of the contract | Several failures from one cause; the actual value hints at it (sorted by the wrong key, off by one page) |
| Runtime exception | Exception type and message, then the topmost frame in the project's own code | `Sequence contains no elements` on empty data, null from a lookup, overflow, culture-sensitive parse |
| Symptom-only ticket | The condition the ticket names ("uppercase", "Vietnam", "large files", "after restart") | That condition is the clue: case, time zone, size, ordering, state |

Read `references/failure-patterns.md` when the failure matches nothing you recognize within a minute. `references/examples.md` shows a complete reply.

## Output: short by default

Aim for about 300 words before the prompts. Every line the user reads costs them assessment time; anything beyond the template below goes only if the user asked for detail. No headings beyond the one title, no "Ruled out" section, no restating the task.

```markdown
## Diagnosis: <n> failure(s), <k> root cause(s)

**Reproduced:** `<command>` -> <real result> | not run (<why>)

| # | Failure(s) | Root cause (file:line) | Proof: input -> actual vs expected |
|---|---|---|---|
| 1 | <tests / error code / ticket case> | <line, why wrong; other site with the same pattern> | <run result> |

**Trap:** <the tempting wrong fix and what it breaks, or "none">
**Not verified:** <one line, or "nothing">

**Fix prompts, one per cause, send in order, run tests between:**

1. > <single paragraph for cause 1>
2. > <single paragraph for cause 2>
```

- One table row per root cause, not per failure.
- Order prompts so the cheapest or most-blocking cause goes first (a compile error before anything it hides).
- If the user must also log something (a rejection of an agent edit), put that one paragraph above the title.
- The reply ends with the last fix prompt, nothing after it, so the user copies the last block without hunting. Notes such as "repo unchanged" or what to log go above the title or nowhere.

### The fix prompt

One paragraph per cause, under about 70 words, addressed to the in-IDE agent. In order: the cause with file:line, the failing test to add first (name, input, expected) or the existing red test to make pass, the change at the cause site, and what stays unchanged (signature, output format, stored data, existing tests, packages). Name the trap when there is one ("use ThenBy, do not delete the line"). One cause per prompt keeps each agent diff small enough to review, and each accepted step is visible in the prompt log graders read.

## In-IDE diagnose prompt

In-IDE agents (Coderbyte's AI panel, Copilot Chat, Cursor) do not have this skill. When the user wants the agent itself to diagnose, give them this; it stops the agent from editing before the cause is known:

```
Do not edit or create any files. <Paste the failure: test output / build error / stack trace / ticket text.>
1. Restate the failure as: input -> actual vs expected, using the requirement in <task file>.
2. Group the failures by shared root cause.
3. For each cause, name the file and line where the wrong value first appears, not just where it throws, in two sentences.
4. Name the quick fix that would look right but break another test or the spec.
Do not propose code yet.
```

When the agent's diagnosis is wrong, correct it with the evidence ("No: <run> shows <value> at <line>, so the cause is <x>"). That visible correction is what assessment graders look for; log it.

## Hand-off

Each fix prompt runs through **tdd-implement**, and its diff through **ai-diff-review**. Edge cases you noticed but did not chase go to **edge-case-tests**. Log the agent's wrong guesses and your corrections for **ownership-writeup**.
