You don't need to paste me the tree. Their agent can see the repo, so get it to do the recon first. Paste this into the AI panel as your first prompt:

```
Task: "The orders module has failing tests. Fix them, then add support for discount codes at checkout."
Java, Maven. You may run `mvn -q test` to see which tests fail, but do not edit or create any files yet. First:
1. Restate the task in one sentence.
2. List the currently failing tests and, for each, the likely cause (file and method).
3. List requirements as a numbered checklist, each marked explicit or implied,
   each with how a test would check it.
4. List assumptions for anything ambiguous (discount types, unknown/blank code,
   case sensitivity, rounding, discount larger than the total), with the default you pick and why.
5. List the files to read, change and leave alone. Existing test files and pom.xml stay unchanged.
6. List edge cases with expected behaviour, marking the ones hidden tests most likely check.
7. Propose a step plan where each step changes one function or fixes one bug,
   with minutes per step for a 50-minute limit and a cut line.
Respect existing public signatures and the existing error-handling style; no new dependencies.
```

When it answers, correct anything it got wrong in your next prompt before you ask for step 1. Graders read that log, and a visible correction counts as judgement.

Here is my recon so you can check its answer against it. Without the tree, file names are guesses, so its answer wins on paths.

## Goal
Make the orders module's existing tests pass, then let checkout take an optional discount code that lowers the total. These are two deliverables, done in that order.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | Currently failing orders tests pass | explicit | `mvn test` green, with no edits to test files |
| R2 | Checkout accepts a discount code and applies it to the total | explicit | new test: valid code gives the expected lower total |
| R3 | Existing checkout calls keep working (add an overload or optional param, don't change the old signature) | implied | old tests plus hidden regression tests still compile and pass |
| R4 | Invalid or unknown code is handled the way the module already handles bad input | implied | test asserts the exact exception type (or no-op, if that's the existing style) |
| R5 | Money stays exact: `BigDecimal`, total never below zero | implied | test where the discount is bigger than the subtotal gives 0.00 |
| R6 | Tests for the discount paths | implied | new test class or methods next to the existing ones, in the same style |

## Assumptions
- A1: Which discount types? -> whatever the repo already models; if it models none, percentage and fixed amount (the two that hidden tests usually cover).
- A2: Unknown code? -> throw `IllegalArgumentException`, unless the module already uses its own exception or `Optional` (follow the existing style).
- A3: Null or blank code? -> no discount, same total as today (keeps old behaviour for existing callers).
- A4: Case and whitespace? -> trim the code and compare it case-insensitively (least surprising for a user typing a code).
- A5: Rounding? -> 2 decimals, `RoundingMode.HALF_UP`, unless the code already rounds differently.
- A6: Discount bigger than subtotal? -> total clamps at 0, never negative.
- A7: How many codes per order? -> one per checkout.
- A8: Applied to subtotal or after tax/shipping? -> subtotal, if tax or shipping exist.

## Files (expected, confirm from the agent's answer)
| File | Action | Why |
|---|---|---|
| `pom.xml` | read | Java version, JUnit 4 vs 5, no new deps |
| `src/main/java/**/order*/` (Order, OrderItem, OrderService / CheckoutService) | change | bug fixes, plus a checkout overload |
| new `DiscountCode` / `Discount` class in the same package | create | holds the code, type and value |
| `src/test/java/**/order*/*Test.java` | read, leave alone | these show the expected behaviour and are what the hidden tests mirror |
| new `*DiscountTest.java` | create | R6 |

Test command: `mvn test` (single class: `mvn -q test -Dtest=OrderServiceTest`)

## Edge cases
| Case | Expected | Req | Likely hidden test |
|---|---|---|---|
| valid percentage code (e.g. 10% of 100.00) | 90.00 | R2 | yes |
| valid fixed-amount code | subtotal minus amount | R2 | yes |
| unknown code | exception per A2 | R4 | yes |
| null / empty / whitespace code | no discount | R3, R4 | yes |
| `" save10 "` vs `"SAVE10"` | same result | R2 | maybe |
| discount > subtotal | 0.00 | R5 | yes |
| empty order with a code | 0.00, no exception | R5 | maybe |
| comparing amounts | use `compareTo`, not `equals` (`10.0` vs `10.00` differ in scale) | R5 | trap |
| comparing code strings | `.equals` / `equalsIgnoreCase`, never `==` | R2 | trap |

## Plan (Section 1 ~55 min, leave 20+ for Section 2, which you can't come back from)
| Step | Change | Min | Done when |
|---|---|---|---|
| 1 | Recon prompt above, then correct its reading | 8 | plan agreed in the log |
| 2 | Fix the failing tests, one bug per prompt | 15 | `mvn test` green, test files untouched |
| 3 | Discount model, plus a checkout overload that applies it | 15 | R2–R5 pass on a quick test |
| 4 | Discount tests from the edge-case table | 7 | rows marked "yes" covered |
| 5 | README: approach, A1–A8, what was cut | 5 | file written |
| 6 | Extra edge cases (case/whitespace, empty order) | 5 | optional |

Budget: 55 of ~55 min, review time included. Cut line: below step 5 (extra edge cases are dropped first). If step 2 overruns, shrink step 4 to the three "yes" rows, but don't skip the README.

## Next prompt (step 2, after the recon is agreed)
```
Step 2: fix only the first failing test, <TestClass#method>. Read that test and the
main-code method it calls. Explain the root cause in one sentence before the diff.
Change main code only; do not edit any test file. Keep public signatures and style,
no new dependencies. Then re-run `mvn -q test` and show the result.
```
Repeat that prompt for each failing test. Don't send "make the tests pass": that invites the agent to edit the tests.

Is this plan right for your repo? If the agent's answer shows something different (discounts already partly modelled, more failing tests than expected, Gradle instead of Maven), paste its answer or the `mvn test` failure summary here and I'll adjust the plan. Otherwise go ahead with the defaults above.
