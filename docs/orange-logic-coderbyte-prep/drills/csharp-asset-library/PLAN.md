# Drill 1 — Implementation plan

Source: `TASK.md` spec + visible tests. Grader folder untouched.

## Goal
Green all 8 visible `AssetLibrary` tests and satisfy every spec rule hidden tests probe, without changing public signatures.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | 8 visible tests pass, fix code not tests | explicit | `dotnet test` 8/8 |
| R2 | `matchAll=true` → every query tag; `false` → any | explicit | match-all test green |
| R3 | Page offset `(page-1)*size`; `TotalPages` ceiling, empty → 0 | explicit | both paging tests green |
| R4 | `GetStorageReport` full spec | explicit | storage test + edge tests green |
| R5 | Checksum blank → `ArgumentException`; dup compare case-insensitive | explicit | `"AA11"` after `"aa11"` throws duplicate |
| R6 | Tags trimmed, blanks dropped | explicit | `" Beach "` → `beach` |
| R7 | Behavior-named tests for tasks 2 + 3 | explicit | new tests green |
| R8 | Public signatures / record shapes unchanged | implied (hidden tests compile) | grader project builds |

## Assumptions
- A1: Where case-insensitivity lives? → `InMemoryAssetRepository.FindByChecksum` with `StringComparison.OrdinalIgnoreCase`; stored checksum keeps original casing (smallest change, record shape intact).
- A2: Null `fileName`/`checksum` exception? → plain `ArgumentException`, never `ArgumentNullException` (xUnit `Assert.Throws<T>` is exact-type).
- A3: Upload validation order? → fileName, checksum, size, then duplicate lookup (invalid input fails before repo access).
- A4: Extension parse? → custom `LastIndexOf('.')`: dot at index 0, at end, or absent → `(none)`; else lowercase. Not `Path.GetExtension` (returns `.gitignore` for `.gitignore`).
- A5: String ordering? → `StringComparer.Ordinal` on every text `ThenBy` (LINQ default is culture-sensitive; spec says ordinal).

Other defaults: null tag item dropped like blank, null tags collection → empty, negative size keeps `ArgumentOutOfRangeException`, `DuplicateAssetException.Checksum` = incoming checksum, asset with extra tags still matches `matchAll`, `_nextId` stays in service.

## Files
| File | Action | Why |
|---|---|---|
| `src/AssetLibrary/AssetService.cs` | change | matchAll, paging, report, checksum validation |
| `src/AssetLibrary/InMemoryAssetRepository.cs` | change | case-insensitive lookup (A1) |
| `src/AssetLibrary/TagNormalizer.cs` | change | trim, drop blanks/nulls (step 1: query uses same normalizer) |
| `tests/AssetLibrary.Tests/SearchByTagsEdgeCaseTests.cs`, `TagNormalizerTests.cs`, `PagingTests.cs`, `StorageReportTests.cs`, `UploadChecksumTests.cs` | added | per-step tests (separate files so parallel patches merge cleanly) |
| `src/AssetLibrary/StorageReportCalculator.cs` | added | pure core for `GetStorageReport` |
| `tests/AssetLibrary.Tests/AssetServiceTests.cs` | add only | R5, R4, R6 tests; existing tests untouched |
| `SOLUTION.md`, `PROMPT-LOG.md` | create | graded write-up + log |
| records, `IAssetRepository.cs`, `*.csproj`, grader folder | leave alone | frozen shapes / hidden tests |

Test command: `dotnet test tests/AssetLibrary.Tests`

## Edge cases
| Case | Expected | Req | Likely hidden |
|---|---|---|---|
| Upload `"AA11"` after `"aa11"` | `DuplicateAssetException`, `ExistingId=1` | R5 | yes |
| Checksum `""`, `"  "`, `null` | `ArgumentException` (A2) | R5 | yes |
| Tags `" Beach "`, `"  "`, `""` | `["beach"]` | R6 | yes |
| `backup.tar.gz` / `README` / `draft.` / `.gitignore` / `PHOTO.JPG` | `gz` / `(none)` ×3 / `jpg` | R4 | yes |
| `topN` -1 / 0 / 99 with 3 assets | `ArgumentOutOfRangeException` / `[]` / all 3 | R4 | yes |
| Sizes 2,000,000,000 ×2 | `TotalBytes` 4,000,000,000 (`long`) | R4 | yes |
| Empty library: report, `GetPage(1,10)` | 0, 0, `[]`, `[]`; items `[]`, `TotalPages` 0 | R3, R4 | yes |
| `GetPage(4,2)` on 5 assets | `[]`, `TotalCount` 5, `TotalPages` 3 | R3 | yes |
| Equal size `B.jpg` vs `a.jpg`; equal ext bytes | `B.jpg` first (ordinal), then id; ext ordinal asc | R4 | yes |
| Search with only blank query tags / empty query, both `matchAll` | `[]` (guard stops vacuous `All`) | R2 | yes |
| `matchAll`, asset `{beach,sunset,city}` vs `{beach}`, query `[beach,sunset]` | `[1]` (catches reversed `asset.Tags.All`) | R2 | yes |
| `matchAll`, asset with no tags, query `[beach]` | not returned | R2 | maybe |
| `matchAll`, query `[Beach,BEACH,beach]` | asset with `{beach}` matches | R2 | maybe |
| Query `"  Beach "`, both `matchAll` | `[1]` (trim applies to query too) | R2, R6 | yes |
| Repo returns assets out of order | ids ascending (fake repo, InMemory hides it) | R2 | maybe |
| Query `[null, beach]` / `null` collection | `[1]` / `[]` (assumed) | R2 | maybe |

## Plan
| Step | Change | Min | Done when | Status |
|---|---|---|---|---|
| 1 | `SearchByTags`: honor `matchAll` (`wanted.All` vs `wanted.Any`); `TagNormalizer`: trim, drop blank/null items, null collection → empty | 6 | match-all test + `SearchByTagsEdgeCaseTests` green | [x] |
| 2 | `GetPage`: `(page-1)*size` offset, ceiling `TotalPages` | 5 | both paging tests green | [x] |
| 3 | `GetStorageReport` per spec (A4, A5) | 15 | visible 8/8 | [x] |
| 4 | Checksum: blank → `ArgumentException`; case-insensitive lookup | 6 | ticket repro test green | [x] |
| 5 | Behavior-named tests for steps 3–4 | 8 | new tests green | [x] |
| 6 | `SOLUTION.md` (≤250 words) + finish `PROMPT-LOG.md` | 5 | files written | [x] |
| 7 | Remaining edge tests (upload tag trim, paging, report) | 2 | all green | [x] |

Budget: 47 of 55 min (recon used 8), review included. Cut line: below step 6 — step 7 dropped first (tests only; trim already shipped in step 1).

## Step 1 prompt
```
Read src/AssetLibrary/AssetService.cs, src/AssetLibrary/TagNormalizer.cs, the test
Search_with_match_all_returns_only_assets_having_every_tag and
tests/AssetLibrary.Tests/SearchByTagsEdgeCaseTests.cs.
First explain in one sentence why that test returns [1, 2]. Then propose a diff that
changes only SearchByTags and TagNormalizer.Normalize so that:
- matchAll=true  -> wanted.All(tag => asset.Tags.Contains(tag)), not asset.Tags.All(...)
  (extra asset tags allowed, an asset with no tags never matches)
- matchAll=false -> wanted.Any(tag => asset.Tags.Contains(tag))
- Normalize: null collection -> empty; drop null items; Trim, then lowercase invariant,
  drop blanks, distinct
Keep: the empty-normalized-query guard before filtering (else All returns every asset),
ordering by Id ascending, public signatures, no new dependencies, no comments,
do not edit tests.
Show the diff only; I will run `dotnet test tests/AssetLibrary.Tests`.
```
