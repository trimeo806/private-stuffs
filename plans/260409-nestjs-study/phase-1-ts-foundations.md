---
phase: 1
title: 'TypeScript + Node.js Foundations'
effort: 8h
depends: []
weeks: '1'
---

# Phase 1: TypeScript + Node.js Foundations

## Context Links
- [Plan Overview](./plan.md)

## Agent & Skills
- **Agent**: `researcher`
- **Skills**: `typescript-pro`
- **Handoffs**: After completion → Phase 2

---

## Why This Phase Exists

NestJS is built entirely on top of TypeScript features that many developers haven't used deeply —
specifically **decorators** and **type metadata at runtime**. If you skip this, you'll copy-paste
NestJS patterns without understanding *why* they work. That leads to confusion when something breaks.

This phase takes about 8 hours. Don't rush it.

---

## Concept: What Is a Decorator?

A **decorator** is a function that wraps another function, class, or property to add behavior —
without modifying the original code.

Think of it like a label you stick on something:

```typescript
// This is a decorator factory — a function that returns a decorator
function Log(target: any, key: string, descriptor: PropertyDescriptor) {
  const originalMethod = descriptor.value;

  descriptor.value = function (...args: any[]) {
    console.log(`Calling ${key} with`, args);
    const result = originalMethod.apply(this, args);
    console.log(`${key} returned`, result);
    return result;
  };

  return descriptor;
}

class TaskService {
  @Log   // ← attach the decorator here
  create(title: string) {
    return { id: 1, title };
  }
}

new TaskService().create('Buy milk');
// Output:
// Calling create with ['Buy milk']
// create returned { id: 1, title: 'Buy milk' }
```

**Why NestJS uses decorators everywhere:**

| NestJS decorator | What it does |
|-----------------|--------------|
| `@Injectable()` | Marks a class so NestJS can inject it into other classes |
| `@Controller()` | Marks a class as an HTTP handler |
| `@Get('/tasks')` | Maps the HTTP GET method + path to a function |
| `@Body()` | Extracts the request body and passes it as a parameter |
| `@Module()` | Groups controllers and services together |

All of these are just decorators — functions that add metadata to your classes.

---

## Concept: What Is `emitDecoratorMetadata`?

NestJS's dependency injection system needs to know the **types** of constructor parameters at runtime.
TypeScript normally erases all type information when it compiles to JavaScript. `emitDecoratorMetadata`
tells the TypeScript compiler to keep type info in the compiled output.

Example:
```typescript
// TypeScript source
@Injectable()
class TasksController {
  constructor(private tasksService: TasksService) {}
}

// With emitDecoratorMetadata: true, the compiled JS includes:
// Reflect.metadata("design:paramtypes", [TasksService])
// This lets NestJS know "inject a TasksService here"
```

Without this flag, NestJS has no idea what to inject. Your app silently gets `undefined` instead of
the service.

---

## Concept: `reflect-metadata` — Why You Import It

`reflect-metadata` is a polyfill (a library that adds a feature the runtime doesn't have yet).
It adds `Reflect.defineMetadata()` and `Reflect.getMetadata()` to Node.js, which is what NestJS
uses internally to read the metadata that `emitDecoratorMetadata` emits.

You'll see this in every NestJS app's `main.ts` or imported via `@nestjs/core`:

```typescript
import 'reflect-metadata'; // must be imported once before anything else
```

You don't call it directly — NestJS uses it behind the scenes to wire up your DI container.

---

## Warm-up Exercise: Write a Decorator From Scratch (30 min)

Open the TypeScript Playground (typescriptlang.org/play) and write this **without copy-pasting**:

```typescript
// Step 1: A method decorator that logs how long a function takes
function Timed(target: any, key: string, descriptor: PropertyDescriptor) {
  const original = descriptor.value;
  descriptor.value = function (...args: any[]) {
    const start = Date.now();
    const result = original.apply(this, args);
    console.log(`${key} took ${Date.now() - start}ms`);
    return result;
  };
  return descriptor;
}

// Step 2: Use it
class DataService {
  @Timed
  processData(items: string[]) {
    return items.map(i => i.toUpperCase());
  }
}

const svc = new DataService();
svc.processData(['a', 'b', 'c']);
// Output: processData took 0ms
```

Once this works, try:
- A **class decorator** that logs when the class is instantiated
- A **property decorator** that validates a value isn't empty

---

## Exercise 1: Generic In-Memory Store (1h)

This pattern appears in Phase 2 before you add a real database. Build it now so it's familiar:

```typescript
// Why generics? So Store works with any type, not just Task
// The constraint `{ id: string }` means "T must have an id field"
class Store<T extends { id: string }> {
  private items: T[] = [];

  save(item: T): T {
    const existing = this.items.findIndex(i => i.id === item.id);
    if (existing >= 0) {
      this.items[existing] = item; // update
    } else {
      this.items.push(item);       // insert
    }
    return item;
  }

  findAll(): T[] {
    return [...this.items]; // return a copy, not the original array
  }

  findById(id: string): T | undefined {
    return this.items.find(i => i.id === id);
  }

  delete(id: string): boolean {
    const before = this.items.length;
    this.items = this.items.filter(i => i.id !== id);
    return this.items.length < before; // true if something was deleted
  }
}

// Test it with two different types
interface Task { id: string; title: string; }
interface User { id: string; email: string; }

const tasks = new Store<Task>();
tasks.save({ id: '1', title: 'Learn NestJS' });
tasks.save({ id: '2', title: 'Build an API' });
console.log(tasks.findAll());
// [{ id: '1', title: 'Learn NestJS' }, { id: '2', title: 'Build an API' }]

tasks.delete('1');
console.log(tasks.findById('1')); // undefined

const users = new Store<User>(); // same class, different type
users.save({ id: 'u1', email: 'test@test.com' });
```

**Why this matters:** `TasksService` in Phase 2 will be almost identical to this `Store<T>`,
just with NestJS decorators added and Prisma replacing the array in Phase 5.

---

## Exercise 2: Understand the tsconfig (30 min)

Create a `tsconfig.json` file and add a comment explaining each flag:

```json
{
  "compilerOptions": {
    "target": "ES2021",
    // ↑ Which JS version to compile to. ES2021 supports async/await, optional chaining.

    "experimentalDecorators": true,
    // ↑ Enables the @Decorator syntax. Without this, @Injectable() is a syntax error.

    "emitDecoratorMetadata": true,
    // ↑ Makes TypeScript include type info in the compiled JS.
    //   NestJS needs this to know what to inject into constructors.

    "strict": true,
    // ↑ Enables all strict type checks: no implicit any, strict null checks, etc.
    //   This catches bugs at compile time that would silently fail at runtime.

    "esModuleInterop": true,
    // ↑ Makes CommonJS imports work with ES module syntax.
    //   Allows: import express from 'express' (not: import * as express from 'express')

    "outDir": "./dist",
    // ↑ Where compiled JS files go.

    "baseUrl": "./",
    "paths": {
      "@/*": ["src/*"]
      // ↑ Path alias so you can write: import { Task } from '@/tasks/task.model'
      //   instead of: import { Task } from '../../tasks/task.model'
    }
  }
}
```

**Question to answer before moving on:** If `emitDecoratorMetadata` is false and you have:

```typescript
@Injectable()
class TasksController {
  constructor(private tasksService: TasksService) {}
}
```

What does NestJS see at runtime for the constructor parameters? (Answer: an empty array `[]`.
NestJS has no idea what `TasksService` is. Injection fails silently with `undefined`.)

---

## Exercise 3: Scaffold the Project (1h)

This is the real start of your **Task API**. Everything in Phases 2–8 builds on this.

```bash
# Install the NestJS CLI globally
npm i -g @nestjs/cli

# Create a new project (choose npm when prompted)
nest new task-api

cd task-api
npm run start:dev
```

Open `http://localhost:3000` — you see `"Hello World!"`.

Now **read every generated file** before writing any code:

### `src/main.ts` — The Entry Point

```typescript
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  // ↑ Creates the NestJS application from AppModule (the root module)
  // This is where the DI container is built, modules are loaded, etc.

  await app.listen(3000);
  // ↑ Starts the HTTP server on port 3000
}
bootstrap();
```

### `src/app.module.ts` — The Root Module

```typescript
import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';

@Module({
  imports: [],        // ← other modules this module depends on
  controllers: [AppController], // ← which controllers belong here
  providers: [AppService],      // ← services/providers available in this module
})
export class AppModule {}
```

**A module is like a namespace or a folder.** It groups related controllers and services.
`AppModule` is the root — everything eventually connects here.

### `src/app.controller.ts` — The Route Handler

```typescript
import { Controller, Get } from '@nestjs/common';
import { AppService } from './app.service';

@Controller()
// ↑ This class is an HTTP controller. The empty string means routes start at '/'

export class AppController {
  constructor(private readonly appService: AppService) {}
  // ↑ NestJS reads the type 'AppService' and injects it automatically
  //   because AppService is listed in AppModule's providers array

  @Get()
  // ↑ This method handles GET requests to '/'
  getHello(): string {
    return this.appService.getHello();
  }
}
```

### `src/app.service.ts` — The Business Logic

```typescript
import { Injectable } from '@nestjs/common';

@Injectable()
// ↑ Marks this class as injectable — NestJS manages its lifecycle
export class AppService {
  getHello(): string {
    return 'Hello World!';
  }
}
```

**Pattern you'll see everywhere:**
- Controller handles the HTTP part (which URL, which method, what body)
- Service handles the logic part (what actually happens)
- They never talk directly — the service is *injected* into the controller

---

## Checkpoint ✓

You're ready for Phase 2 when you can answer these without looking:

- [ ] What does `@Injectable()` do? (Marks a class so NestJS's DI container can manage it)
- [ ] Why does NestJS need `emitDecoratorMetadata: true`? (To know what to inject)
- [ ] What is `main.ts`'s job? (Bootstrap the app, create the DI container, start listening)
- [ ] `GET /` returns `"Hello World!"` when you run `npm run start:dev`
- [ ] You can point to where the route is defined, where the logic is, and why they're separate

## Optional Reading (after you've done the exercises)
- TypeScript Handbook — Decorators: https://www.typescriptlang.org/docs/handbook/decorators.html
- NestJS First Steps: https://docs.nestjs.com/first-steps
- NestJS Overview (5 min read): https://docs.nestjs.com/
