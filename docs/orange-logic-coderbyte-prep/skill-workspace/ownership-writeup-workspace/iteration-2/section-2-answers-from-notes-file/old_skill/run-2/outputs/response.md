Here are the three answers, based only on your notes. Each is 60-120 words, so you can paste them as they are.

**1. Describe how you used the AI assistant during the project.**

I used it in small steps and checked each one. First I gave it import_metadata.py, sample.csv and the task readme and asked for a plan with no code. I kept that plan, including one dataclass per row. Then I asked it to implement parse_rows, and after that to validate rights_expiry, one prompt per piece. I read each output against sample.csv before accepting it. I rejected or changed two of its outputs: the CSV parsing and the bad-date handling. Last, I wrote four pytest tests: quoted comma in a title, empty keywords, a bad date on row 3, and an unknown asset_id. All four pass.

**2. Tell us about a suggestion from the AI that you did not accept. Why not?**

For parse_rows, the agent split each line with line.split(","). I rejected it. Row 2 of sample.csv has the title "Sunset, Bali", and a plain split would cut it into two fields and shift every column after it. I told it to use csv.DictReader, which handles quoted commas. I added a test for that case, and it passes. I also changed its date validation. It skipped bad rows with a silent continue. Now each error is recorded with its row number and reason in an ImportReport. The user needs to know which assets were not updated.

**3. What risks do you see in using AI coding agents on a production codebase like ours?**

The biggest risk is code that looks right but is wrong on real data. My session had two examples. The split(",") parser worked on simple rows but broke on a quoted comma. The silent continue would have dropped metadata updates with no error. In a system that holds many customers' assets, that means rights dates or keywords quietly go missing. Other risks are invented APIs, code that does not match the team's style, and secrets or customer data pasted into prompts. To reduce them, I ask for a plan first, keep changes small, test with real edge cases, and review AI code like any other PR.

---

Two things to keep in mind:
- Your notes say you only tried sample.csv (50 rows), with no large-file or streaming check, and keywords are not deduplicated. I left these out because none of the three questions asks about them. If they ask what you would improve, those are your answers.
- In answer 3, the line about invented APIs, style and secrets is general risk, not something from your session. Cut it if you only want to cite things you saw.
