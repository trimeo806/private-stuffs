---
name: tdd-implement
description: Write the code for a bugfix or feature test-first, one small reviewable step at a time - a failing behavior-named test, the smallest change that turns it green, then a refactor. Designs data model, flow, boundaries and a pure core with side effects at the edges, then clean code (DRY, KISS, pragmatic SOLID); keeps signatures and existing tests intact, adds no dependencies. Use whenever the user wants code written or changed, even without saying TDD - "implement this", "fix this bug", "build the feature", "write the code for step 2", "make the failing tests pass", "add this method", "just hack it in", or the implement step of a timed assessment (Coderbyte AI fluency, take-home). C#/.NET first; also Java, Python, TypeScript, Go. Has paste-ready in-IDE agent prompts. Not for planning only (task-recon), judging a diff (ai-diff-review) or hardening tests on finished code (edge-case-tests).
---

# TDD Implement

Turn one plan step or bug into a small diff backed by a test that was red before the change and green after. A test that never failed proves nothing, and every green step is submittable.

## Inputs

- **The step**: a plan row, bug or request; several behaviours are taken one at a time.
- **The contract**: task text, README, existing tests, current signatures (hidden tests compile against them).
- **Conventions**: read the file you will change, a neighbour and one existing test; copy their naming, error handling and test style.
- **Commands** for the suite and one test. Read `references/testing-patterns.md` when writing the first test in a framework.

Run the whole suite once first, so a pre-existing failure is not mistaken for yours.

## Ground rules

- Keep public signatures, layout and conventions. One behaviour per step, one step per diff.
- **Fix the code, not the tests.** Never edit, skip or loosen an existing assertion. If a test looks wrong, stop and say why.
- **No new dependencies** without asking.
- **Bugs: reproduce, then root cause.** Special-casing the reported input passes the visible test and fails the hidden ones beside it.

## Design, in this order

A sentence each; `references/design-checklist.md` has the questions when state, IO or errors are involved.

1. **Data model**: existing types and the fields this step needs; a new type only to remove a primitive tangle.
2. **Data flow** in one line: `repository -> items -> select changes (pure) -> save each`. Over four arrows: split the step.
3. **Boundaries**: repository, files, network, clock, randomness, environment. Only these get test doubles or mutate.
4. **Pure core**: decision logic as values in, new values out; no IO, no mutating inputs.
5. **Mutation at the edges**: the given method is a thin shell: guard, load, call the core, save what changed, return.
6. **Edge cases and errors**: guard clauses in the repo's error style; decide empty, null, duplicates, case, boundaries, already-applied state.
7. **Clean code** in the refactor: clear names, early returns, DRY on the third duplicate, SOLID only where it pays.

## Do not over-engineer

A quick hack shrinks the diff, not the discipline: the failing test still comes first.

- No interface with one implementation unless a test needs a seam at a real boundary.
- No strategy, factory or builder for one `if`; no config or environment variable for a fixed value (use a named constant).
- No new file or folder for a small helper; keep it beside its caller.
- No helper "for later", no rewriting code the step does not need.

## The loop, per behaviour

1. **Name it** as a behaviour sentence: `archives_assets_that_expire_today`. Never a ticket, step or spec ID.
2. **Red.** One test. Pure logic: a unit test calling the pure function directly. Boundary-crossing behaviour: an integration test through the public method with the repo's in-memory or fake boundary. Run it; it must fail on a wrong value or the stub's not-implemented exception, not a compile error (stub first in compiled languages).
3. **Green.** Smallest change that passes; hard-code nothing for this one input. Run the test, then the suite.
4. **Refactor while green.** Extract the core, rename, dedupe; no test changes; rerun.
5. **Note**: test name, why red, now green.

Two to five behaviours cover a step; exhaustive tables belong to edge-case-tests.

**Bugs:** (1) run or write a failing test named by the correct behaviour and see the symptom, then add the smallest neighbouring input that hits the same rule and see it fail; (2) name the line, the reason and the other inputs it breaks; (3) fix the cause, keeping every existing test unchanged.

Cannot run tests? Say "not run" and why, trace by hand, give the command. Never report an unrun pass.

`references/examples.md` shows one complete C# step; read it when unsure what a step looks like.

## Output

```markdown
## Step <n>: <behaviour>

**Changed**
- <path>: <one line>

**Tests (red -> green)**
| Test | Kind | Red because | Now |
|---|---|---|---|
| <behaviour name> | unit / integration | <failure seen before the change> | green |

**Suite:** `<command>` -> <n> passed, <n> failed (<baseline red, if any>)

**Design notes**
- Model / Flow / Boundary and test double / Mutation lives in: <one line each>

**Edge cases handled:** <case -> behaviour>
**Root cause (bugs):** <line and reason>
**Open risks:** <assumptions, gaps, anything not run>
**Next:** ai-diff-review on this diff, then <next step or edge-case-tests>
```

## In-IDE prompt templates

In-IDE agents (Coderbyte's included) lack this skill and ask approval per edit. Paste these filled in; see the test red yourself before approving any production edit, so the log shows it.

Feature step:

```
Step <n> only: <behaviour in one sentence>.
1. Do not change production code yet. Add one failing test to <test file>,
   named after the behaviour, in the existing framework and style. Show it
   and say what failure you expect.
2. After I confirm it is red, make the smallest change in <source file> that
   makes it pass. Keep public signatures, existing tests and style unchanged.
   No new packages.
3. Put the decision logic in a pure function (values in, value out, no
   repository, IO or clock) and keep loading and saving in <method>.
4. No new interfaces, classes or files unless a test needs the seam.
5. End with: edge cases this handles, edge cases it does not.
```

Bugfix:

```
Bug: <symptom, with the exact input and output>.
1. Before changing any code, add a failing test to <test file> that reproduces
   it with the smallest input, named by the correct behaviour.
2. Explain the root cause in two sentences, naming the line, and which other
   inputs the same cause breaks.
3. Then fix the cause in <source file>, not the reported input. Do not edit,
   skip or loosen any existing test or assertion.
```

Refactor after green:

```
Tests are green. Refactor <file> only: <extract the pure function / rename /
remove duplication>. No behaviour or test changes, public signatures
unchanged. Show the diff and which tests cover the moved code.
```

Reject, with a reason, agent edits to assertions, signatures or package files the step did not ask for.

## Hand-off

Run **ai-diff-review** on each step before the next. When the plan is done, run **edge-case-tests** on the touched functions, starting from your edge cases and open risks. Keep test names, red reasons and rejected edits for **ownership-writeup**.
