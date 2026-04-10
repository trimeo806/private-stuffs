# AI Coding Interview: Master Preparation Document

**All documents merged** — checklist framework with embedded reference content inline.
**Updated**: April 6, 2026 | **Focus**: Claude Code hooks, skills, agents, commands, tri_ai_kit

---

## ABOUT ME

I'm Tri, a frontend developer with more than 3 years of experience, having worked across two projects in the IT field.

**Project 1 — OCBC Bank (Singapore client)**
The client wanted to renew their banking system. I collaborated with the team to develop a real-time application where customers and tellers can interact with each other via dual screens to perform transactions such as withdrawals, deposits, and more. The tech stack I used included ReactJS, WebSocket, and micro-frontend architecture for horizontal scaling — chosen because the client had many features to implement across independent teams.

**Project 2 — ePost (SwissPost, Switzerland)**
SwissPost wanted to digitalize their physical processes onto a cloud platform. My main responsibility was developing a custom UI library (similar to Radix UI but with more specific requirements). Alongside that, I contributed to defining the responsive design concept, animation system, and design system for the application.

With these two projects, I gained a great deal — both technically and in how to communicate effectively with other teams throughout the development process. With my frontend expertise and the backend knowledge I'm currently developing, I'm excited to bring this to the easyGDS team. I'm eager to learn about new business concepts in the travel domain and the technology stack powering your system. It's a great opportunity.

---

## ABOUT easyGDS

### What They Do

easyGDS is a **B2B SaaS travel technology platform** (founded 2021, built on 25+ years of industry heritage):

- Connects **airlines, travel agents, and hotels** via GDS integrations — Amadeus, Sabre, Travelport
- Core products: booking engines, dynamic packaging, NDC-certified airline distribution
- Serves 30+ clients globally; invests 20%+ revenue in R&D
- Business model: transaction-based SaaS for travel operators

---

## SALARY NEGOTIATION

**Known market range**: 600–900 USD. Always anchor at the top third (850–900), not the middle.

### Step 1 — Flip it back first

> "Before I give a number, could you share the range budgeted for this role? I want to make sure we're aligned."

If they share first, you negotiate from their number — not yours.

### Step 2 — If they push you for a number

> "Based on my research and the scope of the role, I'm thinking around **850–900 USD**. But honestly, I'm more focused on the overall fit — the role, the team, and the growth opportunity. I'm open to discussing what makes sense for both sides."

### Step 3 — If they counter below your anchor

> "That's a bit lower than I was expecting. Is there flexibility there, or are there other parts of the package — like performance reviews or growth path — we could talk through?"

### Key Phrases

| Situation              | What to Say                                                        |
| ---------------------- | ------------------------------------------------------------------ |
| They ask first         | "What's the budgeted range for this role?"                         |
| You must give a number | "Around 850–900, but I'm open to discussion."                      |
| They push back         | "I'm flexible — what does the package look like overall?"          |
| They ask if negotiable | "Yes, I'm open. The right fit matters more than the exact number." |

### What to Avoid

- Don't say "600–900" — signals you'll accept 600
- Don't say "I'll take whatever is fair" — too passive
- Don't over-justify — one sentence on reasoning is enough

---

## TABLE OF CONTENTS

- [Week 1: Claude Code Foundations](#week-1-claude-code-foundations)
  - [Mon-Tue: System Architecture](#monday-tuesday-system-architecture)
  - [Wed: Hooks Deep Dive](#wednesday-hooks-deep-dive)
  - [Thu-Fri: Skills System](#thursday-friday-skills-system)
- [Week 2: Prompt Engineering & Configuration](#week-2-prompt-engineering--configuration)
  - [Mon-Tue: Prompt Engineering](#monday-tuesday-prompt-engineering-for-claude-code)
  - [Wed: Settings & Configuration](#wednesday-settings--configuration-mastery)
  - [Thu-Fri: Live Practice Session 1](#thursday-friday-live-practice-session-1)
- [Week 3: Orchestration & System Design](#week-3-orchestration--system-design)
  - [Mon-Tue: Multi-Agent Architecture Deep Dive](#monday-tuesday-multi-agent-architecture-deep-dive)
  - [Wed: Interview Format Deep Dive](#wednesday-interview-format-deep-dive)
  - [Thu-Fri: Mock Interview Session](#thursday-friday-mock-interview-session)
- [Week 4: Stories, Scenarios & Final Polish](#week-4-stories-scenarios--final-polish)
  - [Mon-Tue: Story Development](#monday-tuesday-story-development)
  - [Wed: Common Questions Practice](#wednesday-common-questions-practice)
  - [Thu: Orchestration Scenarios Practice](#thursday-orchestration-scenarios-practice)
  - [Fri: Final Sharp Check](#friday-final-sharp-check)
- [Interview Day Checklist](#interview-day-checklist)
- [Quick Reference: Key Numbers](#quick-reference-claude-code-key-numbers)
- [Command Cheatsheet](#claude-code-command-cheatsheet)
- [Hook Quick Reference](#hook-quick-reference)
- [Agent Chain: Full Feature Delivery](#agent-chain-full-feature-delivery)
- [Success Metrics](#success-metrics)
- [Interview Q&A: All Tiers](#interview-qa-all-tiers)
- [Market Context & Tool Landscape](#market-context--tool-landscape)
- [Interview Formats: Detailed Walkthroughs](#interview-formats-detailed-walkthroughs)
- [Agentic Framework Landscape](#agentic-framework-landscape)

---

## WEEK 1: Claude Code Foundations

### Monday-Tuesday: System Architecture

- [ ] Read Part 2-3 below (Agents overview)
- [ ] Understand the core loop: `/brainstorm → /plan → /cook → /review → /test → /git`
- [ ] Know the 3 agent tiers: orchestration, architecture, implementation
- [ ] Understand routing logic: how intent + platform signals select the agent
- [ ] Read `CLAUDE.md` at `/c/private/CLAUDE.md` (your orchestration rules)

**Self-test**: Can you name the 22 agents and what each does without notes?

---

#### CONTENT: PART 1 — Tool Landscape (Quick Context)

##### Three Tool Archetypes

| Tool               | Paradigm       | Best For                                    | Price    |
| ------------------ | -------------- | ------------------------------------------- | -------- |
| **GitHub Copilot** | IDE plugin     | Fast completions, enterprise standard       | $10/mo   |
| **Cursor**         | Full IDE fork  | Daily work, 30% faster on SWE-bench         | $20/mo   |
| **Claude Code**    | Terminal agent | Complex multi-file refactors, orchestration | Variable |

**Your positioning**: Hybrid — Cursor for daily work, Claude Code for complex orchestration tasks.

##### Interview Format Has Changed

| Old Model                         | New Model                                      |
| --------------------------------- | ---------------------------------------------- |
| LeetCode algorithms on whiteboard | Pair programming with AI on real features      |
| Code output matters               | Prompt quality + validation discipline matters |
| Individual skill                  | Orchestration thinking                         |

---

#### CONTENT: PART 2 — Claude Code Deep Dive

Claude Code is a **CLI-based multi-agent orchestration system** built on the Anthropic Claude API. It provides:

- 22 specialized agents with defined roles and handoff protocols
- 67 skills (passive knowledge modules lazy-loaded by context)
- A lifecycle hook system (Node.js scripts wired to session events)
- A 15-phase production delivery workflow

**Core loop:** `/brainstorm → /plan → /cook → /review → /test → /git`

---

#### CONTENT: PART 3 — Agents (22 Agents)

##### Routing Logic

Every prompt is classified by intent and platform, then routed:

- **Intent signals**: build → cook/developer, fix → debugger, plan → planner, review → code-reviewer
- **Platform signals**: `.tsx` files → frontend agents, `.go` → backend agents
- **Multi-intent**: routes to `project-manager` to decompose first
- **3+ independent tasks**: uses `subagent-driven-development` skill (parallel execution)

##### Agent Tiers

**Orchestration / Planning**

| Agent             | Model  | Key Role                                               |
| ----------------- | ------ | ------------------------------------------------------ |
| `planner`         | opus   | Creates phased implementation plans; spawns architects |
| `project-manager` | opus   | Roadmap-level coordination, multi-phase projects       |
| `brainstormer`    | sonnet | Explores options, evaluates trade-offs                 |

**Architecture (Phase 3)**

| Agent                | Model | Key Role                                                    |
| -------------------- | ----- | ----------------------------------------------------------- |
| `backend-architect`  | opus  | API contracts, data models, auth strategy, DB schema        |
| `frontend-architect` | opus  | Routing hierarchy, state management, component architecture |

**Implementation**

| Agent                | Model  | Key Role                                          |
| -------------------- | ------ | ------------------------------------------------- |
| `developer`          | sonnet | Generic task dispatcher; detects platform, routes |
| `frontend-developer` | sonnet | React/Next.js/TanStack, components, state         |
| `backend-developer`  | sonnet | REST/GraphQL APIs, DB schemas, auth, async jobs   |
| `devops-engineer`    | sonnet | Docker, K8s, Terraform, CI/CD, cloud              |

**Quality & Review**

| Agent              | Model  | Key Role                                                      |
| ------------------ | ------ | ------------------------------------------------------------- |
| `code-reviewer`    | sonnet | Pure reader — applies SEC/PERF/TS/LOGIC/DEAD/ARCH/STATE rules |
| `tester`           | haiku  | Test suites, coverage, E2E validation                         |
| `security-auditor` | sonnet | OWASP Top 10, secrets scan, CVE checks, access control        |
| `muji`             | sonnet | UI component audits, design system compliance                 |
| `a11y-specialist`  | sonnet | WCAG 2.1 AA for web, iOS VoiceOver, Android TalkBack          |
| `debugger`         | sonnet | Root cause analysis from stack traces                         |

**Knowledge & Docs**

| Agent                   | Model  | Key Role                                                |
| ----------------------- | ------ | ------------------------------------------------------- |
| `researcher`            | sonnet | Tech evaluation, docs lookup; spawned by planner --deep |
| `docs-manager`          | sonnet | API docs, ADRs, getting started guides                  |
| `journal-writer`        | haiku  | Captures learnings on session Stop (auto-triggered)     |
| `knowledge-graph-guide` | haiku  | Navigates prior decisions, links related insights       |

**Operations**

| Agent               | Model  | Key Role                                              |
| ------------------- | ------ | ----------------------------------------------------- |
| `git-manager`       | haiku  | Commit, push, PR (2-4 tool calls, token efficient)    |
| `mcp-manager`       | haiku  | External MCP tool integrations (Slack, GitHub, Figma) |
| `design-specialist` | sonnet | Brand identity, design tokens, component libraries    |

##### Key Orchestration Rules

- Agents **never spawn other agents** — only main conversation orchestrates
- Architects produce **decision documents**, not code
- Code-reviewer is a **pure reader** — never modifies files
- Security-auditor is a **pure auditor** — never edits files
- git-manager is **stateless** — no memory
- Plans include an **Agents & Skills table** mapping each phase to the right agent

---

### Wednesday: Hooks Deep Dive

- [ ] Read Part 5 below (Hooks)
- [ ] Know all 8 lifecycle events:
  - [ ] `SessionStart` → session-init.cjs (project detection, env vars)
  - [ ] `UserPromptSubmit` → context-reminder.cjs (inject naming/plan context)
  - [ ] `SubagentStart` → subagent-init.cjs (~200 tokens, compact context)
  - [ ] `PreToolUse (files)` → scout-block.cjs + privacy-block.cjs
  - [ ] `PreToolUse (bash)` → build-gate-hook.cjs
  - [ ] `PostToolUse (write)` → post-index-reminder.cjs
  - [ ] `SubagentStop` → subagent-stop-reminder.cjs
  - [ ] `Stop` → session-metrics.cjs + lesson-capture.cjs + notify.cjs
- [ ] Understand exit codes: 0 = allow, 2 = block (all hooks fail-open)
- [ ] Know the privacy-block approval flow (APPROVED: prefix pattern)
- [ ] Know the build-gate bypass options (--skip-build, TRI_SKIP_BUILD=1)

**Self-test**: Walk through what happens when you run `git commit` in Claude Code.

---

#### CONTENT: PART 5 — Hooks (10 Lifecycle Hooks)

##### Hook Events → Hooks Wired

```
SessionStart        → session-init.cjs
UserPromptSubmit    → context-reminder.cjs
SubagentStart       → subagent-init.cjs
PreToolUse (file)   → scout-block.cjs + privacy-block.cjs
PreToolUse (bash)   → build-gate-hook.cjs
PostToolUse (write) → post-index-reminder.cjs
SubagentStop        → subagent-stop-reminder.cjs
Stop                → session-metrics.cjs + lesson-capture.cjs + notify.cjs
```

##### Detailed Hook Breakdown

**`session-init.cjs` (SessionStart)**

- Detects: project type, package manager, framework, git branch, Node/Python versions, git remote
- Writes env vars for all downstream hooks to consume
- Loads project-scoped secrets from `.claude/.env`
- _Example_: Open new session in C:/private → detects npm, Node v20, branch=master → writes `tri-ai-kit_PACKAGE_MANAGER=npm`, `tri-ai-kit_GIT_BRANCH=master` → prints "Session startup. Project: single-repo | PM: npm | ..."
- _Special case (compact)_: After `/compact`, prints approval-state warning so Claude re-confirms any pending `AskUserQuestion` gates before proceeding

**`context-reminder.cjs` (UserPromptSubmit)**

- Injects: naming pattern, active plan context, file paths (docs/plans/reports), docs index
- Throttled: re-injects at most every 5 minutes (avoid noise on rapid prompts)
- Markdown-formatted reminder appended to user message
- _Example_: You type "add a login endpoint" → hook appends "Active plan: plans/2026-04-06-auth.md, Plans path: C:/private/plans/" → Claude sees plan context without you repeating it every message

**`subagent-init.cjs` (SubagentStart)**

- Injects minimal context into every spawned agent (~200 tokens max)
- Contains: naming pattern, active plan, git branch info
- Optimized for token efficiency — no duplication from main context

**`scout-block.cjs` (PreToolUse: all file tools)**

- Blocks access to heavy directories: `node_modules`, `dist`, `.next`, `vendor`, `__pycache__`, `Pods`, `.git`, etc.
- Patterns defined in `.claude/.tri-ignore` (gitignore syntax, supports `!` negation)
- Blocks: file reads, directory listing; ALLOWS: build commands (npm build, go build, docker build)
- Exit code 2 = blocked, 0 = allowed
- _Example — blocked_: `Read "node_modules/lodash/index.js"` → .tri-ignore has `node_modules/` → EXIT 2: path blocked
- _Example — allowed_: `Bash "npm run build"` → build command → EXIT 0; `Bash "ls dist/"` (if dist/ ignored) → EXIT 2

**`privacy-block.cjs` (PreToolUse: all file tools)**

- Blocks reads of `.env`, secrets, credentials, API keys, private keys
- Flow: LLM tries to read `.env` → BLOCKED → LLM asks user via AskUserQuestion → user approves → LLM retries with `APPROVED:` prefix → hook strips prefix, allows read
- Can be disabled: `.tri-ai-kit.json` → `hooks.privacy.enabled = false`
- _Example flow_: (1) `Read ".env"` → EXIT 2: "PRIVACY BLOCK — use AskUserQuestion"; (2) User approves; (3) `Read "APPROVED:.env"` → EXIT 0: "✓ User-approved access to .env"

**`build-gate-hook.cjs` (PreToolUse: Bash)**

- Intercepts `git commit` and runs build verification first
- Blocks commit if build fails
- Bypass: `--skip-build` flag, `TRI_SKIP_BUILD=1` env var, or config
- Dedup: skips if `TRI_BUILD_GATE_RAN=1` (prevents double-running)
- _Example — pass_: `git commit -m "feat: add login"` → hook intercepts → runs npm build → build passes → EXIT 0: "build passed (3421ms) — commit allowed"
- _Example — fail_: `git commit -m "wip"` → TypeScript error → EXIT 2: "BUILD FAILED — commit blocked. Fix errors above, then retry. To bypass: add --skip-build"

**`post-index-reminder.cjs` (PostToolUse: Edit/Write/MultiEdit)**

- After writing to `docs/`, `reports/`, or `plans/` → reminds agent to update index file
- Throttled: 2 minutes per directory to avoid noise on multiple writes

**`session-metrics.cjs` (Stop)**

- Collects: session duration, git diff stats, tool usage counts
- Appends to `.kit-data/improvements/sessions.jsonl` (append-only JSONL)
- Auto-rotates at 1000 lines
- Silent on failure — never blocks session exit

**`lesson-capture.cjs` (Stop)**

- Evaluates session significance against thresholds: errors encountered, fix iterations ≥2, verification failures, new skills first-seen
- If significant: outputs prompt asking Claude to persist learnings via knowledge-capture skill
- Silent on failure — exits quietly if nothing to capture
- _Example — triggered_: Session had 3 fix iterations on same file → outputs "PATTERN: 3 fix iterations — rework pattern detected. Use knowledge-capture skill to persist these to docs/ if warranted."
- _Example — silent_: Clean session, no errors, no rework → EXIT 0 (nothing printed)

**`notify.cjs` (Stop)**

- Routes notifications to Discord, Telegram, Slack (extensible)
- Configured via env vars: `DISCORD_WEBHOOK`, `TELEGRAM_BOT_TOKEN`, `SLACK_WEBHOOK`
- Triggers on sessions reaching milestones, high-impact changes, security findings
- _Example (Telegram configured)_: `TELEGRAM_BOT_TOKEN=xxx` set → session ends → sends "Session complete — master branch" → logs "[notify] telegram: sent"
- _Example (no providers)_: No `TELEGRAM_*/DISCORD_*/SLACK_*` vars → exits silently

##### Why Hooks Exist — The Core Purpose

Hooks solve a fundamental LLM problem: **agents have no persistent memory, no environmental awareness, and no safety enforcement by default**. Without hooks, every session starts blind, agents can read secrets, broken builds get committed, and learnings are lost.

Hooks inject structure at the edges of the agent loop — before it acts, after it writes, and when it stops — without the agent needing to "remember" to do these things.

```
Without hooks: Agent → reads node_modules/ → context overflow → hallucinates
With hooks:    Agent → PreToolUse fires → scout-block blocks → agent stays focused
```

The design principle: **hooks are guardrails, not logic**. They don't make decisions for the agent — they enforce boundaries and supply context so the agent can make better decisions.

##### Hook Purpose + Agent Benefit Table

| Hook                  | Purpose                                 | Agent Benefit                                                                                        |
| --------------------- | --------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| `session-init`        | Bootstrap session environment           | Agent starts with full project awareness — no need to probe for package manager, branch, paths       |
| `context-reminder`    | Prevent plan drift mid-conversation     | Agent always references the active plan; never goes off-script on long sessions                      |
| `subagent-init`       | Propagate context across agent boundary | Spawned agents share naming conventions and plan context — consistent file naming without being told |
| `scout-block`         | Block heavy directory access            | Protects agent's context window from being flooded; forces focused, relevant reads                   |
| `privacy-block`       | Gate access to secrets                  | Agent never silently leaks credentials; establishes a safe, auditable approval protocol              |
| `build-gate-hook`     | Enforce build quality before commit     | Agent can't accidentally commit broken code; last-line-of-defense before git                         |
| `post-index-reminder` | Keep documentation indexes fresh        | Agent remembers to update indexes after writes — prevents doc rot without an explicit instruction    |
| `session-metrics`     | Record what happened each session       | Feeds data to lesson-capture and improvement summary — enables the system to learn over time         |
| `lesson-capture`      | Surface knowledge worth retaining       | Prompts agent to document patterns of failure and rework before context is lost                      |
| `notify`              | Close the feedback loop asynchronously  | Developer gets session outcome without monitoring the terminal — supports unattended long runs       |

##### Hook Role in the Agent Loop

```
SessionStart
  → session-init writes env vars
  ↓
UserPromptSubmit
  → context-reminder injects plan + paths into agent's working context
  ↓
Agent decides to act (read a file, run a command, write output)
  ↓
PreToolUse
  → scout-block: "Is this path safe to read?"   EXIT 2 = blocked
  → privacy-block: "Is this a secret file?"      EXIT 2 = approval required
  → build-gate-hook: "Is this a git commit?"     EXIT 2 = build must pass first
  ↓
Agent executes tool
  ↓
PostToolUse
  → post-index-reminder: "Did you write to docs/? Update the index."
  ↓
SubagentStart (if agent spawns a subagent)
  → subagent-init: injects ~200 tokens of naming + plan context
  ↓
SubagentStop
  → subagent-stop-reminder: surfaces handoff notes back to orchestrator
  ↓
Stop
  → session-metrics: record duration, diffs, tool usage
  → lesson-capture: evaluate if this session is worth documenting
  → notify: send outcome to Discord/Telegram/Slack
```

##### Which Aspects Help the Agent Most

**Context continuity** (`session-init` + `context-reminder` + `subagent-init`)

> The agent loop has no memory between prompts. These three hooks chain together to ensure the agent always knows: what project it's in, what plan it's executing, where to write files, and what naming pattern to use. Without them, every prompt starts from scratch.

**Context protection** (`scout-block`)

> Agent context windows are finite. If an agent reads `node_modules/`, it can consume tens of thousands of tokens on irrelevant content and hallucinate. scout-block prevents this proactively — the agent never even sees the blocked content.

**Safety enforcement** (`privacy-block` + `build-gate-hook`)

> Agents are optimistic by nature — they'll read whatever file seems useful, commit whenever they think they're done. These hooks enforce human-in-the-loop for secrets and build correctness. The agent cannot bypass these without explicit bypass flags.

**Knowledge compounding** (`session-metrics` + `lesson-capture`)

> Individual sessions are ephemeral. These hooks create a persistent record of patterns: how many fix iterations, what types of errors, which skills were new. Over time this data drives the biweekly improvement summary — the system gets better at diagnosing its own weaknesses.

**Fail-open design** (all hooks)

> Every hook exits 0 on unexpected failure. A crash in `session-init` never blocks Claude from starting. This is intentional: hooks are enhancements, not dependencies. The agent degrades gracefully if a hook fails, and the crash is logged to `.claude/hooks/.logs/hook-log.jsonl` for diagnosis.

---

### Thursday-Friday: Skills System

- [ ] Read Part 4 below (Skills)
- [ ] Understand: skills are passive knowledge modules, NOT executable
- [ ] Know skill-discovery: detects signals (`.tsx` → react-expert, `.go` → golang-pro)
- [ ] Know the ≤3 skills per task limit and why (token efficiency)
- [ ] Memorize all 13 user-invocable slash commands and their purpose
- [ ] Know plan flags: `--fast`, `--deep`, `--parallel`, `--arch`, `--validate`
- [ ] Know audit flags: `--code`, `--security`, `--a11y`, `--ui`

**Self-test**: If you ask Claude to build a React feature, which skills load?

**Deliverable**: Write a 1-page summary of the hook lifecycle from a blank session to commit

---

#### CONTENT: PART 4 — Skills (67 Modules)

##### What Skills Are

Skills are **passive knowledge modules** that agents load at task start. They are NOT executable — they give agents domain knowledge. Up to 3 skills are lazy-loaded per task based on platform detection.

##### How Skills Load

```
UserPromptSubmit
  → skill-discovery reads skill-index.json
  → detects signals: file types (.tsx → react-expert), frameworks, platforms
  → loads ≤3 matching skills into agent context
```

##### User-Invocable Skills (Slash Commands)

| Command                                              | Purpose                                              |
| ---------------------------------------------------- | ---------------------------------------------------- |
| `/plan [--fast/--deep/--parallel/--arch/--validate]` | Create implementation plans                          |
| `/plan --arch`                                       | Full Phase 3: spawn both architects, then plan       |
| `/plan --deep`                                       | Spawn 2 researchers, aggregate findings, create plan |
| `/cook`                                              | Implement feature (dispatches developer agent)       |
| `/review`                                            | Code quality review                                  |
| `/audit [--code/--security/--a11y/--ui]`             | Targeted audits                                      |
| `/test`                                              | Run test suites + coverage                           |
| `/debug [error]`                                     | Root cause analysis from stack trace                 |
| `/docs [--init/--update/--migrate]`                  | Generate/maintain documentation                      |
| `/git [flags]`                                       | Git workflows (commit, push, PR)                     |
| `/brainstorm [problem]`                              | Explore options, evaluate trade-offs                 |
| `/ask [question]`                                    | Answer questions about codebase                      |
| `/loop [interval] [command]`                         | Recurring tasks on interval                          |

##### Key Skills by Category

**Core (always active)**

- `core` — Decision authority, environment safety, documentation standards
- `skill-discovery` — Platform detection, skill selection

**Frontend**

- `react-expert` — React 18+, hooks, component architecture
- `nextjs-developer` — Next.js 14+ App Router, SSR, route handling
- `typescript-pro` — Advanced types, generics, utility types

**Backend**

- `golang-pro` — Concurrent patterns, goroutines, channels
- `postgres-pro` — Query optimization, schema design, migrations
- `fastapi-python` — Async patterns, dependency injection
- `microservices-architect` — Service boundaries, inter-service communication

**Architecture**

- `architecture-designer` — System design, ADRs, trade-offs
- `api-designer` — REST/GraphQL, OpenAPI specs, versioning
- `graphql-architect` — Schema design, resolvers, federation

**Quality**

- `code-review` — Code quality standards, security checks
- `security-reviewer` — OWASP Top 10, vulnerability assessment
- `test` — Test suites, coverage, E2E patterns

**Infrastructure**

- `infra-docker` — Dockerfile, Docker Compose, optimization
- `infra-cloud` — GCP/AWS/Azure architecture, DR strategies
- `kubernetes-specialist` — K8s deployment, scaling, orchestration

---

#### CONTENT: PART 4B — Agents vs Skills (Key Distinction)

##### One-Line Difference

|           |                                                                             |
| --------- | --------------------------------------------------------------------------- |
| **Agent** | _Who_ does the work — isolated subprocess, autonomous, returns output       |
| **Skill** | _How_ the work is done — context injection, shapes reasoning, no subprocess |

##### Output Comparison

|                 | **Agent**                                                        | **Skill**                                        |
| --------------- | ---------------------------------------------------------------- | ------------------------------------------------ |
| **Output**      | A message returned to orchestrator (text, code, diffs, analysis) | Instructions injected into active context window |
| **Process**     | Spawns new subprocess with own context window                    | No subprocess — runs inside caller's context     |
| **Autonomy**    | Makes decisions, calls tools, executes multi-step tasks          | Shapes behavior only — no tool calls             |
| **Parallelism** | Can run in parallel with other agents                            | Sequential                                       |
| **Activation**  | Via `Agent` tool                                                 | Via `Skill` tool or `skills:` frontmatter        |

##### Use Case Examples

**Agent use cases:**
| Scenario | Agent |
|---|---|
| "Build the login API" | `backend-developer` |
| "Review this PR for security issues" | `code-reviewer` |
| "Write tests for this service" | `tester` |
| "Plan this feature end-to-end" | `planner` |
| "Debug why this test fails" | `debugger` |

**Skill use cases:**
| Scenario | Skill |
|---|---|
| Agent needs React 18 patterns | `react-expert` |
| Agent needs Go concurrency conventions | `golang-pro` |
| Agent needs DB optimization rules | `postgres-pro` |
| Agent needs WCAG accessibility rules | `a11y` |
| Need structured thinking for hard problem | `sequential-thinking` |

##### Mental Model

```
Skill  = a hat you put on (expertise, conventions, patterns)
Agent  = a coworker you delegate to (autonomous, isolated, returns output)
```

##### Combined Flow (How They Work Together)

```
User: "Build a login page"
    │
    ├── Main conversation invokes Skill: plan
    │       → loads planning conventions into context
    │
    ├── Main conversation spawns Agent: frontend-developer
    │       → agent loads Skill: react-expert + web-frontend internally
    │       → agent reads files, writes components, returns diffs
    │
    └── Main conversation spawns Agent: code-reviewer
            → reviews what frontend-developer produced
            → returns: list of issues found
```

**Agents chain together. Skills stack inside each agent. Main conversation orchestrates both.**

##### Key Rule

> Agents **activate skills** via `skills:` frontmatter. Plans must include an **Agents & Skills table** mapping each phase to the correct agent _and_ its activated skills. Wrong assignment = wrong expertise loaded at runtime.

---

## WEEK 2: Prompt Engineering & Configuration

### Monday-Tuesday: Prompt Engineering for Claude Code

- [ ] Read Part 8 below (Prompt Engineering)
- [ ] Memorize the 4-part formula:
  - `<context>` — codebase state, existing patterns
  - `<task>` — specific action
  - `<requirements>` — constraints, type safety, performance
  - `<verification>` — expected output, edge cases
- [ ] Write 5 prompts using the formula:
  - [ ] Simple: implement a TypeScript function
  - [ ] Medium: add a new API endpoint with validation
  - [ ] Complex: refactor auth middleware to use new pattern
  - [ ] With edge cases: parsing user input with error handling
  - [ ] With agent dispatch: plan + arch flags for feature design
- [ ] Know XML tag gains: +23% accuracy, -40% hallucination, -35% bugs
- [ ] Practice naming skills in prompts (explicit skill activation)

---

#### CONTENT: PART 8 — Prompt Engineering (For Claude Code)

##### Evidence-Backed Techniques

| Technique                        | Improvement        | Application                 |
| -------------------------------- | ------------------ | --------------------------- |
| XML tags (`<context>`, `<task>`) | +23% accuracy      | Structure prompts to Claude |
| `<thinking>` blocks              | -40% hallucination | Complex reasoning tasks     |
| Explicit verification checklist  | -35% bugs          | AI-generated code review    |

##### The Prompt Formula

```
<context>
[What codebase, what file, what existing patterns]
</context>

<task>
[Specific action: implement X that does Y]
</task>

<requirements>
[Constraints: type safety, performance, existing patterns to follow]
</requirements>

<verification>
[How to know it's correct: test cases, edge cases, expected output]
</verification>
```

##### Agent Prompt Patterns

For Claude Code specifically:

- **Skill activation**: Name the skill in your prompt → agent loads it (`"Use the postgres-pro skill to optimize this query"`)
- **Agent dispatch**: Explicit routing → `"/plan --arch"` spawns both architects before planning
- **Plan resolution**: Branch names trigger plan lookup automatically (`feat/auth-system` → loads `260315-auth-system.md`)
- **Context passing**: Agents receive naming pattern + active plan in every SubagentStart — reference it

##### Interview Prompt Template

```
<context>
Team Stack: [Go/Python/TypeScript/Rust]
Frameworks: [Gin/FastAPI/Next.js/...]
Code Style Guide: [if provided]
Existing Patterns: [mention architectural patterns they use]
</context>

<task>
[Clear, one-sentence feature description]
</task>

<constraints>
- Must handle [X requests/sec]
- Error cases: [list them]
- Testing: [unit/integration/e2e required?]
- Timeline: [interviewer hint: how much time do I have?]
</constraints>

<acceptance_criteria>
- [ ] Code compiles/passes linter
- [ ] Tests pass
- [ ] No n+1 queries (if applicable)
- [ ] Error handling for [specific cases]
</acceptance_criteria>
```

##### Interview-Specific Prompting Tips

1. **Be verbose about constraints** — AI is better at optimization when it understands limits
2. **Mention that you'll explain it** — Signals to AI that output needs clarity, not just correctness
3. **Ask for test cases first** — Interview shows you think about verification
4. **Use follow-up prompts** — "Now optimize for latency" or "Add error handling for X"

---

### Wednesday: Settings & Configuration Mastery

- [ ] Read Part 6 below (Settings)
- [ ] Know `settings.json` structure: env, permissions, hooks, statusLine
- [ ] Know `.tri-ai-kit.json` key settings:
  - [ ] `codingLevel` (0-5 verbosity, -1 auto)
  - [ ] `plan.namingFormat` → `260315-1430-auth-system.md`
  - [ ] `plan.resolution.order` → session > branch-pattern
  - [ ] `plan.validation.mode` → asks 3-8 critical questions
  - [ ] `hooks.scout.enabled` / `hooks.privacy.enabled`
- [ ] Know `.tri-ignore` purpose (gitignore-spec patterns for scout-block)
- [ ] Know output verbosity levels 0-5

**Self-test**: Explain what happens on SessionStart from empty state to ready-for-prompts.

---

#### CONTENT: PART 6 — Settings & Configuration

##### settings.json Structure

```json
{
  "env": { "TRI_AGENT_STYLE": "full" },
  "permissions": {
    "allow": [
      "Bash(npm run test:*)",
      "mcp__xcodebuildmcp__*",
      "Bash(node .claude/hooks/*)"
    ]
  },
  "hooks": {
    /* 7 hook definitions wired to events */
  },
  "statusLine": {
    "type": "command",
    "command": "bash .claude/statusline-command.sh"
  }
}
```

##### .tri-ai-kit.json Configuration

| Setting                                        | Effect                                             |
| ---------------------------------------------- | -------------------------------------------------- |
| `codingLevel: -1`                              | Auto-detect verbosity (0=ELI5, 5=omniscient)       |
| `plan.namingFormat: "{date}-{slug}"`           | Generates `260315-1430-auth-system.md`             |
| `plan.resolution.order: ["session", "branch"]` | Session plan wins; then branch-pattern match       |
| `plan.validation.mode: "prompt"`               | Asks 3-8 critical questions before finalizing plan |
| `hooks.scout.enabled: true`                    | Directory access blocking active                   |
| `hooks.privacy.enabled: true`                  | Sensitive file blocking active                     |
| `project.packageManager: "npm"`                | Package manager for project detection              |

##### Output Verbosity Levels

| Level | Name         | Use Case                       |
| ----- | ------------ | ------------------------------ |
| 0     | ELI5         | Minimal jargon, beginners      |
| 1     | Beginner     | Friendly explanations          |
| 2     | Intermediate | Clear with technical detail    |
| 3     | Advanced     | Technical, assumes familiarity |
| 4     | Expert       | Sparse explanations            |
| 5     | God          | Assume omniscience             |

---

### Thursday-Friday: Live Practice Session 1

- [ ] Clone any open-source TypeScript/React project
- [ ] Use Claude Code to implement one feature:
  - [ ] Start with `/brainstorm` to explore options
  - [ ] Run `/plan --arch` to generate a plan
  - [ ] Run `/cook` to implement
  - [ ] Run `/review` after implementation
  - [ ] Run `/test` to verify
  - [ ] Run `/git` to commit
- [ ] **Document**: Which agents were spawned, which hooks fired, which skills loaded
- [ ] **Reflect**: Where did the AI give wrong output? How did you catch it?

**Deliverable**: Feature implemented with documented agent/hook/skill trace

---

## WEEK 3: Orchestration & System Design

### Monday-Tuesday: Multi-Agent Architecture Deep Dive

- [ ] Read Part 7 below (Multi-Agent Orchestration)
- [ ] Understand the orchestration rules (all 5):
  - [ ] Single intent → one agent
  - [ ] Multi-intent → project-manager first
  - [ ] 3+ independent tasks → subagent-driven-development
  - [ ] Agents never spawn agents
  - [ ] Document-driven execution (never skip code-reviewer)
- [ ] Map the tri_ai_kit agent chain for a full feature:
  - `planner → backend-architect + frontend-architect → developer → code-reviewer → tester → security-auditor → git-manager`
- [ ] Know the 5 failure modes and recovery strategies (from Part 7 table)
- [ ] Understand centralized vs decentralized tradeoffs
- [ ] Read your WORKFLOW.md (`/c/private/tri-ai-kit/WORKFLOW.md`) — know all 15 phases

---

#### CONTENT: PART 7 — Multi-Agent Orchestration (tri_ai_kit)

##### Architecture Pattern

```
Main conversation (orchestrator)
    ↓ Agent tool
Specialized agent (backend-developer, planner, etc.)
    ↓ reads skills
Skill modules (typescript-pro, postgres-pro, etc.)
    ↓ hooks fire
Lifecycle hooks (privacy-block, build-gate, etc.)
```

##### Orchestration Rules

1. **Single intent** → spawn one agent
2. **Multi-intent** → spawn `project-manager` first to decompose
3. **3+ independent tasks** → use `subagent-driven-development` (parallel execution)
4. **Agents never spawn agents** — only main conversation orchestrates
5. **Document-driven execution** — parse all agent references in plan, build full chain, execute sequentially (never skip code-reviewer after developer)

##### State Management Across Agents

- Context injected at SubagentStart via `subagent-init.cjs`
- Active plan name resolved by: session plan > branch-pattern match
- Naming pattern auto-injected to ensure consistent file naming
- Each agent has access to: project type, framework, git info, active plan

##### Failure Mode Analysis (tri_ai_kit)

| Failure Mode             | What Happens                            | Recovery                                                    |
| ------------------------ | --------------------------------------- | ----------------------------------------------------------- |
| Agent gets wrong context | Incorrect files modified                | Session plan wins over branch — check plan resolution order |
| Build fails at commit    | build-gate-hook blocks git commit       | Fix build, retry; or `--skip-build` if intentional          |
| Sensitive file accessed  | privacy-block blocks read               | User approval flow via AskUserQuestion                      |
| context overflow         | scout-block prevents heavy dir scan     | Add pattern to `.tri-ignore`                                |
| Silent hook failure      | Session continues (all hooks fail-open) | Check `.claude/hooks/.logs/hook-log.jsonl`                  |

##### Centralized vs Decentralized Tradeoffs

| Approach                 | Pros                                  | Cons                                              |
| ------------------------ | ------------------------------------- | ------------------------------------------------- |
| Centralized (tri_ai_kit) | Easier debugging, clear orchestration | Coordination overhead, bottleneck at orchestrator |
| Decentralized            | Scales better, fewer bottlenecks      | Harder to reason about, potential agent conflicts |

**Your answer in interviews**: "I chose centralized because with 22 agents, debugging decentralized state was too complex. The main conversation as orchestrator gives me a single control plane — I can trace every agent invocation through the hook log."

---

### Wednesday: Interview Format Deep Dive

- [ ] Read all 4 formats below:
  - [ ] Format 1: Agentic Problem-Solving (30-45 min) — solve a GitHub issue
  - [ ] Format 2: System Design with Agents (45-60 min) — design a pipeline
  - [ ] Format 3: Pair Programming (60-90 min) — live coding with AI
  - [ ] Format 4: Big Tech Multi-Round (2+ hours) — multi-phase assessment
- [ ] For Format 2 specifically: prep your tri_ai_kit design walkthrough

---

#### CONTENT: Interview Formats (2026)

##### The Shift: What Changed in Technical Interviews

| Old Model (Pre-2024)                          | New Model (2025-2026)                                                |
| --------------------------------------------- | -------------------------------------------------------------------- |
| Live coding: LeetCode-style algorithms        | **Pair programming**: Realistic feature work with AI                 |
| Language: Any (less important)                | **Language**: Specific (must know team's stack)                      |
| Tools: None (pen & paper or isolated editor)  | **Tools**: AI coding assistants (expected, encouraged)               |
| Evaluation: Correctness + complexity analysis | **Evaluation**: Design thinking + orchestration + prompt engineering |

##### Format 1: Agentic Problem-Solving (30-45 min)

**What you'll face:**

- "Implement a caching layer for our API endpoints"
- "Fix this failing test suite (given real repo context)"
- "Refactor this module to support multi-tenant architecture"

**What's evaluated:**

- Can you scope the problem correctly?
- Do you prompt the AI effectively?
- Can you orchestrate multiple agents (if needed)?
- Do you validate output rigorously?

**How to prepare:**

1. Practice with Cursor or Claude Code on real open-source repos
2. Time yourself: 30-min solutions require planning + execution
3. Learn to sketch architecture before coding
4. Master prompt iteration (first prompt rarely optimal)

**Timeline for a 45-min session:**

```
0-5 min:  Clarification — "Where should we store webhook subscriptions?"
5-10 min: Architecture sketch (whiteboard/Figma)
10-35 min: Implementation — Prompts 1, 2, 3 iteratively
35-40 min: Verification & Iteration — walk through code with interviewer
40-45 min: Debrief — "How would we scale this to 10k webhooks/sec?"
```

##### Format 2: System Design with Agent Orchestration (45-60 min)

**What you'll face:**

- "Design a code generation system with 5 specialized agents"
- "How would you architect a refactoring agent network?"
- "Build a deployment pipeline with agent-based QA"

**What's evaluated:**

- Understand agent roles, boundaries, communication patterns
- Know LangGraph/CrewAI concepts (state management, error handling)
- Can you design for scale? (100 concurrent agents?)
- Error propagation awareness

**Your answer structure:**

1. Draw architecture: boxes = agents, arrows = communication
2. Define state: what does each agent hand off?
3. Failure modes: "If Agent 2 generates broken code, Agent 3 rejects it, then..."
4. Scale: "At 1000 concurrent refactors, we'd need a message queue"
5. Tradeoffs: "Decentralized agents are faster but harder to debug"

##### Format 3: Pair Programming Interview (60-90 min)

**What you'll face:**

- Real IDE (usually Cursor or GitHub Copilot)
- Real repo (not a toy problem)
- Real feature: "Add a webhook system" or "Implement caching"

**What's evaluated:**

- **Workflow fluency**: Smooth IDE navigation, quick AI tool use
- **Prompt quality**: Clear, structured, iterative
- **Code confidence**: You understand what AI generated, can explain it
- **Problem-solving**: When AI fails, can you recover?
- **Ethical AI use**: You explain your AI usage transparently

**How to prepare:**

1. Practice with Cursor on 5-10 real feature branches (open-source projects)
2. Time yourself on 15-20 min sub-tasks (typical interview rhythm)
3. Record yourself (cringe-worthy but effective) — identify bad prompts
4. Study failure recovery: "AI generated the wrong thing, now what?"

##### Format 4: Big Tech "Agentic AI Thinking" (2+ hours)

**What you'll face:**

- Multi-round interviews where each round escalates complexity
- Round 1: Basic problem-solving with AI
- Round 2: Multi-agent orchestration design
- Round 3: Production deployment + observability

**What's evaluated:**

- Deep understanding of agentic patterns
- Can you optimize for cost/latency/correctness tradeoffs?
- Production-grade thinking (failures, rollbacks, monitoring)

##### Common Interview Scenarios & Recovery Strategies

**Scenario 1: AI Generated Incorrect Logic**

```
Interviewer: "This function doesn't handle the edge case where..."

You: "Good catch. I'll ask the AI to fix it. Here's the issue [explain].
Let me refine the prompt to explicitly include that case."
[Show the new prompt with edge case spelled out]
```

**Scenario 2: AI Too Slow / Took Wrong Approach**

```
Interviewer: "Hmm, this solution is O(n²). Can we optimize?"

You: "Yes, I see it now. Let me tell the AI to focus on [data structure/algorithm].
I'll add to the prompt: 'Use a hash map to track...' and ask for the approach first."
[Iterate with new prompt]
```

**Scenario 3: Interviewer Asks "Why Did AI Choose This Approach?"**

```
Interviewer: "Why does your solution use a linked list instead of an array?"

You: "Good question. The AI chose it because [re-read the code].
Actually, looking at our constraints, an array would be faster.
Let me adjust the prompt to explicitly prefer arrays when order is stable."
[Show iteration]
```

##### What Interviewers Are Silently Evaluating

**Green Flags:**

- You read the code before running it
- You catch bugs before the interviewer does
- You iterate on prompts when output is wrong
- You explain tradeoffs (speed vs readability)
- You ask about their stack's conventions
- You're transparent: "I'm using AI because..."

**Red Flags:**

- You blindly copy-paste AI output without reading
- You treat AI output as gospel (no validation)
- You can't explain what the code does
- You blame the AI when something is wrong
- You have no plan B when AI fails
- You hide the fact that you used AI

##### Company-Specific Interview Styles

| Company Type              | Style                                         | Focus                                                   | Prep                                                     |
| ------------------------- | --------------------------------------------- | ------------------------------------------------------- | -------------------------------------------------------- |
| Google / Meta / Apple     | Agentic system design + production thinking   | Scale, observability, failure modes                     | Study orchestration patterns, draw architecture diagrams |
| Stripe / Netflix / Airbnb | Real problem from codebase + pair programming | Can you ship a feature? Do you validate? Communication? | Practice with real repos, iterate on prompts             |
| Early-Stage Startups      | Build something real in 60 minutes            | Speed, pragmatism, can you own a feature?               | Workflow fluency (IDE + AI tool smoothness)              |
| Established Mid-Size      | Mixed (pair programming + system design)      | Fit with team practices, understanding of their stack   | Study their tech blog, use their stack in practice       |

##### Pre-Interview Skill Checklist

**Technical Skills**

- [ ] **IDE Fluency**: 10+ features of Cursor/Copilot (Go to Def, Composer Mode, multi-file edits)
- [ ] **Prompt Engineering**: Can write clear, structured prompts that AI understands
- [ ] **Code Validation**: Can quickly validate AI output (read tests, trace logic)
- [ ] **Architecture Sketching**: Can draw agent diagrams on whiteboard/Figma
- [ ] **Error Recovery**: Can debug and fix failed AI outputs

**Domain Knowledge**

- [ ] **SWE-Bench**: Familiar with 10 real problem types (data structures, concurrency, API design)
- [ ] **Framework Knowledge**: Basic LangGraph/CrewAI concepts (state, graph nodes, task execution)
- [ ] **Your Stack**: Deep knowledge of team's language, frameworks, deployment
- [ ] **Benchmarks**: Aware of latest model capabilities (Claude Opus 4.5 vs GPT-4o vs Cursor's claims)

**Soft Skills**

- [ ] **Communication**: Explain your AI usage clearly to the interviewer
- [ ] **Ethics**: Articulate why starting with your thinking matters
- [ ] **Humility**: Acknowledge tool limitations
- [ ] **Curiosity**: Ask about their agentic infrastructure

##### Red Flags in Interview Questions

If interviewer asks: "Don't use AI tools for this round"
→ **Clarify**: Are you testing my algorithm skills (fair) or gatekeeping on AI use (outdated)?

If interviewer dismisses AI tool use:
→ **Reframe**: "I see. I use AI for 70% of syntax/boilerplate, 30% is my design. Is that alignment?"

If they ask: "Can you solve this without AI?"
→ **Answer honestly**: "I could, but it would take 45 min instead of 15. In production, I'd use AI. Should I?"

---

### Thursday-Friday: Mock Interview Session

- [ ] **Format 1 Practice**: Pick a GitHub issue, solve in 30 min
  - [ ] Use `/plan` first, explain your prompt to imaginary interviewer
  - [ ] Show validation steps: "Here's how I verify the AI output"
  - [ ] Handle one intentional AI failure: refine prompt and show iteration
  - [ ] Timer + record yourself
- [ ] **Format 2 Practice**: Design a code generation pipeline on whiteboard
  - [ ] Include: agent roles, file ownership matrix, failure recovery, hooks used
  - [ ] Cover: why centralized vs decentralized, what metrics to track

**Deliverable**: 1 recorded mock interview

---

## WEEK 4: Stories, Scenarios & Final Polish

### Monday-Tuesday: Story Development

Prepare 3 STAR-format stories (Situation/Task/Action/Result):

**Story 1: Multi-Agent Orchestration (tri_ai_kit)**

- [ ] Challenge: Coordinating 22 agents — what was hardest?
  - Answer: Context propagation + file ownership conflicts in parallel execution
- [ ] Solution: SubagentStart hook injects context; file ownership matrix prevents conflicts
- [ ] Result: What you learned about orchestration at scale

**Story 2: Hook-Based Safety (Build Gate)**

- [ ] Challenge: AI-generated code that would have broken the build
- [ ] Solution: build-gate-hook caught it at `git commit` before it reached remote
- [ ] Result: How hooks changed your confidence in AI-generated code

**Story 3: Prompt Engineering Iteration**

- [ ] Challenge: Agent produced wrong output (hallucinated DB schema or API field)
- [ ] Solution: Added explicit `<context>` block with actual schema; switched to `--arch` flag for planning
- [ ] Result: Your current structured prompt formula

- [ ] Practice each story in < 2 minutes
- [ ] Record yourself telling each

---

### Wednesday: Common Questions Practice

- [ ] Practice Tier 1 answers (see Part 9 below):
  - [ ] "How do you validate AI-generated code?" → mention code-reviewer agent + build-gate
  - [ ] "Walk me through your prompt engineering process" → 4-part XML formula
  - [ ] "What's one failure case?" → hallucinated schema, caught by build-gate
  - [ ] "How do you decide which tool?" → Cursor (daily), Claude Code (lifecycle integration + hooks)
- [ ] Record yourself — cringe → improve

---

### Thursday: Orchestration Scenarios Practice

- [ ] Practice Tier 2 answers:
  - [ ] "Design a code generation system" → agent chain + file ownership + hooks
  - [ ] "How would you handle agent failures?" → fail-open hooks + hook log + build-gate
  - [ ] "Centralized vs decentralized?" → your answer: centralized for debuggability
- [ ] Sketch on whiteboard: agent flow for full feature delivery
- [ ] Talk through tri_ai_kit design (15 min) — record it

---

### Friday: Final Sharp Check

- [ ] Re-read Quick Reference table (5 min) — see below
- [ ] Review Part 9 talking points (10 min) — see Interview Q&A section
- [ ] Read through hook lifecycle one more time (5 min)
- [ ] Walk through one complete mock interview (30 min)
- [ ] Prepare 3 questions to ask them:
  - [ ] "How do you handle agent failures in your production pipeline?"
  - [ ] "What lifecycle hooks do you use to enforce code quality?"
  - [ ] "How do you manage context propagation when you scale to many agents?"

**Deliverable**: Stories + scripts + whiteboard diagram of tri_ai_kit architecture

#### 1 Week Before: Final Prep Checklist

- [ ] Practice 2 pair-programming scenarios with Cursor (30 min each)
- [ ] Design 1 multi-agent system on whiteboard
- [ ] Review SWE-bench problem archetypes
- [ ] Write 3 strong prompts for your stack
- [ ] Practice explaining AI output (record yourself)
- [ ] Review interviewer's tech blog / GitHub (understand their pain points)
- [ ] Get good sleep (clear thinking > grinding)

---

## INTERVIEW DAY CHECKLIST

### Morning (2 hours before)

- [ ] Read Key Numbers table below (5 min)
- [ ] Read your 3 STAR stories once
- [ ] Do 1 practice prompt using 4-part formula (5 min)
- [ ] Review hook lifecycle in your head: SessionStart → … → Stop

### During Interview

- [ ] **Listen** to the full problem before touching code or tools
- [ ] **Explain your intent**: "I'll use `/plan --arch` first because this involves API design"
- [ ] **Name the agent**: "This dispatches to backend-architect to produce an API contract"
- [ ] **Validate early**: trace AI output before accepting it, mention the code-reviewer
- [ ] **Iterate calmly**: "The first output missed edge cases — I refined the `<requirements>` block"
- [ ] **Hook awareness**: "The build-gate would catch this before commit — let me show that"
- [ ] **Ask about their stack**: shows genuine interest

### Red Flags to Avoid

- [ ] Don't say "The AI generated it" without validation step
- [ ] Don't use tools you haven't practiced (only use Claude Code confidently)
- [ ] Don't forget: agents never spawn agents (only main conversation orchestrates)
- [ ] Don't conflate skills and agents — skills are passive, agents are active
- [ ] Don't forget the file ownership matrix when designing agent pipelines
- [ ] Don't ignore hooks — they're your safety layer, mention them

---

## QUICK REFERENCE: Claude Code Key Numbers

| What                           | Number      | Why It Matters        |
| ------------------------------ | ----------- | --------------------- |
| Agents in tri_ai_kit           | 22          | Your differentiator   |
| Skills total                   | 67          | Lazy-loaded knowledge |
| Max skills per task            | 3           | Token efficiency      |
| Hook lifecycle events          | 8           | Full session coverage |
| Context-reminder throttle      | 5 min       | Avoids token waste    |
| Post-index throttle            | 2 min       | Per directory         |
| git-manager tool calls max     | 2-4         | Token efficiency      |
| Subagent context budget        | ~200 tokens | Injection limit       |
| Sessions.jsonl rotation        | 1000 lines  | Auto-rotates          |
| XML tag accuracy gain          | +23%        | Structured prompts    |
| `<thinking>` hallucination cut | -40%        | Complex reasoning     |
| Verification checklist bug cut | -35%        | AI code review        |
| Claude Opus SWE-bench          | 80.9%       | Benchmark             |
| Cursor speed advantage         | 30%         | vs Copilot            |
| Multi-agent inquiry surge      | 1,445%      | 2024→2025             |

---

## CLAUDE CODE COMMAND CHEATSHEET

```
/plan              → Create implementation plan (fast mode)
/plan --deep       → Research first, then plan (spawns 2 researchers)
/plan --arch       → Architecture phase: spawn both architects first
/plan --parallel   → Dependency-aware parallel execution plan
/plan --validate   → Ask 3-8 critical questions about existing plan

/cook              → Implement feature (dispatches developer)
/review            → Code quality review (code-reviewer agent)
/audit --code      → Code quality audit
/audit --security  → OWASP Top 10 + secrets scan
/audit --a11y      → WCAG 2.1 accessibility audit
/audit --ui        → Design system compliance audit

/test              → Run test suites + coverage
/debug [error]     → Root cause analysis
/docs --init       → Generate initial documentation
/docs --update     → Update existing docs
/git               → Stage, commit, push, create PR

/brainstorm        → Explore options and trade-offs
/ask [question]    → Answer questions about codebase
/loop [interval]   → Run recurring task on interval
```

---

## HOOK QUICK REFERENCE

```
Event             Hook                   Purpose
─────────────────────────────────────────────────────────────────
SessionStart      session-init           Detect project, write env vars
UserPromptSubmit  context-reminder       Inject naming/plan context (5min throttle)
SubagentStart     subagent-init          Inject ~200 token compact context
PreToolUse/files  scout-block            Block node_modules/dist/vendor etc
PreToolUse/files  privacy-block          Block .env/secrets (approval flow)
PreToolUse/bash   build-gate             Validate build before git commit
PostToolUse/write post-index-reminder    Remind to update index files (2min throttle)
SubagentStop      subagent-stop-reminder Cleanup reminders
Stop              session-metrics        Collect duration/diff stats → sessions.jsonl
Stop              lesson-capture         Capture learnings if session significant
Stop              notify                 Discord/Telegram/Slack notifications
```

---

## AGENT CHAIN: FULL FEATURE DELIVERY

```
/plan --arch
  → planner (opus)
    → backend-architect (opus)  ← produces API contract
    → frontend-architect (opus) ← produces routing + state plan
    → synthesizes phased plan with file ownership matrix

/cook (or planner dispatches)
  → backend-developer (sonnet) ← implements API, DB schema
  → frontend-developer (sonnet) ← implements components, routing
    (file ownership prevents conflicts in parallel)

/review
  → code-reviewer (sonnet) ← reads only, never modifies
    applies: SEC/PERF/TS/LOGIC/DEAD/ARCH/STATE rules

/audit --security
  → security-auditor (sonnet) ← reads only, never modifies
    checks: OWASP Top 10, secrets, CVEs, auth gaps

/test
  → tester (haiku) ← runs suites, coverage, E2E

/git
  → git-manager (haiku) ← 2-4 tool calls, stages, commits, pushes
    (build-gate fires on git commit → validates build first)

[session Stop]
  → session-metrics → lesson-capture → notify
```

---

## INTERVIEW Q&A: ALL TIERS

### Tier 1: AI Tool Usage

**"How do you validate AI-generated code?"**

> "My approach depends on how much domain knowledge I have.
>
> **For Next.js** (my strength): I run `/audit --code` against the plan first — that dispatches the `audit` skill which activates `knowledge-retrieval` before checking anything, so it has full project context. Then after implementation, `/review` runs the `code-review` skill as a subagent — pure reader, never modifies files, checks 7 rule categories: SEC, PERF, TS, LOGIC, DEAD, ARCH, STATE. I also read it personally: trace the critical paths, check the component architecture, verify it aligns with the plan spec.
>
> **For Python + GraphQL** (zero prior knowledge): same audit + code-review pass, but I layer on testing because I can't catch logic errors by reading alone. I write test cases, run them manually through the GraphQL playground and Postman, then run E2E tests.
>
> When something breaks — real example: on first implementation, every GraphQL query failed — I don't just re-run the AI and hope. I ask the agent for root cause analysis first: why does this error occur, what's the actual bottleneck? Once I understand the problem, I ask for fix options with pros and cons for each solution. I pick the one that makes architectural sense, not just the one that compiles. If it works, done. If not, I switch to research mode — ask the agent to find actual documentation or source-level resources about the failure, not just pattern-match from its training data.
>
> The discipline: understand the error before applying any fix."

**"Walk me through your prompt engineering process"**

> "I use a 4-part structure: context (codebase state, existing patterns), task (specific action), requirements (constraints, type safety, performance), verification (expected output, edge cases). XML tags around each section improve accuracy by ~23% — Claude parses structured input more reliably than prose."

**"What's one failure case where AI gave you wrong output?"**

> "In a backend app, all test cases passed — but when I triggered the actual GraphQL query in Postman and the GraphQL docs, it threw errors that the tests never caught. The AI had generated code that looked correct in isolation but failed at the integration layer. My recovery: I ran the audit skill to surface structural bottlenecks, then the debug skill to trace the exact failure point. Crucially, I never just applied the first fix the AI suggested — I always asked it to explain the root cause first, so I actually understood the problem. Then I had it present multiple solutions with pros and cons, and I made the final call based on trade-offs. Lesson: passing tests don't guarantee correctness at the integration layer — AI optimizes for what you test, not what you didn't think to test."

**"What's the difference between agents and skills in your system?"**

> "Agents are autonomous subprocesses — I spawn them via the Agent tool, they get their own context window, call tools, make decisions, and return a result. Skills are passive context injections — no subprocess, no tool calls, just domain expertise loaded into whoever is currently running.
>
> The mental model: a skill is a hat you put on (conventions, patterns, expertise). An agent is a coworker you delegate to (autonomous, isolated, returns output).
>
> Output is also different: an agent returns a message — code diffs, analysis, findings. A skill produces nothing visible — it shapes the agent's reasoning and what it knows.
>
> In practice: when I want to _build_ something, I spawn an agent. When I want that agent to _know_ something (React patterns, Go conventions, security rules), I load a skill. The agent declares its skills in frontmatter — `skills: [react-expert, typescript-pro]` — and they load automatically at task start."

**"How do you decide which tool to use?"**

> "Cursor for IDE-based work — multi-file awareness, speed. Claude Code for architectural decisions where I need reasoning steps. The build-gate hook in Claude Code gives me confidence that commits are validated; Cursor doesn't have that level of lifecycle integration."

### Tier 2: Orchestration & Architecture

**"Design a code generation system with N agents"**

> Walk through: planner (decompose → create phases with file ownership), backend-architect + frontend-architect (parallel Phase 3), developer agents (implement with file ownership boundaries), code-reviewer (mandatory post-implementation), security-auditor (pre-launch gate). Key: file ownership matrix prevents conflicts in parallel execution.

**"How would you handle agent failures in a production pipeline?"**

> "In tri_ai_kit, all hooks are fail-open (exit code 0). The session continues even if a hook fails — logged to `.claude/hooks/.logs/hook-log.jsonl`. For agent failures, the pattern is: surface the failure in the hook log, halt at the build-gate before it reaches git, require human review. Never auto-retry on a failed security audit."

**"Compare centralized vs decentralized agent coordination"**

> "Centralized (what I use): single orchestrator = easy debugging, clear trace, coordination overhead. Decentralized: scales better, fewer bottlenecks, harder to reason. I chose centralized for tri_ai_kit because 22 agents is complex enough — I need a single control plane to trace invocations."

### Tier 3: Advanced

**"Describe your multi-agent system architectural decisions"**

> "22 agents, each with declared skill bindings. Skill lazy-loading: up to 3 per task based on file-type signals — `.tsx` → react-expert, `.go` → golang-pro. Hooks enforce safety at the edge: scout-block prevents context overflow from heavy directories, privacy-block requires user approval for secrets, build-gate validates before commit. The pattern: agents are specialized, hooks are guardrails."

**"How do you manage context propagation across agent boundaries?"**

> "Two mechanisms: SubagentStart hook injects ~200 tokens (naming pattern, active plan, git info) into every spawned agent. UserPromptSubmit hook injects full session context into main conversation. Both are throttled to prevent token waste. The plan resolution order (session > branch-pattern) ensures every agent works from the same plan."

**"How would you scale to 100 concurrent agents?"**

> "Current architecture: centralized orchestrator, sequential phase execution. To scale: move to subagent-driven-development for independent phases (already built), add file ownership matrix to prevent conflicts, distribute hook execution, add agent-level circuit breakers for failure isolation. The challenge isn't agents — it's context management at scale."

---

## YOUR SPECIFIC ADVANTAGES

1. **tri_ai_kit**: Built a 22-agent orchestration system — rare direct experience, not theoretical
2. **Hooks**: Implemented lifecycle hooks for safety (privacy-block, build-gate) — shows production thinking
3. **File ownership matrix**: Solved parallel agent conflicts before they happened — proactive architecture
4. **Fail-open design**: Hooks never block session — operational resilience mindset
5. **Context injection**: SubagentStart ~200 token budget — performance-aware design

### Critical Talking Points

**On validation:**

> "I never trust AI output verbatim. I have the code-reviewer agent, but I also read generated code personally — especially security paths. The build-gate hook is the last line before commit."

**On orchestration:**

> "The hardest part wasn't agent design — it was state management. When agent A fails, the session continues (fail-open). But the build-gate catches anything broken before it reaches git. The hook log is my audit trail."

**On tool selection:**

> "Cursor for speed in daily work. Claude Code for tasks that need reasoning steps and lifecycle integration — the hooks give me guardrails that a simple IDE plugin can't."

**On prompt engineering:**

> "I use XML-structured prompts: context, task, requirements, verification. For agents, I name the skill I want to activate. For planning, I use `--arch` flag to ensure architects run before implementation phases."

---

## SUCCESS METRICS

You're interview-ready when:

- [ ] You can name all 22 agents, their model tier, and their key responsibility
- [ ] You can trace the full hook lifecycle without notes
- [ ] You can explain why hooks are fail-open and why that matters
- [ ] You can describe the privacy-block approval flow step by step
- [ ] You can design a multi-agent pipeline on whiteboard with file ownership matrix
- [ ] You can describe 3 tri_ai_kit stories fluently in STAR format
- [ ] You can explain skill lazy-loading (≤3 per task, platform signal detection)
- [ ] You can distinguish: skills (passive) vs agents (active) vs hooks (lifecycle)
- [ ] You know all 13 slash commands and their flags
- [ ] You're comfortable saying "I don't know, but here's how I'd find out"

---

## MARKET CONTEXT & TOOL LANDSCAPE

### Market Snapshot (2026)

| Tool               | Model                           | Best For                                     | Price      | Speed           |
| ------------------ | ------------------------------- | -------------------------------------------- | ---------- | --------------- |
| **GitHub Copilot** | IDE plugin (VS Code, JetBrains) | Quick completions, IDE-native workflows      | $10/mo     | Baseline        |
| **Cursor**         | Full IDE fork                   | Daily coding, multi-file refactors           | $20/mo     | 30% faster      |
| **Claude Code**    | Terminal agent                  | Complex feature work, codebase understanding | $20-200/mo | 45-60 min tasks |

### SWE-Bench Performance (2026)

| Model           | Score | Context   |
| --------------- | ----- | --------- |
| Claude Opus 4.5 | 80.9% | Leading   |
| GPT-4o          | 49%   | Declining |
| Qwen-3 32B      | 34%   | Legacy    |

**Caveat**: Benchmarks overestimate real-world performance. Complex multi-step projects require orchestration, not just task completion.

### Industry-Specific Tool Usage Patterns

Experienced developers use **2.3 tools on average**:

- **Daily coding + feature work**: Cursor (IDE) + Claude Code (complex tasks)
- **Large refactors**: Cursor Composer Mode (multi-file coordination)
- **Exploration**: GitHub Copilot CLI (free, quick lookups)
- **Production agents**: LangGraph (state durability) + custom monitoring
- **Rapid prototyping**: CrewAI (quick to spin up)

### Multi-Agent Development in IDEs

**GitHub Copilot CLI (Feb 2026 GA)**: specialized auto-delegating agents: Explore, Task, Code Review, Plan

**Cursor 3 (April 2, 2026)**: Agents Window with up to 8 concurrent agents. Automations system triggers agents on codebase changes, Slack messages, timers.

**VS Code 1.109**: runs Claude and Codex agents locally or cloud-delegated.

### Adoption Surge

**1,445% surge** in multi-agent system inquiries (Q1 2024 → Q2 2025) — Gartner

### 2026 Hiring Expectations by Role

| Role                      | Expectations                                                                        |
| ------------------------- | ----------------------------------------------------------------------------------- |
| **Backend Engineering**   | LangGraph patterns, multi-agent orchestration, system design with agent boundaries  |
| **Frontend Engineering**  | Cursor Composer Mode, component orchestration, prompt engineering for UI patterns   |
| **Full-Stack**            | Hybrid workflows (IDE + agent), understanding agentic architectures, ethical AI use |
| **DevOps/Infrastructure** | Agent-based deployment, observability across multi-agent systems                    |

### Tool Selection by Approach

| Approach                       | Pros                                            | Cons                                                        | Interview Prep Impact                                         | Recommendation               |
| ------------------------------ | ----------------------------------------------- | ----------------------------------------------------------- | ------------------------------------------------------------- | ---------------------------- |
| **IDE-first (Cursor/Copilot)** | Low barrier, familiar, real-time feedback       | Limited to file scope, weaker multi-file                    | Good for daily practice; interviews expect IDE fluency        | Recommended for all          |
| **Agent-first (Claude Code)**  | Full codebase understanding, async execution    | No real-time IDE feedback, not suitable for live interviews | Poor for live interview, excellent for system design thinking | For post-offer deep work     |
| **Hybrid (Cursor + Claude)**   | Real-time IDE flow + powerful async agents      | Tool switching overhead                                     | Prepares for modern multi-tool workflows                      | **Best for 2026 interviews** |
| **Framework-deep (LangGraph)** | Production-grade durability, fine-grained state | Overkill for startups, long ramp-up                         | Separates junior from senior in system design rounds          | For backend-focused roles    |
| **No AI tools**                | Proves core algorithm skills                    | 40% slower, misaligned with modern practice                 | Increasingly misaligned with industry norm                    | Not recommended              |

---

## AGENTIC FRAMEWORK LANDSCAPE

### Framework Comparison

| Framework     | Architecture           | Strength                                | Token Cost   | Speed   | Best For                                                 |
| ------------- | ---------------------- | --------------------------------------- | ------------ | ------- | -------------------------------------------------------- |
| **LangGraph** | Graph-based, DAG       | Production durability, state management | Mid          | Fastest | Complex stateful workflows, observability                |
| **CrewAI**    | Role-based multi-agent | Intuitive API, low barrier to entry     | 3x LangChain | Slowest | Business workflow automation, quick prototypes           |
| **AutoGen**   | Conversation-based     | Human-in-the-loop, async coordination   | —            | —       | Maintenance mode (merged into Microsoft Agent Framework) |

**Key shift**: Graph-based (LangGraph) replacing conversation-based (AutoGen) for production workloads.

### Orchestration Patterns in Production (2026)

1. **Decentralized**: Agents operate autonomously, share via message buses. Harder to coordinate, easier to scale.
2. **Hierarchical**: High-level agents supervise teams of workers. Clear oversight. Better for complex projects.
3. **Production line model**: Plan → Spawn → Monitor → Verify → Integrate → Retro. Asynchronous execution.

### Recommended Hybrid Workflow (2026)

**Daily**: Use Cursor for IDE work + real-time feedback

**Feature work**: Dispatch to Claude Code terminal agent for multi-file refactors

**Production**: Orchestrate with LangGraph for durability

**Result**: 40% faster features, better code quality, more confident interviews

---

## QUESTIONS TO ASK EASYGDS LINE MANAGER

> **Context**: Frontend developer role focused on AI for coding. easyGDS is a travel tech company — GDS (Global Distribution System) means legacy upstream APIs, deeply nested responses, complex transformation layers. You've already built a Next.js BFF + flight booking UI against Duffel. Ask like someone who's done this work, not someone learning about it.

---

### Category 1: How AI Actually Works on Their Team (Most Important)

These reveal whether "AI focus" means "we gave everyone a Copilot license" or something deeper.

**1. How do you validate AI-generated code before it merges? Is there a review gate, or is it left to individual developers?**

- _Why ask_: This is where you can bring in build-gate and code-reviewer naturally. Also reveals their quality culture.
- _What to listen for_: Structured review process vs "we trust the dev to check it". A strong team will have opinions here.

**2. Is the "AI for coding" focus about using AI to build faster, or are there plans to build AI-powered features into the product itself?**

- _Why ask_: Two very different roles. One is developer tooling/workflow. The other is building AI features (search, recommendations, itinerary generation). You want to know which one this is.
- _What to listen for_: Clarity here sets expectations for the role scope.

---

### Category 2: Frontend Stack & Technical Context

**3. What's the current state of the frontend stack — Next.js App Router or Pages, TypeScript strict mode, and how far along is the design system?**

- _Why ask_: You've built on Next.js 16 / React 19 / Tailwind v4 — you want to know how close the stack is. Also signals maturity of the codebase.
- _What to listen for_: App Router + TypeScript strict = modern. Pages Router + JS = legacy catch-up work. Either is fine, but you want the real picture.

**4. Where does the frontend connect to the GDS layer — directly to upstream APIs, through a BFF, or via a GraphQL gateway?**

- _Why ask_: You built a BFF. If they have direct coupling, there's technical debt and likely complexity. If they have a well-designed BFF, you can hit the ground running.
- _What to listen for_: Whether they've solved the "legacy GDS normalization" problem or are still living with it.

**5. What are the biggest pain points on the frontend right now that you're hoping this hire helps address?**

- _Why ask_: Reverse-engineers what success looks like in month 3. Lets you calibrate your pitch.
- _What to listen for_: "We need someone to build features faster" vs "we need someone to help us build the AI workflow" — very different signals.

---

### Category 3: Team Culture & Autonomy

**6. How much ownership does the team have over which AI tools and workflows to adopt — is it top-down (IT/procurement selects tools) or bottom-up (developers choose their own stack)?**

- _Why ask_: You've built your own hooks system and multi-agent workflow. If the team is locked into one enterprise tool, that's a constraint.
- _What to listen for_: "We choose our own tools" vs "we use what the company approves". Neither is a dealbreaker, but it shapes what you can contribute.

**7. How does the team approach code review for AI-generated code specifically — do you review it differently, or treat it the same as hand-written code?**

- _Why ask_: Reveals team maturity around AI. Teams that "treat it the same" haven't thought through the failure modes. Teams that have differentiated review for AI output are ahead.
- _What to listen for_: Thoughtful answer = good sign. Blank stare = opportunity to contribute.

---

### Category 4: Growth & Longer Term

**8. How does the team share learnings about what works and what doesn't with AI-assisted development — is there a retro process, a shared knowledge base, or does each developer figure it out independently?**

- _Why ask_: Your session-metrics + lesson-capture hook system is specifically about compounding learnings. If they don't share knowledge, the role is less leveraged.
- _What to listen for_: Shared docs, demos, structured retros = good. "We just Slack each other" = early-stage but open. "Each developer handles their own setup" = siloed.

---

### TOP 5 TO ACTUALLY ASK (if time is limited)

| Priority | Question                                                                                                    | Why This One                                       |
| -------- | ----------------------------------------------------------------------------------------------------------- | -------------------------------------------------- |
| **#1**   | "What does your AI development workflow look like day to day — individual tool use, or shared conventions?" | Reveals role depth immediately                     |
| **#2**   | "How do you validate AI-generated code before it merges?"                                                   | Lets you talk build-gate + code-reviewer naturally |
| **#3**   | "Is the AI focus about using AI to build, or building AI features into the product?"                        | Clears up role scope                               |
| **#4**   | "Where does the frontend connect to the GDS layer — BFF, direct, or gateway?"                               | Shows domain knowledge, helps you calibrate fit    |
| **#5**   | "What are the biggest frontend pain points you're hoping this hire addresses?"                              | Reverse-engineers success metrics                  |

---

### WHAT YOUR QUESTIONS SIGNAL

When you ask Q1 and Q2, you're signaling: _"I've thought about AI workflow at a systems level, not just as a tool user."_

When you ask Q3, you're signaling: _"I need clarity on scope to tell you how my experience fits."_

When you ask Q6, you're signaling: _"I understand GDS complexity — I've already built this layer."_

Don't ask about salary, perks, or remote policy in the first-round manager interview — those belong in HR rounds or after an offer.

---

## FINAL WORDS

You built tri_ai_kit. You implemented hooks that other developers only read about. You designed a 22-agent system with fail-open safety, context injection, and build-gate validation.

Most candidates can talk about AI tools. You can talk about building the infrastructure underneath them.

The interview is testing:

1. **Can you think clearly?** → Show structured prompts, explain your reasoning
2. **Can you direct AI effectively?** → Show agent dispatch, skill activation, plan flags
3. **Can you validate output rigorously?** → Show build-gate, code-reviewer, hook log
4. **Can you reason about complex systems?** → Show file ownership matrix, failure modes, centralized vs decentralized tradeoff

Go show them what you built.

---

**Updated**: April 6, 2026 | **Source documents merged**: INTERVIEW_PREP_CHECKLIST.md, INTERVIEW_RESEARCH_SUMMARY.md, ai-coding-tools-quick-reference.md, ai-coding-interview-guide-2026.md, research-ai-coding-tools-2026-04-05.md
