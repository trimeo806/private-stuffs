`TagService.MergeTags` now works. I wrote the tests first and watched them fail on `NotImplementedException`, then wrote the code. `dotnet test tests/AssetLibrary.Tests` passes all 16 tests: your 2 existing ones and 14 new ones.

**What it does** (`src/AssetLibrary/Services/TagService.cs`)
- A null, empty or whitespace source or target throws `ArgumentException`, the same check `AddTag` uses.
- Tag names are trimmed and compared case-insensitively.
- For each asset that has the source tag:
  - If the asset doesn't have the target yet, the target takes the source's place, so tag order is kept.
  - If it already has the target in any case, that copy and its casing stay and no duplicate is added.
  - Every copy of the source is removed, including copies in a different case.
  - The asset is saved.
- Only assets that changed are saved and counted, and that count is the return value.
- The public API is unchanged. I added only one private helper, `IsSameTag`.

**Tests added** (`tests/AssetLibrary.Tests/TagServiceTests.cs`)
- The source is replaced in place.
- The source is matched in any case.
- The target is kept once when it's already there in another case.
- Every copy of the source is removed.
- The return count is right across several assets, and assets without the source are left alone.
- It returns 0 when no asset has the source.
- Input is trimmed.
- It does nothing when the source and target are the same tag.
- Blank source or target (null, `""`, `"   "`) throws.

**Choices I made that TASK.md doesn't cover** (worth checking against the hidden tests)
1. **Merging a tag into itself** (for example `MergeTags("brand", "BRAND")`): it changes nothing and returns 0. It does not throw, and it does not rename the tag to the new casing.
2. **The asset already has the target:** it keeps its existing casing (`"Final"` stays `"Final"`), the same way `AddTag` handles it.
3. **Null input:** it throws a plain `ArgumentException`, not `ArgumentNullException`, so `Assert.Throws<ArgumentException>` passes on the exact type.
