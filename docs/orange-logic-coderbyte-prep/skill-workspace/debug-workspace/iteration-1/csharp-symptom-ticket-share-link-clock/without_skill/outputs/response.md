**Root cause:** `ShareLinkService.IsValid` and `ShareLinkService.ActiveLinksFor` check expiry against `DateTime.Now`, the machine's local time. They should use the injected `_clock.UtcNow`.

- `src/Sharing/ShareLinkService.cs:26`: `return DateTime.Now < link.ExpiresAtUtc;`
- `src/Sharing/ShareLinkService.cs:31`: `.Where(l => l.AssetId == assetId && l.ExpiresAtUtc > DateTime.Now)`

`Create` and `PurgeExpired` already use `_clock.UtcNow`, so only these two read paths are wrong.

**How this causes both reports:** `DateTime` comparisons ignore `Kind`, so local time gets compared directly with a UTC expiry.
- Ho Chi Minh City is UTC+7. Local time is 7 hours ahead of UTC, so a link with a lifetime under 7h already looks expired when it is created. Links "stop working the moment they are created".
- Seattle is UTC-8 (PST in November). Local time is 8 hours behind UTC, so a 1h link stays valid for about 9h. It "still opened the next morning".

**Why the 4 visible tests are green:** none of them calls `IsValid` on a real token or calls `ActiveLinksFor`. The only time-based test covers `PurgeExpired`, which uses the clock correctly.

**Proof:** I added 3 tests to `tests/Sharing.Tests/ShareLinkServiceTests.cs`. They all fail on the current code (`dotnet test`: 3 failed, 4 passed):
- `IsValid_RightAfterCreate_ReturnsTrue`: the clock is set to 2026-06-01, and the call returns `false`.
- `IsValid_AfterLifetimeElapsed_ReturnsFalse`: the clock is set to 2099, advanced past expiry, and the call still returns `true`.
- `ActiveLinksFor_UsesInjectedClock`: it expects only the 2h link after 1h, and gets an empty list.

They use a `FakeClock` set far from today's date, so they give the same result in any timezone, on any machine and in CI.

I did not change any source code.

**What to ask the AI panel for:** in those two methods, replace `DateTime.Now` with `_clock.UtcNow`. That's the whole fix. Any `DateTime.Now` or `DateTime.UtcNow` is wrong here, because the spec says all times come from the injected `IClock`. When reviewing its answer:
- Reject a `DateTime.UtcNow` "fix". It fixes production, but it still ignores the clock, so the 2099 test keeps failing.
- Keep the expiry boundary consistent. `PurgeExpired` treats `ExpiresAtUtc <= now` as expired, so `IsValid` should use `now < ExpiresAtUtc`, which is what `IsValid_AfterLifetimeElapsed_ReturnsFalse` checks.
