# Drill 1 — Requirements checklist

Source: `TASK.md`, `SECTION-2.md`. No spoilers (grader folder untouched).

## Timer
- [ ] Start: ____  → S1 hard stop at +55 min: ____  → end at +80 min: ____
- No return to code after moving to Section 2.

## Rules
- One AI assistant = "in-IDE AI". Its chat is the graded prompt log.
- `PROMPT-LOG.md`: every prompt + one line: kept / rejected / changed + why.
- Run tests often: `dotnet test tests/AssetLibrary.Tests`
- Do not change public signatures or record shapes. Fix code, not tests.
- Flow: `task-recon` → `tdd-implement` → `ai-diff-review` → `edge-case-tests` → `ownership-writeup`.

## Section 1 tasks
- [ ] 1. Visible failing tests → green (fix code only)
- [ ] 2. Support ticket: same file uploaded twice stored twice; checksums arrive upper- and lowercase
- [ ] 3. Feature `AssetService.GetStorageReport(int topN)`
- [ ] 4. Tests proving tasks 2 + 3, behavior-named
- [ ] 5. `SOLUTION.md` ≤ 250 words: approach, AI use (kept / rejected / changed), verification, known limits

## Spec

### Upload
- [ ] `fileName` blank → `ArgumentException`
- [ ] `checksum` blank → `ArgumentException`
- [ ] `sizeBytes` ≥ 0
- [ ] Checksum compare case-insensitive; duplicate → `DuplicateAssetException` with existing id
- [ ] Tags: trimmed, lowercased, blanks dropped, distinct

### SearchByTags(tags, matchAll)
- [ ] Query tags normalized same as upload
- [ ] No usable query tags → empty result
- [ ] `matchAll=true` → all tags; `false` → any tag
- [ ] Ordered by id asc

### GetPage(pageNumber, pageSize)
- [ ] 1-based; both ≥ 1
- [ ] Order: newest `UploadedAt` first, ties id asc
- [ ] `TotalPages` = ceil(TotalCount / pageSize); empty library → 0
- [ ] Page past end → empty `Items`, totals still correct

### GetStorageReport(topN)
- [ ] `topN < 0` → `ArgumentOutOfRangeException`; `0` → empty `Largest`; `> count` → all
- [ ] `TotalBytes` = sum of sizes, may exceed `int.MaxValue`
- [ ] `AssetCount` = number of assets
- [ ] `ByExtension`: one row per ext, lowercase, no dot, last segment only (`backup.tar.gz` → `gz`)
- [ ] `README`, `draft.`, `.gitignore` → `"(none)"`
- [ ] `ByExtension` order: `TotalBytes` desc, then ext ordinal asc
- [ ] `Largest`: `SizeBytes` desc, ties `FileName` ordinal asc, then id asc
- [ ] Empty library → zeros + empty lists

## Section 2 — `ANSWERS.md` (20 min, 60–120 words each)
Pattern: concrete session example → action → reason → result.
- [ ] Q1 How you used AI; most useful prompt and why it worked
- [ ] Q2 One AI suggestion rejected/changed; how you detected the problem
- [ ] Q3 How you verified storage report edge cases; what you'd test with more time
- [ ] Q4 Teammate's fully AI-written PR touching pagination + duplicate detection: what you check before approving
- [ ] Q5 One risk of AI coding tools in production DAM platform + mitigation

## Done when
- [ ] All visible tests pass
- [ ] New tests for tasks 2 + 3 pass
- [ ] `PROMPT-LOG.md` shows recon prompt before code + ≥1 rejected/corrected AI output with reason
- [ ] `SOLUTION.md` written
- [ ] Moved to Section 2 by minute 55

## Score (10 pts)
Tests 5 · prompt log 2 · own tests 1 · `SOLUTION.md` 1 · Section 2 answers 1. Target ≥ 8.
