---
title: 'NestJS Developer Roadmap: Beginner to Advanced'
status: active
created: 2026-04-09
updated: 2026-04-09
effort: 120h
phases: 8
platforms: [backend]
breaking: false
---

# NestJS Developer Roadmap: Beginner to Advanced

A hands-on 12-week program where you build **one app from scratch** and add one real feature per phase.
Every phase ends with a working app you can run and test — no isolated exercises, no throw-away code.

**The app**: A Task Management REST API, starting as a `Hello World` and ending as a production-ready
service with auth, a real DB, tests, caching, queues, WebSockets, Swagger docs, and Docker.

## Platform Notes (Windows)

All phase files include both **Bash** (macOS/Linux/Git Bash) and **Windows PowerShell** versions
of every test command. Key differences to remember:

| Operation | Bash | PowerShell |
|-----------|------|------------|
| HTTP GET | `curl http://...` | `Invoke-RestMethod "http://..."` |
| HTTP POST with JSON | `curl -X POST ... -d '{"key":"val"}'` | `Invoke-RestMethod -Method Post ... -Body '{"key":"val"}'` |
| Set a variable | `TOKEN="eyJ..."` | `$TOKEN = "eyJ..."` |
| Read response field | *(manual)* | `$res = Invoke-RestMethod ...; $res.accessToken` |
| Handle 4xx errors | *(returns error body)* | `try { ... } catch { $_.ErrorDetails.Message \| ConvertFrom-Json }` |
| Rename a file | `mv .env .env.bak` | `Rename-Item .env .env.bak` |
| Line continuation | `\` | `` ` `` (backtick) |

`docker`, `npm`, `npx`, and `nest` commands are identical on all platforms.

---

## Context from Prior Research

- **Pipeline order**: Middleware -> Guards -> Interceptors -> Pipes -> Controller -> Service -> Interceptors -> Exception Filters
- **ORM preference**: Prisma over TypeORM for new projects
- **Testing ratio**: 70% unit / 20% integration / 10% e2e
- **Ecosystem targets**: Swagger, Bull/BullMQ, WebSockets, GraphQL, Passport+JWT
- **Recommended timeline**: 10-12 weeks (this plan: 12 weeks)

## Phases

| # | Phase | Weeks | Effort | Status | File |
|---|-------|-------|--------|--------|------|
| # | Phase | What You Add to the App | Weeks | Effort | Status | File |
|---|-------|------------------------|-------|--------|--------|------|
| 1 | TS + Node Foundations | Scaffold project, `GET /` returns Hello World | 1 | 8h | pending | [phase-1](./phase-1-ts-foundations.md) |
| 2 | NestJS Core | Full CRUD `GET/POST/PATCH/DELETE /tasks` (in-memory) | 2-3 | 14h | pending | [phase-2](./phase-2-nestjs-core.md) |
| 3 | Request Lifecycle | DTOs, ValidationPipe, Exception Filter, Logging Interceptor | 4 | 10h | pending | [phase-3](./phase-3-request-lifecycle.md) |
| 4 | Authentication | `POST /auth/register`, `POST /auth/login`, JWT guards, RBAC | 5-6 | 14h | pending | [phase-4](./phase-4-auth.md) |
| 5 | Database | Swap in-memory for Prisma + PostgreSQL, migrations, seed | 7 | 12h | pending | [phase-5](./phase-5-database.md) |
| 6 | Testing | Unit + integration + E2E test suite for everything built so far | 8-9 | 14h | pending | [phase-6](./phase-6-testing.md) |
| 7 | Advanced Patterns | Config validation, `@CurrentUser()`, Redis caching, `/health` | 10-11 | 10h | pending | [phase-7](./phase-7-advanced.md) |
| 8 | Ecosystem | Swagger docs, BullMQ queue, WebSocket events, Docker | 12 | 12h | pending | [phase-8](./phase-8-ecosystem.md) |

**Total estimated effort**: 94h over 12 weeks (~8h/week)

## Milestone Checkpoints

| Milestone | After Phase | Validation |
|-----------|-------------|------------|
| M1 - Framework literacy | 2 | Can scaffold a module, explain DI, wire a basic CRUD API |
| M2 - Secure API | 4 | JWT auth working, guards protecting routes |
| M3 - Persistent API | 5 | Prisma schema, migrations, repository pattern |
| M4 - Tested codebase | 6 | 70/20/10 test ratio achieved on capstone project |
| M5 - Production ready | 8 | Rate limiting, queues, Swagger docs, health checks |

## The App — What It Looks Like at Each Phase

```
Phase 1: GET /           → "Hello World"
Phase 2: GET /tasks      → [{ id, title, status }] (in-memory)
Phase 3: POST /tasks {}  → 400 with structured error (validation active)
Phase 4: GET /tasks      → 401 without JWT, 200 with valid JWT
Phase 5: GET /tasks      → data survives server restart (PostgreSQL)
Phase 6: npm test:ci     → full test suite green
Phase 7: GET /tasks x2   → second call hits Redis cache
Phase 8: docker-compose up → entire stack runs with one command
```

Each phase = one `git commit`. You always have a working app.

## Key Resources

| Resource | URL | Type |
|----------|-----|------|
| NestJS Official Docs | https://docs.nestjs.com | Docs |
| NestJS Fundamentals (official course) | https://courses.nestjs.com | Video |
| NestJS GitHub | https://github.com/nestjs/nest | Source |
| Prisma Docs | https://www.prisma.io/docs | Docs |
| Awesome NestJS | https://github.com/nestjs/awesome-nestjs | Community |

## Success Criteria

- [ ] Can scaffold and explain a complete NestJS module from memory
- [ ] Understands every layer of the request pipeline
- [ ] Implements JWT with refresh token auth independently
- [ ] Uses Prisma migrations confidently in development and CI
- [ ] Achieves the 70/20/10 test ratio on the capstone project
- [ ] Configures Swagger, BullMQ, and WebSockets in a single app
- [ ] Deploys a production-ready NestJS app with health checks and logging
