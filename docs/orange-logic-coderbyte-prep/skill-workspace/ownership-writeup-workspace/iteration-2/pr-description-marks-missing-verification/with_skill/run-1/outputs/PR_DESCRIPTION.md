## Summary
Adds retry to `uploadToS3` in `storage/upload.ts`: 3 attempts with exponential backoff and jitter, capped at 5s per wait, with no retry on 403. [gap: why retry was needed, e.g. the failure it fixes]

## What the agent did and what I changed
- Copilot added retry to `uploadToS3` (3 attempts, exponential backoff with jitter) - kept.
- It capped the backoff delay at 30s - I changed it to 5s, because the upload dialog times out at 10s, so a 30s wait would outlast the dialog.
- It retried on 403 - I removed that. A 403 is a permissions problem: retrying cannot fix it and only delays the error.
- It renamed several variables in `storage/upload.ts` - I reverted the renames to keep the diff to the retry change.

## Verification
- Not verified: [gap: which tests or manual checks were run, and their result]
- Not verified: a transient failure that succeeds on a later attempt.
- Not verified: a 403 fails at once, with no retry.

## Risks and follow-ups
- Total time vs. the 10s dialog timeout: 3 attempts means up to 2 waits of up to 5s each, so backoff alone can reach 10s before any upload time is counted. The last attempt may run after the dialog has already timed out. Please check the base delay and whether the worst case fits in 10s.
- Please check which other errors the retry covers. Only 403 was excluded; other 4xx responses (e.g. 400, 404) would not be fixed by retrying either.
