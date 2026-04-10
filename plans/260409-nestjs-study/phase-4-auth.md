---
phase: 4
title: 'Authentication — Passport + JWT + RBAC'
effort: 14h
depends: [3]
weeks: '5-6'
---

# Phase 4: Authentication — Passport + JWT + RBAC

## Context Links
- [Plan Overview](./plan.md)
- [Phase 3](./phase-3-request-lifecycle.md)

## Agent & Skills
- **Agent**: `backend-developer`
- **Skills**: `nestjs-pro`, `typescript-pro`, `api-designer`
- **Handoffs**: After completion → `security-auditor` for review → Phase 5

---

## What You Build This Phase

```
POST /auth/register   → create account, return JWT
POST /auth/login      → validate password, return JWT
GET  /tasks           → 401 without a token
GET  /tasks           → 200 with a valid token
DELETE /tasks/:id     → 403 if your role is USER (not ADMIN)
```

---

## Concept: What Is JWT (JSON Web Token)?

A **JWT** is a signed string that proves "I am who I say I am" without the server needing
to look up the user in a database on every request.

A JWT has three base64-encoded parts separated by dots:

```
eyJhbGciOiJIUzI1NiJ9   .   eyJzdWIiOiIxMjMiLCJlbWFpbCI6InRlc3RAdGVzdC5jb20ifQ   .   SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
        │                                    │                                                    │
    Header                               Payload                                             Signature
(algorithm used)                   (data you stored)                          (Header + Payload signed with JWT_SECRET)
```

**The payload** (the middle part) contains "claims" — data you stored when creating the token:
```json
{ "sub": "user-id-123", "email": "test@test.com", "role": "USER", "exp": 1712000000 }
```

**Important:** the payload is **not encrypted** — anyone can base64-decode it and read it.
Never put passwords or sensitive data in a JWT. What makes it secure is the **signature** —
it proves the token was created by your server (because only your server knows `JWT_SECRET`).

**How authentication works with JWT:**

```
1. POST /auth/login  { email, password }
       │
       ▼
   Server checks password → valid
   Server signs a JWT with JWT_SECRET
   Server returns: { accessToken: "eyJ..." }
       │
       ▼
2. Client stores the token (in memory or localStorage)

3. GET /tasks
   Authorization: Bearer eyJ...
       │
       ▼
   Server verifies signature with JWT_SECRET
   Server extracts user info from payload
   Server processes the request as that user
```

The server never stores the token — it just verifies it. This is why JWT is called "stateless".

---

## Concept: What Is Passport.js?

**Passport.js** is an authentication middleware library for Node.js. It supports 500+
authentication strategies (local username/password, Google OAuth, GitHub, JWT, etc.).

In NestJS, Passport is used via `@nestjs/passport`. The key concept is a **Strategy** —
a class that defines one way to authenticate:

| Strategy | What it does |
|----------|-------------|
| `passport-local` | Validates email + password from request body |
| `passport-jwt` | Validates a JWT token from the Authorization header |
| `passport-google-oauth20` | Validates a Google OAuth token |

You only need `local` and `jwt` for this phase.

**How a Strategy works in NestJS:**

```typescript
@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  // ↑ Mixes PassportStrategy and passport-jwt's Strategy together
  //   This is a mixin pattern — JwtStrategy inherits both

  constructor() {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      // ↑ Tell Passport where to find the token:
      //   Look in the Authorization header, expect: "Bearer eyJ..."

      secretOrKey: 'your-secret',
      // ↑ The secret used to verify the signature
    });
  }

  validate(payload: { sub: string; email: string; role: string }) {
    // ↑ Called after the token is verified as valid.
    //   The payload is the decoded JWT body.
    //   Whatever you return here becomes req.user
    return { id: payload.sub, email: payload.email, role: payload.role };
  }
}
```

---

## Concept: What Is a Guard?

A **guard** is a class that decides whether a request should proceed.
It runs after middleware but before pipes. It returns `true` (allow) or `false` (deny → 403).

```typescript
@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {}
// ↑ extends AuthGuard('jwt') means: "use the 'jwt' Passport strategy to decide access"
// If the JWT is valid → allow (req.user is set)
// If the JWT is missing or invalid → 401 Unauthorized
```

You apply guards at three levels:
```typescript
// 1. Single method — protect one endpoint
@UseGuards(JwtAuthGuard)
@Get('profile')
getProfile() { ... }

// 2. Entire controller — protect all endpoints in the controller
@UseGuards(JwtAuthGuard)
@Controller('tasks')
export class TasksController { ... }

// 3. Globally — protect every route in the app
app.useGlobalGuards(new JwtAuthGuard());
```

---

## Concept: What Is RBAC?

**RBAC (Role-Based Access Control)** means: "you can do this only if you have the right role."

A user has a role (e.g. `USER`, `ADMIN`). Certain endpoints require a specific role.

Implementation pattern:
1. Store the role in the JWT payload (e.g. `{ role: "ADMIN" }`)
2. Create a `@Roles()` decorator that attaches required roles to a route
3. Create a `RolesGuard` that reads the required roles and checks the current user's role

---

## Step 1: Install Dependencies (10 min)

```bash
npm i @nestjs/passport @nestjs/jwt @nestjs/config
npm i passport passport-local passport-jwt bcrypt
npm i -D @types/passport-local @types/passport-jwt @types/bcrypt
```

What each package does:
- `@nestjs/passport` — NestJS wrapper for Passport.js
- `@nestjs/jwt` — NestJS wrapper for the `jsonwebtoken` library
- `@nestjs/config` — Loads environment variables from `.env`
- `passport-local` — username/password strategy (for login)
- `passport-jwt` — JWT strategy (for protected routes)
- `bcrypt` — password hashing (NEVER store plaintext passwords)

---

## Step 2: Create .env and Load Config (15 min)

Create `.env` in the project root:
```
JWT_SECRET=super-secret-key-change-in-production
JWT_EXPIRES_IN=15m
```

Add `.env` to `.gitignore` — **secrets must never be committed to git.**

In `app.module.ts`, add `ConfigModule`:

```typescript
import { ConfigModule } from '@nestjs/config';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    // ↑ isGlobal: true means every module can inject ConfigService
    //   without importing ConfigModule themselves
    TasksModule,
  ],
})
export class AppModule {}
```

---

## Step 3: Create Users Feature (30 min)

```bash
nest g module users
nest g service users --no-spec
```

Create `src/users/user.model.ts`:

```typescript
// In Phase 5 this becomes a Prisma model. For now, a plain interface.
export enum Role {
  USER = 'USER',
  ADMIN = 'ADMIN',
}

export interface User {
  id: string;
  email: string;
  passwordHash: string;  // NEVER store the plain password
  role: Role;
}
```

In `src/users/users.service.ts`:

```typescript
import { Injectable } from '@nestjs/common';
import { v4 as uuid } from 'uuid';
import { User, Role } from './user.model';

@Injectable()
export class UsersService {
  // In-memory store — replaced with Prisma in Phase 5
  private users: User[] = [];

  findByEmail(email: string): User | undefined {
    return this.users.find(u => u.email === email);
  }

  findById(id: string): User | undefined {
    return this.users.find(u => u.id === id);
  }

  create(email: string, passwordHash: string): User {
    const user: User = { id: uuid(), email, passwordHash, role: Role.USER };
    this.users.push(user);
    return user;
  }
}
```

In `src/users/users.module.ts`, export `UsersService` so `AuthModule` can use it:

```typescript
@Module({
  providers: [UsersService],
  exports: [UsersService],  // ← without this, AuthModule can't inject UsersService
})
export class UsersModule {}
```

---

## Step 4: Create Auth Feature (1.5h)

```bash
nest g module auth
nest g service auth --no-spec
nest g controller auth --no-spec
```

Create `src/auth/auth.service.ts`:

```typescript
import { Injectable, UnauthorizedException, ConflictException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { UsersService } from '../users/users.service';
import { User } from '../users/user.model';
import * as bcrypt from 'bcrypt';

@Injectable()
export class AuthService {
  constructor(
    private usersService: UsersService,
    private jwtService: JwtService,
    // ↑ JwtService is provided by JwtModule (registered below in auth.module.ts)
  ) {}

  async register(email: string, password: string) {
    // Check if email already taken
    const existing = this.usersService.findByEmail(email);
    if (existing) {
      throw new ConflictException('Email already in use');
      // ConflictException → 409 response
    }

    // Hash the password before saving
    // bcrypt.hash(password, 10) — the '10' is the number of salt rounds
    // More rounds = more secure but slower. 10-12 is the standard.
    const passwordHash = await bcrypt.hash(password, 10);

    const user = this.usersService.create(email, passwordHash);
    return this.generateToken(user);
  }

  async login(email: string, password: string) {
    const user = this.usersService.findByEmail(email);
    if (!user) {
      // Don't say "email not found" — that leaks whether an email is registered
      throw new UnauthorizedException('Invalid credentials');
    }

    // bcrypt.compare takes the plain password and the hash and checks if they match
    // It's async because hashing is intentionally slow (to resist brute force attacks)
    const passwordValid = await bcrypt.compare(password, user.passwordHash);
    if (!passwordValid) {
      throw new UnauthorizedException('Invalid credentials');
    }

    return this.generateToken(user);
  }

  private generateToken(user: User) {
    // The payload is what gets stored IN the JWT
    // 'sub' is a standard JWT claim meaning "subject" (who the token is about)
    const payload = { sub: user.id, email: user.email, role: user.role };

    return {
      accessToken: this.jwtService.sign(payload),
      // jwtService.sign() creates and signs the JWT with JWT_SECRET
    };
  }
}
```

Create `src/auth/auth.controller.ts`:

```typescript
import { Controller, Post, Body, HttpCode } from '@nestjs/common';
import { AuthService } from './auth.service';

@Controller('auth')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('register')
  // POST /auth/register
  register(@Body() body: { email: string; password: string }) {
    return this.authService.register(body.email, body.password);
    // Returns: { accessToken: 'eyJ...' }
  }

  @Post('login')
  @HttpCode(200)
  // ↑ POST normally returns 201, but login is not "creating" something → 200
  login(@Body() body: { email: string; password: string }) {
    return this.authService.login(body.email, body.password);
  }
}
```

Create `src/auth/auth.module.ts`:

```typescript
import { Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { UsersModule } from '../users/users.module';
import { JwtStrategy } from './strategies/jwt.strategy';

@Module({
  imports: [
    UsersModule,
    // ↑ Import UsersModule so we can inject UsersService here

    JwtModule.registerAsync({
      // ↑ registerAsync (instead of register) because we need to read from ConfigService,
      //   which requires the DI container to be ready
      imports: [ConfigModule],
      useFactory: (config: ConfigService) => ({
        secret: config.get<string>('JWT_SECRET'),
        // ↑ ConfigService reads from .env — never hardcode this value
        signOptions: { expiresIn: config.get<string>('JWT_EXPIRES_IN') },
      }),
      inject: [ConfigService],
    }),
  ],
  controllers: [AuthController],
  providers: [AuthService, JwtStrategy],
})
export class AuthModule {}
```

---

## Step 5: Create the JWT Strategy (30 min)

Create `src/auth/strategies/jwt.strategy.ts`:

```typescript
import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  // PassportStrategy(Strategy) is a mixin:
  // - 'Strategy' comes from passport-jwt — knows how to extract + verify JWTs
  // - 'PassportStrategy' is NestJS's wrapper that makes it injectable
  constructor(config: ConfigService) {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      // ↑ Where to find the token in the request.
      //   fromAuthHeaderAsBearerToken() reads: Authorization: Bearer eyJ...

      ignoreExpiration: false,
      // ↑ Reject expired tokens. Always false in production.

      secretOrKey: config.get<string>('JWT_SECRET'),
      // ↑ The secret used to verify the signature
    });
  }

  validate(payload: { sub: string; email: string; role: string }) {
    // This method is called AFTER Passport has verified the token is valid.
    // 'payload' is the decoded JWT body: { sub, email, role, iat, exp }
    // Whatever you return here is attached to req.user
    return { id: payload.sub, email: payload.email, role: payload.role };
  }
}
```

---

## Step 6: Protect Routes with JwtAuthGuard (20 min)

Create `src/auth/guards/jwt-auth.guard.ts`:

```typescript
import { Injectable } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {
  // AuthGuard('jwt') means: use the strategy registered as 'jwt'
  // That's the JwtStrategy we just created (it registers itself as 'jwt' by default)
  // When a request hits a route protected by this guard:
  //   1. Passport extracts the token from the Authorization header
  //   2. Passport verifies the signature with JWT_SECRET
  //   3. If valid: calls JwtStrategy.validate(), sets req.user, allows request
  //   4. If invalid/missing: throws UnauthorizedException (401)
}
```

Apply it to `TasksController`:

```typescript
import { UseGuards } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';

@UseGuards(JwtAuthGuard)
// ↑ Every route in TasksController now requires a valid JWT
@Controller('tasks')
export class TasksController { ... }
```

Don't forget to add `AuthModule` to `AppModule`'s imports.

---

## Step 7: Role-Based Guard (RBAC) (45 min)

Create `src/auth/decorators/roles.decorator.ts`:

```typescript
import { SetMetadata } from '@nestjs/common';

// SetMetadata attaches custom data (metadata) to a route handler.
// This metadata can be read later by a guard using the Reflector service.
export const Roles = (...roles: string[]) => SetMetadata('roles', roles);

// Usage:
// @Roles('ADMIN')         → only ADMINs allowed
// @Roles('ADMIN', 'MOD')  → ADMINs and MODs allowed
// (no @Roles)             → allow any authenticated user
```

Create `src/auth/guards/roles.guard.ts`:

```typescript
import { Injectable, CanActivate, ExecutionContext } from '@nestjs/common';
import { Reflector } from '@nestjs/core';

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private reflector: Reflector) {}
  // ↑ Reflector is a NestJS utility that reads metadata attached by decorators

  canActivate(context: ExecutionContext): boolean {
    // Read the 'roles' metadata set by @Roles() decorator on the route handler
    const requiredRoles = this.reflector.get<string[]>('roles', context.getHandler());

    if (!requiredRoles) {
      return true; // No @Roles() on this route → allow any authenticated user
    }

    // Get the user from req.user (set by JwtAuthGuard → JwtStrategy.validate())
    const { user } = context.switchToHttp().getRequest();

    // Check if the user's role is in the list of required roles
    return requiredRoles.includes(user.role);
    // Returns false → NestJS throws ForbiddenException (403)
  }
}
```

Apply to the delete endpoint:

```typescript
import { UseGuards } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';

@Delete(':id')
@HttpCode(204)
@UseGuards(JwtAuthGuard, RolesGuard)
// ↑ Both guards run. Order matters: JwtAuthGuard first (set req.user), then RolesGuard (check role)
@Roles('ADMIN')
// ↑ Only ADMINs can delete tasks
delete(@Param('id') id: string) {
  this.tasksService.delete(id);
}
```

---

## Checkpoint ✓ — Milestone M2

**Bash / macOS / Git Bash:**
```bash
# Register
curl -X POST http://localhost:3000/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"secret123"}'
# Returns: { "accessToken": "eyJ..." }

# No token → 401
curl http://localhost:3000/tasks

# With token → 200
TOKEN="eyJ..."   # paste the accessToken value here
curl http://localhost:3000/tasks \
  -H "Authorization: Bearer $TOKEN"

# Create a task
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Authenticated task","description":"with JWT"}'

# Delete as USER → 403
curl -X DELETE http://localhost:3000/tasks/some-id \
  -H "Authorization: Bearer $TOKEN"
```

**Windows PowerShell:**
```powershell
# Register and capture the token automatically
$res = Invoke-RestMethod -Method Post "http://localhost:3000/auth/register" `
  -ContentType "application/json" `
  -Body '{"email":"user@test.com","password":"secret123"}'
$TOKEN = $res.accessToken
Write-Host "Token: $TOKEN"

# No token → 401
try { Invoke-RestMethod "http://localhost:3000/tasks" } catch { $_.Exception.Response.StatusCode }

# With token → 200
Invoke-RestMethod "http://localhost:3000/tasks" `
  -Headers @{ Authorization = "Bearer $TOKEN" }

# Create a task
$task = Invoke-RestMethod -Method Post "http://localhost:3000/tasks" `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $TOKEN" } `
  -Body '{"title":"Authenticated task","description":"with JWT"}'
$TASK_ID = $task.id

# Delete as USER → 403
try {
  Invoke-RestMethod -Method Delete "http://localhost:3000/tasks/$TASK_ID" `
    -Headers @{ Authorization = "Bearer $TOKEN" }
} catch { $_.Exception.Response.StatusCode }   # Should print: Forbidden
```

- [ ] `POST /auth/register` returns a JWT access token
- [ ] `GET /tasks` without a token returns 401
- [ ] `GET /tasks` with a valid token returns 200
- [ ] `DELETE /tasks/:id` as a USER-role returns 403
- [ ] Passwords are stored as bcrypt hashes (never plaintext)
- [ ] `JWT_SECRET` is in `.env` and loaded via `ConfigService`

## Optional Reading
- NestJS Authentication: https://docs.nestjs.com/security/authentication
- NestJS Authorization: https://docs.nestjs.com/security/authorization
- JWT.io (decode any JWT): https://jwt.io
- bcrypt explained: https://auth0.com/blog/hashing-passwords-one-way-road-to-security/
