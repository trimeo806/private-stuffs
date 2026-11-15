# Failure patterns

Signal -> usual cause -> how to confirm. A pattern is a lead, not a diagnosis: confirm with a run or trace before reporting it.

## Contents

1. C# build errors
2. C# runtime exceptions
3. C# wrong-value bugs (tests red, no exception)
4. Symptom words in tickets
5. Python, TypeScript, Java quick map

## 1. C# build errors

Read the first error. Fix nothing; find what introduced it (usually the last accepted agent edit).

| Code | Message shape | Usual cause | Confirm |
|---|---|---|---|
| CS0117 | 'string' does not contain a definition for 'X' | Hallucinated static API (`string.IsNullOrBlank`, `Math.Clamp` on old TFMs) | Check the real API name for the target framework in the csproj |
| CS1061 | 'T' does not contain a definition for 'X' and no accessible extension method | Hallucinated instance method, or missing `using System.Linq` | Is it an extension method? Is the `using` present? |
| CS0246 / CS0103 | type or name could not be found | Missing `using`, typo, type the agent assumed exists | Search the repo for the name |
| CS1503 | cannot convert from 'A' to 'B' | A signature changed (e.g. `long` -> `int`) and callers or tests still pass the old type | Compare the signature with `git diff` or the task; callers are right if the task fixed the signature |
| CS7036 / CS1501 | no argument given / no overload takes n arguments | Parameter added or removed | Same; hidden tests compile against the original signature |
| CS0161 | not all code paths return a value | Early return or branch added without a final return | Read every branch |
| CS0266 | cannot implicitly convert 'long' to 'int' | Narrowing assignment, often an overflow bug in disguise | Look at the value range in the requirement |
| CS8602 / CS8618 | nullable warnings | Warnings unless `TreatWarningsAsErrors`; rarely the real failure | Check whether the build failed on them |

A signature change that breaks callers is a contract break, not a call-site problem: the fix direction is to restore the signature, never to edit tests or callers to match.

## 2. C# runtime exceptions

Go to the topmost stack frame inside the project, then ask which input reaches it.

| Exception / message | Usual cause | Inputs that reach it |
|---|---|---|
| `InvalidOperationException: Sequence contains no elements` | `Max`, `Min`, `Average`, `First`, `Single` on an empty sequence of value types | Empty library, filter that matches nothing |
| `InvalidOperationException: Sequence contains more than one element` | `Single` where duplicates are possible | Duplicate data, case variants |
| `NullReferenceException` | `FirstOrDefault`/dictionary `TryGetValue` result used without check; null collection passed in | Missing id, null tags list |
| `KeyNotFoundException` | Indexer on a dictionary for a key that may be missing; key casing differs | Unknown id, different letter case |
| `ArgumentOutOfRangeException` | `Skip`/`Take` negative, `Substring` past end, list index | Page 0, page past end, short strings |
| `OverflowException` | `checked` arithmetic or `Convert.ToInt32` on large values; LINQ `Sum` on `int` throws | Sizes over 2 GB, many items |
| `FormatException` | `int.Parse`/`DateTime.Parse` with culture-dependent input | Comma decimals, non-invariant dates |
| `InvalidOperationException: Collection was modified` | Mutating a list while enumerating it | Any delete or update inside `foreach` |
| `DivideByZeroException` | Integer division by a count or page size | Empty data, page size 0 |

## 3. C# wrong-value bugs

| Symptom in test output | Usual cause |
|---|---|
| Ordering right on the first key, wrong overall | Second `OrderBy` instead of `ThenBy`; it discards the first sort |
| Total pages one too low | Integer division `count / size` instead of ceiling `(count + size - 1) / size` |
| First page missing or page shifted | `Skip(page * size)` with 1-based page; should be `(page - 1) * size` |
| Negative or wrapped total | Summing into `int`, or `(int)` cast on `long` sizes |
| Duplicates not detected / search misses | `==` or `Contains` on strings is ordinal and case-sensitive; normalize on both sides or use `StringComparer.OrdinalIgnoreCase` |
| Fixed only for new data | Normalization added on save but not on compare, so existing rows still differ |
| `.gitignore` reported as extension `gitignore` | `Path.GetExtension(".gitignore")` returns `.gitignore`; dot-files need explicit handling |
| Off by hours, only for some users | `DateTime.Now` vs `DateTime.UtcNow` mixed, or a real clock used where an injected clock exists |
| Expected value correct in a test, but test still flaky | Real clock, random or dictionary ordering in the code under test |
| Two results swap between runs | No tiebreak in ordering; `HashSet`/`Dictionary` enumeration order relied on |
| Rounding off by one at .5 | `Math.Round` uses banker's rounding by default (`MidpointRounding.ToEven`) |

## 4. Symptom words in tickets

| Ticket says | Look for |
|---|---|
| "uppercase", "lowercase", "different case" | Case-sensitive compare, normalization only on one side |
| "only for users in <country>", "hours later", "after midnight" | Local vs UTC, real clock vs injected clock, date-only truncation |
| "large files", "big libraries", "over 2 GB" | `int` where `long` is needed, `Sum` overflow |
| "second page", "last page", "empty page" | Skip/Take off by one, ceiling, page past end |
| "same file twice", "duplicates" | Dedupe key not normalized, compared before normalization |
| "sometimes", "random", "order changes" | Missing tiebreak, unordered collections, real clock |
| "after renaming / after update" | Stale cached copy, mutation of a shared instance, update not saved |
| "with spaces", "blank" | Missing trim, `IsNullOrEmpty` where `IsNullOrWhiteSpace` is needed |

## 5. Python, TypeScript, Java quick map

| Language | Signal | Usual cause |
|---|---|---|
| Python | `KeyError` | dict lookup without `.get`; key casing |
| Python | `TypeError: '<' not supported between` | Sorting mixed types or `None` |
| Python | value off at boundary | `>` vs `>=` in a loop over units or ranges |
| Python | mutable default argument shared | `def f(x=[])` |
| TypeScript | `undefined is not a function` / `cannot read properties of undefined` | Optional chaining missing; wrong import shape (default vs named) |
| TypeScript | numbers sort wrong | `Array.sort()` without comparator sorts as strings |
| TypeScript | date off by hours | `new Date('2026-01-01')` is UTC, `new Date(2026, 0, 1)` is local |
| Java | `NullPointerException` | Unboxing a null `Integer`, map `get` miss |
| Java | strings equal but `==` false | `==` compares references; use `equals` / `equalsIgnoreCase` |
| Java | `ConcurrentModificationException` | Removing inside for-each; use iterator `remove` or `removeIf` |
