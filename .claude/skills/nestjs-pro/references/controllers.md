# NestJS Controllers

## Purpose

Controller patterns — route decorators, parameter extraction, versioning, response serialization, and the `@Res()` footgun.

## Table of Contents

- [Route Decorators](#route-decorators)
- [Parameter Decorators](#parameter-decorators)
- [The @Res() Footgun](#the-res-footgun)
- [URI Versioning](#uri-versioning)
- [Response Serialization](#response-serialization)
- [DO / DON'T](#do--dont)
- [Related Documents](#related-documents)

---

## Route Decorators

```typescript
@Controller('articles')
export class ArticlesController {
  @Get()           findAll() { ... }
  @Get(':id')      findOne(@Param('id') id: string) { ... }
  @Post()          create(@Body() dto: CreateArticleDto) { ... }
  @Put(':id')      replace(@Param('id') id: string, @Body() dto: UpdateArticleDto) { ... }
  @Patch(':id')    update(@Param('id') id: string, @Body() dto: PartialUpdateArticleDto) { ... }
  @Delete(':id')   remove(@Param('id') id: string) { ... }
}
```

- Prefer `@Patch` for partial updates, `@Put` for full replacement.
- Route prefix in `@Controller('articles')` is relative — no leading slash needed.

---

## Parameter Decorators

| Decorator | Extracts |
|-----------|----------|
| `@Param('id')` | Named route param |
| `@Query('page')` | Query string param |
| `@Body()` | Full request body (typed to DTO) |
| `@Body('field')` | Single body field |
| `@Headers('authorization')` | Specific header value |
| `@Req()` | Raw Express `Request` object |
| `@Res({ passthrough: true })` | Raw Express `Response` — passthrough mode only |
| `@Ip()` | Client IP address |
| `@HostParam()` | Sub-domain param from `@Controller({ host: ':account.example.com' })` |

```typescript
@Get()
findAll(
  @Query('page') page = 1,
  @Query('limit') limit = 20,
  @Headers('x-tenant-id') tenantId: string,
) {
  return this.articlesService.findAll({ page, limit, tenantId });
}
```

---

## The @Res() Footgun

`@Res()` without `passthrough: true` hands full response control to you. NestJS interceptors, exception filters, and serialization all stop working.

```typescript
// WRONG — interceptors and exception filters are bypassed
@Get(':id')
findOne(@Param('id') id: string, @Res() res: Response) {
  const article = this.service.findOne(id);
  res.status(200).json(article);
}

// CORRECT — passthrough keeps the NestJS pipeline intact
@Get(':id')
findOne(@Param('id') id: string, @Res({ passthrough: true }) res: Response) {
  res.setHeader('x-custom', 'value');
  return this.service.findOne(id); // return value is still processed by interceptors
}
```

Use `@Res({ passthrough: true })` only when you genuinely need to set headers or cookies. Otherwise, avoid `@Res` entirely.

---

## URI Versioning

Enable once in `main.ts`, then annotate each controller (or individual routes) with `@Version`.

```typescript
// main.ts
import { VersioningType } from '@nestjs/common';

app.enableVersioning({ type: VersioningType.URI });

// articles-v1.controller.ts
@Controller({ path: 'articles', version: '1' })
export class ArticlesV1Controller {
  @Get()
  findAll() { ... }
}

// articles-v2.controller.ts
@Controller({ path: 'articles', version: '2' })
export class ArticlesV2Controller {
  @Get()
  findAll() { ... }  // new shape, backward-compatible
}
```

Resulting routes: `GET /v1/articles`, `GET /v2/articles`.

Other versioning types: `HEADER` (custom header), `MEDIA_TYPE` (Accept header), `CUSTOM` (user-supplied extractor).

---

## Response Serialization

Use `ClassSerializerInterceptor` with `@Exclude()` / `@Expose()` on entity/DTO classes to strip sensitive fields from responses automatically.

```typescript
// main.ts — enable globally
app.useGlobalInterceptors(new ClassSerializerInterceptor(app.get(Reflector)));

// user.entity.ts
import { Exclude, Expose } from 'class-transformer';

export class UserEntity {
  @Expose() id: string;
  @Expose() email: string;

  @Exclude()        // never sent in any response
  passwordHash: string;

  constructor(partial: Partial<UserEntity>) {
    Object.assign(this, partial);
  }
}

// users.controller.ts
@Get(':id')
async findOne(@Param('id') id: string): Promise<UserEntity> {
  const raw = await this.usersService.findOne(id);
  return new UserEntity(raw); // class-transformer picks up @Exclude/@Expose
}
```

- `@SerializeOptions({ strategy: 'excludeAll' })` flips the default — only `@Expose()` fields are included.
- Wrap raw Prisma results in entity classes before returning from the controller.

---

## DO / DON'T

| DO | DON'T |
|----|-------|
| Type `@Body()` to a DTO class | Accept `@Body() body: any` |
| Use `@Res({ passthrough: true })` when you need the response object | Use `@Res()` without `passthrough: true` |
| Enable versioning in `main.ts` once, apply `@Version` per controller | Duplicate version prefixes in route strings |
| Return entity/DTO instances for serialization | Return raw Prisma objects with sensitive fields |
| Keep controllers thin — delegate all logic to services | Put business logic or DB calls in controllers |

---

## Related Documents

- `references/modules.md` — Registering controllers in a module
- `references/validation.md` — DTO validation with ValidationPipe
- `references/gipf.md` — Binding interceptors and guards to controllers
- `references/exceptions.md` — Throwing and catching HTTP exceptions
