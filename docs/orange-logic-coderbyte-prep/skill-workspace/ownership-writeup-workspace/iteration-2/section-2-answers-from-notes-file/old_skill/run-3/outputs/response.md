Here are the three answers, built only from your session-notes.md. Each one is about 80-110 words, so you can paste them as they are.

---

**1. Describe how you used the AI assistant during the project.**

I used it in small steps and reviewed each one. First I gave it import_metadata.py, sample.csv and the task readme, and asked for a plan with no code. The plan was good, so I kept it, including one dataclass per row. Then I asked it to implement one function at a time: parse_rows first, then rights_expiry validation. After each step I checked the output against sample.csv before I moved on. I rejected one version and changed another. Last, I wrote 4 pytest tests to confirm the behaviour: a quoted comma in a title, empty keywords, a bad date, and an unknown asset_id. All 4 pass.

**2. Tell us about a suggestion from the AI that you did not accept. Why not?**

For parse_rows, the agent split each line with `line.split(",")`. I rejected it because titles can contain quoted commas. Row 2 of sample.csv has the title "Sunset, Bali", so a plain split would shift every column after the title, and keywords and rights_expiry would get the wrong values. I told it to use `csv.DictReader`, which handles quoted fields. A pytest test checks that a quoted comma in a title parses as one field, and it passes. I also changed a second suggestion: it skipped rows with bad dates using a silent `continue`. Now each bad row goes into an ImportReport with its row number and reason, so the user knows which assets did not update.

**3. What risks do you see in using AI coding agents on a production codebase like ours?**

- **Code that works on the happy path only.** The agent's `split(",")` looked fine but broke on real data with quoted commas. Mitigation: test with real sample files and edge cases, not only the obvious input.
- **Silent data loss.** It dropped bad rows with `continue`. In an asset system that means metadata quietly does not update. Mitigation: review error handling first, and report failures with a row number and reason.
- **Over-trust and untested scale.** I only tested a 50-row file. AI code can pass small tests and fail on large imports. Mitigation: load tests and a human review before merge.
- **Context and data leaks.** Agents need context. Share only the files they need, and never secrets or customer data.

---

Note: in answer 3, the last point (context and data leaks) is a general risk, not something from your session. Your notes do not cover it, so I did not claim you did anything about it. Remove that point if you want only session-based answers.
