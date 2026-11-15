# Testing patterns

How to write the red test in each loop iteration so it describes behavior, fails for the right reason, and runs in the project's own framework.

## Contents

1. Naming by behavior
2. Unit or integration
3. Test doubles only at boundaries
4. Red for the right reason
5. Framework detection and commands
6. xUnit (C#)
7. NUnit (C#)
8. JUnit 5 (Java)
9. pytest (Python)
10. Jest and Vitest (TypeScript)
11. Go testing

## 1. Naming by behavior

A test name is a sentence about what the system does, readable by someone who has never seen the ticket.

- Good: `Archiving_an_archived_asset_changes_nothing`, `returns_empty_page_past_the_end`, `it("serves public assets from the cdn")`.
- Bad: `Test1`, `ArchiveWorks`, `AC2_archive`, `test_bug_1234`, `should work correctly`.
- Follow the repo's casing (`Method_does_x_when_y`, `doesXWhenY`, `test_does_x_when_y`, `it("does x when y")`).
- One behavior per test. If the name needs "and" twice, split it.
- Names never cite plan, ticket, task or spec IDs. The mapping from requirement to test belongs in notes, not in code.

## 2. Unit or integration

| Code under test | Test kind | Setup |
|---|---|---|
| Pure function (rule, formatter, filter, merge) | Unit | Values in, assert the value out. No fakes. |
| Public method that loads, decides and saves | Integration at the boundary | Real in-memory repository or fake that the repo already has, then assert stored state and return value |
| Guard clause on a public method | Unit through the public method | Assert exception type, and that nothing was saved if cheap to check |
| Real database, file system or HTTP | Integration | Only when the repo already has that harness; otherwise the in-memory fake |

Typical step: one or two unit tests on the pure core, one integration test through the public method. Unit tests catch the edge cases cheaply; the integration test proves the shell actually persists the result.

Assert observable behavior: return values, stored state, thrown exception type. Do not assert private methods, call counts on your own code, or internal ordering that the contract does not promise.

## 3. Test doubles only at boundaries

- Prefer the repo's existing fake (`InMemoryAssetRepository`, a fake HTTP handler) over a mocking library. Fakes keep tests about state, not about calls.
- If no fake exists and the boundary is a small interface, write a hand-rolled fake in the test project.
- Use a mocking library only if the repo already uses one, and only for a boundary.
- Never mock the pure core or the class under test. If you feel the need, move logic out of the shell into a pure function.
- Pass clock, ids and randomness as values in tests; fixed dates make tests deterministic.

## 4. Red for the right reason

Run the new test before writing production code and read the failure:

| Failure seen | Meaning | Action |
|---|---|---|
| Assertion: expected X, got Y | Right reason | Write the production change |
| `NotImplementedException` / `UnsupportedOperationException` / `NotImplementedError` from the stub | Right reason | Write the production change |
| Compile error, import error, typo | Wrong reason | Add a stub, fix the test, run again |
| Test passes already | The behavior exists, or the test is too weak | Strengthen the assertion or pick the next behavior |
| Different test now fails | Shared state or a baseline failure | Check the baseline run; isolate state |

For a bug, the red message should show the reported symptom (`expected TotalPages 3, got 2`). If it shows something else, you have not reproduced the bug yet.

## 5. Framework detection and commands

| Signal | Framework | Run all | Run one |
|---|---|---|---|
| `xunit` in `*.csproj` | xUnit | `dotnet test` | `dotnet test --filter "FullyQualifiedName~ReplaceKeyword"` |
| `NUnit` in `*.csproj` | NUnit | `dotnet test` | `dotnet test --filter "Name~Replace"` |
| `junit-jupiter` in `pom.xml` / `build.gradle` | JUnit 5 | `mvn test` / `./gradlew test` | `mvn test -Dtest=KeywordServiceTest#replacesKeyword` / `./gradlew test --tests '*KeywordServiceTest.replaces*'` |
| `pytest` in `pyproject.toml`, `conftest.py` | pytest | `python -m pytest` | `python -m pytest tests/test_x.py::test_name` or `-k name` |
| `jest` in `package.json` | Jest | `npx jest` | `npx jest path -t "name"` |
| `vitest` in `package.json` | Vitest | `npx vitest run` | `npx vitest run path -t "name"` |
| `*_test.go`, `go.mod` | Go testing | `go test ./...` | `go test ./pkg -run TestName` |

When the solution has several projects and no solution file at the root, pass the test project path to `dotnet test`.

## 6. xUnit (C#)

```csharp
using Xunit;

namespace AssetLibrary.Tests;

public class KeywordReplaceTests
{
    [Fact]
    public void Replaces_the_keyword_with_its_replacement()
    {
        var replaced = KeywordReplace.Apply(new[] { "logo", "print" }, "logo", "brand");

        Assert.Equal(new[] { "brand", "print" }, replaced);
    }

    [Theory]
    [InlineData("")]
    [InlineData("   ")]
    public void Rejects_a_blank_keyword(string source)
    {
        var service = new KeywordService(new InMemoryAssetRepository());

        var error = Assert.Throws<ArgumentException>(() => service.ReplaceKeyword(source, "brand"));
        Assert.Equal("keyword", error.ParamName);
    }
}
```

- `Assert.Throws<T>` is exact-type; `Assert.ThrowsAny<T>` accepts subclasses.
- `[MemberData]` for cases that need objects; `[InlineData]` for constants.
- Collection equality: `Assert.Equal(expectedArray, actualEnumerable)` compares in order.

## 7. NUnit (C#)

```csharp
using NUnit.Framework;

[TestFixture]
public class KeywordReplaceTests
{
    [Test]
    public void Replaces_the_keyword_with_its_replacement()
    {
        var replaced = KeywordReplace.Apply(new[] { "logo", "print" }, "logo", "brand");

        Assert.That(replaced, Is.EqualTo(new[] { "brand", "print" }));
    }

    [TestCase("")]
    [TestCase("   ")]
    public void Rejects_a_blank_keyword(string source)
    {
        var service = new KeywordService(new InMemoryAssetRepository());

        Assert.Throws<ArgumentException>(() => service.ReplaceKeyword(source, "brand"));
    }
}
```

## 8. JUnit 5 (Java)

```java
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class KeywordReplaceTest {

    @Test
    void replacesTheKeywordWithItsReplacement() {
        var replaced = KeywordReplace.apply(List.of("logo", "print"), "logo", "brand");

        assertThat(replaced).containsExactly("brand", "print");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void rejectsABlankKeyword(String source) {
        var service = new KeywordService(new InMemoryAssetRepository());

        assertThrows(IllegalArgumentException.class, () -> service.replaceKeyword(source, "brand"));
    }
}
```

- Use AssertJ only if the project already depends on it; otherwise `assertEquals(List.of(...), replaced)`.
- `@DisplayName` text follows the same behavior-naming rule.

## 9. pytest (Python)

```python
import pytest

from keywords import replace_keyword, KeywordService
from repository import InMemoryAssetRepository


def test_replaces_the_keyword_with_its_replacement():
    assert replace_keyword(["logo", "print"], "logo", "brand") == ["brand", "print"]


@pytest.mark.parametrize("source", ["", "   "], ids=["empty", "whitespace"])
def test_rejects_a_blank_keyword(source):
    service = KeywordService(InMemoryAssetRepository())

    with pytest.raises(ValueError):
        service.replace_keyword(source, "brand")
```

- `pytest.raises(ValueError, match="...")` when the message is part of the contract.
- Use `tmp_path` for file system boundaries and `monkeypatch` for environment variables.
- Run with `python -B -m pytest -p no:cacheprovider` when the environment should stay free of cache folders.

## 10. Jest and Vitest (TypeScript)

```ts
import { describe, expect, it } from "vitest";
import { replaceKeyword } from "./keywords";

describe("replaceKeyword", () => {
  it("replaces the keyword with its replacement", () => {
    expect(replaceKeyword(["logo", "print"], "logo", "brand")).toEqual(["brand", "print"]);
  });

  it.each(["", "   "])("rejects a blank keyword %j", (source) => {
    expect(() => replaceKeyword(["logo"], source, "brand")).toThrow(RangeError);
  });
});
```

- Jest uses the same `describe`, `it`, `expect`; drop the `vitest` import if the repo uses Jest globals.
- `toEqual` for values, `toBe` for primitives and identity, `toThrow(ErrorClass)` for the type.
- For async code: `await expect(promise).rejects.toThrow(...)`.

## 11. Go testing

```go
package keywords

import (
	"errors"
	"reflect"
	"testing"
)

func TestReplaceSwapsTheKeyword(t *testing.T) {
	got := Replace([]string{"logo", "print"}, "logo", "brand")
	want := []string{"brand", "print"}
	if !reflect.DeepEqual(got, want) {
		t.Fatalf("Replace() = %v, want %v", got, want)
	}
}

func TestReplaceKeywordRejectsBlankKeywords(t *testing.T) {
	tests := []struct {
		name   string
		source string
	}{
		{"empty", ""},
		{"whitespace", "   "},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			_, err := NewService(NewMemoryRepository()).ReplaceKeyword(tt.source, "brand")
			if !errors.Is(err, ErrBlankKeyword) {
				t.Fatalf("err = %v, want ErrBlankKeyword", err)
			}
		})
	}
}
```

- Table-driven tests with `t.Run` are the idiom for parametrized cases.
- Compare errors with `errors.Is` against a sentinel, not by message text.
