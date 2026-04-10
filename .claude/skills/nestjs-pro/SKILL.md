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

# NestJS Pro

Senior NestJS developer specializing in modular architecture, dependency injection, GIPF layer design, validation pipelines, and Prisma repository patterns for production-grade TypeScript APIs.

## Core Workflow

1. **Analyze architecture** — Review module boundaries, DI graph, and feature ownership
2. **Design modules** — One feature module per domain; SharedModule for cross-cutting; `@Global()` for PrismaModule/ConfigModule only
3. **Implement** — Wire controllers, services, and repositories with correct DI tokens; run `tsc --noEmit` before proceeding
4. **Apply GIPF layer** — Bind guards, interceptors, pipes, and filters at the correct scope (global > controller > route)
5. **Validate** — Run `eslint` and fix all violations; confirm `ValidationPipe` options are set correctly
6. **Test** — Unit tests with `Test.createTestingModule`, E2E tests with `supertest`; 80%+ coverage

## Reference Guide

Load detailed guidance based on context:

| Topic | Reference | Load When |
|-------|-----------|-----------|
| Modules | `references/modules.md` | @Module, forRootAsync, SharedModule, circular deps |
| Controllers | `references/controllers.md` | Route decorators, param decorators, versioning, @Res footgun |
| GIPF | `references/gipf.md` | Guards, Interceptors, Pipes, Filters, execution order |
| Validation | `references/validation.md` | ValidationPipe, class-validator, DTOs, mapped types |
| Exceptions | `references/exceptions.md` | Built-in exceptions, custom exception classes, filter shape |
| Prisma | `references/prisma.md` | PrismaService, PrismaModule, repository interface pattern |
| Testing | `references/testing.md` | Unit testing, E2E testing, test doubles |
| Project Structure | `references/project-structure.md` | Directory layout, main.ts bootstrap order |

## Core Pattern Example

Canonical feature module wiring — module, service, and controller with DI:

```typescript
// users/users.module.ts
import { Module } from '@nestjs/common';
import { UsersController } from './users.controller';
import { UsersService } from './users.service';
import { USER_REPO } from './repositories/user-repo.token';
import { PrismaUserRepository } from './repositories/prisma-user.repository';

@Module({
  controllers: [UsersController],
  providers: [
    UsersService,
    { provide: USER_REPO, useClass: PrismaUserRepository },
  ],
  exports: [UsersService],
})
export class UsersModule {}

// users/users.service.ts
import { Injectable, Inject, NotFoundException } from '@nestjs/common';
import { USER_REPO } from './repositories/user-repo.token';
import { IUserRepository } from './repositories/user.repository.interface';
import { CreateUserDto } from './dto/create-user.dto';

@Injectable()
export class UsersService {
  constructor(@Inject(USER_REPO) private readonly repo: IUserRepository) {}

  async findOne(id: string) {
    const user = await this.repo.findById(id);
    if (!user) throw new NotFoundException(`User ${id} not found`);
    return user;
  }

  create(dto: CreateUserDto) {
    return this.repo.create(dto);
  }
}

// users/users.controller.ts
import { Controller, Get, Post, Body, Param } from '@nestjs/common';
import { UsersService } from './users.service';
import { CreateUserDto } from './dto/create-user.dto';

@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.usersService.findOne(id);
  }

  @Post()
  create(@Body() dto: CreateUserDto) {
    return this.usersService.create(dto);
  }
}
```

Key properties demonstrated: `@Module` with explicit `exports`, `@Injectable` service injecting a repository via token, controller using constructor DI (never manually instantiated), service owning all business logic.

## Constraints

### MUST DO
- Declare one feature module per domain; keep `exports: []` explicit
- Use `Symbol('TOKEN')` injection tokens — never raw strings
- Apply `ValidationPipe({ whitelist: true, forbidNonWhitelisted: true, transform: true })` globally
- Bind `GlobalExceptionFilter` in `main.ts` via `app.useGlobalFilters()`
- Use `@Res({ passthrough: true })` if you must access the response object
- Scope guards/interceptors/pipes at the most specific level needed (global > controller > route)
- Write repository interfaces; services inject the interface token, never PrismaClient directly
- Follow bootstrap order: `useGlobalPipes` → `useGlobalInterceptors` → `useGlobalFilters` → `enableCors` → `listen`

### MUST NOT DO
- Use `@Res()` without `passthrough: true` — disables interceptors and exception filters
- Use `forwardRef()` unless truly unavoidable — treat circular deps as a smell; extract shared service
- Use `@Global()` on feature modules — reserve it for PrismaModule and ConfigModule only
- Use string injection tokens (collision risk); always use `Symbol`
- Skip `transform: true` on ValidationPipe — `@Type()` coercion will silently fail without it
- Inject `PrismaClient` directly into services — always go through repository interface
- For generic TypeScript patterns (utility types, generics, decorators), defer to `typescript-pro`. This skill covers NestJS-specific architecture only.

## Output Templates

When implementing NestJS features, provide:
1. Module file with `controllers`, `providers`, `exports` declared
2. Service file with typed constructor injection
3. Controller file with route and param decorators
4. DTO with class-validator decorators
5. Brief explanation of DI wiring and GIPF bindings used

## Knowledge Reference

NestJS 10+, @Module, @Injectable, @Controller, @Guard, @Interceptor, @Pipe, @Filter, CanActivate, NestInterceptor, PipeTransform, ExceptionFilter, ValidationPipe, class-validator, class-transformer, @Type, @ValidateNested, PartialType, PickType, OmitType, IntersectionType, @nestjs/mapped-types, ExecutionContext, ArgumentsHost, HttpException, BadRequestException, NotFoundException, ConflictException, PrismaClient, OnModuleInit, OnModuleDestroy, forRootAsync, useFactory, ConfigService, Symbol injection tokens, useClass, useFactory, useValue, useExisting, REQUEST scope, TRANSIENT scope, ClassSerializerInterceptor, @Exclude, @Expose, VersioningType.URI, @Version, supertest, Test.createTestingModule
