# Longer examples

Read this when you want a full model to imitate: messy notes turned into a complete SOLUTION.md, two Section 2 answers, and a weak answer rewritten into a strong one.

## Example 1: notes to SOLUTION.md and answers

Notes as the user wrote them:

```
task: GET /assets search endpoint, filter by tag + mime type, paginate.
gave agent: assets.ts, db schema, README "API conventions" part. asked for plan first.
plan ok, but it wanted offset pagination. i switched to cursor (createdAt,id)
bc offset skips rows when new uploads land mid-scroll.
agent impl: tag filter built SQL with string concat -> rejected, injection. made it use params.
kept: response shape, validation with zod.
tests: empty result, tag+mime combined, cursor across 2 pages, bad mime -> 400. all green.
didnt do: full text search on title, no index on tags yet.
```

SOLUTION.md (under 200 words):

```markdown
## Approach
I added GET /assets with tag and MIME type filters and cursor pagination on (createdAt, id). I chose a cursor over offset pagination because offset skips or repeats rows when new uploads arrive while a user scrolls.

## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| Plan the endpoint from assets.ts, the schema and the README API conventions | Changed: offset to cursor pagination | Offset breaks when uploads land mid-scroll |
| Implement the tag filter | Rejected the SQL string concatenation | SQL injection risk; I made it use parameters |
| Response shape and zod validation | Kept | Matched the README conventions |

## Verification
- Tests: empty result, tag and MIME combined, cursor across two pages, invalid MIME returns 400. All pass.

## Trade-offs
- Cursor pagination over offset: stable under concurrent uploads, but no "jump to page N".

## Known limits
- No full-text search on title -> add it with a search index.
- No index on tags -> add one before this sees real data volume.
```

Answer to "Give an example where AI output was wrong" (76 words):

```markdown
When I asked the agent to implement the tag filter, it built the SQL query by concatenating the tag string into it. That is an injection hole on a public search endpoint, so I rejected it and had the filter use query parameters instead. I kept the rest of its implementation, the response shape and zod validation, because they matched the README conventions. The combined tag and MIME test and the invalid MIME test both pass.
```

Answer to "How did you give the agent context?" (78 words):

```markdown
Before asking for code, I gave the agent assets.ts, the database schema and the API conventions section of the README, and asked for a plan rather than an implementation. Reviewing the plan first was cheap: I caught that it chose offset pagination and switched it to a cursor on createdAt and id before any code existed. Because the agent had the conventions up front, its response shape and validation matched the existing API and I kept them unchanged.
```

Note what is absent: no claim about performance testing, no claim about a code review by anyone else, no test that is not in the notes.

## Example 2: weak answer rewritten

Question: "How do you verify AI-generated code?"

Weak (generic, no evidence):

```markdown
I always carefully review AI-generated code and make sure it is robust and follows best practices. I leverage unit tests and manual testing to ensure quality. AI is a powerful tool, but it is important to stay in control and verify everything before shipping.
```

What is wrong: no example from the session, filler words ("robust", "leverage", "best practices"), and "verify everything" names no method.

Strong (same session as Example 1, 81 words):

```markdown
Two checks, both from this task. First, review before running: I rejected the agent's tag filter because it concatenated user input into SQL, and had it use parameters instead. Second, tests on the behaviour most likely to break: an empty result, tag and MIME combined, a cursor crossing two pages, and an invalid MIME returning 400. All pass. Review catches what the agent got wrong; tests catch what neither of us thought about, like the page boundary between two cursor pages.
```

## Example 3: notes with gaps

Notes:

```
agent added retry to s3 upload, 3 attempts exp backoff. i changed max delay 30s -> 5s, ui times out at 10s.
```

The notes say nothing about tests. Do not write "tests pass". Write the Verification section as:

```markdown
## Verification
- [gap: which tests were run and their result]
```

and ask the user once whether they ran anything.
