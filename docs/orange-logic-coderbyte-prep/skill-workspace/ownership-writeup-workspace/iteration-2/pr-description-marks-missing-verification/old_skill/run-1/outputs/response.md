Here's a PR description based on your notes. Your notes don't say how this was tested, so I left the Verification section as TODO items. I didn't want to claim checks that didn't happen. Fill them in, or delete the ones that don't apply, before you open the PR.

---

## Add retry with backoff to `uploadToS3`

### Approach
`uploadToS3` in `storage/upload.ts` now retries failed uploads up to 3 times. It uses exponential backoff with jitter. The max delay is capped at 5s so the retries finish inside the upload dialog's 10s timeout. 403 responses are not retried.

### How I used AI
| Step | What the agent did | What I did with it |
|---|---|---|
| Implement | Copilot added retry to `uploadToS3`: 3 attempts, exponential backoff with jitter | Kept the retry structure and attempt count |
| Max delay | Set the max backoff delay to 30s | Changed it to 5s. The upload dialog times out at 10s, so a 30s delay would only ever fire after the user had already seen the error |
| Retryable errors | Included 403 in the retryable statuses | Removed 403. It's a permissions problem, so a retry can't succeed and only delays the error |
| Scope | Renamed several variables in `storage/upload.ts` | Reverted the renames to keep the diff small and focused on retry |

### Verification
- [ ] TODO: tests added or updated (name the test file and cases)
- [ ] TODO: retry on a transient failure (e.g. 5xx or network error), then success on attempt 2 or 3
- [ ] TODO: gives up after 3 attempts and surfaces the error
- [ ] TODO: 403 fails right away with no retry
- [ ] TODO: total retry time stays under the 10s dialog timeout
- [ ] TODO: manual check in the upload dialog (how, and what you saw)

### Trade-offs
- 5s max delay over 30s: retries have to fit inside the 10s dialog timeout. Longer waits would never help the user.
- Fail fast on 403 instead of retrying: a permissions error won't fix itself, and the user should see it right away.
- Kept the original variable names instead of the agent's renames: a smaller diff is easier to review. Any renaming can go in a separate PR.

### Known limits / next steps
- TODO: confirm which statuses and errors are retried besides 403 (5xx? 429? network timeouts?) and list them here.
- The 10s dialog timeout and the retry budget are now coupled. If one changes, the other has to change too. Deriving both from one shared config value would make that link explicit.

---

Questions for you, so I can finish the TODOs:
1. How did you verify this? Unit tests (which file and cases), a manual upload, or nothing yet?
2. Apart from 403, which errors does the retry treat as retryable?
3. Did the agent do anything else on this branch that the notes leave out?
