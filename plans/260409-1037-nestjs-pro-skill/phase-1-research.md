---
phase: 1
title: "Research NestJS best practices"
effort: 1h
depends: []
---

# Phase 1 — Research NestJS Best Practices

## Goal

Consolidate the NestJS 10+ patterns that must be encoded in the skill. This phase feeds Phase 2 directly — no separate output file needed.

## Agent & Skills

- **Agent**: `researcher`
- **Skills**: `docs-seeker`, `knowledge-retrieval`
- **Handoffs**:
  - After completion → Phase 2 (`backend-developer`)

## Topics to Research

### 1. Module Architecture
- Feature module pattern: one `*.module.ts` per domain
- `SharedModule` for cross-cutting providers (`exports: [...]`)
- `CoreModule` (global singleton services, imported once in `AppModule`)
- `forRoot()` vs `forRootAsync()` — when to use `ConfigService` in `useFactory`
- `@Global()` — when appropriate, when to avoid
- Circular dependency resolution with `forwardRef()`

### 2. Dependency Injection
- `@Injectable()`, `@Inject(TOKEN)`, custom injection tokens (`Symbol` vs string)
- Custom providers: `useClass`, `useFactory`, `useValue`, `useExisting`
- Provider scope: `DEFAULT` (singleton) vs `REQUEST` (per-request, performance cost) vs `TRANSIENT`
- Module encapsulation rules: never export what callers don't need

### 3. Controllers
- Route decorators: `@Get`, `@Post`, `@Put`, `@Patch`, `@Delete`, `@All`
- Param decorators: `@Param()`, `@Query()`, `@Body()`, `@Headers()`
- Why to avoid `@Res()` — breaks interceptors and exception filters
- `@HttpCode()`, `@Header()`, `@Redirect()`
- URI versioning: `app.enableVersioning({ type: VersioningType.URI })`
- Response serialization: `ClassSerializerInterceptor` + `@Exclude()` / `@Expose()`

### 4. Services
- Single-responsibility per domain concern
- Repository interface boundary (ORM-agnostic): service depends on interface, not ORM class
- Async patterns: `async/await` only — never block the event loop
- Where to put business logic: service layer, never controller

### 5. GIPF Layer (Guards / Interceptors / Pipes / Filters)
- **Guard** (`CanActivate`): auth, RBAC. Example: `JwtAuthGuard`, `RolesGuard`
- **Interceptor** (`NestInterceptor`): logging, response transform, timeout. `tap()` / `map()` on Observable
- **Pipe** (`PipeTransform`): input validation and transformation. Global `ValidationPipe` config
- **Filter** (`ExceptionFilter`): unified error envelope. `@Catch(HttpException)`
- Binding order: global (main.ts) → controller → route-level

### 6. Validation & DTOs
- `class-validator` decorators: `@IsString()`, `@IsUUID()`, `@IsEmail()`, `@IsOptional()`, `@ValidateNested()`
- `class-transformer`: `@Type()`, `@Transform()`, `@Exclude()`, `@Expose()`
- `ValidationPipe` global config: `{ whitelist: true, forbidNonWhitelisted: true, transform: true }`
- Why `transform: true` is required for `@Type()` coercion (plain object → class instance)
- Mapped types from `@nestjs/mapped-types`: `PartialType`, `PickType`, `OmitType`, `IntersectionType`
- `@ApiProperty()` parity: every DTO field gets swagger annotation

### 7. Exception Handling
- `HttpException` and built-in subclasses: `NotFoundException`, `BadRequestException`, `UnauthorizedException`, `ForbiddenException`, `ConflictException`
- Custom domain exceptions: `class UserNotFoundException extends NotFoundException`
- `HttpExceptionFilter` — consistent error envelope: `{ statusCode, message, timestamp, path }`
- Binding globally in `main.ts` via `app.useGlobalFilters()`

### 8. Prisma Integration
- `PrismaService extends PrismaClient` — `onModuleInit()` + `enableShutdownHooks(app)`
- `PrismaModule` as `@Global()` — imported once in `AppModule`, available everywhere
- Repository interface in service layer: `IUserRepository` implemented by `PrismaUserRepository`
- TypeORM secondary note: `@InjectRepository(Entity)` — same service interface pattern applies

### 9. Project Structure
- `src/` layout: `app.module.ts`, `main.ts`, `common/`, `config/`, feature folders
- `main.ts` bootstrap checklist: global pipe, global filter, versioning, swagger, CORS, compression
- `nest-cli.json`: `sourceRoot`, `compilerOptions`, `assets`

### 10. Testing
- Unit: `Test.createTestingModule({ providers: [...] }).compile()`
- Mocking: `{ provide: ServiceClass, useValue: { method: jest.fn() } }`
- E2E: `@nestjs/testing` + `supertest` — `app.getHttpServer()`

## Validation

- [ ] All 10 topic areas have concrete examples and DO/DON'T rules identified
- [ ] Prisma pattern is ORM-agnostic at service boundary
- [ ] GIPF binding order is confirmed against NestJS docs
- [ ] `transform: true` coercion behavior is verified
