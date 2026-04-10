---
phase: 2
title: "Author SKILL.md + references"
effort: 2h
depends: [1]
---

# Phase 2 — Author SKILL.md + References

## Goal

Create `nestjs-pro/SKILL.md` and 8 reference files following the exact same structure as `golang-pro`. All content comes from Phase 1 research.

## Agent & Skills

- **Agent**: `backend-developer`
- **Skills**: `typescript-pro`, `docs-seeker`
- **Handoffs**:
  - After completion → Phase 3 (registration + verification)
  - On quality concern → `code-reviewer`

## Files to Create

### Primary file

**`D:/projects/private-stuffs/.claude/skills/nestjs-pro/SKILL.md`**

YAML frontmatter (exact fields):
```yaml
---
name: nestjs-pro
description: "Use when building NestJS applications. Invoke for module architecture, dependency injection, controller/service design, Guards/Interceptors/Pipes/Filters (GIPF), validation with class-validator, exception handling, or Prisma repository patterns. Triggers on: NestJS, @Module, @Controller, @Injectable, @Guard, @Interceptor, @Pipe, @Filter, ValidationPipe, nest-cli.json, *.module.ts, *.controller.ts, *.service.ts, nestjs DI, nestjs authentication, nestjs exception filter."
license: MIT
metadata:
  author: tri-ai-kit
  version: "1.0.0"
  domain: backend
  triggers: NestJS, @Module, @Controller, @Injectable, nest-cli.json, "*.module.ts", "*.controller.ts", "*.service.ts", Guards NestJS, Interceptors NestJS, Pipes NestJS, Filters NestJS, ValidationPipe, class-validator, Prisma NestJS, NestJS DI, NestJS authentication, NestJS exception filter
  role: specialist
  scope: implementation
  output-format: code
  related-skills: typescript-pro, postgres-pro, api-designer
---
```

SKILL.md body must include:
1. One-line role description
2. Core Workflow (numbered steps: design modules → DTOs → service → controller → GIPF → bootstrap)
3. Reference Guide table (8 rows, each pointing to a `references/*.md` file with load-when trigger)
4. Canonical module code example (inline — module + service + controller wiring, ~30 lines)
5. MUST DO constraints (6+ rules)
6. MUST NOT DO constraints (5+ rules)
7. Output Templates (feature module scaffold, GIPF addition)
8. Knowledge Reference (NestJS decorator/API keyword list for activation matching)

### Reference files

All in `D:/projects/private-stuffs/.claude/skills/nestjs-pro/references/`:

| File | Key contents |
|------|-------------|
| `modules.md` | Feature/Shared/Core module patterns, `forRoot`/`forRootAsync`, `@Global()`, `forwardRef()`, DI tokens |
| `controllers.md` | Route decorators, param decorators, why avoid `@Res()`, versioning, `ClassSerializerInterceptor` |
| `gipf.md` | `CanActivate` guard example, `NestInterceptor` example, `PipeTransform` example, `ExceptionFilter` example, binding order table |
| `validation.md` | `ValidationPipe` global config, class-validator decorators, `@Type()` coercion, mapped types, `@ApiProperty()` |
| `exceptions.md` | `HttpException` hierarchy, custom exception pattern, `HttpExceptionFilter` with unified envelope |
| `prisma.md` | `PrismaService` pattern, `PrismaModule @Global()`, repository interface, `onModuleInit`, TypeORM note |
| `testing.md` | `createTestingModule` unit test pattern, jest.fn() mock providers, E2E with supertest |
| `project-structure.md` | Full `src/` directory tree, `main.ts` bootstrap checklist, `nest-cli.json` example |

## Reference to Existing Skill for Format

Read `D:/projects/private-stuffs/.claude/skills/golang-pro/SKILL.md` for the exact formatting conventions before authoring.

## Scope Fence

The `nestjs-pro` SKILL.md constraint section must explicitly state:
> "For generic TypeScript patterns (utility types, generics, decorators), defer to `typescript-pro`. This skill covers NestJS-specific architecture and conventions only."

## Validation

- [ ] YAML frontmatter parses cleanly (no tabs, valid YAML)
- [ ] `triggers` string covers both decorator names and file patterns
- [ ] SKILL.md body is under 500 lines
- [ ] Each reference file is under 300 lines
- [ ] Canonical code example compiles (verify mentally — no placeholder types)
- [ ] MUST DO / MUST NOT DO lists are specific, not generic
- [ ] Scope fence is present in constraints section
