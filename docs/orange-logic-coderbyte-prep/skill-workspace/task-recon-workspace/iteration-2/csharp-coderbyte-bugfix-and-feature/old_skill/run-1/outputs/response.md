Here's the recon. It's built only from the task text and the file tree, so treat anything about the code as a guess until step 1 confirms it.

**Budget:** 80 min total, with 20 kept for the written section you can't come back to. That leaves **60 min for Section 1**. Stop coding at minute 55, whatever state you're in.

**Missing info, with the defaults I used:** test command → `dotnet test AssetLibrary.sln` from the repo root. README → not read yet, so check it in step 1 for any conventions, such as whether a missing asset or tag should return null or throw.

## Goal
Make `GetBestRendition` never return a rendition wider than `maxWidth`, and add `TagService.MergeTags`. It moves every asset from `sourceTag` to `targetTag`, case-insensitively, then deletes `sourceTag`. The visible and hidden xUnit tests must pass.

## Requirements
| # | Requirement | Type |
|---|---|---|
| R1 | `GetBestRendition` returns only renditions with `Width <= maxWidth` | explicit |
| R2 | Among those, it returns the one with the largest width | explicit |
| R3 | An exact width match is returned (`ReturnsExactWidthMatch`) | explicit (visible test) |
| R4 | Asset with no renditions → `null` (`ReturnsNullWhenAssetHasNoRenditions`) | explicit (visible test) |
| R5 | Every rendition wider than `maxWidth` → `null` (the same "nothing fits" result as R4) | implied |
| R6 | `MergeTags(source, target)`: every asset that had `source` now has `target` | explicit |
| R7 | `source` is deleted afterwards, from the tag store and from every asset | explicit |
| R8 | Tag names are compared case-insensitively | explicit |
| R9 | An asset that already had both tags ends up with `target` exactly once | implied (matches `AddTagIgnoresDuplicateTag`) |
| R10 | `IAssetRepository` and the public signatures stay unchanged | implied (hidden tests may compile against them) |
| R11 | `AddTag` and existing tests keep working | implied |

## Assumptions
- A1: `target` doesn't exist yet → create it, the same way `AddTag` does.
- A2: `source` doesn't exist → no-op, no throw. If `AddTag` or the README shows a throwing convention, follow that instead.
- A3: `source` equals `target` ignoring case → no-op. Do **not** delete the tag.
- A4: null, empty or whitespace argument → `ArgumentException`. If the service never validates input, follow its style instead.
- A5: Stored casing of `target` → keep the existing tag's casing. If `target` is new, use the casing passed in.
- A6: Case-insensitive means `StringComparer.OrdinalIgnoreCase`, not culture-sensitive comparison (avoids the Turkish-i problem).
- A7: Unknown `assetId` in `GetBestRendition` → `null`, matching R4. Follow the code if it already throws.
- A8: Several renditions with the same best width → return the first one in the stored order (stable).
- A9: `maxWidth <= 0` → `null`, since nothing can fit.
- A10: If the repository has no delete-tag method, delete through what already exists, for example by updating the tag list or the assets. Add a method to `InMemoryAssetRepository` only, never to the interface. A hidden test may implement `IAssetRepository`, and a new interface member would break its compile.

## Files
| File | Action | Why |
|---|---|---|
| `README.md` | Read | Conventions, null vs throw, how to run tests |
| `Services/RenditionService.cs` | Change | Task 1 bug |
| `Services/TagService.cs` | Change | Add `MergeTags`, reuse `AddTag`'s normalization |
| `Models/Asset.cs`, `Rendition.cs`, `Tag.cs` | Read | Shape of `Tags` (strings or `Tag` objects?), `Width` type, nullability |
| `Repositories/IAssetRepository.cs` | Read only | Don't change the contract (A10) |
| `Repositories/InMemoryAssetRepository.cs` | Read; change only if unavoidable | How tags and assets are stored; does it return copies or live references? |
| `tests/.../RenditionServiceTests.cs` | Read + add tests | Learn the arrange style, add edge-case tests |
| `tests/.../TagServiceTests.cs` | Read + add tests | Same |
| `*.csproj`, `.sln` | Leave alone | No new packages |

## Edge cases
**GetBestRendition**
- Mixed widths `[400, 800, 1200]`, max 1000 → 800. This is the reported bug, so likely causes are a missing filter, `OrderBy` instead of `OrderByDescending`, filtering on `Height`, or `<` vs `<=`.
- Unsorted input `[1200, 400, 800]`, max 1000 → 800.
- Every rendition wider than max → `null`.
- Exactly equal to max → that rendition.
- One rendition that fits → that rendition. One rendition that doesn't fit → `null`.
- Duplicate widths → the first one (A8).
- `maxWidth` 0 or negative → `null`.
- `maxWidth = int.MaxValue` → the widest rendition.
- `Renditions` is null rather than empty → `null`, no NRE.
- Unknown asset → `null` (A7).

**MergeTags**
- Asset with `source` only → ends with `target` only.
- Asset with both tags → `target` once, `source` gone.
- Asset with neither tag → unchanged.
- Mixed case: `MergeTags("Sunset", "BEACH")` while the assets hold `"sunset"` and `"beach"` → merges correctly, no duplicate `beach`.
- `source == target` ignoring case → no-op, tag still exists (A3).
- `source` missing → no-op (A2). `target` missing → created (A1).
- No assets tagged → `source` still deleted.
- Null or empty argument → `ArgumentException` (A4).
- Changing a collection while looping over it → take a snapshot (`.ToList()`) before mutating, to avoid `InvalidOperationException`.
- Running merge a second time → no-op, no throw.

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 1 | Read README, models, repo, both services, both test files. Run `dotnet test` | 6 | Baseline is green, and you know the shape of `Tags` and the null-vs-throw convention |
| 2 | Write a failing test: mixed and unsorted widths with max in the middle → correct rendition | 4 | The test is red and reproduces the bug |
| 3 | Fix `GetBestRendition`: filter `Width <= maxWidth`, take the max width, `FirstOrDefault`, null-safe | 5 | Step 2 test plus both visible tests are green |
| 4 | Rendition edge tests: all too wide, equal, ≤0, single, duplicate widths | 6 | Green |
| 5 | Write failing tests for the `MergeTags` core path: source-only asset, both-tags asset, source deleted | 5 | Red |
| 6 | Implement `MergeTags` with `OrdinalIgnoreCase`, reusing `AddTag`'s lookup or normalization. No interface change | 12 | Step 5 tests are green |
| 7 | Merge edge tests: mixed case, same tag, source missing, target missing, null args | 8 | Green |
| 8 | Full `dotnet test`, read the diff, remove debug output and comments | 4 | All green, small diff |
| — | Buffer | 5 | — |
| | **Total** | **55** | |

Cut line, in drop order: argument-validation tests (part of 7), then the `int.MaxValue` and duplicate-width tests (part of 4), then the rest of 7. **Never drop 3 or 6**. Partial credit comes from the hidden tests on the core paths.

## First prompt
Paste this into the Coderbyte AI panel and attach the file tree:

```
Before writing any code: read README.md, Models/*.cs, Repositories/*.cs,
Services/RenditionService.cs, Services/TagService.cs and both test files.
Then tell me:
1. Restate both tasks in one sentence each.
2. For RenditionService.GetBestRendition: quote the current implementation and
   name the exact line(s) that let a rendition wider than maxWidth be returned.
3. For TagService: how are tags stored on Asset (strings or Tag objects), how
   does AddTag find/normalize tags (case handling), and does IAssetRepository
   expose a way to delete a tag? Does the codebase return null or throw for
   missing items?
4. List edge cases for both tasks with expected behavior.
5. Propose a step plan, one function per step.
Constraints: do not change IAssetRepository or any public signature, no new
packages, use StringComparer.OrdinalIgnoreCase for tag names, follow the
existing code and test style, no code comments. Don't write code yet.
```

Then send one prompt per plan step, for example: "Step 2: write only the failing test for mixed unsorted widths, then run dotnet test and show the result."
