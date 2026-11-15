Paste the **Recon prompt** below into their AI panel first. It makes the agent read the repo and run the tests without editing anything. Don't wait on the tree: everything below has a default, and the agent's answer stands in for the tree.

## Goal
Make the orders module's existing tests pass by fixing production code, then let checkout take an optional discount code that lowers the order total.

There are two deliverables, in order: (1) bug fixes, (2) the discount feature. Bug fixes come first because they are the cheapest credit.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | Every currently failing test in the orders module passes | explicit | `mvn test` is green |
| R2 | Fixes go in production code only; no test file is edited and no passing test breaks | implied | `git diff` shows no change under `src/test` from the bug-fix steps |
| R3 | Checkout accepts a discount code and lowers the total | explicit | a new test shows 100.00 with `SAVE10` gives 90.00 |
| R4 | Checkout without a code behaves exactly as before, with the same signature | implied | old tests pass and the old call still compiles (hidden tests use the current API) |
| R5 | An unknown code is rejected clearly and does not change the order | implied | a test expects the exception and the total is unchanged |
| R6 | The total is never negative and money is rounded to 2 decimals | implied | tests for over-discount and rounding pass |
| R7 | The README lists assumptions, what was cut and how to run | implied | README section exists |

## Assumptions
- A1: Can a failing test be "wrong"? -> no. Fix the production code and never edit the given tests (the hidden tests are likely copies of them).
- A2: Where do valid codes live? -> a small in-memory catalog (an enum or `Map<String, Discount>`) inside the orders module, passed into or held by the checkout class. Reuse an existing `Discount`/`Coupon`/`Promo` class if the repo has one (less new surface, and hidden tests may already target it).
- A3: What kinds of discount? -> percentage off and fixed amount off, each one field on the discount. If time is short, keep percentage only and write the cut in the README.
- A4: How do codes match? -> trim, then compare case-insensitively (`toUpperCase(Locale.ROOT)` on the key). Users type codes in lowercase, and nothing a caller expects breaks.
- A5: What happens with an unknown code? -> throw `IllegalArgumentException`, or the module's own exception type if it already has one (e.g. `InvalidOrderException`). Silently ignoring the code would hide a pricing error.
- A6: What about a null or blank code? -> no discount, same result as checkout without a code (backward compatible).
- A7: Can a discount exceed the subtotal? -> clamp the total at 0.00, never negative.
- A8: Is the discount applied before or after tax/shipping? -> on the item subtotal, before tax and shipping, if those exist. That is the common retail rule, and it is easiest to test.
- A9: What money type and rounding? -> keep whatever type the order total already uses (changing it breaks hidden tests that compile against it). If it is `BigDecimal`, use `setScale(2, RoundingMode.HALF_UP)` and compare with `compareTo`, not `equals`.
- A10: Can codes stack? -> one code per checkout. Stacking, expiry, usage limits and minimum order go below the cut line, unless the existing model already has those fields.
- A11: What is the API shape? -> add an overload `checkout(<existing args>, String discountCode)` and keep the existing `checkout(...)` unchanged, delegating to the new one with `null`.

## Files
Names are guesses until the agent's recon answer comes back. Swap in the real paths then.

| File | Action | Why |
|---|---|---|
| `pom.xml` | read, leave alone | Java version, JUnit 4 or 5, no new dependencies |
| `README.md` / task notes | read, then change at the end | may define discount rules; write-up goes here |
| `src/test/java/**/order*/*Test.java` | read, leave alone | failing tests show the bugs and the hidden-test style |
| `src/main/java/**/order*/` (Order, OrderService/Checkout, OrderItem) | change | bug fixes, plus the discount overload |
| `Discount.java` / `DiscountCode` enum in the orders package | create, unless one exists (A2) | code catalog and calculation |
| `src/test/java/**/order*/CheckoutDiscountTest.java` | create | new tests, kept out of the given test files |
| `target/`, `.mvn/`, lock or generated files | leave alone | build output and harness files |

Test command: `mvn -q test`. For one class: `mvn -q test -Dtest=OrderServiceTest`. Use `./mvnw` if there is a wrapper.

## Edge cases
| Case | Expected (chosen behaviour) | Req | Likely hidden test |
|---|---|---|---|
| each currently failing test | passes, with no test file edited | R1, R2 | yes |
| checkout with no code | same total as before the change | R4 | yes |
| 100.00 subtotal, valid 10% code `SAVE10` | 90.00 | R3 | yes |
| `" save10 "` | same as `SAVE10`: 90.00 (A4) | R3 | maybe |
| unknown code `NOPE` | `IllegalArgumentException` (or the module's own exception, A5), order total unchanged | R5 | yes |
| `null` or `""` code | no discount, same as no code (A6) | R4 | yes |
| fixed 20.00 off a 15.00 subtotal | 0.00, not -5.00 (A7) | R6 | yes |
| 100% code | 0.00 | R6 | maybe |
| 15% off 19.99 | 16.99 (16.9915 rounded HALF_UP) | R6 | maybe |
| percent computed as `10 / 100` in int math | must not give 0 discount; use decimal math | R3 | yes (a typical planted bug) |
| empty order with a valid code | the empty-order behaviour is unchanged (existing exception if there is one, else 0.00) | R4, R6 | maybe |
| Java pitfalls in the bug hunt: `==` on Strings, `BigDecimal.equals` vs `compareTo`, int division, off-by-one loops, removing inside for-each | fixed at the cause, not by special-casing test values | R1 | yes |

## Plan
About 55 minutes for Section 1, with at least 20 kept for Section 2. A few minutes are already gone, so this plans for 50.

| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Recon prompt; correct any wrong reading in the agent's answer | 5 | failing tests are listed with causes; A1-A11 confirmed or adjusted |
| 1 | Fix the first failing bug (one cause) | 6 | its tests pass, no test file touched |
| 2 | Fix each remaining bug, one prompt per bug | 8 | `mvn test` is fully green (R1, R2) |
| 3 | Discount model and catalog with lookup (A2-A5), plus its unit tests | 8 | lookup tests pass for valid, case/space-varied and unknown codes |
| 4 | Checkout overload that applies the discount (A6-A9, A11), plus tests | 8 | R3-R6 rows of the edge-case table pass, old tests still green |
| 5 | README: assumptions, what was cut, how to run | 4 | R7 |
| 6 | Final full `mvn test`, review the whole diff, submit | 3 | green, submitted before minute ~55 |
| 7 | Optional: fixed-amount codes (if dropped), expiry/min-order, extra edge-case tests | rest | only if time is left |

Budget: 42 of 50 min, review time included, with 8 min of slack for a nasty bug. Cut line: below step 6. Step 7 is dropped first. If the bugs are still not green at minute ~25, cut fixed-amount codes (A3) and keep percentage only.

## Recon prompt (paste first)
```
Do not edit or create any files yet. Task: "The orders module has failing tests.
Fix them, then add support for discount codes at checkout."
First:
0. Show the project tree (src/main and src/test of the orders module, pom.xml),
   run `mvn -q test` (or ./mvnw), and list each failing test with its assertion
   message and the production line you think causes it.
1. Restate the task in one sentence.
2. List requirements as a numbered checklist, each marked explicit or implied,
   each with how a test would check it.
3. List assumptions for anything ambiguous, each with the default you pick and why.
   Cover at least: where valid codes are defined, percent vs fixed amount,
   case sensitivity, unknown/null code behaviour, total below zero, rounding,
   and the checkout method signature. Say if the repo or README already has a
   Discount/Coupon/Promo class or rules for codes.
4. List the files to read, change and leave alone. Test files are leave-alone.
5. List edge cases, each with the expected behaviour you chose, marking the ones
   hidden tests most likely check.
6. Propose a step plan where each step changes one function or fixes one bug,
   with minutes per step for a 45-minute limit and a cut line.
Respect existing signatures and style; no new dependencies.
```

When it answers, compare its assumptions with A1-A11 above. Correct any that differ in one short message, e.g. "Use case-insensitive matching, unknown code throws IllegalArgumentException, keep the old checkout signature." That visible correction counts in the graded log.

## Step 1 prompt (first implementation prompt)
```
Step 1: fix only the first failing test from your list, <TestClass#testName>.
Read that test and the production class it exercises before changing anything.
Explain the root cause in one or two sentences before the diff.
Constraints: change production code only, do not edit any file under src/test,
do not special-case test inputs, keep all public signatures, no new dependencies,
follow the existing style. Watch for the usual planted Java bugs: == on Strings,
BigDecimal equals vs compareTo, integer division, off-by-one loop bounds,
removing from a list inside for-each. Do not touch discount codes yet.
After the fix, run `mvn -q test` and report which tests now pass and which
still fail.
```

Defaults most worth checking: A5 (an unknown code throws rather than being ignored), A3 (percent and fixed rather than percent only) and A11 (an overload rather than a changed signature). If the repo's README or an existing class says otherwise, follow the repo.

One question: once the agent replies to the recon prompt, can you paste its failing-test list and anything it finds about discount rules (README text or an existing Discount/Coupon class)? I'll tighten A2-A5 and fill in the real file names for step 1. Please confirm the plan before sending the step 1 prompt.
