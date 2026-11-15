# AI Diff Traps

Patterns that AI agents produce often and that look correct at a glance. Read the cross-language section first, then only the section for the diff's language. Each entry: the trap, the input that exposes it, and what correct code does instead.

Contents: Cross-language, C# / .NET, Java, Python, JavaScript / TypeScript.

## Cross-language

| Trap | How it shows up | Exposing input | Correct move |
|---|---|---|---|
| Test edited to pass | Assert value changed to the buggy output, assertion deleted, test skipped, matcher loosened (`Equal` to `Contains`) | Revert the test hunk: the suite goes red again | Revert the test change; fix the production code |
| Fix in the wrong layer | Case-insensitive compare "fixed" by lowercasing data on save, trimming on write instead of on compare, normalizing in the UI | Data that already exists, written before the change or by another path | Normalize or compare at the comparison site; leave stored data as it is |
| Mutating input | Sorting, trimming or lowercasing the caller's list or object in place | Caller reuses the object after the call | Work on a copy, or return a new value |
| Partial fix | Handles the example from the bug report, not the rule | The same rule with a different value (two spaces fixed, tabs not) | Implement the rule, then test a second instance of it |
| Scope creep | Refactor, rename or new feature bundled with the fix | Any caller of the renamed symbol | Ask for the minimal change; split the rest out |
| Swallowed failure | `catch` that logs and returns a default, `?? ""`, `or []` that hides bad input | Invalid input now succeeds silently | Fail loudly, or handle the case explicitly as the requirement says |
| Hallucinated API | A method, overload, option or package that does not exist in this version | Compile or import fails, or a different overload resolves | Check the signature in the repo or the official docs |
| Signature or format drift | Return type widened, parameter added, output line format changed, JSON field renamed | Existing callers and stdout-comparing tests | Keep the contract unless the requirement changes it |
| Accidental O(n²) | `list.Contains` or `IndexOf` inside a loop, re-querying inside a loop | 10^5 items | Hash set or dictionary lookup, one pass |
| Integer overflow | Running total in 32-bit int | Sum above 2,147,483,647 | 64-bit accumulator |
| Float equality | `==` on doubles, money in floating point | `0.1 + 0.2` | Tolerance compare, or a decimal type for money |
| Banker's rounding surprise | `Math.Round(2.5)` and Python `round(2.5)` both give 2 | Half values | Pass the rounding mode explicitly |
| Time zones | Local "now" used for stored or compared timestamps | Server in another zone, DST change | UTC for storage and comparison |

## C# / .NET

| Trap | Exposing input | Correct move |
|---|---|---|
| `Path.GetExtension` includes the dot | `"a.jpg"` gives `".jpg"`, compared to `"jpg"` never matches | `TrimStart('.')`, or compare with the dot on both sides |
| `Path.GetExtension` edge cases | `"archive.tar.gz"` gives `".gz"`; `"README"` gives `""`; `".gitignore"` gives `".gitignore"`; `"file."` gives `""`; `null` gives `null` | Decide each case against the requirement and test it |
| `==` and `string.Equals(a, b)` are ordinal and case-sensitive | `"ABC" == "abc"` is false | `string.Equals(a, b, StringComparison.OrdinalIgnoreCase)` |
| `ToLower()` / `ToUpper()` use the current culture | Turkish culture: `"TITLE".ToLower()` gives a dotless i | `ToLowerInvariant()`, or better, an ignore-case comparison instead of changing case |
| Culture-sensitive sorting and searching | `OrderBy(s => s)`, `List<string>.Sort()`, `CompareTo`, `StartsWith(string)`, `IndexOf(string)` use the current culture; results change by machine | `StringComparer.Ordinal` / `OrdinalIgnoreCase`, or the overload taking `StringComparison` |
| Dictionary and HashSet keys are case-sensitive by default | Key `"Jpg"` misses `"jpg"` | `new Dictionary<string, T>(StringComparer.OrdinalIgnoreCase)` |
| `int` arithmetic is unchecked | `int total` summing sizes wraps to a negative number | `long` accumulator |
| LINQ `Sum()` on `int` is checked | Same sum now throws `OverflowException` instead of wrapping | `Sum(x => (long)x.Size)` |
| `First()` / `Single()` throw | Empty sequence, or two matches for `Single` | `FirstOrDefault()` plus an explicit null case, if the requirement allows none |
| Multiple enumeration | `IEnumerable` from a query or generator enumerated twice: double work or different results | Materialize once with `ToList()` |
| `Split` keeps empty entries | `"a,,b".Split(',')` has 3 items; `" a, b".Split(',')` keeps spaces | `StringSplitOptions.RemoveEmptyEntries \| StringSplitOptions.TrimEntries` |
| `Substring` range | Input shorter than the length | Guard the length, or use ranges with checks |
| `DateTime.Now`, `DateTime.Parse` | Server zone, culture-specific formats | `DateTime.UtcNow`, `DateTimeOffset`, `ParseExact` with `CultureInfo.InvariantCulture` |
| Async misuse | `async void`, `.Result` or `.Wait()`, `Task` not awaited | `async Task`, await all the way |
| Nullable warnings silenced | `!` null-forgiving operator added to make the build quiet | Handle the null, or prove it cannot happen |
| `decimal` vs `double` | Money in `double` | `decimal` |

## Java

| Trap | Exposing input | Correct move |
|---|---|---|
| `==` on strings or boxed numbers | Strings built at runtime; `Integer` values above 127 | `equals`, `Objects.equals` |
| `toLowerCase()` / `toUpperCase()` default locale | Turkish locale and the letter I | `toLowerCase(Locale.ROOT)`, or `equalsIgnoreCase` |
| `int` overflow, `Math.abs(Integer.MIN_VALUE)` is negative | Large sums, hash-based indexes | `long`, `Math.addExact`, `Math.floorMod` |
| `Optional.get()` without a check | Empty optional | `orElse`, `orElseThrow` with a message |
| Modifying a collection while iterating | `ConcurrentModificationException` | `removeIf`, or an iterator's `remove` |
| `String.split` takes a regex and drops trailing empties | `"a.b".split(".")` is empty; `"a,b,,".split(",")` has 2 items | `Pattern.quote`, `split(",", -1)` |
| Comparator by subtraction | `a - b` overflows on large values | `Integer.compare`, `Comparator.comparing` |

## Python

| Trap | Exposing input | Correct move |
|---|---|---|
| Mutable default argument | `def f(items=[])` keeps state between calls | `items=None`, then create the list inside |
| `os.path.splitext` edge cases | `".bashrc"` gives `('.bashrc', '')`; `"a.tar.gz"` gives `.gz` | Decide each case against the requirement |
| `lower()` for case-insensitive compare | Some non-ASCII letters, such as German sharp s | `casefold()` |
| `is` instead of `==` | Ints outside the small-int cache, strings built at runtime | `==` |
| Integer vs true division | `/` returns a float; `//` floors toward negative infinity | Pick explicitly; test negatives |
| `round` is banker's rounding | `round(2.5)` is 2 | `decimal` with `ROUND_HALF_UP` when the requirement says so |
| `sorted` on mixed case | Uppercase sorts before lowercase | `key=str.casefold` when the requirement wants case-insensitive order |
| Bare `except` | Swallows everything, including `KeyboardInterrupt` | Catch the specific exception |

## JavaScript / TypeScript

| Trap | Exposing input | Correct move |
|---|---|---|
| Default `sort()` is lexicographic | `[10, 9, 1].sort()` gives `[1, 10, 9]` | `sort((a, b) => a - b)` |
| `localeCompare` without a locale | Order changes between machines | Pass the locale and options, or compare code points for ordinal order |
| `==` coercion | `0 == ""` is true | `===` |
| `path.extname` edge cases | `".bashrc"` gives `""`; `"index."` gives `"."` | Decide each case against the requirement |
| `forEach` with `async` callbacks | Function returns before the work finishes | `for...of` with `await`, or `Promise.all` |
| Number precision | IDs above `Number.MAX_SAFE_INTEGER` lose digits | `BigInt`, or keep IDs as strings |
| `parseInt` partial parse | `parseInt("12px")` is 12; `parseInt("")` is `NaN` | `Number(...)` plus a validity check |
| TypeScript `as` or `any` to silence errors | Runtime shape does not match the cast | Narrow with a type guard or validate the input |
| Optional chaining hiding bugs | `a?.b?.c ?? 0` turns missing required data into 0 | Fail when the data is required |
