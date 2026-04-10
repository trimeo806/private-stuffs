---
phase: 5
title: 'Database — Prisma + PostgreSQL'
effort: 12h
depends: [4]
weeks: '7'
---

# Phase 5: Database — Prisma + PostgreSQL

## Context Links
- [Plan Overview](./plan.md)
- [Phase 4](./phase-4-auth.md)

## Agent & Skills
- **Agent**: `backend-developer`
- **Skills**: `nestjs-pro`, `typescript-pro`, `postgres-pro`
- **Handoffs**: After completion → `code-reviewer` → Phase 6

---

## What You Build This Phase

Replace every in-memory array with a real PostgreSQL database.

The API behaves identically from the outside. The difference:
**restart the server and your data is still there.**

---

## Concept: What Is an ORM?

An **ORM (Object-Relational Mapper)** is a library that lets you talk to a SQL database
using TypeScript objects instead of writing raw SQL.

Without an ORM:
```typescript
// You write SQL manually — error-prone and no type safety
const result = await db.query('SELECT * FROM tasks WHERE id = $1', [id]);
const task = result.rows[0]; // task is typed as 'any'
```

With Prisma (an ORM):
```typescript
// TypeScript — fully typed, autocomplete, no SQL required
const task = await prisma.task.findUnique({ where: { id } });
// 'task' is typed as: Task | null — based on your schema
```

---

## Concept: What Is Prisma?

**Prisma** is a modern ORM for Node.js/TypeScript. What makes it different:

1. **Schema as single source of truth** — you define your data in `prisma/schema.prisma`
2. **Auto-generated client** — Prisma generates a type-safe client based on your schema
3. **Migrations** — Prisma tracks schema changes and generates SQL migration files
4. **Prisma Studio** — a visual browser for your database

The workflow is:
```
Edit schema.prisma → run prisma migrate dev → Prisma generates:
  - SQL migration file (what changed in the DB)
  - Updated PrismaClient types (TypeScript types matching your schema)
```

---

## Concept: What Is a Migration?

A **migration** is a versioned SQL script that changes your database schema.

When you change your `schema.prisma` (add a column, create a table, etc.), Prisma generates
a migration file:

```
prisma/migrations/
  20260409120000_init/
    migration.sql    ← actual SQL that was run
```

**Why migrations instead of just changing the DB directly?**
- Every change is tracked in git history
- Your entire team can run `npx prisma migrate deploy` to apply the same changes
- You can see exactly what changed and when
- Production deployments are reproducible

---

## Concept: What Is the Repository Pattern?

The **Repository Pattern** adds an extra layer between your service and the database.
Instead of calling Prisma directly in your service, you call a repository that wraps Prisma.

```
Controller → Service → Repository → Prisma → Database
```

**Why add the repository layer?**
- In tests, you can swap the repository with a fake (no real DB needed for unit tests)
- Query complexity is hidden from the service (the service just calls `tasksRepo.findAll()`)
- If you ever switch from Prisma to something else, you only change the repository

For a learning project, a repository layer is optional — but it's a pattern you'll see
in real codebases.

---

## Step 1: Start PostgreSQL with Docker (10 min)

If you don't have Docker: install it from docker.com.

Create `docker-compose.yml` at the project root:

```yaml
version: '3.8'
services:
  db:
    image: postgres:16-alpine
    # ↑ PostgreSQL 16, alpine = smaller image size
    environment:
      POSTGRES_DB: taskapi
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - '5432:5432'
    # ↑ Maps container port 5432 to your local port 5432
    volumes:
      - pgdata:/var/lib/postgresql/data
      # ↑ Persist the database data even if the container is removed

volumes:
  pgdata:
```

```bash
docker-compose up -d   # -d = run in background
```

Add `DATABASE_URL` to `.env`:
```
DATABASE_URL="postgresql://postgres:postgres@localhost:5432/taskapi"
```

---

## Step 2: Install and Initialize Prisma (10 min)

```bash
npm i @prisma/client
npm i -D prisma

npx prisma init
# ↑ Creates:
#   - prisma/schema.prisma  (your schema file)
#   - Updates .env with a DATABASE_URL placeholder
```

---

## Step 3: Write the Schema (30 min)

Replace `prisma/schema.prisma` contents:

```prisma
generator client {
  provider = "prisma-client-js"
  // ↑ Generate a JavaScript/TypeScript client
}

datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
  // ↑ Read the connection string from .env
}

// An enum in Prisma becomes both a DB enum type AND a TypeScript enum
enum Role {
  USER
  ADMIN
}

enum TaskStatus {
  TODO
  IN_PROGRESS
  DONE
}

model User {
  // 'model' is Prisma's word for a database table + its TypeScript type
  id        String   @id @default(cuid())
  // ↑ Primary key. cuid() generates collision-resistant IDs like 'clk5f...'
  //   You could also use @default(uuid()) for UUIDs

  email     String   @unique
  // ↑ @unique creates a UNIQUE constraint in PostgreSQL
  //   Inserting a duplicate email → Prisma error P2002

  password  String
  // ↑ No @unique — multiple users could theoretically have same password (unlikely but allowed)

  role      Role     @default(USER)
  // ↑ @default sets the value when not provided

  tasks     Task[]
  // ↑ One-to-many relation: one User has many Tasks
  //   This is NOT a DB column — it's a Prisma virtual field for queries with 'include'

  createdAt DateTime @default(now())
  // ↑ Automatically set to current timestamp on creation
}

model Task {
  id          String     @id @default(cuid())
  title       String
  description String     @default('')
  status      TaskStatus @default(TODO)

  owner       User   @relation(fields: [ownerId], references: [id])
  // ↑ This defines the foreign key relationship:
  //   - 'ownerId' is the actual column in the DB (a foreign key pointing to User.id)
  //   - 'owner' is a virtual field for include queries: prisma.task.findMany({ include: { owner: true } })

  ownerId     String
  // ↑ The actual foreign key column in the 'tasks' table

  createdAt   DateTime @default(now())
  updatedAt   DateTime @updatedAt
  // ↑ @updatedAt automatically updates this field on every record update
}
```

**Understanding the User ↔ Task relation:**
```
DB structure:
users table:            tasks table:
  id (PK)               id (PK)
  email                 title
  password              ownerId (FK → users.id)
  role                  status
  createdAt             createdAt
                        updatedAt

users.id ←── tasks.ownerId
(one user can have many tasks)
```

---

## Step 4: Run the Migration (10 min)

```bash
npx prisma migrate dev --name init
# ↑ 'migrate dev' does three things:
#   1. Compares schema.prisma to the current DB state
#   2. Generates SQL to make the DB match the schema
#   3. Runs the SQL and saves it as prisma/migrations/TIMESTAMP_init/migration.sql
#   4. Regenerates the Prisma Client TypeScript types
```

Look at the generated file in `prisma/migrations/` — it's the actual SQL:
```sql
CREATE TYPE "Role" AS ENUM ('USER', 'ADMIN');
CREATE TABLE "User" (
    "id" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    ...
);
-- etc.
```

---

## Step 5: Create PrismaService (20 min)

Create `src/prisma/prisma.service.ts`:

```typescript
import { Injectable, OnModuleInit } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';

@Injectable()
export class PrismaService extends PrismaClient implements OnModuleInit {
  // ↑ extends PrismaClient: PrismaService IS a PrismaClient.
  //   So you can call: this.prisma.user.findMany() etc.

  async onModuleInit() {
    // ↑ NestJS lifecycle hook — runs when the module is initialized
    await this.$connect();
    // ↑ Opens the database connection.
    //   Prisma actually connects lazily (on first query) but being explicit is good practice.
  }
  // Note: No onModuleDestroy needed — Prisma handles cleanup automatically
}
```

Create `src/prisma/prisma.module.ts`:

```typescript
import { Global, Module } from '@nestjs/common';
import { PrismaService } from './prisma.service';

@Global()
// ↑ @Global() makes PrismaService available in every module without needing to import PrismaModule.
//   Without @Global(), every module that needs the DB would have to add PrismaModule to its imports.
//   Use @Global() sparingly — only for truly cross-cutting concerns like the DB connection.
@Module({
  providers: [PrismaService],
  exports: [PrismaService],
})
export class PrismaModule {}
```

Import `PrismaModule` in `AppModule`:

```typescript
@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    PrismaModule,   // ← add this
    TasksModule,
    UsersModule,
    AuthModule,
  ],
})
export class AppModule {}
```

---

## Step 6: Replace In-Memory with Prisma (1h)

Update `src/users/users.service.ts` to use Prisma:

```typescript
import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaService) {}
  // ↑ PrismaService is @Global(), so it's injectable here without importing PrismaModule

  findByEmail(email: string) {
    return this.prisma.user.findUnique({ where: { email } });
    // ↑ findUnique: expects a unique field (email has @unique). Returns User | null
    // The return type is inferred automatically from your schema — no manual typing needed!
  }

  findById(id: string) {
    return this.prisma.user.findUnique({ where: { id } });
  }

  create(email: string, password: string) {
    return this.prisma.user.create({
      data: { email, password },
      // ↑ 'role' defaults to 'USER' (as defined in schema with @default(USER))
    });
  }
}
```

Update `src/tasks/tasks.service.ts`:

```typescript
import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { TaskStatus } from '@prisma/client';
// ↑ Import TaskStatus from Prisma client — it's generated from your schema

@Injectable()
export class TasksService {
  constructor(private prisma: PrismaService) {}

  findAll(userId: string) {
    return this.prisma.task.findMany({
      where: { ownerId: userId },
      // ↑ Only return tasks belonging to this user
      orderBy: { createdAt: 'desc' },
      // ↑ Most recent first
    });
  }

  async findById(id: string, userId: string) {
    const task = await this.prisma.task.findFirst({
      where: { id, ownerId: userId },
      // ↑ findFirst instead of findUnique because we're filtering by two fields
      //   Also ensures users can only access their own tasks
    });
    if (!task) throw new NotFoundException(`Task ${id} not found`);
    return task;
  }

  create(userId: string, title: string, description: string) {
    return this.prisma.task.create({
      data: { title, description, ownerId: userId },
    });
  }

  async updateStatus(id: string, userId: string, status: TaskStatus) {
    await this.findById(id, userId); // throws 404 if not found or not owner
    return this.prisma.task.update({
      where: { id },
      data: { status },
      // ↑ updatedAt is automatically updated by Prisma (@updatedAt in schema)
    });
  }

  async delete(id: string, userId: string) {
    await this.findById(id, userId); // throws 404 if not found or not owner
    await this.prisma.task.delete({ where: { id } });
  }
}
```

Update `TasksController` to pass `userId` from the JWT:

```typescript
@Get()
findAll(@Request() req) {
  return this.tasksService.findAll(req.user.id);
  // req.user is set by JwtStrategy.validate() in Phase 4
}

@Post()
create(@Request() req, @Body() dto: CreateTaskDto) {
  return this.tasksService.create(req.user.id, dto.title, dto.description);
}
```

---

## Step 7: Seed the Database (30 min)

Create `prisma/seed.ts`:

```typescript
import { PrismaClient, Role } from '@prisma/client';
import * as bcrypt from 'bcrypt';

const prisma = new PrismaClient();

async function main() {
  console.log('Seeding...');

  const adminHash = await bcrypt.hash('admin123', 10);
  const userHash = await bcrypt.hash('user123', 10);

  // upsert = update if exists, create if not
  // This lets you run the seed multiple times without duplicates
  const admin = await prisma.user.upsert({
    where: { email: 'admin@test.com' },
    update: {},
    create: { email: 'admin@test.com', password: adminHash, role: Role.ADMIN },
  });

  const user = await prisma.user.upsert({
    where: { email: 'user@test.com' },
    update: {},
    create: { email: 'user@test.com', password: userHash },
  });

  await prisma.task.createMany({
    data: [
      { title: 'Setup NestJS project', description: 'Phases 1-2', ownerId: admin.id, status: 'DONE' },
      { title: 'Add validation', description: 'Phase 3', ownerId: admin.id, status: 'DONE' },
      { title: 'Add authentication', description: 'Phase 4', ownerId: user.id },
      { title: 'Add database', description: 'Phase 5', ownerId: user.id },
    ],
    skipDuplicates: true,
  });

  console.log('Done! Created admin@test.com and user@test.com');
}

main()
  .catch(console.error)
  .finally(() => prisma.$disconnect());
```

Add to `package.json`:
```json
"prisma": {
  "seed": "ts-node prisma/seed.ts"
}
```

```bash
npm i -D ts-node
npx prisma db seed
npx prisma studio   # Opens at http://localhost:5555 — visual DB browser
```

---

## Checkpoint ✓ — Milestone M3

**Bash / macOS / Git Bash:**
```bash
# 1. Login with seeded admin
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"admin123"}'

TOKEN="paste-token-here"

# 2. List tasks — should see seeded tasks
curl http://localhost:3000/tasks -H "Authorization: Bearer $TOKEN"

# 3. Create a new task
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Survives restart","description":"testing persistence"}'

# 4. Stop the server (Ctrl+C), restart with: npm run start:dev
# 5. List tasks again — task should still be there
curl http://localhost:3000/tasks -H "Authorization: Bearer $TOKEN"
```

**Windows PowerShell:**
```powershell
# 1. Login and capture token automatically
$res = Invoke-RestMethod -Method Post "http://localhost:3000/auth/login" `
  -ContentType "application/json" `
  -Body '{"email":"admin@test.com","password":"admin123"}'
$TOKEN = $res.accessToken

# 2. List seeded tasks
Invoke-RestMethod "http://localhost:3000/tasks" `
  -Headers @{ Authorization = "Bearer $TOKEN" }

# 3. Create a new task
$task = Invoke-RestMethod -Method Post "http://localhost:3000/tasks" `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $TOKEN" } `
  -Body '{"title":"Survives restart","description":"testing persistence"}'
Write-Host "Created task id: $($task.id)"

# 4. Stop the server (Ctrl+C in the terminal running npm run start:dev)
# 5. Start it again: npm run start:dev
# 6. Re-login (token expires; get a fresh one) then list tasks:
$res2 = Invoke-RestMethod -Method Post "http://localhost:3000/auth/login" `
  -ContentType "application/json" `
  -Body '{"email":"admin@test.com","password":"admin123"}'
Invoke-RestMethod "http://localhost:3000/tasks" `
  -Headers @{ Authorization = "Bearer $($res2.accessToken)" }
# The task you created should still appear
```

- [ ] Tasks persist after server restart
- [ ] `prisma/migrations/` contains at least one migration file
- [ ] `npx prisma studio` shows User and Task tables with data
- [ ] `DATABASE_URL` is only in `.env`, never in source code
- [ ] You understand what a foreign key is and why `tasks.ownerId` references `users.id`

## Optional Reading
- Prisma Getting Started: https://www.prisma.io/docs/getting-started
- Prisma NestJS recipe: https://docs.nestjs.com/recipes/prisma
- Prisma Schema reference: https://www.prisma.io/docs/concepts/components/prisma-schema
- Prisma Migrations guide: https://www.prisma.io/docs/concepts/components/prisma-migrate
