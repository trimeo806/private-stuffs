---
phase: 3
title: 'Request Lifecycle — Pipes, Guards, Interceptors, Filters'
effort: 10h
depends: [2]
weeks: '4'
---

# Phase 3: Request Lifecycle — Pipes, Guards, Interceptors, Filters

## Context Links
- [Plan Overview](./plan.md)
- [Phase 2](./phase-2-nestjs-core.md)

## Agent & Skills
- **Agent**: `researcher`
- **Skills**: `nestjs-pro`, `typescript-pro`
- **Handoffs**: After completion → `code-reviewer` → Phase 4

---

## What You Build This Phase

- `POST /tasks` with a missing `title` → returns a structured `400` error (not a crash)
- `POST /tasks` with extra fields like `"hacker": "yes"` → stripped automatically
- Every request logs `[HTTP] POST /tasks - 4ms` to the console

---

## Concept: The Request Pipeline

When a request arrives at your NestJS app, it doesn't go straight to your controller.
It passes through **7 layers in order**, each with a specific job:

```
Incoming HTTP Request
        │
        ▼
1. Middleware          → Runs first. No NestJS context. Good for: logging, CORS, body parsing.
        │
        ▼
2. Guards             → "Is this request allowed?" Returns true/false.
        │               Good for: authentication, authorization.
        ▼
3. Interceptors (pre) → Runs before the handler. Good for: transforming the request,
        │               adding timing/tracing.
        ▼
4. Pipes              → "Is this data valid?" Validates and transforms input.
        │               Good for: DTO validation, type coercion (string → number).
        ▼
5. Controller Handler → Your actual function. Calls the service.
        │
        ▼
6. Interceptors (post)→ Runs after the handler. Good for: transforming the response,
        │               logging response time.
        ▼
7. Exception Filters  → Catches any error thrown in layers 2–6.
        │               Good for: formatting error responses consistently.
        ▼
HTTP Response
```

**Why this order?** It's designed so each layer has the right information:
- Guards run before pipes because there's no point validating data for an unauthorized user
- Interceptors wrap both the before and after so they can measure duration
- Exception filters are last because they catch errors from all other layers

---

## Concept: What Is a DTO?

A **DTO (Data Transfer Object)** is a class that describes the shape of data coming *into* your API.

**Why a class and not an interface?**

```typescript
// Interface — erased at runtime, no validation possible
interface CreateTaskDto {
  title: string;
  description: string;
}

// Class — exists at runtime, can have decorators, validators, and metadata
class CreateTaskDto {
  @IsString()
  @IsNotEmpty()
  title: string;

  @IsString()
  description: string;
}
```

TypeScript interfaces disappear when compiled to JavaScript. Classes stay. NestJS's
`ValidationPipe` needs runtime access to the class to read the `@IsString()` decorator
and actually check the value. Interfaces can't do this.

---

## Concept: What Is a Pipe?

A **pipe** intercepts incoming data before it reaches your controller and either:
1. **Validates** it — throws an error if data is wrong
2. **Transforms** it — converts the type (e.g. `"42"` → `42`)

```
POST /tasks  →  body: { "title": "" }
                              │
                              ▼
                         ValidationPipe
                              │
                   title is empty → throw BadRequestException
                              │
                              ▼
                         400 Bad Request
                     (never reaches controller)
```

NestJS's built-in `ValidationPipe` reads your DTO class's decorators (`@IsString()`,
`@IsNotEmpty()`, etc.) and throws a `BadRequestException` if the data doesn't match.

---

## Concept: What Is an Exception Filter?

By default, NestJS handles `HttpException` subclasses for you. But the format it returns
is fixed. An **exception filter** lets you customize error responses.

**Default NestJS error:**
```json
{ "statusCode": 404, "message": "Task not found", "error": "Not Found" }
```

**With your custom filter:**
```json
{
  "statusCode": 404,
  "message": "Task not found",
  "error": "Not Found",
  "timestamp": "2026-04-09T12:00:00.000Z",
  "path": "/tasks/abc-123"
}
```

The filter catches the error *before* it reaches the client, letting you add fields,
change the format, or log the error.

---

## Concept: What Is an Interceptor?

An **interceptor** wraps your route handler. It runs code before and after the handler executes.

```typescript
intercept(context, next) {
  console.log('Before handler');           // runs first
  return next.handle().pipe(
    tap(() => console.log('After handler')) // runs after handler returns
  );
}
```

The `next.handle()` call is what actually runs your controller method. Everything before it
is "pre-handler", everything after (via RxJS `.pipe()`) is "post-handler".

Common uses:
- **Logging**: measure how long each request takes
- **Response transformation**: wrap every response in `{ data: ..., meta: ... }`
- **Timeouts**: cancel requests that take too long
- **Caching** (Phase 7)

---

## Step 1: Install class-validator and class-transformer (5 min)

```bash
npm i class-validator class-transformer
```

- **class-validator** — provides decorators like `@IsString()`, `@IsEmail()`, `@Min()`
- **class-transformer** — converts plain JSON objects into typed class instances
  (needed for class-validator to work at runtime)

---

## Step 2: Create DTOs (30 min)

Create `src/tasks/dto/create-task.dto.ts`:

```typescript
import { IsString, IsNotEmpty, MinLength, IsOptional } from 'class-validator';

// This class defines what a "create task" request body must look like.
// The decorators are the validation rules.
export class CreateTaskDto {
  @IsString()
  // ↑ The value must be a string (not a number, object, etc.)

  @IsNotEmpty()
  // ↑ The string can't be empty ('')

  @MinLength(3)
  // ↑ The string must be at least 3 characters
  title: string;

  @IsString()
  @IsOptional()
  // ↑ This field is allowed to be absent entirely
  //   Without @IsOptional(), omitting description throws an error
  description?: string;
}
```

Create `src/tasks/dto/update-task-status.dto.ts`:

```typescript
import { IsEnum } from 'class-validator';
import { TaskStatus } from '../task.model';

export class UpdateTaskStatusDto {
  @IsEnum(TaskStatus)
  // ↑ Value must be one of: 'TODO', 'IN_PROGRESS', 'DONE'
  //   Sending { "status": "INVALID" } throws a 400
  status: TaskStatus;
}
```

---

## Step 3: Update Controller to Use DTOs (20 min)

In `tasks.controller.ts`, replace the plain object types:

```typescript
import { CreateTaskDto } from './dto/create-task.dto';
import { UpdateTaskStatusDto } from './dto/update-task-status.dto';

// Before (no validation):
@Post()
create(@Body() body: { title: string; description: string }) {

// After (DTO class = validation rules attached):
@Post()
create(@Body() dto: CreateTaskDto) {
  // By the time this runs, NestJS has already validated dto.title is a non-empty string
  return this.tasksService.create(dto.title, dto.description);
}

@Patch(':id/status')
updateStatus(@Param('id') id: string, @Body() dto: UpdateTaskStatusDto) {
  // dto.status is guaranteed to be a valid TaskStatus enum value
  return this.tasksService.updateStatus(id, dto.status);
}
```

---

## Step 4: Register the Global ValidationPipe (15 min)

In `src/main.ts`, add the pipe before `app.listen()`:

```typescript
import { ValidationPipe } from '@nestjs/common';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  app.useGlobalPipes(new ValidationPipe({
    whitelist: true,
    // ↑ IMPORTANT: Automatically strips any properties from the body that
    //   aren't declared in the DTO. If someone sends { title: 'ok', hacker: 'yes' },
    //   the 'hacker' field is silently removed before reaching your controller.
    //   This prevents mass-assignment attacks.

    forbidNonWhitelisted: true,
    // ↑ Instead of silently stripping unknown fields, throw a 400 error.
    //   Combined with whitelist: true, this tells the client "you sent invalid fields".

    transform: true,
    // ↑ Automatically converts the plain JSON object to an instance of the DTO class.
    //   Without this, dto would be a plain object and class-validator wouldn't work.
    //   Also converts route params: @Param('id') id: number would auto-convert '42' → 42
  }));

  await app.listen(3000);
}
```

**Test it now:**

**Bash / macOS / Git Bash:**
```bash
# Missing required field → 400
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -d '{"description":"no title"}'

# Unknown field → 400 ("property extraField should not exist")
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"ok","description":"ok","extraField":"injected"}'

# Valid request → 201
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy milk","description":"2% oat"}'
```

**Windows PowerShell:**
```powershell
# Missing required field → 400
try {
  Invoke-RestMethod -Method Post "http://localhost:3000/tasks" `
    -ContentType "application/json" `
    -Body '{"description":"no title"}'
} catch { $_.ErrorDetails.Message | ConvertFrom-Json }

# Unknown field → 400
try {
  Invoke-RestMethod -Method Post "http://localhost:3000/tasks" `
    -ContentType "application/json" `
    -Body '{"title":"ok","description":"ok","extraField":"injected"}'
} catch { $_.ErrorDetails.Message | ConvertFrom-Json }

# Valid request → 201
Invoke-RestMethod -Method Post "http://localhost:3000/tasks" `
  -ContentType "application/json" `
  -Body '{"title":"Buy milk","description":"2% oat"}'
```

> **PowerShell tip:** `Invoke-RestMethod` throws an exception on 4xx/5xx responses.
> Wrap in `try/catch` and read `$_.ErrorDetails.Message` to see the error body.

---

## Step 5: Global Exception Filter (45 min)

Create `src/common/filters/all-exceptions.filter.ts`:

```typescript
import {
  ExceptionFilter,
  Catch,
  ArgumentsHost,
  HttpException,
  HttpStatus,
  Logger,
} from '@nestjs/common';
import { Request, Response } from 'express';

@Catch()
// ↑ @Catch() with no arguments catches ALL exceptions.
//   @Catch(HttpException) would only catch HTTP exceptions.
export class AllExceptionsFilter implements ExceptionFilter {
  private logger = new Logger(AllExceptionsFilter.name);

  catch(exception: unknown, host: ArgumentsHost) {
    // ArgumentsHost gives access to the underlying request/response
    // switchToHttp() gives us the Express-specific objects
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const request = ctx.getRequest<Request>();

    // Determine the status code:
    // - If it's a NestJS HttpException (NotFoundException, etc.), use its status
    // - Otherwise it's an unexpected error → 500
    const status = exception instanceof HttpException
      ? exception.getStatus()
      : HttpStatus.INTERNAL_SERVER_ERROR;

    // Get the error message:
    // - HttpException.getResponse() returns the message (string or object)
    // - Unknown errors → generic message (never expose stack traces to clients!)
    const message = exception instanceof HttpException
      ? exception.getResponse()
      : 'Internal server error';

    // Log unexpected errors (500s) with the full stack trace for debugging
    if (status === HttpStatus.INTERNAL_SERVER_ERROR) {
      this.logger.error(exception);
    }

    // Send a consistent error shape to all clients
    response.status(status).json({
      statusCode: status,
      timestamp: new Date().toISOString(),
      path: request.url,
      message,
    });
  }
}
```

Register in `main.ts`:

```typescript
import { AllExceptionsFilter } from './common/filters/all-exceptions.filter';

app.useGlobalFilters(new AllExceptionsFilter());
```

**Test it:**

**Bash:**
```bash
curl http://localhost:3000/tasks/not-a-real-id
# After filter: { "statusCode": 404, "timestamp": "...", "path": "/tasks/not-a-real-id", "message": {...} }
```

**Windows PowerShell:**
```powershell
try {
  Invoke-RestMethod "http://localhost:3000/tasks/not-a-real-id"
} catch {
  $_.ErrorDetails.Message | ConvertFrom-Json
  # Shows: statusCode, timestamp, path, message
}
```

Now every error — whether from your code or from NestJS internals — returns the same shape.
Frontend developers can rely on this structure for every error response.

---

## Step 6: Logging Interceptor (45 min)

Create `src/common/interceptors/logging.interceptor.ts`:

```typescript
import {
  Injectable,
  NestInterceptor,
  ExecutionContext,
  CallHandler,
  Logger,
} from '@nestjs/common';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable()
export class LoggingInterceptor implements NestInterceptor {
  private readonly logger = new Logger('HTTP');

  intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
    // ExecutionContext gives you access to the request before the handler runs
    const request = context.switchToHttp().getRequest();
    const { method, url } = request;
    const start = Date.now();

    // next.handle() executes the controller method and returns an Observable
    // .pipe(tap(...)) runs the function after the Observable completes (after the handler returns)
    return next.handle().pipe(
      tap(() => {
        const duration = Date.now() - start;
        this.logger.log(`${method} ${url} - ${duration}ms`);
        // Output: [HTTP] GET /tasks - 3ms
      }),
    );
  }
}
```

Register in `main.ts`:

```typescript
import { LoggingInterceptor } from './common/interceptors/logging.interceptor';

app.useGlobalInterceptors(new LoggingInterceptor());
```

Every request now logs its method, path, and duration. You'll use this in Phase 7 to
verify that caching is working (the second request should be faster and the DB call
should not appear in the logs).

---

## What `main.ts` Looks Like Now

After all three additions, your `main.ts` should be:

```typescript
import { NestFactory } from '@nestjs/core';
import { ValidationPipe } from '@nestjs/common';
import { AppModule } from './app.module';
import { AllExceptionsFilter } from './common/filters/all-exceptions.filter';
import { LoggingInterceptor } from './common/interceptors/logging.interceptor';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  // 1. Validate all incoming request bodies against DTO classes
  app.useGlobalPipes(new ValidationPipe({
    whitelist: true,
    forbidNonWhitelisted: true,
    transform: true,
  }));

  // 2. Catch all errors and return a consistent JSON shape
  app.useGlobalFilters(new AllExceptionsFilter());

  // 3. Log every request's method, path, and duration
  app.useGlobalInterceptors(new LoggingInterceptor());

  await app.listen(3000);
}
bootstrap();
```

---

## Checkpoint ✓

Run the app and test:

**Bash / macOS / Git Bash:**
```bash
# Test 1: empty body → 400
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -d '{}'

# Test 2: extra field → 400
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"test","description":"ok","unknown":"field"}'

# Test 3: valid → 201
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy milk","description":"oat"}'
```

**Windows PowerShell:**
```powershell
# Test 1: empty body → 400
try {
  Invoke-RestMethod -Method Post "http://localhost:3000/tasks" `
    -ContentType "application/json" -Body '{}'
} catch { $_.ErrorDetails.Message | ConvertFrom-Json }

# Test 2: extra field → 400
try {
  Invoke-RestMethod -Method Post "http://localhost:3000/tasks" `
    -ContentType "application/json" `
    -Body '{"title":"test","description":"ok","unknown":"field"}'
} catch { $_.ErrorDetails.Message | ConvertFrom-Json }

# Test 3: valid → 201
Invoke-RestMethod -Method Post "http://localhost:3000/tasks" `
  -ContentType "application/json" `
  -Body '{"title":"Buy milk","description":"oat"}'
```

> **Test 4:** Check your terminal after any of the above — you should see `[HTTP] POST /tasks - Xms`

- [ ] Invalid POST body returns `{ statusCode: 400, message: [...], timestamp, path }`
- [ ] Extra fields are rejected, not silently accepted
- [ ] Terminal shows `[HTTP] METHOD /path - Xms` for every request
- [ ] You can draw the 7-layer pipeline from memory and explain each layer's job

## Optional Reading
- NestJS Pipes: https://docs.nestjs.com/pipes
- NestJS Guards: https://docs.nestjs.com/guards
- NestJS Interceptors: https://docs.nestjs.com/interceptors
- NestJS Exception Filters: https://docs.nestjs.com/exception-filters
- class-validator decorators list: https://github.com/typestack/class-validator#decorators
