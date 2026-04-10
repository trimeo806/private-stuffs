# NestJS Exception Handling

## Purpose

Built-in HTTP exception hierarchy, custom exception classes, and consistent error response shapes.

## Table of Contents

- [Built-in HTTP Exceptions](#built-in-http-exceptions)
- [Throwing Exceptions in Services](#throwing-exceptions-in-services)
- [Custom Exception Classes](#custom-exception-classes)
- [Error Response Shape](#error-response-shape)
- [DO / DON'T](#do--dont)
- [Related Documents](#related-documents)

---

## Built-in HTTP Exceptions

NestJS ships with subclasses of `HttpException` for all common status codes. Use the most specific one available.

| Exception Class | HTTP Status |
|----------------|-------------|
| `BadRequestException` | 400 |
| `UnauthorizedException` | 401 |
| `ForbiddenException` | 403 |
| `NotFoundException` | 404 |
| `MethodNotAllowedException` | 405 |
| `NotAcceptableException` | 406 |
| `RequestTimeoutException` | 408 |
| `ConflictException` | 409 |
| `GoneException` | 410 |
| `PayloadTooLargeException` | 413 |
| `UnsupportedMediaTypeException` | 415 |
| `UnprocessableEntityException` | 422 |
| `TooManyRequestsException` | 429 |
| `InternalServerErrorException` | 500 |
| `NotImplementedException` | 501 |
| `ServiceUnavailableException` | 503 |

---

## Throwing Exceptions in Services

```typescript
import { Injectable, NotFoundException, ConflictException, BadRequestException } from '@nestjs/common';

@Injectable()
export class UsersService {
  async findOne(id: string) {
    const user = await this.repo.findById(id);
    if (!user) {
      throw new NotFoundException(`User with id ${id} was not found`);
    }
    return user;
  }

  async create(dto: CreateUserDto) {
    const existing = await this.repo.findByEmail(dto.email);
    if (existing) {
      throw new ConflictException(`A user with email ${dto.email} already exists`);
    }
    return this.repo.create(dto);
  }

  async activate(id: string) {
    const user = await this.findOne(id);
    if (user.isActive) {
      throw new BadRequestException('User is already active');
    }
    return this.repo.update(id, { isActive: true });
  }
}
```

- Throw in the service, not the controller — services own business rules.
- The `GlobalExceptionFilter` (see `references/gipf.md`) catches every unhandled exception and formats the response.

---

## Custom Exception Classes

Extend the appropriate `HttpException` subclass for domain-specific errors. This gives callers a typed exception to catch and keeps error messages consistent.

```typescript
// common/exceptions/user-already-exists.exception.ts
import { ConflictException } from '@nestjs/common';

export class UserAlreadyExistsException extends ConflictException {
  constructor(email: string) {
    super(`User with email '${email}' already exists`);
  }
}

// common/exceptions/insufficient-credits.exception.ts
import { UnprocessableEntityException } from '@nestjs/common';

export class InsufficientCreditsException extends UnprocessableEntityException {
  constructor(required: number, available: number) {
    super(`Insufficient credits: required ${required}, available ${available}`);
  }
}

// Usage in service
throw new UserAlreadyExistsException(dto.email);
throw new InsufficientCreditsException(10, user.credits);
```

For exceptions that carry structured detail data:

```typescript
import { HttpException, HttpStatus } from '@nestjs/common';

export class ValidationFailedException extends HttpException {
  constructor(errors: Record<string, string[]>) {
    super(
      {
        statusCode: HttpStatus.UNPROCESSABLE_ENTITY,
        message: 'Validation failed',
        errors,
      },
      HttpStatus.UNPROCESSABLE_ENTITY,
    );
  }
}
```

---

## Error Response Shape

The `GlobalExceptionFilter` (see `references/gipf.md`) normalizes all errors to this shape:

```json
{
  "statusCode": 404,
  "message": "User with id abc-123 was not found",
  "timestamp": "2026-04-09T10:00:00.000Z",
  "path": "/users/abc-123"
}
```

For `ValidationPipe` errors (400), NestJS produces:

```json
{
  "statusCode": 400,
  "message": ["email must be an email", "password is too short"],
  "error": "Bad Request"
}
```

The global filter should handle this by reading `exception.getResponse()` and extracting `message` or the full object.

```typescript
// In GlobalExceptionFilter.catch():
const response = exception.getResponse();
const message =
  typeof response === 'object' && 'message' in response
    ? (response as { message: string | string[] }).message
    : exception.message;
```

---

## DO / DON'T

| DO | DON'T |
|----|-------|
| Use the most specific built-in exception class | Throw raw `new Error('...')` from NestJS routes |
| Extend `HttpException` subclasses for custom domain errors | Extend `HttpException` directly with no added context |
| Throw from the service layer | Throw from the controller — controllers should be thin |
| Let `GlobalExceptionFilter` format responses uniformly | Construct `res.status(x).json(...)` inside controllers |
| Log unexpected (non-`HttpException`) errors in the filter | Log every 404/400 as an error — reserve error logs for unexpected failures |
| Return user-safe messages (no stack traces, no SQL text) | Expose `exception.stack` or Prisma error details in the response |

---

## Related Documents

- `references/gipf.md` — GlobalExceptionFilter implementation
- `references/validation.md` — 400 errors from ValidationPipe
- `references/controllers.md` — HTTP method + route context
- `references/prisma.md` — Catching Prisma constraint errors and rethrowing as domain exceptions
