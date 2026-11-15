## Diagnosis: 2 ticket symptoms, 1 root cause

**Reproduced:** I added 3 tests to `ShareLinkServiceTests.cs`, then ran `dotnet test` in `tests/Sharing.Tests`. Result: 3 failed, 4 passed. All 3 failures are the new tests, and they stay red on purpose. Production code is untouched.

Ticket case: a 1h link is created at 09:00 UTC. At 09:59 UTC it should be valid, and at 10:00 UTC it should be invalid. In HCMC (UTC+7) it is already invalid at 09:00. In Seattle (UTC-8) it stays valid until 18:00 UTC.

| # | Failure(s) | Root cause (file:line) | Proof: input -> actual vs expected |
|---|---|---|---|
| 1 | HCMC: links dead on creation. Seattle: 1h link still opens next morning | `src/Sharing/ShareLinkService.cs:26` (`IsValid`) compares `DateTime.Now` (local time, not the injected clock) with `ExpiresAtUtc`. The local UTC offset shifts the expiry: -7h in HCMC, +8h in Seattle. The same bug is at `:31` (`ActiveLinksFor`). `Create` (`:17`) and `PurgeExpired` (`:38`) correctly use `_clock.UtcNow`, so the 4 existing tests never call the broken lines. | `IsValid_BeforeExpiry_ReturnsTrue` (clock +59 min): False, expected True. `IsValid_AfterExpiry_ReturnsFalse` (clock +1h): True, expected False. `ActiveLinksFor_ExcludesExpiredAndOrdersOldestFirst`: `[]`, expected `[older, newer]`. A throwaway probe I ran, then deleted, used the real current time. Under `TZ=Asia/Saigon`, a fresh 1h link gave False. Under `TZ=America/Los_Angeles`, a link that expired 2h ago gave True. Those match the two ticket reports. |

**Trap:** Replacing `DateTime.Now` with `DateTime.UtcNow` fixes production, but it still ignores `IClock`. The new FakeClock tests stay red, and that breaks the spec rule "All times... come from the injected `IClock`". Converting with `ToLocalTime()`/`ToUniversalTime()` is also wrong.
**Not verified:** Daylight-saving transitions are not checked. They are not needed once nothing reads local time.

**Fix prompt (one cause), send it and then run `dotnet test`:**

1. > In `src/Sharing/ShareLinkService.cs`, `IsValid` (line 26) and `ActiveLinksFor` (line 31) read `DateTime.Now`. That is local time, and it is compared with `ExpiresAtUtc`. Make the red tests `IsValid_BeforeExpiry_ReturnsTrue`, `IsValid_AfterExpiry_ReturnsFalse` and `ActiveLinksFor_ExcludesExpiredAndOrdersOldestFirst` pass. Replace both reads with `_clock.UtcNow`. Do not use `DateTime.UtcNow` or `ToLocalTime`. Keep the `>`/`<` comparisons consistent with `PurgeExpired`. Do not change signatures, `ShareLink`, `IClock`, or the existing tests.
