No tree needed to start. Paste this into the Coderbyte AI panel first, then send it the `mvn test` failure output when you have it:

```
Task: "The orders module has failing tests. Fix them, then add support for
discount codes at checkout." Java, Maven.

Before writing any code:
1. List the files in the orders module (main + test) and name the checkout
   entry point (class + method that computes the order total).
2. Restate the task in one sentence.
3. List explicit and implied requirements, and assumptions for anything
   ambiguous (discount type, invalid/expired codes, rounding, stacking).
4. List edge cases with expected behavior.
5. Propose a step plan where each step changes one method: fix the failing
   tests first (fix production code, not the tests, unless a test is clearly
   wrong), then discount codes.
Respect existing signatures, package layout and style; no new dependencies;
use BigDecimal for money if the code already does.
```

If the panel can't see the files, run `mvn -q test` (or `./mvnw -q test`) in their terminal and paste the failing test names + first stack frame into the panel instead.

## Goal
Make the orders module's tests pass, then let checkout accept a discount code that lowers the order total.

## Requirements
| # | Requirement | Type |
|---|---|---|
| R1 | All existing tests in the orders module pass (`mvn test` green) | explicit |
| R2 | Fixes go in production code; tests change only if a test is provably wrong | implied |
| R3 | Checkout accepts an optional discount code | explicit |
| R4 | A valid code reduces the total by its amount or percentage | implied |
| R5 | No code / blank code → total unchanged | implied |
| R6 | Unknown or invalid code → clear failure (exception or result flag, matching existing error style) | implied |
| R7 | Total never goes below zero | implied |
| R8 | Existing checkout callers still compile (overload or optional param, no breaking signature change) | implied |
| R9 | New tests cover the discount behavior | implied |

## Assumptions
- A1: Discount type unspecified → support percentage and fixed amount via a small `DiscountCode` model; if the code already hints at one, use only that.
- A2: Where codes come from → in-memory registry/map injected into the checkout service; no DB.
- A3: Invalid code → throw `IllegalArgumentException` (or the module's existing exception type), do not silently ignore.
- A4: Codes are case-insensitive, trimmed.
- A5: One code per order; no stacking.
- A6: Discount applies to the item subtotal, before tax/shipping if those exist.
- A7: Money rounding → `BigDecimal`, scale 2, `HALF_UP` (match existing code if it differs).
- A8: Time → about 55 min for this task, leaving 20 for Section 2 and 5 buffer. Adjust if this task is the whole test.

## Files
Unknown until the AI lists them. Expected shape:
| File | Action | Why |
|---|---|---|
| `pom.xml` | read | Java version, JUnit 4 vs 5, test plugin |
| `src/main/java/**/order*/*` (Order, OrderItem, OrderService/Checkout) | read, change | failing-test fixes + discount hook |
| `src/test/java/**/order*/*Test.java` | read; edit only if a test is wrong | they define expected behavior |
| new `DiscountCode` (+ registry) next to the order classes | add | discount model |
| new `*DiscountTest.java` | add | discount tests |
| other modules, generated `target/` | leave | out of scope |

## Edge cases
- Empty order with a code → total 0, no error
- Single item, 100% code → total 0
- Fixed discount larger than subtotal → total 0, not negative
- Code `" save10 "` / `SAVE10` → same as `SAVE10`
- `null` or empty code → no discount
- Unknown code → error per A3
- Expired or inactive code (if model has dates) → treated as invalid
- Percentage outside 0-100 or negative amount → rejected at code creation
- Rounding: 33.33% of 10.00 → 6.67 total, 2 decimals
- Duplicate items in the order → discount applies to the full subtotal once

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 1 | Run `mvn test`, have AI read pom + orders module, list failures and root causes | 8 | each failure has a named cause |
| 2 | Fix failing tests one method at a time | 15 | `mvn test` green |
| 3 | Add `DiscountCode` model + registry (A1, A2, A4) | 8 | compiles, validation rejects bad values |
| 4 | Apply code in checkout via overload/optional param (R3-R8) | 10 | existing callers unchanged, total correct for a valid code |
| 5 | Tests for discount happy path + edge cases above | 10 | new tests green, full suite green |
| 6 | Polish: naming, remove dead code, final `mvn test` | 4 | clean diff, all green |
Cut line: drop step 6, then trim step 5 to happy path + invalid code + not-below-zero. Never cut step 2: green existing tests is the first stated requirement.

## Questions (answer if you can, otherwise the defaults above stand)
1. Does the task text or any existing class/test mention how a discount is shaped (percent, fixed, expiry)?
2. Is there a Section 2 after this, or is the 80 min all for this task?
