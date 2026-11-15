Recon below. I haven't seen the code either, so every default that depends on code you haven't read says what to check and when to switch. Nothing here is code. Paste the recon prompt first, compare the agent's answer with these tables, then send the step 1 prompt.

## Goal
There are two deliverables: (1) make `RenditionService.GetBestRendition` never return a rendition wider than `maxWidth`, and (2) add `TagService.MergeTags(sourceTag, targetTag)`, which moves every asset from source to target and then deletes source, matching tag names case-insensitively.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | Return the largest rendition with `Width <= maxWidth` | explicit | widths [200, 800, 400] with maxWidth 500 returns 400 |
| R2 | The boundary counts: `Width == maxWidth` qualifies | explicit (`<=`) + visible `ReturnsExactWidthMatch` | the visible test stays green |
| R3 | Return `null` when the asset has no renditions | implied (visible test) | `ReturnsNullWhenAssetHasNoRenditions` stays green |
| R4 | The result never depends on the order renditions are stored in | implied (the "sometimes" in the bug report) | the same result for ascending, descending and shuffled lists |
| R5 | After `MergeTags`, every asset that had sourceTag has targetTag | explicit | 2 assets tagged source both end with target |
| R6 | sourceTag is deleted: gone from the tag store and from every asset | explicit | source cannot be looked up and no asset carries it |
| R7 | Tag names match case-insensitively in `MergeTags` | explicit | `MergeTags("cats", "ANIMALS")` works on the stored names "Cats" and "Animals" |
| R8 | No duplicate target on an asset that already had both tags | implied (`AddTagIgnoresDuplicateTag`) | that asset carries target exactly once |
| R9 | The existing public API and the 4 visible tests are unchanged | implied (hidden tests compile against today's API) | `dotnet test` is green and no signature changed |

## Assumptions
- A1: What causes the bug? -> Default fix: filter `Width <= maxWidth`, then take the max by `Width`, without sorting the stored list in place. The likely causes are `First()`/`Last()` on an unsorted list, `>=` instead of `<=`, sorting ascending instead of descending, picking before filtering, or comparing `Height`. Have the agent name the actual line before it changes anything.
- A2: Every rendition is wider than maxWidth? -> Return `null`. Returning the smallest one would break "never wider than maxWidth", and `null` matches the existing no-renditions behaviour.
- A3: The assetId is unknown? -> Return `null`. Switch to `KeyNotFoundException` only if `GetBestRendition` or the repository already throws for a missing asset.
- A4: Two renditions have the same width? -> Return the first one in stored order. `MaxBy` does this and it is deterministic.
- A5: maxWidth is 0 or negative? -> Return `null` without throwing. Switch to `ArgumentOutOfRangeException` only if the service already validates its arguments.
- A6: sourceTag does not exist? -> No-op, no throw. `AddTagIgnoresDuplicateTag` shows a lenient, idempotent service style. Switch to `KeyNotFoundException` only if TagService already throws for missing tags (for example in a Remove or Get method).
- A7: targetTag does not exist? -> Create it with the casing passed in, then retag. "Must end up tagged with targetTag" requires it to exist.
- A8: Source and target are equal ignoring case ("Cats" -> "cats")? -> No-op that keeps the tag. Deleting the source here would delete the target too and untag every asset.
- A9: Which casing survives? -> The stored name of the existing target tag. The caller's casing is used only when A7 creates the tag.
- A10: Null, empty or whitespace names? -> Throw `ArgumentException` for all three. xUnit `Assert.Throws<T>` checks the exact type, so `ArgumentNullException` would fail a test that expects `ArgumentException`. Switch only if `AddTag` already validates with a different type: copy what it does.
- A11: How are tags stored on assets (as strings, as `Tag` objects or as tag ids)? -> Compare names with `StringComparison.OrdinalIgnoreCase` and remap whatever the asset holds. Go only through `IAssetRepository` and never cast to `InMemoryAssetRepository`.
- A12: The repository lacks a method that MergeTags needs? -> Use the existing members first. Adding a member to `IAssetRepository` breaks any hidden-test fake that implements it, so add one only if there is no other way, and implement it in `InMemoryAssetRepository`.

## Files
| File | Action | Why |
|---|---|---|
| src/AssetLibrary/Services/RenditionService.cs | change | Task 1 |
| src/AssetLibrary/Services/TagService.cs | change | Task 2 |
| src/AssetLibrary/Models/Rendition.cs, Asset.cs, Tag.cs | read | Width type, how tags hang off assets (A11) |
| src/AssetLibrary/Repositories/IAssetRepository.cs | read, leave alone | hidden tests may implement it (A12) |
| src/AssetLibrary/Repositories/InMemoryAssetRepository.cs | read; change only per A12 | is persisting needed after you mutate an asset? does it throw for missing ids? |
| tests/AssetLibrary.Tests/RenditionServiceTests.cs, TagServiceTests.cs | read, leave alone | they show the shape of the hidden tests (arrange style, how the repo is built) |
| tests/AssetLibrary.Tests/RenditionServiceBestFitTests.cs | create | your new tests stay separate from the given file |
| tests/AssetLibrary.Tests/TagServiceMergeTagsTests.cs | create | same reason |
| README.md | read, then append a short "Assumptions and notes" section | the graded write-up |
| AssetLibrary.sln, both .csproj | leave alone | add no packages |

Test command: `dotnet test` from the repo root (`dotnet test --filter FullyQualifiedName~TagService` while iterating).

## Edge cases
| Case | Expected (chosen behaviour) | Req | Likely hidden test |
|---|---|---|---|
| widths [200, 800, 400], maxWidth 500 | the 400 rendition | R1, R4 | yes |
| widths [200, 400, 800] and [800, 400, 200], maxWidth 500 | 400 both times | R4 | yes |
| width exactly 500, maxWidth 500 | that rendition | R2 | yes (visible) |
| every width greater than maxWidth ([800, 1200], max 500) | `null` (A2: never return a wider one) | R1 | yes |
| no renditions | `null` | R3 | yes (visible) |
| two renditions at width 400, max 500 | the first one in stored order (A4) | R1 | maybe |
| maxWidth 0 or negative | `null`, no throw (A5) | R1 | maybe |
| unknown assetId | `null` (A3) | R3 | maybe |
| the asset's rendition list after the call | same order, not sorted in place | R4 | maybe |
| A{Cats} and B{Cats, Animals}; `MergeTags("Cats", "Animals")` | A{Animals}, B{Animals} once each, and the "Cats" tag no longer exists | R5, R6, R8 | yes |
| `MergeTags("cats", "ANIMALS")` on the stored "Cats" and "Animals" | same result as the previous row; target keeps its stored name "Animals" (A9) | R7 | yes |
| target "Pets" does not exist | "Pets" created; former Cats assets tagged Pets; Cats deleted (A7) | R5 | yes |
| `MergeTags("Cats", "cats")` | no-op; tag and assets unchanged (A8) | R6, R7 | yes |
| source "Dogs" does not exist | no-op, no throw (A6) | R6 | maybe |
| source exists but no asset carries it | source deleted, other assets untouched | R6 | maybe |
| asset with neither tag | unchanged | R5 | maybe |
| null, "" or "  " for either name | `ArgumentException` (A10) | R5 | maybe |
| retagging while iterating the assets | iterate over `.ToList()`, so no `InvalidOperationException` | R5 | indirectly |
| `AddTag("cats")` when "Cats" exists | ignored as a duplicate (the task says tag names are case-insensitive) | R7 | maybe |

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Recon prompt; correct the agent's reading against these tables | 8 | the agent's plan matches A1 to A12 |
| 1 | Task 1: failing tests first (unsorted list, all wider), then the `GetBestRendition` fix | 10 | the new tests and both visible rendition tests pass |
| 2 | Task 2 core: `MergeTags` retags case-insensitively, dedupes and deletes source | 13 | the first two merge rows pass and the visible tag tests stay green |
| 3 | Task 2 edges: same tag (A8), missing source (A6), missing target (A7), argument checks (A10) | 7 | those rows pass |
| 4 | README notes: A1 to A12 as chosen, the cause of the bug, what was cut | 5 | written |
| 5 | Full `dotnet test`, diff read end to end, no stray files | 5 | green, `git status` shows only the intended files |
| 6 | Optional: make the `AddTag` duplicate check `OrdinalIgnoreCase` if it is case-sensitive; add tests for ties, maxWidth <= 0 and the unknown asset | 5 | rows pass |
| - | Submit Section 1 | 2 | |

Budget: 55 of 80 min for Section 1, review time included, which leaves 25 min for the written Section 2. Cut line: below step 5. Step 6 (the `AddTag` case alignment and the extra tests) goes first. Step 4 stays above the line because the README is graded. If step 2 overruns past minute 35, skip step 3's argument checks before you skip the README.

## Recon prompt (paste first)
Paste this with both task statements and the file tree attached:

```
Do not edit or create any files yet. First read README.md, both services, the three models,
both repository files and both test files. Then:
1. Restate each of the two tasks in one sentence.
2. List requirements as a numbered checklist, each marked explicit or implied,
   each with how a test would check it.
3. List assumptions for anything ambiguous, each with the default you pick and why.
   Cover at least: no rendition fits maxWidth, unknown assetId, MergeTags when source is
   missing, when target is missing, and when source equals target ignoring case.
4. List the files to read, change and leave alone.
5. List edge cases, each with the expected behaviour you chose, marking the ones
   hidden tests most likely check.
6. Name the exact line or expression in GetBestRendition that can return a rendition
   wider than maxWidth, and why it happens only "sometimes".
7. Propose a step plan where each step changes one function or fixes one bug,
   with minutes per step for a 47-minute limit and a cut line.
Respect existing signatures and style; no new dependencies; do not change IAssetRepository
unless there is no other way.
```

Check its answer against A2, A6, A8 and A10 in particular. If it proposes "return the smallest rendition when none fit", or deleting the tag when source equals target, correct it in one sentence that gives the reason. That correction is the judgement the graders look for.

## Step 1 prompt (first implementation prompt)
```
Step 1 only: fix RenditionService.GetBestRendition. Read src/AssetLibrary/Services/RenditionService.cs,
src/AssetLibrary/Models/Rendition.cs and tests/AssetLibrary.Tests/RenditionServiceTests.cs.
First explain in one or two sentences what makes it return a rendition wider than maxWidth and
why only sometimes. Then create tests/AssetLibrary.Tests/RenditionServiceBestFitTests.cs, following
the arrange style of the existing tests, with these failing tests:
- widths [200, 800, 400], maxWidth 500 -> the 400 rendition
- widths [200, 400, 800] and [800, 400, 200], maxWidth 500 -> 400 both times
- widths [800, 1200], maxWidth 500 -> null
Then fix the method: keep only renditions with Width <= maxWidth (equal counts), return the one
with the largest Width, the first in stored order on a tie, and null when none qualify. Do not sort
or modify the asset's rendition list in place.
Constraints: keep the public signature and return type, no new packages, do not edit
RenditionServiceTests.cs, do not touch TagService or the repositories. Run dotnet test and show
that the two existing rendition tests still pass.
```

Defaults most worth checking once you see the code: A6 (missing source is a no-op rather than a throw) and A8 (merging a tag into itself is a no-op). The hidden tests are most likely to disagree with these two. One question: does the task page or README say anything about `MergeTags` when sourceTag doesn't exist, or which exception type the services use? If it does, switch A6 and A10 to match. Otherwise confirm this plan and send the recon prompt. No code until the agent's plan matches these tables.
