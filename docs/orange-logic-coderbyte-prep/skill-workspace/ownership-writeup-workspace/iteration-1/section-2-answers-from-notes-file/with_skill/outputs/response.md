Here are your three Section 2 answers, built only from session-notes.md. Each is within 60-120 words, so you can paste them as-is.

**Q: Describe how you used the AI assistant during the project.**
I used the assistant in small, scoped steps and reviewed each output before moving on. First I gave it import_metadata.py, sample.csv and the task readme and asked for a plan with no code. I kept the plan, including a dataclass per row, because it fit the task. Then I asked for one piece at a time: parse_rows, then rights_expiry validation. Reviewing those, I rejected a parser that split lines on commas and changed silent skipping of bad dates into an error report. I finished with four pytest tests covering a quoted comma in a title, empty keywords, a bad date and an unknown asset_id. All four pass.

**Q: Tell us about a suggestion from the AI that you did not accept. Why not?**
When I asked the agent to implement parse_rows, it parsed each line with line.split(","). I rejected that because titles can contain quoted commas: row 2 of sample.csv has the title "Sunset, Bali", so a plain split would cut it into two fields and shift every column after it. I told the agent to use csv.DictReader instead, because the csv module handles quoted fields and maps values by header name. A pytest test now checks that a quoted comma in a title parses as one field, and it passes along with the other three tests.

**Q: What risks do you see in using AI coding agents on a production codebase like ours?**
The main risk is code that looks right and fails quietly on real data. My session showed both forms. The agent's first parser split CSV lines on commas, which would shift every column for a title like "Sunset, Bali". Its date validation skipped bad rows with a bare continue, so users would never learn which assets did not update. Neither fails a happy-path demo; in production they mean wrong or missing asset metadata nobody notices. I kept control by giving the agent narrow tasks, reading every output against the real data, and requiring an ImportReport plus four passing pytest tests. A second risk is untested scale: I only ran the 50-row sample, so large files are unverified.

Word counts: 107, 94, 117.

Notes for you:
- I left out keyword dedupe (in your not-done list) to stay under 120 words in Q3. If you have room, add: "Keywords are also not deduplicated yet."
- Your notes don't say who wrote the four tests, so the answers only say they exist and pass. If you wrote them yourself, say "I wrote four pytest tests" in Q1.
