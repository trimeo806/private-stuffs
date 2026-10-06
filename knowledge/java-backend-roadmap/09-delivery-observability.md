# Phase 9: Delivery and Observability for the JVM

## Purpose

Make Trackr reproducibly buildable, safely deployable, resource-aware, diagnosable, and reversible as a Spring Boot/PostgreSQL service.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Operational Philosophy](#operational-philosophy)
- [Build Artifact and Container](#build-artifact-and-container)
- [JVM and Container Resources](#jvm-and-container-resources)
- [Development Stack and Migrations](#development-stack-and-migrations)
- [Maven CI and Supply Chain](#maven-ci-and-supply-chain)
- [Actuator, Metrics, Logs, and Traces](#actuator-metrics-logs-and-traces)
- [JFR, JMC, and jcmd](#jfr-jmc-and-jcmd)
- [Health, SLOs, and Alerts](#health-slos-and-alerts)
- [Rollout, Rollback, and Incidents](#rollout-rollback-and-incidents)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [Common Pitfalls](#common-pitfalls)
- [AI-Assisted Workflow](#ai-assisted-workflow)
- [Hands-On Exercises and Evidence](#hands-on-exercises-and-evidence)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

A service is unfinished when it works only from an IDE. Trackr needs a repeatable Maven build, an immutable non-root runtime image, controlled Flyway migrations, intentional JVM/container limits, correlated telemetry, an actionable SLO, and a rollback path exercised against the actual artifact.

## Agent & Skills

- **Agent:** `devops-engineer`
- **Skills:** `infra-docker`, `test`, `core`
- **Handoff:** give Phase 10 the image digest, clean build evidence, migration and release procedure, resource envelope, telemetry map, JFR diagnosis, alert exercise, incident note, and rollback proof.

## Operational Philosophy

Prefer boring, inspectable lifecycle boundaries. Company JDK, Maven, repository, base-image, and release conventions override greenfield choices; otherwise use JDK 25 LTS, the Maven Wrapper, and the current stable Spring Boot 4.1 service release. Pin inputs and promote the same built artifact through environments.

A container packages a JVM process; it does not erase memory accounting, signals, DNS, TLS, filesystems, database capacity, or startup behavior. Keep Trackr a modular monolith. Add operational components only when a named reliability or capacity pressure repays their failure modes.

## Build Artifact and Container

Build an executable layered Spring Boot jar in a multi-stage container:

1. copy the Maven Wrapper, POM, and dependency descriptors first to leverage the cache;
2. resolve/build with `./mvnw` in a pinned JDK builder image and a clean context;
3. extract or use Spring Boot jar layers so dependencies and application code form stable image layers;
4. copy the layers into a pinned, patched JRE runtime image;
5. create/use a numeric non-root user and a read-only filesystem where practical;
6. include required CA certificates, time-zone data, and diagnostics intentionally;
7. set an exec-form entry point and let the JVM receive termination signals;
8. record source revision, base image, final digest, SBOM, and build configuration.

Do not copy Maven caches, source credentials, `.env`, or test artifacts into the runtime image. A smaller JRE is valuable only if operators can still establish health and capture approved diagnostics. Prefer a vendor-supported minimal JRE or a tested `jlink` image; custom modules increase patch and compatibility responsibility.

Use Spring Boot graceful shutdown and an orchestration grace period longer than the application’s bounded drain. Stop accepting traffic before termination, finish only promised work, and leave durable outbox items reclaimable.

## JVM and Container Resources

Container memory includes more than Java heap: metaspace, code cache, thread stacks, direct/native buffers, memory-mapped files, agents, and JVM/native overhead. Leave headroom instead of setting maximum heap equal to the container limit. Inspect actual heap ergonomics and choose explicit percentage or size settings according to platform policy.

Track:

- heap used/committed/max and allocation/GC pause behavior;
- metaspace and class loading;
- process resident memory and container working set;
- platform and virtual thread counts, stack/native pressure, and pinned/blocked behavior;
- direct buffer usage;
- CPU throttling and effective processors;
- Hikari active/idle/pending connections and acquisition time;
- executor queue/activity and outbox age.

Set Hikari maximum pool size from PostgreSQL’s total connection budget across all replicas, migrations, operations, and safety reserve—not from HTTP thread count. A virtual thread still waits for a real connection. Set query, transaction, socket, request, and shutdown budgets coherently.

## Development Stack and Migrations

Use Compose for reproducible local PostgreSQL plus optional telemetry collector. Health checks establish readiness; container start order does not. Keep development credentials isolated and disposable.

Flyway owns ordered SQL migrations. Validate/check migration state in CI and apply production migrations as a controlled one-shot release task with exactly one owner. Do not let every application replica race schema changes at startup. Use expand/migrate/contract:

1. add backward-compatible schema;
2. deploy code that tolerates old/new states;
3. backfill with bounded, restartable work;
4. verify invariants;
5. remove old code, then contract in a later release.

Back up before risky changes and restore into an isolated environment. Rollback of the application may require roll-forward of data/schema; never assume down migrations are safe.

## Maven CI and Supply Chain

Run the repository’s Maven Wrapper from a clean checkout. A practical pipeline orders fast feedback before expensive gates:

1. validate wrapper/JDK/project configuration and compile;
2. run focused unit and Spring MVC tests;
3. run integration tests against Testcontainers PostgreSQL;
4. verify OpenAPI and Flyway migration policy;
5. scan dependencies, plugins, secrets, and container; generate an SBOM;
6. package the layered jar once;
7. build/sign or attest the image and record digest/provenance;
8. deploy that digest to a safe environment;
9. apply the controlled migration and smoke critical journeys;
10. promote the same digest after gates and retain reports.

Use cache keys that cannot substitute a different dependency graph. Keep deploy credentials out of untrusted pull-request jobs. Treat flaky tests as defects. Company build conventions decide exact Maven profiles and quality tools; do not create a second pipeline vocabulary.

## Actuator, Metrics, Logs, and Traces

Expose only required Spring Boot Actuator endpoints. Separate and secure management access; health details, environment/configuration, loggers, heap dumps, and mappings can disclose sensitive data. Define liveness and readiness groups deliberately.

Micrometer supplies application observations and metrics. Start with:

- HTTP request rate, server-error rate, and duration by route template and outcome;
- Hikari connections, pending acquisitions, and timeouts;
- JdbcClient/SQL span latency without statement parameters or sensitive text;
- executor active tasks, queue/rejections, and outbox depth/oldest age;
- JVM heap, GC, threads, CPU, and process/container signals;
- authentication denials, rate limits, and dependency outcomes at bounded cardinality.

Use structured logs with timestamp, level, service/version, safe event name, route template, outcome, request ID, trace/span ID, and controlled error category. Never label metrics with raw paths, user IDs, project IDs, email, SQL values, or exception messages.

Export traces and metrics using Micrometer-supported OpenTelemetry/OTLP configuration to an OpenTelemetry Collector or approved backend. Propagate validated trace context through Spring MVC, application service, JdbcClient, and outbox work. Sampling controls cost; ensure error/slow-path evidence remains useful. Request IDs aid support and trace IDs connect spans; they are related but not interchangeable.

## JFR, JMC, and jcmd

Java Flight Recorder is the first-line low-overhead production diagnostic when policy allows it. Capture a bounded recording around a known symptom and inspect it in JDK Mission Control. Correlate CPU, allocation, GC, socket/file I/O, monitor contention, thread parking, exceptions, Hikari waits, and virtual-thread pinning with service telemetry.

Use `jcmd` against an authorized local JVM to inspect `VM.version`, `VM.flags`, `GC.heap_info`, `Thread.print -l`, and `JFR.check/start/dump/stop`. Attach permissions are sensitive; do not expose them through an application endpoint or broadly enable remote management. Heap dumps and recordings may contain user data or credentials: protect, limit retention, and sanitize before sharing.

A single snapshot rarely establishes a trend. Capture thread dumps over time and compare them with metrics and traces. Tune only after identifying the dominant user-facing constraint, then rerun the same scenario.

## Health, SLOs, and Alerts

- **Liveness:** the process is not irrecoverably stuck; a brief database outage should not trigger a restart storm.
- **Readiness:** this instance should receive new traffic and has completed required startup state.
- **Startup:** protects legitimately slow initialization where the platform supports it.

Define one Trackr SLI, such as the proportion of valid issue reads completed successfully below a latency threshold, and an SLO over a window. Alert on sustained user harm or imminent exhaustion: error-budget burn, server-error ratio, Hikari wait, outbox age, memory headroom, GC pause pressure, or storage risk. Every alert needs owner, severity, dashboard/query, first actions, and escalation. Do not alert on every exception.

## Rollout, Rollback, and Incidents

Use a rolling rollout for ordinary backward-compatible changes or a canary when limiting exposure repays routing and analysis cost. Verify readiness, SLO, security and data invariants before widening. Drain instances within a bounded grace period.

Rollback means redeploying a known previous image digest and confirming restored user behavior. Schema or data incompatibility may force roll-forward, so rehearse both. An incident runbook covers detection, triage, dependency checks, safe stabilization, rollback/roll-forward criteria, communication, evidence preservation, and escalation. Timestamp decisions and change one variable at a time where safety permits.

## Decision Rules and Trade-offs

| Decision | Default | Change when |
|---|---|---|
| Fat jar vs layered jar | Layered executable jar/image | Simpler artifact is worth slower, larger image churn |
| Full JRE vs `jlink` | Supported minimal JRE | Tested custom runtime reduces a measured constraint |
| Build per environment vs promote | Build once, promote digest | Never rebuild for configuration differences |
| Startup migration vs release task | Controlled one-shot Flyway task | Only a single-instance environment accepts coupling |
| Heap sizing | Leave native/container headroom | Measurements justify a different envelope |
| Logs vs metrics vs traces vs JFR | Use each for its own question | No one signal substitutes for the others |
| Rolling vs canary | Rolling for compatible low-risk change | Risk/traffic justifies controlled exposure |
| Rollback vs roll-forward | Prepare both | Data transformations determine which is safe |

## Common Pitfalls

- Using an unpinned `latest` builder/runtime image or rebuilding different artifacts per environment.
- Running as root, copying secrets into layers, or assuming a tiny image is automatically secure.
- Setting `-Xmx` to the container limit and ignoring native/thread/direct memory.
- Sizing Hikari per replica without a global PostgreSQL connection budget.
- Running Flyway concurrently from every replica or contracting schema before old code exits.
- Exposing all Actuator endpoints or recording high-cardinality/sensitive telemetry.
- Treating Compose start order as readiness or a dependency outage as a liveness failure.
- Capturing JFR/heap diagnostics without retention and access controls.
- Calling a dashboard, successful deploy, or rollback script “proof” without inducing and observing the path.

## AI-Assisted Workflow

Use AI to review a Dockerfile, propose incident hypotheses, or interpret unfamiliar JFR event categories. Never provide secrets, production data, raw heap dumps, or unsanitized recordings. Independently verify image layers/configuration, effective JVM flags, Actuator exposure, OTLP signals, migration ordering, and rollback against the actual digest. Follow one real request and inspect the recording yourself; generated commands are untrusted until their blast radius and version compatibility are understood.

## Hands-On Exercises and Evidence

1. **Clean artifact:** build the layered jar and image from a clean context. **Evidence:** JDK/Maven/Boot versions, source revision, digest, layers, SBOM, numeric runtime user, and health response.
2. **Memory envelope:** run under a fixed container limit and representative load. **Evidence:** heap/native headroom, GC behavior, thread counts, Hikari wait, and chosen settings.
3. **Compatible migration:** overlap old and new application versions around an additive Flyway change. **Evidence:** both serve core journeys, followed by verified later contraction.
4. **Trace:** follow issue creation from Spring MVC through application service, JdbcClient, PostgreSQL, and outbox. **Evidence:** correlated safe log, metric, trace, and committed rows.
5. **Diagnostic drill:** induce database delay or executor saturation. **Evidence:** bounded JFR, JMC interpretation, thread dumps, relevant metrics, and one justified change/retest.
6. **Release failure:** canary a deliberately faulty version and recover. **Evidence:** alert threshold, timeline, image digests, migration state, restored SLI, and incident note.

## Exit Exercise

Ship Trackr as a non-root layered JRE image built once by Maven CI. Apply Flyway as a controlled release task; configure JVM/container and Hikari limits; expose secured Actuator health plus Micrometer logs, metrics, and OTLP traces; diagnose an induced slowdown with JFR/JMC/`jcmd`; and execute a digest-based rollout and rollback.

**Exit gate:** a clean checkout produces the identified artifact; runtime memory has demonstrated headroom; migrations remain compatible and single-owned; one request is correlated through SQL/outbox without sensitive telemetry; an actionable alert detects induced harm; JFR evidence supports the diagnosis; and rollback or justified roll-forward restores the SLI.

## Related Documents

- [Previous: Security](08-security.md)
- [Roadmap Navigation](README.md)
- [Next: Full-Stack Capstone](10-full-stack-capstone.md)
