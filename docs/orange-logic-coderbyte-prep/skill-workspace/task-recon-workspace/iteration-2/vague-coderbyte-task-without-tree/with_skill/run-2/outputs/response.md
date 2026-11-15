You don't need to paste the tree. Their AI panel can read the repo itself, so the first prompt has it read the repo and run the tests, with edits forbidden. Below is the full recon with a default for everything I can't see yet. Paste **Recon prompt** now. When it answers, check its reading against the tables below. Then send **Step 1 prompt**.

## Goal
Make the orders module's existing tests pass by fixing production code, then let checkout accept an optional discount code that lowers the order total.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | Every currently failing orders test passes | explicit | `mvn test` green for the orders module |
| R2 | The fix is in production code. Given tests are not edited | implied | `git diff src/test` shows no changes to existing test files |
| R3 | Checkout accepts a discount code and the total is reduced by it | explicit | 10% code on 100.00 gives 90.00 |
| R4 | Checkout without a code behaves exactly as before | implied | old checkout tests still pass, with the old signature unchanged |
| R5 | An invalid code is rejected and leaves the order unchanged | implied | unknown code throws and the total is unchanged |
| R6 | Money stays exact and never goes negative | implied | totals are `BigDecimal` with scale 2 (or whatever type the repo already uses) and the minimum is 0.00 |
| R7 | README lists assumptions and what was cut | implied (AI-fluency grading) | README section exists |

## Assumptions
- A1: Which Java and build tool? -> Java with Maven, `mvn test`. If the panel finds `build.gradle`, use `./gradlew test` and nothing else changes.
- A2: Should we fix the tests or the code? -> Fix the code. Change a test only if it contradicts the task statement, and say so in the README. "Make the tests pass" by editing the tests loses credit.
- A3: Where does the code go in? -> Keep the existing `checkout(...)` and add an overload with an extra `String discountCode` parameter that delegates to it. If the code already has a discount hook (a field, a stub, a TODO, a `Discount` class), use that instead.
- A4: What kind of discount? -> A percentage off the subtotal, read from an in-memory map of code to percent inside the module. Add fixed-amount codes only if the task or the existing code already defines them. It's the smallest model that satisfies "discount codes".
- A5: Are codes case-sensitive? -> No. Trim the code and match it case-insensitively, so " save10 " works like "SAVE10" and a user typing the code isn't surprised.
- A6: What happens with an unknown code? -> Throw `IllegalArgumentException` and leave the order untouched. If the module already has its own exception (for example `InvalidOrderException`), throw that instead. Silently charging full price is the worse surprise.
- A7: What about a null or blank code? -> No discount and the same total as no-code checkout, because the code is optional.
- A8: How is money rounded? -> Compute the discount as a `BigDecimal`, round it `HALF_UP` to 2 decimals and subtract it. Then clamp the total at 0.00. If the repo stores long cents, stay in cents with the same rounding.
- A9: Is the discount applied before or after tax and shipping? -> To the subtotal before tax and shipping, if those exist.
- A10: Can codes stack? Do they expire or have a minimum order? -> One code per checkout. No expiry, minimum order or usage limits unless the task text mentions them. This goes in the README under "cut".

## Files
| File | Action | Why |
|---|---|---|
| `pom.xml` | read, leave alone | Java version and test framework (JUnit 4 or 5). No new dependencies |
| `src/test/java/**/orders/*Test.java` | read, leave alone | Shows what the failing tests expect and how hidden tests will be shaped |
| `src/main/java/**/orders/*` (order, checkout service, line item) | change | Bug fixes and the discount overload |
| `src/main/java/**/orders/DiscountCode*.java` | create, only if no discount type exists | Code-to-percent lookup |
| `src/test/java/**/orders/DiscountCodeTest.java` | create | New tests, without touching the given test files |
| `README.md` | change or create | Assumptions A1 to A10 and what was cut |
| `target/`, any harness or `Main` reading stdin | leave alone | Generated or graded |

Test command: `mvn test`. For one class use `mvn -q test -Dtest=OrderServiceTest`. In a multi-module repo use `mvn test -pl orders -am`.

## Edge cases
| Case | Expected (chosen behaviour) | Req | Likely hidden test |
|---|---|---|---|
| Each currently failing test | passes with no edit to the test | R1, R2 | yes |
| Checkout with no code, or with the old signature | same total as before | R4 | yes |
| Valid 10% code on 100.00 | 90.00 | R3 | yes |
| "save10" or " SAVE10 " | same result as "SAVE10" (A5) | R3 | maybe |
| Unknown code "BOGUS" | `IllegalArgumentException`, order total unchanged (A6) | R5 | yes |
| null or "" code | full total, no exception (A7) | R4 | yes |
| 10% on 19.99 | 17.99 (discount 1.999 rounds to 2.00) | R6 | maybe |
| 100% code, or a discount bigger than the subtotal | 0.00, never negative | R6 | maybe |
| Empty order with a valid code | 0.00 | R3, R6 | maybe |
| `assertEquals(new BigDecimal("90.00"), total)` | passes because the result has `setScale(2)`. `BigDecimal.equals` compares scale | R6 | yes |

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 0 | Recon prompt, then correct the agent's reading | 6 | the agent's assumptions match A1 to A10 |
| 1 | Run tests and fix the root cause of the first failing test | 8 | that test passes and no test file changed |
| 2 | Fix each remaining failure, one bug per prompt | 10 | `mvn test` fully green (R1, R2) |
| 3 | Add the discount overload with a valid percentage code and a test | 10 | 100.00 with 10% gives 90.00. Old tests still pass (R3, R4) |
| 4 | Handle unknown, null, case, clamping and rounding, with tests | 7 | the "yes" rows above pass (R5, R6) |
| 5 | README: approach, A1 to A10, what was cut | 5 | file written (R7) |
| 6 | Final full `mvn test`, review the diff, submit Section 1 | 5 | green and submitted |
| 7 | Tests for the "maybe" rows | spare only | rows covered |

Budget: 51 of about 53 min left in Section 1, review time included. Section 2 (written, you can't go back) keeps its 20+ min. Cut line: below step 6. Step 7 goes first. If time is short, trim step 4 down to the unknown-code and null-code rows, but never skip README or the final run.

## Recon prompt (paste first)
```
Task: "The orders module has failing tests. Fix them, then add support for discount codes at checkout."

Do not edit or create any files yet. You can read the repo: start with pom.xml, the orders
module's source and tests, and run `mvn test` and report exactly which tests fail and the
assertion or exception for each. Then:
1. Restate the task in one sentence.
2. List requirements as a numbered checklist, each marked explicit or implied,
   each with how a test would check it.
3. List assumptions for anything ambiguous, each with the default you pick and why.
4. List the files to read, change and leave alone.
5. List edge cases, each with the expected behaviour you chose, marking the ones
   hidden tests most likely check.
6. Propose a step plan where each step changes one function or fixes one bug,
   with minutes per step for a 50-minute limit and a cut line.
Respect existing signatures and style; no new dependencies; never edit existing test files.
```
When it answers, check two things. Does it propose fixing code rather than tests (A2)? Are its discount defaults the same as A3 to A8? If not, correct it in one line, with the reason. That correction counts in your favour in the graded log.

## Step 1 prompt (first implementation prompt)
```
Step 1 only. Take the first failing test from the `mvn test` output. Read that test method
and the production class it calls. Fix the root cause of that one failure in production code.
Constraints: do not edit any file under src/test, keep all public signatures, no new
dependencies, follow the existing style and exception types, and do not start on discount
codes. If money is involved, keep BigDecimal (compare with compareTo, return scale 2).
Before the diff, explain the cause in one sentence and name the line. After the diff, run
`mvn test` and list what still fails.
```
For the next failures, send: "Step 2: next failing test `<name>`. Same constraints. Cause first, then the diff." If it edits a test, reject it: "Rejected: that changes the test instead of the bug (R2). Fix it in `<class>` instead."

---

**Defaults most worth checking:** A4 (percentage-only codes), A6 (unknown code throws `IllegalArgumentException`) and A3 (overload instead of changing `checkout`). Once the agent shows you the code, switch any of these to whatever the repo already does.

**One question:** does the task text say anything more about discount codes, such as which codes exist, percent versus fixed amount, or what happens with an invalid code? If it does, paste that line, because hidden tests come from its exact wording.

Before step 1, confirm the plan, or just paste the agent's recon answer here and I'll check it. No code until then.
