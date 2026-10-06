# Phase 12: System Design and Job Readiness

## Purpose

Turn Trackr evidence into a repeatable system-design method, clear Java/Spring/PostgreSQL reasoning, conscious MongoDB trade-offs, and concise interview stories grounded in work actually performed.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Philosophy and Method](#philosophy-and-method)
- [Clarify Requirements](#clarify-requirements)
- [Estimate Capacity](#estimate-capacity)
- [Sketch APIs and Data](#sketch-apis-and-data)
- [Scale Java, Spring, and PostgreSQL](#scale-java-spring-and-postgresql)
- [MongoDB as a Conscious Alternative](#mongodb-as-a-conscious-alternative)
- [Caches, Queues, and Consistency](#caches-queues-and-consistency)
- [Failure, Security, and Observability](#failure-security-and-observability)
- [Trackr Architecture Narrative](#trackr-architecture-narrative)
- [Interview Topics](#interview-topics)
- [Behavioral Evidence](#behavioral-evidence)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [Common Pitfalls](#common-pitfalls)
- [AI-Assisted Workflow](#ai-assisted-workflow)
- [Mock Interview Rubric](#mock-interview-rubric)
- [Hands-On Exercises and Evidence](#hands-on-exercises-and-evidence)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

System design is disciplined reduction of ambiguity, not memorized architecture pictures. Begin with users, invariants, scale, and failure budgets; choose the simplest sufficient design; identify its first bottlenecks; then evolve one component at a time. Trackr’s Spring MVC/PostgreSQL implementation supplies evidence for the claims.

## Agent & Skills

- **Agents:** `backend-architect`, `researcher`, `code-reviewer`
- **Skills:** `plan`, `research`, `problem-solving`, `review`
- **Handoff:** give Phase 13 the recorded design, calculations, scored rubric, six evidence-based stories, and direct Trackr artifact references supporting each competency claim.

## Philosophy and Method

Narrate in order: clarify requirements, estimate capacity, define contracts and data, draw the critical request path, find limits, add one justified mechanism, analyze failure/recovery, and summarize trade-offs. Do not begin with microservices, Kafka, a cache, MongoDB, or sharding. Every component answers a named requirement or measured constraint.

Separate facts, estimates, and choices. State units and assumptions aloud, use reviewable round numbers, and explain what telemetry would validate them. Prefer a modular monolith because local transactions, one deployment, and simpler diagnosis are valuable until isolation, scaling, or team ownership repays distribution.

A useful diagram shows browser/clients, edge/load balancer, stateless Spring Boot replicas, PostgreSQL, and only earned cache/queue components. Mark trust boundaries, request direction, source of truth, transaction owner, replication, and failure points.

## Clarify Requirements

Ask before designing:

- Which journeys are critical: issue reads/writes, comments, membership, search, notifications?
- How many active users, projects, issues, and comments; what peak and hot-tenant skew?
- What latency, availability, durability, RPO, and RTO targets apply?
- Which operations require read-your-writes or serializable business behavior?
- Which feeds, notification effects, analytics, and summaries may lag?
- What tenant isolation, retention, region, privacy, and audit constraints exist?
- What are maximum request/page/entity sizes and abusive-work limits?

Classify invariants. Membership authority, uniqueness, versioned writes, and idempotency require strong durable enforcement. Search, metrics, notifications, and generated summaries may be eventually consistent with explicit freshness and replay behavior. State non-goals.

## Estimate Capacity

For a 10-million-user exercise, choose visible assumptions. Example: 2 million daily active users at 20 reads and 2 writes each yields 40 million reads/day and 4 million writes/day. Dividing by 86,400 gives roughly 460 reads/s and 46 writes/s average. At a 10× peak, reason about 4,600 reads/s and 460 writes/s. At 3 KB of durable data per write, raw growth is about 12 GB/day before indexes, WAL, replicas, and backups.

Use:

`peak operations/s = operations/day ÷ 86,400 × peak factor`

Also estimate concurrent requests from latency, Hikari connection budget, database working set, index/storage multiplier, backup/restore time, egress, queue arrival/service rates, and outbox age. Do not multiply every maximum into an impossible worst case. Present base, peak, and one stress scenario plus the signals that could falsify them.

## Sketch APIs and Data

Reuse Trackr’s project-scoped issue, comment, membership, session, and pagination contracts. State status/error behavior, expected-version updates, idempotency keys, CSRF/session behavior, and cursor ordering. A diagram without conflict and retry semantics is incomplete.

PostgreSQL models users, projects, memberships, issues, labels, issue labels, comments, sessions, idempotency records, outbox, and audit events. Foreign keys, uniqueness, checks, and version predicates enforce invariants. Explain hot queries: project issue feed ordered by `(updated_at, id)`, membership lookup, assignee/status filters, and leased outbox claiming. Index order follows equality plus range/order requirements and is verified with `EXPLAIN (ANALYZE, BUFFERS)` in a safe environment.

At the code boundary, Spring MVC handles HTTP translation, application services own authorization and transactions, JdbcClient repositories own explicit parameterized SQL, and PostgreSQL owns durable integrity. Avoid generic CRUD repositories that hide query shape and transaction scope.

## Scale Java, Spring, and PostgreSQL

Run stateless Spring Boot replicas behind a load balancer while sharing opaque session state durably. Configure graceful shutdown/readiness so traffic drains before termination. Bound request bodies, execution, provider calls, queues, and deadlines. Scale from latency, throughput, error, and saturation evidence—not CPU alone.

For the JVM:

- size heap with headroom for metaspace, stacks, direct/native buffers, code cache, and agents;
- observe allocation, GC pauses, CPU throttling, platform/virtual threads, pinning, and blocked work;
- use Spring MVC by default; virtual threads can reduce blocking-thread cost but not database or downstream capacity;
- use semaphores, bounded executors, Hikari limits, and admission policy for scarce resources;
- capture JFR/thread dumps under the representative bottleneck before tuning.

PostgreSQL remains authoritative. First improve query shape/indexes, cap each replica’s Hikari pool against a global connection budget, shorten transactions, batch deliberately, and maintain statistics/vacuum from evidence. Add read replicas only for reads tolerant of lag; send authorization-sensitive and read-after-write queries to the primary when staleness is unsafe.

Partition large append-oriented audit/outbox data when retention or maintenance proves the need. Sharding is late because it complicates transactions, joins, uniqueness, resharding, and operations. If required, project/tenant is a likely locality key, while global identity and administration need explicit handling.

## MongoDB as a Conscious Alternative

MongoDB is not a beginner database or an automatic scale button. It can be the better source of truth when aggregates are naturally document-shaped, most changes are atomic within one bounded document, access paths align with document ownership, schema evolution benefits from document flexibility, and the team can operate replication, indexes, validation, backup/restore, and sharding.

Prefer PostgreSQL for Trackr because memberships, labels, comments, sessions, audit, uniqueness, joins, and cross-entity transactions are central and were proven in the capstone. The Phase 5 Mongo implementation remains learning evidence, not a second production authority.

If MongoDB is proposed, explain:

- embed versus reference from update/read patterns and document growth;
- schema validation and application migrations;
- compound/multikey index shape and `explain` evidence;
- single-document atomicity versus multi-document transaction cost;
- read/write concern, replication lag, retryable operations, and causal needs;
- shard key cardinality, locality, monotonicity, hot spots, and resharding;
- backup, point-in-time recovery, restore, and monitoring.

Reject dual writes as an easy migration plan. Use one authority and a controlled cutover or a genuinely owned event-driven synchronization design with reconciliation and failure semantics.

## Caches, Queues, and Consistency

Cache only a named expensive read with a staleness, invalidation, authorization, memory, and outage policy. Stale membership is a security failure; direct primary reads or carefully versioned invalidation may be safer. Cache failure must not corrupt truth.

Trackr’s PostgreSQL outbox atomically commits notification intent with the issue mutation. Bounded workers claim leased batches, retry transient failures with jitter, and use destination idempotency. Add a broker when measured throughput, fan-out, retention, ordering, or independent-consumer requirements exceed the database approach—not because “systems at scale use Kafka.”

Choose consistency per operation. Issue/membership writes favor consistency; temporary refusal is safer than conflicting authority. Search, analytics, notification delivery, and summaries may be eventually consistent with visible freshness. Define retry ownership and avoid “exactly once” claims; at-least-once delivery plus durable deduplication is the normal design.

## Failure, Security, and Observability

Walk through JVM/container OOM, long GC pause, thread/connection saturation, instance crash, PostgreSQL failover, replica lag, outbox backlog, cache outage, provider outage, bad migration, credential compromise, and region loss. For each state detection, user effect, invariant, containment, recovery, RPO/RTO, ownership, and evidence.

Authenticate at the servlet boundary and authorize inside every project use case. Preserve CSRF, tenant-scoped SQL, rate/body/work limits, secrets rotation, audit, and redacted errors as scale changes. New replicas do not fix a hot row, bad query, or database connection storm.

Define SLIs for successful request latency/error and durable notification delay. Correlate OTLP traces through Spring MVC, application services, JdbcClient, and outbox. Use Micrometer/Actuator for bounded signals and JFR/JMC/`jcmd` for JVM diagnosis. Include compatible migration, rollout, rollback/roll-forward, backup restore, and incident ownership.

## Trackr Architecture Narrative

“Trackr starts as a Spring Boot modular monolith behind a load balancer. Browser requests flow through Spring MVC to application services, explicit JdbcClient repositories, and authoritative PostgreSQL. Services own authorization and transactions; constraints protect project relationships and versioned writes. Opaque sessions are durable across stateless replicas. Bounded executors/semaphores and Hikari capacity protect dependencies; mutations and outbox events commit together. Read replicas, caches, a broker, partitions, sharding, or a service split appear only against measured pressure. MongoDB is an evaluated alternative, not a second authority. Telemetry, JFR, restore, and rollback protect each evolution.”

Support the narrative with actual artifacts: query plan, data cutover, restore, conflict, cross-tenant denial, saturation/JFR result, trace, alert, and rollback. A portfolio statement without accessible evidence is only an assertion.

## Interview Topics

### Java and JVM

Explain object/reference semantics, immutability, records, generics and type erasure, checked/unchecked exceptions, try-with-resources, collections equality/hash contracts, streams versus loops, class loading, heap versus stack/native memory, GC trade-offs, the Java Memory Model, happens-before, volatile versus atomic/lock, executors/futures, interruption, concurrent collections, platform versus virtual threads, and JFR/thread-dump diagnosis.

Java has no standard Go-style dynamic race detector. Describe how JMM reasoning, synchronization contracts, stress tests, invariants, JFR, and specialist tools provide different evidence without overclaiming.

### Spring and HTTP

Explain bean lifecycle and dependency injection without magic, configuration precedence, servlet filter chain, Spring MVC request lifecycle, validation/error mapping, transaction proxy boundaries and rollback, SecurityContext/session/CSRF/CORS, JdbcClient/named SQL, Hikari, Actuator/Micrometer, graceful shutdown, and why WebFlux is not automatically faster.

### PostgreSQL and SQL

Explain normalization/denormalization, keys/constraints, joins/aggregates/window functions, transactions and isolation anomalies, MVCC, locks/deadlocks, optimistic versioning, index selection/order, query plans, statistics/vacuum, pagination, connection pools, migrations, replication lag, and backup/restore.

### MongoDB

Explain document modeling, embed/reference, schema validation, indexes and multikey limits, aggregation, single-document atomicity, transaction trade-offs, read/write concern, replication/oplog, retryability, shard keys/hot spots, and backup/restore. Compare it with PostgreSQL from Trackr’s access patterns rather than slogans.

In coding rounds, state complexity, validate boundaries, preserve resource closure/interruption, use clear types, and choose data structures from operations. In architecture rounds, state assumptions and failure behavior before naming infrastructure.

## Behavioral Evidence

Prepare six truthful stories: hard bug, disagreement/trade-off, security improvement, reliability incident, performance investigation, and ownership/learning. Use Situation–Task–Action–Result–Reflection. Emphasize your own action, observed outcome, rejected option, and changed mental model.

Link each story to an artifact such as a trace, query plan, JFR recording, review, migration reconciliation, or incident timeline. Remove confidential data without inventing metrics, scope, or production impact. Practice 90-second and three-minute versions.

## Decision Rules and Trade-offs

| Pressure | First move | Escalate when |
|---|---|---|
| HTTP latency | Trace request; inspect SQL/pool/JVM | Scale replicas or redesign only from identified saturation |
| JVM concurrency | Bound work and resources; inspect JFR | Virtual threads/executor change has measured benefit |
| Read latency | Query/index proof | Replica/cache after primary evidence |
| Write scale | Short transactions, batching, contention analysis | Partition/shard beyond proven primary limits |
| Data model | PostgreSQL relational invariants | MongoDB when bounded document aggregates dominate |
| Async effects | PostgreSQL outbox | Broker capabilities are measurably required |
| Availability | Redundancy, compatible deploy, restore | Multi-region required by RTO/latency |
| Interview answer | Deep baseline and one evolution | Add patterns only when requirements call for them |

## Common Pitfalls

- Naming Spring Cloud, Kafka, Redis, MongoDB, or Kubernetes before requirements and estimates.
- Scaling Spring replicas while exceeding PostgreSQL’s connection budget.
- Treating virtual threads as permission for unbounded database/provider concurrency.
- Sending authorization or read-after-write queries to lagging replicas.
- Choosing MongoDB for “flexibility” without bounded aggregates, indexes, validation, and migration ownership.
- Claiming a cache, queue, or dual write improves availability without analyzing staleness, replay, reconciliation, and outage.
- Drawing failover without RPO, RTO, restore, promotion, and split-brain responsibility.
- Tuning heap/GC from folklore rather than container signals and JFR.
- Reciting framework annotations while unable to trace one request or transaction.
- Using invented portfolio metrics or generic behavioral stories.

## AI-Assisted Workflow

Use AI as a skeptical interviewer: change one constraint, challenge assumptions, test unit calculations, identify failures, and critique clarity. Make the initial design and estimates yourself. Generated limits, benchmark results, incident outcomes, and behavioral claims are not facts.

Independently verify APIs and guarantees in current Java, Spring, PostgreSQL, and MongoDB primary documentation. Recalculate units, inspect real query plans and JFR evidence, trace one request, and explain the final design without notes or AI. AI may edit prose but must never invent ownership, impact, or measurements.

## Mock Interview Rubric

Score 0–3 for requirements, estimates, API/data, baseline request path, JVM/resource model, bottleneck-driven evolution, consistency, failure/recovery, security/observability, trade-offs, and communication. A 2 is coherent with evidence; a 3 adds quantitative justification, alternatives, and recovery proof. Any zero fails.

Record a timed answer. Review assumption timing, diagram/narrative agreement, unit errors, technologies without requirements, PostgreSQL/Mongo rationale, and recovery detail. Repair weak sections and repeat with one changed constraint.

## Hands-On Exercises and Evidence

1. **Baseline:** draw Trackr and narrate one write. **Evidence:** session, authorization, transaction, response, and telemetry agree.
2. **Capacity:** compute base/peak QPS, storage, Hikari budget, memory envelope, and queue rates. **Evidence:** visible units, assumptions, and sensitivity.
3. **Failure table:** analyze at least eight JVM/database/deployment failures. **Evidence:** detection, containment, recovery, RPO/RTO, invariant, and user effect.
4. **Database decision:** redesign one bounded Trackr capability for MongoDB. **Evidence:** document/index/consistency model and a reason to retain or reject it.
5. **Constraint change:** add a hot tenant, strict RTO, or second region. **Evidence:** only justified components change.
6. **Interview stories:** map six genuine artifacts to narratives. **Evidence:** each result is observable and explainable.

## Exit Exercise

Deliver a recorded 45-minute design for Trackr at 10 million users: requirements, estimates, API/data model, Browser → Spring MVC → application services → JdbcClient → PostgreSQL path, JVM/container/resource limits, bottlenecks, scaling sequence, consistency, security, observability, failure recovery, RPO/RTO, MongoDB comparison, and rejected alternatives. Score it and prepare six evidence-based stories plus concise Java/JVM, Spring, PostgreSQL/SQL, and MongoDB explanations.

**Exit gate:** no rubric category is zero; each component answers a requirement or estimate; calculations are dimensionally sound; JVM and database limits are connected; recovery preserves tenant/data invariants; MongoDB is accepted or rejected from access patterns; stories link to genuine evidence; and you can repeat the design and interview explanations without AI or notes.

## Related Documents

- [Previous: Safe LLM Elective](11-llm-elective.md)
- [Roadmap Navigation](README.md)
- [Next: Resources and Completion Checklists](13-resources-checklists.md)
