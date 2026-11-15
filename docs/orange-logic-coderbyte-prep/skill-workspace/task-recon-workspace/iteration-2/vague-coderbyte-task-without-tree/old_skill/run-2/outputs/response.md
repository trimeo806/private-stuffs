No tree needed to start. Paste this into the Coderbyte AI panel first. It makes the AI build the tree and the plan for you, and it writes no code yet:

```
Before writing any code, do recon on this repo:
1. Show the file tree (skip target/ and .git/) and the pom.xml: Java version, test framework (JUnit 4 or 5), dependencies.
2. Run `mvn -q test` and list every failing test: class, method, assertion message, and the production method it exercises.
3. For each failure, say in one line whether the bug is in the production code or in the test. Assume the tests are correct unless one clearly contradicts the others.
4. Find the checkout path in the orders module: the class/method that computes the order total, and the model classes it uses (Order, OrderItem, money type). Say whether money is BigDecimal, double or long cents.
5. Restate the task in one sentence: "The orders module has failing tests. Fix them, then add support for discount codes at checkout."
6. List explicit and implied requirements, assumptions for anything ambiguous (with the safer default), and edge cases with expected behavior.
7. Propose a step plan where each step changes one function, failing tests first, discount codes after.
Respect existing signatures and style; no new dependencies; do not edit or delete existing tests to make them pass.
```

Below is the recon I'd work from until you can see the repo. Correct it once the AI answers.

## Goal
Make every test in the orders module pass, then let checkout apply a discount code to the order total, covered by new tests.

## Requirements
| # | Requirement | Type |
|---|---|---|
| R1 | All existing orders tests pass (`mvn test` green) | explicit |
| R2 | Fix production code, not the tests, unless a test is provably wrong | implied |
| R3 | Checkout accepts an optional discount code | explicit |
| R4 | A valid code lowers the total by its rule (percent or fixed amount) | implied |
| R5 | Unknown, expired or blank code is rejected with a clear error, or ignored if the existing API never throws | implied |
| R6 | Total never goes below zero | implied |
| R7 | Money keeps the repo's existing type and rounding (BigDecimal, 2 dp, HALF_UP if nothing is set) | implied |
| R8 | New JUnit tests cover the discount behavior | implied |
| R9 | Existing checkout signature still works with no code | implied |

## Assumptions
- A1: No spec for code format or storage → in-memory map of codes inside the orders module, case-insensitive, trimmed.
- A2: Discount types not specified → support PERCENT and FIXED; nothing else.
- A3: One code per order; codes don't stack.
- A4: Discount applies to the item subtotal, before tax and shipping if those exist.
- A5: Invalid code → throw the module's existing exception type (or `IllegalArgumentException`); if checkout returns a result object, report it there instead.
- A6: Add the code as an overload or optional field; never break existing callers or tests.
- A7: Failing tests describe intended behavior → fix the code they test.

## Files
| File | Action | Why |
|---|---|---|
| `pom.xml` | read | Java version, JUnit 4 vs 5 |
| `src/test/java/**/order*/**` | read | the failing tests are the spec |
| Order service / checkout class | change | bug fixes, apply discount |
| Order / OrderItem model | read, maybe change | total calculation, where the code is stored |
| New `DiscountCode` (+ type enum) | create | discount rule |
| New `DiscountCodeTest` / checkout tests | create | prove R3-R9 |
| Other modules, `target/`, hidden grader tests | leave alone | out of scope, generated |

## Edge cases
- Empty cart + valid code → total 0, no exception.
- Null or blank code → same total as no code.
- Code in other case or with spaces (`" save10 "`) → accepted.
- Unknown code → rejected per A5, total unchanged.
- Fixed discount larger than subtotal → total 0, not negative.
- 100% and 0% codes → total 0 / unchanged.
- Percent on odd amounts (10% of 0.15) → rounded to 2 dp per A1/R7.
- Same code applied twice → applied once.
- Quantity 0 or negative price in an item → whatever the fixed tests expect; don't add new rules here.

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 1 | Run the recon prompt above, read failing tests | 6 | failures listed with their root cause |
| 2 | Fix failing test #1 (one function) | 8 | that test green |
| 3 | Fix remaining failures, one function per prompt | 12 | `mvn test` all green |
| 4 | Add `DiscountCode` + type, write its tests first | 8 | percent/fixed tests green |
| 5 | Wire the code into checkout (overload / optional) | 8 | checkout-with-code test green, old tests still green |
| 6 | Invalid/blank/unknown code handling + floor at 0 | 6 | edge-case tests green |
| 7 | Rounding, case-insensitivity, empty cart tests | 4 | full suite green |
| 8 | Review diff, remove dead code, final `mvn test` | 4 | clean diff, green |
| | Buffer | 4 | |
Total 60 min (80 minus the 20 for Section 2).

Cut line: drop step 7 first, then FIXED discounts (keep PERCENT only). Never cut steps 1-3: green existing tests are the first thing graded.

## Open questions (answer when you can, defaults above apply until then)
- Does the task text say anything more about codes (format, where they live, expiry)?
- JUnit 4 or 5, and is there an existing `checkout(...)` method name I should target?
