# Example reply

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
## Diagnosis: 3 failures, 3 root causes

**Reproduced:** `dotnet test tests/AssetLibrary.Tests` -> 3 failed, 11 passed

| # | Failure(s) | Root cause (file:line) | Proof: input -> actual vs expected |
|---|---|---|---|
| 1 | Upload_DuplicateChecksumDifferentCase_Throws | InMemoryAssetRepository.cs:22 `FindByChecksum` compares with `==` (case-sensitive); `Delete` at :31 has the same `==` | stored "ABC", upload "abc" -> no exception vs DuplicateAssetException |
| 2 | GetPage_SecondPage_ReturnsRemainingItems | AssetService.cs:71 `Skip(pageNumber * pageSize)` treats the 1-based page as 0-based | 5 items, page 2, size 2 -> 1 item vs 2 |
| 3 | SearchByTags_MatchAny_ReturnsAssetsOrderedById | AssetService.cs:58 returns repository enumeration order, no `OrderBy(a => a.Id)` | inserted 3,1,2 -> [3,1,2] vs [1,2,3] |

**Trap:** lowercasing the checksum in `Upload` passes test 1 but misses assets already stored in uppercase and changes stored data; fix the comparison in the repository.
**Not verified:** hidden tests not seen.

**Fix prompts, one per cause, send in order, run tests between:**

1. > Root cause: InMemoryAssetRepository.cs:22 compares checksums with == (case-sensitive), and Delete at :31 does the same. Make the red test Upload_DuplicateChecksumDifferentCase_Throws pass by comparing with StringComparison.OrdinalIgnoreCase in both places, not by lowercasing in Upload. Keep stored checksum values, signatures, existing tests and packages unchanged.
2. > Root cause: AssetService.cs:71 skips pageNumber * pageSize, but pages are 1-based. Make GetPage_SecondPage_ReturnsRemainingItems pass with Skip((pageNumber - 1) * pageSize). Keep the signature, ordering and existing tests unchanged.
3. > Root cause: AssetService.cs:58 returns results in repository order. Make SearchByTags_MatchAny_ReturnsAssetsOrderedById pass by ordering by Id ascending in SearchByTags. Keep the signature and existing tests unchanged.
```

Why this works: about 250 words before the prompts. One row per cause, each proven by a real run. The trap line stops the tempting wrong fix. Each prompt is one reviewable step, so the prompt log shows three accepted, separate diffs.
