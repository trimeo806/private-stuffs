I added 3 tests to `tests/Sharing.Tests/ShareLinkServiceTests.cs`. They are red on purpose: they are the failing tests for the fix. Production code is unchanged.

## Diagnosis: 1 symptom-only ticket (2 reports), 1 root cause

**Reproduced:** `dotnet test tests/Sharing.Tests` -> 3 red, 4 green (the 3 red are the new tests). The ticket case is: 1 h link -> invalid as soon as it is created in HCMC, and still valid after it expired in Seattle. Expected: valid until creation + 1 h UTC.

| # | Failure(s) | Root cause (file:line) | Proof: input -> actual vs expected |
|---|---|---|---|
| 1 | HCMC "dead on creation", Seattle "1 h link opens next morning" | `ShareLinkService.cs:26` (`IsValid`) compares **`DateTime.Now`** (machine local time) with `ExpiresAtUtc`, and ignores the injected `IClock`. Same bug at `:31` (`ActiveLinksFor`). `PurgeExpired` (`:38`) correctly uses `_clock.UtcNow`. Local time is UTC+7 in HCMC, so every link under 7 h looks expired. Seattle is UTC-8, so links live 8 h too long. | Scratch probe with `SystemClock`, run under `TZ=Asia/Saigon`: new 1 h link -> `IsValid=False` (want True). Under `TZ=America/Los_Angeles`: link that expired 1 h ago -> `IsValid=True` (want False). New fake-clock tests: `IsValid_BeforeExpiry_ReturnsTrue` -> False, `IsValid_AfterExpiry_ReturnsFalse` -> True, `ActiveLinksFor_ReturnsOnlyUnexpiredLinks` -> `[]` (want the 2 h link). |

**Trap:** changing `DateTime.Now` to `DateTime.UtcNow` fixes production but still ignores `IClock`. The spec says time "come[s] from the injected `IClock`", so the fake-clock tests (and likely the hidden ones) stay red. Also do not fix only `IsValid`: line 31 has the same bug.
**Not verified:** nothing. Both reports were reproduced in their own time zone.

**Fix prompt, send it, then run `dotnet test`:**

1. > In `src/Sharing/ShareLinkService.cs`, `IsValid` (line 26) and `ActiveLinksFor` (line 31) use `DateTime.Now`, which is local time, so share-link expiry shifts by the server's UTC offset. Replace both with `_clock.UtcNow`, the same as `PurgeExpired`. Do not use `DateTime.UtcNow`. Make the existing red tests `IsValid_BeforeExpiry_ReturnsTrue`, `IsValid_AfterExpiry_ReturnsFalse` and `ActiveLinksFor_ReturnsOnlyUnexpiredLinks` pass. Keep the public signatures, the `<`/`>` boundary comparisons, the oldest-first ordering and all the tests unchanged. Add no packages.
