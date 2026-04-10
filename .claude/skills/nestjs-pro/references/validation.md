# NestJS Validation

## Purpose

DTO design, ValidationPipe configuration, class-validator decorators, nested object validation, and mapped types for reducing boilerplate.

## Table of Contents

- [ValidationPipe Setup](#validationpipe-setup)
- [DTO with class-validator](#dto-with-class-validator)
- [Nested Object Validation](#nested-object-validation)
- [Mapped Types](#mapped-types)
- [Swagger Parity](#swagger-parity)
- [DO / DON'T](#do--dont)
- [Related Documents](#related-documents)

---

## ValidationPipe Setup

Always configure globally in `main.ts` with these four options:

```typescript
// main.ts
import { ValidationPipe } from '@nestjs/common';

app.useGlobalPipes(
  new ValidationPipe({
    whitelist: true,              // silently strip unknown properties
    forbidNonWhitelisted: true,   // throw 400 when unknown properties are present
    transform: true,              // coerce plain JS objects → DTO class instances
    transformOptions: {
      enableImplicitConversion: true, // convert query param strings to number/boolean
    },
  }),
);
```

**`transform: true` is required.** Without it, `@Type(() => AddressDto)` decorators have no effect and nested objects remain as plain objects — validators never run on them.

---

## DTO with class-validator

```typescript
// users/dto/create-user.dto.ts
import {
  IsEmail, IsString, IsOptional, MinLength, MaxLength,
  IsEnum, IsNotEmpty,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export enum UserRole {
  ADMIN = 'admin',
  USER = 'user',
}

export class CreateUserDto {
  @ApiProperty({ example: 'alice@example.com' })
  @IsEmail()
  @IsNotEmpty()
  email: string;

  @ApiProperty({ example: 'StrongP@ss1', minLength: 8, maxLength: 64 })
  @IsString()
  @MinLength(8)
  @MaxLength(64)
  password: string;

  @ApiProperty({ enum: UserRole, default: UserRole.USER })
  @IsEnum(UserRole)
  role: UserRole = UserRole.USER;

  @ApiPropertyOptional({ example: 'Alice' })
  @IsString()
  @IsOptional()
  displayName?: string;
}
```

Common class-validator decorators:

| Decorator | Validates |
|-----------|-----------|
| `@IsString()` | string |
| `@IsInt()` | integer |
| `@IsNumber()` | float or integer |
| `@IsBoolean()` | boolean |
| `@IsEmail()` | RFC-valid email |
| `@IsUUID()` | UUID v4 |
| `@IsUrl()` | URL |
| `@IsEnum(Enum)` | value is in enum |
| `@IsNotEmpty()` | not empty string/array |
| `@IsOptional()` | allow undefined/null (skip validation) |
| `@Min(n)` / `@Max(n)` | numeric range |
| `@MinLength(n)` / `@MaxLength(n)` | string length |
| `@IsArray()` | array |
| `@ArrayMinSize(n)` | minimum array items |
| `@ValidateNested()` | validate nested DTO |
| `@Type(() => Class)` | coerce to class instance (requires transform: true) |

---

## Nested Object Validation

`@ValidateNested()` alone is not enough — you must also use `@Type(() => NestedDto)` so `class-transformer` instantiates the nested class before validators run.

```typescript
// users/dto/address.dto.ts
import { IsString, IsPostalCode } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class AddressDto {
  @ApiProperty() @IsString() street: string;
  @ApiProperty() @IsString() city: string;
  @ApiProperty() @IsPostalCode('any') postalCode: string;
}

// users/dto/create-user.dto.ts
import { ValidateNested, IsArray, ArrayMinSize } from 'class-validator';
import { Type } from 'class-transformer';
import { AddressDto } from './address.dto';

export class CreateUserDto {
  // ... other fields

  @ApiProperty({ type: AddressDto })
  @ValidateNested()
  @Type(() => AddressDto)
  address: AddressDto;

  @ApiProperty({ type: [AddressDto] })
  @IsArray()
  @ArrayMinSize(1)
  @ValidateNested({ each: true })
  @Type(() => AddressDto)
  shippingAddresses: AddressDto[];
}
```

---

## Mapped Types

Use `@nestjs/mapped-types` to derive update/partial DTOs from create DTOs without repeating decorators.

```typescript
import { PartialType, PickType, OmitType, IntersectionType } from '@nestjs/mapped-types';

// All fields optional — typical for PATCH endpoints
export class UpdateUserDto extends PartialType(CreateUserDto) {}

// Only specific fields — for sub-resource forms
export class LoginDto extends PickType(CreateUserDto, ['email', 'password'] as const) {}

// Remove sensitive fields — for public-facing DTOs
export class PublicCreateUserDto extends OmitType(CreateUserDto, ['password'] as const) {}

// Combine two DTOs
export class AdminCreateUserDto extends IntersectionType(CreateUserDto, AdminFlagsDto) {}
```

`PartialType` / `PickType` / `OmitType` / `IntersectionType` all preserve class-validator decorators from the source DTO.

---

## Swagger Parity

Add `@ApiProperty()` to every DTO field so Swagger documentation stays in sync with validation rules.

```typescript
@ApiProperty({
  description: 'User email address',
  example: 'alice@example.com',
  format: 'email',
})
@IsEmail()
email: string;

@ApiPropertyOptional({
  description: 'Optional display name',
  minLength: 2,
  maxLength: 50,
})
@IsOptional()
@IsString()
displayName?: string;
```

Install `@nestjs/swagger` and use `SwaggerModule.setup('api', app, document)` in `main.ts` to generate the interactive spec.

---

## DO / DON'T

| DO | DON'T |
|----|-------|
| Set `whitelist: true` and `forbidNonWhitelisted: true` globally | Accept unconstrained request bodies |
| Set `transform: true` — it is required for `@Type()` to work | Omit `transform: true` and wonder why nested validation fails |
| Use `@IsOptional()` instead of marking a property `?` — validators still run on present values | Use `?` alone and expect validators to skip the field |
| Derive update DTOs with `PartialType(CreateDto)` | Copy-paste create DTO and add `?` to all fields manually |
| Add `@ApiProperty()` on every field | Let Swagger show `{}` for request bodies |

---

## Related Documents

- `references/gipf.md` — Binding ValidationPipe globally
- `references/exceptions.md` — 400 responses from validation failures
- `references/controllers.md` — Typing `@Body()` to a DTO
- `references/testing.md` — Testing validation in unit and E2E tests
