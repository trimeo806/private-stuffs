---
phase: 6
title: 'Testing — Unit, Integration, E2E'
effort: 14h
depends: [5]
weeks: '8-9'
---

# Phase 6: Testing — Unit, Integration, E2E

## Context Links
- [Plan Overview](./plan.md)
- [Phase 5](./phase-5-database.md)

## Agent & Skills
- **Agent**: `tester`
- **Skills**: `web-testing`, `nestjs-pro`
- **Handoffs**: After completion → `code-reviewer` → Phase 7

---

## What You Build This Phase

A test suite that covers every important part of your Task API:

```
npm run test:unit   → tests services and guards in isolation (no DB, no HTTP)
npm run test:e2e    → tests the full HTTP flow: register → login → CRUD tasks
```

---

## Concept: Why Test?

Tests are not just about catching bugs. They serve three purposes:

1. **Confidence** — change code without fear of breaking something unknown
2. **Documentation** — tests describe what the code is *supposed* to do
3. **Design feedback** — code that's hard to test is usually poorly designed (too many dependencies, too much coupling)

---

## Concept: Three Types of Tests

**Unit Test**
- Tests ONE function or class in isolation
- All dependencies are replaced with fakes (mocks)
- Very fast (no DB, no HTTP, no external services)
- Questions answered: "Does this function handle a missing task correctly?"

**Integration Test**
- Tests multiple pieces together with real dependencies (real DB)
- Slower than unit tests (involves a real PostgreSQL connection)
- Questions answered: "Does TasksService actually write to and read from the DB?"

**E2E (End-to-End) Test**
- Tests the full user journey via real HTTP requests
- Runs the entire NestJS app, sends requests with SuperTest
- Slowest but closest to real usage
- Questions answered: "Can a user register, login, create a task, and retrieve it?"

**The 70/20/10 ratio:**
```
70% unit tests      — fast, many, covers all logic branches
20% integration     — medium speed, covers DB queries
10% e2e             — slow, covers critical user flows only
```

---

## Concept: What Is Mocking?

**Mocking** means replacing a real dependency with a fake one that you control.

Why mock?
- Unit tests should be isolated — if your DB is down, unit tests should still pass
- Tests should be deterministic — no "it works on my machine" issues
- Tests should be fast — no waiting for DB connections

```typescript
// Real TasksService uses PrismaService → hits the actual DB
// In a unit test, we replace PrismaService with a mock:

const mockPrismaService = {
  task: {
    findMany: jest.fn(),     // jest.fn() creates a function you can control
    findFirst: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  },
};

// Now in tests:
mockPrismaService.task.findFirst.mockResolvedValue({ id: '1', title: 'Test' });
// ↑ "When findFirst is called, return this value"
// The real DB is never touched.
```

---

## Concept: What Is `Test.createTestingModule()`?

This is NestJS's way to create a module just for testing. It works like `AppModule` but
you can swap real providers with mocks:

```typescript
const module = await Test.createTestingModule({
  providers: [
    TasksService,
    // Instead of the real PrismaService, inject the mock:
    { provide: PrismaService, useValue: mockPrismaService },
  ],
}).compile();

const service = module.get<TasksService>(TasksService);
// ↑ Get the TasksService instance from the test module — ready to test!
```

---

## Warm-up: First Jest Test (20 min)

Before testing NestJS code, write a test for a pure function. This gets you familiar with Jest.

Create `src/utils/task-status.util.ts`:

```typescript
import { TaskStatus } from '@prisma/client';

// Returns true if a task can transition from 'current' status to 'next' status
// Rules: TODO → IN_PROGRESS → DONE (can't skip, can't go backwards)
export function isValidTransition(current: TaskStatus, next: TaskStatus): boolean {
  const transitions: Record<TaskStatus, TaskStatus[]> = {
    TODO: [TaskStatus.IN_PROGRESS],
    IN_PROGRESS: [TaskStatus.DONE],
    DONE: [],
  };
  return transitions[current].includes(next);
}
```

Create `src/utils/task-status.util.spec.ts`:

```typescript
import { isValidTransition } from './task-status.util';
import { TaskStatus } from '@prisma/client';

describe('isValidTransition', () => {
  // describe() groups related tests

  it('allows TODO → IN_PROGRESS', () => {
    // it() is one test case
    // expect(value).toBe(expected) is the assertion
    expect(isValidTransition(TaskStatus.TODO, TaskStatus.IN_PROGRESS)).toBe(true);
  });

  it('allows IN_PROGRESS → DONE', () => {
    expect(isValidTransition(TaskStatus.IN_PROGRESS, TaskStatus.DONE)).toBe(true);
  });

  it('does not allow skipping: TODO → DONE', () => {
    expect(isValidTransition(TaskStatus.TODO, TaskStatus.DONE)).toBe(false);
  });

  it('does not allow going backwards: DONE → TODO', () => {
    expect(isValidTransition(TaskStatus.DONE, TaskStatus.TODO)).toBe(false);
  });

  it('does not allow staying in same state: TODO → TODO', () => {
    expect(isValidTransition(TaskStatus.TODO, TaskStatus.TODO)).toBe(false);
  });
});
```

```bash
npm test -- --testPathPattern task-status
# All 5 tests should pass in < 1 second
```

---

## Step 1: Unit Test — TasksService (1.5h)

Create `src/tasks/tasks.service.spec.ts`:

```typescript
import { Test, TestingModule } from '@nestjs/testing';
import { TasksService } from './tasks.service';
import { PrismaService } from '../prisma/prisma.service';
import { NotFoundException } from '@nestjs/common';
import { TaskStatus } from '@prisma/client';

// ---- The mock ----
// We define what PrismaService looks like from TasksService's perspective
// jest.fn() creates a spy — you can control return values and check if it was called
const mockPrisma = {
  task: {
    findMany: jest.fn(),
    findFirst: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  },
};

// ---- Test suite ----
describe('TasksService', () => {
  let service: TasksService;

  beforeEach(async () => {
    // Set up a fresh TestingModule before EACH test
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        TasksService,
        { provide: PrismaService, useValue: mockPrisma },
        // ↑ When TasksService asks for PrismaService, give it mockPrisma instead
      ],
    }).compile();

    service = module.get<TasksService>(TasksService);

    jest.clearAllMocks();
    // ↑ Reset all mock functions between tests
    //   Without this, a mock call in test 1 would be counted in test 2
  });

  // ---- Tests for findAll() ----
  describe('findAll', () => {
    it('returns all tasks for the given userId', async () => {
      const fakeTasks = [
        { id: '1', title: 'Task 1', ownerId: 'user-1' },
        { id: '2', title: 'Task 2', ownerId: 'user-1' },
      ];
      // Set up what the mock returns when findMany is called
      mockPrisma.task.findMany.mockResolvedValue(fakeTasks);

      const result = await service.findAll('user-1');

      // 1. Check the service returned what Prisma returned
      expect(result).toEqual(fakeTasks);

      // 2. Check Prisma was called with the right arguments
      expect(mockPrisma.task.findMany).toHaveBeenCalledWith({
        where: { ownerId: 'user-1' },
        orderBy: { createdAt: 'desc' },
      });
    });
  });

  // ---- Tests for findById() ----
  describe('findById', () => {
    it('returns the task when found', async () => {
      const fakeTask = { id: 'task-1', title: 'Found it', ownerId: 'user-1' };
      mockPrisma.task.findFirst.mockResolvedValue(fakeTask);

      const result = await service.findById('task-1', 'user-1');

      expect(result).toEqual(fakeTask);
    });

    it('throws NotFoundException when task does not exist', async () => {
      // When Prisma returns null, the service should throw NotFoundException
      mockPrisma.task.findFirst.mockResolvedValue(null);

      // rejects.toThrow() tests that an async function throws
      await expect(service.findById('nope', 'user-1')).rejects.toThrow(NotFoundException);
    });

    it('throws NotFoundException when task belongs to another user', async () => {
      // findFirst returns null if the where clause doesn't match
      mockPrisma.task.findFirst.mockResolvedValue(null);

      await expect(service.findById('task-1', 'other-user')).rejects.toThrow(NotFoundException);
    });
  });

  // ---- Tests for create() ----
  describe('create', () => {
    it('creates a task with the correct data', async () => {
      const fakeCreated = { id: 'new-1', title: 'Buy milk', description: 'oat', ownerId: 'user-1' };
      mockPrisma.task.create.mockResolvedValue(fakeCreated);

      const result = await service.create('user-1', 'Buy milk', 'oat');

      // Check that Prisma was called with the right data
      expect(mockPrisma.task.create).toHaveBeenCalledWith({
        data: { title: 'Buy milk', description: 'oat', ownerId: 'user-1' },
      });

      expect(result).toEqual(fakeCreated);
    });
  });
});
```

```bash
npm test -- --testPathPattern tasks.service
```

---

## Step 2: Unit Test — RolesGuard (30 min)

Create `src/auth/guards/roles.guard.spec.ts`:

```typescript
import { RolesGuard } from './roles.guard';
import { Reflector } from '@nestjs/core';
import { ExecutionContext } from '@nestjs/common';

describe('RolesGuard', () => {
  let guard: RolesGuard;
  let reflector: Reflector;

  beforeEach(() => {
    reflector = new Reflector();
    guard = new RolesGuard(reflector);
  });

  // Helper to create a fake ExecutionContext with the given user role
  const createContext = (userRole: string, requiredRoles: string[] | undefined): ExecutionContext => ({
    getHandler: () => ({}),
    switchToHttp: () => ({
      getRequest: () => ({ user: { role: userRole } }),
    }),
    // Override reflector to return specific roles:
    // (we'll mock this in the actual test)
  } as any);

  it('allows access when no @Roles() decorator is present', () => {
    jest.spyOn(reflector, 'get').mockReturnValue(undefined);
    // ↑ jest.spyOn lets you replace a real method on an object with a mock

    const context = createContext('USER', undefined);
    expect(guard.canActivate(context)).toBe(true);
  });

  it('allows access when user has the required role', () => {
    jest.spyOn(reflector, 'get').mockReturnValue(['ADMIN']);
    const context = createContext('ADMIN', ['ADMIN']);
    expect(guard.canActivate(context)).toBe(true);
  });

  it('denies access when user lacks the required role', () => {
    jest.spyOn(reflector, 'get').mockReturnValue(['ADMIN']);
    const context = createContext('USER', ['ADMIN']);
    expect(guard.canActivate(context)).toBe(false);
  });
});
```

---

## Step 3: E2E Test — Full Auth + Tasks Flow (1.5h)

Create `test/app.e2e-spec.ts`:

```typescript
import * as request from 'supertest';
import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import { AppModule } from '../src/app.module';
import { PrismaService } from '../src/prisma/prisma.service';

describe('Task API (e2e)', () => {
  let app: INestApplication;
  let prisma: PrismaService;
  let accessToken: string;

  beforeAll(async () => {
    // Create the real NestJS app (with real DB, real services)
    const module: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = module.createNestApplication();

    // Apply the same global pipes as main.ts
    app.useGlobalPipes(new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
    }));

    await app.init();

    prisma = app.get(PrismaService);
  });

  afterEach(async () => {
    // Clean up DB between tests so they don't interfere with each other
    await prisma.task.deleteMany();
    await prisma.user.deleteMany();
  });

  afterAll(async () => {
    await app.close();  // Shut down the app after all tests
  });

  // ---- Auth tests ----
  describe('POST /auth/register', () => {
    it('returns 201 and an access token', async () => {
      const res = await request(app.getHttpServer())
        // ↑ app.getHttpServer() returns the underlying Express HTTP server
        //   request() from supertest wraps it to make test HTTP calls
        .post('/auth/register')
        .send({ email: 'test@test.com', password: 'secret123' })
        .expect(201);
        // ↑ .expect(201) asserts the HTTP status code

      expect(res.body.accessToken).toBeDefined();
      accessToken = res.body.accessToken;  // Save for later tests
    });

    it('returns 409 if email is already taken', async () => {
      await request(app.getHttpServer())
        .post('/auth/register')
        .send({ email: 'dup@test.com', password: 'secret123' });

      // Register again with same email
      await request(app.getHttpServer())
        .post('/auth/register')
        .send({ email: 'dup@test.com', password: 'other' })
        .expect(409);
    });
  });

  // ---- Auth guard tests ----
  describe('GET /tasks', () => {
    it('returns 401 without a token', () => {
      return request(app.getHttpServer())
        .get('/tasks')
        .expect(401);
    });

    it('returns 200 with a valid token', async () => {
      // Register first to get a token
      const res = await request(app.getHttpServer())
        .post('/auth/register')
        .send({ email: 'tasks@test.com', password: 'pass123' });
      const token = res.body.accessToken;

      return request(app.getHttpServer())
        .get('/tasks')
        .set('Authorization', `Bearer ${token}`)
        // ↑ .set() adds a request header
        .expect(200);
    });
  });

  // ---- Validation tests ----
  describe('POST /tasks', () => {
    beforeEach(async () => {
      // Register and login before each task test
      const res = await request(app.getHttpServer())
        .post('/auth/register')
        .send({ email: 'taskuser@test.com', password: 'pass123' });
      accessToken = res.body.accessToken;
    });

    it('returns 400 when title is missing', () => {
      return request(app.getHttpServer())
        .post('/tasks')
        .set('Authorization', `Bearer ${accessToken}`)
        .send({ description: 'no title' })
        .expect(400);
    });

    it('creates a task and returns 201', async () => {
      const res = await request(app.getHttpServer())
        .post('/tasks')
        .set('Authorization', `Bearer ${accessToken}`)
        .send({ title: 'E2E task', description: 'created in test' })
        .expect(201);

      expect(res.body.id).toBeDefined();
      expect(res.body.title).toBe('E2E task');
      expect(res.body.status).toBe('TODO');
    });
  });
});
```

---

## Step 4: Add Test Scripts to package.json

```json
"scripts": {
  "test": "jest",
  "test:watch": "jest --watch",
  "test:unit": "jest --testPathIgnorePatterns=e2e-spec",
  "test:e2e": "jest --config ./test/jest-e2e.json",
  "test:cov": "jest --coverage",
  "test:ci": "jest --forceExit --detectOpenHandles"
}
```

---

## Checkpoint ✓ — Milestone M4

```bash
npm run test:unit   # services and guards pass in < 5 seconds
npm run test:e2e    # full register → login → CRUD flow passes
```

- [ ] `TasksService.findById` tests cover both found and not-found cases
- [ ] `RolesGuard` test covers allow (correct role), deny (wrong role), and no-decorator cases
- [ ] E2E test proves `GET /tasks` returns 401 without a token
- [ ] E2E test proves `POST /tasks` with missing title returns 400
- [ ] You understand why unit tests use mocks and E2E tests use the real app
- [ ] `jest.clearAllMocks()` is in `beforeEach` — you know why (prevent test pollution)

## Optional Reading
- NestJS Testing guide: https://docs.nestjs.com/fundamentals/testing
- Jest docs: https://jestjs.io/docs/getting-started
- SuperTest: https://github.com/ladjs/supertest
- Jest mock functions: https://jestjs.io/docs/mock-functions
