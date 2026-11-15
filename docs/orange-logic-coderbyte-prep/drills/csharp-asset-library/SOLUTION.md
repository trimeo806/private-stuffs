## Approach
I planned first: spec rules, assumptions, edge cases, timed steps. Then each step test-first: failing test, smallest fix, full suite. Storage report is a thin service method over a pure `StorageReportCalculator`, so tie-breaks are testable without the repository.

## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| Edge cases for search | Changed plan: trimming moved into search step | It sat in the step I would cut first; query tags share the rule |
| Implement four steps in parallel | Changed: isolated copies returning patches | All edit `AssetService.cs`; shared builds would collide |
| Duplicate-check fix | Rejected moving compare into service | Lowercasing changes stored checksums |
| Paging fix | Kept, with overflow test | `GetPage(int.MaxValue, 2)` returned page 1 before |

## Verification
- `dotnet test tests/AssetLibrary.Tests`: 61 passed, 0 failed, 0 warnings. Existing tests unchanged.
- Covered: case-variant duplicates, blank checksums, `TotalBytes` above `int.MaxValue`, `B.jpg` before `a.jpg`.
- Not verified: other repository implementations.

## Trade-offs
- Case-insensitive lookup in repository over service: keeps original checksum casing.

## Known limits
- Checksums and file names not trimmed (spec silent).
- Lookups scan all assets -> index at scale.
