# Drill 1 Rubric (spoilers)

## Grade

```bash
dotnet test docs/orange-logic-coderbyte-prep/drills/csharp-asset-library-grader/AssetLibrary.GraderTests
```

Or tell Claude: "grade my drill" → runs hidden + visible tests, reviews `PROMPT-LOG.md`, `SOLUTION.md`, `ANSWERS.md`.

## Planted defects

| # | Where | Defect | Caught by |
|---|---|---|---|
| 1 | `AssetService.SearchByTags` | `matchAll` ignored (always Any) | visible |
| 2 | `AssetService.GetPage` | `Skip(pageNumber * pageSize)` off by one page | visible |
| 3 | `AssetService.GetPage` | `TotalPages` integer division, no ceiling | visible |
| 4 | `InMemoryAssetRepository.FindByChecksum` | case-sensitive compare | support ticket + hidden |
| 5 | `AssetService.Upload` | blank checksum accepted | spec + hidden |
| 6 | `TagNormalizer.Normalize` | no trim, whitespace tags kept | spec + hidden |
| 7 | `GetStorageReport` | not implemented; traps: dot-files, trailing dot, `tar.gz`, tie order, `topN` 0 / negative, long sum | visible + hidden |

AI traps to notice: agent "fixing" #4 by lowercasing on save only (breaks stored original), changing tests instead of code, using `Path.GetExtension` (returns `.gitignore` for dot-files), casting sizes to `int`, sorting with culture comparer.

## Score (10 pts, like Coderbyte)

| Part | Pts | Full marks when |
|---|---|---|
| Hidden + visible tests | 5 | all pass (pro-rata) |
| Prompt log | 2 | recon prompt before code; small scoped prompts; ≥1 rejection/correction with concrete reason |
| Own tests | 1 | cover ticket + report edge cases, behavior names |
| SOLUTION.md | 1 | kept/rejected/changed, verification, limits; ≤250 words |
| Section 2 | 1 | each answer cites a real example from session |

8+ ready. 6–7 tighten workflow. <6 redo drill with timer.

Reference fix: `reference-solution/` (copy over `src/AssetLibrary/` to check grader is green).
