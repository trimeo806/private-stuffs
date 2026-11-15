**For the prompt log (rejection):** Rejected the agent's edit to `UploadService.Upload`. The case-insensitive checksum comparison (`StringComparison.OrdinalIgnoreCase`) was correct, but the "tidied up validation" broke the build. It calls `string.IsNullOrBlank`, which does not exist in .NET (CS0117). It also changed `sizeBytes` from `long` to `int` without being asked, which changes a public signature and breaks files over 2 GB. TASK.md says not to change public signatures and to support files larger than 2 GB. Keep only the comparison change.

## Diagnosis: 1 build break, 2 root causes (the second is hidden behind the first)

**Reproduced:** `dotnet test tests/Uploads.Tests` -> `UploadService.cs(9,20)` and `(10,20)`: `error CS0117: 'string' does not contain a definition for 'IsNullOrBlank'`. Repo left unchanged. I ran the probes below on a scratch copy.

| # | Failure(s) | Root cause (file:line) | Proof: input -> actual vs expected |
|---|---|---|---|
| 1 | CS0117 x2 | `src/Uploads/UploadService.cs:9-10`: the agent invented the API `string.IsNullOrBlank`. The original code used `string.IsNullOrWhiteSpace`. | As shipped: CS0117 on both lines. Scratch copy with `IsNullOrWhiteSpace`: the main project compiles. |
| 2 | CS1503 at `UploadServiceTests.cs:41,42` (hidden until #1 is fixed) | `src/Uploads/UploadService.cs:7`: the parameter is now `int sizeBytes` instead of `long`. This changes the public signature against the spec, and `StoredFile.SizeBytes` is still `long`. | Scratch copy with #1 fixed: `Upload("movie.mp4","f00d",3_000_000_000L)` -> `cannot convert from 'long' to 'int'`. Expected: compiles and `TotalBytes()==4_000_000_000`. With `long` restored: 4/4 tests pass. |

**Trap:** Don't fix #1 alone and don't "fix" the test by shrinking the sizes or casting to `int`. That hides the 2 GB requirement and any hidden test that calls `Upload(..., long)`. Also don't revert the whole diff, because the `OrdinalIgnoreCase` line on line 13 is the actual ticket fix.
**Not verified:** No existing test covers mixed-case duplicates, so the ticket fix itself has no test yet.

**Fix prompts, one per cause, send in order, run tests between:**

1. > In `src/Uploads/UploadService.cs` lines 9-10, `string.IsNullOrBlank` does not exist (CS0117). Replace both calls with `string.IsNullOrWhiteSpace`. Leave the messages, the `nameof` arguments and the `OrdinalIgnoreCase` comparison on line 13 unchanged. Do not touch the tests. Then run `dotnet test` and show me the next error.
2. > In `src/Uploads/UploadService.cs:7`, change `int sizeBytes` back to `long sizeBytes`. TASK.md forbids changing public signatures and requires files larger than 2 GB. `TotalBytes_SupportsFilesLargerThanTwoGigabytes` must compile and pass unchanged. First add the test `Upload_SameChecksumDifferentCase_ThrowsDuplicate`: upload `("a.jpg","ABC123",10)`, then `("b.jpg","abc123",10)`, and expect `DuplicateFileException` with `ExistingId` equal to the first id. Do not edit existing tests, `StoredFile` or packages.
