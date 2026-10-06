# Phase 5: Relational Data and Persistence

## Purpose

Replace document-store instincts with relational modeling, SQL competence, and safe Go persistence using PostgreSQL and `pgx`.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Philosophy and Design Rationale](#philosophy-and-design-rationale)
- [Tables, Keys, and Constraints](#tables-keys-and-constraints)
- [Normalization and SQL](#normalization-and-sql)
- [Indexes and Query Plans](#indexes-and-query-plans)
- [Transactions, Isolation, and Locks](#transactions-isolation-and-locks)
- [Migrations](#migrations)
- [Go Access with pgx](#go-access-with-pgx)
- [JSONB and Backup Boundaries](#jsonb-and-backup-boundaries)
- [Decision Table](#decision-table)
- [Patterns, Pitfalls, and AI](#patterns-pitfalls-and-ai)
- [Hands-On Exercises](#hands-on-exercises)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

PostgreSQL is not MongoDB with rows. A relational design states identity, relationships, cardinality, and invariants so every concurrent writer shares one source of truth. SQL is a declarative language: specify the result, then inspect how PostgreSQL executes it.

## Agent & Skills

- **Agents:** `backend-architect`, `developer`
- **Skills:** `postgres-pro`, `golang-pro`
- **Handoffs:** give Phase 6 migrations, an ER diagram, transactional repository operations, representative data, `EXPLAIN ANALYZE` evidence, pool settings, and a demonstrated rollback.

## Philosophy and Design Rationale

Put durable invariants as close to durable data as possible. Application validation improves errors; constraints guarantee truth despite races, scripts, and future callers. Prefer explicit SQL over a hidden object mapper while learning: query shape, locks, round trips, and errors remain visible.

Go’s explicit errors and contexts fit PostgreSQL well. `pgx` exposes PostgreSQL features and pooling without pretending rows are objects. Keep SQL in adapters, map rows deliberately, and let use cases own transaction scope.

## Tables, Keys, and Constraints

Trackr’s core relations:

- `users(id, email, password_hash, created_at)` with unique normalized email;
- `projects(id, name, created_by, created_at)`;
- `project_members(project_id, user_id, role)` with composite primary key;
- `issues(id, project_id, number, title, body, status, assignee_id, version, created_by, created_at, updated_at)`;
- `labels(id, project_id, name)` and `issue_labels(issue_id, label_id)`;
- `comments(id, issue_id, author_id, body, created_at)`.

Use primary keys for identity, foreign keys for references, `NOT NULL` for required facts, `UNIQUE` for alternate keys, and `CHECK` for row-local rules. Scope issue numbers and label names by project. Decide foreign-key deletion behavior explicitly; accidental cascades are dangerous, while blanket restrictions impede intentional cleanup.

MongoDB application-side references can drift. PostgreSQL foreign keys prevent orphaned rows. Java/JPA annotations are not the database guarantee unless the resulting schema actually contains the constraint.

## Normalization and SQL

Normalize facts that change independently. Project membership, labels, and comments deserve relations. Denormalize only for a measured read need and define how the duplicate stays correct.

Essential SQL skills:

- `JOIN` combines related rows; know when inner versus left joins preserve absence;
- aggregates use `GROUP BY`, with care to avoid multiplication across several one-to-many joins;
- subqueries and CTEs name intermediate relations; CTEs are for clarity or semantics, not automatic speed;
- window functions compute values across related rows without collapsing them;
- `RETURNING` retrieves generated values without a second query.

Parameterized query:

```sql
SELECT id, project_id, title, status, version, updated_at
FROM issues
WHERE project_id = $1
  AND ($2::text IS NULL OR status = $2)
  AND (updated_at, id) < ($3, $4)
ORDER BY updated_at DESC, id DESC
LIMIT $5;
```

Do not interpolate values. Dynamic identifiers such as sort columns must come from an allowlist and map to fixed SQL fragments.

## Indexes and Query Plans

An index is an ordered access path with storage and write costs. Design from important query predicates and ordering. For the issue feed, `(project_id, updated_at DESC, id DESC)` can support scope, cursor, and order. A partial index may help a heavily used subset such as open issues, but only if query predicates match.

Use `EXPLAIN (ANALYZE, BUFFERS)` on representative data. Inspect estimated versus actual rows, loops, scan type, sort, buffer activity, and total time. `ANALYZE` executes the query; wrap mutating investigations safely.

A sequential scan is not inherently bad for small tables or broad queries. Do not force an index because its name sounds relevant. Recheck plans as cardinality and distribution change.

## Transactions, Isolation, and Locks

A transaction groups operations atomically. The default `READ COMMITTED` gives each statement a fresh committed snapshot; repeated reads may change. Stronger isolation prevents more anomalies but raises retry or contention costs.

Use constraints and conditional updates for ordinary conflicts. Use `SELECT ... FOR UPDATE` when a workflow must serialize decisions about existing rows. Lock rows in consistent order to reduce deadlocks, keep transactions short, and retry serialization/deadlock failures only at the complete transaction boundary.

Transaction pattern:

```go
tx, err := pool.Begin(ctx)
if err != nil { return err }
defer tx.Rollback(ctx)
// issue, labels, audit, idempotency record
if err := tx.Commit(ctx); err != nil { return fmt.Errorf("commit issue: %w", err) }
```

Deferred rollback is safe after commit with `pgx`; still check the API contract. A successful statement before a failed commit is not durable success.

## Migrations

Migrations are ordered, reviewed schema changes applied exactly once. Keep SQL explicit and versioned. A production-safe change often expands then contracts:

1. add nullable/new structure compatibly;
2. deploy code that writes both or uses the new structure as required;
3. backfill in bounded batches;
4. validate constraints;
5. switch reads;
6. remove obsolete structure in a later clean cutover.

For this single Trackr codebase, migrate all current callers and remove obsolete application paths. Test migrations against realistic data. Understand which DDL takes locks and whether the migration tool wraps transactions. Never edit an already-applied migration; add another.

## Go Access with pgx

Use `github.com/jackc/pgx/v5/pgxpool` when this phase introduces PostgreSQL. Configure a bounded pool from deployment capacity, not defaults alone. Across all replicas, leave room for migrations and administration.

Pass contexts to `Query`, `QueryRow`, and `Exec`. Close `Rows`, check scan errors, and check `rows.Err()` after iteration. Convert PostgreSQL-specific error codes to domain categories in the adapter without leaking driver types inward.

Avoid N+1 queries: fetch a page and its needed related data with an intentional join or small bounded query set. Do not keep transactions open while calling external services.

## JSONB and Backup Boundaries

`JSONB` is appropriate for sparse extension metadata that is not central to joins, constraints, or common filtering. It is not an escape hatch from modeling. Promote frequently queried or constrained properties to columns. Document JSON schema and evolution even if PostgreSQL does not enforce all of it.

Backups are only useful when restorable. Understand logical versus physical backup, point-in-time recovery, retention, encryption, access, and recovery objectives. In learning, perform a restore into a separate database and verify counts and representative journeys; a successful backup command alone proves little.

## Decision Table

| Decision | Prefer | Trade-off / alternative |
|---|---|---|
| Normalize vs duplicate | Normalize independently changing facts | Denormalize measured hot reads with synchronization plan |
| Database constraint vs app check | Both: constraint guarantees, app explains | App-only loses races; constraint-only gives poor UX |
| Surrogate vs natural key | Stable opaque primary key plus unique natural key | Natural keys simplify some joins but can change/leak meaning |
| Join vs N+1 | Intentional set query | Several bounded queries may avoid row multiplication |
| Optimistic vs row lock | Versioned conditional update | Row lock when decision must serialize, with contention cost |
| `READ COMMITTED` vs stronger | Default plus atomic statements/constraints | Serializable for cross-row invariants with retries |
| Column vs JSONB | Column for important typed fact | JSONB for sparse extension data |
| ORM vs explicit SQL | SQL + pgx | ORM can speed common CRUD but obscures plans/features |

## Patterns, Pitfalls, and AI

**Correct:** parameterized values and checked iteration. **Incorrect:** concatenating input into SQL, ignoring `rows.Err()`, or beginning/committing transactions inside unrelated repository calls.

Common pitfalls: nullable columns without domain meaning; redundant indexes; timestamp-only cursors; accidental cascade deletion; `SELECT *`; long idle transactions; pool size multiplied across replicas; migration and application deployed incompatibly; assuming MongoDB-style embedded snapshots update themselves.

Ask AI for schema counterexamples, query alternatives, and likely anomalies. Independently verify SQL in PostgreSQL, inspect constraints, seed representative distributions, and compare query plans. Never execute generated destructive SQL without review and a recovery path.

Primary resources: [PostgreSQL documentation](https://www.postgresql.org/docs/current/), [Transactions](https://www.postgresql.org/docs/current/tutorial-transactions.html), [`EXPLAIN`](https://www.postgresql.org/docs/current/using-explain.html), and [pgx documentation](https://pkg.go.dev/github.com/jackc/pgx/v5).

## Hands-On Exercises

1. Convert a MongoDB-style nested project document into relations; document cardinalities and deletion rules. **Evidence:** ER diagram and constraints.
2. Write joins for issue labels/comments and an aggregate for issue counts by status. **Evidence:** expected and actual rows including empty relationships.
3. Race two membership or issue-number inserts. **Evidence:** the database constraint preserves the invariant.
4. Compare issue-feed plans before and after one candidate index on representative data. **Evidence:** full plan and explanation, not “index scan is faster.”
5. Induce a failure after issue insert but before labels/audit. **Evidence:** transaction leaves no partial state.

## Exit Exercise

Design and migrate Trackr users, projects, memberships, issues, labels, issue-labels, and comments. Implement transactional issue creation and stable cursor pagination using `pgx`; classify constraint errors safely. Run `EXPLAIN ANALYZE` to justify one index and perform a backup restore into a separate database.

**Exit gate:** constraints reject invalid/orphaned data, induced failure rolls back, concurrent writes preserve uniqueness, pagination is deterministic, the index claim is plan-backed, pool limits are justified, and restored data supports a representative Trackr journey.

## Related Documents

- [Previous: API Architecture](04-api-architecture.md)
- [Roadmap Navigation](README.md)
- [Next: Reliability, Concurrency, and Testing](06-reliability-concurrency-testing.md)
