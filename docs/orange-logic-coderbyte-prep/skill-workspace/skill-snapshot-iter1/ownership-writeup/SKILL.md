---
name: ownership-writeup
description: Turn notes from an AI-assisted coding session (prompts sent, agent output kept, rejected or edited and why, tests run, trade-offs, known limits) into a short first-person write-up that shows the human owned the work - a README or SOLUTION.md with a How I used AI table, 60-120 word answers to free-form questions about AI use, or a PR description for agent-written changes. Use it whenever the user is wrapping up a take-home or timed assessment (Coderbyte AI fluency, Section 2 written questions) or says things like "write my SOLUTION.md", "write up my approach", "answer the section 2 questions", "explain how I used AI", "PR description for what the agent did", "summarize what Copilot or Claude changed for review", or pastes session notes and asks for a README, even if they never say write-up. Grounds every claim in the notes and marks gaps instead of inventing events.
---

# Ownership Write-up

Graders of AI-assisted work, whether a Coderbyte rubric or a reviewer on a PR, are not asking "did AI write good code". They are asking "did this person stay in charge": did they give the agent the right context, catch what it got wrong, verify the result, and know where it is still weak. This skill turns raw session notes into writing that answers that question with evidence.

It is the last step of a chain (task-recon, tdd-implement, ai-diff-review, edge-case-tests, ownership-writeup), so the best input is whatever those steps left behind: the recon notes, the review findings, the test list. It works just as well for any take-home README or AI-assisted PR description.

## Step 1: Collect the notes

Work only from what the user gives you: pasted notes, a notes file, the diff, the test output, the IDE prompt log. Pull out these five things:

1. Task and final state: what was asked, what works, what does not.
2. Key prompts: the two to four that shaped the result, with their intent.
3. Agent output kept, rejected or changed by hand, each with the reason.
4. Verification: tests added or run (names, inputs, pass or fail), manual checks.
5. Trade-offs and known limits.

If something is missing, ask one short question listing all the gaps at once. When there is no time to ask (the assessment clock is running, or the user says "just write it"), write the draft anyway and put `[gap: what is missing]` where the fact belongs. A visible gap costs a few seconds to fill; an invented test run or an invented rejection is a false statement in a graded document, and a reviewer who checks the prompt log will find it.

## Step 2: Pick the output

- Output A, README or SOLUTION.md: when the user asks for a README, SOLUTION.md, "write up my approach", or the assessment wants a summary file.
- Output B, free-form answers: when the user pastes written questions or says "section 2". If they ask to prepare before seeing the questions, read `references/question-bank.md`.
- Output C, PR description: when the work is a pull request or the user says "PR description".

Produce more than one when the request covers more than one. Default to 250 words for A unless the user or task gives another limit; B answers are 60 to 120 words each.

## Output A: README / SOLUTION.md

Use this template. Drop a section only if the notes have nothing for it, and then say so in one line rather than leaving it out silently.

```markdown
## Approach
<2-3 sentences: the problem, the design chosen, why this design over the obvious alternative>

## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| <what I asked the agent to do> | <kept, rejected, or changed: what exactly> | <the specific reason or evidence> |

## Verification
- Tests: <test names or behaviours, and the result: N passing, 1 failing>
- Edge cases: <the inputs covered>
- Manual checks: <what I ran or read by hand>

## Trade-offs
- <choice> over <alternative> because <reason>

## Known limits
- <limit> -> <what I would do next with more time>
```

The table is the heart of the README. Graders cross-check it against the prompt log, so each row should be traceable to a real prompt. Include at least one rejected or changed row when the notes contain one; if the notes show the agent's output was kept unchanged everywhere, say what checks it passed before you kept it.

## Output B: free-form answers

Write each answer in four beats: a concrete example from this session, what I did, why, and the result. A general claim ("I always review AI code carefully") earns little; the same claim tied to the moment it happened ("the agent's first parser split on commas, so I...") earns full credit because it proves the behaviour.

```markdown
**Q: <question as asked>**
<Example from the session>. <What I did>. <Why>. <Result, with evidence>.
```

Keep each answer 60 to 120 words. Under 60 usually means the example or the result is missing; over 120 usually means generic padding crept in. Answer the question that was asked: if it is about risks of AI in production, lead with the risk and use the session as the evidence, not the other way round.

## Output C: PR description

```markdown
## Summary
<1-2 sentences: what changed and why>

## What the agent did and what I changed
- <agent change> - <kept / edited / rejected, and why>

## Verification
- <tests run and result, or manual check>

## Risks and follow-ups
- <known limit or what a reviewer should look at closely>
```

A reviewer of an agent-written PR wants to know which parts a human actually read and tested. Say so plainly, and name the parts nobody has verified yet.

## Writing rules and why

- First person, active voice: "I rejected", "I asked the agent to". The point is ownership, and passive voice ("the code was refactored") hides who decided.
- Name the evidence for every "I verified": a test name, an input, a command, a result. "Verified" with nothing behind it reads as a guess.
- Use only events from the notes. Rephrase and tighten freely, but never add a test, a prompt, a rejection or a result that is not there. Mark gaps as `[gap: ...]`.
- Be specific about the rejected suggestion: what the agent proposed, how you noticed it was wrong, what you did instead. This is the single most persuasive sentence in the write-up.
- Own the limits. A known limit stated by the author reads as judgement; the same limit found by the grader reads as a miss.
- Cut filler: no "leveraged", "robust", "seamless", "cutting-edge", no restating the question. Short sentences.
- Respect word limits. Count before you hand it over.

## Worked example

Notes (as the user typed them):

```
dedupe uploads by checksum. asked agent to plan from README + uploads.ts.
kept plan, fit the existing upload flow.
agent first impl hashed filename not bytes -> rejected, same name diff content
would collide. had it hash file bytes w/ sha256 instead.
tests: same bytes diff names -> 1 asset, diff bytes same name -> 2. both pass.
no streaming, loads whole file in memory. didnt test >100MB.
```

Output A, How I used AI and Known limits only:

```markdown
## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| Plan dedupe from README and uploads.ts | Kept the plan | Matched the existing upload flow |
| Implement checksum dedupe | Rejected first version | It hashed the filename, so two different files with the same name would collide; I had it hash file bytes with SHA-256 |

## Known limits
- Whole file is loaded into memory; untested above 100 MB -> stream the hash
```

Output B, "Give an example of AI output you rejected":

```markdown
The agent's first dedupe version hashed the filename instead of the file bytes. That is wrong for an asset library: two different files that share a name would collide and be treated as one asset. I rejected it and had the agent hash the file bytes with SHA-256 instead, because content, not naming, defines a duplicate. Two tests cover it: same bytes with different names gives one asset, and different bytes with the same name gives two. Both pass, which confirms the dedupe now keys on content, not names.
```

For a longer end-to-end example with a full README, and a weak answer rewritten into a strong one, read `references/examples.md`.

## Final check before handing over

1. Every event in the write-up appears in the notes; anything else is a `[gap: ...]`.
2. At least one rejected or changed AI output is named specifically, or the write-up says which checks the kept output passed.
3. Each "verified" names a test, input or command.
4. Word limits hold: README within its limit, each answer 60 to 120 words.
5. First person throughout, no filler words.

In a timed assessment, budget about 5 minutes for the README and 3 per answer. Section 2 does not allow going back to the code, so write the README while the code is still open.

## In-IDE prompt template

Coderbyte's in-IDE assistant has no skills, so paste this when you want it to draft for you. Write the kept and rejected decisions yourself first; the AI only formats them.

```
Draft a README from my notes below. Sections: Approach, How I used AI (table:
prompt intent | kept/rejected/changed | reason), Verification, Trade-offs,
Known limits. First person, under 250 words. Use only facts in the notes; where
something is missing write [gap: ...] instead of guessing. No filler words.

Notes:
<paste notes>
```

For a Section 2 answer:

```
Answer this question in 60-120 words, first person, using only my notes:
concrete example from the session, what I did, why, result. Do not add events
that are not in the notes.

Question: <question>
Notes: <paste notes>
```
