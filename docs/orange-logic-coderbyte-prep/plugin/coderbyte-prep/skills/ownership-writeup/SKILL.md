---
name: ownership-writeup
description: Turn notes from an AI-assisted coding session (prompts sent, agent output kept, rejected or edited and why, tests run, trade-offs, known limits) into a short first-person write-up that shows the human owned the work - a README or SOLUTION.md with a How I used AI table, 60-120 word answers to free-form questions about AI use, or a PR description for agent-written changes. Use it whenever the user is wrapping up a take-home or timed assessment (Coderbyte AI fluency, Section 2 written questions) or says things like "write my SOLUTION.md", "write up my approach", "answer the section 2 questions", "explain how I used AI", "PR description for what the agent did", "summarize what Copilot or Claude changed for review", or pastes session notes and asks for a README, even if they never say write-up. Grounds every claim in the notes, marks gaps instead of inventing events, and measures word limits with a tool.
---

# Ownership Write-up

Graders of AI-assisted work ask: did this person stay in charge? Did they give the agent context, catch what it got wrong, verify the result, and know where it is still weak. Answer with evidence from the session.

It is the last step of the chain task-recon -> tdd-implement (the implement step) -> ai-diff-review -> edge-case-tests -> ownership-writeup, so use what those steps left: recon notes, test results, review findings. It also fits any take-home README or AI-assisted PR description.

## Step 1: Collect the facts

From the notes, notes file, diff, code, test output or prompt log, pull out:

1. Task and final state: what works, what does not.
2. The two to four prompts that shaped the result, with their intent.
3. Agent output kept, rejected or changed, each with the reason.
4. Verification: tests run (names, inputs, result), manual checks.
5. Trade-offs and known limits.

If something is missing, ask one question listing all gaps. With no time to ask (clock running, "just write it"), write the draft with `[gap: what is missing]` where the fact belongs.

## Step 2: Invent nothing

Every factual claim (event, prompt, rejection, test, count, timing, behaviour, result) must trace to the notes, the code, or a command output you saw. The usual slips are small:

- A conclusion the notes never drew, such as "the new limit keeps us inside the other limit". If you work it out, show the arithmetic; if it is tight or fails, state it as a risk.
- A habit the notes do not record: "I reviewed every diff", "I checked each output against the sample data".
- A person: a reviewer, a teammate, a user report.
- Who did something: if the notes say tests exist but not who wrote them, say they exist.
- A planned check that reads as done: scenarios under Verification that were not run are labelled "Not verified:".
- Your own next steps in Known limits: allowed, but tell the user which ones are yours.

Anything you cannot trace becomes `[gap: ...]` or "Not verified: ...". One invented claim found in the prompt log discredits every true sentence around it.

## Step 3: Pick the output

- A, README or SOLUTION.md (or "write up my approach"): default limit 250 words.
- B, free-form answers (pasted questions, "section 2"): 60 to 120 words each. To prepare before seeing the questions, read `references/question-bank.md`.
- C, PR description: the work is a pull request.

Produce several when the request covers several.

## Output A: README / SOLUTION.md

Drop a section only when the notes have nothing for it, and say so in one line.

```markdown
## Approach
<2-3 sentences: the problem, the design chosen, why over the obvious alternative>

## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| <what I asked the agent to do> | <kept, rejected, or changed: what exactly> | <the specific reason or evidence> |

## Verification
- Tests: <test names or behaviours, and the result from the notes>
- Not verified: <what was not run or measured>

## Trade-offs
- <choice> over <alternative> because <reason>

## Known limits
- <limit> -> <what I would do next>
```

Each row traces to a real prompt. Include a rejected or changed row when the notes have one; if everything was kept, say which checks it passed.

## Output B: free-form answers

Four beats: a concrete example from this session, what I did, why, the result. A general claim earns little; tied to the moment it happened, it proves the behaviour.

```markdown
**Q: <question as asked>**
<Example from the session>. <What I did>. <Why>. <Result, with evidence>.
```

For a risks question, lead with the risk and use the session as evidence.

## Output C: PR description

```markdown
## Summary
<1-2 sentences: what changed and why>

## What the agent did and what I changed
- <agent change> - <kept / edited / rejected, and why>

## Verification
- <tests run and result>, or Not verified: <what still needs checking>

## Risks and follow-ups
- <known limit or what a reviewer should look at closely>
```

## Step 4: Measure word counts

Estimates run low: table pipes, bullets and headings count as words in `wc -w`, which is what a grader runs.

1. Write the text to a file: the deliverable, or each answer to its own scratch file.
2. Run `wc -w <file>` on the raw text. For one section of a file: `sed -n '/^## Start/,/^## Next/p' file | wc -w`.
3. Aim at least 10% under the limit (250 -> at most 225); for a range, aim for the middle.
4. Over target: cut filler, never a rejected row or a known limit. Re-count.
5. Report the number the tool printed and the command, never an estimate.

## Writing rules

- First person, active voice: "I rejected", "I asked the agent to". Passive voice hides who decided.
- Every "I verified" names a test, input, command or result.
- Make the rejected suggestion specific: what the agent proposed, how you noticed, what you did instead.
- Own the limits: stated by the author they read as judgement, found by the grader as a miss.
- No filler ("leveraged", "robust"), no restating the question.

For model write-ups (full SOLUTION.md, answers, a weak answer rewritten, notes with gaps), read `references/examples.md`.

## Timing

In a timed assessment, budget about 5 minutes for the README and 3 per answer, and write the README while the code is still open: Section 2 does not allow going back.

## Hand-off

Reply with the file path (if written), the measured word count and its command, the gaps as one question, and which lines are your suggestions rather than facts from the notes.

## In-IDE prompt templates

Coderbyte's in-IDE assistant has no skills, so paste these. Decide kept and rejected yourself first, and check its count with `wc -w` before pasting.

```
Draft a README from my notes below. Sections: Approach, How I used AI (table:
prompt intent | kept/rejected/changed | reason), Verification, Trade-offs,
Known limits. First person, under <limit> words. Use only facts in the notes;
write [gap: ...] for anything missing and "Not verified" for anything not run.
No filler words.

Notes:
<paste notes>
```

```
Answer this question in 60-120 words, first person, using only my notes:
example from the session, what I did, why, result. Add no events, habits or
checks that are not in the notes.

Question: <question>
Notes: <paste notes>
```
