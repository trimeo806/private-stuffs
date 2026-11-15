# Edge-case catalogue

Read this during step 5 of the recon. Pick only the rows that match the task's input and output types; a recon that lists every row for every task buries the five cases that matter. For each case you keep, write the expected behaviour and the requirement it serves.

Contents:

1. Where hidden tests come from
2. By input type
3. By task shape
4. Per-language pitfalls

## 1. Where hidden tests come from

Hidden tests in assessments are usually written from the task statement and mirror the visible tests. Read these signals first:

- Every example in the task statement is almost certainly a test. Restate each one as an edge-case row.
- Boundary words: "at most", "at least", "between", "up to", "inclusive", "non-empty", "unique", "case-insensitive", "in order", "must not", "only". Each gives a test on both sides of the boundary.
- Named errors: "throw", "return null", "return an error", "reject", "ignore", "skip". Each gives a test that triggers it and checks the exact type or value.
- Visible test names and structure: if visible tests cover the happy path of method A, hidden ones usually cover A's failure path and the same paths for the new method.
- Existing behaviour the task does not mention: hidden tests often check the old behaviour still works (regression), so changing it breaks credit you already had.

## 2. By input type

| Input | Cases to consider |
|---|---|
| Collection | empty, one item, two items, duplicates, all identical, already sorted, reverse sorted, very large (performance), contains null items |
| String | empty, whitespace only, leading or trailing spaces, mixed case, unicode and accents, emoji, very long, separator characters inside the value |
| Integer | zero, negative, one, max and min of the type (overflow), off-by-one around every limit in the task |
| Decimal or money | rounding mode, more decimals than the currency allows, negative amounts, zero, sums that must stay exact (no binary floating point) |
| Date and time | time zones and UTC, daylight-saving change, end of month, leap day, start equals end, end before start |
| Optional or nullable | null, missing field, default value, empty versus null (often treated differently) |
| Identifier or key | not found, found twice, different case, leading zeros, id of a deleted item |
| File or path | missing file, empty file, header only, trailing blank line, different line endings, path with spaces, very large file |
| Enum or status | every value, an unknown value, transitions that should be refused |

## 3. By task shape

| Task shape | Cases to consider |
|---|---|
| Search or filter | no match, every item matches, match on the boundary of a range, case sensitivity, combined filters |
| Sort | ties (is it stable?), nulls first or last, ascending versus descending, sort by a missing field |
| Pagination | page past the end, page size larger than the total, total an exact multiple of the page size, page zero or negative |
| Aggregate or summary | empty input, one group, groups that differ only by case, negative values cancelling out |
| Merge, move or rename | source equals target, source missing, target missing, item already in both, name collision |
| Parse or import | malformed row, missing column, extra column, quoted separator, blank lines, encoding |
| State change | the same operation twice (idempotent?), an operation on a deleted item, concurrent updates if state is shared |
| HTTP or API | 400 on invalid input, 404 on missing resource, 409 on conflict, empty body, unexpected extra fields |
| Permissions or ownership | another user's or tenant's item, missing permission, admin override |
| Cache or memoisation | stale value after update, eviction, key collision |

## 4. Per-language pitfalls

These are the bugs an AI agent most often introduces or misses. Turn the relevant ones into edge-case rows or into constraints in the prompt.

**C# / .NET**

- `null` versus `string.Empty`; `string.IsNullOrWhiteSpace` versus `IsNullOrEmpty`.
- String comparison: use `StringComparison.OrdinalIgnoreCase` or `StringComparer.OrdinalIgnoreCase` for case-insensitive keys; `ToLower()` is culture-sensitive.
- `First()` throws on empty, `FirstOrDefault()` returns default; `Single()` throws on duplicates.
- `Dictionary` indexer throws `KeyNotFoundException`; `TryGetValue` does not. A dictionary keyed by string is case-sensitive unless built with a comparer.
- Modifying a collection while enumerating it throws `InvalidOperationException`.
- Deferred LINQ: an `IEnumerable` enumerated twice runs twice; `.ToList()` to materialise.
- Integer division truncates; `int` overflow wraps silently unless `checked`.
- `decimal` for money, never `double`.
- `DateTime.Kind` (Local, Utc, Unspecified); prefer `DateTimeOffset` or UTC.
- Exception type matters to tests: `ArgumentException`, `ArgumentNullException`, `InvalidOperationException`, `KeyNotFoundException`.
- `async void`, missing `await`, `.Result` deadlocks.

**Java**

- `==` versus `.equals` on strings and boxed numbers.
- `Optional.get()` on empty; `NullPointerException` from unboxing.
- `ConcurrentModificationException` when removing in a for-each loop.
- `BigDecimal` for money; `equals` compares scale, `compareTo` does not.
- `List.of` and `Map.of` are immutable and reject nulls.

**Python**

- Mutable default arguments shared between calls.
- `None` versus falsy (`0`, `""`, `[]`): `if not x` treats them the same.
- `//` floors toward negative infinity; `round` uses banker's rounding.
- `int("3.0")` raises; `float` for money is inexact, use `Decimal`.
- `str.lower()` versus `casefold()` for unicode.
- Trailing newline in files produces an empty last line with naive `split("\n")`.

**JavaScript / TypeScript**

- `==` versus `===`; `null` versus `undefined`.
- `Array.prototype.sort` without a comparator sorts as strings and mutates in place.
- Floating point: `0.1 + 0.2 !== 0.3`.
- `Date` months are 0-based; parsing date-only strings is UTC, date-time strings are local.
- `parseInt("08")` and `Number("")` (which is 0).
- Missing `await` returns a pending promise that is always truthy.

**Go**

- Nil map write panics; nil slice is fine.
- Loop variable capture in goroutines (before Go 1.22).
- Integer division and overflow wrap silently.
- Errors ignored with `_`.
