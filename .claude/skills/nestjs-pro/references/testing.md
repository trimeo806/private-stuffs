# NestJS Testing

## Purpose

Unit testing with `Test.createTestingModule`, E2E testing with `supertest`, and test double strategies for NestJS services, guards, and pipes.

## Table of Contents

- [Unit Testing Services](#unit-testing-services)
- [Unit Testing Controllers](#unit-testing-controllers)
- [Unit Testing Guards](#unit-testing-guards)
- [E2E Testing](#e2e-testing)
- [Test Utilities](#test-utilities)
- [DO / DON'T](#do--dont)
- [Related Documents](#related-documents)

---

## Unit Testing Services

Replace real dependencies with jest mock objects. Test business logic in isolation.

```typescript
// users/users.service.spec.ts
import { Test, TestingModule } from '@nestjs/testing';
import { UsersService } from './users.service';
import { USER_REPO } from './repositories/user-repo.token';
import { IUserRepository } from './repositories/user.repository.interface';
import { NotFoundException, ConflictException } from '@nestjs/common';

const mockUserRepo: jest.Mocked<IUserRepository> = {
  create: jest.fn(),
  findById: jest.fn(),
  findByEmail: jest.fn(),
  findAll: jest.fn(),
  update: jest.fn(),
  delete: jest.fn(),
};

describe('UsersService', () => {
  let service: UsersService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UsersService,
        { provide: USER_REPO, useValue: mockUserRepo },
      ],
    }).compile();

    service = module.get<UsersService>(UsersService);
  });

  afterEach(() => jest.clearAllMocks());

  describe('findOne', () => {
    it('returns user when found', async () => {
      const user = { id: '1', email: 'alice@example.com' } as any;
      mockUserRepo.findById.mockResolvedValue(user);

      const result = await service.findOne('1');

      expect(result).toEqual(user);
      expect(mockUserRepo.findById).toHaveBeenCalledWith('1');
    });

    it('throws NotFoundException when user does not exist', async () => {
      mockUserRepo.findById.mockResolvedValue(null);

      await expect(service.findOne('missing')).rejects.toThrow(NotFoundException);
    });
  });

  describe('create', () => {
    it('throws ConflictException when email already registered', async () => {
      mockUserRepo.findByEmail.mockResolvedValue({ id: '1' } as any);

      await expect(
        service.create({ email: 'alice@example.com', password: 'pass' }),
      ).rejects.toThrow(ConflictException);
    });
  });
});
```

---

## Unit Testing Controllers

Compile only the controller and a mock service — do not start a full HTTP server.

```typescript
// users/users.controller.spec.ts
import { Test, TestingModule } from '@nestjs/testing';
import { UsersController } from './users.controller';
import { UsersService } from './users.service';

const mockUsersService = {
  findOne: jest.fn(),
  create: jest.fn(),
};

describe('UsersController', () => {
  let controller: UsersController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UsersController],
      providers: [{ provide: UsersService, useValue: mockUsersService }],
    }).compile();

    controller = module.get<UsersController>(UsersController);
  });

  afterEach(() => jest.clearAllMocks());

  it('calls service.findOne with the route param', async () => {
    const user = { id: '1', email: 'alice@example.com' } as any;
    mockUsersService.findOne.mockResolvedValue(user);

    const result = await controller.findOne('1');

    expect(result).toEqual(user);
    expect(mockUsersService.findOne).toHaveBeenCalledWith('1');
  });
});
```

---

## Unit Testing Guards

Provide a mock `Reflector` (or use the real one) and construct a fake `ExecutionContext`.

```typescript
// common/guards/roles.guard.spec.ts
import { RolesGuard } from './roles.guard';
import { Reflector } from '@nestjs/core';
import { ExecutionContext } from '@nestjs/common';

const createContext = (user: object, handler: object = {}): ExecutionContext =>
  ({
    switchToHttp: () => ({ getRequest: () => ({ user }) }),
    getHandler: () => handler,
    getClass: () => ({}),
  }) as unknown as ExecutionContext;

describe('RolesGuard', () => {
  let guard: RolesGuard;
  let reflector: Reflector;

  beforeEach(() => {
    reflector = new Reflector();
    guard = new RolesGuard(reflector);
  });

  it('allows access when no roles required', () => {
    const ctx = createContext({ roles: [] });
    jest.spyOn(reflector, 'getAllAndOverride').mockReturnValue(undefined);

    expect(guard.canActivate(ctx)).toBe(true);
  });

  it('denies access when user lacks required role', () => {
    const ctx = createContext({ roles: ['user'] });
    jest.spyOn(reflector, 'getAllAndOverride').mockReturnValue(['admin']);

    expect(guard.canActivate(ctx)).toBe(false);
  });

  it('grants access when user has required role', () => {
    const ctx = createContext({ roles: ['admin', 'user'] });
    jest.spyOn(reflector, 'getAllAndOverride').mockReturnValue(['admin']);

    expect(guard.canActivate(ctx)).toBe(true);
  });
});
```

---

## E2E Testing

Start a full NestJS application and drive it with `supertest`. Validates the entire request pipeline including pipes, guards, and exception filters.

```typescript
// test/users.e2e-spec.ts
import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('UsersController (e2e)', () => {
  let app: INestApplication;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();

    // Mirror main.ts setup
    app.useGlobalPipes(
      new ValidationPipe({ whitelist: true, forbidNonWhitelisted: true, transform: true }),
    );

    await app.init();
  });

  afterAll(async () => {
    await app.close();
  });

  it('POST /users — creates a user (201)', () => {
    return request(app.getHttpServer())
      .post('/users')
      .send({ email: 'test@example.com', password: 'StrongP@ss1' })
      .expect(201)
      .expect((res) => {
        expect(res.body.id).toBeDefined();
        expect(res.body.email).toBe('test@example.com');
        expect(res.body.passwordHash).toBeUndefined(); // excluded via @Exclude
      });
  });

  it('POST /users — returns 400 on invalid email', () => {
    return request(app.getHttpServer())
      .post('/users')
      .send({ email: 'not-an-email', password: 'StrongP@ss1' })
      .expect(400);
  });

  it('GET /users/:id — returns 404 for unknown id', () => {
    return request(app.getHttpServer())
      .get('/users/00000000-0000-0000-0000-000000000000')
      .expect(404);
  });
});
```

---

## Test Utilities

### Override providers in E2E tests

```typescript
const moduleFixture = await Test.createTestingModule({
  imports: [AppModule],
})
  .overrideProvider(USER_REPO)
  .useValue(mockUserRepo)
  .compile();
```

### Access the compiled module's providers

```typescript
const service = moduleFixture.get<UsersService>(UsersService);
const repo = moduleFixture.get<IUserRepository>(USER_REPO);
```

---

## DO / DON'T

| DO | DON'T |
|----|-------|
| Use `jest.clearAllMocks()` in `afterEach` | Let mock call history bleed between tests |
| Mirror `main.ts` pipe/filter setup in E2E `beforeAll` | Use different validation options in E2E than production — tests become meaningless |
| Test happy path + at least one error path per service method | Test only the success case |
| Mock repository interfaces, not `PrismaService` directly | Mock `PrismaService` — you may miss type mismatches in the repository |
| Use `overrideProvider` to swap dependencies in E2E tests | Re-import individual modules and wire them manually for E2E |

---

## Related Documents

- `references/modules.md` — `Test.createTestingModule` mirrors module structure
- `references/gipf.md` — Testing guards and interceptors in isolation
- `references/validation.md` — E2E tests verify ValidationPipe behaviour
- `references/prisma.md` — Mock the repository interface, not PrismaService
