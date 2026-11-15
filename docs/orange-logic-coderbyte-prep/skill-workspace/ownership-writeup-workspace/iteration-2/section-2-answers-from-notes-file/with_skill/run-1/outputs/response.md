Here are the three answers, ready to paste. Each is 60-120 words, measured with `wc -w` on each answer saved to its own file.

**Q1: Describe how you used the AI assistant during the project.** (95 words)

I kept the decisions and let the agent draft. My first prompt gave it import_metadata.py, sample.csv and the task README and asked for a plan, no code yet. I kept the plan, including a dataclass per row. Then I asked for one piece at a time: parse_rows, then rights_expiry validation. Small changes were easy to review, and I caught two problems: I rejected a comma-split parser and replaced a silent skip of bad dates with an error report. Four pytest tests pass: quoted comma in a title, empty keywords, a bad date, an unknown asset_id.

**Q2: Tell us about a suggestion from the AI that you did not accept. Why not?** (98 words)

When I asked the agent to implement parse_rows, it split each line with line.split(","). I rejected it: titles can contain quoted commas, and row 2 of sample.csv, "Sunset, Bali", would become two fields and shift every column after it. I told it to use csv.DictReader, which respects quoted fields; the test "quoted comma in title parses as one field" passes. I also changed its date validation, which skipped bad rows with a bare continue. It now records each failure with row number and reason in an ImportReport, because the user needs to know which assets did not update.

**Q3: What risks do you see in using AI coding agents on a production codebase like ours?** (98 words)

First, code that looks right but breaks on real data: the agent's line.split(",") parser works until a title has a quoted comma, like "Sunset, Bali" in sample.csv, then shifts every column. Second, silent failure: its date validation dropped bad rows with a bare continue, so assets would quietly not update, leaving wrong metadata nobody notices. Third, checks that only cover small samples: I only tried the 50-row sample.csv, so large files and streaming are unverified. Mitigations: give the agent real sample data, require tests for edge inputs, report every failed row, and test at production volume before release.

---

- **Word counts:** I ran `wc -w q1.txt q2.txt q3.txt` and got 95, 98 and 98.
- **Sources:** every event comes from session-notes.md: the planning prompt, the two prompts after it, the `split(",")` rejection, the change from a silent `continue` to ImportReport, the 4 passing pytest tests, and the 50-row sample limit.
- **My additions, not in your notes:**
  - Q1: "small changes were easy to review" is my reading of why you split the prompts.
  - Q3: the four mitigations in the last sentence are general practice I added. Testing at production volume is a next step, not something you did. "Wrong metadata nobody notices" is my description of what the silent skip would cause.
- **Left out:** keywords are not deduplicated. If you want that limit in Q3, swap it in for the volume point.
- **No gaps:** the notes covered everything the three questions needed, so there is nothing for you to fill in.
