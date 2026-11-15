Here are your three answers. Every event in them comes from session-notes.md, and each one sits in the 60 to 120 word range.

**Q1: Describe how you used the AI assistant during the project.**
I used the agent in small, scoped steps on the metadata CSV import. First I gave it import_metadata.py, sample.csv and the task readme and asked for a plan with no code yet. I kept the plan, including its dataclass per row. Then I asked for one function at a time: implement parse_rows, then validate rights_expiry. Those small outputs are where I caught two problems: I rejected its comma-split parser and changed its silent skipping of bad dates. Four pytest tests cover the result, including a quoted comma, a bad date and an unknown asset_id. All four pass.

**Q2: Tell us about a suggestion from the AI that you did not accept. Why not?**
When I asked the agent to implement parse_rows, it parsed each line with line.split(","). I rejected that because titles can contain quoted commas: row 2 of sample.csv has the title "Sunset, Bali", and a plain split would shift every column after it, so keywords and rights_expiry would be read from the wrong fields. I told it to use csv.DictReader instead, which respects quoting and maps columns by header. A pytest test now checks that a quoted comma in a title parses as one field, and it passes along with the other three.

**Q3: What risks do you see in using AI coding agents on a production codebase like ours?**
The main risk is code that runs and looks right but fails on real data. Here the agent's first parser split CSV lines on commas, which breaks on a quoted title like "Sunset, Bali". Second, silent failure: it skipped bad dates with a bare continue, so assets would quietly not update. I changed that to an ImportReport with row number and reason. Third, untested scale: I only ran the 50-row sample, so large files are not verified. Beyond this task: secrets or customer data pasted into prompts. Mitigations: scoped prompts, edge-case tests, no silent drops, and approved tools only.

**Word counts:** Q1 97, Q2 92, Q3 99. I counted them with `wc -w a1.txt a2.txt a3.txt`, with each answer saved to its own scratch file.

**Lines that are mine, not from your notes:**
- Q2: "which respects quoting and maps columns by header" describes how `csv.DictReader` works. Your notes don't say it.
- Q3: the secrets and customer-data risk, and the "approved tools only" fix, are general points. Nothing in this session showed them. Keep them only if you'd stand behind them in an interview.
- Q1: "Those small outputs are where I caught two problems" is how I framed it. Your notes show the two catches but don't say how you spotted them.

**Gaps:** none that block you. If you want one more concrete detail in Q3, I left out that keywords aren't deduplicated. You could swap it in for the scale point.
