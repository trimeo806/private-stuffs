# Edge case catalogue

Concrete inputs per category, the behavior a contract usually wants, and the bug each one catches. Pick per input dimension; skip what cannot happen given the types. Where the contract says nothing, the "Usual expectation" column is a reasonable default to mark as `assumed`.

- [Empty and null](#empty-and-null)
- [Boundaries and off-by-one](#boundaries-and-off-by-one)
- [Duplicates](#duplicates)
- [Whitespace](#whitespace)
- [Invalid input](#invalid-input)
- [Large values and overflow](#large-values-and-overflow)
- [Ordering](#ordering)
- [Paging](#paging)
- [Unicode and culture](#unicode-and-culture)
- [Numbers, money and dates](#numbers-money-and-dates)
- [State and repeat calls](#state-and-repeat-calls)
- [Endpoints](#endpoints)

## Empty and null

| Input | Usual expectation | Catches |
|---|---|---|
| empty collection | empty result, zero totals, no exception | `First()` / `[0]` on empty, divide by count |
| single item | works like the general case | loops that only handle pairs, `n - 1` bounds |
| `null` / `None` collection | argument exception (`ArgumentNullException`, `TypeError`, `IllegalArgumentException`, `NullPointerException` per language idiom) | missing guard, NRE deep inside |
| `null` element inside collection | skip or reject, whichever the contract says | NRE in a lambda |
| empty string `""` | treated as missing or rejected | `""` treated as a valid name |
| whitespace-only `"   "` | same as `""` | `IsNullOrEmpty` used where `IsNullOrWhiteSpace` was meant |
| missing optional field | default value | KeyError / undefined access |

## Boundaries and off-by-one

For every limit `L` in the contract test `L - 1`, `L`, `L + 1`, plus `0` and `1`.

| Input | Usual expectation | Catches |
|---|---|---|
| exactly at max (`pageSize = 100` when max is 100) | accepted | `<` written as `<=` |
| one above max | rejected | missing upper bound |
| zero | rejected or empty result, per contract | `< 0` written where `< 1` was meant |
| count that is an exact multiple of a group size | `count / size` groups | ceiling done with `+ 1` always |
| count one more than a multiple | one extra partial group | floor division instead of ceiling |
| index of the last element | returned | `< length - 1` loop bound |
| inclusive vs exclusive range ends | as documented | `Between` semantics swapped |

## Duplicates

| Input | Usual expectation | Catches |
|---|---|---|
| exact duplicates | merged once or counted twice, per contract | set used where a multiset was needed, or the reverse |
| case variants `"Logo"`, `"logo"`, `"LOGO"` | one entry when matching is case-insensitive | dedupe before normalizing |
| duplicates after trimming `"a"`, `" a "` | one entry | trim after dedupe |
| file extensions `.JPG` vs `.jpg` | same group | extension compared case-sensitively |
| all items identical | single group, stable output | sort or group code that assumes distinct keys |
| duplicate key on insert | "already exists" error or upsert, per contract | silent overwrite |

## Whitespace

| Input | Usual expectation | Catches |
|---|---|---|
| leading / trailing spaces | trimmed | raw input stored and compared |
| internal runs `"a   b"` | collapsed if the contract normalizes | split on single space giving empty tokens |
| tabs, newlines, non-breaking space ` ` | treated as whitespace if the contract says whitespace | `' '` checked instead of `char.IsWhiteSpace` / `str.isspace` |
| trailing newline from file or form input | ignored | `"value\n" != "value"` |

## Invalid input

Assert the exact exception type, and the parameter name when possible.

| Input | Usual expectation | Catches |
|---|---|---|
| negative where only non-negative makes sense (size, count, quantity) | `ArgumentOutOfRangeException` / `ValueError` / `IllegalArgumentException` / `RangeError` | negatives silently subtracting from totals |
| wrong format (`"12a"`, `"2024-13-01"`) | format or validation error, not a crash | `int.Parse` throwing an unexpected exception type |
| unknown enum or status value | rejected | default branch doing something plausible but wrong |
| path traversal in a name `../x` | rejected or sanitized | file written outside the target folder |
| very long string over the limit | rejected | truncation without notice |

## Large values and overflow

| Input | Usual expectation | Catches |
|---|---|---|
| `int.MaxValue` / `Integer.MAX_VALUE` as a page or index | handled without wrap-around | `(page - 1) * size` overflowing to a negative skip |
| sum of sizes over 2^31 bytes (files over 2 GB) | correct `long` total | `int` accumulator overflow |
| many items (10^5 or more) | finishes quickly | O(n^2) nested loops, repeated `List.Contains` |
| deep nesting | no stack overflow | unbounded recursion |
| float accumulation `0.1 + 0.2` | compared with tolerance or uses decimal | exact float equality |

## Ordering

| Input | Usual expectation | Catches |
|---|---|---|
| ties on the primary sort key | deterministic secondary order (id, name) | unstable or missing tie-breaker; flaky tests |
| mixed case keys `"b", "A", "a"` | ordinal-ignore-case or culture order, per contract | uppercase sorted before lowercase by ordinal compare |
| already sorted | unchanged | algorithms that assume shuffled input |
| reverse sorted | correctly sorted | |
| descending sort with ties | ties still in ascending secondary order | `Reverse()` applied to the whole list |
| numbers as strings `"10", "9"` | numeric order if they are numbers | lexical sort |

## Paging

Assume 1-based pages unless the contract says 0-based; state it.

| Input | Usual expectation | Catches |
|---|---|---|
| page 0 or negative | argument exception | treated as page 1 silently |
| page size 0 or negative | argument exception | divide by zero |
| page size above the max | argument exception or clamp, per contract | unbounded queries |
| first page | first `size` items in sort order | skip computed as `page * size` |
| last partial page | remaining items only | `TotalPages` from floor division |
| page past the end | empty items, totals still correct | exception or last page returned instead |
| page size larger than the total | all items, one page | |
| empty source | empty items, `TotalCount = 0`, `TotalPages = 0` | `TotalPages = 1` on empty |
| huge page number | empty items, no overflow | skip overflow wrapping to page 1 |

## Unicode and culture

| Input | Usual expectation | Catches |
|---|---|---|
| accented letters `"Café"`, `"Ñandú"` | lowercased and compared correctly; folded to ASCII if the contract asks for slugs | `[^a-z]` regex dropping letters |
| decomposed vs composed `"é"` vs `"é"` | equal after normalization (NFC) if names are compared | two "identical" names not matching |
| emoji `"📷"` | length counted as the contract defines (code points vs UTF-16 units) | `Substring` cutting a surrogate pair |
| Turkish `"I"` / `"i"` under `tr-TR` | invariant comparison for identifiers | `ToLower()` / `ToUpper()` using the current culture |
| decimal separator `"1,5"` vs `"1.5"` | parsed with invariant culture for machine data | `double.Parse` depending on machine locale |
| right-to-left or zero-width characters | preserved or stripped, per contract | invisible duplicates |

In C#, prefer `StringComparer.OrdinalIgnoreCase` and `ToLowerInvariant` for identifiers; in Java `toLowerCase(Locale.ROOT)`; in Python `casefold()` for case-insensitive matching.

## Numbers, money and dates

| Input | Usual expectation | Catches |
|---|---|---|
| rounding at `.5` | the documented rounding mode (banker's vs away from zero) | `Math.Round` default is banker's in .NET |
| currency amounts | decimal type, not float | cent drift |
| end of month, Feb 29, year boundary | correct date arithmetic | adding 30 days for "a month" |
| DST change, UTC vs local | stored in UTC, displayed in local | off-by-one-hour bugs |
| timestamps equal to a cutoff | inclusive or exclusive as documented | `>` vs `>=` on dates |

## State and repeat calls

| Input | Usual expectation | Catches |
|---|---|---|
| item not found | documented not-found result or exception | `null` returned where an exception was promised |
| already exists | conflict error or idempotent success | duplicate rows |
| same call twice | same result (idempotent) or documented change | hidden static state, caches not keyed correctly |
| input collection mutated by the function | input unchanged | in-place sort of the caller's list |

## Endpoints

For an HTTP endpoint, the function cases above apply to each parameter, plus:

| Input | Usual expectation | Catches |
|---|---|---|
| missing required body field | 400 with a field error | 500 from a null dereference |
| wrong type in JSON (`"size": "big"`) | 400 | model binding default `0` accepted |
| unknown id | 404 | 500 or 200 with null body |
| unauthenticated / unauthorized | 401 / 403 | data returned to anyone |
| duplicate create | 409 or idempotent 200, per contract | second row created |
| query string paging past the end | 200 with empty list | 404 or 500 |
