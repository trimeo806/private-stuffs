# Framework notes

Read only the section for the framework the repo already uses. When the existing tests' style differs from these notes, follow the existing tests.

- [C# xUnit](#c-xunit)
- [C# NUnit](#c-nunit)
- [Java JUnit 5](#java-junit-5)
- [Python pytest](#python-pytest)
- [Jest or Vitest](#jest-or-vitest)
- [Go testing](#go-testing)

## C# xUnit

- Detect: test `.csproj` references `xunit`; tests use `[Fact]` / `[Theory]`. Use `FluentAssertions` or `Shouldly` only if already referenced.
- Parametrize: `[Theory]` + `[InlineData(...)]` for compile-time constants (`int.MaxValue` is fine); `[MemberData(nameof(Cases))]` with `TheoryData<...>` for lists and objects.
- Exceptions: `var ex = Assert.Throws<ArgumentOutOfRangeException>(() => ...); Assert.Equal("page", ex.ParamName);`. `Throws` is exact-type, `ThrowsAny` accepts subclasses; prefer exact.
- `Assert.Equal(expected, actual)`: expected first, or failure messages read backwards.
- Run: `dotnet test <Tests.csproj> --filter "FullyQualifiedName~<Class>"`, then `dotnet test`.

## C# NUnit

- Detect: `.csproj` references `NUnit` and `NUnit3TestAdapter`; tests use `[Test]` / `[TestCase]`.
- Parametrize: `[TestCase(11, 10, ExpectedResult = 2)]` on a method that returns the value; `[TestCaseSource(nameof(Cases))]` for objects.
- Exceptions: `Assert.That(() => ..., Throws.TypeOf<ArgumentOutOfRangeException>().With.Property("ParamName").EqualTo("page"));`.
- Run: `dotnet test`.

## Java JUnit 5

- Detect: `junit-jupiter` in `pom.xml` / `build.gradle`; tests use `@Test`. Use AssertJ only if already present.
- Parametrize: `@ParameterizedTest` with `@CsvSource({"0, 10, 0", "11, 10, 2"})`, `@ValueSource(ints = {0, -1})`, `@NullAndEmptySource`, or `@MethodSource("cases")` for objects. Needs `junit-jupiter-params` (included in the `junit-jupiter` aggregate; check before relying on it).
- Exceptions: `assertThrows(IllegalArgumentException.class, () -> ...)`.
- Names: camelCase method plus `@DisplayName("throws when page is below one")` if the repo uses it.
- Run: `mvn test -Dtest=<Class>` or `./gradlew test --tests <Class>`.

## Python pytest

- Detect: `[tool.pytest.ini_options]` in `pyproject.toml`, `pytest.ini`, `conftest.py`, `test_*.py`. If the repo uses `unittest.TestCase`, keep it and use `subTest` for tables.
- Parametrize: `@pytest.mark.parametrize(("name", "expected"), [...], ids=[...])`; `ids` in behavior words make failures readable.
- Exceptions: `with pytest.raises(ValueError, match="size"):`.
- `pytest.approx` for floats. Ints never overflow, so large-value rows test performance or totals, not wrap-around.
- Run: `pytest tests/<file> -q`, then `pytest -q`. If pytest is not installed, `uv run --with pytest pytest -q` adds nothing to the project.

## Jest or Vitest

- Detect: `jest`, `ts-jest` or `vitest` in `package.json` devDependencies, `jest.config.*` / `vitest.config.*`, `*.test.ts` / `*.spec.ts`.
- Vitest needs `import { describe, expect, it } from "vitest"` unless `globals: true`; Jest has globals.
- Parametrize: `it.each([{ input: "", expected: "" }])("returns $expected for $input", ({ input, expected }) => ...)`.
- Exceptions: `expect(() => fn()).toThrow(RangeError)`; async: `await expect(fn()).rejects.toThrow(RangeError)`.
- `toBe` for primitives, `toEqual` for objects and arrays. Numbers lose integer precision past 2^53 (`Number.MAX_SAFE_INTEGER`).
- Run: `npx vitest run <file>` or `npx jest <file>`, or the repo's `npm test`.

## Go testing

- Detect: `go.mod`, `*_test.go` beside the code. Use `testify` only if it is in `go.mod`.
- Parametrize: table-driven, `cases := []struct{ name string; ... }{...}` with `t.Run(tc.name, ...)`.
- Errors are values: assert with `errors.Is(err, ErrInvalidPage)` / `errors.As`, not string matching.
- `int` is 64-bit on most targets; overflow rows matter for `int32` fields and products of large values.
- Run: `go test ./... -run <TestName> -v`.
