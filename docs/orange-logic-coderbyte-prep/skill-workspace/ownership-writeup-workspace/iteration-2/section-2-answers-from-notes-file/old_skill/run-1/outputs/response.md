Here are the three answers, built only from your notes. Each one is 60-120 words, so you can paste them as they are.

**1. Describe how you used the AI assistant during the project.**

I used it in small steps and made the decisions myself. First I gave it context (import_metadata.py, sample.csv and the task readme) and asked for a plan with no code. The plan was sound, so I kept it, including its idea of a dataclass per row. Then I asked for one piece at a time: parse_rows, then rights_expiry validation. I checked each output against sample.csv before I moved on. I rejected one output and changed another (see answer 2). I verified the result with 4 pytest tests: a quoted comma in a title, empty keywords, a bad date on row 3, and an unknown asset_id. All 4 pass.

**2. Tell us about a suggestion from the AI that you did not accept. Why not?**

For parse_rows, the agent split each line with line.split(","). I rejected it because titles can contain quoted commas. Row 2 of sample.csv has the title "Sunset, Bali", so every later column would shift into the wrong field. I told it to use csv.DictReader, which handles CSV quoting. A pytest test now checks that a quoted comma in a title parses as one field, and it passes. I also changed its date validation. It skipped bad rights_expiry rows with a silent `continue`. I made it record each error with the row number and reason in an ImportReport, because the user needs to know which assets did not update.

**3. What risks do you see in using AI coding agents on a production codebase like ours?**

The main risk is code that looks right but breaks on real data. The agent's line.split(",") works on simple rows and silently breaks on "Sunset, Bali". The second risk is silent failure. Its validation dropped bad dates with a bare `continue`, so assets would keep wrong rights data and nobody would be told. Other risks: invented APIs, style that drifts from the codebase, untested code, and secrets or customer data pasted into prompts. I reduce them by keeping prompts small and scoped, reviewing every diff, testing edge cases taken from real data, reporting errors instead of hiding them, and keeping secrets out of the context. I also state gaps: I only tested the 50-row sample.csv.

---

Notes:
- Your notes don't say who wrote the 4 tests, so the answers say the work was "verified with" them and don't claim you wrote them. If you wrote them, change answer 1 to "I added 4 pytest tests".
- I left out the keyword dedup gap. It only fits a "what would you improve" question. If one comes up, use both gaps: no large-file or streaming check, and keywords are not deduped.
