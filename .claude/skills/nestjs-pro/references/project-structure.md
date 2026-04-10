# NestJS Project Structure

## Purpose

Standard directory layout for a NestJS application, `main.ts` bootstrap order, and file naming conventions.

## Table of Contents

- [Directory Layout](#directory-layout)
- [main.ts Bootstrap Order](#maints-bootstrap-order)
- [File Naming Conventions](#file-naming-conventions)
- [nest-cli.json](#nest-clifjson)
- [DO / DON'T](#do--dont)
- [Related Documents](#related-documents)

---

## Directory Layout

```
src/
├── main.ts                         # Bootstrap — app factory + global config
├── app.module.ts                   # Root module — imports PrismaModule, ConfigModule, feature modules
├── app.controller.ts               # Health check endpoint (optional)
│
├── common/                         # Cross-cutting infrastructure (no domain logic)
│   ├── decorators/                 # Custom param/method decorators
│   │   └── current-user.decorator.ts
│   ├── filters/                    # Exception filters
│   │   └── global-exception.filter.ts
│   ├── guards/                     # Auth and role guards
│   │   ├── jwt-auth.guard.ts
│   │   └── roles.guard.ts
│   ├── interceptors/               # Logging, timeout, serialization interceptors
│   │   └── logging.interceptor.ts
│   └── pipes/                      # Custom validation/transformation pipes
│       └── parse-uuid.pipe.ts
│
├── config/                         # Configuration schemas and factories
│   ├── app.config.ts
│   └── database.config.ts
│
├── database/                       # Prisma service and module
│   ├── prisma.service.ts
│   └── prisma.module.ts
│
└── modules/                        # Feature modules — one subdirectory per domain
    ├── users/
    │   ├── dto/
    │   │   ├── create-user.dto.ts
    │   │   └── update-user.dto.ts
    │   ├── repositories/
    │   │   ├── user-repo.token.ts          # Symbol('IUserRepository')
    │   │   ├── user.repository.interface.ts
    │   │   └── prisma-user.repository.ts
    │   ├── users.controller.ts
    │   ├── users.service.ts
    │   └── users.module.ts
    │
    └── auth/
        ├── dto/
        │   └── login.dto.ts
        ├── strategies/
        │   └── jwt.strategy.ts
        ├── auth.controller.ts
        ├── auth.service.ts
        └── auth.module.ts

test/                               # E2E test files (Jest with supertest)
├── users.e2e-spec.ts
└── jest-e2e.json

prisma/
├── schema.prisma
└── migrations/
```

---

## main.ts Bootstrap Order

Apply global bindings in this exact sequence. Order matters — later registrations can depend on earlier ones being set up.

```typescript
// src/main.ts
import { NestFactory, Reflector } from '@nestjs/core';
import { ValidationPipe, ClassSerializerInterceptor } from '@nestjs/common';
import { AppModule } from './app.module';
import { GlobalExceptionFilter } from './common/filters/global-exception.filter';
import { LoggingInterceptor } from './common/interceptors/logging.interceptor';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  // 1. Pipes — validate and transform incoming data first
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
    }),
  );

  // 2. Interceptors — wrap request/response pipeline
  app.useGlobalInterceptors(
    new LoggingInterceptor(),
    new ClassSerializerInterceptor(app.get(Reflector)),
  );

  // 3. Filters — catch any unhandled exception after the pipeline
  app.useGlobalFilters(new GlobalExceptionFilter());

  // 4. CORS — configure before listen
  app.enableCors({
    origin: process.env.ALLOWED_ORIGINS?.split(',') ?? '*',
    credentials: true,
  });

  // 5. Versioning (if needed)
  // app.enableVersioning({ type: VersioningType.URI });

  // 6. Swagger (if needed)
  // SwaggerModule.setup('api', app, document);

  // 7. Start listening
  await app.listen(process.env.PORT ?? 3000);
}

bootstrap();
```

---

## File Naming Conventions

NestJS uses a `{name}.{type}.ts` pattern for all generated files:

| Type | Example filename |
|------|-----------------|
| Module | `users.module.ts` |
| Controller | `users.controller.ts` |
| Service | `users.service.ts` |
| Guard | `jwt-auth.guard.ts` |
| Interceptor | `logging.interceptor.ts` |
| Pipe | `parse-uuid.pipe.ts` |
| Filter | `global-exception.filter.ts` |
| Decorator | `current-user.decorator.ts` |
| DTO | `create-user.dto.ts`, `update-user.dto.ts` |
| Interface | `user.repository.interface.ts` |
| Spec (unit) | `users.service.spec.ts` |
| Spec (E2E) | `users.e2e-spec.ts` |

- All filenames are `kebab-case`.
- Spec files sit next to the file they test (unit) or in `test/` (E2E).

---

## nest-cli.json

Standard configuration for a monorepo or single-app project:

```json
{
  "$schema": "https://json.schemastore.org/nest-cli",
  "collection": "@nestjs/schematics",
  "sourceRoot": "src",
  "compilerOptions": {
    "deleteOutDir": true,
    "plugins": ["@nestjs/swagger"]
  }
}
```

- `plugins: ["@nestjs/swagger"]` enables automatic `@ApiProperty` inference from class-validator decorators — reduces manual annotation.
- `deleteOutDir: true` cleans the `dist/` folder before each build.

---

## DO / DON'T

| DO | DON'T |
|----|-------|
| Put all shared infrastructure in `common/` | Scatter guards, filters, and interceptors inside feature modules |
| Keep one feature per `modules/{name}/` directory | Put multiple domains in the same module directory |
| Place spec files alongside source files for unit tests | Put all tests in a single flat `test/` directory |
| Follow `{name}.{type}.ts` naming consistently | Use arbitrary names like `user-helpers.ts` for NestJS-typed files |
| Configure `main.ts` bootstrap order: pipes → interceptors → filters → cors → listen | Configure CORS or Swagger before pipes — they must precede `listen` but can follow global GIPF setup |

---

## Related Documents

- `references/modules.md` — Module wiring and imports
- `references/gipf.md` — Where to put guards, interceptors, pipes, and filters
- `references/prisma.md` — `database/` directory and PrismaModule
- `references/testing.md` — `test/` directory and E2E spec setup
