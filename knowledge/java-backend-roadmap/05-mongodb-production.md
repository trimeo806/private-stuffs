# Phase 5: MongoDB in Production

## Purpose

Turn familiar MongoDB work into a production-conscious Trackr persistence implementation. Model from access patterns and atomicity needs, enforce collection validation and explicit indexes, inspect plans, handle optimistic conflicts, configure bounded clients, reason about replica-set failover, protect data, and prove restore. This phase is intentionally complete enough to improve current-job judgment before PostgreSQL replaces MongoDB in Phase 6.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Philosophy and Design Rationale](#philosophy-and-design-rationale)
- [Access Patterns Before Documents](#access-patterns-before-documents)
- [Embedding, References, and Aggregate Boundaries](#embedding-references-and-aggregate-boundaries)
- [Schema Validation and Evolution](#schema-validation-and-evolution)
- [Repositories and MongoTemplate](#repositories-and-mongotemplate)
- [Explicit Indexes and Explain](#explicit-indexes-and-explain)
- [Atomicity, Optimistic Updates, and Transactions](#atomicity-optimistic-updates-and-transactions)
- [Pagination, Aggregation, and Query Shape](#pagination-aggregation-and-query-shape)
- [Replica Sets and Failover](#replica-sets-and-failover)
- [Read and Write Concerns](#read-and-write-concerns)
- [Timeouts, Pools, and Capacity](#timeouts-pools-and-capacity)
- [Security, Monitoring, and Restore](#security-monitoring-and-restore)
- [PostgreSQL Cutover Handoff](#postgresql-cutover-handoff)
- [Techniques and Evidence](#techniques-and-evidence)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [AI-Assisted Workflow](#ai-assisted-workflow)
- [Common Pitfalls](#common-pitfalls)
- [Hands-On Exercises](#hands-on-exercises)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

MongoDB's flexible documents do not remove schema, consistency, capacity, or recovery decisions. They move more of those decisions into aggregate design, validators, application workflows, indexes, driver configuration, and operations. The unit of single-document atomicity is a powerful design boundary, not a reason to place an unbounded product inside one document.

Implement the Phase 4 ports using Spring Data MongoDB without changing Trackr's HTTP contract. Use derived repositories only when an operation is obvious bounded CRUD. Use `MongoTemplate` for explicit queries, projections, conditional updates, aggregations, hints, and other work where the actual database operation must remain visible.

## Agent & Skills

- **Agents:** `backend-architect`, `developer`
- **Skills:** `core`, `problem-solving`, `test`
- **Handoff:** give Phase 6 the access-pattern catalog, document schemas/validators, explicit index definitions and plans, export mapping, record counts, invariant queries, restore evidence, and a frozen contract for the ports PostgreSQL will replace.

## Philosophy and Design Rationale

Start with the questions Trackr must answer and the facts that must change together. Document structure is an optimization for a known workload and atomicity boundary. It is not a direct serialization of Java object graphs.

Production discipline means:

- schema is explicit even when documents may evolve;
- every important query has representative plan evidence;
- consistency settings express product risk rather than folklore;
- pools, waiters, timeouts, retries, and document growth are bounded;
- failure is tested against a replica set, not simulated only by mocks;
- backup success is incomplete until restore and reconciliation succeed;
- sensitive data, credentials, and operational endpoints are protected;
- MongoDB-specific strengths remain inside the adapter rather than weakening the domain port.

## Access Patterns Before Documents

Catalog each important operation before designing collections:

| Trackr operation | Scope/filter | Sort | Returned shape | Write/atomicity need |
|---|---|---|---|---|
| Get issue | project + issue ID | none | issue summary/detail | none |
| List issues | project + status/label + cursor | updated time + stable ID | bounded projection | none |
| Create issue | project + idempotency key | none | created issue | unique project issue number and deduplication |
| Update issue | project + issue ID + version | none | new state/version | conditional single-issue transition |
| Add comment | project + issue | creation order | bounded page | comment plus optional audit fact |
| Membership check | user + project | none | role/state | must be applied to every use case/query |

For each operation estimate expected cardinality, growth, selectivity, read/write frequency, and maximum response. Include tenant/project scope as the leading access condition where it is required for isolation. Avoid indexes and projections designed from toy data.

Trackr's list contract orders by `(updatedAt DESC, _id DESC)` or another stable unique tuple. The Mongo query must match the cursor predicate and index ordering. Never page with an unbounded `skip` on a changing high-cardinality feed.

## Embedding, References, and Aggregate Boundaries

Embed when data:

- is normally read with the parent;
- has the same ownership and lifecycle;
- changes atomically with the parent;
- remains predictably bounded;
- is not independently shared or queried at scale.

Reference or separate into another collection when data:

- grows without a safe bound;
- changes or is paged independently;
- is shared by many aggregates;
- has a distinct security/lifecycle policy;
- would cause large document moves or frequent array contention.

A practical Trackr model can embed a bounded snapshot such as reporter display information while storing canonical users separately. Comments should usually be separate or strictly bounded/paged rather than an ever-growing issue array. Labels may be canonical project records with issue-side identifiers, depending on rename and query needs.

Duplication is a consistency decision. For every copied field define the source of truth, acceptable staleness, update strategy, failure recovery, and reconciliation. Do not pretend a reference gives foreign-key enforcement; application and maintenance processes own missing/invalid references.

Stay well below document-size limits and monitor growth distributions. Large arrays make updates, indexes, and working-set behavior expensive even before a hard limit is reached.

## Schema Validation and Evolution

Flexible schema means documents can evolve; it does not mean any shape is acceptable. Define collection validators for required fields, BSON types, enums, nested shapes, and bounds that the database can enforce. Align Java mapping, JSON transport, and BSON representation deliberately without making them the same class.

Version schema changes:

1. add readers that tolerate the old and new representation when necessary;
2. deploy compatible writers;
3. backfill in bounded, restartable batches with progress and conflict handling;
4. verify counts and invariants;
5. tighten the validator only after incompatible old documents are gone;
6. remove compatibility code.

Do not silently coerce an invalid production document into a default that hides corruption. Quarantine or fail with enough safe identity/context for repair.

Test validators against the real database. Spring Bean Validation protects one application path; collection validation protects other writers as configured. Neither replaces authorization or reference reconciliation.

## Repositories and MongoTemplate

Use a Spring Data repository for obvious, bounded CRUD such as a direct lookup whose method name remains clear and whose generated operation is understood. Prefer `MongoTemplate` when Trackr needs:

- compound tenant/project filters;
- projections and controlled field exposure;
- cursor predicates and explicit sort;
- conditional version updates;
- `$inc`, `$set`, array operators, or update pipelines;
- aggregation pipelines;
- explicit hints, collation, comments, or per-operation settings;
- bulk operations with defined ordering/error behavior.

Keep database-specific query construction in the outbound adapter. Application ports speak in Trackr behavior, not `Query`, `Criteria`, `Document`, or repository paging types.

Repository convenience is not proof of efficient or safe behavior. Inspect the derived query and plan. Avoid `save` for a partial update when replacement semantics, lost fields, or extra round trips are unclear. Prefer an explicit conditional update when the business operation is “change status if current version is N.”

Do not create a generic repository interface over MongoDB and PostgreSQL. Their constraints, transaction behavior, query languages, and optimized access paths differ.

## Explicit Indexes and Explain

Treat indexes as reviewed production schema. Create them explicitly through an owned migration/startup/admin process; do not depend on accidental or broad automatic index creation. Name them predictably.

Candidate Trackr indexes include:

- unique `(projectId, issueNumber)`;
- unique `(projectId, idempotencyKey)` when the key belongs to an operation record;
- `(projectId, status, updatedAt DESC, _id DESC)` for a common status feed;
- `(projectId, labelIds, updatedAt DESC, _id DESC)` only if the label query justifies its multikey cost;
- membership lookup keyed by project/user according to the actual access direction.

Field order follows equality filters, then sort/range needs, adjusted through evidence. Every index costs storage, cache, and write work. Unique indexes also enforce durable invariants, but only across indexed values and MongoDB's rules for missing/null fields; test the actual document population.

For each critical query capture `explain("executionStats")` with representative cardinality. Inspect:

- winning plan and index bounds;
- keys and documents examined versus returned;
- in-memory/blocking sort stages;
- rejected plans where useful;
- execution time as supporting, not sole, evidence;
- whether collation and type match index assumptions;
- effects of multikey fields and projections.

A collection scan is not automatically wrong for a tiny complete scan, and an index scan is not automatically good. Judge work and growth against the operation's objective.

## Atomicity, Optimistic Updates, and Transactions

A MongoDB write is atomic at the single-document level. Design an issue state transition as one conditional update when the invariant fits the issue document:

```text
filter: projectId, issueId, version = expectedVersion, allowed current state
update: set changed fields, increment version, set updatedAt
result: exactly one match means success; zero requires safe classification
```

Use Spring Data `@Version` for straightforward aggregate saves only after understanding its generated predicate/update and conflict exception. Use `MongoTemplate` for explicit conditional operations where fields, projection, and result classification matter. Do not perform read-check-write as separate unprotected operations.

A zero-match conditional update can mean missing resource, wrong project, forbidden/non-exposed resource, stale version, or invalid current state. Classify without leaking cross-project existence. An additional scoped read may be needed for a better internal result, with the accepted race documented.

Prefer single-document atomicity. Use a multi-document transaction only when a named invariant cannot be redesigned around one aggregate and inconsistency is unacceptable. Transactions require a replica set and add session, lifetime, cache, lock, write-conflict, retry, and operational costs. Keep them short, avoid user/network waits, and retry the entire transaction only for documented transient cases with an idempotent use case and remaining budget.

Do not claim exactly-once behavior. An ambiguous network failure can leave commit outcome unknown; reconcile using idempotency keys and read state.

## Pagination, Aggregation, and Query Shape

Implement cursor pagination with a stable order. For descending `(updatedAt, _id)`, the next-page predicate selects records with earlier `updatedAt`, or equal time and a lower `_id`, while preserving the project and filter predicates. Sign or validate opaque cursors if clients could tamper with scope or types.

Bound `limit`, projected fields, pipeline output, and memory/disk use. Aggregation stages should filter early when semantics allow, preserve tenant scope, and use indexes before blocking work. Inspect explain output rather than assuming stage order optimizes itself.

Arrays and multikey indexes have interaction constraints. Multiple array fields in compound indexes, `$unwind` cardinality, and large `$lookup` operations can surprise. Use aggregation for a clear data-side benefit, not to reproduce arbitrary application logic.

Avoid N+1 reference loading from repository loops. Prefer an explicit batched query, aggregation, or adjusted document shape and measure the trade-off.

## Replica Sets and Failover

Use a replica set for production and for any exercise claiming transaction or failover semantics. Know the roles:

- the primary accepts normal writes;
- secondaries replicate the oplog and may lag;
- elections can create a period without a writable primary;
- topology discovery and server selection occur in the driver;
- acknowledged writes and client responses can become ambiguous around failure.

Exercise primary loss during reads, an ordinary conditional write, and a transaction. Observe exception labels/categories, elapsed server selection, pool behavior, and final data. Do not assume retryable writes make an entire multi-step use case safe. A driver retry can protect a supported single operation; product idempotency protects business intent.

Secondary reads can be stale and may violate read-your-own-write expectations. Choose them only for explicitly stale-tolerant operations and monitor replication lag. Never route authorization or a just-written critical state to a secondary casually.

Failover testing must have a recovery step. Verify the client rediscovers topology, waiters drain, and the application becomes ready only when its required dependency behavior is available.

## Read and Write Concerns

Choose read concern, read preference, and write concern from the product guarantee:

- stronger write acknowledgement reduces loss ambiguity but costs latency/availability under degraded replicas;
- reading from the primary supports fresher ordering relative to primary writes but does not replace transaction semantics;
- secondary reads may improve distribution for stale-tolerant work but introduce lag;
- causal/session guarantees require correct session ownership and are not global linearizability;
- transaction-level settings should be consistent with the invariant and deployment.

Document settings per operation category rather than declaring one choice “safe” universally. Trackr authorization, version checks, and user-visible updates generally favor primary reads and appropriately acknowledged writes. Analytics or non-critical background views may accept different behavior if explicitly labeled.

Verify effective settings from the configured client and operation. URI defaults, Spring configuration, template overrides, and transaction options can interact.

## Timeouts, Pools, and Capacity

Bound the complete wait path:

- DNS/connect/TLS as exposed by the driver/platform;
- server selection;
- connection-pool checkout;
- socket/read activity;
- operation maximum time where suitable;
- transaction lifetime and application request budget.

A Spring request thread can spend most of its time waiting for a pool connection before any query starts. Size the MongoDB pool from concurrency, operation duration, server capacity, number of application replicas, and non-application clients. A larger pool can overload the database and deepen queues.

Monitor checked-out connections, waiters, checkout latency/timeouts, command latency/errors, connection creation, server-selection failures, and replication lag. Keep application request budgets longer than required inner operations only by enough margin to classify and respond.

Interruption or an HTTP timeout does not prove the driver operation stopped or the write did not commit. Use driver-supported bounds and reconciliation. Avoid retry storms by limiting attempts, using jitter, consuming the remaining budget, and retrying only safe/transient operations.

## Security, Monitoring, and Restore

Production controls include:

- authentication and least-privilege application roles;
- separate migration/admin privileges from runtime privileges;
- TLS and certificate/hostname verification according to the platform;
- network restriction and no public unauthenticated exposure;
- secrets from the approved manager with rotation procedures;
- redaction of credentials, session data, tokens, sensitive fields, queries, and payloads;
- encryption-at-rest and key ownership according to policy;
- patch/version ownership and vulnerability response;
- auditing for privileged/security-relevant activity;
- alerts tied to user impact and capacity, not raw noise.

Monitor operation latency/error by bounded category, pool saturation, cache/working-set pressure, page faults where relevant, replication lag, elections, disk, connections, and slow operations. Never use raw user IDs, query text, or collection cardinality as high-cardinality metric labels.

Define backup objective, retention, encryption, access, and point-in-time expectations. Then restore into an isolated environment using the same procedure and reconcile:

- collection counts by tenant/project;
- sampled identities and references;
- validator and index definitions;
- issue-number uniqueness and version fields;
- application-level core journeys.

A backup log is not restore proof. Record duration and compare it with recovery objectives.

## PostgreSQL Cutover Handoff

Phase 6 is mandatory. Prepare without introducing dual writes:

1. freeze the public contract and required application ports;
2. inventory BSON-specific values, identifiers, dates, null/missing states, arrays, and duplicated fields;
3. define a deterministic export format with schema version and stable ordering;
4. write invariant and count queries that will reconcile source and target;
5. identify duplicate/orphan/invalid data that PostgreSQL constraints will reject;
6. decide canonical values and repair/quarantine policy before import;
7. capture a fresh MongoDB backup and prove its restore;
8. document the write freeze, export, transform, import, reconcile, adapter switch, verification, and rollback steps.

Do not add a live PostgreSQL writer in this phase. The next phase performs a controlled cutover, then removes MongoDB dependencies, configuration, adapter code, and runtime infrastructure. Historical artifacts may remain as learning evidence; the deployed application must not read or write both stores.

## Techniques and Evidence

- Generate representative cardinality and value distributions without production data.
- Capture exact query shape, index definition, plan, and returned count together.
- Use command comments/correlation that are safe and bounded for diagnosis.
- Make migrations/backfills resumable and observable.
- Test conditional updates under concurrent writers.
- Use a replica-set-capable environment for transaction/failover claims.
- Compare server-side final state with client-visible errors after ambiguity.
- Inspect effective client settings and pool behavior, not configuration text alone.
- Restore to a separate target and run invariant checks.

## Decision Rules and Trade-offs

| Decision | Default | Alternative and cost |
|---|---|---|
| Embed vs reference | Embed bounded data read/changed with parent | Reference independent/unbounded data; application owns integrity and extra reads |
| Repository vs `MongoTemplate` | Repository for obvious bounded CRUD | Template for explicit operations; more code but clearer query/update behavior |
| Automatic vs explicit indexes | Explicit, reviewed index management | Automatic creation is convenient locally but weakens rollout ownership |
| Save vs conditional update | Explicit conditional update for state/version changes | Save is simpler for aggregate replacement but can obscure conflicts and fields |
| Single document vs transaction | Single-document atomicity | Transaction only for unavoidable cross-document invariant; higher runtime cost |
| Primary vs secondary read | Primary for critical/authorization/current state | Secondary only for stated stale tolerance and monitored lag |
| Stronger vs weaker acknowledgement | Match business loss/latency tolerance | Stronger acknowledgement may reduce availability/increase latency under failure |
| Cursor vs skip/offset | Stable cursor for changing feeds | Skip is simple at shallow bounded depth but scales/drifts poorly |
| Duplicate field vs lookup | Duplicate only with source/reconciliation plan | Lookup/reference preserves canonical value but adds query cost |
| Bigger pool vs bounded demand | Bound demand to measured database capacity | Bigger pool can reduce checkout wait briefly while overloading the server |

## AI-Assisted Workflow

Give AI the access-pattern table, representative cardinality, exact MongoDB/Spring Data versions, document examples with synthetic data, and the invariant under review. Ask for competing embed/reference models, index order alternatives, ambiguous-failure scenarios, and queries that detect migration corruption.

Independently verify by inspecting generated BSON/query operations, running them against a replica-set-capable MongoDB, capturing `explain("executionStats")`, injecting primary loss, observing final state after timeouts, checking effective concerns/timeouts, and completing a restore. Never paste production documents, credentials, or sensitive indexes. Reject a suggested transaction, index, or retry rule that has no named invariant and measured plan/failure evidence.

## Common Pitfalls

- Treating flexible schema as no schema.
- Mirroring a Java object graph without access-pattern analysis.
- Embedding unbounded comments, audit events, or membership arrays.
- Assuming references provide foreign keys or joins for free.
- Depending on automatic index creation in production.
- Declaring an index good because its name appears rather than inspecting work.
- Using repository derivation for complex queries whose shape is unclear.
- Performing read-check-save without a version predicate.
- Using a transaction where one atomic document update would suffice.
- Testing transactions/failover against a standalone or mock database.
- Reading authorization state from lagging secondaries.
- Treating acknowledged client error as proof no commit occurred.
- Growing pools/retries during saturation.
- Logging documents, credentials, or sensitive query values.
- Calling a successful backup a recovery test.
- Building dual writes “temporarily” for the PostgreSQL exercise.

## Hands-On Exercises

### Exercise 1: Access-pattern document model

Model users, projects, memberships, issues, labels, comments, audit facts, and idempotency records. For each relationship decide embed, reference, duplicate snapshot, or separate collection.

**Evidence:** access-pattern/cardinality table, aggregate boundaries, maximum-growth assumptions, atomicity map, and duplication reconciliation policy.

### Exercise 2: Validators and explicit indexes

Create collection validators and a versioned setup/migration path. Add only indexes justified by Trackr operations, including issue-number uniqueness and stable feed access.

**Evidence:** invalid documents rejected at the database, complete named index definitions, rollout/rollback notes, and storage/write-cost discussion.

### Exercise 3: Plans and cursor pagination

Implement issue feed queries with project scope, optional status or label filter, stable compound cursor, bounded projection, and limit. Use `MongoTemplate` and inspect plans with representative data.

**Evidence:** first/middle/end page results without duplicate/skip under a controlled concurrent insert, plus keys/documents examined and sort-stage observations for each important shape.

### Exercise 4: Optimistic atomic update

Implement “update issue if expected version and allowed state” as one atomic operation. Race at least two writers and classify zero-match safely without exposing cross-project existence.

**Evidence:** exactly one intended winner, stable public conflict response, final version/state, and the actual filter/update shape.

### Exercise 5: Transaction decision

Choose one candidate cross-document invariant such as issue creation plus idempotency/audit. First attempt a single-document or remodelled design. If a transaction is still required, implement it briefly against a replica set and inject a transient failure.

**Evidence:** written reason the transaction is or is not earned; if used, commit/abort/retry observations and final invariant. Do not keep a transaction just to demonstrate the API.

### Exercise 6: Timeout, pool, and failover drill

Under bounded concurrent traffic, observe pool checkout, operation latency, and server selection. Trigger primary loss during read, conditional write, and transaction/idempotent operation.

**Evidence:** client result, elapsed budget, error category, retry decision, final database state, recovery/readiness timing, and no unbounded waiter growth.

### Exercise 7: Security and restore drill

Run Trackr with a least-privilege account and encrypted connection according to the environment. Create a backup, restore it into an isolated replica set, restore validators/indexes, and run reconciliation plus core journeys.

**Evidence:** sanitized privilege/settings record, restore duration, counts/invariants, sampled references, and working application checks. No secrets or production data.

## Exit Exercise

Replace the in-memory adapter with a production-conscious Spring Data MongoDB implementation while preserving the Phase 4 OpenAPI contract. Use repositories only for obvious CRUD and `MongoTemplate` for explicit query/update work. Include validators, explicit indexes, cursor pagination, projections, optimistic updates, appropriate concerns, bounded timeouts/pools, replica-set behavior, secure configuration, monitoring signals, and restore procedure.

Run success, invalid schema, duplicate key, stale update, concurrent cursor, timeout with ambiguous completion, pool saturation, primary failover, and restore scenarios. Prepare the deterministic export schema and reconciliation rules for PostgreSQL but do not introduce dual writes.

**Exit gate:** plans justify every critical index; one conditional update proves single-document atomicity; every transaction is justified or removed; effective concerns/timeouts/pool bounds are observed; failover recovers within a recorded window; restore passes counts and invariants; and the Phase 6 cutover packet can identify and transform every persisted Trackr fact without keeping MongoDB in the final runtime.

## Related Documents

- [Previous: API Architecture](04-api-architecture.md)
- [Roadmap Navigation](README.md)
- [Next: PostgreSQL and SQL](06-postgresql-sql.md)
