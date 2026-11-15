## Summary
Adds retry to `uploadToS3` in `storage/upload.ts`: 3 attempts with exponential backoff and jitter, delay capped at 5s, and no retry on 403. Copilot wrote the retry; I narrowed it to fit the upload dialog and kept the diff small.

## What the agent did and what I changed
- Retry with 3 attempts, exponential backoff and jitter - kept.
- Max backoff delay of 30s - changed to 5s. The upload dialog times out at 10s, so a 30s wait could never finish inside it.
- Retry on HTTP 403 - removed. A 403 is a permissions problem; retrying cannot fix it and only delays the error the user needs to see.
- Renamed several variables in `storage/upload.ts` - reverted, to keep the diff to the retry change only.

## Verification
- Not verified: [gap: which tests or manual checks were run, and their result]
- Not verified: a 403 fails on the first attempt with no retry.
- Not verified: a transient failure followed by a success uploads once and returns success.

## Risks and follow-ups
- The 5s cap does not by itself keep retries inside the 10s dialog timeout. If 3 attempts means 3 calls, there are 2 waits of up to 5s each: up to 10s of backoff alone, before counting the time of the uploads themselves. The last attempt can land after the dialog has already timed out. If it means 3 retries (4 calls), backoff alone can reach 15s. [gap: base delay, and whether "3 attempts" includes the first call]
- Which other errors are retried is not stated here. Reviewers should check the retry condition does not cover other non-transient errors, e.g. 400 or 404. [gap: list of retried status codes / error types]
- Retrying a non-idempotent upload could leave duplicate or partial objects; worth checking the S3 call used is safe to repeat.
