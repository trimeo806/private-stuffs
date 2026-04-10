---
phase: 7
title: 'Advanced Patterns — Config Validation, Custom Decorators, Caching'
effort: 10h
depends: [6]
weeks: '10-11'
---

# Phase 7: Advanced Patterns — Config Validation, Custom Decorators, Caching

## Context Links
- [Plan Overview](./plan.md)
- [Phase 6](./phase-6-testing.md)

## Agent & Skills
- **Agent**: `backend-architect`
- **Skills**: `nestjs-pro`, `typescript-pro`
- **Handoffs**: After completion → `code-reviewer` → Phase 8

---

## What You Build This Phase

Three improvements to the existing app that don't change any routes — just make the
internals smarter:

1. App **crashes at startup** if `JWT_SECRET` is missing (instead of crashing on first login)
2. Controllers use `@CurrentUser()` instead of `@Request() req` — typed and clean
3. `GET /tasks` is served from Redis on repeated calls — faster and the DB isn't hit

---

## Concept: Why Validate Config at Startup?

Consider this scenario:
- You deploy your app to production
- You forgot to set `JWT_SECRET` in the production environment
- App starts successfully — no errors
- First user tries to log in → **crash** or silent failure
- Users are affected before you notice

Config validation catches this at startup:
- You forgot `JWT_SECRET` → app refuses to start → you catch it during deployment, not in production

This is the "fail fast" principle: detect problems as early as possible.

---

## Concept: What Is a Custom Parameter Decorator?

In Phase 4 you wrote:
```typescript
@Get()
findAll(@Request() req) {
  return this.tasksService.findAll(req.user.id);
  // Problems:
  // - req.user is typed as 'any' — no autocomplete, no type safety
  // - You must remember that req.user has .id, .email, .role — it's implicit
  // - If someone removes JwtAuthGuard accidentally, this crashes with 'undefined'
}
```

A custom parameter decorator extracts what you need and gives it a proper type:

```typescript
@Get()
findAll(@CurrentUser() user: JwtPayload) {
  return this.tasksService.findAll(user.id);
  // Benefits:
  // - 'user' is fully typed — autocomplete works for user.id, user.email, user.role
  // - Intention is clear — this route needs the current user
  // - Reusable across all controllers
}
```

`createParamDecorator` is NestJS's way to create these:

```typescript
// createParamDecorator takes a factory function that receives:
// - data: the argument passed to the decorator (@CurrentUser('email') → data = 'email')
// - ctx: the ExecutionContext (gives access to request, response, etc.)
const CurrentUser = createParamDecorator(
  (data: unknown, ctx: ExecutionContext) => {
    const request = ctx.switchToHttp().getRequest();
    return request.user;  // return what you want injected as the parameter
  }
);
```

---

## Concept: What Is Caching?

**Caching** stores the result of an expensive operation so you don't have to repeat it.

Without caching:
```
GET /tasks  →  DB query  →  wait 50ms  →  response
GET /tasks  →  DB query  →  wait 50ms  →  response   ← same query, same data
GET /tasks  →  DB query  →  wait 50ms  →  response
```

With caching:
```
GET /tasks  →  DB query  →  wait 50ms  →  cache result  →  response
GET /tasks  →  cache hit  →  wait 1ms   →  response   ← no DB query
GET /tasks  →  cache hit  →  wait 1ms   →  response
```

**What is Redis?**

Redis is an in-memory data store. Unlike your PostgreSQL database (data on disk),
Redis keeps everything in memory — which is why reads are ~1ms instead of ~50ms.

Redis is commonly used for:
- Caching (this phase)
- Job queues (Phase 8 — BullMQ uses Redis)
- Session storage
- Rate limiting

**Cache invalidation** — the tricky part:

When you update or create a task, the cached list of tasks is now stale (out of date).
You need to **delete the cache** so the next request fetches fresh data from the DB.

```
POST /tasks (create a new task)
  → create in DB
  → delete 'tasks' from cache  ← cache invalidation
  → next GET /tasks hits DB and caches the new list
```

---

## Step 1: Config Validation with Joi (30 min)

Install: `npm i joi`

Update `app.module.ts`:

```typescript
import { ConfigModule } from '@nestjs/config';
import * as Joi from 'joi';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      validationSchema: Joi.object({
        // Joi.string().required() means: must exist and be a non-empty string
        // If it's missing, Joi throws a validation error BEFORE the app finishes starting
        PORT: Joi.number().default(3000),
        JWT_SECRET: Joi.string().required(),
        JWT_EXPIRES_IN: Joi.string().default('15m'),
        DATABASE_URL: Joi.string().required(),
        REDIS_URL: Joi.string().default('redis://localhost:6379'),
      }),
      validationOptions: {
        // abortEarly: false → show ALL missing vars, not just the first one
        abortEarly: false,
      },
    }),
    // ... rest of imports
  ],
})
```

**Test it:**

**Bash / macOS / Git Bash:**
```bash
mv .env .env.bak
npm run start:dev
# Expected: app refuses to start with "Config validation error: JWT_SECRET is required"
mv .env.bak .env
```

**Windows PowerShell:**
```powershell
Rename-Item .env .env.bak
npm run start:dev
# Expected: app refuses to start with "Config validation error: JWT_SECRET is required"
Rename-Item .env.bak .env
```

---

## Step 2: @CurrentUser() Decorator (30 min)

Create `src/auth/decorators/current-user.decorator.ts`:

```typescript
import { createParamDecorator, ExecutionContext } from '@nestjs/common';

// Define the shape of what JwtStrategy.validate() returns
// This is what req.user looks like after a successful JWT check
export interface JwtPayload {
  id: string;
  email: string;
  role: string;
}

export const CurrentUser = createParamDecorator(
  (_data: unknown, ctx: ExecutionContext): JwtPayload => {
    const request = ctx.switchToHttp().getRequest();
    return request.user as JwtPayload;
    // ↑ request.user was set by JwtStrategy.validate() in Phase 4
    //   We cast it to JwtPayload so TypeScript knows the shape
  },
);
```

Now update every controller method that accesses `req.user`:

```typescript
// tasks.controller.ts — Before:
@Get()
findAll(@Request() req) {
  return this.tasksService.findAll(req.user.id);  // req.user is 'any'
}

// After:
@Get()
findAll(@CurrentUser() user: JwtPayload) {
  return this.tasksService.findAll(user.id);  // fully typed, autocomplete works
}

// Full example with all methods updated:
@Controller('tasks')
@UseGuards(JwtAuthGuard)
export class TasksController {
  constructor(private tasksService: TasksService) {}

  @Get()
  findAll(@CurrentUser() user: JwtPayload) {
    return this.tasksService.findAll(user.id);
  }

  @Get(':id')
  findById(@Param('id') id: string, @CurrentUser() user: JwtPayload) {
    return this.tasksService.findById(id, user.id);
  }

  @Post()
  create(@Body() dto: CreateTaskDto, @CurrentUser() user: JwtPayload) {
    return this.tasksService.create(user.id, dto.title, dto.description);
  }

  @Patch(':id/status')
  updateStatus(
    @Param('id') id: string,
    @Body() dto: UpdateTaskStatusDto,
    @CurrentUser() user: JwtPayload,
  ) {
    return this.tasksService.updateStatus(id, user.id, dto.status);
  }

  @Delete(':id')
  @HttpCode(204)
  @UseGuards(RolesGuard)
  @Roles('ADMIN')
  delete(@Param('id') id: string, @CurrentUser() user: JwtPayload) {
    return this.tasksService.delete(id, user.id);
  }
}
```

---

## Step 3: Add Redis to Docker (5 min)

Update `docker-compose.yml` to add Redis:

```yaml
services:
  # ... existing db service ...

  redis:
    image: redis:7-alpine
    ports:
      - '6379:6379'
```

```bash
docker-compose up -d
```

Add to `.env`:
```
REDIS_URL=redis://localhost:6379
```

---

## Step 4: Redis Caching on GET /tasks (1h)

Install: `npm i @nestjs/cache-manager cache-manager cache-manager-ioredis-yet ioredis`

Register `CacheModule` in `AppModule`:

```typescript
import { CacheModule } from '@nestjs/cache-manager';
import { redisStore } from 'cache-manager-ioredis-yet';

CacheModule.registerAsync({
  isGlobal: true,
  // ↑ Make the cache available in all modules without importing CacheModule each time

  imports: [ConfigModule],
  useFactory: async (config: ConfigService) => ({
    store: await redisStore({
      socket: {
        host: 'localhost',
        port: 6379,
      },
    }),
    ttl: 30 * 1000,  // 30 seconds in milliseconds
    // ↑ Cached data expires after 30 seconds
    //   After expiry, the next request hits the DB and re-populates the cache
  }),
  inject: [ConfigService],
}),
```

Add caching to the `GET /tasks` endpoint:

```typescript
import { UseInterceptors } from '@nestjs/common';
import { CacheInterceptor, CacheKey, CacheTTL } from '@nestjs/cache-manager';

@UseInterceptors(CacheInterceptor)
// ↑ CacheInterceptor automatically caches the response from this handler
//   On first call: runs handler, caches result, returns result
//   On subsequent calls within TTL: returns cached result, handler DOES NOT run

@CacheKey('tasks-list')
// ↑ The key used in Redis to store this response
//   Every route uses its URL as the key by default, but we set it explicitly here

@CacheTTL(30000)
// ↑ How long to keep this in cache (30 seconds in milliseconds)
//   After 30 seconds, the next request will hit the DB again

@Get()
findAll(@CurrentUser() user: JwtPayload) {
  console.log('DB hit — fetching tasks from database');
  // ↑ Add this console.log temporarily. If caching works,
  //   you should only see this message ONCE for multiple rapid requests
  return this.tasksService.findAll(user.id);
}
```

**Cache invalidation — clear cache on mutations:**

```typescript
import { Inject } from '@nestjs/common';
import { CACHE_MANAGER } from '@nestjs/cache-manager';
import { Cache } from 'cache-manager';

@Controller('tasks')
@UseGuards(JwtAuthGuard)
export class TasksController {
  constructor(
    private tasksService: TasksService,
    @Inject(CACHE_MANAGER) private cacheManager: Cache,
    // ↑ CACHE_MANAGER is the injection token for the cache instance
    //   @Inject() is used when the token isn't a class (it's a string/Symbol)
  ) {}

  @Post()
  async create(@Body() dto: CreateTaskDto, @CurrentUser() user: JwtPayload) {
    const task = await this.tasksService.create(user.id, dto.title, dto.description);
    await this.cacheManager.del('tasks-list');
    // ↑ The task list changed — delete the cache so the next GET hits the DB
    return task;
  }

  @Patch(':id/status')
  async updateStatus(
    @Param('id') id: string,
    @Body() dto: UpdateTaskStatusDto,
    @CurrentUser() user: JwtPayload,
  ) {
    const task = await this.tasksService.updateStatus(id, user.id, dto.status);
    await this.cacheManager.del('tasks-list');  // invalidate again
    return task;
  }
}
```

**Verify caching works:**

**Bash / macOS / Git Bash:**
```bash
TOKEN="your-jwt-token"

# Call twice in quick succession
curl http://localhost:3000/tasks -H "Authorization: Bearer $TOKEN"
curl http://localhost:3000/tasks -H "Authorization: Bearer $TOKEN"

# Terminal should show "DB hit — fetching tasks from database" only ONCE
```

**Windows PowerShell:**
```powershell
$TOKEN = "your-jwt-token"

# Call twice — watch the terminal for DB hit logs
Invoke-RestMethod "http://localhost:3000/tasks" -Headers @{ Authorization = "Bearer $TOKEN" }
Invoke-RestMethod "http://localhost:3000/tasks" -Headers @{ Authorization = "Bearer $TOKEN" }

# Terminal should log "DB hit" only once — second call is a cache hit
```

---

## Step 5: Health Endpoint (20 min)

Install: `npm i @nestjs/terminus`

Create `src/health/health.module.ts` and `health.controller.ts`:

```typescript
// health.controller.ts
import { Controller, Get } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Controller('health')
export class HealthController {
  constructor(private prisma: PrismaService) {}

  @Get()
  async check() {
    // Run a trivial DB query to verify the connection is alive
    await this.prisma.$queryRaw`SELECT 1`;
    // ↑ If this throws, the DB is down → the exception filter returns 500

    return {
      status: 'ok',
      timestamp: new Date().toISOString(),
      services: {
        database: 'up',
      },
    };
  }
}
```

This endpoint is used by:
- **Docker** health checks: `HEALTHCHECK CMD curl --fail http://localhost:3000/health`
- **Kubernetes** liveness probes: if `/health` returns non-200, restart the pod
- **Monitoring tools**: ping every 30 seconds to alert on downtime

---

## Checkpoint ✓

**Bash / macOS / Git Bash:**
```bash
# Test 1: Remove JWT_SECRET, start app — should refuse
mv .env .env.bak && npm run start:dev
mv .env.bak .env   # restore

# Test 2: Restart
npm run start:dev
TOKEN="your-jwt-token"

# Test 3: GET /tasks twice — only one "DB hit" log in terminal
curl http://localhost:3000/tasks -H "Authorization: Bearer $TOKEN"
curl http://localhost:3000/tasks -H "Authorization: Bearer $TOKEN"

# Test 4: Health check
curl http://localhost:3000/health
```

**Windows PowerShell:**
```powershell
# Test 1: Rename .env, start app — should refuse
Rename-Item .env .env.bak
npm run start:dev
# Ctrl+C to stop, then:
Rename-Item .env.bak .env

# Test 2: Restart and login
npm run start:dev
$res = Invoke-RestMethod -Method Post "http://localhost:3000/auth/login" `
  -ContentType "application/json" `
  -Body '{"email":"admin@test.com","password":"admin123"}'
$TOKEN = $res.accessToken

# Test 3: GET /tasks twice — watch terminal, "DB hit" should appear only once
Invoke-RestMethod "http://localhost:3000/tasks" -Headers @{ Authorization = "Bearer $TOKEN" }
Invoke-RestMethod "http://localhost:3000/tasks" -Headers @{ Authorization = "Bearer $TOKEN" }

# Test 4: Health check
Invoke-RestMethod "http://localhost:3000/health"
```

- [ ] Removing `JWT_SECRET` from `.env` prevents the app from starting
- [ ] `@CurrentUser()` is used in all controller methods — no more `@Request() req`
- [ ] Second `GET /tasks` call skips the DB (no "DB hit" log printed)
- [ ] `GET /health` returns 200
- [ ] You can explain what cache invalidation is and why it's needed

## Optional Reading
- NestJS Configuration: https://docs.nestjs.com/techniques/configuration
- NestJS Caching: https://docs.nestjs.com/techniques/caching
- NestJS Custom Decorators: https://docs.nestjs.com/custom-decorators
- NestJS Terminus (health): https://docs.nestjs.com/recipes/terminus
- Redis in 100 seconds: https://www.youtube.com/watch?v=G1rOthIU-uo
