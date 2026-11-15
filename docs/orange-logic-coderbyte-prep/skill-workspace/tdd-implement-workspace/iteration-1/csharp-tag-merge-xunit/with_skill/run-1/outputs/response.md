## Step 2: merge tags (`TagService.MergeTags`)

**Changed**
- `src/AssetLibrary/Services/TagService.cs`: implemented `MergeTags` as a thin shell (guard, load, decide, save changed, return count) plus three private helpers: `RequireTagName`, `MergeTagList` (pure core), `IsSameTag`. Public API unchanged; `AddTag` untouched.
- `tests/AssetLibrary.Tests/TagServiceTests.cs`: added 4 tests (one theory with 6 cases). Existing tests unchanged.

**Tests (red -> green)**
| Test | Kind | Red because | Now |
|---|---|---|---|
| `MergeTags_retags_every_source_asset_with_the_target_and_returns_the_count` | integration (in-memory repo) | stub threw `NotImplementedException` | green |
| `MergeTags_matches_tags_in_any_case_and_keeps_a_single_copy_of_an_existing_target` | integration | first green used ordinal match: expected 1 changed, got 0 | green |
| `MergeTags_rejects_a_blank_tag_name_and_changes_nothing` (empty, whitespace, null for each argument) | unit through public method | no exception thrown, all 6 cases | green |
| `MergeTags_into_the_same_tag_in_another_case_changes_nothing` | integration | `MergeTags("logo", " LOGO ")` deleted the tag and returned 1 instead of 0 | green |

**Suite:** `dotnet test tests/AssetLibrary.Tests` -> 11 passed, 0 failed (baseline: 2 passed, 0 failed)

**Design notes**
- Model: existing `Asset`; reads `Tags`, writes a new `Asset` with the merged tag list. No new types.
- Flow: `validate -> repository.GetAll -> keep assets with source -> MergeTagList (pure) -> Save each -> count`.
- Boundary and test double: `IAssetRepository`, tested with the existing `InMemoryAssetRepository`; no mocks.
- Mutation lives in: the single `Save` loop in `MergeTags`. Loaded assets are not mutated; only assets that carry the source tag are saved, so the count equals the saves.

**Edge cases handled**
- Blank, whitespace or null `sourceTag`/`targetTag` -> `ArgumentException` with the parameter name, thrown before any load, nothing saved.
- Tag names are trimmed (same as `AddTag`) and compared with `OrdinalIgnoreCase`.
- Asset without the target -> source replaced in place by the target (tag order preserved).
- Asset already has the target in any case -> source removed, existing target kept once with its existing spelling (same rule as `AddTag`).
- Source appearing twice in different cases on one asset -> a single target.
- Source and target equal ignoring case/whitespace -> returns 0, nothing saved.
- No asset has the source -> returns 0, nothing saved.

**Open risks / assumptions**
- Tag order: I chose "replace in place". If hidden tests expect the target appended at the end instead, the first test's `["web", "brand"]` style assertions would differ; the task does not say.
- Merging a tag into itself returns 0 rather than renaming its casing (e.g. `logo` -> `LOGO`). Reasonable reading of "number of assets that changed", but not stated.
- Pre-existing duplicate target copies on an asset (`["Brand", "brand"]`) are left as they are; only the merge itself never adds a second copy.
- Not tested directly: duplicate source variants on one asset (handled by `MergeTagList`, untested).
- `AddTag` repeats the blank-tag guard; I left it alone to keep the diff to this step. Switching it to `RequireTagName` is a safe follow-up refactor.

**Next:** ai-diff-review on this diff, then edge-case-tests on `MergeTags` (duplicate source variants, append-vs-in-place order, many assets).
