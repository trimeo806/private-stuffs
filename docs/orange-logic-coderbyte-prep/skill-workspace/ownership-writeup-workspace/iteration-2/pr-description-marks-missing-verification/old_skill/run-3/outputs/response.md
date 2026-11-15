I wrote the PR description to `PR_DESCRIPTION.md`. It covers what Copilot added (3 attempts, exponential backoff with jitter) and the three changes you made, each with your reason: max delay cut from 30s to 5s because of the 10s dialog timeout, no retry on 403, and the variable renames reverted.

**Verification is marked "Not verified yet".** Your notes don't mention any tests or manual checks, so I didn't claim any. The section has an unchecked list of what to run before merge:
- success on the 2nd attempt
- 403 fails at once
- gives up after 3 attempts
- no delay over 5s
- a manual check through the dialog

If you did run something, tell me what and I'll replace the checklist with it.

Two open questions are in the "Known limits" section:
1. **The retries may still go past the 10s dialog timeout.** 3 attempts means 2 waits of up to 5s each. That's up to about 10s of waiting, plus the time each upload takes. You could lower the cap, set a total retry budget, or use 2 attempts.
2. **Should other 4xx errors (400, 401, 404) fail at once too?** Right now only 403 skips the retry.

I also need the reason for the change: an issue link or the failure that prompted it. I left a "Why:" placeholder for it.
