# Design checklist

Questions to answer, in order, before and during the red-green-refactor loop. Each answer is a sentence or two. Answering them keeps logic testable without mocks, keeps the diff small, and puts the one risky line (the write) where a reviewer will look.

## Contents

1. Data model
2. Data flow
3. Boundaries
4. Pure core
5. Mutation at the edges
6. Edge cases and errors
7. Clean-code smells and fixes
8. SOLID, pragmatically
9. Over-engineering smells

## 1. Data model

- Which existing types carry the data for this step? Reuse them; hidden tests construct them.
- Which fields does this step read, and which does it change? Write both lists down; the second list should be short.
- What can be null, empty or missing? Follow the repo's existing answer (nullable annotations, `Optional`, `None`, `undefined`).
- Is identity by id or by value? Matters for duplicates and for "already in that state".
- Is there a primitive tangle (parallel lists, a tuple whose positions mean things, a string that encodes two values)? Only then introduce a small record or dataclass, in the same file as its first user.
- Are comparisons case-sensitive, culture-sensitive, trimmed? Decide once, in one place.

## 2. Data flow

Write the flow as one line of arrows:

```text
request -> validate -> load from repository -> decide (pure) -> save changed -> return count
```

- More than about four arrows means the step is too big; split it.
- Every arrow that crosses a boundary (load, save, call, read clock) is a candidate for an integration test; every arrow inside is unit-testable.
- The decide arrow should take only values that the load produced, plus parameters.

## 3. Boundaries

A boundary is anything whose result is not determined by the arguments alone:

| Boundary | Examples | Test with |
|---|---|---|
| Persistence | repository, DbContext, ORM session, SQL | the repo's in-memory implementation, or a hand-written fake |
| File system | read, write, list files | a temp directory |
| Network | HTTP client, queue, email | a fake client or a stub handler |
| Clock | `DateTime.Now`, `time.time()`, `Date.now()` | pass the time in as a parameter, or a fixed clock |
| Randomness / ids | `Guid.NewGuid()`, `uuid4()`, `Math.random()` | pass the value in, or a seeded source |
| Environment | env vars, config, current culture | pass the value in |

Rules of thumb:

- Pass a value in before reaching for an abstraction. A `today` parameter beats an `IClock` when only one method needs it.
- When the repo already has an interface for a boundary (`IAssetRepository`), use it and its existing fake; do not invent a parallel one.
- Never put a test double in front of your own pure code. If a pure function is hard to call from a test, its inputs are wrong.

## 4. Pure core

A pure function takes values, returns values, and does nothing else: no IO, no clock, no mutation of its arguments, no static state.

- Signature shape: `decide(current state, request) -> new state or decision`.
- Return new collections rather than modifying the ones passed in. Callers and other tests may still hold references to the input.
- Keep it in the same file as the shell, as a private static method or a small static class, until a second caller exists.
- It is the place for business rules: matching, filtering, merging, sorting, formatting, computing totals.

Examples of shape across languages:

```csharp
public static IReadOnlyList<string> Rename(IReadOnlyList<string> names, string from, string to) =>
    names.Select(name => string.Equals(name, from, StringComparison.OrdinalIgnoreCase) ? to : name).ToList();
```

```java
static List<String> rename(List<String> names, String from, String to) {
    return names.stream().map(name -> name.equalsIgnoreCase(from) ? to : name).toList();
}
```

```python
def rename(names: list[str], old: str, new: str) -> list[str]:
    return [new if name.casefold() == old.casefold() else name for name in names]
```

```ts
export const rename = (names: readonly string[], from: string, to: string): string[] =>
  names.map((name) => (name.toLowerCase() === from.toLowerCase() ? to : name));
```

```go
func rename(names []string, from, to string) []string {
	out := make([]string, len(names))
	for i, name := range names {
		if strings.EqualFold(name, from) {
			name = to
		}
		out[i] = name
	}
	return out
}
```

## 5. Mutation at the edges

The method with the given public signature is the imperative shell:

```text
guard arguments -> load -> call pure core -> if changed, save -> return
```

- One place writes. If you see `Save` or `Update` in two branches, restructure so the decision comes first and the write once.
- Compare before saving; skip the write when nothing changed. Return values like "number changed" depend on it, and needless writes cost real systems.
- Do not mutate an entity in place and rely on reference sharing to persist it. Real repositories copy or track; call the save the contract offers.
- Transactions, retries and logging belong in the shell, never in the core.
- Repositories that hand out copies (as many in-memory fakes do) will expose code that mutates without saving. That is a feature of the fake, not a bug to work around.

## 6. Edge cases and errors

Decide each of these per step; test the ones the task names or implies:

- empty input, null or missing value, whitespace-only string
- duplicates, case variants, values equal after trimming
- boundaries: 0, 1, exactly at the limit, one past it
- the operation applied to something already in the target state (merge into itself, archive an archived item)
- the referenced thing does not exist

Error handling patterns, matching what the repo already does:

| Language | Invalid argument | Not found | Notes |
|---|---|---|---|
| C# | `ArgumentException`, `ArgumentNullException`, `ArgumentOutOfRangeException` with `nameof(param)` | `KeyNotFoundException` or the repo's own, or `null` / `bool TryX(out ...)` if neighbors do | `ArgumentException.ThrowIfNullOrWhiteSpace(x)` exists on .NET 8+ |
| Java | `IllegalArgumentException`, `Objects.requireNonNull` | `Optional.empty()` or a domain exception | do not return `null` from a method that returns `Optional` |
| Python | `ValueError`, `TypeError` | `KeyError`, `LookupError` or `None` per neighbors | do not catch broad `Exception` to hide a bug |
| TypeScript | `throw new RangeError` / `TypeError`, or a result type if the repo uses one | `undefined` or a result type | match the repo: throwing vs returning errors |
| Go | `return fmt.Errorf("...: %w", err)` or a sentinel `errors.New` | sentinel `ErrNotFound`, check with `errors.Is` | never panic for expected input |

Guidelines:

- Guard clauses go first in the shell, before any load or write, so an invalid call changes nothing.
- Use the exact exception type the task or neighbors use; hidden tests often assert the type.
- Error messages name the problem and the value; they never cite a ticket or spec ID.
- Do not swallow an exception to make a test pass.

## 7. Clean-code smells and fixes

| Smell | Fix |
|---|---|
| Logic and IO interleaved in one loop | Decide first in a pure function, then write in a separate loop |
| Mutating an argument | Return a new value |
| Boolean flag parameter that switches behavior | Two well-named functions, or a small enum if the repo uses them |
| Same expression in three places | Extract a named function; two places can stay |
| Magic number or string | Named constant next to its use |
| Deep nesting | Guard clauses and early returns |
| Long method (more than a screen) | Extract the pure parts first; the shell stays short |
| Name says what, not why (`list2`, `temp`, `data`) | Name by role: `expired`, `merged`, `toSave` |
| Comment explaining a block | Extract the block into a function whose name says it |
| `catch` that logs and continues | Let it propagate, or handle the specific exception |
| Culture-sensitive compare by accident | Explicit ordinal or case-insensitive compare |
| Repeated enumeration or query inside a loop | Load once, build a lookup |

## 8. SOLID, pragmatically

Apply a principle when it removes a real cost now; skip it when it only adds a file.

- **Single responsibility**: the useful form is "decisions apart from IO", which the pure core already gives. Do not split a 30-line class into four.
- **Open/closed**: extend through a new branch or a new function until there are three or more variants that change independently. Then consider polymorphism.
- **Liskov**: if you implement an existing interface, honor its contract (same exceptions, same null handling, same ordering).
- **Interface segregation**: when adding to an existing interface, add only what the caller needs. Do not create a new interface for one caller.
- **Dependency inversion**: depend on the abstraction the repo already offers for a boundary. Do not create abstractions for pure code or for a boundary that a parameter can replace.

## 9. Over-engineering smells

Remove these before showing the diff:

- An interface with one implementation and no test that needs the seam.
- A factory, strategy, builder, visitor or registry for one or two cases.
- A new project, package, folder or file for a helper with one caller.
- Configuration, options classes or dependency-injection registration for a constant.
- Generic type parameters used with one type.
- A "utility" module of functions nobody calls yet.
- Defensive checks for states the type system already rules out.
- Rewrites of code outside the step "while I was there".

A useful test: could a reviewer delete this layer and lose nothing but lines? Then delete it.
