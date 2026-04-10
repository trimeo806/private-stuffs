# NestJS GIPF Layer

## Purpose

Guards, Interceptors, Pipes, and Filters — execution order, implementation patterns, binding scope, and canonical examples.

## Table of Contents

- [Execution Order](#execution-order)
- [Binding Scope](#binding-scope)
- [Guards](#guards)
- [Interceptors](#interceptors)
- [Pipes](#pipes)
- [Filters](#filters)
- [DO / DON'T](#do--dont)
- [Related Documents](#related-documents)

---

## Execution Order

A request passes through every layer in this exact order:

```
Middleware → Guards → Before Interceptors → Pipes → Controller Method
         → Service → After Interceptors → Exception Filters (on error)
```

- Exception filters wrap the entire response path — they catch errors thrown anywhere in the chain.
- Interceptors have a before-phase (before `next.handle()`) and an after-phase (inside the returned observable).

---

## Binding Scope

Binding priority: **global (main.ts)** > **controller** > **route**. More specific bindings override less specific ones for the same type.

```typescript
// Global — applies to every route in the app
app.useGlobalGuards(new AuthGuard());
app.useGlobalInterceptors(new LoggingInterceptor());
app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));
app.useGlobalFilters(new GlobalExceptionFilter());

// Controller-level — applies to all routes in the class
@UseGuards(RolesGuard)
@UseInterceptors(ClassSerializerInterceptor)
@Controller('users')
export class UsersController { ... }

// Route-level — applies to one handler only
@Get(':id')
@UseGuards(OwnerGuard)
findOne(@Param('id') id: string) { ... }
```

---

## Guards

Implement `CanActivate`. Return `true` to allow, `false` (or throw) to deny. Use `Reflector` to read metadata.

```typescript
// common/guards/roles.guard.ts
import { Injectable, CanActivate, ExecutionContext } from '@nestjs/common';
import { Reflector } from '@nestjs/core';

export const Roles = (...roles: string[]) => SetMetadata('roles', roles);

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private readonly reflector: Reflector) {}

  canActivate(context: ExecutionContext): boolean {
    const requiredRoles = this.reflector.getAllAndOverride<string[]>('roles', [
      context.getHandler(),   // route-level metadata first
      context.getClass(),     // controller-level fallback
    ]);

    if (!requiredRoles) return true; // no roles required — public route

    const { user } = context.switchToHttp().getRequest();
    return requiredRoles.some((role) => user?.roles?.includes(role));
  }
}
```

Usage:
```typescript
@Get('admin')
@Roles('admin')
@UseGuards(AuthGuard, RolesGuard)
adminOnly() { ... }
```

---

## Interceptors

Implement `NestInterceptor`. Wrap `next.handle()` with RxJS operators for logging, timeout, caching, or response transformation.

```typescript
// common/interceptors/logging.interceptor.ts
import { Injectable, NestInterceptor, ExecutionContext, CallHandler, RequestTimeoutException } from '@nestjs/common';
import { Observable, throwError, TimeoutError } from 'rxjs';
import { tap, timeout, catchError } from 'rxjs/operators';

@Injectable()
export class LoggingInterceptor implements NestInterceptor {
  intercept(context: ExecutionContext, next: CallHandler): Observable<unknown> {
    const req = context.switchToHttp().getRequest();
    const start = Date.now();

    return next.handle().pipe(
      timeout(5000),
      tap(() => {
        console.log(`[${req.method}] ${req.url} — ${Date.now() - start}ms`);
      }),
      catchError((err) => {
        if (err instanceof TimeoutError) {
          return throwError(() => new RequestTimeoutException());
        }
        return throwError(() => err);
      }),
    );
  }
}
```

---

## Pipes

Implement `PipeTransform`. Used for validation and value transformation. `ValidationPipe` is the standard global pipe.

```typescript
// main.ts — canonical ValidationPipe setup
app.useGlobalPipes(
  new ValidationPipe({
    whitelist: true,              // strip unknown properties
    forbidNonWhitelisted: true,   // throw 400 on unknown properties
    transform: true,              // coerce plain objects to DTO class instances
    transformOptions: {
      enableImplicitConversion: true, // coerce query param strings to numbers/booleans
    },
  }),
);
```

Custom pipe example — parse and validate a UUID param:

```typescript
import { PipeTransform, Injectable, BadRequestException } from '@nestjs/common';
import { isUUID } from 'class-validator';

@Injectable()
export class ParseUUIDPipe implements PipeTransform<string, string> {
  transform(value: string): string {
    if (!isUUID(value)) {
      throw new BadRequestException(`${value} is not a valid UUID`);
    }
    return value;
  }
}

// Usage
@Get(':id')
findOne(@Param('id', ParseUUIDPipe) id: string) { ... }
```

---

## Filters

Implement `ExceptionFilter`. Catch exceptions, shape the error response, and prevent raw stack traces leaking to clients.

```typescript
// common/filters/global-exception.filter.ts
import {
  Catch, ExceptionFilter, ArgumentsHost, HttpException, HttpStatus, Logger,
} from '@nestjs/common';
import { Request, Response } from 'express';

@Catch()
export class GlobalExceptionFilter implements ExceptionFilter {
  private readonly logger = new Logger(GlobalExceptionFilter.name);

  catch(exception: unknown, host: ArgumentsHost): void {
    const ctx = host.switchToHttp();
    const res = ctx.getResponse<Response>();
    const req = ctx.getRequest<Request>();

    const status =
      exception instanceof HttpException
        ? exception.getStatus()
        : HttpStatus.INTERNAL_SERVER_ERROR;

    const message =
      exception instanceof HttpException
        ? ((exception.getResponse() as { message?: string }).message ?? exception.message)
        : 'Internal server error';

    if (!(exception instanceof HttpException)) {
      this.logger.error(exception);  // log unexpected errors only
    }

    res.status(status).json({
      statusCode: status,
      message,
      timestamp: new Date().toISOString(),
      path: req.url,
    });
  }
}
```

Register in `main.ts`:

```typescript
app.useGlobalFilters(new GlobalExceptionFilter());
```

---

## DO / DON'T

| DO | DON'T |
|----|-------|
| Bind global GIPF in `main.ts` using `useGlobal*` methods | Register global GIPF inside `AppModule` providers (breaks DI context) |
| Use `Reflector.getAllAndOverride` in guards to respect route > controller metadata | Call `Reflector.get` directly — it only reads one level |
| Wrap `next.handle()` with `timeout()` in interceptors | Let requests hang without a timeout |
| Log unexpected errors in the global filter; keep `HttpException` messages clean | Expose stack traces or Prisma error details to the client |
| Use `transform: true` on `ValidationPipe` | Skip `transform: true` — `@Type()` coercions will silently fail |

---

## Related Documents

- `references/validation.md` — DTO validation and class-validator decorators
- `references/exceptions.md` — HttpException hierarchy and custom exceptions
- `references/controllers.md` — Binding GIPF at controller and route level
- `references/modules.md` — Registering providers used by guards/interceptors
