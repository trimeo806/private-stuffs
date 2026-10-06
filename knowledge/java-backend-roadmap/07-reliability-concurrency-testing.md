# Phase 7: Reliability, Concurrency, and Testing

## Purpose

Make Trackr correct under overlapping work, overload, cancellation, retries, and dependency failure by combining Java concurrency reasoning with evidence-driven tests and runtime diagnostics.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Philosophy and Reliability Model](#philosophy-and-reliability-model)
- [Java Memory Model](#java-memory-model)
- [Executors, Futures, and Threads](#executors-futures-and-threads)
- [Shared State and Synchronization](#shared-state-and-synchronization)
- [Bounds, Backpressure, and Cancellation](#bounds-backpressure-and-cancellation)
- [Retries, Idempotency, and the Outbox](#retries-idempotency-and-the-outbox)
- [Testing Strategy](#testing-strategy)
- [Stress and Runtime Diagnostics](#stress-and-runtime-diagnostics)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [Common Pitfalls](#common-pitfalls)
- [AI-Assisted Workflow](#ai-assisted-workflow)
- [Hands-On Exercises and Evidence](#hands-on-exercises-and-evidence)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

Concurrency is useful only when it preserves invariants and respects finite CPU, memory, queues, database connections, and downstream capacity. This phase turns Trackr’s PostgreSQL version checks and outbox into a reliable end-to-end design, then proves it through focused tests, controlled failure, stress work, Java Flight Recorder, and thread dumps.

## Agent & Skills

- **Agents:** `developer`, `tester`, `debugger`
- **Skills:** `core`, `test`, `problem-solving`, `debug`
- **Handoff:** give Phase 8 the concurrency model, overload policy, interruption rules, retry/idempotency contract, focused test evidence, and remaining failure risks.

## Philosophy and Reliability Model

Prefer synchronous code until concurrency produces measured benefit or represents independently progressable work. For every task, name its owner, lifetime, input bound, result/error destination, cancellation path, and shutdown behavior. A fast happy path does not compensate for an unbounded failure path.

State invariants before implementation: one issue version wins, one idempotency key denotes one logical request, a committed outbox event is eventually claimable, permanent failures do not retry, and shutdown does not silently lose accepted durable work. Database constraints remain the final guard across JVMs.

## Java Memory Model

The Java Memory Model defines when one thread’s writes become visible to another. Program order inside one thread does not create cross-thread visibility. Establish a *happens-before* relation through monitor unlock/lock, volatile write/read, thread start/join, or concurrency utilities whose contracts provide it.

- Prefer immutable values and safe publication.
- Use `volatile` for visibility and ordering of suitable single-variable state, not compound atomicity.
- `count++` is read-modify-write and is not made atomic by declaring the field volatile.
- Final fields gain initialization guarantees only when `this` does not escape construction.
- Do not use `Thread.sleep` as coordination; use latches, barriers, futures, or observable eventual conditions.

Java has **no standard Go-style dynamic race detector** comparable to `go test -race`. Tests, review, the JMM, stress harnesses, JFR, and thread dumps provide complementary evidence, but none proves the absence of every data race. Use specialized tools such as jcstress only when a low-level concurrency claim genuinely warrants that additional machinery.

## Executors, Futures, and Threads

Use `ExecutorService` to own task admission and lifecycle rather than creating ad hoc threads. For platform-thread pools, make worker count, queue capacity, rejection policy, and shutdown explicit. Avoid the convenience factories that hide an unbounded queue when overload behavior matters.

Use `CompletableFuture` for a real dependency graph of asynchronous stages, not to decorate synchronous code. Pass an intentional executor; otherwise common-pool contention and blocking are easy to hide. Preserve the original failure, set deadlines, and avoid stage chains whose exception or cancellation ownership is unclear.

Platform threads are scarce OS-backed resources and suit bounded pools. Virtual threads make blocking request-style code much cheaper, but do not make database connections, sockets, memory, or rate limits unlimited. Do not pool virtual threads merely to limit concurrency; use a semaphore or bounded resource pool. Measure pinning and `ThreadLocal` footprint, and keep blocking synchronized/native regions out of hot paths.

Use Spring MVC by default. Do not combine virtual threads with WebFlux for novelty. Choose WebFlux only when the whole call path is reactive, the team can operate it, and measurement shows a need that the blocking servlet model cannot meet simply.

## Shared State and Synchronization

Choose the narrowest primitive that expresses the invariant:

- `synchronized` or `ReentrantLock` for a short multi-field critical section;
- `AtomicLong`, `AtomicReference`, or `LongAdder` for their documented atomic operation patterns;
- `ConcurrentHashMap` for concurrent access, using atomic map operations rather than check-then-act sequences;
- immutable snapshots when reads dominate and replacement is cheap;
- `Semaphore` for limiting concurrent use of an external capacity.

Keep lock scope small, document lock ordering, release explicit locks in `finally`, and never hold an in-memory lock across HTTP or database I/O. Concurrent collections make their own operations safe; they do not make a multi-step business invariant atomic. PostgreSQL transactions and constraints protect cross-request and cross-instance state.

## Bounds, Backpressure, and Cancellation

For notification delivery, use a bounded queue only for transient in-process coordination; durable acceptance belongs in the PostgreSQL outbox. Define overload as an observable policy: reject, defer durably, shed optional work, or wait only within a caller budget. An unbounded queue converts overload into delayed memory failure.

Interruption is cooperative cancellation. When catching `InterruptedException`, either propagate it or restore the flag with `Thread.currentThread().interrupt()` before returning. Do not swallow interruption, retry indefinitely, or confuse it with a business error. On shutdown: stop intake, allow bounded completion where promised, interrupt/cancel remaining work, close the executor, and leave durable unfinished events reclaimable.

Use semaphores around scarce outbound capacity, with timed acquisition and `finally` release. Queue depth, oldest-event age, rejection count, active tasks, database-pool wait, and completion outcomes reveal whether backpressure works.

## Retries, Idempotency, and the Outbox

Retry only classified transient failures when time remains and the operation is idempotent or deduplicated. Use exponential backoff with jitter and hard limits on attempts and elapsed time. Validation, authorization, most conflicts, and permanent provider responses fail immediately. Retrying at several layers multiplies load; assign one retry owner.

Bind an idempotency key to tenant, operation, request fingerprint, status, and stable result. Enforce uniqueness durably. For asynchronous effects, write the Trackr mutation and outbox event in one transaction. Workers claim events with leases, record attempts, and deliver at least once; the destination uses a durable effect key so replay is harmless. “Exactly once” is not an honest default across a database and external service.

## Testing Strategy

Keep tests that defend observable contracts:

- **JUnit:** pure state transitions, retry classification, permission rules, cursor behavior, and interruption handling;
- **AssertJ:** readable assertions on domain outcomes and failure details, not internal call order;
- **Spring MVC tests:** `MockMvc` for routing, validation, sessions, status codes, and error envelopes without pretending it proves proxy or database behavior;
- **integration:** Testcontainers with real PostgreSQL for migrations, constraints, transactions, locking, JdbcClient SQL, and outbox claiming;
- **system/concurrency:** simultaneous HTTP or service calls preserving a durable invariant;
- **contract/browser:** OpenAPI/client behavior and the few critical cookie/session journeys.

Use deterministic fakes for owned clocks and senders. Do not mock PostgreSQL semantics. Coordinate concurrent tests with barriers or latches, give them hard deadlines, isolate data, and assert the invariant rather than a lucky ordering. Avoid timing sleeps and mock echoes.

## Stress and Runtime Diagnostics

A stress test repeats adversarial interleavings under realistic contention and checks invariants; it is not proof that an unseen interleaving is safe. Vary executor size, queue pressure, cancellation timing, database-pool capacity, and task failures. Retain the seed/configuration and failure evidence.

Record a short JFR during saturation and inspect it in JDK Mission Control for allocation, monitor contention, blocked/parked threads, executor behavior, virtual-thread pinning, socket/database waits, and GC pauses. Capture thread dumps with `jcmd <pid> Thread.print -l`; compare multiple dumps to distinguish transient waits from deadlock or starvation. Diagnose before tuning.

## Decision Rules and Trade-offs

| Decision | Default | Change when |
|---|---|---|
| Synchronous vs concurrent | Synchronous, easy-to-prove path | Independent work yields measured latency/throughput benefit |
| Platform vs virtual threads | Company Spring convention; otherwise evaluate MVC virtual threads | Work is mostly blocking and measurements show benefit without resource overload |
| Lock vs atomic | Lock for a multi-value invariant | One documented atomic transition is sufficient |
| Concurrent map vs database | Map for JVM-local ephemeral state | Durable or multi-instance invariant requires PostgreSQL |
| Queue vs semaphore | Queue for accepted work awaiting service | Semaphore when excess work should wait briefly or reject without buffering |
| In-memory vs outbox | Outbox for promised effects | Loss on crash is explicitly acceptable |
| Retry vs fail fast | Fail fast | Failure is transient, operation replay-safe, and budget remains |
| Mock vs real dependency | Fake behavior Trackr owns | Real container for database or network semantics |

## Common Pitfalls

- Assuming thread-safe collections make check-then-act business logic atomic.
- Using an unbounded executor queue or submitting one task per row without admission control.
- Blocking the common `ForkJoinPool` through accidental `CompletableFuture` defaults.
- Swallowing `InterruptedException`, clearing the flag, or treating cancellation as retryable.
- Pooling virtual threads, while leaving database and outbound concurrency unbounded.
- Holding locks across I/O or acquiring locks in inconsistent order.
- Retrying timeouts without idempotency, causing duplicate writes or a retry storm.
- Marking an outbox item complete before the effect, or claiming exactly-once delivery.
- Treating green tests, a single stress run, or a thread dump as proof that races cannot exist.

## AI-Assisted Workflow

Ask AI to enumerate interleavings, identify hidden unbounded resources, suggest failure injections, and critique an invariant-based test. Never accept generated concurrency code because it looks idiomatic. Independently verify every happens-before claim against current Java API/JLS documentation, reproduce it with the smallest experiment, exercise the real PostgreSQL boundary, inspect JFR/thread dumps yourself, and explain why each retained test would fail on a plausible bug.

## Hands-On Exercises and Evidence

1. **Visibility:** build a deliberately unsafe publication example, then correct it with immutability or synchronization. **Evidence:** written JMM reasoning and repeatable experiment, without claiming the experiment proves safety.
2. **Bounded executor:** saturate a small platform-thread pool and queue. **Evidence:** documented rejection or timed-wait behavior, bounded queue metric, and clean shutdown.
3. **Interruption:** cancel idle, running, and blocked tasks. **Evidence:** interruption is propagated/restored and all tasks reach a defined terminal state.
4. **Virtual-thread comparison:** run the same blocking workload with platform and virtual threads while constraining database connections. **Evidence:** latency/resource measurements and JFR inspection, not a universal winner.
5. **Replay:** crash after an external effect but before outbox completion. **Evidence:** reclaimed event produces no duplicate user-visible effect.
6. **Concurrent update:** issue two stale updates through Spring MVC against PostgreSQL. **Evidence:** one versioned update wins and the other returns the specified conflict.

## Exit Exercise

Implement Trackr’s bounded notification delivery from PostgreSQL outbox through an executor, with interruption-aware shutdown, retry classification, jittered limits, leases, and an idempotent destination. Exercise concurrent issue updates and queue saturation through the Spring MVC application. Retain focused JUnit/AssertJ, MockMvc, Testcontainers, concurrency, and stress evidence plus a JFR and thread-dump diagnosis.

**Exit gate:** accepted durable work remains reclaimable after shutdown; queue and outbound concurrency stay bounded; permanent failures do not retry; replay has one intended effect; PostgreSQL invariants survive overlapping requests; diagnostics explain observed contention; and you can state precisely why Java’s lack of a standard Go-style race detector limits the proof.

## Related Documents

- [Previous: PostgreSQL and SQL](06-postgresql-sql.md)
- [Roadmap Navigation](README.md)
- [Next: Security](08-security.md)
