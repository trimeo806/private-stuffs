# NestJS + Prisma

## Purpose

PrismaService lifecycle, PrismaModule global setup, and the repository interface pattern that decouples services from Prisma internals.

## Table of Contents

- [PrismaService](#prismaservice)
- [PrismaModule](#prismamodule)
- [Repository Interface Pattern](#repository-interface-pattern)
- [Catching Prisma Errors](#catching-prisma-errors)
- [Transactions](#transactions)
- [DO / DON'T](#do--dont)
- [Related Documents](#related-documents)

---

## PrismaService

Extend `PrismaClient` and implement `OnModuleInit` / `OnModuleDestroy` to manage the connection lifecycle with NestJS.

```typescript
// database/prisma.service.ts
import { Injectable, OnModuleInit, OnModuleDestroy } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';

@Injectable()
export class PrismaService extends PrismaClient implements OnModuleInit, OnModuleDestroy {
  async onModuleInit(): Promise<void> {
    await this.$connect();
  }

  async onModuleDestroy(): Promise<void> {
    await this.$disconnect();
  }
}
```

- `$connect()` is called once when the module initialises — no manual connection management needed.
- `$disconnect()` runs on graceful shutdown, draining any in-flight queries before exit.

---

## PrismaModule

Mark as `@Global()` so every feature module can inject `PrismaService` without explicitly importing `PrismaModule`.

```typescript
// database/prisma.module.ts
import { Global, Module } from '@nestjs/common';
import { PrismaService } from './prisma.service';

@Global()
@Module({
  providers: [PrismaService],
  exports: [PrismaService],
})
export class PrismaModule {}

// app.module.ts — import once at the root
@Module({
  imports: [PrismaModule, UsersModule, /* ... */],
})
export class AppModule {}
```

---

## Repository Interface Pattern

Services inject a repository interface token, never `PrismaService` directly. This keeps services unit-testable — swap the real repository for a mock without touching service code.

```typescript
// users/repositories/user.repository.interface.ts
import { User } from '@prisma/client';
import { CreateUserDto } from '../dto/create-user.dto';

export const USER_REPO = Symbol('IUserRepository');

export interface IUserRepository {
  create(dto: CreateUserDto): Promise<User>;
  findById(id: string): Promise<User | null>;
  findByEmail(email: string): Promise<User | null>;
  findAll(skip: number, take: number): Promise<User[]>;
  update(id: string, data: Partial<User>): Promise<User>;
  delete(id: string): Promise<void>;
}

// users/repositories/prisma-user.repository.ts
import { Injectable } from '@nestjs/common';
import { PrismaService } from '../../database/prisma.service';
import { IUserRepository } from './user.repository.interface';
import { CreateUserDto } from '../dto/create-user.dto';
import { User } from '@prisma/client';

@Injectable()
export class PrismaUserRepository implements IUserRepository {
  constructor(private readonly prisma: PrismaService) {}

  create(dto: CreateUserDto): Promise<User> {
    return this.prisma.user.create({ data: dto });
  }

  findById(id: string): Promise<User | null> {
    return this.prisma.user.findUnique({ where: { id } });
  }

  findByEmail(email: string): Promise<User | null> {
    return this.prisma.user.findUnique({ where: { email } });
  }

  findAll(skip: number, take: number): Promise<User[]> {
    return this.prisma.user.findMany({ skip, take, orderBy: { createdAt: 'desc' } });
  }

  async update(id: string, data: Partial<User>): Promise<User> {
    return this.prisma.user.update({ where: { id }, data });
  }

  async delete(id: string): Promise<void> {
    await this.prisma.user.delete({ where: { id } });
  }
}

// users/users.module.ts — wire the token
@Module({
  providers: [
    UsersService,
    { provide: USER_REPO, useClass: PrismaUserRepository },
  ],
  exports: [UsersService],
})
export class UsersModule {}

// users/users.service.ts — inject the interface
@Injectable()
export class UsersService {
  constructor(@Inject(USER_REPO) private readonly repo: IUserRepository) {}
}
```

---

## Catching Prisma Errors

Translate Prisma constraint errors to domain exceptions in the repository layer — never let Prisma errors bubble to the client.

```typescript
import { PrismaClientKnownRequestError } from '@prisma/client/runtime/library';
import { ConflictException, InternalServerErrorException } from '@nestjs/common';

async create(dto: CreateUserDto): Promise<User> {
  try {
    return await this.prisma.user.create({ data: dto });
  } catch (err) {
    if (err instanceof PrismaClientKnownRequestError) {
      if (err.code === 'P2002') {
        // Unique constraint violation
        throw new ConflictException('A user with this email already exists');
      }
      if (err.code === 'P2025') {
        // Record not found (e.g. on update/delete)
        throw new NotFoundException('Record not found');
      }
    }
    throw new InternalServerErrorException('Database error');
  }
}
```

Common Prisma error codes:

| Code | Meaning |
|------|---------|
| `P2002` | Unique constraint violation |
| `P2003` | Foreign key constraint failed |
| `P2025` | Record not found |
| `P2014` | Relation violation |

---

## Transactions

Use `$transaction` for operations that must succeed or fail atomically.

```typescript
// Sequential transaction (array form)
async transferCredits(fromId: string, toId: string, amount: number): Promise<void> {
  await this.prisma.$transaction([
    this.prisma.user.update({ where: { id: fromId }, data: { credits: { decrement: amount } } }),
    this.prisma.user.update({ where: { id: toId },   data: { credits: { increment: amount } } }),
  ]);
}

// Interactive transaction (callback form) — use when logic depends on intermediate results
async purchaseItem(userId: string, itemId: string): Promise<Order> {
  return this.prisma.$transaction(async (tx) => {
    const item = await tx.item.findUniqueOrThrow({ where: { id: itemId } });
    if (!item.available) throw new ConflictException('Item is no longer available');

    await tx.user.update({
      where: { id: userId },
      data: { credits: { decrement: item.price } },
    });

    return tx.order.create({ data: { userId, itemId } });
  });
}
```

- Interactive transactions hold the connection for their duration — keep them short.
- Throw inside the callback to trigger automatic rollback.

---

## DO / DON'T

| DO | DON'T |
|----|-------|
| Inject `PrismaService` only in repository classes | Inject `PrismaService` directly into service classes |
| Use `Symbol('IUserRepository')` as the injection token | Use string tokens — risk of collision |
| Catch `PrismaClientKnownRequestError` in the repository and rethrow as `HttpException` | Let Prisma errors surface in HTTP responses |
| Use `$transaction` for multi-step atomic operations | Chain separate `await prisma.x.update()` calls without a transaction |
| Keep transactions short and focused | Run long business logic inside an interactive transaction |

---

## Related Documents

- `references/modules.md` — Registering PrismaModule globally
- `references/exceptions.md` — Translating Prisma errors to domain exceptions
- `references/testing.md` — Mocking the repository interface in unit tests
- `references/project-structure.md` — Where `prisma.service.ts` lives
