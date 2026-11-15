# Drill 1 — Asset Library (C#, AI fluency, open project)

**Time: 80 min total. Section 1 = 55 min max, then Section 2 (`SECTION-2.md`) = 20 min.**
Do not open `../csharp-asset-library-grader/` until done: it holds hidden tests + reference solution.

## Rules (simulate Coderbyte)

- Use one AI assistant as your "in-IDE AI" (Claude Code, Copilot Chat, ChatGPT…). Treat its chat as the graded prompt log.
- Keep `PROMPT-LOG.md` in this folder: paste every prompt you send + one line on what you did with the answer (kept / rejected / changed + why). Graders read this.
- Run tests often: `dotnet test tests/AssetLibrary.Tests`
- Use your skills: `/coderbyte-prep:task-recon` → `/coderbyte-prep:tdd-implement` → `/coderbyte-prep:ai-diff-review` → `/coderbyte-prep:edge-case-tests` → `/coderbyte-prep:ownership-writeup`.
- Hidden tests check behaviors in the spec below that visible tests do not.

## Context

`AssetLibrary` is a tiny in-memory digital asset library: upload files with tags, search by tag, browse pages, report storage use. Code in `src/AssetLibrary/`, tests in `tests/AssetLibrary.Tests/`. Do not change public signatures or record shapes.

## Tasks

1. **Bugfix: failing tests.** Some visible tests fail. Fix the code, not the tests.
2. **Bugfix: support ticket.** "Customers upload the same file twice and both copies are stored. Our client sends checksums in uppercase, the web uploader in lowercase."
3. **Feature: `AssetService.GetStorageReport(int topN)`** per spec below.
4. **Tests**: add tests that prove tasks 2 and 3, named by behavior.
5. **README**: add `SOLUTION.md` (max 250 words): approach, how you used AI (kept / rejected / changed), verification, known limits.

## Spec

### Upload
- `fileName` required (not blank). `checksum` required (not blank) → `ArgumentException`.
- `sizeBytes` ≥ 0.
- Checksums compare **case-insensitively**. Duplicate → `DuplicateAssetException` with existing id.
- Tags: trimmed, lowercased, blanks dropped, distinct.

### SearchByTags(tags, matchAll)
- Query tags normalized same as upload tags.
- No usable query tags → empty result.
- `matchAll = true` → asset must have every tag; `false` → at least one.
- Result ordered by id ascending.

### GetPage(pageNumber, pageSize)
- 1-based `pageNumber`; both must be ≥ 1.
- Order: newest `UploadedAt` first, ties by id ascending.
- `TotalPages` = ceiling(TotalCount / pageSize); empty library → 0.
- Page past the end → empty `Items`, totals still correct.

### GetStorageReport(topN)
- `topN < 0` → `ArgumentOutOfRangeException`. `topN = 0` → empty `Largest`. `topN > count` → all.
- `TotalBytes` = sum of sizes (may exceed `int.MaxValue`). `AssetCount` = number of assets.
- `ByExtension`: one row per extension, lowercase, no dot, last segment only (`backup.tar.gz` → `gz`). No extension (`README`), trailing dot (`draft.`), or dot-file (`.gitignore`) → `"(none)"`. Ordered by `TotalBytes` desc, then extension ordinal asc.
- `Largest`: top `topN` by `SizeBytes` desc, ties by `FileName` ordinal asc, then id asc.
- Empty library → zeros and empty lists.

## Done when

- [ ] All visible tests pass
- [ ] New tests for tasks 2 and 3 pass
- [ ] `PROMPT-LOG.md` shows recon prompt, ≥1 rejected/corrected AI output with reason
- [ ] `SOLUTION.md` written
- [ ] Moved to `SECTION-2.md` by minute 55
