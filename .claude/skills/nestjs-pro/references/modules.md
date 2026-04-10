# NestJS Modules

## Purpose

Module architecture rules for feature isolation, shared utilities, dynamic configuration, and circular dependency resolution.

## Table of Contents

- [Feature Module Pattern](#feature-module-pattern)
- [SharedModule](#sharedmodule)
- [Global Modules](#global-modules)
- [Dynamic Modules — forRootAsync](#dynamic-modules--forrootasync)
- [Circular Dependencies](#circular-dependencies)
- [DO / DON'T](#do--dont)
- [Related Documents](#related-documents)

---

## Feature Module Pattern

One module per domain. Declare `controllers`, `providers`, and `exports` explicitly. Never rely on implicit visibility.

```typescript
@Module({
  imports: [TypeOrmModule.forFeature([UserEntity])],
  controllers: [UsersController],
  providers: [UsersService, { provide: USER_REPO, useClass: PrismaUserRepository }],
  exports: [UsersService],  // only export what other modules genuinely need
})
export class UsersModule {}
```

- `exports` limits the public API of a module — omit providers that are internal implementation details.
- Import `UsersModule` in another feature module to access `UsersService` via DI.

---

## SharedModule

Use a `SharedModule` for utilities needed across multiple features (e.g., `LoggerService`, `MailService`).

```typescript
@Module({
  providers: [LoggerService, MailService],
  exports: [LoggerService, MailService],
})
export class SharedModule {}
```

- Import `SharedModule` in each feature module that needs it.
- Do not put domain logic in `SharedModule` — it is infrastructure only.

---

## Global Modules

Use `@Global()` sparingly — only for modules that every feature genuinely needs without explicit import.

```typescript
@Global()
@Module({
  providers: [PrismaService],
  exports: [PrismaService],
})
export class PrismaModule {}
```

**Reserve `@Global()` for**: `PrismaModule`, `ConfigModule`.

**Never use `@Global()` on**: feature modules, domain services, or anything with business logic.

---

## Dynamic Modules — forRootAsync

Use `forRootAsync` with `useFactory` when a module needs async configuration (e.g., reading from `ConfigService`).

```typescript
// database.module.ts
@Module({})
export class DatabaseModule {
  static forRootAsync(options: DatabaseModuleAsyncOptions): DynamicModule {
    return {
      module: DatabaseModule,
      imports: options.imports ?? [],
      providers: [
        {
          provide: DATABASE_OPTIONS,
          useFactory: options.useFactory,
          inject: options.inject ?? [],
        },
        DatabaseService,
      ],
      exports: [DatabaseService],
      global: false,
    };
  }
}

// app.module.ts — consuming forRootAsync
DatabaseModule.forRootAsync({
  imports: [ConfigModule],
  useFactory: async (config: ConfigService) => ({
    host: config.get<string>('DB_HOST'),
    port: config.get<number>('DB_PORT'),
    database: config.get<string>('DB_NAME'),
  }),
  inject: [ConfigService],
})
```

- `inject` array order must match `useFactory` parameter order.
- `ConfigModule.forRoot({ isGlobal: true })` means you can skip `imports: [ConfigModule]` in child modules.

---

## Circular Dependencies

`forwardRef()` resolves circular module imports at runtime. Treat it as a code smell.

```typescript
// users.module.ts — if UsersModule and AuthModule depend on each other
@Module({
  imports: [forwardRef(() => AuthModule)],
  providers: [UsersService],
  exports: [UsersService],
})
export class UsersModule {}

// Service-level circular dep
@Injectable()
export class UsersService {
  constructor(
    @Inject(forwardRef(() => AuthService))
    private readonly authService: AuthService,
  ) {}
}
```

**Preferred resolution**: Extract the shared logic into a third `SharedAuthModule` or `TokenService` that both modules import. This removes the cycle entirely.

---

## DO / DON'T

| DO | DON'T |
|----|-------|
| Declare explicit `exports: []` in every module | Leave `exports` undefined and rely on implicit access |
| Use `forRootAsync` + `useFactory` for async config | Use `forRoot` with hard-coded config values |
| Use `@Global()` only on PrismaModule / ConfigModule | Apply `@Global()` to feature modules |
| Extract shared services to resolve circular deps | Pile `forwardRef()` calls without addressing the root cause |
| Keep each module focused on one domain | Mix unrelated providers in one module for convenience |

---

## Related Documents

- `references/controllers.md` — Controller binding within modules
- `references/prisma.md` — PrismaModule global setup
- `references/project-structure.md` — Where module files live
- `references/testing.md` — `Test.createTestingModule` usage
