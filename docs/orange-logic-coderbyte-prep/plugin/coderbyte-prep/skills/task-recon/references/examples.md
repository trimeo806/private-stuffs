# Worked example

Read this when you want to see a complete recon in the output template, or when unsure how detailed a row should be.

Input: "Coderbyte task, 45 minutes, in-IDE agent. Fix `paginate(items, page, pageSize)`: the last page repeats the previous page's final item. Then add an optional `sortBy` field name. TypeScript, Vitest." Tree: `src/paginate.ts`, `src/types.ts`, `test/paginate.test.ts`.

The in-IDE agent has no plan yet, so the output has both a recon prompt and a step 1 prompt. Every assumption and every edge case carries a chosen behaviour, including the ones that depend on code not read yet.

```markdown
## Goal
Make paginate return correct, non-overlapping pages and accept an optional sortBy field.

## Requirements
| # | Requirement | Type | Done when |
|---|---|---|---|
| R1 | Last page has no item from the previous page | explicit | 7 items, size 3, page 3 returns 1 item |
| R2 | Optional sortBy sorts ascending by that field before slicing | explicit | test with unsorted input |
| R3 | Existing call paginate(items, page, pageSize) unchanged | implied | existing tests still pass |
| R4 | Input array not mutated by sorting | implied | test compares input after call |

## Assumptions
- A1: page is 1-based? -> yes (existing test calls page 1 for the first items)
- A2: page past the end? -> empty array, no throw (matches current behaviour)
- A3: page 0 or negative? -> empty array; switch to throwing RangeError only if paginate already validates arguments (least surprise for existing callers)
- A4: sortBy names a field some items lack? -> those items keep their original relative order after the others (stable sort, no throw)

## Files
| File | Action | Why |
|---|---|---|
| src/paginate.ts | change | bug and feature |
| src/types.ts | change | add sortBy to options |
| test/paginate.test.ts | read, leave alone | 1-based pages, style |
| test/paginate.sort.test.ts | create | new tests without touching the given file |

Test command: `npx vitest run`

## Edge cases
| Case | Expected (chosen behaviour) | Req | Likely hidden test |
|---|---|---|---|
| 7 items, size 3, page 3 | exactly the 7th item | R1 | yes |
| items an exact multiple of pageSize, page after the last | [] | R1, A2 | yes |
| empty items | [] | R1 | yes |
| page 0 | [] (A3) | A3 | maybe |
| sortBy on a missing field | stable, items without it last (A4) | R2 | maybe |
| input array after a sorted call | unchanged order | R4 | yes |

## Plan
| Step | Change | Min | Done when |
|---|---|---|---|
| 1 | fix slice bounds | 10 | R1 tests pass |
| 2 | add sortBy on a copied array | 15 | R2, R4 pass |
| 3 | README: A1 to A4, what was cut | 5 | defaults written down |
| 4 | extra edge-case tests | 10 | table above covered |

Budget: 40 of 45 min, review time included. Cut line: below step 3 (extra edge-case tests get dropped).

## Recon prompt (paste first)
The in-IDE template with <N> = 40, the task statement and tree attached.

## Step 1 prompt (first implementation prompt)
Read src/paginate.ts and test/paginate.test.ts. Fix only the off-by-one in paginate: for 7 items with pageSize 3, page 3 must return exactly the 7th item, and an exact multiple of pageSize must not produce an extra page. Pages are 1-based; a page past the end returns []. Keep the signature and style, no new dependencies, do not edit test/paginate.test.ts, do not touch sorting yet. Explain the cause in one sentence before the diff.
```

Defaults to check: A3 (page 0 returns [] rather than throwing). Confirm the plan before step 1.
