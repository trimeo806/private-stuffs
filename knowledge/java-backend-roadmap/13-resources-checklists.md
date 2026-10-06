# Phase 13: Resources and Completion Checklists

## Purpose

Provide a version-aware primary-source lookup layer and an objective audit for graduating with credible Java/Spring, MongoDB, PostgreSQL, reliability, security, operations, portfolio, and interview evidence from Trackr.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Philosophy and Evidence](#philosophy-and-evidence)
- [Java and JVM Resources](#java-and-jvm-resources)
- [Spring and HTTP Resources](#spring-and-http-resources)
- [MongoDB Resources](#mongodb-resources)
- [PostgreSQL and SQL Resources](#postgresql-and-sql-resources)
- [Testing Resources](#testing-resources)
- [Security and Supply-Chain Resources](#security-and-supply-chain-resources)
- [Delivery, Observability, and JVM Diagnostics](#delivery-observability-and-jvm-diagnostics)
- [Maven and Application Commands](#maven-and-application-commands)
- [JVM Diagnostic Commands](#jvm-diagnostic-commands)
- [MongoDB Commands](#mongodb-commands)
- [PostgreSQL Commands](#postgresql-commands)
- [Per-Phase Artifact Checklist](#per-phase-artifact-checklist)
- [Capstone Rubric](#capstone-rubric)
- [Portfolio and Interview Checklist](#portfolio-and-interview-checklist)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [Common Pitfalls](#common-pitfalls)
- [AI-Assisted Audit](#ai-assisted-audit)
- [Glossary](#glossary)
- [Audit Exercises and Evidence](#audit-exercises-and-evidence)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

This chapter is an index and graduation gate, not a substitute for building. Prefer company/version-matched primary documentation for behavior. A competency claim is complete only when it links to observed Trackr evidence, includes a relevant failure case, identifies the exercised revision and environment, and can be explained without copied notes or AI.

## Agent & Skills

- **Agents:** `docs-manager`, `code-reviewer`
- **Skills:** `docs`, `knowledge-retrieval`, `review`
- **Handoff:** publish a resolved evidence map, scored rubric, concise competency matrix, reproducible portfolio package, and honest next learning targets.

## Philosophy and Evidence

Documentation describes intended behavior; execution establishes what happened in one scenario. For every material claim retain:

1. the contract or hypothesis;
2. exact version, source revision, environment, input, and command/journey;
3. observed output with secrets and personal data removed;
4. interpretation and limits of the evidence;
5. artifact link and date;
6. the decision or mental-model correction that followed.

Company JDK, Spring, Maven, repository, and deployment conventions override greenfield recommendations. Otherwise this roadmap targets JDK 25 LTS, the current stable Spring Boot 4.1 service release, Maven Wrapper, Spring MVC, Flyway, Hikari, JdbcClient/named SQL, and PostgreSQL for the final deployment. Check release notes and support policies before copying a command or configuration.

A failed gate means repair the behavior or mark the skill as developing—not soften wording. Never invent logs, scores, benchmark numbers, screenshots, incidents, or AI explanations.

## Java and JVM Resources

- [Oracle Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) for LTS/support context.
- [JDK 25 documentation](https://docs.oracle.com/en/java/javase/25/) and [JDK 25 release notes](https://www.oracle.com/java/technologies/javase/25-relnotes.html) for the selected runtime.
- [OpenJDK JDK 25 project](https://openjdk.org/projects/jdk/25/) and [JEP index](https://openjdk.org/jeps/0) for platform changes and rationale.
- [Java Language Specification, Java SE 25](https://docs.oracle.com/javase/specs/jls/se25/html/index.html), especially [Chapter 17: Threads and Locks](https://docs.oracle.com/javase/specs/jls/se25/html/jls-17.html), for exact memory-model semantics.
- [Java virtual threads guide](https://docs.oracle.com/en/java/javase/25/core/virtual-threads.html) for intended usage and diagnostics.
- [`java.base` API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/module-summary.html), including concurrency, HTTP client, collections, I/O, time, and exceptions.
- [Java Flight Recorder guide](https://docs.oracle.com/en/java/javase/25/jfapi/flight-recorder.html) and [`jcmd`](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jcmd.html) for runtime evidence.

Java does not include a standard Go-style dynamic race detector. Do not translate a race-detector command into a JVM claim. Use JMM reasoning, synchronization contracts, focused invariant tests, stress runs, JFR/thread dumps, and a specialist tool only when justified.

## Spring and HTTP Resources

- [Spring Boot project and current release](https://spring.io/projects/spring-boot) plus [system requirements](https://docs.spring.io/spring-boot/system-requirements.html).
- [Spring Boot reference](https://docs.spring.io/spring-boot/reference/) for configuration, packaging, testing, Actuator, observability, and deployment.
- [Spring Framework reference](https://docs.spring.io/spring-framework/reference/) for Spring MVC, `RestClient`, JdbcClient, validation, transactions, and testing.
- [Spring MVC reference](https://docs.spring.io/spring-framework/reference/web/webmvc.html) and the [MVC/WebFlux comparison](https://docs.spring.io/spring-framework/reference/web/webflux/new-framework.html) for the default/exception decision.
- [Spring JDBC reference](https://docs.spring.io/spring-framework/reference/data-access/jdbc.html) and [transaction management](https://docs.spring.io/spring-framework/reference/data-access/transaction.html).
- [Spring Security servlet reference](https://docs.spring.io/spring-security/reference/servlet/index.html) for filter chains, authentication, authorization, sessions, CSRF, and CORS.
- [RFC 9110 HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110), [RFC 9112 HTTP/1.1](https://www.rfc-editor.org/rfc/rfc9112), and [OpenAPI Specification](https://spec.openapis.org/oas/latest.html).
- [MDN HTTP](https://developer.mozilla.org/en-US/docs/Web/HTTP), [cookies](https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies), and [CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS) for browser-observable behavior.

Verify cookies, CSRF, CORS, forwarding, limits, TLS, and timeouts through the deployed proxy/browser. An in-process controller test cannot establish those boundaries.

## MongoDB Resources

- [MongoDB Server manual](https://www.mongodb.com/docs/manual/) and [production notes](https://www.mongodb.com/docs/manual/administration/production-notes/).
- [Data modeling](https://www.mongodb.com/docs/manual/data-modeling/) and [schema validation](https://www.mongodb.com/docs/manual/core/schema-validation/).
- [Indexes](https://www.mongodb.com/docs/manual/indexes/), [`explain` results](https://www.mongodb.com/docs/manual/reference/explain-results/), and [query optimization](https://www.mongodb.com/docs/manual/core/query-optimization/).
- [Transactions production considerations](https://www.mongodb.com/docs/manual/core/transactions-production-consideration/), [replication](https://www.mongodb.com/docs/manual/replication/), and [sharding](https://www.mongodb.com/docs/manual/sharding/).
- [Backup methods](https://www.mongodb.com/docs/manual/core/backups/), [monitoring](https://www.mongodb.com/docs/manual/administration/monitoring/), and the [security checklist](https://www.mongodb.com/docs/manual/administration/security-checklist/).
- [Spring Data MongoDB reference](https://docs.spring.io/spring-data/mongodb/reference/) for mapping, `MongoTemplate`, repositories, sessions, transactions, and index management.

Tie document boundaries, indexes, concerns, transactions, and shard keys to actual access patterns. Retain Phase 5 as evidence, but the final Trackr runtime is PostgreSQL-only after the clean adapter/data cutover.

## PostgreSQL and SQL Resources

- [Current PostgreSQL manual](https://www.postgresql.org/docs/current/) for SQL, data definition, administration, backup, and monitoring.
- [SQL language](https://www.postgresql.org/docs/current/sql.html), [data types](https://www.postgresql.org/docs/current/datatype.html), and [constraints](https://www.postgresql.org/docs/current/ddl-constraints.html).
- [`EXPLAIN`](https://www.postgresql.org/docs/current/using-explain.html), [index types](https://www.postgresql.org/docs/current/indexes-types.html), and [routine vacuuming](https://www.postgresql.org/docs/current/routine-vacuuming.html).
- [Transaction isolation](https://www.postgresql.org/docs/current/transaction-iso.html), [explicit locking](https://www.postgresql.org/docs/current/explicit-locking.html), and [concurrency control](https://www.postgresql.org/docs/current/mvcc.html).
- [Backup and restore](https://www.postgresql.org/docs/current/backup.html), [continuous archiving/PITR](https://www.postgresql.org/docs/current/continuous-archiving.html), and [monitoring](https://www.postgresql.org/docs/current/monitoring.html).
- [Flyway documentation](https://documentation.red-gate.com/fd) for migration lifecycle and [HikariCP project](https://github.com/brettwooldridge/HikariCP) for pool configuration/metrics.

Store SQL, parameter shapes, schema/data scale, actual plan, relevant buffers/timing, and interpretation together. Run `ANALYZE` only in a safe environment when the query itself is safe. Restore a backup to prove recoverability.

## Testing Resources

- [JUnit User Guide](https://docs.junit.org/current/user-guide/) for lifecycle, assertions, parameterized tests, tags, parallel execution, and extensions.
- [AssertJ documentation](https://assertj.github.io/doc/) for readable outcome assertions.
- [Spring Boot testing reference](https://docs.spring.io/spring-boot/reference/testing/) and [Spring Framework testing reference](https://docs.spring.io/spring-framework/reference/testing.html).
- [Spring Security test support](https://docs.spring.io/spring-security/reference/servlet/test/index.html) for actual security-filter behavior in servlet tests.
- [Testcontainers for Java](https://java.testcontainers.org/) and [JUnit 5 integration](https://java.testcontainers.org/test_framework_integration/junit_5/).
- [Testcontainers PostgreSQL module](https://java.testcontainers.org/modules/databases/postgres/) for real database integration.
- [jcstress](https://openjdk.org/projects/code-tools/jcstress/) only for low-level concurrency questions that justify a specialized harness.

Use JUnit/AssertJ for observable outcomes, MockMvc for Spring MVC behavior, and Testcontainers PostgreSQL for migrations, SQL, locks, and transaction semantics. A unit mock does not prove isolation; one stress run does not prove absence of data races; JFR is diagnosis, not a test oracle.

## Security and Supply-Chain Resources

- [OWASP ASVS](https://owasp.org/www-project-application-security-verification-standard/) for testable application controls.
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/) for authentication, password storage, sessions, CSRF, CORS, SQL injection, SSRF, logging, and secrets.
- [OWASP API Security](https://owasp.org/API-Security/) for API threat prompts.
- [NIST Digital Identity Guidelines](https://pages.nist.gov/800-63-4/) for identity lifecycle.
- [Spring Security reference](https://docs.spring.io/spring-security/reference/) for framework behavior; verify the exact version used.
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/) for known-component detection and [CycloneDX Maven plugin](https://cyclonedx.github.io/cyclonedx-maven-plugin/) for SBOM generation.
- [OpenSSF Scorecard](https://securityscorecards.dev/) and [SLSA](https://slsa.dev/) as supply-chain review models, not automatic trust scores.

Map each control or finding to Trackr’s threat model, reachable behavior, owner, artifact digest, and resolution. A copied checklist or clean scan is not proof of security.

## Delivery, Observability, and JVM Diagnostics

- [OCI Image Specification](https://github.com/opencontainers/image-spec), [Dockerfile reference](https://docs.docker.com/reference/dockerfile/), and [Compose Specification](https://compose-spec.io/).
- [Spring Boot container images](https://docs.spring.io/spring-boot/how-to/container-images.html), [efficient deployments](https://docs.spring.io/spring-boot/how-to/deployment/efficient.html), and [graceful shutdown](https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html).
- [Spring Boot Actuator endpoints](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html) and [observability](https://docs.spring.io/spring-boot/reference/actuator/observability.html).
- [Micrometer documentation](https://docs.micrometer.io/micrometer/reference/) and [Micrometer Tracing](https://docs.micrometer.io/tracing/reference/).
- [OpenTelemetry documentation](https://opentelemetry.io/docs/), [Java instrumentation](https://opentelemetry.io/docs/languages/java/), and [OTLP specification](https://opentelemetry.io/docs/specs/otlp/).
- [Java Flight Recorder guide](https://docs.oracle.com/en/java/javase/25/jfapi/flight-recorder.html), [JDK Mission Control](https://www.oracle.com/java/technologies/jdk-mission-control.html), and [JDK troubleshooting guide](https://docs.oracle.com/en/java/javase/25/troubleshoot/).
- [Google SRE books](https://sre.google/books/) for SLOs, error budgets, incidents, and toil.

Pin and promote immutable artifacts, run non-root, leave JVM native-memory headroom, secure management/diagnostic access, redact telemetry, and prove rollout/rollback. A dashboard matters only when it supports a user-impact, diagnosis, or capacity decision.

## Maven and Application Commands

Run commands from Trackr’s repository with the checked-in wrapper. Company profiles may add or replace goals; record exact versions and active profiles with evidence.

```text
./mvnw --version
./mvnw help:active-profiles
./mvnw -DskipTests compile
./mvnw test
./mvnw -Dtest=IssueServiceTest test
./mvnw -Dtest=IssueHttpTest#rejectsCrossTenantRead test
./mvnw -DskipTests package
./mvnw spring-boot:run
./mvnw dependency:tree
./mvnw versions:display-dependency-updates
./mvnw flyway:info
./mvnw flyway:validate
java -version
java -jar target/<trackr-artifact>.jar
curl -i http://localhost:8080/actuator/health/readiness
```

Run plugin-specific vulnerability and SBOM goals only when those plugins and policies are configured; capture their version and output. Never paste secrets in command arguments or commit environment files.

## JVM Diagnostic Commands

First identify the intended local/container JVM and obtain authorization; attach and output may expose sensitive data.

```text
jcmd -l
jcmd <pid> VM.version
jcmd <pid> VM.flags
jcmd <pid> VM.system_properties
jcmd <pid> GC.heap_info
jcmd <pid> Thread.print -l
jcmd <pid> JFR.check
jcmd <pid> JFR.start name=trackr settings=profile duration=60s filename=/safe/trackr.jfr
jcmd <pid> JFR.dump name=trackr filename=/safe/trackr-now.jfr
jcmd <pid> JFR.stop name=trackr
jfr summary /safe/trackr.jfr
jfr print --events jdk.CPULoad,jdk.GarbageCollection /safe/trackr.jfr
```

Use JDK Mission Control for correlated inspection. Avoid dumping all properties in shared logs; they may contain configuration/secrets. Capture multiple thread dumps before diagnosing a persistent wait, and delete sensitive recordings according to policy.

## MongoDB Commands

Use an isolated development/restore environment and a least-privileged URI. Exact commands depend on MongoDB version and topology.

```text
mongosh "$MONGODB_URI"
db.runCommand({ ping: 1 })
db.version()
db.getCollectionInfos()
db.issues.getIndexes()
db.issues.find({ projectId: <id> }).sort({ updatedAt: -1, _id: -1 }).limit(20).explain("executionStats")
db.issues.validate({ full: true })
db.issues.aggregate([/* reviewed bounded pipeline */], { explain: true })
rs.status()
rs.printReplicationInfo()
sh.status()
```

Inspect winning plan, keys/docs examined, returned rows, execution time, index bounds, rejected plans, replication lag, and document/index sizes. Do not run unbounded scans, `full` validation, replica/sharding commands, or destructive migration work against production without an approved plan.

## PostgreSQL Commands

Use a safe database role/environment and set statement timeouts for investigative queries.

```text
psql "$DATABASE_URL"
\conninfo
\d+ issues
\di+
SELECT version();
SHOW transaction_isolation;
SELECT * FROM pg_stat_activity WHERE datname = current_database();
SELECT * FROM pg_stat_database WHERE datname = current_database();
EXPLAIN (ANALYZE, BUFFERS, SETTINGS) SELECT ...;
pg_dump --format=custom --file=trackr.dump "$DATABASE_URL"
createdb trackr_restore_check
pg_restore --dbname=trackr_restore_check --exit-on-error trackr.dump
```

`EXPLAIN ANALYZE` executes the statement: use it only where effects and load are safe. Sanitize query text/output. A backup file is not restore proof; verify schema, row/domain invariants, and a Trackr journey in the restored database.

## Per-Phase Artifact Checklist

- **README:** schedule, product scope, dependency map, artifact conventions, and navigation.
- **01:** learning loop, debugging journal, corrected misconception, and baseline plan.
- **02:** Java/JVM experiment, importer, immutable model, resource/error and memory/concurrency explanations.
- **03:** bounded Spring MVC service, HTTP/error contract, timeout/cancellation, and graceful shutdown evidence.
- **04:** modular boundaries, OpenAPI, DTO mapping, transaction ownership, CRUD/cursor/conflict/idempotency evidence.
- **05:** production-conscious Mongo model, validation/index/explain, concurrency, replication/backup/restore evidence.
- **06:** PostgreSQL schema/Flyway, JdbcClient adapter, transaction/query plan, data migration/reconciliation, clean cutover plan.
- **07:** JMM reasoning, bounded executor/semaphore, interruption, retry/outbox/replay, JUnit/MockMvc/Testcontainers, stress/JFR/thread dumps.
- **08:** threat model, Spring Security chain, session/CSRF/CORS, tenant abuse, safe errors, dependency scan/SBOM.
- **09:** layered non-root image, Maven CI, Flyway release, JVM memory envelope, correlated telemetry, alert, JFR diagnosis, rollback.
- **10:** PostgreSQL-only deployed app, browser journeys, concurrency/security evidence, accessibility review, architecture record, runbook, demo.
- **11 optional:** narrow Java `Summarizer`, immutable validated DTO, budgets, injection/fallback, deterministic tests, versioned evaluation.
- **12:** recorded design, calculations, JVM/Spring/PostgreSQL/Mongo reasoning, scored rubric, six truthful stories.
- **13:** resolved evidence map, final rubric, reproducibility audit, and honest next targets.

Every link must open, identify the exercised revision/version, and contain observations rather than future promises. Remove obsolete Mongo runtime artifacts after the Phase 10 cutover while retaining sanitized learning evidence outside the production path.

## Capstone Rubric

Score 0 absent, 1 partial/fragile, 2 complete with evidence, 3 complete plus failure proof and trade-off explanation.

| Area | Evidence required for at least 2 |
|---|---|
| Product/API | Required browser journeys after refresh; OpenAPI/runtime parity |
| Java/Spring | Clear MVC/application/JdbcClient boundaries; validated DTOs; lifecycle/resource reasoning |
| Mongo learning | Production-conscious Phase 5 model/index/operation evidence |
| PostgreSQL | PostgreSQL-only final runtime; constraints, transaction, cutover, plan, restore |
| Reliability | Conflict, idempotency, backpressure, interruption, stress and JVM diagnostics |
| Security | Spring Security session lifecycle, CSRF/CORS, tenant/abuse rejection, SBOM triage |
| Client | Error/conflict states and keyboard-accessible critical path |
| Operations | Reproducible image/CI, telemetry, memory envelope, alert, rollout/rollback |
| Communication | Decisions, runbook, design recording, honest demo and stories |

Every required area must score at least 2. Features cannot compensate for zero security, integrity, reproducibility, or core behavior. The LLM elective cannot raise the score of an incomplete required area.

## Portfolio and Interview Checklist

- Clean-checkout instructions use the Maven Wrapper and work without private steps; CI and artifacts match the shown revision.
- JDK, Spring Boot, dependencies, database, container, and browser versions are recorded.
- No credentials, sessions, personal/production data, raw prompts, heap dumps, or unsafe JFR artifacts are published.
- Architecture shows Browser → Spring MVC → application services → JdbcClient → PostgreSQL and matches runtime.
- Final deployment, configuration, health checks, and dependencies contain no MongoDB runtime path.
- OpenAPI, Java DTOs, JavaScript client types/behavior, Flyway schema, and safe errors agree.
- Demo includes one collaboration journey, concurrent conflict or tenant denial, correlated diagnosis, and recovery.
- Three decisions name alternatives, protected invariants, operational costs, and reversal evidence.
- Six stories state truthful personal actions and observed results.
- The 45-minute design and core Java/JVM, Spring, SQL/PostgreSQL, and MongoDB topics can be explained unaided.
- Any LLM capability is labeled, evaluated, budgeted, disableable, and removable.

## Decision Rules and Trade-offs

| Audit choice | Default | Use the alternative when |
|---|---|---|
| Reference | Version-matched official documentation | Secondary material discovers context, then primary evidence confirms it |
| Proof surface | Real dependency/deployed boundary | Fake is enough for deterministic behavior Trackr owns |
| Permanent test | Observable contract or plausible invariant bug | Throwaway experiment answers one-time uncertainty |
| Database evidence | Actual plan/transaction/restore | Schema inspection alone cannot establish runtime behavior |
| JVM claim | Metrics plus focused JFR/thread/test evidence | Never infer from generic tuning advice |
| Gate score | Linked observed evidence | Mark developing rather than infer competence from file presence |
| Optimization | Query/load/profile measurement | Reject complexity until a bottleneck is demonstrated |
| Portfolio breadth | Small reproducible evidence set | Add features only after every required gate reaches 2 |

## Common Pitfalls

- Using old blog posts for current JDK/Spring behavior without checking release/version docs.
- Linking a command without input, observed output, environment, revision, and interpretation.
- Counting mocked SQL, cookies, proxy behavior, migration, or restore as real-boundary evidence.
- Treating a stress test, JFR, benchmark, and load test as interchangeable.
- Claiming JVM races are absent because tests passed; Java has no standard Go-style race detector.
- Publishing `jcmd` properties, logs, dumps, database exports, or screenshots before secret/privacy review.
- Running `EXPLAIN ANALYZE`, validation, load, or destructive commands in production casually.
- Leaving MongoDB driver/configuration in the final runtime after calling the cutover complete.
- Awarding rubric points for file or annotation presence without exercising behavior.
- Letting AI turn uncertain evidence into confident claims or repair broken links by deleting requirements.

## AI-Assisted Audit

AI may detect missing links, compare the rubric with artifacts, suggest counterexamples, or conduct a mock interview. It cannot invent proof, infer an unobserved result, upgrade a weak score, receive secrets/private data, or serve as sole reviewer.

Independently open every internal artifact and primary reference, verify version compatibility, and rerun the smallest decisive scenario when evidence is stale. Sample Maven reports, migration state, security behavior, query plans, browser journeys, container identity, telemetry, JFR/thread evidence, and restore/rollback. Explain each sample aloud; inability to explain it fails the gate even when an AI approves it.

## Glossary

- **Backpressure:** limiting or rejecting intake when downstream capacity is exhausted.
- **Happens-before:** Java Memory Model ordering that guarantees visibility of prior actions.
- **Idempotency:** repeated equivalent requests/effects preserve one intended outcome.
- **Invariant:** condition preserved across operations, concurrency, and failure.
- **Isolation:** visibility/ordering guarantees among concurrent database transactions.
- **Modular monolith:** one deployable with enforced internal capability boundaries.
- **Opaque session:** random identifier whose authority is resolved server-side.
- **Outbox:** durable events committed in the same transaction as the business mutation.
- **RPO/RTO:** tolerated data-loss window and target recovery time.
- **SBOM:** machine-readable inventory of components in an artifact.
- **SLI/SLO:** measured service indicator and its target objective.
- **Virtual thread:** lightweight JVM-managed thread for blocking-style concurrency, not a resource-capacity bypass.

## Audit Exercises and Evidence

1. **Version audit:** resolve the JDK, Spring Boot, Maven plugins, drivers, databases, and container bases actually used. **Evidence:** version matrix linked to release/support sources and the built digest.
2. **Boundary sample:** rerun one JUnit case, MockMvc security case, Testcontainers transaction case, browser journey, and restore journey. **Evidence:** sanitized observed result, revision, environment, and explanation of what each does not prove.
3. **Production-path audit:** inspect runtime dependencies, configuration, image, health, and deployment. **Evidence:** PostgreSQL is the only data authority and no MongoDB driver/service/fallback remains.
4. **Operations drill:** induce bounded database or worker pressure, follow Micrometer/OTLP signals, inspect JFR/thread dumps, and recover. **Evidence:** timeline, diagnosis, user effect, and corrected decision.
5. **Independent review:** give the artifact map and clean-checkout instructions to another developer. **Evidence:** their reproduction notes, defects found, corrections, and final rubric score.

## Exit Exercise

Audit the entire roadmap from a clean checkout. Open every evidence link; reproduce the build, PostgreSQL-only deployment, migrations, core browser journey, cross-tenant denial, concurrent conflict, bounded saturation, telemetry/JFR diagnosis, backup restore, and rollback. Score the rubric, perform the recorded system-design interview, explain sampled artifacts without AI, and mark unsupported competencies as developing.

**Exit gate:** every required rubric area scores at least 2 with genuine, sanitized, versioned evidence; all relative navigation links resolve; official references match the exercised stack; final runtime has one PostgreSQL authority and no MongoDB path; another developer reproduces the critical journey and recovery; and every published claim can be defended without AI assistance.

## Related Documents

- [Previous: System Design and Job Readiness](12-system-design-job-readiness.md)
- [Roadmap Navigation](README.md)
- [Start Again: Learning Method](01-learning-method.md)
