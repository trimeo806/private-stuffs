I wrote the PR description to `pr-description.md` (328 words, measured with `wc -w pr-description.md`). It covers only what you told me. Where a fact was missing I put a `[gap: ...]` marker, and every check that wasn't run is labelled "Not verified".

---

## Summary
Adds retry to `uploadToS3` in `storage/upload.ts`: 3 attempts with exponential backoff and jitter, with the delay capped at 5s. [gap: what prompted this, e.g. the failure or ticket that showed uploads failing on transient errors]

## What the agent did and what I changed
- Copilot added the retry loop: 3 attempts, exponential backoff with jitter - kept.
- It capped the backoff delay at 30s - changed to 5s. The upload dialog times out at 10s, so a 30s wait would keep retrying after the user has already seen the dialog fail.
- It retried on 403 - removed. A 403 is a permissions problem that a retry will not fix; retrying only delays the error the user needs to see.
- It renamed several variables in `storage/upload.ts` - reverted, to keep the diff to the retry change.

## Verification
- Not verified: [gap: which tests or manual checks were run, and their result]
- Not verified: an upload that fails once with a transient error and succeeds on a later attempt.
- Not verified: a 403 fails on the first attempt with no retry.
- Not verified: total time for 3 failed attempts against the 10s dialog timeout.

## Risks and follow-ups
- Worst-case timing may not fit the dialog. If "3 attempts" includes the first try, there are 2 waits; at the 5s cap that is up to 2 x 5s = 10s of waiting alone, before adding the time of the 3 upload attempts. So the last attempt can still be running when the dialog times out at 10s. Actual waits depend on the base delay [gap: base delay], which may keep them under the cap. If 3 means 3 retries after the first try, it is 3 waits, up to 15s.
- Reviewers: check which errors the retry condition still covers. Only 403 was removed; other non-transient 4xx responses (e.g. 400, 404) would be retried too if the condition includes them.

---

**One question to fill the gaps:** Did you run any tests or manual checks on the retry (which ones, and did they pass)? Also: what base delay does the backoff use, does "3 attempts" include the first try, and what failure or ticket led to adding retry?

**Timing risk:** I checked the 5s cap against the 10s dialog timeout, and it doesn't clearly fit. With 3 attempts there are 2 waits. At the 5s cap that's up to 10s of waiting before the upload attempts themselves are counted. So I listed it as a risk instead of saying the new cap keeps retries inside the dialog's timeout. If the base delay is small, the real waits may stay well under the cap. Tell me the base delay and I'll replace this with the real worst-case number.

**What came from me, not from you:**
- The three "Not verified" scenarios under Verification. They are the checks I'd suggest, not ones you ran.
- The second point under Risks, about other 4xx codes such as 400 and 404 possibly still being retried. Your notes don't say what the retry condition covers.
- The 15s figure for the case where "3 attempts" means 3 retries after the first try.
