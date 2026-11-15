Here is a PR description built only from your notes. Your notes say nothing about tests or manual checks, so I left Verification as open TODOs instead of making anything up. Fill those in, or delete the ones you won't do, before you open the PR.

```markdown
## Add retry with backoff to uploadToS3

## Approach
`uploadToS3` in `storage/upload.ts` now retries failed uploads up to 3 times,
using exponential backoff with jitter. Max delay is 5s, so retries finish
before the upload dialog's 10s timeout.

## How I used AI
| Step | What the agent proposed | What I did with it |
|---|---|---|
| Retry logic | 3 attempts, exponential backoff with jitter | Kept |
| Max delay | Backoff capped at 30s | Changed to 5s. The upload dialog times out at 10s, so a 30s wait could never finish before the user sees the timeout |
| Retryable errors | Retry on 403 too | Removed. A 403 is a permissions problem, so retrying can't fix it and only makes the user wait longer for the error |
| Variable renames | Renamed several variables in `upload.ts` | Reverted to keep the diff small and focused on retry |

## Verification
- [ ] TODO: tests for the retry path (success after a transient failure, giving up after 3 attempts)
- [ ] TODO: test that a 403 fails at once with no retry
- [ ] TODO: test or check that no single delay goes over 5s
- [ ] TODO: manual upload with a simulated failure, to confirm the dialog shows the right result inside 10s

Not verified yet: I have not run any of the checks above.

## Trade-offs
- 5s max delay instead of 30s: matches the 10s dialog timeout, but gives a
  slow-to-recover S3 less time to come back.
- Kept the existing variable names instead of the agent's renames: smaller
  diff, easier review. Renames can go in their own PR if they are wanted.

## Known limits / next steps
- 3 attempts with a 5s max delay can still add up to more than 10s in the
  worst case. Worth checking the total retry time against the dialog timeout.
- Only 403 is excluded from retries. Other non-transient errors (such as 400
  or 404) may still be retried. Worth deciding which status codes should retry.
```

Questions before you open the PR:
1. Did you add or run any tests, or upload a file by hand to check it? If yes, tell me the test names or what you tried and I will replace the TODOs.
2. Do you know if 3 attempts at up to 5s each can go past the 10s dialog timeout? If you have already checked this, I can drop that point from Known limits.
