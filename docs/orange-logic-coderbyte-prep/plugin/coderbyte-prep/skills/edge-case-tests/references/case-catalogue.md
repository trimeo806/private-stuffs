# Edge case catalogue

Concrete inputs, the behavior a contract usually wants, and the bug each catches (use it for the table's "why" column). Read only the sections that match the function. Where the contract is silent, the usual expectation is a reasonable default to mark `assumed`.

- [Empty, null and whitespace](#empty-null-and-whitespace)
- [Duplicates and strings](#duplicates-and-strings)
- [Invalid input](#invalid-input)
- [Large values and overflow](#large-values-and-overflow)
- [Ordering](#ordering)
- [Paging](#paging)
- [Unicode and culture](#unicode-and-culture)
- [Numbers, money and dates](#numbers-money-and-dates)
- [State and repeat calls](#state-and-repeat-calls)
- [Endpoints](#endpoints)

## Empty, null and whitespace

| Input | Usual expectation | Catches |
|---|---|---|
| empty collection | empty result, zero totals | `First()` / `[0]` on empty, divide by count |
| single item | same as the general case | loops that assume pairs, `n - 1` bounds |
| null collection | argument exception for the language (`ArgumentNullException`, `TypeError`, `NullPointerException`) | missing guard, null dereference deep inside |
| null element inside | skip or reject, per contract | null dereference in a lambda |
| `""` and `"   "` | both missing or both rejected | `IsNullOrEmpty` where `IsNullOrWhiteSpace` was meant |
| tabs, newlines, non-breaking space, trailing `\n` | treated as whitespace | only `' '` checked |
| missing optional field | default value | KeyError / undefined access |

## Duplicates and strings

| Input | Usual expectation | Catches |
|---|---|---|
| exact duplicates | merged or counted twice, per contract | set where a multiset was needed, or the reverse |
| `"Logo"`, `"logo"`, `"LOGO"` | one entry when matching ignores case | dedupe or group before normalizing |
| `"a"`, `" a "` | one entry | trim after dedupe |
| `.JPG` vs `.jpg` | same group, reported lowercase | extension compared case-sensitively |
| all items identical | one group, stable output | code that assumes distinct keys |
| internal runs `"a   b"` | collapsed if the contract normalizes | empty tokens from a single-space split |

## Invalid input

Assert the exact exception type, and the parameter name where possible.

| Input | Usual expectation | Catches |
|---|---|---|
| negative size, count or quantity | `ArgumentOutOfRangeException` / `ValueError` / `IllegalArgumentException` / `RangeError` | negatives silently reducing totals |
| invalid value after valid ones | still rejected | only the first item validated |
| wrong format (`"12a"`, `"2024-13-01"`) | validation error | parse throwing an unexpected exception type |
| unknown enum or status | rejected | default branch doing something plausible but wrong |
| `../x` in a name | rejected or sanitized | file written outside the target folder |
| string over the length limit | rejected | silent truncation |

## Large values and overflow

| Input | Usual expectation | Catches |
|---|---|---|
| `int.MaxValue` / `Integer.MAX_VALUE` as a page or index, with size above 1 | empty result or rejection, no wrap-around | `(page - 1) * size` wrapping negative and returning the first page |
| sizes summing past 2^31 (files over 2 GB) | correct 64-bit total | `int` accumulator overflow |
| `count + 1` or `a + b` at the type's max | handled | midpoint `(lo + hi) / 2` overflow |
| 10^5 items or more | finishes quickly | O(n^2) loops, repeated `List.Contains` |
| deep nesting | no stack overflow | unbounded recursion |
| `0.1 + 0.2` | tolerance or decimal | exact float equality |

## Ordering

| Input | Usual expectation | Catches |
|---|---|---|
| ties on the primary key | deterministic secondary order | missing tie-breaker, flaky tests |
| `"b", "A", "a"` | case-insensitive or culture order, per contract | uppercase first under ordinal compare |
| descending sort with ties | ties still ascending on the secondary key | `Reverse()` on the whole list |
| `"10", "9"` | numeric order when they are numbers | lexical sort |
| input list after the call | unchanged | in-place sort of the caller's list |

## Paging

Assume 1-based pages unless the contract says otherwise, and state it.

| Input | Usual expectation | Catches |
|---|---|---|
| page 0 or negative | argument exception | silently treated as page 1 |
| page size 0, negative, or above the max | argument exception (or clamp, per contract) | divide by zero, unbounded queries |
| first page | first `size` items in sort order | skip computed as `page * size` |
| count an exact multiple of size, and one more | `count / size` pages, then one extra partial page | floor division for total pages, or `+ 1` always |
| page past the end | empty items, totals still filled | exception, or last page returned |
| huge page number | empty items | skip overflow wrapping to page 1 |
| empty source | empty items, total count 0, total pages 0 | total pages 1 on empty |

## Unicode and culture

| Input | Usual expectation | Catches |
|---|---|---|
| `"Café"`, `"Ñandú"` | lowercased correctly; folded to ASCII if the contract asks for slugs | `[^a-z]` dropping letters |
| composed vs decomposed `é` | equal after NFC if names are compared | identical-looking names not matching |
| emoji | length as the contract defines | substring cutting a surrogate pair |
| Turkish `I` / `i` under `tr-TR` | invariant comparison for identifiers | culture-sensitive `ToLower()` |
| `"1,5"` vs `"1.5"` | invariant culture for machine data | parse depending on machine locale |
| zero-width or right-to-left characters | kept or stripped, per contract | invisible duplicates |

For identifiers use `StringComparer.OrdinalIgnoreCase` / `ToLowerInvariant` in C#, `toLowerCase(Locale.ROOT)` in Java, `casefold()` in Python.

## Numbers, money and dates

| Input | Usual expectation | Catches |
|---|---|---|
| rounding at `.5` | the documented mode | .NET `Math.Round` defaults to banker's rounding |
| currency | decimal, not float | cent drift |
| end of month, Feb 29, year boundary | correct calendar arithmetic | "a month" as 30 days |
| DST change, UTC vs local | stored UTC, shown local | off-by-one-hour |
| timestamp equal to a cutoff | inclusive or exclusive as documented | `>` vs `>=` |

## State and repeat calls

| Input | Usual expectation | Catches |
|---|---|---|
| item not found | documented not-found result or exception | `null` where an exception was promised |
| already exists | conflict error or idempotent success | duplicate rows |
| same call twice | same result | hidden static state, badly keyed caches |

## Endpoints

Apply the cases above to each parameter, plus:

| Input | Usual expectation | Catches |
|---|---|---|
| missing required field | 400 with a field error | 500 from a null dereference |
| wrong JSON type (`"size": "big"`) | 400 | binding default `0` accepted |
| unknown id | 404 | 500, or 200 with a null body |
| unauthenticated / unauthorized | 401 / 403 | data returned to anyone |
| duplicate create | 409 or idempotent 200, per contract | second row created |
| paging past the end | 200 with an empty list | 404 or 500 |
