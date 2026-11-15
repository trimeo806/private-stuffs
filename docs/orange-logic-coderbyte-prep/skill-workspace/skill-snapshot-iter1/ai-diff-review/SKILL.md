---
name: ai-diff-review
description: Review AI-generated code, a diff or a PR before accepting it. Checks requirement fit, correctness, edge cases, hallucinated APIs, signature and contract changes, complexity, security, consistency with the codebase and tests, including an agent that edited a test to make it pass, then returns a verdict of ACCEPT, ACCEPT WITH FIX or REJECT, a findings table graded blocker, major or minor with file:line, and a one-line feedback prompt to paste back to the agent. Use it whenever code came from Copilot, Cursor, Claude, ChatGPT or an in-IDE coding agent and the user is deciding whether to keep it, even if they never say review, for example "the agent wrote this, looks fine?", "should I approve this edit", "can I accept this change", "it says all tests pass, are we done?", "sanity-check this patch", "what is wrong with this AI fix", or a pasted diff right after an implement step. Also use it in timed AI-fluency coding assessments such as Coderbyte, where the accept and reject log is graded.
---

# AI Diff Review

AI-written diffs are dangerous in a specific way: they compile, read well, and often pass the very tests they touched. Their mistakes cluster in a few places: doing more or less than asked, skipping edge cases, fixing the symptom in the wrong layer, quietly changing a contract, and editing tests until they go green. This skill turns a diff into a decision (keep it, patch it, or send it back) plus a reason the user can paste straight back to the agent.

In an AI-fluency assessment the decision matters as much as the code. Graders read the prompt log and what was accepted or rejected, so a rejection that names a concrete breaking input is evidence of judgment, and a rubber-stamped bad edit is evidence of the opposite.

It sits in the chain task-recon, tdd-implement, ai-diff-review, edge-case-tests, ownership-writeup, but it works on any AI-generated change or PR.

## Gather the inputs

- **The diff or generated code**, plus whatever the agent claimed about it ("fixed it, all tests pass"). The claim is something to verify, not evidence.
- **The requirement**: task text, ticket, README, or the task-recon checklist. If there is none, infer it from failing test names or the user's words and state the inference in the review, because a review against the wrong requirement is worthless.
- **Surrounding code, only where the diff reaches it**: callers of any changed signature, the test file for the touched code, and how data is stored and loaded when the diff touches persistence.

Read the whole diff before judging any single line. The trap is often in the second file: a test assertion, a save path, a config flag.

## Review steps

1. **Restate the requirement in one line** and list 3 to 6 edge cases, taken from recon or derived now: empty, null, single item, duplicates, letter case and culture, boundary and very large values, data that already exists.
2. **Sort the hunks** into production code, test code, and config or dependencies. For every test hunk, decide whether it adds coverage or changes an expectation. A changed assert value, a removed assertion, a skip attribute, a loosened matcher or a deleted test is presumed to be the agent making the suite agree with its bug, unless the requirement itself changed that behavior.
3. **Run the checks in order.** The order is deliberate: once correctness fails, style findings are noise.

| # | Check | Look for | Why it matters |
|---|---|---|---|
| 1 | Requirement fit | Does exactly what was asked; no extra features, no partial fix | Scope creep adds untested surface; partial fixes fail hidden tests |
| 2 | Correctness | Off-by-one, wrong condition, null or empty, overflow, float compare, mutated input, comparator bugs | Wrong results are the only thing that always loses points |
| 3 | Edge cases | Trace each listed edge case through the new code | Hidden tests are mostly edge cases |
| 4 | Hallucination | Methods, overloads, packages or config that do not exist in this language version or repo | Code that does not compile or silently resolves to a different overload |
| 5 | Contract | Changed signatures, renamed public symbols, changed output format, changed stored data | Breaks callers and existing data the diff never shows |
| 6 | Complexity | O(n²) where O(n) or O(n log n) is easy; repeated enumeration or queries in loops | Large hidden inputs time out |
| 7 | Security | Injection, unchecked input, secrets, unsafe deserialization, path traversal | One finding here outranks style entirely |
| 8 | Consistency | Naming, error handling, logging and test style of the repo; no new dependency | Reviewers and graders judge fit with the codebase |
| 9 | Tests | New tests assert real behavior; no test edited to match the bug; the breaking inputs are covered | Tests are the proof; edited tests are the most common AI cheat |

4. **Trace, do not guess.** For each edge case write `input -> actual output` against the new code, and run the tests yourself when the environment allows it. A finding without a concrete breaking input is a hunch: put it under "Not verified", not in the findings table.
5. **Pick the verdict** using the rules below, then **write the feedback prompt**.

Read `references/ai-traps.md` when the diff is in C#, Java, Python or JavaScript/TypeScript, or touches string comparison or sorting, file names and extensions, numeric sums, dates, or collections. It lists the library behaviors that AI agents get wrong most often, with the correct alternative.

Read `references/in-ide-prompts.md` when the user is working with an in-IDE agent (Coderbyte, Copilot Chat, Cursor) and needs more prompts to paste than the two below, for example a guard against test edits or an accept-with-fix follow-up.

## Severity and verdict

- **blocker**: wrong result, crash, data corruption, security hole, or a test edited to hide a bug.
- **major**: missed edge case, contract or signature change, complexity that fails at realistic size, a fix applied in the wrong layer that still happens to work.
- **minor**: naming, style, small readability or consistency issues.

Verdict:

- **REJECT** when there is any blocker, or when the approach itself is wrong (fix in the wrong layer, tests changed to pass), even if the output looks right today. Accepting and patching keeps the wrong design in the codebase and in the log.
- **ACCEPT WITH FIX** when there is no blocker but at least one major, or a minor worth fixing, that a small manual edit or one follow-up prompt resolves.
- **ACCEPT** when there is no blocker or major. List minors as optional; they do not downgrade the verdict.

## When the diff is fine

Say so plainly. Inventing problems to look rigorous wastes time and, in an assessment, reads as poor judgment, which is exactly what is being graded. For a clean diff, give ACCEPT, an empty findings table or optional minors only, and fill "Edge cases traced" and "Checks passed" so the acceptance is visibly earned rather than rubber-stamped.

## Output format

Use this template every time, so the verdict and the paste-back prompt are always in the same place:

```markdown
**Requirement:** <one line>
**Verdict:** ACCEPT | ACCEPT WITH FIX | REJECT - <one-sentence reason>

| # | Severity | Where | Problem | Breaking input: actual vs expected | Fix |
|---|---|---|---|---|---|
| 1 | blocker | path/File.cs:42 | ... | ... | ... |

**Edge cases traced:** <input> -> <output> (ok / wrong), ...
**Checks passed:** <check names from the table>
**Not verified:** <needs a test run, missing context, or "nothing">

**Feedback prompt for the agent:**
> <one line>
```

`Where` is `file:line` using the new-file line from the hunk header; when line numbers are not available, use `file` plus the method name. With no findings, write a single row `| - | - | - | none | - | - |`.

## Writing the feedback prompt

One line, written to the agent, that it cannot misread:

- what is wrong and the input that proves it (`"A1B2" vs "a1b2" returns false`),
- the expected behavior,
- the scope: which function to change, and what to leave alone (signature, output format, existing tests),
- a test for the breaking input.

For ACCEPT, write `none, accept as is`. For ACCEPT WITH FIX, prefer making a fix of about three lines or fewer by hand and say so in the prompt line: a manual edit shows ownership in the log and costs less than another agent round trip.

## In-IDE prompt templates

Coderbyte's in-IDE agent can only edit and create files, every edit needs approval, and it has no skills, so the user pastes these. Review each proposed edit as it comes, not only the final state: every approval is a logged decision.

Before reviewing, ask the agent to review itself:

```
Review your own change against the requirement: list any input that breaks it
(empty, null, duplicates, negative, very large, different letter case), any API
you used that may not exist in <language/version>, any signature, output-format
or stored-data change, and any test you modified. Do not rewrite yet, list issues first.
```

When rejecting:

```
Rejected: <problem> because <input> gives <actual> instead of <expected>.
Fix only <function>, keep the signature, output format and existing tests
unchanged, and add a test for <input>.
```

## Worked example

Requirement: `FilterByExtension(assets, ext)` returns assets whose file extension equals `ext`, ignoring case; callers pass `"jpg"`.

Agent diff in `src/Dam.Core/Search/AssetFilter.cs`:

```csharp
public static IEnumerable<Asset> FilterByExtension(IEnumerable<Asset> assets, string ext)
{
    return assets.Where(a => Path.GetExtension(a.FileName) == "." + ext);
}
```

Review:

**Requirement:** return assets whose extension equals `ext`, case-insensitive.
**Verdict:** REJECT - extension compare is case-sensitive, so uppercase files are missed.

| # | Severity | Where | Problem | Breaking input: actual vs expected | Fix |
|---|---|---|---|---|---|
| 1 | blocker | src/Dam.Core/Search/AssetFilter.cs:14 | `==` is an ordinal, case-sensitive compare | `"photo.JPG"`, ext `"jpg"`: excluded vs included | `string.Equals(..., StringComparison.OrdinalIgnoreCase)` |
| 2 | major | src/Dam.Core/Search/AssetFilter.cs:14 | Assumes `ext` has no leading dot | ext `".jpg"`: compares to `"..jpg"`, nothing matches | `TrimStart('.')` on both sides |

**Edge cases traced:** `"a.jpg"` -> match (ok); `"a.JPG"` -> no match (wrong); `"noext"` -> `""`, no match (ok); ext `".jpg"` -> no match (wrong)
**Checks passed:** hallucination, contract, complexity, security
**Not verified:** null `FileName`; depends on whether the model allows it

**Feedback prompt for the agent:**
> Rejected: "photo.JPG" with ext "jpg" is excluded because the compare is case-sensitive; in FilterByExtension only, compare Path.GetExtension(name).TrimStart('.') to ext.TrimStart('.') with StringComparison.OrdinalIgnoreCase, keep the signature, and add tests for "photo.JPG" and ext ".jpg".

## Hand-off

Every breaking input found here becomes a test in edge-case-tests. Every rejection and manual fix, with its reason, is raw material for ownership-writeup, so keep a one-line note of each as you go.
