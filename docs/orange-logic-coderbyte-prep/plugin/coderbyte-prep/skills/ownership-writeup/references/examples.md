# Longer examples

Read this when you want a full model to imitate: messy notes turned into a complete SOLUTION.md, two Section 2 answers, a weak answer rewritten into a strong one, and notes with gaps.

## Example 0: short notes to a table row and an answer

Notes:

```
dedupe uploads by checksum. asked agent to plan from README + uploads.ts.
kept plan, fit the existing upload flow.
agent first impl hashed filename not bytes -> rejected, same name diff content
would collide. had it hash file bytes w/ sha256 instead.
tests: same bytes diff names -> 1 asset, diff bytes same name -> 2. both pass.
no streaming, loads whole file in memory. didnt test >100MB.
```

```markdown
## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| Plan dedupe from README and uploads.ts | Kept the plan | Matched the existing upload flow |
| Implement checksum dedupe | Rejected first version | It hashed the filename, so two different files with the same name would collide; I had it hash file bytes with SHA-256 |

## Known limits
- Whole file is loaded into memory; not verified above 100 MB -> stream the hash
```

Answer to "Give an example of AI output you rejected":

```markdown
The agent's first dedupe version hashed the filename instead of the file bytes. That is wrong for an asset library: two different files that share a name would collide and be treated as one asset. I rejected it and had the agent hash the file bytes with SHA-256 instead, because content, not naming, defines a duplicate. Two tests cover it: same bytes with different names gives one asset, and different bytes with the same name gives two. Both pass.
```

Not written: "I reviewed the diff line by line", "dedupe is fast", "a teammate approved it". None is in the notes.

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

SOLUTION.md (186 words by `wc -w`):

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

## Example 3: notes with gaps and a tempting conclusion

Notes:

```
agent added a cache to getUserProfile, TTL 10 min. i cut TTL to 60s, profile
edits must show within a minute. it also cached 404s, i removed that.
```

The notes say nothing about tests and never say edits now show within a minute in practice. Do not write "tests pass" or "edits now appear within a minute". Write:

```markdown
## Verification
- Not verified: [gap: which tests or manual checks were run, and their result]

## Risks and follow-ups
- The 60s TTL is meant to make edits show within a minute; nothing confirms this yet, e.g. a cached entry written just before an edit.
```

and ask the user once whether they ran anything.
