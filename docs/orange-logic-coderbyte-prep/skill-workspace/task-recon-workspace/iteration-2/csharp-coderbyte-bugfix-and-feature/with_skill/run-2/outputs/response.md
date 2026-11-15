Here's the recon. I haven't seen the code either, so every default that depends on a file you haven't opened says what to check and when to switch. Plan for Section 1 to take about 55 min and stop there, because Section 2 (written) needs at least 20 min and you can't go back.

## Goal
Make `GetBestRendition` never return a rendition wider than `maxWidth`, and add `TagService.MergeTags(sourceTag, targetTag)`. `MergeTags` moves every asset from `sourceTag` to `targetTag`, matching names case-insensitively, then deletes `sourceTag`.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | Return the largest rendition with `Width <= maxWidth` | explicit | widths 320/640/1280, `maxWidth` 1000 returns 640 |
| R2 | Never return a rendition wider than `maxWidth`, whatever order the renditions are stored in | explicit | stored order 1280/320/640, `maxWidth` 700 returns 640 |
| R3 | Keep current behaviour for an exact match and for no renditions | implied (visible tests) | `ReturnsExactWidthMatch` and `ReturnsNullWhenAssetHasNoRenditions` still pass |
| R4 | Every asset tagged `sourceTag` ends up tagged `targetTag` | explicit | 2 assets tagged Red, `MergeTags("Red","Crimson")`: both have Crimson |
| R5 | `sourceTag` no longer exists afterwards | explicit | no asset has it and the tag store no longer lists it |
| R6 | Tag names match case-insensitively | explicit | `MergeTags("red","CRIMSON")` works on assets tagged "Red" with an existing tag "Crimson" |
| R7 | No duplicate target tag on an asset | implied (`AddTagIgnoresDuplicateTag`) | an asset that had both tags ends up with Crimson exactly once |
| R8 | Create the target tag if it does not exist | implied (`AddTagCreatesTagWhenMissing`) | merging into a new name creates it |
| R9 | Public API unchanged: exactly `MergeTags(string sourceTag, string targetTag)` on `TagService`, other signatures untouched | implied (hidden tests compile against it) | solution builds and all visible tests pass |

## Assumptions
- A1: No rendition fits (all wider than `maxWidth`) -> return `null` (same as "no renditions" today; never fall back to the smallest or the first, which is probably the bug).
- A2: Unknown `assetId` -> keep whatever the current code does. If it currently throws `NullReferenceException`, return `null` instead (not part of the bug, and changing it risks a regression test).
- A3: Two renditions with the same width -> return the first in stored order (deterministic, no test likely).
- A4: `maxWidth` of 0 or less -> return `null`, no throw. Switch to `ArgumentOutOfRangeException` only if `RenditionService` already validates its arguments.
- A5: `sourceTag` does not exist -> do nothing, no throw. Switch to `KeyNotFoundException` only if `TagService` already throws for unknown tags (for example in a remove method). The current API is lenient: `AddTag` creates a missing tag.
- A6: Target tag already exists in a different case -> keep its stored name ("Crimson" stays "Crimson" even if called with "crimson"). If it is missing, create it with `targetTag` as passed.
- A7: Source and target are the same name ignoring case (for example "Red" and "red") -> do nothing, and do not delete the tag. Deleting it would strip the tag from every asset.
- A8: Return type -> `void`. If `AddTag` returns something (`bool`, `Tag`), mirror that style instead.
- A9: Null or blank names -> do what `AddTag` does. If `AddTag` has no validation, use `ArgumentException.ThrowIfNullOrWhiteSpace`: null throws `ArgumentNullException`, blank throws `ArgumentException`.
- A10: How to delete a tag -> use existing `IAssetRepository` methods. Add a member to the interface only if no existing method can delete a tag, and first check that no test fake implements `IAssetRepository`, because a new member would break compiling the hidden tests. If a fake does exist, use a default interface method.
- A11: Compare names with `StringComparison.OrdinalIgnoreCase` or `StringComparer.OrdinalIgnoreCase`, never `ToLower()`.

## Files
| File | Action | Why |
|---|---|---|
| `README.md` | read, then change at the end | conventions; assumptions and cut list go here |
| `src/AssetLibrary/Services/RenditionService.cs` | change | Task 1 bug |
| `src/AssetLibrary/Services/TagService.cs` | change | Task 2 `MergeTags` |
| `src/AssetLibrary/Models/Asset.cs`, `Rendition.cs`, `Tag.cs` | read | how tags are linked to assets (names or ids), `Width` field |
| `src/AssetLibrary/Repositories/IAssetRepository.cs`, `InMemoryAssetRepository.cs` | read; change only per A10 | tag storage and delete support; check whether the in-memory store is case-sensitive |
| `tests/AssetLibrary.Tests/RenditionServiceTests.cs`, `TagServiceTests.cs` | read; append new tests only, never edit existing ones | test style and repo setup; hidden tests mirror them |
| `AssetLibrary.sln`, `*.csproj` | leave alone | no new packages |

Test command: `dotnet test` (from the repo root)

## Edge cases
| Case | Expected (chosen behaviour) | Req | Likely hidden test |
|---|---|---|---|
| widths 320/640/1280, `maxWidth` 1000 | 640 | R1 | yes |
| `maxWidth` 640 (exact) | 640 | R3 | yes (visible) |
| `maxWidth` 639 (one below) | 320 | R1 | yes |
| stored unsorted 1280/320/640, `maxWidth` 700 | 640 | R2 | yes (likely the "sometimes") |
| all wider, `maxWidth` 100 | `null` (A1) | R1, A1 | yes |
| no renditions | `null` | R3 | yes (visible) |
| `maxWidth` 0 or negative | `null` (A4) | A4 | maybe |
| unknown `assetId` | unchanged current behaviour, else `null` (A2) | A2 | maybe |
| 2 assets tagged "Red", `MergeTags("Red","Crimson")` | both have Crimson, neither has Red, Red is gone from the tag store | R4, R5 | yes |
| assets tagged "RED", called with "red" and "crimson" | merge happens | R6 | yes |
| asset has both Red and Crimson | Crimson once, Red removed | R7 | yes |
| target "Crimson" missing | created, assets tagged with it | R8 | yes |
| target exists as "Crimson", called with "crimson" | keeps "Crimson" casing, no second tag (A6) | R6, A6 | maybe |
| assets without Red | unchanged | R4 | yes (regression) |
| source missing | no-op, no throw (A5) | A5 | maybe |
| `MergeTags("Red","red")` | no-op, Red kept (A7) | A7 | yes |
| null or blank name | `AddTag`'s behaviour, else `ArgumentNullException` / `ArgumentException` (A9) | A9 | maybe |
| removing tags while iterating assets | no `InvalidOperationException`: materialise with `.ToList()` first | R4 | indirectly |

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | This recon, plus the recon prompt in the IDE and correcting its answer | 8 | the agent's plan matches A1 to A11 |
| 1 | Fix `GetBestRendition`: filter `Width <= maxWidth`, take the largest, `null` if none. Add tests for one-below, unsorted and none-fit | 10 | Task 1 rows above pass, visible tests green |
| 2 | `MergeTags` core: retag assets case-insensitively, dedupe, create target, delete source. Add tests | 14 | R4 to R8 rows pass |
| 3 | `MergeTags` guards: same tag no-op (A7), missing source no-op (A5), null or blank (A9). Add tests | 8 | guard rows pass |
| 4 | Full `dotnet test`, then a README section with approach, A1 to A11 and what was cut | 7 | all green, defaults written down |
| -- | cut line -- | | |
| 5 | Extra tests: ties, `maxWidth` <= 0, unknown asset, target casing (A6) | 5 | rows covered |
| 6 | Submit Section 1 | 2 | submitted |

Budget: 54 of 55 min (49 without step 5), review time included. Cut line: below step 4 (step 5's extra tests get dropped). If step 2 runs long, step 3 shrinks to the A7 test alone, since that is the likeliest trap.

What could cause the bug (check the agent's explanation against these): the filter result is thrown away (`Where` not assigned), the comparison is flipped or uses `Height`, the loop breaks early assuming the list is sorted, or there is a fallback that returns the first or smallest rendition when nothing fits.

## Recon prompt (paste first)
```
Do not edit or create any files yet. Read README.md, every file under src/AssetLibrary
and both test files first. Then:
1. Restate the two tasks in one sentence each.
2. List requirements as a numbered checklist, each marked explicit or implied,
   each with how a test would check it.
3. List assumptions for anything ambiguous, each with the default you pick and why.
   Cover at least: no rendition fits, unknown assetId, maxWidth <= 0, sourceTag missing,
   targetTag missing, source and target equal ignoring case, asset already has both tags,
   null or blank tag names, and whether IAssetRepository can delete a tag today.
4. List the files to read, change and leave alone.
5. List edge cases, each with the expected behaviour you chose, marking the ones
   hidden tests most likely check.
6. Explain in one or two sentences why GetBestRendition can return a rendition wider
   than maxWidth, citing the line.
7. Propose a step plan where each step changes one function or fixes one bug,
   with minutes per step for a 47-minute limit and a cut line.
Respect existing signatures and style; no new dependencies; do not modify existing tests.

Task 1 (bug): RenditionService.GetBestRendition(Guid assetId, int maxWidth) should return
the largest rendition whose width is less than or equal to maxWidth. Users report it
sometimes returns a rendition wider than maxWidth.
Task 2 (feature): Add TagService.MergeTags(string sourceTag, string targetTag). Every asset
tagged with sourceTag must end up tagged with targetTag, and sourceTag must be deleted
afterwards. Tag names are case-insensitive.
```
Correct the agent's answer before step 1 wherever it differs from A1 to A11. Pay most attention to A1 (`null`, not a fallback) and A7 (same tag means no-op). A correction it can see shows judgement in the graded log.

## Step 1 prompt (first implementation prompt)
```
Step 1 only: fix the bug in RenditionService.GetBestRendition. Read
src/AssetLibrary/Services/RenditionService.cs, src/AssetLibrary/Models/Rendition.cs and
tests/AssetLibrary.Tests/RenditionServiceTests.cs.
It must return the largest rendition whose Width <= maxWidth, regardless of the order
renditions are stored in, and null when none fits (never fall back to the smallest or
first). Keep the method signature, the null result for an asset with no renditions, and
the current behaviour for an unknown assetId. No new packages, follow the existing style,
do not touch TagService and do not edit the existing tests.
Expected: widths 320/640/1280 -> maxWidth 1000 gives 640, 640 gives 640, 639 gives 320,
100 gives null; stored order 1280/320/640 with maxWidth 700 gives 640; maxWidth 0 gives null.
First explain the cause in one sentence, citing the line. Then show the diff, and add new
xUnit tests in RenditionServiceTests.cs for the one-below, unsorted and none-fit cases,
following the existing test setup and naming style. Then run dotnet test.
```

Defaults most worth checking: A7 (same name ignoring case means do nothing and keep the tag), A5 (a missing source does nothing instead of throwing) and A10 (don't add members to `IAssetRepository` if a test fake implements it).

One question, which you can answer from the agent's recon reply: does `TagService` already throw for an unknown tag or a blank name? If it does, switch A5 and A9 to match it.

Confirm the plan, or tell me which defaults to change, before you send the step 1 prompt.
