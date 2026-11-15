# Add retry with backoff to `uploadToS3`

## Approach
`uploadToS3` in `storage/upload.ts` now retries failed uploads: up to 3 attempts, exponential backoff with jitter, max delay 5s. A 403 fails at once and is not retried. The diff is limited to the retry logic; no unrelated renames.

**Why:** _<add the issue link or the failure that prompted this>_

## How I used AI
| Step | What the agent (Copilot) did | What I did with it |
|---|---|---|
| Implement | Added retry: 3 attempts, exponential backoff with jitter | Kept the retry loop, attempt count and jitter |
| Implement | Set max delay to 30s | Changed to 5s. The upload dialog times out at 10s, so a 30s wait can never finish inside it |
| Implement | Retried on 403 | Removed. 403 is a permissions problem; retrying cannot fix it and only delays the error for the user |
| Implement | Renamed several variables in `storage/upload.ts` | Reverted. Keeps the diff small and focused on the retry change |

## Verification
**Not verified yet.** No tests were added or run for this change, and no manual check has been done. Before merge:
- [ ] Unit test: transient failure on attempt 1, success on attempt 2 → upload succeeds, 2 calls
- [ ] Unit test: 403 → fails at once, 1 call, original error surfaced
- [ ] Unit test: 3 failures → gives up after 3 attempts and surfaces the last error
- [ ] Unit test (fake timers, fixed jitter): no delay is over 5s
- [ ] Manual: upload through the dialog with a failing / throttled network, confirm the error shows before the 10s dialog timeout
- [ ] Existing upload tests still pass

## Trade-offs
- 5s max delay over 30s: retries must fit inside the 10s dialog timeout; a longer wait only produces a dialog timeout instead of a clear error.
- Fail fast on 403 over retrying every error: retries are for transient faults; a permissions error needs a different fix.
- Reverted renames over accepting them: a smaller diff is easier to review; renames can be a separate PR if wanted.

## Known limits / open questions
- **Total time vs. the 10s dialog timeout.** 3 attempts means up to 2 waits of up to 5s each, so backoff alone can reach about 10s, plus the upload time of each attempt. The dialog may still time out before the last attempt finishes. Options: lower the cap, cap the total retry budget (for example 8s), or drop to 2 attempts. Needs a decision.
- **Other non-retryable errors.** Only 403 is excluded. Other 4xx responses (400, 401, 404) are likely also permanent and should probably fail fast too. To confirm in review.
