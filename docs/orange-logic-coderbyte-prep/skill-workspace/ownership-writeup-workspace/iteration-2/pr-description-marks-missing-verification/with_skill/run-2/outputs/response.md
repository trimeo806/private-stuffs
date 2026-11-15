I wrote the PR description to `pr-description.md`. `wc -w pr-description.md` gives 314 words. Here it is:

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

---

**One question so I can fill the gaps:** Did you run any tests or manual checks on the retry, and what was the result? Also, does "3 attempts" mean 3 calls in total or 3 retries after the first call? What is the base delay, and which errors or status codes does it retry? Until you answer, Verification says "Not verified" and those points are marked `[gap: ...]`. I didn't write "tests pass" because your notes don't mention any tests.

**Things to check before you post it:**
- **The 10s timeout.** Your notes say you capped the delay at 5s because the dialog times out at 10s. The numbers don't guarantee it fits, though. With 3 calls there are 2 waits of up to 5s each, so backoff alone can take 10s before any upload time counts. I put this under Risks and didn't say the cap keeps uploads inside the dialog window.
- **My suggestions, not facts from your notes:**
  - The last two "Not verified" bullets (a 403 fails without retrying; a transient failure then a success).
  - The Risks bullet about other non-transient errors such as 400 or 404.
  - The Risks bullet about non-idempotent or partial uploads.

  Delete any of them that don't apply.
