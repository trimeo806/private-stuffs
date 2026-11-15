# Framework snippets

Read only the section for the framework the repo already uses. Each section gives the detection signals, the parametrized shape, the exception assertion and the run command. Match the existing tests' style over these snippets when they differ.

- [C# xUnit](#c-xunit)
- [C# NUnit](#c-nunit)
- [Java JUnit 5](#java-junit-5)
- [Python pytest](#python-pytest)
- [TypeScript / JavaScript Jest or Vitest](#typescript--javascript-jest-or-vitest)
- [Go testing](#go-testing)

## C# xUnit

Detect: test `.csproj` references `xunit` and `xunit.runner.visualstudio`; tests use `[Fact]` / `[Theory]`. Check for `FluentAssertions` or `Shouldly` and use them if present.

```csharp
using Xunit;

namespace Library.Tests;

public class PagerTests
{
    [Theory]
    [InlineData(0)]
    [InlineData(-1)]
    public void Throws_when_page_is_below_one(int page)
    {
        var ex = Assert.Throws<ArgumentOutOfRangeException>(() => Pager.GetPage(Array.Empty<int>(), page, 10));
        Assert.Equal("page", ex.ParamName);
    }

    public static TheoryData<int, int, int> TotalPagesCases => new()
    {
        { 0, 10, 0 },
        { 10, 10, 1 },
        { 11, 10, 2 },
    };

    [Theory]
    [MemberData(nameof(TotalPagesCases))]
    public void Computes_total_pages(int count, int pageSize, int expected)
    {
        var result = Pager.GetPage(Enumerable.Range(1, count).ToList(), 1, pageSize);
        Assert.Equal(expected, result.TotalPages);
    }
}
```

- `[InlineData]` takes compile-time constants only; use `[MemberData]` or `TheoryData<>` for lists and objects.
- `Assert.Throws<T>` is exact-type; `Assert.ThrowsAny<T>` accepts subclasses. Prefer exact.
- `Assert.Equal(expected, actual)`: expected first, or failure messages read backwards.
- Run: `dotnet test` (or `dotnet test <Tests.csproj> --filter "FullyQualifiedName~PagerTests"`).

## C# NUnit

Detect: `.csproj` references `NUnit` and `NUnit3TestAdapter`; tests use `[Test]` / `[TestCase]`.

```csharp
using NUnit.Framework;

namespace Library.Tests;

public class PagerTests
{
    [TestCase(0)]
    [TestCase(-1)]
    public void Throws_when_page_is_below_one(int page)
    {
        Assert.That(() => Pager.GetPage(Array.Empty<int>(), page, 10), Throws.TypeOf<ArgumentOutOfRangeException>());
    }

    [TestCase(0, 10, ExpectedResult = 0)]
    [TestCase(11, 10, ExpectedResult = 2)]
    public int Computes_total_pages(int count, int pageSize)
    {
        return Pager.GetPage(Enumerable.Range(1, count).ToList(), 1, pageSize).TotalPages;
    }
}
```

- `[TestCaseSource(nameof(Cases))]` for non-constant data.
- Run: `dotnet test`.

## Java JUnit 5

Detect: `pom.xml` or `build.gradle` has `junit-jupiter`; tests use `@Test`. `junit-jupiter-params` is needed for `@ParameterizedTest` (it ships with the `junit-jupiter` aggregate). Check for AssertJ.

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PagerTest {

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("throws when page is below one")
    void throwsWhenPageIsBelowOne(int page) {
        assertThrows(IllegalArgumentException.class, () -> Pager.getPage(List.of(), page, 10));
    }

    @ParameterizedTest(name = "{0} items at size {1} -> {2} pages")
    @CsvSource({"0, 10, 0", "10, 10, 1", "11, 10, 2"})
    void computesTotalPages(int count, int pageSize, int expected) {
        assertEquals(expected, Pager.getPage(Pager.range(count), 1, pageSize).totalPages());
    }
}
```

- `@NullAndEmptySource` covers null and empty strings or collections in one line.
- `@MethodSource("cases")` for objects.
- Run: `mvn test -Dtest=PagerTest` or `./gradlew test --tests PagerTest`.

## Python pytest

Detect: `pyproject.toml` `[tool.pytest.ini_options]`, `pytest.ini`, `conftest.py`, files named `test_*.py`. If the repo uses `unittest.TestCase`, keep that style and use `subTest` for tables.

```python
import pytest

from pager import get_page


@pytest.mark.parametrize("page", [0, -1], ids=["zero", "negative"])
def test_rejects_page_below_one(page):
    with pytest.raises(ValueError, match="page"):
        get_page([], page, 10)


@pytest.mark.parametrize(
    ("count", "page_size", "expected"),
    [(0, 10, 0), (10, 10, 1), (11, 10, 2)],
    ids=["empty", "exact-multiple", "partial-last-page"],
)
def test_computes_total_pages(count, page_size, expected):
    assert get_page(list(range(count)), 1, page_size).total_pages == expected
```

- `ids=` makes failures readable; use behavior words, not numbers.
- `pytest.approx` for floats.
- Run: `pytest tests/test_pager.py -q`, then `pytest -q`.

## TypeScript / JavaScript Jest or Vitest

Detect: `package.json` devDependencies (`jest`, `ts-jest`, `vitest`), `jest.config.*` or `vitest.config.*`, files `*.test.ts` / `*.spec.ts`. Vitest needs `import { describe, it, expect } from "vitest"` unless `globals: true` is set; Jest has globals.

```ts
import { describe, expect, it } from "vitest";
import { getPage } from "./pager";

describe("getPage", () => {
  it.each([0, -1])("rejects page %i", (page) => {
    expect(() => getPage([], page, 10)).toThrow(RangeError);
  });

  it.each([
    { count: 0, pageSize: 10, expected: 0 },
    { count: 10, pageSize: 10, expected: 1 },
    { count: 11, pageSize: 10, expected: 2 },
  ])("returns $expected pages for $count items at size $pageSize", ({ count, pageSize, expected }) => {
    const items = Array.from({ length: count }, (_, i) => i);
    expect(getPage(items, 1, pageSize).totalPages).toBe(expected);
  });
});
```

- `toBe` for primitives, `toEqual` for objects and arrays, `toStrictEqual` when `undefined` fields matter.
- Async: `await expect(fn()).rejects.toThrow(...)`.
- Run: `npx vitest run src/pager.test.ts` or `npx jest src/pager.test.ts`, or the repo's `npm test`.

## Go testing

Detect: `go.mod`, files `*_test.go` beside the code. Check for `testify` in `go.mod`.

```go
package pager

import (
	"errors"
	"testing"
)

func TestTotalPages(t *testing.T) {
	cases := []struct {
		name     string
		count    int
		pageSize int
		want     int
	}{
		{"empty", 0, 10, 0},
		{"exact multiple", 10, 10, 1},
		{"partial last page", 11, 10, 2},
	}
	for _, tc := range cases {
		t.Run(tc.name, func(t *testing.T) {
			got, err := GetPage(make([]int, tc.count), 1, tc.pageSize)
			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}
			if got.TotalPages != tc.want {
				t.Errorf("TotalPages = %d, want %d", got.TotalPages, tc.want)
			}
		})
	}
}

func TestRejectsPageBelowOne(t *testing.T) {
	_, err := GetPage(nil, 0, 10)
	if !errors.Is(err, ErrInvalidPage) {
		t.Fatalf("err = %v, want ErrInvalidPage", err)
	}
}
```

- Go reports errors as values; assert with `errors.Is` / `errors.As`, not string matching.
- Run: `go test ./... -run TestTotalPages -v`.
