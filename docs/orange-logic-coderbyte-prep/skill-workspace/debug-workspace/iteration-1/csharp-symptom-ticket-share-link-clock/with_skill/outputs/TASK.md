# Task 2 - Bugfix: support ticket

> "Our Ho Chi Minh City office says share links stop working the moment they are created.
> At the same time a customer in Seattle reports that a link we set to expire after 1 hour
> still opened the next morning."

Find and fix the cause. Keep public signatures unchanged. Add tests that prove the fix.

## Spec

- `Create(assetId, lifetime)`: `lifetime` must be positive, else `ArgumentOutOfRangeException`. The link expires at creation time + lifetime.
- `IsValid(token)`: true only for a known token that has not expired.
- `ActiveLinksFor(assetId)`: the asset's links that have not expired, oldest first.
- `PurgeExpired()`: removes expired links and returns how many were removed.
- All times are UTC and come from the injected `IClock`.
