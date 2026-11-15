`TagService.MergeTags` is done, and all 17 tests pass (`dotnet test tests/AssetLibrary.Tests`): the 2 tests that were already there plus 15 new ones.

**How it works** (`src/AssetLibrary/Services/TagService.cs`)
- If either tag name is null, empty or whitespace, it throws `ArgumentException`. The `ParamName` is `sourceTag` or `targetTag`.
- Both names are trimmed, the same way `AddTag` trims its input. Tags are compared with `OrdinalIgnoreCase`.
- For each asset that has the source tag:
  - If the asset doesn't have the target tag yet, the target goes where the first source tag was, so the other tags keep their order.
  - If the asset already has the target tag in any case, it keeps that existing tag and its casing, and no second copy is added.
  - Every copy of the source tag is removed, whatever its case.
  - The asset is saved and counted.
- Assets without the source tag are not saved and not counted. The method returns how many assets changed.
- If the source and target are the same tag apart from case (`"brand"` → `"BRAND"`), it changes nothing and returns 0.
- The public signature is unchanged.

**Tests:** I added `tests/AssetLibrary.Tests/MergeTagsTests.cs`. It covers:
- a basic merge and keeping tag order
- matching the source tag in any case
- an asset that already has the target tag
- removing duplicate source tags
- counting across several assets
- returning 0 when nothing matches
- trimming
- source and target being the same tag
- blank or null source and target names

**Choices I made where TASK.md doesn't say** (the hidden tests might expect something different):
1. **Position:** the target tag replaces the source tag where it stood. It is not added at the end.
2. **Same tag:** a source and target that differ only in case count as a no-op, so you can't use this method to change a tag's casing.
3. **Existing duplicates:** if an asset already holds the target tag twice in different cases (`"Final"`, `"final"`), both copies stay. Only the source tag's copies are removed.
