Recon below is built from the task text and the file tree only. You haven't opened the code yet, so every point that depends on it is written down as an assumption with something to check. Fix each one when the in-IDE agent reads the real files.

## Goal
Make `GetBestRendition` never return a rendition wider than `maxWidth`, and add `TagService.MergeTags` so that every asset with `sourceTag` ends up with `targetTag` and `sourceTag` is deleted, with tag names compared case-insensitively. These are two separate deliverables, so ship them in that order.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | `GetBestRendition` returns the widest rendition with `Width <= maxWidth` | explicit | test: widths 100/300/500, max 400 -> 300 |
| R2 | Width equal to `maxWidth` is allowed (inclusive) | explicit | `ReturnsExactWidthMatch` still green, plus 300/max 300 -> 300 |
| R3 | No rendition fits -> return the same "nothing" value as the no-renditions case (null) | implied | test: widths 500/800, max 400 -> null |
| R4 | Result does not depend on the order renditions are stored in | implied | test with unsorted input passes |
| R5 | `MergeTags(string sourceTag, string targetTag)` exists on `TagService` with exactly that signature | explicit | hidden tests compile |
| R6 | Every asset tagged `sourceTag` ends up tagged `targetTag` | explicit | test: 2 assets with "red", merge into "crimson" -> both have "crimson" |
| R7 | `sourceTag` is deleted afterwards, from every asset and from the tag store | explicit | no asset has the tag and the tag lookup finds nothing |
| R8 | Names match case-insensitively (`"Red"` == `"red"`) | explicit | merge("RED", "Crimson") with stored "red"/"crimson" works |
| R9 | No duplicate target tag on an asset that already had both | implied (mirrors `AddTagIgnoresDuplicateTag`) | asset with red and crimson ends with one crimson |
| R10 | Existing public API and visible tests unchanged | implied | the 4 visible tests still pass, no signature changed |

## Assumptions
- A1: Return type of `MergeTags` -> `void`, unless `AddTag` returns something (`Tag`/`bool`), in which case match it (hidden tests are written against the house style).
- A2: Source and target are the same tag ignoring case (`"red"`, `"Red"`) -> no-op. **Never delete the tag** (this is the classic trap: a naive merge deletes the tag it just merged into).
- A3: `targetTag` does not exist yet -> create it, the same way `AddTag` creates a missing tag (the task says assets "must end up tagged with targetTag").
- A4: `sourceTag` does not exist -> follow how existing code handles a missing entity (`KeyNotFoundException` if the repo/service throws on missing ids, otherwise a silent no-op). Decide after reading `TagService`/`InMemoryAssetRepository`, and write it in the README.
- A5: null/empty/whitespace tag name -> `ArgumentException` (`ArgumentNullException` for null if the code already uses it).
- A6: Existing casing of the target tag is kept (merge into "crimson" when "Crimson" is stored -> stays "Crimson").
- A7: Unknown `assetId` in `GetBestRendition` -> keep the current behaviour, whatever it is (probably null). Don't change it, because a hidden regression test may rely on it.
- A8: Ties on width -> any of the tied renditions, keep the current first-match order.

## Files
| File | Action | Why |
|---|---|---|
| README.md | read, then change at the end | conventions, then assumptions and decisions |
| src/AssetLibrary/Services/RenditionService.cs | change | bug |
| src/AssetLibrary/Services/TagService.cs | change | add `MergeTags` |
| src/AssetLibrary/Models/Asset.cs, Rendition.cs, Tag.cs | read | how tags are stored (strings vs `Tag` objects, list vs set, comparer?) |
| src/AssetLibrary/Repositories/IAssetRepository.cs | read; change only if unavoidable | adding interface members can break hidden test fakes that implement it |
| src/AssetLibrary/Repositories/InMemoryAssetRepository.cs | read; change only alongside the interface | storage, any case-sensitive dictionary |
| tests/AssetLibrary.Tests/*.cs | read, then add tests | naming style, setup pattern; don't edit the existing 4 tests |
| *.csproj, AssetLibrary.sln | leave alone | no new packages |

Test command: `dotnet test` (from the repo root, where `AssetLibrary.sln` is)

## Edge cases
| Case | Expected | Req | Likely hidden test |
|---|---|---|---|
| Widths 100/300/500, max 400 | 300 | R1 | yes |
| Width exactly == max | that rendition | R2 | yes (visible already) |
| All renditions wider than max | null | R3 | yes |
| Renditions stored unsorted / descending | still the widest fitting one | R4 | yes (this is probably the bug) |
| Single rendition wider than max | null, not that rendition | R1, R3 | yes |
| maxWidth 0 or negative | null | R3 | maybe |
| Asset has source only | gets target, loses source | R6, R7 | yes |
| Asset has both source and target | one target, no source | R9 | yes |
| Different case ("RED" vs stored "red") | treated as same tag | R8 | yes |
| source == target ignoring case | no-op, tag survives | A2 | likely |
| Target tag does not exist | created, then applied | A3 | likely |
| Source tag does not exist | per A4 | A4 | maybe |
| Assets without source untouched | unchanged | R6 | maybe |
| null / "" / "  " tag | ArgumentException | A5 | maybe |

C# traps to tell the agent about: use `StringComparison.OrdinalIgnoreCase` / `StringComparer.OrdinalIgnoreCase`, not `ToLower()`. Don't remove items from a tag list while iterating over it (`InvalidOperationException`); call `.ToList()` first. A `Dictionary<string, Tag>` built without a comparer is case-sensitive. `First()` throws on empty, so use `FirstOrDefault()`.

Likely bug shapes to check in `GetBestRendition`: max picked before filtering, a filter on `Height` instead of `Width`, a `>=` the wrong way round, or `FirstOrDefault` with no sort that only works when renditions happen to be sorted.

## Plan
Time: 80 min total, about 55 for Section 1, at least 20 kept for Section 2.

| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Paste the in-IDE recon prompt, correct its reading (A2, A4, interface risk) | 8 | agent's plan matches this one |
| 1 | Fix `GetBestRendition`, plus 1 regression test (unsorted, max between widths) | 10 | R1-R4 tests green, visible tests green |
| 2 | `MergeTags` core: copy target to every source asset, delete source, case-insensitive | 12 | R5-R8 tests green |
| 3 | `MergeTags` rules: same-tag no-op, already has target, missing target/source, blank input, plus 3-4 tests | 8 | A2-A5 rows green |
| 4 | README: bug cause, approach, A1-A8, what was cut | 5 | decisions written down |
| 5 | Full `dotnet test`, read the final diff once | 3 | all green, no stray edits |
| --- | **cut line** | | |
| 6 | Extra tests for the remaining table rows (maxWidth <= 0, untouched assets, ties) | 7 | rows covered |
| | submit buffer | 2 | |

Budget: 55 of 55 min (Section 1 share), review time included. Cut line: below step 5 (extra edge-case tests get dropped first, and the README is never dropped). If step 2 runs long, still submit with step 1 done and the visible tests green.

## First prompt
Paste this into the Coderbyte IDE first, with both task statements and the file tree. The graders read the prompt log, so planning first is part of your score:

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
   with minutes per step for a 47-minute limit and a cut line.
Respect existing signatures and style; no new dependencies.
```

When it answers, correct anything it gets wrong in your next message before you ask for step 1, for example: "Merging a tag into itself (any case) must be a no-op, not a delete." Graders count a visible correction as judgement.

Then step 1:

```
Read src/AssetLibrary/Services/RenditionService.cs, src/AssetLibrary/Models/Rendition.cs
and tests/AssetLibrary.Tests/RenditionServiceTests.cs. Fix only GetBestRendition(Guid assetId,
int maxWidth): it must return the rendition with the largest Width that is <= maxWidth
(inclusive), regardless of the order renditions are stored in, and null when none fits
(same as the no-renditions case). Examples: widths 100/300/500 with max 400 -> 300;
max 300 -> 300; widths 500/800 with max 400 -> null; a single rendition wider than max -> null.
Keep the signature, the behaviour for an unknown asset, and the existing style; no new packages;
don't touch TagService or the existing tests. Add one xUnit test in RenditionServiceTests,
following its naming and setup, for the unsorted-input case. Explain the root cause in one
sentence before the diff.
```

Before any code gets written, confirm or correct three things:
1. Is 55/20 the right Section 1/Section 2 split, or do you want more time for the written answers?
2. Defaults A2 (same-tag merge is a no-op) and A3 (create a missing target tag): keep them?
3. A4 (missing source tag): throw or no-op? I've left this one to decide after the agent reads `TagService`. Match whatever the existing code does for a missing entity.
