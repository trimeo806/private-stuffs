Here is the recon, built from the task text and the file tree only. Anything marked "verify" needs one quick look at the code. Budget: 60 min for Section 1, which leaves 20 for Section 2.

## Goal
Make `GetBestRendition` return the widest rendition that fits within `maxWidth` (or null when none fits), and add a case-insensitive `MergeTags` that moves every asset from `sourceTag` to `targetTag` and then deletes `sourceTag`. Visible tests and as many hidden tests as possible must pass.

## Requirements
| # | Requirement | Type |
|---|---|---|
| R1 | `GetBestRendition` never returns a rendition with `Width > maxWidth` | explicit |
| R2 | Of the renditions that fit, it returns the widest | explicit |
| R3 | A rendition with `Width == maxWidth` is returned (visible test `ReturnsExactWidthMatch`) | explicit |
| R4 | An asset with no renditions returns null (visible test) | explicit |
| R5 | Returns null when every rendition is wider than `maxWidth` (no "closest" or "smallest" fallback) | implied |
| R6 | The result does not depend on the order of the renditions list | implied |
| R7 | An unknown `assetId` keeps the current behaviour (null or the existing exception) | implied |
| R8 | After `MergeTags`, every asset that had `sourceTag` has `targetTag` | explicit |
| R9 | `sourceTag` no longer exists after the merge (not in the tag store, not on any asset) | explicit |
| R10 | Tag names compare case-insensitively everywhere in the merge (`"Cat"` equals `"cat"`) | explicit |
| R11 | An asset that already has both tags ends up with exactly one `targetTag` (same rule as `AddTagIgnoresDuplicateTag`) | implied |
| R12 | If `targetTag` does not exist yet, it is created (same rule as `AddTagCreatesTagWhenMissing`) | implied |
| R13 | Assets that never had `sourceTag` are unchanged | implied |
| R14 | No public signatures change, no new packages, and the 4 visible tests stay green | implied |

## Assumptions
- A1: No rendition fits → return null. The task says "less than or equal", so a wider fallback is the bug and not a feature.
- A2: Two renditions with the same best width → return the first one in list order. This is deterministic and unlikely to be tested.
- A3: `maxWidth <= 0` → null. If the code already has a guard that throws, keep it.
- A4: `sourceTag` equals `targetTag` ignoring case → no-op. Do not delete the tag, because deleting it would strip the tag from every asset.
- A5: `sourceTag` does not exist → follow whatever `AddTag`/`RemoveTag` do for "not found" (verify). If there is no precedent, do nothing (no-op).
- A6: Null or blank arguments → copy the validation `AddTag` already uses (verify). Note that xUnit's `Assert.Throws<T>` matches the exact type: an `ArgumentNullException` fails an `Assert.Throws<ArgumentException>`.
- A7: When `targetTag` already exists, keep its stored casing. When it is created, use the casing the caller passed.
- A8: Do not add members to `IAssetRepository`. Hidden tests may use their own fake of it, and a new interface member would break their compile. Build `MergeTags` from the methods that already exist.

## Files
| File | Action | Why |
|---|---|---|
| README.md | Read | Run commands, stated rules |
| Models/Rendition.cs, Asset.cs, Tag.cs | Read | Is `Width` nullable? How are tags stored on an asset (strings or `Tag` objects)? |
| Repositories/IAssetRepository.cs | Read, do not change (A8) | Which methods exist for get / update / delete tag |
| Repositories/InMemoryAssetRepository.cs | Read | Name comparison (`==` vs `StringComparer.OrdinalIgnoreCase`), whether reads return copies or live references (decides if you must call Update) |
| Services/RenditionService.cs | Change | Task 1 fix |
| Services/TagService.cs | Change | Task 2 `MergeTags`; mirror `AddTag` for validation and case handling |
| tests/.../RenditionServiceTests.cs, TagServiceTests.cs | Read, then add tests | Copy how they build the service and repository |
| *.csproj, AssetLibrary.sln | Leave alone | No new packages |

Likely causes of the rendition bug, to check in this order: a fallback to the closest or smallest rendition when none fits; a loop that assumes sorted input and breaks early; ordering by `Math.Abs(width - maxWidth)`; comparing `Height` instead of `Width`; `<` where `<=` is needed (that one would fail the exact-match test instead).

## Edge cases
- Width == maxWidth → that rendition is returned
- All renditions wider → null
- Renditions unsorted (for example 800, 200, 1200, 400 with max 500) → the 400 rendition
- Several renditions fit → the widest of them, not the first or the smallest
- Single rendition that fits / single rendition that is too wide → that rendition / null
- Duplicate widths → one of them, and it never exceeds maxWidth
- maxWidth 0 or negative → null (A3)
- Unknown assetId or null Renditions list → null, with no NullReferenceException
- Merge with different casing (`MergeTags("CAT", "feline")` where the stored tag is `"cat"`) → works
- Asset already has both tags → exactly one target tag
- Target tag missing → it is created and the assets get it
- Source equals target ignoring case → no-op (A4)
- Source tag missing → no-op or the existing not-found behaviour (A5)
- Source tag on 0 assets → the tag is still deleted
- Removing tags while iterating the asset list → iterate over a `.ToList()` copy, otherwise you get an `InvalidOperationException`

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Read the files above, run `dotnet test` | 6 | Baseline green; you can name the rendition bug |
| 1 | Add failing tests: unsorted list, all wider → null, several fit → widest | 5 | The new tests are red for the right reason |
| 2 | Fix `GetBestRendition` (filter `Width <= maxWidth`, take the max width, else null) | 4 | All rendition tests green |
| 3 | `MergeTags` tests: basic move plus source deleted, mixed casing, asset already has both | 7 | Red; they compile against a stub that throws `NotImplementedException` |
| 4 | Implement `MergeTags` core, using only existing repository methods | 12 | Step 3 tests green, visible tests still green |
| 5 | Edge tests and handling: target missing, source == target, source missing, null/blank args | 9 | Green |
| 6 | Full `dotnet test`, read the diff, remove stray comments and debug code | 4 | All green, diff limited to the two services and the two test files |
| | Buffer | 13 | |

Total 60 min. Task 1 goes first because it is the cheapest sure points.
Cut line: drop the null/blank-argument part of step 5 first, then the source-missing case. Never cut steps 2 and 4. If you are at minute 50 and step 4 is red, stop and make the basic merge pass.

## First prompt
```
Context: .NET 8 + xUnit repo (file tree attached). Two tasks:
1) Bug: RenditionService.GetBestRendition(Guid assetId, int maxWidth) must return the
   rendition with the largest Width that is <= maxWidth. It sometimes returns one wider
   than maxWidth.
2) Feature: TagService.MergeTags(string sourceTag, string targetTag): every asset tagged
   sourceTag ends up tagged targetTag (no duplicates), then sourceTag is deleted. Tag
   names are case-insensitive.

Before writing any code: read Models/*, IAssetRepository, InMemoryAssetRepository,
RenditionService, TagService and both test files. Then:
- Quote the exact lines in GetBestRendition that let a wider rendition through and explain why.
- Tell me how tags are stored on Asset, how the repository compares tag names, and which
  existing repository methods MergeTags can use without changing IAssetRepository.
- Tell me what AddTag does for null/blank names and for a missing tag.
- List edge cases with expected behavior (unsorted renditions, none fit, source == target
  ignoring case, source missing, target missing, asset already has both).
Propose a step plan where each step changes one function. Respect existing signatures
and style; no new dependencies; no code comments.
```

Questions I could not settle without the code (the defaults above apply until you check): does the task page say what to return when no rendition fits, and does `AddTag` throw or no-op on a missing or blank tag? Copy whatever the code already does for both.
