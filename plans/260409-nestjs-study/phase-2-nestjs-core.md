---
phase: 2
title: 'NestJS Core — Modules, DI, Controllers, Services'
effort: 14h
depends: [1]
weeks: '2-3'
---

# Phase 2: NestJS Core — Modules, DI, Controllers, Services

## Context Links
- [Plan Overview](./plan.md)
- [Phase 1](./phase-1-ts-foundations.md)

## Agent & Skills
- **Agent**: `researcher`
- **Skills**: `nestjs-pro`, `typescript-pro`
- **Handoffs**: After completion → `code-reviewer` → Phase 3

---

## What You Build This Phase

Add a `TasksModule` to the project. By the end you'll have a full in-memory CRUD API:

```
POST   /tasks         → create a task
GET    /tasks         → list all tasks
GET    /tasks/:id     → get one task (or 404)
PATCH  /tasks/:id/status → update status
DELETE /tasks/:id     → delete a task
```

---

## Concept: What Is a Module?

A **module** in NestJS is a class decorated with `@Module()`. It's a way to group related code —
controllers and services that belong together — into a single unit.

Think of it like a **folder with a manifest**. The manifest (the `@Module()` decorator) says:
- What this module provides to the outside world (`exports`)
- What it needs from other modules (`imports`)
- Which controllers live here (`controllers`)
- Which services/providers live here (`providers`)

```typescript
@Module({
  imports: [TypeOrmModule],   // use TypeORM features in this module
  controllers: [TasksController],  // HTTP layer
  providers: [TasksService],  // business logic layer
  exports: [TasksService],    // make TasksService available to other modules
})
export class TasksModule {}
```

**Why modules exist:** In a real app you'll have many features — tasks, users, auth, notifications, etc.
Modules let you keep each feature self-contained. You can even move a module to a different app.

**The module tree:** `AppModule` is the root. Every other module is imported into `AppModule`
(or into another module that's imported into `AppModule`). NestJS builds a dependency graph
from this tree when the app starts.

```
AppModule
├── TasksModule
│   ├── TasksController
│   └── TasksService
└── UsersModule
    ├── UsersController
    └── UsersService
```

---

## Concept: What Is Dependency Injection (DI)?

Dependency Injection means: **instead of creating objects yourself, you ask for them and they
are given to you.**

Without DI (tight coupling — hard to test, hard to change):
```typescript
class TasksController {
  // You create the service yourself. Problems:
  // 1. Can't swap it for a mock in tests
  // 2. If TasksService needs its own dependencies, you manage those too
  // 3. Two controllers can't share the same instance
  private service = new TasksService();
}
```

With DI (loose coupling — testable, flexible):
```typescript
class TasksController {
  // You declare what you need. NestJS creates and manages it.
  // Benefits:
  // 1. In tests, inject a mock
  // 2. NestJS handles TasksService's own dependencies
  // 3. By default, the same instance is shared (Singleton)
  constructor(private service: TasksService) {}
}
```

**How NestJS wires this up:**
1. You mark `TasksService` with `@Injectable()` — "I can be injected"
2. You add it to `providers: [TasksService]` in the module — "register it in this module's DI container"
3. You declare it in the constructor — NestJS reads the TypeScript type (`TasksService`) thanks to `emitDecoratorMetadata` and injects the right object

---

## Concept: What Is a Provider?

A **provider** is any class that NestJS manages via DI. Services are the most common type,
but you can also have:

```typescript
// Class provider (most common)
providers: [TasksService]
// Equivalent to:
providers: [{ provide: TasksService, useClass: TasksService }]

// Value provider (for config, constants)
providers: [{ provide: 'API_KEY', useValue: 'abc123' }]

// Factory provider (when creation needs logic)
providers: [{
  provide: 'DB_CONNECTION',
  useFactory: (config: ConfigService) => createConnection(config.get('DB_URL')),
  inject: [ConfigService],
}]
```

For now, you only need class providers. The others become useful in Phase 7.

---

## Concept: What Is a Controller?

A **controller** is responsible for handling incoming HTTP requests and sending responses.
It's the front door of your feature.

A controller should be **thin** — it just maps HTTP to service calls. No business logic here.

```typescript
@Controller('tasks')
// ↑ All routes in this class start with /tasks

export class TasksController {
  constructor(private tasksService: TasksService) {}
  // ↑ DI: NestJS injects TasksService automatically

  @Get()
  // ↑ Handles: GET /tasks
  findAll() {
    return this.tasksService.findAll(); // delegate to service
  }

  @Get(':id')
  // ↑ Handles: GET /tasks/:id
  // The ':id' is a route parameter (like Express)
  findById(@Param('id') id: string) {
    // @Param('id') extracts 'id' from the URL: GET /tasks/abc123 → id = 'abc123'
    return this.tasksService.findById(id);
  }

  @Post()
  // ↑ Handles: POST /tasks
  create(@Body() body: { title: string; description: string }) {
    // @Body() extracts the JSON body from the request
    return this.tasksService.create(body.title, body.description);
  }

  @Patch(':id/status')
  // ↑ Handles: PATCH /tasks/:id/status
  updateStatus(@Param('id') id: string, @Body('status') status: string) {
    // @Body('status') extracts just the 'status' field from the body
    return this.tasksService.updateStatus(id, status as TaskStatus);
  }

  @Delete(':id')
  // ↑ Handles: DELETE /tasks/:id
  @HttpCode(204)
  // ↑ Normally DELETE returns 200, but 204 (No Content) is more correct
  delete(@Param('id') id: string) {
    this.tasksService.delete(id);
    // No return value needed for 204
  }
}
```

**Parameter decorators explained:**

| Decorator | Extracts | Example |
|-----------|---------|---------|
| `@Param('id')` | URL parameter | `GET /tasks/123` → `id = '123'` |
| `@Query('status')` | Query string | `GET /tasks?status=DONE` → `'DONE'` |
| `@Body()` | Entire request body | `POST /tasks` with JSON body |
| `@Body('title')` | One field from body | Just the `title` field |
| `@Headers('authorization')` | One request header | The `Authorization` header |

---

## Concept: What Is a Service?

A **service** is where your **business logic lives**. It doesn't know about HTTP — it just does work.

Why separate logic from controllers?
1. **Testability** — you can test the service with fake data without making HTTP calls
2. **Reusability** — multiple controllers (or other services) can use the same service
3. **Separation of concerns** — HTTP concerns live in controllers, logic lives in services

```typescript
@Injectable()
// ↑ Required for DI. Without this, NestJS won't manage this class.
export class TasksService {
  // In-memory store for now — replaced with Prisma in Phase 5
  private tasks: Task[] = [];

  findAll(): Task[] {
    return this.tasks;
  }

  findById(id: string): Task {
    const task = this.tasks.find(t => t.id === id);
    if (!task) {
      // NestJS catches this and returns a 404 JSON response automatically
      throw new NotFoundException(`Task with id "${id}" not found`);
    }
    return task;
  }
}
```

**`NotFoundException` — how NestJS handles it:**

When you throw `new NotFoundException(...)`, NestJS's built-in exception handling catches it
and responds with:
```json
{
  "statusCode": 404,
  "message": "Task with id \"abc\" not found",
  "error": "Not Found"
}
```

You don't need to catch it or format it yourself. NestJS handles this for all its built-in
exceptions: `BadRequestException` (400), `UnauthorizedException` (401), `ForbiddenException` (403),
`NotFoundException` (404), `ConflictException` (409), `InternalServerErrorException` (500).

---

## Step 1: Define the Task Model (20 min)

Create `src/tasks/task.model.ts`:

```typescript
// An enum is a set of named constants.
// Instead of checking if status === 'todo', you check status === TaskStatus.TODO
// This prevents typos and gives you autocomplete.
export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE',
}

// An interface describes the shape of an object.
// NestJS will use Prisma-generated types in Phase 5, but for now this works.
export interface Task {
  id: string;
  title: string;
  description: string;
  status: TaskStatus;
}
```

---

## Step 2: Generate the Feature Module (20 min)

```bash
# The Nest CLI generates boilerplate files for you
nest g module tasks         # creates src/tasks/tasks.module.ts
nest g service tasks --no-spec   # creates src/tasks/tasks.service.ts
nest g controller tasks --no-spec # creates src/tasks/tasks.controller.ts
```

`--no-spec` skips the test file for now (you'll write tests properly in Phase 6).

Open `src/app.module.ts` — `TasksModule` was auto-added to `imports: [TasksModule]`.
The CLI did that for you.

---

## Step 3: Build the Service (45 min)

Install uuid for generating IDs: `npm i uuid && npm i -D @types/uuid`

Replace the contents of `src/tasks/tasks.service.ts`:

```typescript
import { Injectable, NotFoundException } from '@nestjs/common';
import { v4 as uuid } from 'uuid';  // generates unique IDs like 'a1b2c3d4-...'
import { Task, TaskStatus } from './task.model';

@Injectable()
export class TasksService {
  // The in-memory "database" — just a JavaScript array
  // This is lost every time the server restarts (fixed in Phase 5 with Prisma)
  private tasks: Task[] = [];

  findAll(): Task[] {
    return this.tasks;
  }

  findById(id: string): Task {
    const task = this.tasks.find(t => t.id === id);
    if (!task) {
      throw new NotFoundException(`Task "${id}" not found`);
      // NestJS catches this and returns: { statusCode: 404, message: "Task ... not found" }
    }
    return task;
  }

  create(title: string, description: string): Task {
    const task: Task = {
      id: uuid(),          // e.g. 'f47ac10b-58cc-4372-a567-0e02b2c3d479'
      title,
      description,
      status: TaskStatus.TODO,  // all new tasks start as TODO
    };
    this.tasks.push(task);
    return task;
  }

  updateStatus(id: string, status: TaskStatus): Task {
    const task = this.findById(id);  // reuse findById — throws 404 if not found
    task.status = status;            // mutate the object in the array
    return task;
  }

  delete(id: string): void {
    this.findById(id);  // throws 404 if not found — don't delete what doesn't exist
    this.tasks = this.tasks.filter(t => t.id !== id);
  }
}
```

---

## Step 4: Build the Controller (45 min)

Replace the contents of `src/tasks/tasks.controller.ts`:

```typescript
import { Controller, Get, Post, Patch, Delete, Param, Body, HttpCode } from '@nestjs/common';
import { TasksService } from './tasks.service';
import { TaskStatus } from './task.model';

@Controller('tasks')
// ↑ All routes in this controller are prefixed with /tasks
export class TasksController {
  constructor(private tasksService: TasksService) {}
  // ↑ NestJS sees 'TasksService' as the type, looks it up in the DI container,
  //   and injects the singleton instance. You never call 'new TasksService()'.

  @Get()
  // Maps to: GET /tasks
  findAll() {
    return this.tasksService.findAll();
    // NestJS automatically serializes the return value to JSON
    // The HTTP status is 200 by default for GET
  }

  @Get(':id')
  // Maps to: GET /tasks/:id  (e.g. GET /tasks/abc-123)
  findById(@Param('id') id: string) {
    // @Param('id') reads the ':id' segment from the URL
    return this.tasksService.findById(id);
    // If the task doesn't exist, TasksService throws NotFoundException → 404
  }

  @Post()
  // Maps to: POST /tasks
  // Default HTTP status for POST is 201 (Created)
  create(@Body() body: { title: string; description: string }) {
    // @Body() parses the JSON request body into a JavaScript object
    // In Phase 3 we'll replace this plain object with a proper DTO class
    return this.tasksService.create(body.title, body.description);
  }

  @Patch(':id/status')
  // Maps to: PATCH /tasks/:id/status
  // PATCH = partial update (only changing the status, not the whole task)
  updateStatus(
    @Param('id') id: string,
    @Body('status') status: TaskStatus,
    // @Body('status') extracts just the 'status' field from the JSON body
  ) {
    return this.tasksService.updateStatus(id, status);
  }

  @Delete(':id')
  // Maps to: DELETE /tasks/:id
  @HttpCode(204)
  // By default DELETE returns 200, but 204 (No Content) is semantically correct:
  // "I did it, there's nothing to return"
  delete(@Param('id') id: string) {
    this.tasksService.delete(id);
    // No return value — HTTP 204 means no body in the response
  }
}
```

---

## Step 5: Wire the Module (10 min)

Open `src/tasks/tasks.module.ts` — the CLI should have already generated this:

```typescript
import { Module } from '@nestjs/common';
import { TasksController } from './tasks.controller';
import { TasksService } from './tasks.service';

@Module({
  controllers: [TasksController],
  // ↑ Registers TasksController. NestJS will handle routes declared in it.

  providers: [TasksService],
  // ↑ Registers TasksService in this module's DI container.
  //   Without this, injecting TasksService anywhere in this module will fail.
})
export class TasksModule {}
```

---

## Step 6: Run and Test (30 min)

```bash
npm run start:dev
```

Test every endpoint:

**Bash / macOS / Git Bash:**
```bash
# Create a task (expect 201)
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn NestJS","description":"Phase 2 complete"}'

# List all tasks
curl http://localhost:3000/tasks

# Get one task (replace the-id with the id from the create response)
curl http://localhost:3000/tasks/the-id

# Get non-existent task (expect 404)
curl http://localhost:3000/tasks/fake-id

# Update status
curl -X PATCH http://localhost:3000/tasks/the-id/status \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_PROGRESS"}'

# Delete (expect 204 empty response)
curl -X DELETE http://localhost:3000/tasks/the-id
```

**Windows PowerShell:**
```powershell
# Create a task
$task = Invoke-RestMethod -Method Post "http://localhost:3000/tasks" `
  -ContentType "application/json" `
  -Body '{"title":"Learn NestJS","description":"Phase 2 complete"}'
$task   # prints the created task; note $task.id for use below

# List all tasks
Invoke-RestMethod "http://localhost:3000/tasks"

# Get one task
Invoke-RestMethod "http://localhost:3000/tasks/$($task.id)"

# Get non-existent task (expect 404 — PowerShell throws on 4xx by default)
try { Invoke-RestMethod "http://localhost:3000/tasks/fake-id" } catch { $_.Exception.Response.StatusCode }

# Update status
Invoke-RestMethod -Method Patch "http://localhost:3000/tasks/$($task.id)/status" `
  -ContentType "application/json" `
  -Body '{"status":"IN_PROGRESS"}'

# Delete (expect 204)
Invoke-RestMethod -Method Delete "http://localhost:3000/tasks/$($task.id)"
```

---

## Exploration: Break the DI Container (15 min)

This will cement your understanding of what `providers` does.

In `src/tasks/tasks.module.ts`, remove `TasksService` from `providers`:

```typescript
@Module({
  controllers: [TasksController],
  providers: [],  // ← remove TasksService
})
```

Restart the app. You'll see:
```
Error: Nest can't resolve dependencies of the TasksController (?).
Please make sure that the argument TasksService at index [0] is available
in the TasksModule context.
```

This error means: "You declared that TasksController needs TasksService, but I can't find it."
Put it back and the app works again.

**This is the most common NestJS beginner error.** Now you know exactly why it happens.

---

## Checkpoint ✓ — Milestone M1

Before moving to Phase 3, verify:

- [ ] `POST /tasks` returns a task with a UUID
- [ ] `GET /tasks/:id` with a fake ID returns `404 Not Found`
- [ ] You can explain the difference between `providers`, `controllers`, and `imports` in `@Module()`
- [ ] You understand why `@Injectable()` is needed on services
- [ ] You can trace a GET request from the URL all the way to the service and back

## Optional Reading
- NestJS Modules: https://docs.nestjs.com/modules
- NestJS Controllers: https://docs.nestjs.com/controllers
- NestJS Providers: https://docs.nestjs.com/providers
