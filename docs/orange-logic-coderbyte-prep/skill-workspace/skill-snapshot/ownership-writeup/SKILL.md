---
name: ownership-writeup
description: Turn notes about a coding session (prompts sent, AI suggestions accepted, rejected or changed, tests run, trade-offs) into a README / PR-style summary and concise answers to free-form interview questions about how AI was used. Use at the end of an AI-fluency coding assessment, when writing a README for a take-home, a PR description for AI-assisted work, or when the user says "write up my approach", "explain what I did", "answer the free-form questions".
---

# Ownership Write-up

Goal: show the grader that the human owned the solution: what AI did, what the user decided, and why.

## Inputs

Ask for quick bullet notes if not given:
- Task and final result (tests passing? what is missing?)
- Key prompts sent (2-4 best ones)
- AI output accepted, rejected, changed by hand, with the reason
- Tests added and how the solution was verified
- Trade-offs and known limits

## Output A: README / summary

```markdown
## Approach
<2-3 sentences: problem, design, why this design>

## How I used AI
| Step | What I asked | What I did with the output |
|---|---|---|
| Recon | ... | kept plan, changed assumption X |
| Implement | ... | rejected first version: <input> broke it |

## Verification
- Tests: <added / passing>
- Edge cases covered: <list>
- Manual checks: <list>

## Trade-offs
- <choice> over <alternative> because <reason>

## Known limits / next steps
- <limit> → <what I would do with more time>
```

## Output B: free-form answers

Pattern for each answer, 60-120 words: **concrete example from the session → action → reason → result.** No generic claims without an example.

Prepared question bank:
1. How did you use AI in this task? Which prompts were most effective and why?
2. Give an example where AI output was wrong or suboptimal. How did you detect it and what did you change?
3. How do you verify AI-generated code before shipping it?
4. How do you give an AI agent context in a large codebase?
5. What are the risks of AI-assisted coding and how do you mitigate them? (hallucinated APIs, security, secrets / PII leakage, licensing, over-trust, style drift, untested code)
6. With more time, what would you improve in your solution?
7. How would you review a teammate's AI-generated pull request?

## Rules

- Every claim of "I verified" names how (test name, input, run).
- Mention at least one rejection or correction of AI output. If none happened, say which checks the output passed.
- Plain English, short sentences, no filler. Fits in the time left: write README in under 5 minutes, each answer in under 3.
- Never invent work that did not happen.

## In-IDE prompt template (only if allowed; write the decisions yourself)

```
Draft a README section from these notes: <notes>. Sections: Approach, How I
used AI (table: step, prompt, what I kept/rejected/changed and why),
Verification, Trade-offs, Known limits. Keep it under 250 words and do not add
anything not in the notes.
```
