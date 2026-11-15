Here is the PR description. Your notes don't say how you tested it, so I left a `[gap: ...]` in Verification rather than invent a test run. Fill it in or replace it before you open the PR.

```markdown
## Summary
Adds retry to `uploadToS3` in `storage/upload.ts`: up to 3 attempts with exponential backoff and jitter, with each delay capped at 5s. Upload failures that clear on their own now recover without the user having to start again. [gap: the failure or ticket that prompted this]

## What the agent did and what I changed
- Copilot added the retry loop: 3 attempts, exponential backoff with jitter. **Kept.**
- Copilot capped the delay at 30s. **Edited:** I changed the cap to 5s. The upload dialog times out at 10s, so a 30s wait would run past the dialog and the user would never see the result.
- Copilot also retried on 403. **Removed:** a 403 means a permissions problem, and retrying it only delays the error the user needs to see.
- Copilot renamed several variables in `storage/upload.ts`. **Reverted:** they had nothing to do with the retry, and leaving them in would have made the diff harder to review.

## Verification
- [gap: tests or manual checks run and their results, e.g. a transient failure succeeds on attempt 2, a 403 fails on the first attempt with no retry, no delay goes over 5s, the call stops after 3 attempts]

## Risks and follow-ups
- Not verified yet: the items listed under Verification. Until they are filled in, please read this as an untested change.
- Timing against the 10s dialog: with 3 attempts there are two waits, each up to 5s. That adds up to 10s of waiting before any upload time is counted, so a slow failing upload could still outlast the dialog. Please check whether the cap or the total retry time needs to be lower.
- Retryable errors: 403 is excluded. [gap: whether other 4xx errors such as 400 or 404 are also excluded, or only 403]. Please look closely at the retry condition.
```

Questions that would close the gaps, all at once:
1. What tests or manual checks did you run, and did they pass? If you ran none, I'd replace the Verification line with "Not yet tested", which is safer than leaving it blank.
2. Which errors does the retry condition skip besides 403?
3. Is there a ticket or incident behind this change that the Summary should name?
