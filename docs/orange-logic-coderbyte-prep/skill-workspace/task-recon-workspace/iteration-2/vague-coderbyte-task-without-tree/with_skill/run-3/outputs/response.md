Short answer: paste the **Recon prompt** below first. It tells their agent to read the repo and run the tests without editing anything. Once you've checked its answer, send the **Step 1 prompt**. I haven't seen the tree, so every point that depends on it has a default you can switch if the code says otherwise.

## Goal
Get the orders module's existing tests passing, then add discount codes at checkout without breaking the current checkout API. These are two deliverables, in that order.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | Every orders test that fails now passes, fixed in production code and not in the tests | explicit | `mvn test` shows 0 failures and 0 errors in orders |
| R2 | Checkout accepts a discount code and lowers the total | explicit | a test with a valid code gets the discounted total |
| R3 | Checkout without a code works exactly as before | implied (hidden tests compile against the current API) | the old checkout tests still pass, with the old signature unchanged |
| R4 | An invalid code is rejected the same way every time | implied | an unknown code throws the exception chosen in A4 |
| R5 | Money stays exact | implied | totals are `BigDecimal` at scale 2, or whatever money type the code already uses |
| R6 | README covers assumptions, what was cut and why | implied (AI-fluency is graded on ownership) | README section written |

## Assumptions
- A1: Where do codes live? -> An in-memory `Map<String, DiscountCode>` in a small `DiscountCodeRepository`/registry class seeded with a few codes. Use the existing Discount/Coupon/Promo class or repository if the repo has one. This needs no new dependency, no DB and no config.
- A2: Discount kinds? -> Percentage (e.g. 10%) and fixed amount (e.g. 5.00), as an enum `DiscountType { PERCENTAGE, FIXED }`. These are the two kinds a hidden test is most likely to name.
- A3: How are codes matched? -> Trimmed and case-insensitive (`"save10"` == `"SAVE10"`). This surprises a shopper least.
- A4: What happens with an unknown code? -> Throw `IllegalArgumentException`. If the orders module already has its own exception (e.g. `InvalidOrderException`), throw that one instead. Failing loudly is safer than silently charging full price.
- A5: What happens with a null or blank code? -> No discount and the same total as before, with no exception. That keeps R3 intact.
- A6: Is the discount applied before or after tax and shipping? -> To the item subtotal, before tax, with shipping not discounted. Switch only if the existing checkout already computes a total another way. This is the most common reading.
- A7: What if the discount is bigger than the subtotal? -> The total floors at 0.00 and never goes negative.
- A8: How are amounts rounded? -> `BigDecimal`, `setScale(2, RoundingMode.HALF_UP)`, applied once on the discount amount. If the code uses `long` cents or `double`, keep its type and don't refactor it.
- A9: How do we add the discount without breaking the API? -> Add an overload `checkout(..., String discountCode)` and make the old method delegate with `null`. Old callers and hidden tests keep compiling.
- A10: Can a test that looks wrong be changed? -> No by default. Only change it if it contradicts the task statement, and if you do, note it in the README.
- A11: Do we need expiry dates, minimum order, stacking or usage limits? -> No, they're cut: one code per checkout. They are listed in the README as not done.

## Files (to be confirmed by the agent's recon reply)
| File | Action | Why |
|---|---|---|
| `pom.xml` (root + orders module) | read | Java version, JUnit 4 or 5, module name for `-pl` |
| `orders/src/test/java/**` | read, leave alone | They show what "fixed" means and how hidden tests are shaped |
| orders classes under test (Order, OrderItem, OrderService/Checkout) | change | bug fixes for R1, checkout overload for R2 |
| `DiscountCode`, `DiscountType`, registry | create (or extend the existing class, A1) | R2 to R4 |
| `orders/src/test/java/.../DiscountCodeTest.java` (or CheckoutDiscountTest) | create | new tests, so the given test files stay untouched |
| `README.md` | change | R6 |
| `target/`, lock files, any Coderbyte harness files | leave alone | generated or graded |

Test command: `mvn -q test`. If it's multi-module, use `mvn -q -pl orders -am test`. For a single test: `mvn -q -pl orders -am test -Dtest=ClassName`.

## Edge cases
| Case | Expected (chosen behaviour) | Req | Likely hidden test |
|---|---|---|---|
| Checkout with no code / `null` / `""` / `"  "` | same total as before, no exception (A5) | R3 | yes |
| Valid percentage code, e.g. 10% off 100.00 | 90.00 | R2 | yes |
| Valid fixed code, e.g. 5.00 off 20.00 | 15.00 | R2 | yes |
| Code in different case or with spaces, `" save10 "` | same as `SAVE10` (A3) | R2 | maybe |
| Unknown code | `IllegalArgumentException`, or the module's own exception (A4) | R4 | yes |
| Fixed discount larger than subtotal, 50.00 off 20.00 | 0.00, never negative (A7) | R2 | yes |
| Percentage on an amount that rounds, 15% of 33.33 | discount 5.00, total 28.33, HALF_UP at scale 2 (A8) | R5 | maybe |
| Empty order (no items) with a valid code | 0.00 total; or the existing empty-order exception if checkout already throws one | R2, R3 | maybe |
| `BigDecimal` equality in tests | compare with `compareTo`, not `equals` (`10.0` vs `10.00`) | R5 | n/a (pitfall) |
| The failing-test fixes | each fix keeps the other currently green tests green | R1 | yes (regression) |

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Paste the recon prompt, then check the agent's tree, failing tests and causes against A1 to A11 | 6 | Failures listed with one cause each; defaults confirmed or switched |
| 1 | Fix the first failing root cause (one bug) | 8 | Those tests pass and nothing else turned red |
| 2 | Fix each remaining root cause, one prompt per bug | 10 | `mvn test` green in orders (R1) |
| 3 | `DiscountCode` + `DiscountType` + registry lookup (A1 to A4), test first | 7 | Lookup tests for valid, mixed-case and unknown codes pass |
| 4 | Checkout overload applying the discount (A5 to A9), old method delegates | 10 | R2 and R3 pass, including the floor-at-zero case |
| 5 | README: approach, A1 to A11, what was cut (A11) | 5 | README written |
| 6 | Extra edge-case tests from the table (rounding, empty order, blank code) | 7 | Rows covered and the suite is green |
| 7 | Optional: expiry or minimum order | 0 | Only if time is left |

Budget: 53 of about 55 min for Section 1, diff review included, plus 2 min to submit. That leaves at least 20 min for Section 2, the written part you can't go back to. Cut line: below step 5, so step 6's extra tests are dropped first and step 7 is dropped entirely. If the failing tests eat into the time, cut the fixed-amount type from A2 and keep percentage only, then say so in the README.

## Recon prompt (paste first)
```
Task: "The orders module has failing tests. Fix them, then add support for
discount codes at checkout."

Do not edit or create any files yet. First:
0. Show the project tree for the orders module (src/main and src/test) and the
   Java and JUnit versions from pom.xml. Run `mvn -q test` (or
   `mvn -q -pl orders -am test`) and list each failing test with the
   one-sentence root cause you believe causes it.
1. Restate the task in one sentence.
2. List requirements as a numbered checklist, each marked explicit or implied,
   each with how a test would check it.
3. List assumptions for anything ambiguous, each with the default you pick and why.
   Cover at least: where discount codes are defined, percentage vs fixed amount,
   case sensitivity, unknown code, null/blank code, discount larger than the
   subtotal, rounding, and how checkout's signature changes.
4. List the files to read, change and leave alone.
5. List edge cases, each with the expected behaviour you chose, marking the ones
   hidden tests most likely check.
6. Propose a step plan where each step changes one function or fixes one bug,
   with minutes per step for a 50-minute limit and a cut line.
Respect existing signatures and style; no new dependencies; do not modify
existing tests to make them pass.
```
When it answers, compare its defaults with A1 to A11 above. Correct anything that differs in your next message, with a reason. For example: "Use HALF_UP at scale 2, not double, because money must stay exact." Graders read that correction as judgement.

## Step 1 prompt (first implementation prompt)
```
Step 1 only: fix the first failing root cause from your list, <cause, e.g.
"Order.getTotal ignores item quantity">, which fails <TestClass#testMethod>.
Read <the test class> and <the production class it tests> before changing anything.
Change production code only. Do not edit any file under src/test, do not change
public method signatures, add no dependencies, follow the existing style and
exception types. Do not touch discount codes or the other failing tests yet.
The tests that pass today must still pass. Compare BigDecimal with compareTo,
not equals.
Explain the cause in one or two sentences before showing the diff, then run
`mvn -q test` and report which tests still fail.
```

The defaults most worth checking are A4 (an unknown code throws instead of being ignored), A6 (the discount comes off the subtotal before tax and shipping) and A9 (an overload, not a changed signature). The agent's tree may already decide these.

Question: once you have the agent's recon reply, can you paste its list of failing tests and its checkout method signature? Then I can lock A4, A6 and A9 to the real code.

Confirm the plan, or tell me what's off, before you send the step 1 prompt. No code is written until then.
