**For the prompt log (rejection):** Rejected the agent's edit to `UploadService.cs`. The case-insensitive comparison (line 13, `StringComparison.OrdinalIgnoreCase`) is correct, but the "tidy-up" of validation broke the build twice: it calls `string.IsNullOrBlank`, which does not exist in .NET (CS0117), and it changed `Upload`'s `sizeBytes` from `long` to `int`, which changes a public signature (TASK.md forbids this) and breaks the spec that files can be larger than 2 GB (CS1503 in the test project). The agent also added no test for the case-insensitive duplicate. Keeping only the comparison change; reverting the rest.

## Diagnosis: build broken, 2 root causes

**Reproduced:** `dotnet test tests/Uploads.Tests` -> 2x CS0117 `'string' does not contain a definition for 'IsNullOrBlank'` (UploadService.cs:9,10). The test project never compiled, which hid the second error. On a scratch copy (repo untouched) with only that call reverted: 2x CS1503 `cannot convert from 'long' to 'int'` (UploadServiceTests.cs:41,42). With both reverted plus a scratch case test: 5/5 pass.

| # | Failure(s) | Root cause (file:line) | Proof: input -> actual vs expected |
|---|---|---|---|
| 1 | CS0117 x2 | `UploadService.cs:9-10`: `string.IsNullOrBlank` is a hallucinated API; the original `IsNullOrWhiteSpace` was right | compile -> CS0117 vs builds |
| 2 | CS1503 x2 (hidden behind #1) | `UploadService.cs:7`: `int sizeBytes` narrowed from `long`. Breaks the public signature and the >2 GB rule. `StoredFile` and `TotalBytes()` are still `long`, so it is the only site | `Upload("movie.mp4","f00d",3_000_000_000L)` -> CS1503 vs `TotalBytes()==4_000_000_000` |

The duplicate fix itself works: `"ABC123"` then `"abc123"` -> `DuplicateFileException` with id 1 (scratch test, passed).

**Trap:** For #1, `IsNullOrEmpty` or a homemade `IsNullOrBlank` extension. `IsNullOrEmpty` lets `"   "` through and fails `Upload_BlankChecksum_Throws`. For #2, casting or changing the test values to fit `int`, or making `StoredFile.SizeBytes` an `int`. That compiles, but it breaks the >2 GB spec, the signature rule and any hidden tests that pass a `long`.
**Not verified:** nothing.

**Fix prompts, one per cause, send in order, run `dotnet test` between:**

1. > In `src/Uploads/UploadService.cs` lines 9-10, `string.IsNullOrBlank` does not exist (CS0117). Replace both calls with `string.IsNullOrWhiteSpace`, as before your edit. Do not use `IsNullOrEmpty` (blank `"   "` must still throw, see `Upload_BlankChecksum_Throws`) and do not add an extension method. Change nothing else; the next build error (CS1503 in tests) is expected and is the next step.
2. > In `src/Uploads/UploadService.cs:7`, restore the parameter to `long sizeBytes`. The `int` change breaks the public signature (TASK.md forbids this) and `TotalBytes_SupportsFilesLargerThanTwoGigabytes`. Do not cast, do not edit test values, do not touch `StoredFile`. Then add the test `Upload_SameChecksumDifferentCase_ThrowsDuplicate`: upload `"ABC123"`, then `"abc123"` -> `DuplicateFileException` with the first id. Keep the existing `OrdinalIgnoreCase` comparison on line 13.

I have not changed the repo. Both prompts restore lines the agent rewrote without being asked. Review each diff before you accept it.
