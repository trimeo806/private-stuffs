Here are your three answers, ready to paste. Each one uses only what is in session-notes.md, and each is within 60 to 120 words. They are also saved in `section-2-answers.md`.

**Q1: Describe how you used the AI assistant during the project.**
I split the work into three scoped prompts. First I gave the agent import_metadata.py, sample.csv and the task README and asked for a plan, no code yet; I kept the plan and its dataclass per row. Then I asked for one piece at a time: parse_rows, then rights_expiry validation. Small steps kept each change easy to judge: I rejected its line.split(",") parsing and changed its date validation so bad rows are reported, not dropped. Four pytest tests pass: a quoted comma in a title, empty keywords, a bad date on row 3, and an unknown asset_id.

**Q2: Tell us about a suggestion from the AI that you did not accept. Why not?**
When I asked the agent to implement parse_rows, it split each line with line.split(","). Titles can contain quoted commas: row 2 of sample.csv is "Sunset, Bali", so that title would split in two and shift every column after it. I rejected the change and told the agent to use csv.DictReader, which respects quoting and maps fields by header name. A pytest test now checks that a quoted comma in a title parses as one field; it passes, along with the other three tests.

**Q3: What risks do you see in using AI coding agents on a production codebase like ours?**
The main risk is code that looks right but quietly does the wrong thing. Here the agent's rights_expiry validation skipped bad dates with a bare continue, so users would never learn which assets failed to update. I changed it to return an ImportReport with each row number and reason, and a test confirms a bad date on row 3 is reported while other rows still import. Second, code proven only on sample data: I ran 50 rows, never a large file. Mitigation: give the agent real context, keep prompts small, and merge nothing without tests that fail before the change.


**Word counts** (measured with `sed -n "/^\*\*Qn/{n;p;}" section-2-answers.md | wc -w`): Q1 = 96, Q2 = 83, Q3 = 100.

**What comes from your notes and what I added**
- Facts from the notes: all three prompts, the plan and the dataclass you kept, the `line.split(",")` rejection and the "Sunset, Bali" reason, the switch to `csv.DictReader`, the silent `continue` you changed to an `ImportReport`, the four passing tests, and the fact that you only ran the 50-row sample.
- My wording, not from the notes:
  - Q1: "Small steps kept each change easy to judge" is my reading of why scoped prompts helped.
  - Q2: "respects quoting and maps fields by header name" is general `csv.DictReader` behaviour, not something you wrote down.
  - Q3: "code that looks right but quietly does the wrong thing" is how I framed the risk, and the last sentence (give the agent real context, keep prompts small, no merge without tests that fail before the change) is general advice, not something you did in the session.
- I left these out on purpose: any claim about *how* you spotted the split bug (reading the diff, running it, and so on), because your notes don't say. I also left out keyword dedupe, because none of the three questions asks about limits.

**One question (no need to answer before pasting):** did you spot the `line.split(",")` problem by reading the code or by running it on sample.csv? If you remember, add a few words to Q2 ("I saw it when I read the diff" or "I saw it when I ran it on row 2"). That still keeps Q2 under 100 words.
