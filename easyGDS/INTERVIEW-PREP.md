# easyGDS Interview Preparation
**Date**: Monday, April 7, 2026  
**Focus**: AI for coding — Deep dive into Claude Code & how it was used to build the assessment projects

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Claude Code — Deep Dive](#2-claude-code--deep-dive)
3. [Hooks System — What Each Hook Does](#3-hooks-system--what-each-hook-does)
4. [Multi-Agent System (tri_ai_kit)](#4-multi-agent-system-tri_ai_kit)
5. [Frontend — SkyBook](#5-frontend--skybook)
6. [Backend — Flight Booking BFF](#6-backend--flight-booking-bff)
7. [AI Workflow — How I Used Claude Code](#7-ai-workflow--how-i-used-claude-code)
8. [Key Technical Decisions](#8-key-technical-decisions)
9. [Other AI Coding Tools (Brief)](#9-other-ai-coding-tools-brief)
10. [Likely Interview Questions & Answers](#10-likely-interview-questions--answers)
11. [Things I Learned / Limitations](#11-things-i-learned--limitations)

---

## 1. Project Overview

Two separate assessments, both built end-to-end using Claude Code + tri_ai_kit.

| | Frontend | Backend |
|---|---|---|
| **Project** | SkyBook (flight booking app) | Flight Booking BFF (GraphQL API wrapper) |
| **Stack** | Next.js 16, React 19, TypeScript, Tailwind v4, Zustand | FastAPI, Strawberry GraphQL, httpx, Pydantic v2 |
| **Deployed** | Vercel | Railway |
| **Tests** | Manual E2E browser + Duffel test API | 176 pytest-asyncio automated tests |
| **Live** | `https://flight-explorer-twenty-six.vercel.app/en` | `https://backend-assessment-production.up.railway.app/graphql` |

---

## 2. Claude Code — Deep Dive

### What Claude Code Is

Claude Code is an **agentic CLI tool** by Anthropic — not a chatbot, not an IDE plugin. It runs in the terminal, reads the actual filesystem, executes real shell commands, and takes real actions on the codebase. The model can:
- Read and write files directly
- Run Bash commands and see the output
- Search the codebase with Glob/Grep
- Spawn subagents for parallel work
- Fetch web content

The critical difference from chatbots: it doesn't just generate text — it *acts*. When it runs `npm install`, it actually installs packages. When it writes a file, that file exists on disk.

### How It Works — The Agentic Loop

```
User prompt
    ↓
Claude reasons about what to do
    ↓
Calls tools (Bash, Read, Write, Edit, Grep, Glob, Agent, ...)
    ↓
Tool executes → result returned to Claude
    ↓
Claude reasons about the result
    ↓
Calls more tools if needed
    ↓
Responds to user when done
```

This loop continues until Claude decides the task is complete. Each tool call is shown to the user, who can approve or deny it (depending on permission mode).

### Core Tools

| Tool | Purpose | When Used |
|---|---|---|
| **Bash** | Execute shell commands | Run tests, install deps, git operations, build commands |
| **Read** | Read file contents | Understanding existing code before editing |
| **Write** | Create/overwrite files | Creating new files from scratch |
| **Edit** | Precise string replacement in files | Modifying existing code — sends only the diff |
| **Glob** | Find files by pattern | Discovering files: `src/**/*.tsx`, `**/*.test.ts` |
| **Grep** | Search file contents by regex | Finding usages, class definitions, imports |
| **Agent** | Spawn a subagent | Delegate complex tasks; run work in parallel |
| **WebFetch** | Fetch a URL | Reading docs, API specs, GitHub issues |
| **WebSearch** | Search the web | Finding library docs, best practices, error solutions |
| **TodoWrite/Read** | Task tracking | Breaking work into steps, tracking progress |

**Key rule**: Always use the dedicated tool over Bash when possible — `Read` not `cat`, `Grep` not `grep`, `Edit` not `sed`. Dedicated tools give better UX and clearer permission review.

### Context Management

Claude Code has a finite context window. Key mechanisms:
- **Auto-compaction**: When context approaches limit, prior messages are compressed into a summary. A `SessionStart` hook fires again on compact to re-inject context.
- **CLAUDE.md files**: Instructions read at session start — project-level (`./CLAUDE.md`) and global (`~/.claude/CLAUDE.md`). These inject context without consuming the conversation window.
- **Memory system** (`~/.claude/projects/.../memory/`): Persistent file-based memory survives across sessions — stores user preferences, project context, feedback.
- **Hooks inject context** at key moments (session start, each prompt, subagent start) without the user needing to re-state it.

### Permission Model

Three modes:
- **Default**: Prompts user for approval on potentially risky operations
- **Auto-approve**: Allows specific tool patterns (e.g., `Bash(npm run test:*)`)
- **Deny**: Blocks specific tools/patterns entirely

Configured in `.claude/settings.json`:
```json
{
  "permissions": {
    "allow": ["Bash(npm run test:*)", "Bash(node .claude/hooks/*)"],
    "deny": ["Bash(rm -rf *)"]
  }
}
```

Exit codes from hooks determine block/allow: `0` = allow, `2` = block.

### CLAUDE.md — The Project Instruction File

Every project has a `CLAUDE.md` at root. Claude reads this at session start. It contains:
- What the project is and its architecture
- Routing rules (which agent for which intent)
- Decision authority (what to auto-execute vs ask first)
- Code conventions and standards
- Related documents

This is how context is preserved across sessions without re-explaining the project every time.

### MCP (Model Context Protocol)

An open protocol by Anthropic for connecting Claude to external systems:
- **Tools**: Functions Claude can call (e.g., a GitHub MCP exposes `create_pr`, `list_issues`)
- **Resources**: Data sources Claude can read (e.g., a database MCP exposes schema)
- **Prompts**: Reusable prompt templates

Claude Code's own tool system (`Bash`, `Read`, etc.) is implemented as MCP tools internally. Adding an MCP server to `.claude/settings.json` gives Claude access to new tools.

In our project: Asana, Atlassian, Slack, Gmail, and Google Calendar MCP servers are configured — Claude can read Slack channels, create Jira issues, or send messages directly from the conversation.

### Subagent System (Agent Tool)

The `Agent` tool spawns a completely fresh Claude instance as a subagent:
```
Main context (orchestrator)
    ↓ Agent tool call
Subagent (fresh context, specific task)
    ↓ Returns single result message
Back to main context
```

Key behaviors:
- **Fresh context**: Subagent starts clean — no history from main conversation. Gets only what you pass in the prompt.
- **Foreground vs background**: `run_in_background: true` lets main context continue while subagent works independently.
- **Parallel dispatch**: Multiple `Agent` tool calls in a single message run simultaneously.
- **Worktree isolation**: `isolation: "worktree"` gives the subagent a temporary git worktree — its file changes don't touch the main branch.
- **Subagents cannot spawn subagents** — only the main context orchestrates. This prevents infinite agent recursion and runaway cost.

**Why subagents**: Each agent gets a clean, focused context. A `code-reviewer` subagent analyzing a component doesn't carry the context of the entire build conversation — it sees only what it needs to review. This produces sharper, more accurate output.

---

## 3. Hooks System — What Each Hook Does

Hooks are Node.js `.cjs` scripts that run automatically at specific lifecycle events. They are registered in `.claude/settings.json` and executed by the Claude Code harness — not by Claude itself.

### Hook Events

| Event | When It Fires | Use Case |
|---|---|---|
| `SessionStart` | Once at session start, resume, clear, or compact | Load project config, inject environment context |
| `SubagentStart` | When `Agent` tool spawns a subagent | Inject context into subagent before it runs |
| `SubagentStop` | When a subagent finishes | Remind subagent to summarize, check outputs |
| `UserPromptSubmit` | Every time user sends a message | Inject reminders, active plan context, rules |
| `PreToolUse` | Before any tool call | Block dangerous operations, enforce safety |
| `PostToolUse` | After a tool call completes | Trigger follow-up actions, remind about indexes |
| `Stop` | When Claude finishes responding | Collect metrics, capture learnings, notify |

### Our Hooks — Detail

#### `session-init.cjs` — SessionStart
**What it does**: The project initializer. Fires once at session start (and again after context compaction).

- Loads `.claude/.env` secrets into the process environment (gitignored, never committed)
- Detects project type (npm/Python/Go), package manager, and framework from file structure
- Resolves the active plan from session state or git branch name
- Writes ~20 environment variables to `CLAUDE_ENV_FILE` — things like `tri-ai-kit_GIT_BRANCH`, `tri-ai-kit_PROJECT_TYPE`, `tri-ai-kit_PLANS_PATH` — so other hooks don't need to recompute them
- Checks if biweekly improvement summary is due (every 14 days) and prompts to run `/review-improvements`
- Outputs a compact session context line like `"Session startup. Project: single-repo | PM: npm | Plan naming: {date}-{slug}"`
- After context compaction: warns Claude that any pending `AskUserQuestion` approval gates must be re-confirmed with the user before proceeding

**Why it matters**: Subagents and hook scripts read the env vars written here rather than recomputing git/project info independently. One detection pass per session.

---

#### `context-reminder.cjs` — UserPromptSubmit
**What it does**: Injects context at every user message to prevent drift.

- Reads session state (active plan, paths, project type) from env vars written by `session-init`
- Builds a compact reminder block: current plan, reports path, key rules (YAGNI/KISS/DRY), plan naming format
- Throttled: if context was injected recently (within N turns), skips to avoid noise
- Uses `wasRecentlyInjected()` check against the transcript path to determine if injection is needed

**Why it matters**: In a long session, Claude can forget the active plan path or where to write reports. This hook ensures the context is refreshed each turn without user repetition.

---

#### `scout-block.cjs` — PreToolUse (Bash, Glob, Grep, Read, Edit, Write)
**What it does**: Blocks overly broad file access to protect context window from overflow.

- Reads `.claude/.tri-ignore` — a gitignore-style file listing blocked patterns (e.g., `node_modules`, `dist`, `.next`)
- Blocks Glob patterns like `**/*` that would return ALL files
- Blocks file paths matching ignored directories
- **Allows** build commands like `npm build`, `go build`, `cargo build`, `docker build` even if they touch `node_modules` internally
- Exit code `2` = block; error message tells Claude to use more specific patterns
- Configurable: can be disabled via `.tri-ai-kit.json`: `{ "hooks": { "scout": { "enabled": false } } }`

**Example blocked**: `ls node_modules/` → "BLOCKED: Access to `*/node_modules/*` denied"  
**Example allowed**: `npm run build` → passes through, even though it writes to `dist/`

**Why it matters**: Without this, Claude might scan thousands of files trying to understand a project, consuming the entire context window on irrelevant paths.

---

#### `privacy-block.cjs` — PreToolUse (Bash, Glob, Grep, Read, Edit, Write)
**What it does**: Blocks reads of sensitive files unless the user explicitly approves.

- Detects files like `.env`, `.env.local`, `secrets.json`, credential files, private keys
- Blocks the `Read` tool on these files with exit code `2`
- Returns a structured `PRIVACY_PROMPT` JSON block that Claude parses to ask the user via `AskUserQuestion`
- **Approval flow**: 
  1. Claude tries `Read ".env"` → BLOCKED
  2. Claude asks user via `AskUserQuestion`: "I need to read `.env` which may contain secrets. Approve?"
  3. If user says yes → Claude retries with `Read "APPROVED:.env"` → ALLOWED (hook detects the `APPROVED:` prefix)
- Bash access is warned but not blocked (allows `cat .env` if user approves)

**Why it matters**: Prevents Claude from silently reading API keys, passwords, and tokens without user knowledge. The approval flow makes access explicit and auditable.

---

#### `build-gate-hook.cjs` — PreToolUse (Bash)
**What it does**: Intercepts `git commit` commands and runs the project build before allowing the commit.

- Only activates on `git commit` Bash commands (fast-exits for everything else)
- Runs `lib/build-gate.cjs` which detects the project's build command (`npm run build`, `cargo build`, etc.) and executes it
- If build **passes**: commit is allowed
- If build **fails**: commit is blocked with exit code `2`; Claude sees the build error output and must fix it before retrying
- **Bypass options**: 
  - `git commit --skip-build` in the command
  - `TRI_SKIP_BUILD=1` environment variable
  - `{ "hooks": { "build-gate": { "enabled": false } } }` in `.tri-ai-kit.json`
  - `TRI_BUILD_GATE_RAN=1` (set after skill already ran the gate to prevent double-running)

**Why it matters**: Prevents broken builds from being committed. Claude cannot commit code that doesn't compile.

---

#### `subagent-init.cjs` — SubagentStart
**What it does**: Injects essential context into every subagent before it runs.

- Fires when the `Agent` tool spawns a subagent
- Injects a compact (~200 token) context block including:
  - Subagent type and ID
  - Active plan path (or suggested plan)
  - Reports path (where to write output)
  - Plans and docs paths
  - Language settings (thinking language vs response language if configured)
  - Naming pattern for output files (e.g., `260407-1430-{slug}`)
  - Core rules: YAGNI/KISS/DRY, concise output
  - Trust passphrase (if configured) to verify subagent authenticity
  - Agent-specific instructions from `.tri-ai-kit.json` config
- Returns output in `hookSpecificOutput.additionalContext` format (required by SubagentStart API)

**Why it matters**: Subagents start with zero context. Without this hook, every subagent would need to be given full project context in its prompt (expensive). This hook makes subagents "project-aware" cheaply.

---

#### `session-metrics.cjs` — Stop
**What it does**: Records session metrics to an append-only JSONL file when Claude finishes.

- Reads the `current-session.json` written by `session-init` to calculate session duration
- Collects git diff stats: files changed, insertions, deletions
- Parses the session transcript to count tool calls by type and count how many subagents were spawned
- Appends a JSON entry to `.kit-data/improvements/sessions.jsonl`
- Auto-rotates the file at 1000 lines (keeps last 500)
- Cleans up `current-session.json` after writing

**Metrics captured**: session duration, git stats, task counts, error counts, rework iterations, skills used, tool call breakdown, subagent count.

**Why it matters**: Powers the `/review-improvements` command which analyzes session patterns biweekly to identify recurring issues and productivity patterns.

---

#### `lesson-capture.cjs` — Stop
**What it does**: Evaluates whether the session contained significant learnings worth capturing.

- Reads the last 6 session entries from `sessions.jsonl`
- Evaluates significance thresholds:
  - **FINDING**: Any errors were encountered → worth capturing
  - **PATTERN**: 2+ fix iterations detected → rework pattern worth documenting
  - **CONV**: 1+ verification failures → convention or process gap
  - **NOTE**: A skill was used for the first time → worth noting
- If triggers are found: outputs a JSON prompt asking Claude to use the knowledge-capture skill
- If nothing significant: exits silently

**Why it matters**: Automatically surfaces learnings that should be documented (bugs fixed, patterns identified, new tools learned) without requiring the developer to remember to write them down.

---

#### `post-index-reminder.cjs` — PostToolUse (Edit, Write, MultiEdit)
**What it does**: After writing a file in `docs/`, `reports/`, or `plans/`, reminds Claude to update the corresponding index file.

- Fires after every `Edit`, `Write`, or `MultiEdit` tool call
- Checks if the written file path is inside `docs/`, `reports/`, or `plans/`
- If yes: outputs a reminder message with the exact schema for updating the index JSON
- Throttled: once per directory type per 2 minutes, to avoid noise when writing multiple files quickly
- Returns `{ "additionalContext": "..." }` which Claude sees as a follow-up instruction

**Why it matters**: Without this, new documents get created but the index files (`docs/index.json`, `reports/index.json`, `plans/index.json`) fall out of sync. The hook acts as an automated process reminder.

---

### Hook Configuration in settings.json

```json
{
  "hooks": {
    "SessionStart":    [{ "hooks": [{ "type": "command", "command": "node .claude/hooks/session-init.cjs" }] }],
    "SubagentStart":   [{ "matcher": "*", "hooks": [{ "type": "command", "command": "node .claude/hooks/subagent-init.cjs" }] }],
    "SubagentStop":    [{ "matcher": "*", "hooks": [{ "type": "command", "command": "node .claude/hooks/subagent-stop-reminder.cjs" }] }],
    "UserPromptSubmit":[{ "hooks": [{ "type": "command", "command": "node .claude/hooks/context-reminder.cjs" }] }],
    "PostToolUse":     [{ "matcher": "Edit|Write|MultiEdit", "hooks": [{ "type": "command", "command": "node .claude/hooks/post-index-reminder.cjs" }] }],
    "PreToolUse": [
      { "matcher": "Bash|Glob|Grep|Read|Edit|Write", "hooks": [
          { "type": "command", "command": "node .claude/hooks/scout-block.cjs" },
          { "type": "command", "command": "node .claude/hooks/privacy-block.cjs" }
      ]},
      { "matcher": "Bash", "hooks": [{ "type": "command", "command": "node .claude/hooks/build-gate-hook.cjs" }] }
    ],
    "Stop": [
      { "hooks": [
          { "type": "command", "command": "node .claude/hooks/session-metrics.cjs" },
          { "type": "command", "command": "node .claude/hooks/lesson-capture.cjs" },
          { "type": "command", "command": "node .claude/hooks/notifications/notify.cjs" }
      ]}
    ]
  }
}
```

**Key points**:
- `matcher` field: filters which tool names trigger the hook (regex pattern)
- `type: "command"`: runs a shell command; stdout becomes context injected into Claude
- Exit code `0` = allow; `2` = block (PreToolUse only)
- All hooks are **fail-open**: crashes exit with `0` so they never block Claude on error

---

## 4. Multi-Agent System (tri_ai_kit)

### What It Is

A custom orchestration layer built on top of Claude Code. The main conversation always acts as orchestrator — it classifies intent, selects the right agent, spawns it via the `Agent` tool, and merges the result.

```
Main conversation (orchestrator)
    ↓ classify intent: build/fix/plan/review/test/research
    ↓ select agent: frontend-developer, backend-architect, tester, ...
    ↓ Agent tool → spawns subagent with:
        - CLAUDE.md context
        - Active plan reference
        - Skill modules loaded (fastapi-python, react-expert, graphql-architect, ...)
        - subagent-init.cjs context injection
    ↓ Subagent works in clean context → returns result
    ↓ Main context merges result
```

### Why It Works

**Context isolation**: A `frontend-developer` building a PassengerCard component gets a clean context with only what it needs. It doesn't carry the 3,000-token history of legacy API exploration from Phase 2. Smaller context = sharper output.

**Architecture gates**: Before any implementation agent runs, an architect agent (e.g., `frontend-architect`) reviews and approves the component structure. This caught real issues:
- TanStack Query cut before any code was written (Server Actions made it redundant)
- Circuit breaker wrapping order decided before `legacy.py` existed

**Fresh reviewer**: The `code-reviewer` subagent doesn't share the implementer's blind spots — it audits with genuinely clean eyes.

**Skill loading**: Agents declare which skills they need (`fastapi-python`, `graphql-architect`, `react-expert`). The skill files are loaded into the agent's context — domain-specific knowledge without bloating every agent's base prompt.

### 22 Agents — Key Ones

| Agent | Role |
|---|---|
| `planner` | Decompose requirements into phased plan files |
| `researcher` | Live API exploration, library research, competitor analysis |
| `frontend-architect` | Lock component hierarchy, rendering strategy, state decisions |
| `backend-architect` | Design API contracts, ADRs, Mermaid diagrams, resilience patterns |
| `frontend-developer` | React components, Server Actions, Route Handlers, i18n |
| `backend-developer` | FastAPI services, GraphQL resolvers, transformation logic |
| `code-reviewer` | TypeScript audit, accessibility, quality gates |
| `debugger` | Root cause analysis, fix verification |
| `tester` | Test suite generation, fixture quality, coverage |
| `docs-manager` | Architecture docs, README, workflow docs |
| `git-manager` | Commit, push, PR creation with security scanning |

---

## 5. Frontend — SkyBook

### Architecture

```
Browser (Client Components + Zustand sessionStorage)
    ↓ Server Actions (mutations)
    ↓ Route Handlers (GET proxies)
Next.js Server — holds DUFFEL_API_KEY
    ↓ Bearer token
Duffel API (api.duffel.com)
```

4 screens: Search → Results → Passengers → Confirmation

### Key Non-Obvious Patterns

**`useHydrated()` pattern** — Zustand rehydrates from `sessionStorage` only after client mount. Server always sees `offerRequestId = null`. Naive route guard would always redirect during SSR.
```typescript
export function useHydrated() {
  const [hydrated, setHydrated] = useState(false);
  useEffect(() => setHydrated(true), []);
  return hydrated;
}
// Guard: if (!hydrated) return <Skeleton /> — defer redirect until hydrated
```

**`ActionResult<T>` discriminated union** — forces callers to handle both branches:
```typescript
type ActionResult<T> = { success: true; data: T } | { success: false; error: string; code?: string };
```

**`return_offers=false`** — round-trip offer request returns 1,638 offers inline by default. This parameter returns only the `orq_` ID for paginated fetching.

**`passengerIds` verbatim constraint** — `passenger.id` values from `POST /air/offer_requests` must be reused exactly in `POST /air/orders`. They cannot be fabricated.

### Stack Decisions

| Decision | Choice | Reason |
|---|---|---|
| State | Zustand + sessionStorage | Tab isolation; passenger data dies on tab close (security) |
| Forms | RHF + Zod | Uncontrolled = no keystroke re-renders; Zod = type + runtime validation |
| Data fetching | Server Actions + Route Handlers | TanStack Query made redundant by Server Actions |
| API client | Raw `duffelFetch<T>` | 1KB wrapper vs 50KB SDK |
| UI | shadcn/ui (Radix) | Radix gives ARIA/keyboard nav for free |

---

## 6. Backend — Flight Booking BFF

### What It Does

BFF (Backend-for-Frontend) API wrapper: sits between clients and a legacy GDS with deeply nested responses, 4 error formats, 5 date formats, and codes with no labels.

**Single endpoint**: `POST /graphql`  
**6 operations**: searchFlights, offerDetails, createBooking, retrieveBooking, airports, airport

### Architecture Layers

```
Client → GraphQL (Strawberry) → Services → Cache (TTLCache) → HTTP Client (httpx + retry + CB) → Legacy API
```

Each layer has one job — services never make HTTP calls; HTTP client never transforms data.

### Key Patterns

**Resilience pipeline**:
```
Input validation → Circuit breaker → Retry (3×, exp+jitter, 503/429 only) → Upstream
```

**Spec-first development**: Full GraphQL SDL written as a document before any service code. Services written to satisfy the contract — not the other way around. When AI drifted, the spec was the ground truth.

**Price as String**: `round(97.075, 2)` → `97.07` (IEEE 754 bug). `f"{float(val):.2f}"` → `97.08`. Always format via string path.

**DI via `info.context`**: `LegacyClient` injected through `info.context["legacy_client"]` in every resolver — not a global. Makes every resolver independently testable with `AsyncMock`.

---

## 7. AI Workflow — How I Used Claude Code

### The Development Loop (Both Projects)

```
1. Research   → researcher agent explores the problem space live (real API calls, not docs guessing)
2. Plan       → planner agent decomposes into phase files (committed before implementation starts)
3. Architecture gate → architect agent reviews and locks structure (catches issues before code)
4. Implement  → developer agent builds to the spec (constrained = accurate)
5. Review     → code-reviewer agent audits with fresh context (no implementer blind spots)
6. Test/Fix   → tester agent generates tests; debugger agent root-causes failures
7. Deploy     → git-manager + devops-engineer
```

### What Claude Code Accelerated

**1. Boilerplate elimination (~60%)**
- Zod validation schemas from passenger type rules
- TypeScript types generated from live API responses (curl output → types)
- i18n message files across 3 locales with consistent key structure
- Route Handler boilerplate with consistent error handling
- Full GraphQL SDL from architecture spec

**2. Edge-case handling (~25%)**
- `useHydrated()` pattern designed for SSR/Zustand mismatch
- Date format normalizer handling 5 upstream formats in one function
- `OfferExpiryGuard` countdown and redirect pattern
- Error recovery flows (`price_changed`, `offer_expired`)

**3. Test generation (~15%)**
- 176 test cases with realistic fixtures from actual `curl` responses
- Fixture fidelity (real field names, real nesting depth) caught bugs simplified mocks missed

### Where Human Judgment Was Needed (Real Examples)

| Issue | What AI Did | What I Did |
|---|---|---|
| Circuit breaker wrapping | Put retry outside CB → each 3-retry attempt counted as 1 tick | Restructured: CB wraps retry, each attempt = separate tick |
| Duplicate Strawberry types | `BaggageItem` in both `flight.py` and `booking.py` → chose `@strawberry.type(name="BookingBaggageItem")` workaround | Defined once in `flight.py`, imported into `booking.py` |
| Price precision | Used `round(97.075, 2)` → `97.07` | Changed to `f"{float(val):.2f}"` → `97.08` |
| `PassengerInput` field naming | Used `given_name`/`family_name` to match output type | Spec requires `first_name`/`last_name` for HTML form conventions |
| Static airport names | Left `Airport.name` as `None` for all airports | Added `AIRPORT_NAMES` enrichment table in `enrichment.py` |
| TanStack Query | In initial research list | Cut it at architecture gate — Server Actions made it redundant |

**Pattern**: AI is reliable for boilerplate. For multi-file architectural decisions, subtle precision issues, or "correct solution vs convenient workaround" — human review every time.

---

## 8. Key Technical Decisions

### Frontend

| Decision | Choice | Reasoning |
|---|---|---|
| Why Server Actions for mutations? | Security — DUFFEL_API_KEY never reaches browser; type-safe RPC |
| Why sessionStorage not localStorage? | Tab isolation for price-checking; passenger data doesn't persist to disk |
| Why not TanStack Query? | Server Actions eliminated the need; useMemo+Zustand replaced client cache |
| Why not Duffel SDK? | 1KB wrapper vs 50KB SDK; explicit, debuggable error handling |

### Backend

| Decision | Choice | Reasoning |
|---|---|---|
| GraphQL not REST? | Deeply nested upstream, self-documenting, spec-first SDL approach |
| Monetary as String? | IEEE 754 can't represent `97.08` exactly; `round()` produces wrong result |
| Why spec-first? | Contract written before services; AI constrained to known spec, can't drift |
| CB wraps retry, not vice versa? | Each retry = separate CB failure tick; wrong order under-counts failures |
| In-process TTLCache not Redis? | Zero infra for single instance; same key scheme migrates to Redis when needed |

---

## 9. Other AI Coding Tools (Brief)

| Tool | What It Is | When to Choose |
|---|---|---|
| **GitHub Copilot** | IDE inline autocomplete plugin | Fast single-line suggestions, enterprise environments |
| **Cursor** | AI-first IDE fork | Daily IDE coding, multi-file refactors with IDE UX |
| **Claude Code** | Agentic terminal CLI | Complex orchestration, architecture decisions, full workflows |

**The key difference**: Copilot and Cursor complete code inside an IDE. Claude Code takes actions on the codebase — reads, writes, runs commands, spawns agents — like a developer working in the terminal. For building the assessment projects, Claude Code was used for the entire workflow: research → planning → implementation → review → deployment.

---

## 10. Likely Interview Questions & Answers

### "What is Claude Code and how does it work?"

> Claude Code is an agentic CLI tool by Anthropic that runs in the terminal. Unlike IDE plugins that suggest completions, Claude Code actually acts — it reads files, runs commands, writes code, and can spawn subagents for parallel work. The core loop is: Claude reasons → calls a tool (Bash/Read/Write/Agent) → sees the result → reasons again → responds when done. I used it with a custom multi-agent orchestration layer called tri_ai_kit that routes tasks to specialized agents — each one gets a clean focused context rather than sharing one bloated conversation.

### "What are hooks in Claude Code and how did you use them?"

> Hooks are shell scripts that run automatically at lifecycle events — session start, before/after tool calls, when a subagent starts, when Claude finishes responding. They run outside Claude's context, so Claude doesn't need to think about them — the harness just executes them.
>
> In our project we have 9 hooks. The most important ones: `session-init` fires at startup and writes ~20 environment variables about the project (git branch, plan path, framework) so other hooks don't need to recompute them. `scout-block` intercepts every file access and blocks patterns from `.tri-ignore` like `node_modules` — prevents context window overflow. `privacy-block` blocks reads of `.env` files unless the user explicitly approves — security guard. `build-gate-hook` intercepts `git commit` and runs the build first — you cannot commit broken code. `session-metrics` and `lesson-capture` fire on Stop — they record what happened and surface significant learnings worth documenting.

### "Why use multi-agent instead of one long conversation?"

> Context drift. In a single long conversation, architectural decisions from the first hour get forgotten, the context fills with irrelevant history, and responses become generic. With multi-agent: each subagent starts clean with only what it needs. The `code-reviewer` subagent has no idea what the `frontend-developer` was thinking — it audits with genuinely fresh eyes. The `researcher` exploring the Duffel API doesn't contaminate the context of the `frontend-developer` writing components. Smaller focused contexts produce sharper output.

### "How do you validate AI-generated code?"

> My validation checklist: (1) Read every generated line — not skim, read. (2) Trace the critical path manually. (3) Run tests with fixtures from real API responses, not simplified mocks — fidelity to real data catches bugs that round numbers miss. (4) For the backend, every function was tested against an actual `curl` response before being accepted. (5) Schema changes verified via GraphQL introspection in GraphiQL after every type change.
>
> Real failure: AI used `round(97.075, 2)` which returns `97.07` due to IEEE 754. I caught it when `assert price.amount == "97.08"` failed. That's exactly the kind of numerically subtle error that looks correct at a glance.

### "Describe a specific AI failure and how you fixed it."

> Circuit breaker wrapping order. The first draft placed `@retry(...)` as a decorator around a function that called the circuit breaker internally. This meant Tenacity retried 3 times, but the circuit breaker only saw 1 failure per request — because the entire 3-attempt sequence happened inside one CB call. The correct design is: circuit breaker wraps the retry function, so each individual attempt registers as a separate tick on the failure counter. If 5 attempts fail (across 1-2 requests), the breaker opens. Wrong: you'd need 15 failures. I restructured the call hierarchy manually after understanding what the code was actually doing.

### "How do you decide when not to trust AI output?"

> I trust AI for boilerplate with well-defined patterns — Zod schemas, TypeScript types from API responses, test fixtures. I distrust AI for: multi-file architectural decisions (it optimizes for the local context, not the full import topology), anything requiring IEEE 754 awareness, and "convenient workaround vs correct solution" choices. The `DuplicatedTypeName` case: AI's fix was adding a custom name to one type — a workaround that would accumulate technical debt. The correct fix was defining the type once and importing it. AI couldn't see that distinction because it required knowing the long-term intent.

### "What is spec-first development and why did you use it?"

> Write the full API contract before writing any implementation. For the backend: I wrote the complete GraphQL SDL — every type, field, query, mutation, input — as a document in `plans/5-wrapper-api-spec.md` before any service code existed. Services were then written to satisfy this contract.
>
> The benefit: AI code generation is constrained to a known specification. When it drifts (wrong field names, wrong nullability), the spec provides immediate ground truth to compare against. Without a spec, AI generates code that works for the local context but might not match what the frontend actually needs. With a spec, the code is always checked against the contract.

### "How do you manage context in long Claude Code sessions?"

> Several mechanisms: (1) CLAUDE.md at project root — project architecture, routing rules, code conventions — read at every session start without consuming conversation history. (2) Hooks inject minimal context at key moments — the `context-reminder` hook re-injects the active plan path every prompt turn, so Claude doesn't forget where to write output. (3) Subagents: for tasks that would pollute the main context with irrelevant detail (e.g., deep API exploration), spawn a subagent — its work happens in isolation and only the result comes back. (4) Plan files committed before implementation — they're always readable from disk, no need to keep them in context.

---

## 11. Things I Learned / Limitations

### What Claude Code Is Reliably Good At
- Boilerplate with consistent patterns (route handlers, validation schemas, test fixtures)
- Generating code from a spec (constrained = accurate)
- TypeScript types from live API responses
- Producing structured plan documents quickly
- Keeping architectural decisions consistent when given a spec

### What Requires Human Review Every Time
- Multi-file architectural decisions (import topology, type ownership)
- Subtle numeric precision (float formatting, rounding edge cases)
- "Correct solution vs convenient workaround" (DuplicatedTypeName)
- Any decision where "why" matters as much as "what"
- Cases requiring full project context that spans subagent boundaries

### The Planning-First Multiplier
Plans committed before implementation are the key multiplier. Because the SDL contract and transformation tables existed before service code, AI's generation was constrained to a known spec. When it produced wrong output, there was immediate ground truth. Plans also produce a verifiable audit trail — git history shows planning happened before coding, not after.

### The Architecture Gate Pattern
Requiring architect approval before implementation agents begin is cheap insurance. For the frontend: TanStack Query cut before any code was written. For the backend: CB wrapping order decided before `legacy.py`. Gates are cheap; retrofits are expensive.

### Hooks as Automation Layer
The hooks system moved project conventions out of Claude's context and into enforced automation. You don't prompt Claude "remember to update the index file" — `post-index-reminder` does it automatically. You don't worry about committing broken code — `build-gate-hook` prevents it. This reduces the cognitive load of the session and keeps the conversation focused on the actual problem.

---

*Prepared for easyGDS interview — Monday, April 7, 2026*
