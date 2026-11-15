# Prompt log

One line per prompt: what I sent, what came back, what I did with it (kept / rejected / changed) and why.

## 0. Recon (before any code)

**Prompt:** `/coderbyte-prep:task-recon` on `TASK.md` + file tree. _(Paste the exact recon prompt from the earlier session here.)_
**Result:** `PLAN.md`: requirements R1–R8, assumptions A1–A5, edge-case table, 7 timed steps, cut line.
**Decision:** kept. Assumptions fixed before coding: case-insensitive checksum compare in the repository (A1), plain `ArgumentException` for null checksum (A2), custom `LastIndexOf('.')` instead of `Path.GetExtension` (A4), ordinal string ordering (A5).

## 1. Edge cases for the first requirement (search)

**Prompt:** `/coderbyte-prep:edge-case-tests review the current PLAN.md of the first requirements`
**Result:** 10-row table for `SearchByTags`, new `SearchByTagsEdgeCaseTests.cs` (14 cases); 6 red on the untouched code (matchAll ignored, query not trimmed, null tags throw).
**Decision:** kept the tests. **Changed the plan:** the review showed that trimming was scheduled in Step 7 (first to be cut), but the spec says query tags are normalized like upload tags, so cutting Step 7 would also fail hidden search tests.

## 2. Plan correction

**Prompt:** `Yes, update plan`
**Result:** Step 1 now also covers `TagNormalizer` trim + null handling (4 → 6 min), Step 7 reduced to tests only (4 → 2 min), edge-case table and Step 1 prompt updated (explicitly `wanted.All(...)`, not `asset.Tags.All(...)`; keep the empty-query guard).
**Decision:** kept. Budget unchanged at 47 min.

## 3. Implementation, test-first, one agent per step

**Prompt:** `/coderbyte-prep:tdd-implement based on the PLAN.md. Start the implementation by using the subagent for each tasks. We could do in parallel if we can`
**Rejected approach:** four agents editing the same folder in parallel. Steps 1–4 all touch `AssetService.cs` and share one `bin/obj`, so builds would collide and red/green evidence would be unreliable.
**Changed to:** each agent works in its own copy, writes failing tests first, returns a patch; I apply the four patches in order and run the full suite. New tests go in separate files so the patches merge cleanly.

Per-step briefs (summary of what each agent was told):

| Step | Brief | Red seen before fix | Outcome |
|---|---|---|---|
| 1 | `SearchByTags`: `wanted.All` / `wanted.Any`; `TagNormalizer`: null-safe, trim, lowercase, drop blanks | visible match-all test returned `[1, 2]`; trim tests returned `[]`; null tag → `NullReferenceException` | kept |
| 2 | `GetPage`: `(page-1)*size`, ceiling `TotalPages`, no int overflow | `[3, 2]` instead of `[5, 4]`; `TotalPages` 2 instead of 3; `GetPage(int.MaxValue, 2)` returned page 1 (offset wrapped to -2) | kept |
| 3 | `GetStorageReport` as thin shell over pure `StorageReportCalculator` | `NotImplementedException` from a stub | kept |
| 4 | Ticket: case-insensitive duplicate check; blank checksum → `ArgumentException` | `"AA11"` after `"aa11"`: no exception thrown | kept |

Corrections during merge:
- Agents reported that `git add -A` staged `bin/` and `obj/` build output (no `.gitignore`); patches restricted to source files. **Changed.**
- `patch` left `AssetService.cs.orig` after applying with offsets; deleted. **Changed.**
- Step 4 agent flagged that the case-insensitive compare lives only in `InMemoryAssetRepository`. Considered moving it to the service (store lowercased checksum or scan `All()`). **Rejected:** lowercasing changes stored data and the returned `Asset.Checksum`; scanning `All()` drops the repository lookup. Kept A1.

Result after merge: `dotnet test tests/AssetLibrary.Tests` → 61 passed, 0 failed, 0 build warnings.

## 4. Review of the merged diff

**Prompt:** `Run ai-diff-review`
**Result:** ACCEPT, no blocker or major. Traced overflow (`GetPage(int.MaxValue, 2)` → empty), `TotalBytes` 4,000,000,000, ordinal tie `B.jpg` before `a.jpg`, `"AA11"` after `"aa11"` → duplicate id 1. Existing tests byte-for-byte unchanged.
**Decision:** kept. Noted limits: equal-bytes extension test would also pass with culture ordering (the `B.jpg`/`a.jpg` test is the one proving ordinal); trailing space in file name gives extension `"jpg "` (spec silent).

## 5. Write-up

**Prompt:** `Yes, write them`
**Result:** this log and `SOLUTION.md`.
**Decision:** kept after checking the word count and that every claim matches the test run.
