# Orange Logic — Coderbyte AI Fluency Assessment Prep

Assessment: 80 min. S1 "Software Engineer AI Fluency 2026 (Open Project)" → S2 free-form questions. No return to S1 after moving on.

## 1. What they grade (from sources)

| Source | Signal graded |
|---|---|
| OL Vietnam SE job post | Use AI agents for feature/bugfix work across codebase: **context preparation → agent direction → implementation review → testing → PR-quality delivery** |
| Coderbyte "Testing for AI Fluency" | Grade looks at **how you wrote prompts, what you accepted/rejected from agent, how you explain final work in README** |
| Coderbyte expert guide | Useful prompt · enough context to agent · recognize wrong output · improve mediocre code, not blindly accept · explain kept/rejected/changed |
| Coderbyte take-home docs | AI-graded vs custom rubric (best practices + technical ability), normalized 0–10. Report shows **your prompts** to employer |
| Coderbyte report analysis | Accuracy & completeness · Efficiency · Documentation & best practices |
| Free-form | Can be AI-graded too |

→ Final code ≠ whole score. Prompt log + review judgment + README + S2 answers = big part.

## 2. Coderbyte IDE facts

- In-IDE AI panel: button on left → opens right panel. Models incl. GPT-5.x/6.x, Claude Opus 4.6/4.7, Sonnet 4.6. VS Code variant may have Codex + Claude Code.
- Add context to prompt: current file, whole file tree.
- Agent mode: can edit/create files only — **cannot rename/move/delete**. Every edit needs your approval → each accept/reject is a recorded judgment.
- Open project = multi-file repo, filetree, possibly hidden/read-only files, test logs on right.
- Timer can't pause. Submit each task, then green "Submit Assessment". Keep browser open until tests finish.
- Editor detects copy/paste + tab leave. OL explicitly allows external AI, but in-IDE prompts are what graders see → **do main AI work inside IDE**; external tool only for side research.
- Disable ad-blocker. Algorithm-style tasks: don't touch the stdin/function-call line at bottom.

## 3. Workflow to run in S1 (rehearse this)

1. **Read (5 min, no AI)**: task, README, file tree, existing tests. Note language/framework, entry point, how tests run.
2. **Context prompt**: give AI the task + relevant files; ask it to restate requirements, list assumptions, edge cases, plan. Do not ask for code yet.
3. **Direct in small steps**: one function/bug per prompt. State constraints (keep signatures, no new deps, follow existing style, handle X edge case).
4. **Review every diff**: read before accept. Reject or ask for fix when wrong → reason stated in next prompt ("rejected: breaks empty input because…"). This is the signal they want.
5. **Test**: run provided tests; ask AI for extra edge-case tests (empty, single, dup, negative, huge, null, unicode, concurrency if relevant). Run again.
6. **Own it**: refactor naming/complexity yourself where AI was mediocre. Manual edits show ownership.
7. **README / summary** (if allowed/asked): approach · what AI did · what I kept/rejected/changed and why · trade-offs · known limits · next steps.
8. Submit. Leave ≥20 min for S2.

## 4. Time budget (80 min)

| Block | Min |
|---|---|
| S1 read + context | 8 |
| S1 implement + review loops | 35 |
| S1 test + polish + README | 10 |
| S1 submit buffer | 2 |
| S2 free-form | 20 |
| Final buffer | 5 |

Partial credit counts. Working simple solution first, then improve.

## 5. Likely S2 free-form questions — draft answers beforehand

- How did you use AI in Section 1? Which prompts worked best, why?
- Example where AI was wrong/suboptimal. How did you detect it, what did you change?
- How do you verify AI-generated code before shipping (tests, review, reading docs, running)?
- How do you give an AI agent context in a large codebase?
- Risks of AI coding (hallucinated APIs, security, licensing, leaking secrets/PII, over-trust, inconsistency with conventions) and mitigations.
- If more time: what would you improve in your solution?
- How would you review a teammate's AI-generated PR?

Answer pattern: concrete example from S1 → action → reason → result. Short, specific, English.

## 6. Technical prep (job post)

- One backend language fluent; C#/.NET is plus (OL DAM platform is .NET-heavy) — warm up C# syntax, LINQ, async/await, xUnit/NUnit.
- DSA basics: hashmap, two pointers, sorting, BFS/DFS, string parsing, complexity.
- SQL: joins, group by/having, window funcs, indexes.
- Unit testing + debugging an unfamiliar multi-file repo.
- Practice: Coderbyte free challenges (learn editor I/O) + 2–3 timed "fix bug / add feature in small repo with AI" drills.

## 7. Skills worth building (Claude Code, for practice + reusable prompt templates)

Skills don't run inside Coderbyte. Value = rehearse workflow now + turn each skill into a **prompt template you type into Coderbyte's AI panel**.

| Skill | Input | Output | Maps to graded signal |
|---|---|---|---|
| `task-recon` | task text + file tree | requirements checklist, assumptions, edge cases, plan, time budget | context preparation |
| `ai-diff-review` | AI-generated diff/code | bugs, missed edge cases, complexity, security, style drift, accept/reject verdict with reason | implementation review, reject bad output |
| `edge-case-tests` | function + language | table of edge cases + unit tests in project's framework | testing |
| `ownership-writeup` | notes of prompts / decisions | README section (kept/rejected/changed, trade-offs, limits) + S2 answer drafts | explain work, PR-quality delivery |
| `mock-assessment` (optional) | language/stack | timed drill: small buggy repo + task + rubric, then self-grade | full rehearsal |

Built as temporary local plugin `coderbyte-prep` (`~/.claude/skills/coderbyte-prep/skills/`): `/coderbyte-prep:task-recon`, `/coderbyte-prep:ai-diff-review`, `/coderbyte-prep:edge-case-tests`, `/coderbyte-prep:ownership-writeup`. Remove after the test: `rm -rf ~/.claude/skills/coderbyte-prep`. Each has an "In-IDE prompt template" section to type into Coderbyte's AI panel. Chain: recon → implement → diff-review → edge-case-tests → writeup.

## 8. Day-of checklist

- [ ] Ad-blocker off, stable network, Chrome, quiet room, webcam ok
- [ ] Prompt templates (sec. 7) memorised / on paper
- [ ] S2 answer skeletons ready
- [ ] Read candidate guide once
- [ ] Timer watch: move to S2 at ~T+55 min latest
- [ ] Platform issue → screenshot → recruiter

## Sources

- https://www.ol-vietnam.vn/jobs/software-engineer-core-product
- https://help.coderbyte.com/knowledge/ai-fluency
- https://help.coderbyte.com/knowledge/use-chatgpt-during-interviews
- https://help.coderbyte.com/knowledge/take-home-projects
- https://help.coderbyte.com/knowledge/ai-powered-candidate-report-analysis
- https://coderbyte.com/blog/the-expert-guide-for-running-technical-assessments-and-hiring-better-candidates
- https://coderbyte.com/candidate-guide
- https://www.cohesyve.com/blog/coderbyte-assessment

## Unresolved

- Exact S1 stack/language unknown (OL posts say any backend language; C#/.NET plus).
- Whether S1 uses native IDE or VS Code variant (Claude Code inside) unknown.
- Glassdoor details on this exact test blocked behind login.
