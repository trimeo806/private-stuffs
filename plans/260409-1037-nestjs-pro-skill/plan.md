---
title: "nestjs-pro skill for tri-ai-kit"
status: completed
created: 2026-04-09
updated: 2026-04-09
effort: 3.5h
phases: 3
platforms: [backend]
breaking: false
---

# nestjs-pro Skill

A passive specialist skill for NestJS 10+ application-layer best practices. Activates on NestJS file patterns and decorator keywords. Scoped to modules, DI, controllers, services, GIPF layer, validation, exception handling, and Prisma-first repository design.

## Success Criteria

- `nestjs-pro/SKILL.md` exists with valid YAML frontmatter and correct trigger string
- 8 reference files created under `nestjs-pro/references/`
- `skill-index.json` contains a `nestjs-pro` entry with correct `path` and `triggers`
- Skill activates when NestJS patterns are detected (smoke test passes)

## Phases

| # | Phase | Effort | Status | File |
|---|-------|--------|--------|------|
| 1 | Research NestJS best practices | 1h | done | [phase-1](./phase-1-research.md) |
| 2 | Author SKILL.md + references | 2h | done | [phase-2](./phase-2-author-skill.md) |
| 3 | Register in skill-index + verify | 0.5h | done | [phase-3](./phase-3-register-verify.md) |

## Agents & Skills

| Phase | Agent | Skills |
|-------|-------|--------|
| 1 | `researcher` | `docs-seeker`, `knowledge-retrieval` |
| 2 | `backend-developer` | `typescript-pro`, `docs-seeker` |
| 3 | `backend-developer` | — |

## Key Decisions

- **Scope fence**: application layer only — no microservices, queues, WebSockets
- **ORM**: Prisma-first; TypeORM acknowledged as a secondary note
- **Activation**: decorator names + file-name patterns (not just "nestjs" keyword)
- **Not user-invocable**: passive skill, no slash command (same as golang-pro)
- **Reference-per-topic**: 8 separate `references/*.md` files, SKILL.md stays under 500 lines
