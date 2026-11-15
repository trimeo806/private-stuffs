# Example diagnosis

Input: a Coderbyte task says "Bugfix: some visible tests fail. Fix the code, not the tests." `dotnet test` shows three red tests in `AssetServiceTests`:

```
Failed SearchByTags_MatchAny_ReturnsAssetsOrderedById
  Assert.Equal() Failure: Collections differ  Expected: [1, 2, 3]  Actual: [3, 1, 2]
Failed GetPage_SecondPage_ReturnsRemainingItems
  Assert.Equal() Failure: Values differ  Expected: 2  Actual: 1   (Items.Count)
Failed Upload_DuplicateChecksumDifferentCase_Throws
  Assert.Throws() Failure: No exception was thrown
```

Output:

```markdown
## Diagnosis: three red AssetService tests

**Reproduced:** `dotnet test tests/AssetLibrary.Tests` -> 3 failed, 11 passed
**Requirement:** search results ordered by id ascending; pages newest first with ties by id; checksums compare case-insensitively

| # | Failure(s) | Root cause (file:line) | Evidence: input -> actual vs expected | Confidence |
|---|---|---|---|---|
| 1 | SearchByTags_MatchAny_ReturnsAssetsOrderedById | AssetService.cs:58 returns `matches` straight from the repository's dictionary enumeration, no `OrderBy(a => a.Id)` | ids 3,1,2 inserted in that order -> [3,1,2] vs [1,2,3]; scratch run with insertion 1,2,3 passes, so order is insertion order | proven |
| 2 | GetPage_SecondPage_ReturnsRemainingItems | AssetService.cs:71 `Skip(pageNumber * pageSize)` treats the 1-based page as 0-based | 5 items, page 2, size 2 -> Skip(4) returns 1 item (5th newest) vs 2 items (3rd and 4th newest) | proven |
| 3 | Upload_DuplicateChecksumDifferentCase_Throws | InMemoryAssetRepository.cs:22 `FindByChecksum` uses `==` (ordinal, case-sensitive) | stored "ABC", upload "abc" -> no match, no exception vs DuplicateAssetException | proven |

**Symptom site vs cause site:** #3 shows in `Upload` but starts in the repository lookup. Lowercasing in `Upload` before save would pass this test but miss assets already stored in uppercase, and the stored checksum would change (a data change the spec does not ask for).
**Blast radius:** #2 also makes page 1 skip the first page entirely and every page-past-end check off by one. #3 also breaks any other lookup that compares checksums; `Delete` uses the same `==` at line 31.
**Ruled out:** #1 is not the tag normalizer: `TagNormalizer` output was checked in a scratch test and matches.
**Fix direction (for tdd-implement):** #1 order by id in `SearchByTags`. #2 `Skip((pageNumber - 1) * pageSize)` in `GetPage`. #3 compare with `string.Equals(a, b, StringComparison.OrdinalIgnoreCase)` in the repository lookup; keep stored checksums as uploaded, keep all signatures and tests.
**Failing test to write first:** #2 `first_page_starts_with_newest_asset` (5 items, page 1, size 2 -> newest two). #3 `delete_finds_asset_by_checksum_in_other_case`. #1 already has its red test.
**Not verified:** whether hidden tests store data before the fix and read it after; not seen.

**In-IDE fix prompt:**
> Root cause: InMemoryAssetRepository.cs:22 compares checksums with == which is case-sensitive, and Delete at line 31 has the same bug. Before changing code, add a failing test to AssetServiceTests.cs named delete_finds_asset_by_checksum_in_other_case that stores "ABC" and deletes by "abc". Then fix the comparison in the repository with StringComparison.OrdinalIgnoreCase, not by lowercasing in Upload; keep the signatures, stored checksum values and every existing test and assertion unchanged, and add no packages.
```

Why this works: one row per cause, each with a concrete input and a real run; the symptom-vs-cause line explains why the tempting fix is wrong; blast radius gives tdd-implement and edge-case-tests their next tests. The fix prompt covers one cause; the user sends one prompt per cause, one step per diff.
