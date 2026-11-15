Paste the **First prompt** at the bottom into the AI panel now, and attach the file tree there (the panel can see it even if I can't). The rest is your map for the next ~60 min, which leaves 20 min for Section 2.

One question, answer only if you have 10 seconds: what does the tree show under `src/main/java/.../orders` and `src/test/java/.../orders`? Until then I've assumed a standard Maven layout (`mvn test`, JUnit).

## Goal
Get every test in the orders module passing without weakening the tests, then let checkout take an optional discount code that lowers the order total.

## Requirements
| # | Requirement | Type |
|---|---|---|
| R1 | All currently failing orders tests pass (`mvn test`) | explicit |
| R2 | Fix production code, not the tests (unless a test is clearly wrong; ask before editing one) | implied |
| R3 | Tests that pass today still pass | implied |
| R4 | Checkout accepts a discount code | explicit |
| R5 | A valid code reduces the total; no code means the same total as today | implied |
| R6 | An invalid / unknown / expired code is rejected in a defined way (exception or ignored, matching existing error style) | implied |
| R7 | New tests cover the discount path | implied |
| R8 | Keep existing method signatures working (add an overload or an optional param) and add no new dependencies | implied |

## Assumptions
- A1: Discount type is not specified → support percentage and fixed-amount codes; percentage first if time is short.
- A2: Where the codes come from → an in-memory map or enum (e.g. `SAVE10` = 10%), no DB or config file.
- A3: Invalid code → throw the same exception type the module already uses for bad input (e.g. `IllegalArgumentException`); if none exists, throw that one.
- A4: Discount applies to the subtotal, before tax/shipping, if those exist.
- A5: Total never goes below 0.
- A6: Codes are case-insensitive and trimmed.
- A7: One code per order (no stacking).
- A8: Money is `BigDecimal` if the module already uses it; rounding HALF_UP to 2 decimals.

## Files
| File | Action | Why |
|---|---|---|
| `pom.xml` | Read | Java version, JUnit 4 vs 5, test command |
| `README` / task statement | Read | Hidden rules (rounding, code formats) |
| `src/test/java/**/orders/*Test.java` | Read, don't edit | They define the expected behavior; the failure messages are the spec |
| `src/main/java/**/orders/Order*.java`, `*Checkout*`, `*Service*` | Change | Bug fixes + discount entry point |
| New `DiscountCode` (enum or class) in the orders package | Create | Holds the code → rule mapping |
| New `DiscountCodeTest` / extra cases in the checkout test | Create | Covers R4-R7 |
| `target/`, generated sources, other modules | Leave alone | Build output / out of scope |

## Edge cases
- Empty order (no items) → total 0, a discount keeps it 0
- Single item → discount math correct on one line
- Duplicate items / same product twice → quantities summed correctly before the discount
- Quantity 0 or negative, negative price → rejected (or match what the existing tests expect)
- Null / empty / whitespace code → no discount, same total as without a code
- Unknown code → rejected per A3
- Lowercase or padded code (`" save10 "`) → accepted per A6
- Discount larger than the subtotal → total 0, not negative
- 100% code → total 0
- Rounding: 10% off 9.99 → 8.99 (8.991 rounded HALF_UP); match any rounding the existing tests already assert
- Very large totals → no overflow (`BigDecimal` / `long`, not `int` / `double`)

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 1 | Run `mvn test`; list each failing test and its message | 5 | You have a list of N failures, grouped by cause |
| 2 | Fix failure group 1 (one function per prompt) | 10 | That group goes green, nothing else goes red |
| 3 | Fix remaining failure groups | 10 | `mvn test` is fully green |
| 4 | Add `DiscountCode` with percentage codes + checkout overload taking a code | 10 | A new test with `SAVE10` gives a 10% lower total |
| 5 | Invalid / null / case / floor-at-zero handling | 8 | Edge-case tests pass |
| 6 | Fixed-amount codes | 5 | Fixed-amount test passes |
| 7 | Full `mvn test`, read the diff, remove debug prints | 5 | All green, clean diff |
| — | Buffer | 7 | |
Cut line: drop step 6 first, then trim step 5 to null + unknown-code only. Never skip steps 1-3: green existing tests are the explicit requirement.

## First prompt
```
Context: Java/Maven project, file tree attached. Task: "The orders module has
failing tests. Fix them, then add support for discount codes at checkout."

Do NOT write code yet. First:
1. Run (or tell me to run) `mvn test` and list every failing test in the orders
   module with its failure message, grouped by likely root cause.
2. Restate the task in one sentence.
3. List explicit and implied requirements, and assumptions for anything ambiguous
   (discount type, invalid-code behavior, rounding, where codes are stored).
4. List edge cases with expected behavior.
5. Propose a step plan where each step changes one function: fix the failing
   tests first (production code only, do not edit tests), then add discount codes.
Respect existing signatures and style; keep current checkout calls working;
no new dependencies.
```

After it answers, send one step at a time: "Do step 1 only: fix <method> so <test> passes. Show the diff, then run the tests."
