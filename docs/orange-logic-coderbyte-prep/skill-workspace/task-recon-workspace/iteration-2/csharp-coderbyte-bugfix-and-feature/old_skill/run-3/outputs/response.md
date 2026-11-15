I haven't seen the code yet, so this plan is based only on the task text and the file tree. The "likely cause" notes are guesses to check once you open the files. Budget: Section 1 gets 60 of the 80 minutes and Section 2 keeps its 20.

## Goal
Fix `GetBestRendition` so it never returns a rendition wider than `maxWidth`, and add `MergeTags`, which moves every asset from one tag to another without regard to case and then deletes the source tag. Both should pass the hidden tests.

## Requirements
| # | Requirement | Type |
|---|---|---|
| R1 | `GetBestRendition` returns a rendition with `Width <= maxWidth` | explicit |
| R2 | Among the renditions that fit, it returns the one with the largest width | explicit |
| R3 | It returns `null` when the asset has no renditions (an existing test checks this) | explicit |
| R4 | An exact width match is returned (an existing test checks this) | explicit |
| R5 | It returns `null` when no rendition fits (all are wider than `maxWidth`) | implied |
| R6 | After `MergeTags`, every asset that had `sourceTag` has `targetTag` | explicit |
| R7 | `sourceTag` is deleted afterwards: from the tag store and from every asset | explicit |
| R8 | Tag names are matched without regard to case in both arguments | explicit |
| R9 | An asset that already had both tags ends up with `targetTag` once, not twice | implied |
| R10 | Assets that never had `sourceTag` are unchanged | implied |
| R11 | The 4 visible tests still pass and the method signatures stay as given | implied |

## Assumptions
- A1: Two renditions fit with the same width → return the first one in the stored order. Don't invent a tie-breaker the tests can't know about.
- A2: The asset ID doesn't exist → do whatever the method does today (probably `null`). Keep the current contract.
- A3: `maxWidth <= 0` → return `null`, which falls out naturally from the filter. No exception.
- A4: `sourceTag` equals `targetTag` without regard to case (`"Cat"` / `"cat"`) → do nothing. It must NOT delete the tag. This is the most likely hidden trap.
- A5: `sourceTag` doesn't exist → do nothing, no exception, unless `AddTag` or the repository already throws for missing tags. If so, match that.
- A6: `targetTag` doesn't exist → create it, the same way `AddTag` does, then merge. The behavior is effectively a rename.
- A7: When `targetTag` already exists, keep its stored spelling and don't overwrite it with the argument's casing.
- A8: `null` or blank arguments → `ArgumentException`, but only if `AddTag` already validates arguments this way. Otherwise leave it out.
- A9: The repository has no delete method → add one to both `IAssetRepository` and `InMemoryAssetRepository`. Change only what you need there.

## Files
| File | Action | Why |
|---|---|---|
| `README.md` | Read | Rules from the task setter and how tests are run |
| `Models/Asset.cs`, `Rendition.cs`, `Tag.cs` | Read | Whether an asset stores tags as strings or `Tag` objects or IDs, and whether a rendition has a nullable width |
| `Repositories/IAssetRepository.cs` | Read; change only if needed (A9) | Which list, update and delete methods exist |
| `Repositories/InMemoryAssetRepository.cs` | Read; change only if needed (A9) | Whether it returns copies or live references, and whether its lookups ignore case |
| `Services/RenditionService.cs` | Change | Task 1 bug |
| `Services/TagService.cs` | Change | Task 2 feature. Copy `AddTag`'s duplicate check and case handling |
| `tests/.../RenditionServiceTests.cs`, `TagServiceTests.cs` | Read, then add tests | Copy the setup style; add edge-case tests |
| `AssetLibrary.sln`, both `.csproj` | Leave alone | No new packages, no target change |

Likely causes of the Task 1 bug, to check when you read the code:
- sorting by width descending without first filtering by `<= maxWidth`
- using `<` or `>` the wrong way round
- comparing `Height` instead of `Width`
- `FirstOrDefault` over an unsorted list
- a fallback that returns the largest rendition when nothing fits

Also check `AddTag`. It probably already holds the case-insensitive comparison that `MergeTags` should reuse (`StringComparison.OrdinalIgnoreCase`).

## Edge cases
- Renditions given in mixed order, e.g. widths 1920, 640, 1280 with `maxWidth` 1300 → 1280
- All renditions wider than `maxWidth` → `null` (the most likely reproduction of the reported bug)
- A single rendition that fits → that one; a single rendition that's too wide → `null`
- A rendition exactly at `maxWidth` → returned (`<=`, not `<`)
- `maxWidth` of 0 or negative → `null`
- Two renditions with the same width that fits → the first one, without crashing
- An asset with no renditions or a `null` list → `null`
- Merging `"Nature"` into `"nature"` → nothing changes and the tag still exists
- Merging `"NATURE"` into `"Landscape"` when the stored tag is `"nature"` → assets move and `"nature"` is deleted
- An asset that has both tags → ends up with `targetTag` once
- `sourceTag` missing → nothing changes; `targetTag` missing → created
- Several assets with the source tag → all of them move. Don't change a collection while looping over it (C# throws `InvalidOperationException` if you do)
- An asset with no tags or a `null` tag list → skipped, no `NullReferenceException`

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 1 | Read the models, the repository and both services; run `dotnet test` to get a baseline | 7 | All 4 visible tests pass; you know how tags are stored |
| 2 | Write failing tests for Task 1: mixed order, nothing fits, exact match at the boundary | 5 | The new tests fail for the reason in the bug report |
| 3 | Fix `GetBestRendition`: filter `Width <= maxWidth`, take the largest, return `null` if none fit | 6 | All the `RenditionService` tests pass |
| 4 | Add `MergeTags` for the main case: find the source tag ignoring case, add the target to each asset without duplicates, delete the source | 15 | A test passes where 2 assets move from `"A"` to `"B"` and `"A"` is gone |
| 5 | Handle the `MergeTags` edge cases: same tag in different casing, missing source, missing target, asset with both tags | 10 | One test per edge case passes |
| 6 | Run the full `dotnet test`; review the diff for unused code, debug output and changed signatures | 5 | All tests pass and the diff is limited to the files that need to change |
| 7 | Buffer | 12 | — |

Cut line: if time runs short, drop step 7 first, then the tests for missing source and missing target in step 5. Never drop the same-tag-different-casing no-op or the duplicate-tag guard. Stop Section 1 at the 60-minute mark whatever happens; the written questions can't be revisited.

## First prompt
```
Read README.md, src/AssetLibrary/Models/*.cs, src/AssetLibrary/Repositories/*.cs,
src/AssetLibrary/Services/RenditionService.cs, src/AssetLibrary/Services/TagService.cs
and both test files. Do not write code yet.

Report:
1. How Asset stores tags (strings, Tag objects or IDs) and renditions (nullable width?).
2. Which repository methods exist for listing/updating assets and for deleting a tag.
3. How AddTag compares tag names (which StringComparison, if any) and handles missing tags.
4. The exact line(s) in GetBestRendition that can return a rendition wider than maxWidth,
   with a concrete input that reproduces it.

Then propose, as a step plan where each step changes one method:
- a fix for GetBestRendition: largest rendition with Width <= maxWidth, null if none fits;
- MergeTags(sourceTag, targetTag): case-insensitive; no-op when source equals target
  ignoring case; create target if missing; no duplicate tag on assets that already have
  target; remove source from every asset and delete the source tag.
List the xUnit tests you would add for each, in the existing test style.
Keep existing signatures and style; no new packages.
```

Open questions I answered with defaults, so check them when you read the code:
- Do missing tags throw? (A5)
- Does the repository have a delete method? (A9)
- Does `AddTag` validate blank names? (A8)
