---
name: tdd-implement
description: Write the code for a bugfix or feature test-first, one small reviewable step at a time - a failing behavior-named test, the smallest change that turns it green, then a refactor. Designs in order - data model, data flow, boundaries, pure functional core, side effects isolated at the edges, edge cases and errors, clean code (DRY, KISS, pragmatic SOLID) - keeping public signatures, conventions and existing tests intact, with no new dependencies. Use it whenever the user wants code written or changed, even without saying TDD - "implement this", "fix this bug", "build the feature", "write the code for step 2 of the plan", "make the failing tests pass", "add this method", "just hack it in", or the implement step of a timed assessment (Coderbyte AI fluency, take-home). C#/.NET first; also Java, Python, TypeScript, Go. Has paste-ready in-IDE agent prompts. Not for planning only (task-recon), judging a finished diff (ai-diff-review) or hardening tests on finished code (edge-case-tests).
---

# TDD Implement

This skill writes production code. It turns one step of a plan, or one bug, into a small diff backed by a test that was red before the change and green after it.

Test-first matters more under time pressure, not less. A test written first pins the behavior before the code can bias it, proves the change did something (a test that never failed proves nothing), and leaves a submittable state after every step, which is what partial credit rewards. Small steps keep each diff readable in one glance, and in an AI-fluency assessment every diff is an approval in a log that graders read.

It sits in the chain task-recon, **tdd-implement**, ai-diff-review, edge-case-tests, ownership-writeup, and works the same on any bugfix or feature in a real repo.

## Gather the inputs

- **The step**: one row of the task-recon plan, or the bug report, or the user's request. If there is no plan and the request holds more than one behavior, list the behaviors and take them one at a time rather than writing a plan document.
- **The contract**: task text, README, existing tests, the signatures already in the code. Hidden tests compile against today's public API, so the signature you were given is part of the requirement.
- **The conventions**: open the file you will change, one neighboring file and one existing test. Copy naming, error handling, null handling, test framework and assertion style. Code that looks like the repo is code a reviewer trusts.
- **The test command**, and the command to run a single test (see `references/testing-patterns.md`).

Run the whole suite once before touching anything. The baseline tells you what was already red, so a failure you did not cause is not mistaken for one you did, and so you notice if a test you never touched turns red later.

## Ground rules, and why

- **Keep public signatures, file layout and conventions.** Callers and hidden tests depend on them. Add a new private or internal function freely; change a public one only when the task says so.
- **One behavior per step, one step per diff.** A reviewer can verify ten lines and only skim a hundred. If a step grows past what fits on one screen, split it.
- **Fix the code, not the tests.** An existing test is a statement of the contract. Changing an assertion to match the code hides the bug and, in an assessment, reads as cheating. If you believe a test is wrong, stop and say so with the reason; do not edit it silently.
- **No new dependencies without asking.** A new package can break the build the grader runs and widens the review. The standard library almost always suffices.
- **Root cause before fix.** For a bug, reproduce it with a failing test first, then find the line that makes it happen. A fix that special-cases the reported input passes the visible test and fails the hidden ones next to it.

## Design, in this order

Work through these before and during the first red test. Each answer is a sentence or two, not a document. `references/design-checklist.md` has the questions, patterns and smells for each; read it when the change touches state, IO or error handling you have not mapped yet.

1. **Data model.** Which types carry the data, which fields matter for this step, what is optional. Reuse the existing types; a new type is justified only when it removes a primitive tangle (three parallel lists, a magic tuple).
2. **Data flow.** Input to output in one line: `repository -> assets -> pick the ones to change (pure) -> save each`. If the line has more than four arrows, the step is too big.
3. **Boundaries.** Name every place the code touches the outside world: repository or database, file system, network, clock, randomness, environment, logging. Those are the only places a test double is allowed, and the only places mutation should happen.
4. **Pure core.** Put the decision logic in a function that takes values and returns values, with no IO and no hidden state: `Rename(names, from, to) -> names`. Pure functions are tested with plain unit tests, no setup, and hidden edge-case tests hit them hardest.
5. **Mutation at the edges.** The method with the given signature is the thin imperative shell: load, call the pure core, save, return. Do not mutate input collections inside the core; return new values. The repository save is the one write, and it is easy to find in review.
6. **Edge cases and errors.** Guard clauses at the top of the shell for invalid arguments, using the exception type or error style the repo already uses. Empty, null, duplicates, letter case, boundaries, already-applied state: decide each one, test the ones the task names or implies.
7. **Clean code.** In the refactor step: clear names, no duplication that has appeared three times, short functions, early returns. SOLID only where it pays (see the next section).

## When not to over-engineer

KISS beats SOLID ceremony in an 80-minute task and in most bugfixes. Extra layers cost review time, give hidden tests more surface to break, and signal poor judgment to a grader as clearly as a missing test does.

- No interface with one implementation unless a test needs the seam (a real boundary like a repository, clock or HTTP client). A pure function needs no seam at all.
- No strategy, factory, builder or plugin mechanism for a single `if`. Two branches are a conditional.
- No new project, folder or file for a ten-line helper; put it next to its only caller, as a private method or a small static class in the same file, unless the repo clearly does otherwise.
- No configuration for a value that is a constant in the task. A named constant is enough.
- No generic helper "for later". Extract on the third duplicate, not the first.
- Do not rewrite code the step does not need. Adjacent cleanup belongs in a separate, later diff.

A "quick hack" request changes the size of the diff, not the discipline. Keep the change small and local, and still write the one failing test first: it takes two minutes and it is the only proof the hack works.

## The loop, per behavior

Repeat for each behavior in the step until the step's "done when" holds.

1. **Name the behavior** as one sentence a test name can carry: `archives_assets_that_expire_today`, `returns_an_empty_page_past_the_last_one`. Names state behavior, never a ticket, task or spec ID.
2. **Red.** Write one test for that behavior. Pure logic gets a unit test; behavior that crosses a boundary gets one integration test through the public method with the real in-memory or fake boundary the repo already has. Run it and read the failure. It must fail for the right reason: a wrong value or the expected `NotImplementedException`, not a typo or a missing import. In compiled languages, add the empty method or stub first so the red is an assertion, not a compile error.
3. **Green.** Write the smallest production change that makes this test pass. Hard-code nothing that only satisfies this one input. Run the test, then the whole suite; every previously green test stays green.
4. **Refactor while green.** Extract the pure core, rename, remove the duplication you just created. Behavior does not change, so no test changes. Run the suite again.
5. **Note it**: test name, why it was red, green. This line is what the output reports and what ownership-writeup later turns into evidence.

Two to five behaviors usually cover a step: the main path, one boundary, one invalid input, one "already in that state" case. Leave the exhaustive edge-case table to edge-case-tests.

### Bugs

1. Reproduce: write a test named by the correct behavior, using the smallest input that shows the bug, and watch it fail with the reported symptom. If an existing test already fails for this bug, run it, then still add the smallest neighboring case that exposes the cause (the lowest input that hits the same rule, not only the value in the report).
2. Locate the cause: the line and the reason, in one or two sentences. Check whether the same cause breaks other inputs; the fix should cover all of them.
3. Fix the cause, run the suite, and keep every existing test and assertion unchanged.

If a test cannot be run (no SDK, no permission, IDE agent without a terminal), say "not run" and why, trace the test through the code by hand, and ask the user to run the command. Never report a test as passing without a run.

## Output

Report each completed step with this template:

```markdown
## Step <n>: <behavior in one line>

**Changed**
- <path>: <what changed, one line>

**Tests (red -> green)**
| Test | Kind | Red because | Now |
|---|---|---|---|
| <behavior name> | unit / integration | <assertion or exception seen before the change> | green |

**Suite:** `<command>` -> <n> passed, <n> failed (<baseline: what was red before, if anything>)

**Design notes**
- Model: <types used or added, and why>
- Flow: <input -> ... -> output>
- Boundary: <repository / IO / clock touched, and the double used in tests>
- Mutation lives in: <the one method that writes>

**Edge cases handled:** <case -> behavior, ...>
**Open risks:** <assumptions, cases not covered, anything not run>
**Next:** review this diff with ai-diff-review, then <next step or edge-case-tests>
```

## Worked example

Task: add `AssetService.ArchiveExpired(DateOnly today)`, which archives every asset whose `ExpiresOn` is on or before `today` and returns how many it archived. xUnit, an `InMemoryAssetRepository` already exists, `Asset` is a record.

Design: model is the existing `Asset(Guid Id, string Name, DateOnly? ExpiresOn, bool IsArchived)`. Flow: `repository -> assets -> keep expired and not archived (pure) -> save archived copy -> count`. Boundaries: the repository, and the clock, which the signature already turns into a `today` parameter, so no clock abstraction is needed.

Red 1, unit test on the pure rule, with a stub `IsExpired` that returns `false`:

```csharp
[Theory]
[InlineData(2026, 3, 1, true)]
[InlineData(2026, 3, 2, false)]
public void Asset_expires_on_its_expiry_date_and_not_before(int year, int month, int day, bool expected)
{
    var asset = new Asset(Guid.NewGuid(), "a.png", new DateOnly(year, month, day), false);

    Assert.Equal(expected, Expiry.IsExpired(asset, new DateOnly(2026, 3, 1)));
}
```

Green 1:

```csharp
public static class Expiry
{
    public static bool IsExpired(Asset asset, DateOnly today) =>
        asset.ExpiresOn is { } expiresOn && expiresOn <= today;
}
```

Red 2, integration test through the public method and the real in-memory repository; it fails with `NotImplementedException`:

```csharp
[Fact]
public void Archives_only_expired_assets_and_returns_the_count()
{
    var repository = new InMemoryAssetRepository(
        new Asset(Guid.NewGuid(), "old.png", new DateOnly(2026, 1, 1), false),
        new Asset(Guid.NewGuid(), "new.png", new DateOnly(2026, 12, 1), false),
        new Asset(Guid.NewGuid(), "forever.png", null, false));

    var archived = new AssetService(repository).ArchiveExpired(new DateOnly(2026, 3, 1));

    Assert.Equal(1, archived);
    Assert.Equal(new[] { "old.png" }, repository.GetAll().Where(a => a.IsArchived).Select(a => a.Name));
}
```

Green 2, the thin shell that owns the only write:

```csharp
public int ArchiveExpired(DateOnly today)
{
    var expired = _repository.GetAll()
        .Where(asset => !asset.IsArchived && Expiry.IsExpired(asset, today))
        .ToList();

    foreach (var asset in expired)
    {
        _repository.Save(asset with { IsArchived = true });
    }

    return expired.Count;
}
```

Refactor: nothing to extract; `Expiry` stays a static class in the service's file because it has one caller. No `IClock`, no `IExpiryPolicy`: the date is already a parameter and the rule is one line. Edge cases handled: no expiry date never expires, an already archived asset is not counted again. Open risk: the task does not say whether `today` is in UTC; the caller decides.

## In-IDE prompt templates

Coderbyte's in-IDE agent, and most agents outside Claude Code, only edit and create files, ask approval for every edit, and do not have this skill. Paste these, filled in. Approve the test edit, run the tests yourself and see red before approving any production edit: the log then shows a test that failed before the fix.

Feature step:

```
Step <n> only: <behavior in one sentence>.
1. Do not change production code yet. Add one failing test to <test file>,
   named after the behavior, in the same framework and style as the existing
   tests. Show it and say what failure you expect.
2. After I confirm it is red, make the smallest change in <source file> that
   makes it pass. Keep public signatures, existing tests and style unchanged.
   No new packages.
3. Put the decision logic in a pure function (values in, value out, no
   repository, IO or clock calls) and keep loading and saving in <method>.
4. No new interfaces, classes or files unless a test needs the seam.
5. End with: edge cases this handles, edge cases it does not.
```

Bugfix:

```
Bug: <symptom, with the exact input and output>.
1. Before changing any code, add a failing test to <test file> that reproduces
   it with the smallest input, named by the correct behavior.
2. Explain the root cause in two sentences, naming the line, and say which
   other inputs the same cause breaks.
3. Then fix the cause in <source file>, not the reported input. Do not edit,
   skip or loosen any existing test or assertion.
```

Refactor after green:

```
Tests are green. Refactor <file> only: <extract the pure function / rename /
remove duplication>. No behavior change, no test changes, public signatures
unchanged. Show the diff and confirm which tests cover the moved code.
```

When the agent's edit touches a test assertion, a signature or a package file the step did not ask for, reject it with the reason; a visible rejection is evidence of judgment.

## Hand-off

After each step, review the diff with **ai-diff-review** before starting the next one, so a wrong turn costs one step, not three. When the plan's steps are done, run **edge-case-tests** on the functions you touched; the edge cases and open risks from your step reports are its starting list. Keep the test names, red reasons and any rejected agent edits: **ownership-writeup** turns them into the README and the written answers.

## Reference files

- `references/design-checklist.md`: questions and patterns for data model, flow, boundaries, pure core, mutation isolation, error handling per language, clean-code smells with fixes, SOLID applied pragmatically, over-engineering smells. Read when the step touches state, IO or errors, or before the refactor.
- `references/testing-patterns.md`: behavior naming, unit versus integration, test doubles only at boundaries, red for the right reason, and per-framework snippets and single-test commands for xUnit, NUnit, JUnit 5, pytest, Jest, Vitest and Go. Read when writing the first test in a framework, or when unsure whether something needs a unit or an integration test.
