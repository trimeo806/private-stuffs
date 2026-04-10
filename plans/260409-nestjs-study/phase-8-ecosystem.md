---
phase: 8
title: 'Ecosystem — Swagger, BullMQ, WebSockets, Docker'
effort: 12h
depends: [7]
weeks: '12'
---

# Phase 8: Ecosystem — Swagger, BullMQ, WebSockets, Docker

## Context Links
- [Plan Overview](./plan.md)
- [Phase 7](./phase-7-advanced.md)

## Agent & Skills
- **Agent**: `backend-architect`
- **Skills**: `nestjs-pro`, `api-designer`, `microservices-architect`
- **Handoffs**: After completion → `devops-engineer` for deployment → plan complete

---

## What You Build This Phase

Four final additions to make the Task API production-grade:

1. **Swagger** — auto-generated interactive API docs at `/api`
2. **BullMQ** — async welcome email queue when a user registers
3. **WebSockets** — real-time `task:updated` event when a task status changes
4. **Docker** — single `docker-compose up` starts the entire stack

---

## Concept: What Is Swagger / OpenAPI?

**Swagger** (now called OpenAPI) is a standard for describing REST APIs.

When you add Swagger to your NestJS app, it reads your decorators (`@Get()`, `@Body()`, `@ApiProperty()`, etc.)
and generates an interactive web page at `/api` where anyone can:
- See every endpoint, its expected inputs, and its response shape
- Try out API calls without needing Postman or curl
- Understand error responses and status codes

Without Swagger:
- Frontend developers ask "what does POST /tasks expect in the body?"
- You answer verbally or write a Word doc
- The doc gets out of date immediately

With Swagger:
- The docs are auto-generated from your code — always accurate
- The `/api` page IS the documentation

---

## Concept: What Is a Job Queue?

A **job queue** is a list of tasks to be processed asynchronously — in the background,
after you've already responded to the user.

**Without a queue:**
```
POST /auth/register
  → save user to DB               (fast, ~5ms)
  → send welcome email            (slow, ~500ms — calling external email API)
  → return { accessToken: '...' } (user waits 505ms)
```

**With a queue:**
```
POST /auth/register
  → save user to DB               (fast, ~5ms)
  → add 'welcome-email' to queue  (fast, ~2ms — just writes to Redis)
  → return { accessToken: '...' } (user waits only 7ms)

[Background worker, separately]:
  → picks up 'welcome-email' job
  → calls email API               (500ms — nobody is waiting)
  → marks job as completed
```

The user gets a response immediately. The email sends in the background.

**BullMQ** is a popular job queue library backed by Redis. NestJS has first-class support
via `@nestjs/bullmq`.

---

## Concept: What Are WebSockets?

**HTTP** is one-directional: the client asks, the server answers. Then the connection closes.
```
Client: GET /tasks
Server: [200] [list of tasks]
<connection closed>
```

**WebSockets** are persistent, two-way connections:
```
Client connects → connection stays open
Server → Client: "task:updated" event  ← server pushes without client asking
Client → Server: "subscribe" event     ← client sends messages too
```

WebSockets are used for:
- Live notifications ("You have a new message")
- Collaborative editing (Google Docs)
- Real-time dashboards
- Chat applications

In this phase: when a task's status changes (via `PATCH /tasks/:id/status`), every
connected WebSocket client immediately receives the updated task — without polling.

---

## Concept: What Is Docker?

**Docker** packages your app and all its dependencies into a portable container.
"It works on my machine" → "it works in the container, which works everywhere."

A **Dockerfile** describes how to build your app's container image:
```dockerfile
FROM node:20-alpine   ← start from a Node.js base image
WORKDIR /app          ← set working directory inside the container
COPY . .              ← copy your code in
RUN npm install       ← install dependencies
CMD ["node", "dist/main"]  ← run the app
```

**docker-compose** runs multiple containers together. Instead of:
1. Start PostgreSQL manually
2. Start Redis manually
3. Run `npm run start:dev`
4. Hope you remembered all the env vars

You run: `docker-compose up` — and everything starts together, configured correctly.

---

## Step 1: Swagger (30 min)

Install: `npm i @nestjs/swagger`

In `main.ts`, add before `app.listen()`:

```typescript
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';

// DocumentBuilder builds the metadata for your API docs
const config = new DocumentBuilder()
  .setTitle('Task API')
  .setDescription('A task management API built with NestJS')
  .setVersion('1.0')
  .addBearerAuth()
  // ↑ Adds an "Authorize" button to the Swagger UI.
  //   Click it, paste your JWT, and all subsequent requests will include it.
  .build();

// createDocument scans your decorators and builds the OpenAPI spec
const document = SwaggerModule.createDocument(app, config);

// setup serves the Swagger UI at /api
SwaggerModule.setup('api', app, document);
```

Visit `http://localhost:3000/api` in your browser — you'll see your endpoints.

> **Swagger UI replaces curl/PowerShell for testing.** Click any endpoint → "Try it out" →
> fill the body → "Execute". Use the "Authorize" button (top right) to paste your JWT once
> and it attaches to all requests automatically.

**Improve the docs by decorating your DTOs:**

```typescript
// create-task.dto.ts
import { ApiProperty } from '@nestjs/swagger';

export class CreateTaskDto {
  @ApiProperty({
    example: 'Buy groceries',
    description: 'The title of the task. Must be at least 3 characters.',
  })
  @IsString()
  @IsNotEmpty()
  @MinLength(3)
  title: string;

  @ApiProperty({
    example: 'Milk, eggs, bread',
    description: 'Optional details about the task.',
    required: false,
  })
  @IsString()
  @IsOptional()
  description?: string;
}
```

**Mark protected endpoints in the docs:**

```typescript
// tasks.controller.ts
import { ApiBearerAuth, ApiTags, ApiOperation, ApiResponse } from '@nestjs/swagger';

@ApiTags('tasks')
// ↑ Groups all routes in this controller under "tasks" in the Swagger UI
@ApiBearerAuth()
// ↑ Tells Swagger this controller requires a Bearer token
@UseGuards(JwtAuthGuard)
@Controller('tasks')
export class TasksController {

  @ApiOperation({ summary: 'Get all tasks for the current user' })
  @ApiResponse({ status: 200, description: 'Returns an array of tasks' })
  @ApiResponse({ status: 401, description: 'No or invalid JWT token' })
  @Get()
  findAll(@CurrentUser() user: JwtPayload) {
    return this.tasksService.findAll(user.id);
  }
}
```

---

## Step 2: BullMQ — Welcome Email Queue (1.5h)

Install: `npm i @nestjs/bullmq bullmq`

Redis is already running from Phase 7.

**Register the queue module in AppModule:**

```typescript
import { BullModule } from '@nestjs/bullmq';

BullModule.forRoot({
  connection: {
    host: 'localhost',
    port: 6379,
  },
}),
// ↑ BullModule.forRoot sets up the Redis connection for all queues
```

**Register the specific queue in AuthModule:**

```typescript
import { BullModule } from '@nestjs/bullmq';

@Module({
  imports: [
    UsersModule,
    JwtModule.registerAsync({ ... }),
    BullModule.registerQueue({ name: 'email' }),
    // ↑ Creates a queue named 'email' in Redis
    //   Jobs added to this queue will be processed by EmailProcessor
  ],
  ...
})
export class AuthModule {}
```

**Add a job to the queue when a user registers (in AuthService):**

```typescript
import { InjectQueue } from '@nestjs/bullmq';
import { Queue } from 'bullmq';

@Injectable()
export class AuthService {
  constructor(
    private usersService: UsersService,
    private jwtService: JwtService,
    @InjectQueue('email') private emailQueue: Queue,
    // ↑ @InjectQueue('email') injects the queue named 'email'
    //   The string must match what you used in BullModule.registerQueue()
  ) {}

  async register(email: string, password: string) {
    const existing = await this.usersService.findByEmail(email);
    if (existing) throw new ConflictException('Email already in use');

    const passwordHash = await bcrypt.hash(password, 10);
    const user = await this.usersService.create(email, passwordHash);

    // Add a job to the queue — this returns immediately
    // The job will be processed asynchronously by EmailProcessor
    await this.emailQueue.add('welcome', {
      email: user.email,
      name: user.email.split('@')[0],
    });
    // ↑ 'welcome' is the job name — the processor checks this to know what to do
    // The second argument is the job's data payload

    return this.generateToken(user);
    // ↑ Returns immediately — user doesn't wait for the email to send
  }
}
```

**Create the processor that handles the jobs:**

```typescript
// src/email/email.processor.ts
import { Processor, WorkerHost } from '@nestjs/bullmq';
import { Job } from 'bullmq';
import { Logger } from '@nestjs/common';

@Processor('email')
// ↑ This class handles jobs from the 'email' queue
//   The name must match BullModule.registerQueue({ name: 'email' })
export class EmailProcessor extends WorkerHost {
  private readonly logger = new Logger(EmailProcessor.name);

  async process(job: Job) {
    // job.name is the job type ('welcome', 'password-reset', etc.)
    // job.data is the payload you passed in emailQueue.add(...)
    switch (job.name) {
      case 'welcome':
        this.logger.log(`📧 Sending welcome email to ${job.data.email}`);

        // In a real app, you'd call SendGrid, Resend, or Nodemailer here:
        // await this.emailService.send({
        //   to: job.data.email,
        //   subject: 'Welcome!',
        //   html: `<h1>Welcome, ${job.data.name}!</h1>`
        // });

        await new Promise(r => setTimeout(r, 100)); // simulate 100ms email send time
        this.logger.log(`✅ Welcome email sent to ${job.data.email}`);
        break;

      default:
        this.logger.warn(`Unknown job: ${job.name}`);
    }
  }
}
```

Register `EmailProcessor` as a provider in a new `EmailModule`:

```typescript
// src/email/email.module.ts
import { Module } from '@nestjs/common';
import { BullModule } from '@nestjs/bullmq';
import { EmailProcessor } from './email.processor';

@Module({
  imports: [BullModule.registerQueue({ name: 'email' })],
  providers: [EmailProcessor],
})
export class EmailModule {}
```

Import `EmailModule` in `AppModule`.

**Test it:** Register a new user and watch the terminal:
```
[EmailProcessor] 📧 Sending welcome email to newuser@test.com
[EmailProcessor] ✅ Welcome email sent to newuser@test.com
```

These logs appear **after** the HTTP response is already sent.

---

## Step 3: WebSocket Gateway (1h)

Install: `npm i @nestjs/websockets @nestjs/platform-socket.io socket.io`

Create `src/tasks/tasks.gateway.ts`:

```typescript
import { WebSocketGateway, WebSocketServer } from '@nestjs/websockets';
import { Server } from 'socket.io';

@WebSocketGateway({
  cors: { origin: '*' },
  // ↑ Allow connections from any origin (restrict in production)
})
export class TasksGateway {
  @WebSocketServer()
  server: Server;
  // ↑ @WebSocketServer() injects the Socket.io server instance
  //   You use this to emit events to connected clients

  // Called by TasksService when a task is updated
  emitTaskUpdated(task: any) {
    this.server.emit('task:updated', task);
    // ↑ 'emit' sends an event to ALL connected WebSocket clients
    //   'task:updated' is the event name — clients subscribe to this name
    //   'task' is the data sent with the event
  }
}
```

**What is `@WebSocketGateway()`?**
It's NestJS's decorator that turns a class into a WebSocket server. Under the hood,
it starts Socket.io alongside your HTTP server. Both can run on the same port.

Inject `TasksGateway` into `TasksService` and emit when status changes:

```typescript
// tasks.module.ts — add TasksGateway to providers
@Module({
  controllers: [TasksController],
  providers: [TasksService, TasksGateway],
  // ↑ TasksGateway must be in providers so it can be injected into TasksService
})
export class TasksModule {}

// tasks.service.ts
@Injectable()
export class TasksService {
  constructor(
    private prisma: PrismaService,
    private tasksGateway: TasksGateway,
    // ↑ NestJS injects the gateway just like any other provider
  ) {}

  async updateStatus(id: string, userId: string, status: TaskStatus) {
    await this.findById(id, userId);
    const updated = await this.prisma.task.update({
      where: { id },
      data: { status },
    });
    this.tasksGateway.emitTaskUpdated(updated);
    // ↑ After the DB update, broadcast the change to all WebSocket clients
    return updated;
  }
}
```

**Test it:**
Open your browser's DevTools console on any page at `localhost:3000` and run:

```javascript
// Connect to the WebSocket server
const { io } = await import('https://cdn.socket.io/4.7.4/socket.io.esm.min.js');
const socket = io('http://localhost:3000');

// Subscribe to task updates
socket.on('task:updated', (task) => {
  console.log('Task updated!', task);
});

console.log('Listening for task updates...');
```

Now in a different tab, call `PATCH /tasks/:id/status` with a valid token.
You should see the updated task appear in the DevTools console.

---

## Step 4: Docker + docker-compose (1h)

Create `Dockerfile` at the project root:

```dockerfile
# Stage 1: Build
# We use a build stage to compile TypeScript to JavaScript.
# This stage is temporary — the final image won't include TypeScript or dev dependencies.
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
# ↑ 'npm ci' is like 'npm install' but reads package-lock.json exactly (reproducible builds)
COPY . .
RUN npm run build
# ↑ Compiles TypeScript → dist/

# Stage 2: Production
# This is the actual image that runs. It's smaller because:
# - No TypeScript compiler
# - No devDependencies (jest, ts-node, etc.)
# - Only the compiled dist/ and node_modules
FROM node:20-alpine AS runner
WORKDIR /app

# Create a non-root user for security
# Running as root in production is a security risk
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only what's needed to run the app
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/prisma ./prisma
COPY package*.json ./

# Switch to the non-root user
USER appuser

EXPOSE 3000

# Run database migrations before starting the app
# 'migrate deploy' applies pending migrations (safe for production — no drops)
CMD ["sh", "-c", "npx prisma migrate deploy && node dist/main"]
```

Update `docker-compose.yml` to include the app:

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - '3000:3000'
    environment:
      # These environment variables replace your .env file inside the container
      DATABASE_URL: postgresql://postgres:postgres@db:5432/taskapi
      # ↑ Note: 'db' (not 'localhost') — inside Docker, services talk to each other
      #   by service name, not by localhost
      JWT_SECRET: change-me-in-production-use-a-real-secret
      JWT_EXPIRES_IN: 15m
      REDIS_URL: redis://redis:6379
      PORT: 3000
    depends_on:
      - db
      - redis
      # ↑ Wait for db and redis to start before starting app
      #   Note: this only waits for the container to start, not for PostgreSQL
      #   to be ready. A healthcheck would be more robust for production.
    restart: unless-stopped

  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: taskapi
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - '5432:5432'  # expose for Prisma Studio / local access
    volumes:
      - pgdata:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - '6379:6379'

volumes:
  pgdata:
```

**Test the full Docker setup:**

```bash
# Same command on all platforms (Docker CLI is cross-platform)
docker-compose up --build
```

Open a second terminal while it's running:

**Bash / macOS / Git Bash:**
```bash
curl http://localhost:3000/health

curl -X POST http://localhost:3000/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"docker@test.com","password":"test123"}'
# Works! App + DB + Redis all running in Docker
```

**Windows PowerShell:**
```powershell
Invoke-RestMethod "http://localhost:3000/health"

Invoke-RestMethod -Method Post "http://localhost:3000/auth/register" `
  -ContentType "application/json" `
  -Body '{"email":"docker@test.com","password":"test123"}'
# Works! App + DB + Redis all running in Docker
```

> You can also open `http://localhost:3000/api` in your browser and test everything from Swagger UI.

---

## Checkpoint ✓ — Milestone M5 — PLAN COMPLETE

```bash
docker-compose up --build
```

- [ ] `http://localhost:3000/api` shows Swagger UI — all endpoints documented
- [ ] `POST /auth/register` triggers a BullMQ job (terminal shows "📧 Sending welcome email")
- [ ] `PATCH /tasks/:id/status` emits `task:updated` to connected WebSocket clients
- [ ] `docker-compose up` starts app + PostgreSQL + Redis with one command
- [ ] Container runs as non-root user
- [ ] Prisma migrations run automatically on container start

---

## Final Capstone Checklist

| Feature | Phase | Done |
|---------|-------|------|
| TypeScript decorators understood | 1 | [ ] |
| Full CRUD at `/tasks` (in-memory) | 2 | [ ] |
| DTO validation + exception filter + logging | 3 | [ ] |
| JWT register/login + RBAC guard | 4 | [ ] |
| Prisma + PostgreSQL (data persists) | 5 | [ ] |
| Unit + E2E test suite | 6 | [ ] |
| Config validation at startup | 7 | [ ] |
| `@CurrentUser()` custom decorator | 7 | [ ] |
| Redis caching on GET /tasks | 7 | [ ] |
| Health endpoint | 7 | [ ] |
| Swagger docs at `/api` | 8 | [ ] |
| BullMQ welcome email queue | 8 | [ ] |
| WebSocket real-time events | 8 | [ ] |
| Docker + docker-compose | 8 | [ ] |

## Optional Reading
- NestJS Swagger: https://docs.nestjs.com/openapi/introduction
- NestJS BullMQ: https://docs.nestjs.com/techniques/queues
- NestJS WebSockets: https://docs.nestjs.com/websockets/gateways
- Socket.io docs: https://socket.io/docs/v4/
- Docker multi-stage builds: https://docs.docker.com/build/building/multi-stage/
