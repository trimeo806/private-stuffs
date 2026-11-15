Both reports in the ticket come from one cause. `IsValid` and `ActiveLinksFor` compare against `DateTime.Now`, which is local time. The expiry they compare it to is stored in UTC, and both methods skip the injected `IClock`. I proved it by running the code under each office's time zone. I haven't changed any production code. I added five tests to the existing test file and left them failing on purpose; they are the failing tests the fix has to make pass.

## Diagnosis: share links expire too early in UTC+ zones and too late in UTC- zones

**Reproduced:**
- The 4 original tests pass. They never call `IsValid` on a live or expired link, and never call `ActiveLinksFor`.
- I wrote a throwaway test using the real `SystemClock` and ran it under three time zones (it's deleted now). It creates a 1-hour link, then calls `IsValid` straight away:
  - `TZ=Asia/Ho_Chi_Minh`: `IsValid = False`, and `ActiveLinksFor` returns 0 links. The link is dead the moment it's created.
  - `TZ=America/Los_Angeles`: `IsValid = True`. Local time read 06:09 while the expiry was 15:09Z, so the link stays valid until 15:09 *local*, which is 9 hours instead of 1.
  - `TZ=UTC`: `IsValid = True`. That's correct, which is why nobody sees the bug in UTC.
- `dotnet test` with my new tests: **5 failed, 4 passed** (details below).

**Requirement (TASK.md):** "All times are UTC and come from the injected `IClock`". `IsValid` is true only for a known token that has not expired. `ActiveLinksFor` returns the asset's links that have not expired, oldest first.

| # | Failure(s) | Root cause (file:line) | Evidence: input -> actual vs expected | Confidence |
|---|---|---|---|---|
| 1 | Ticket HCMC "links stop working the moment they are created"; Ticket Seattle "1-hour link still opened next morning"; `IsValid_BeforeExpiry_ReturnsTrue`, `IsValid_AfterExpiry_ReturnsFalse`, `IsValid_AtExactExpiry_ReturnsFalse`, `ActiveLinksFor_UnexpiredLink_IsReturned`, `ActiveLinksFor_ExcludesExpiredLinks_OldestFirst` | `src/Sharing/ShareLinkService.cs:26` (`DateTime.Now < link.ExpiresAtUtc`) and `:31` (`l.ExpiresAtUtc > DateTime.Now`) compare local wall-clock time with a UTC value. `DateTime` comparison ignores `Kind` and only compares ticks, so the error equals the UTC offset. Both lines also skip `_clock`, so a fake clock has no effect on them. | Under HCMC (UTC+7), a 1h link reads as already expired at creation. Under LA (UTC-8), it stays valid for 9h. With a FakeClock at 2026-06-01 09:00Z and 30 minutes elapsed, `IsValid` returns False (expected True). With a FakeClock at 2030-01-01 and 2h elapsed, `IsValid` returns True (expected False). `ActiveLinksFor` returns 3 links where 2 are expected, and returns `[]` for a live link. | proven |

**Symptom site vs cause site:**
- The ticket describes what customers see when they open a link. That's `IsValid`, line 26.
- The same mistake is copied into `ActiveLinksFor`, line 31.
- `Create` (line 17) and `PurgeExpired` (line 38) already use `_clock.UtcNow` and are correct.
- If the fix touches only `IsValid`, or swaps in `DateTime.UtcNow`, the ticket gets closed but problems remain:
  - Hidden tests that use a `FakeClock` on `ActiveLinksFor` would still fail.
  - `DateTime.UtcNow` would still ignore the injected clock. The spec says time comes from `IClock`, and a fake clock is the only way to test expiry.

**Blast radius:**
- **Any short lifetime in UTC+ zones.** In HCMC, any link with a lifetime of 7h or less is dead on creation. Any longer link loses 7h. In Seattle every link gets 8h extra (7h during summer time, PDT).
- **The asset's share list.** `ActiveLinksFor` shows an empty list in Asia and lists expired links in America.
- **The three methods disagree with each other.** `PurgeExpired` uses the clock correctly. So in Seattle, a link can be purged by `PurgeExpired` while `ActiveLinksFor` called just before it still listed the link as active. In HCMC, a link that `IsValid` rejects stays in the store until its real UTC expiry.
- **The boundary at exactly `ExpiresAtUtc`.** The current operators (`<` in `IsValid`, `>` in `ActiveLinksFor`, `<=` in `PurgeExpired`) already agree that a link is expired at that instant. The fix must keep those operators. `IsValid_AtExactExpiry_ReturnsFalse` pins this.
- **Stored data is fine.** `CreatedAtUtc` and `ExpiresAtUtc` are written correctly from the clock, so no data needs migrating.
- **No other call sites.** I searched for `DateTime.Now` and `DateTime.UtcNow` in `src`. The only hits are lines 26 and 31, plus `SystemClock` (correct).

**Ruled out:**
- **Wrong expiry calculation in `Create`.** `Create_SetsExpiryFromClock` passes, and line 18 is `now.Add(lifetime)` from the clock.
- **The lifetime guard.** The `<= TimeSpan.Zero` check matches the spec.
- **Token lookup.** The dictionary uses `StringComparer.Ordinal` and the GUIDs are lowercase `N` format, so lookups work.
- **Ordering in `ActiveLinksFor`.** It sorts by `CreatedAtUtc` ascending, which is correct. The failing assertion is caused only by the expired filter.

**Fix direction (for tdd-implement):** In `ShareLinkService`, replace `DateTime.Now` with `_clock.UtcNow` on line 26 (`IsValid`) and line 31 (`ActiveLinksFor`). Read the clock once per call, before the LINQ query, the same way `Create` does. Keep these unchanged:
- every public signature and the `ShareLink` record
- the existing comparison operators, so a link is expired at exactly `ExpiresAtUtc`
- `PurgeExpired`
- the 4 original tests

Do not use `DateTime.UtcNow` directly, and do not convert with `ToUniversalTime()`. Either one fixes the time zone but still bypasses `IClock`.

**Failing tests already written (red), in `tests/Sharing.Tests/ShareLinkServiceTests.cs`:**
- `IsValid_BeforeExpiry_ReturnsTrue`: clock at 2026-06-01 09:00Z, 1h link, 30 minutes elapsed. Expect true; currently false.
- `IsValid_AfterExpiry_ReturnsFalse`: clock at 2030-01-01 09:00Z, 1h link, 2h elapsed. Expect false; currently true.
- `IsValid_AtExactExpiry_ReturnsFalse`: 1h link, exactly 1h elapsed. Expect false; currently true.
- `ActiveLinksFor_ExcludesExpiredLinks_OldestFirst`: a 30-minute link, then a 3h link, then a 2h link on asset 7, plus one link on asset 8, then 1h elapsed. Expect the asset 7 tokens `[older, newer]`; currently returns 3 tokens.
- `ActiveLinksFor_UnexpiredLink_IsReturned`: a fresh 1h link. Expect `[token]`; currently `[]`.

The tests use two fake-clock dates, one in the past and one in the future (2030). That way the tests fail on any machine in any time zone while the bug is there. After the fix they depend only on the fake clock.

**Not verified:**
- I didn't check the HCMC/Seattle timing against real customer timestamps. I reproduced it with `TZ=` on this machine (November, so Los Angeles is on PST, UTC-8).
- No fix was applied, so the 5 new tests have not been seen passing yet.

**Question for you:** do you want a time-zone-specific regression test kept in the suite as well? It would set `TZ` or use `SystemClock`. I left it out because time-zone tests are flaky in CI, and the fake-clock tests already fail without it.

**In-IDE fix prompt:**
> Root cause: src/Sharing/ShareLinkService.cs:26 (IsValid) and :31 (ActiveLinksFor) compare DateTime.Now (local time) against the UTC ExpiresAtUtc and bypass the injected IClock. In UTC+7 a 1-hour link is already expired at creation, and in UTC-8 it lives 9 hours. The same cause makes ActiveLinksFor return an empty list or include expired links, and makes these two methods disagree with PurgeExpired, which already uses _clock.UtcNow. Five failing tests are already in tests/Sharing.Tests/ShareLinkServiceTests.cs: IsValid_BeforeExpiry_ReturnsTrue, IsValid_AfterExpiry_ReturnsFalse, IsValid_AtExactExpiry_ReturnsFalse, ActiveLinksFor_ExcludesExpiredLinks_OldestFirst, ActiveLinksFor_UnexpiredLink_IsReturned. Run dotnet test and confirm they are red. Then fix the cause by replacing DateTime.Now with _clock.UtcNow in IsValid and ActiveLinksFor, reading the clock once into a local before the comparison or query. Do not use DateTime.UtcNow or ToUniversalTime(). Keep the existing comparison operators (a link is expired at exactly ExpiresAtUtc), every public signature, the ShareLink record, PurgeExpired, and all existing tests and assertions unchanged, and add no packages. Show me the diff and the dotnet test result (expect 9 passed).
