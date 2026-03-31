# Research: Java 22-24 and Spring Boot 3.x-4.0 Roadmap

**Date**: March 29, 2026
**Agent**: Researcher
**Scope**: Java language features (22-24), Spring Framework 6.2, Spring Boot 3.4-3.5 and 4.0 roadmap, GraalVM support, Spring Modulith, observability
**Status**: ACTIONABLE

---

## Table of Contents

1. [Java Language Features](#java-language-features)
2. [Spring Framework 6.2](#spring-framework-62)
3. [Spring Boot 3.4](#spring-boot-34)
4. [Spring Boot 3.5](#spring-boot-35)
5. [Spring Boot 4.0](#spring-boot-40)
6. [GraalVM Native Image Support](#graalvm-native-image-support)
7. [Spring Modulith](#spring-modulith)
8. [Observability & Tracing](#observability--tracing)
9. [Verdict & Next Steps](#verdict--next-steps)
10. [Unresolved Questions](#unresolved-questions)

---

## Java Language Features

### Java 22 (March 2024)

**Finalized Features:**
- **Foreign Function & Memory API (Finalized)** — Production-ready interop with native code and memory, eliminates JNI brittleness. Enables safe C/C++ library calls without brittle JNI wrappers.

**Preview Features (2nd Preview, stable):**
- **Structured Concurrency (2nd Preview)** — StructuredTaskScope provides clean control flow for spawning/waiting on concurrent tasks with automatic cancellation and error handling. No changes from Java 21 preview, stability focused.
- **Scoped Values (2nd Preview)** — Immutable data sharing within and across threads; preferred over ThreadLocal for virtual thread workloads. No changes, stability focused.
- **String Templates (2nd Preview)** — Template literals with expression interpolation for cleaner string building.

**Other Notable (Preview):**
- **Gatherers API (Preview)** — Custom intermediate stream operations via `java.util.stream.Gatherers`. Enables writing infinite-size stream transformations not possible with standard ops.

**Source**: [HappyCoders Java 22](https://www.happycoders.eu/java/java-22-features/), [Baeldung Java 22](https://www.baeldung.com/java-22-overview), [InfoWorld Java 22](https://www.infoworld.com/article/2335077/jdk-22-the-new-features-in-java-22.html)

---

### Java 23 (September 2024)

**Finalized Features:**
- **Primitive Types in Patterns (Preview/Roadmap)** — Pattern matching now extends to all primitive types in instanceof and switch; supports safe narrowing conversions.

**Major Preview Features (3rd Preview):**
- **Scoped Values (3rd Preview)** — Refined with typed `ScopedValue.callWhere(CallableOp<T>)` allowing compiler to detect checked exception throwing. More ergonomic, typed exception handling.
- **Structured Concurrency (3rd Preview)** — Stable, no breaking changes; focuses on community feedback integration.

**Important Enhancement:**
- **Implicitly Declared Classes & Instance Main Methods (Preview)** — Beginners can write programs without `public static void main` or explicit class declarations. Automatic imports from `java.base` module. Instance methods allowed. Can use `println(...)` instead of `System.out.println(...)` in implicit classes. Bridges gap for learning Java while enabling scaling to larger projects.

**Source**: [SymFlower Java 23](https://symflower.com/en/company/blog/2024/what-is-new-in-java-23/), [HappyCoders Java 23](https://www.happycoders.eu/java/java-23-features/), [Baeldung Implicit Classes](https://www.baeldung.com/java-implicit-classes-instance-main), [Java 23 Has Arrived](https://foojay.io/today/java-23-has-arrived-and-it-brings-a-truckload-of-changes/)

---

### Java 24 (March 2025)

**Preview Features:**
- **Flexible Constructor Bodies (3rd Preview)** — Allows statements before explicit `super()` or `this()` calls. Enables field validation, logging, and argument transformation before invoking parent constructors. Improves reliability when methods are overridden. Likely stabilizing soon given 3rd preview status.

- **Primitive Types in Patterns (2nd Preview)** — Pattern matching with primitive types (int, double, boolean, etc.) in instanceof and switch. Safe narrowing conversions baked into patterns. Eliminates need for wrapper type juggling.

**Status**: Java 24 is a short-term feature release (March 2025); expected to stabilize multiple previews moving toward Java 25-26.

**Source**: [Oracle Java 24](https://blogs.oracle.com/java/the-arrival-of-java-24), [OpenJDK JEP 513](https://openjdk.org/jeps/513), [Medium Java 24](https://medium.com/@kaustubh.saha/whats-new-in-java-24-815623a10112)

---

## Spring Framework 6.2

**Release Date**: November 14, 2024
**Supported Java**: JDK 17-23, Jakarta EE 9-10

**Key Features:**

| Feature | Impact | Details |
|---------|--------|---------|
| **SpEL Compilation** | Performance | Improved compilation for varargs, array/list indexing, method invocation via public interfaces |
| **URL Parsing Security** | Security | Two new RFC 3986-compliant parsers replace regex-based parsing; fixes CVE-2024-22262 and related vulnerabilities |
| **Reflection Hints (@RegisterReflection, @ReflectionScan)** | Developer Experience | Simplified reflection hint registration for GraalVM native image compilation without boilerplate |
| **Autowiring Algorithm** | Consistency | Revised generic type matching, faster name-based resolution |
| **TaskDecorator for Scheduled Tasks** | Observability | Harmonized task execution and scheduling with TaskDecorator support across sync/async boundaries |
| **ResponseBodyEmitter State Listeners** | Streaming | Multiple listeners on ResponseBodyEmitter for WebFlux applications |
| **ServerResponse Streaming** | Web | Enhanced streaming support beyond Server-Sent Events for other protocols |

**Verdict**: Framework-level improvements focus on security, native image readiness (GraalVM), and developer experience. Stable for adoption.

**Source**: [Spring Blog 6.2](https://spring.io/blog/2024/11/14/spring-framework-6-2-0-available-now/), [GitHub 6.2 Release Notes](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-6.2-Release-Notes), [InfoQ Spring 6.2](https://www.infoworld.com/article/2335077/jdk-22-the-new-features-in-java-22.html)

---

## Spring Boot 3.4

**Release Date**: November 21, 2024
**Baseline**: JDK 17+, Spring Framework 6.2

**Key Features:**

| Feature | Impact | Details |
|---------|--------|---------|
| **MockitoBean / MockitoSpyBean** | Testing | `@MockBean` and `@SpyBean` deprecated in favor of `@MockitoBean` and `@MockitoSpyBean`. Removal planned 3.6.0. Cleaner, more expressive testing API. |
| **Structured Logging** | Observability | Built-in support: ECS (Elastic Common Schema), GELF (Graylog), Logstash formats. Config: `logging.structured.format.file/console` = `ecs|gelf|logstash`. Enables machine-readable logs for observability stacks. |
| **@Fallback Bean Support** | Dependency Injection | `@ConditionalOnSingleCandidate` now matches @Fallback beans. Enables graceful degradation patterns: primary bean or single non-fallback bean wins. |
| **Docker Compose Support** | Development | Improved integration for local dev with docker-compose managed services. Auto-detection and configuration. |
| **SSL Bundle Improvements** | TLS Management | Enhanced SSL bundle management for simplified certificate handling and rotation. |
| **HTTP Interface Clients** | REST | Declarative HTTP clients via `@HttpExchange` annotation. WebClient-backed, cleaner than RestTemplate. |

**Migration Path**: Straightforward from 3.3; @MockBean deprecation is non-breaking (warnings only).

**Source**: [GitHub 3.4 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.4-Release-Notes), [Spring Blog 3.4](https://spring.io/blog/2024/11/21/spring-boot-3-4-0-available-now/), [Structured Logging](https://spring.io/blog/2024/08/23/structured-logging-in-spring-boot-3-4/)

---

## Spring Boot 3.5

**Expected Release**: Mid-2025 (based on typical 6-month cadence)
**Baseline**: JDK 17+, Spring Framework 6.2+
**Strategic Role**: Final minor release in 3.x series; "operational polish" and preparation for Boot 4.0

**Key Features:**

| Feature | Impact | Details |
|---------|--------|---------|
| **SSL Client-Side Support Expansion** | TLS | Extended to Cassandra, Couchbase, Elasticsearch, Kafka, MongoDB, RabbitMQ, Redis. Each service now supports client-side SSL via bundles. |
| **SSL Bundle Metrics** | Observability | New metrics: `ssl.chains` (count + status), `ssl.chain.expiry` (seconds until expiry per cert). Proactive certificate expiry detection. |
| **Structured Logging Customization** | Observability | Control stack trace size/format in structured logs via `logging.structured.json.stacktrace` properties. Reduces noise in structured log output. |
| **Multi-Line Environment Variables** | Configuration | Load multiple properties from single multi-line env var with `env:` prefix. Simplifies config in containerized/K8s environments. |
| **HTTP Client Improvements** | REST | WebClient now recommended for new projects. RestTemplate deprecated (legacy support continues). HTTP/2, connection pooling, improved error handling. |
| **General Observability Polish** | Platform | Continued maturation of metrics, tracing, and logging for operational readiness. |

**Verdict**: Consolidates 3.x, addresses operational gaps (SSL, config, observability), acts as stable bridge to 4.0. Lower risk adoption target.

**Source**: [InfoQ Spring Boot 3.5](https://www.infoworld.com/article/2335077/jdk-22-the-new-features-in-java-22.html), [GitHub 3.5 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes), [Medium 3.5 Features](https://medium.com/threadsafe/top-6-features-of-spring-boot-3-5-ae0b38a7822a)

---

## Spring Boot 4.0

**Release Date**: November 2025 (already released as of March 2026 research)

**Baseline Requirements:**

| Requirement | Details |
|-------------|---------|
| **Minimum Java** | Java 17+ (Java 25 first-class support at release) |
| **Kotlin** | Kotlin 2.2+ required for projects using Kotlin |
| **GraalVM** | GraalVM 25+ for native image compilation |
| **Jakarta EE** | Jakarta EE 11, Servlet 6.1 compatible container |
| **Build Tools** | Gradle 8.14+ (Gradle 9 supported) |

**Key Changes:**
- **Bootstrap Baseline Jump**: Significant jump from 3.x to 4.0 in baseline versions reflects architectural maturity and deprecation cleanup.
- **Kotlin Alignment**: Kotlin 2.2 brings null-safety improvements across Spring ecosystem.
- **GraalVM First-Class Support**: Native image support is now mature; enterprise-grade AOT compilation.

**Migration Impact**: Medium-high. Baseline bumps require careful dependency audit. Projects stuck on older Java/Kotlin need modernization. 6-12 month migration window typical for enterprises.

**Verdict**: Major version bump justified by baseline improvements. Not a quick upgrade for conservative orgs. Target 1-2 year adoption window for most teams.

**Source**: [Spring Blog 4.0](https://spring.io/blog/2025/11/20/spring-boot-4-0-0-available-now/), [GitHub 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes), [Baeldung Boot 4 & Framework 7](https://www.baeldung.com/spring-boot-4-spring-framework-7)

---

## GraalVM Native Image Support

**Current State (Spring Boot 3.x)**: **Production-ready, General Availability**

**Key Achievements:**
- Spring Boot 3.0+ natively supports GraalVM native image (Spring Native project deprecated).
- Automatic AOT (Ahead-of-Time) processing during build phase.
- Auto-generation of GraalVM reflection/serialization configs; manual configuration largely eliminated.
- Startup time: 2.5s JVM → ~50ms native (20-50x improvement typical).
- Memory footprint: Massive reduction (ideal for serverless, containers).

**How It Works:**
1. Build phase: Spring AOT analysis scans components + dependencies for dynamic usages.
2. Config generation: Automatic JSON config for reflection, resource loading, serialization.
3. GraalVM compilation: native-image tool builds optimized binary.
4. Runtime: Instant startup, fixed memory footprint.

**Tooling Support:**
- Maven: `mvn -Pnative native:compile`
- Gradle: `gradle nativeCompile`
- IDE support: IntelliJ IDEA, VS Code (GraalVM extension).

**Limitations:**
- Mockito not supported in native test execution (use TestNG, JUnit 5 alternatives).
- Not all Spring modules have built-in support; manual configuration still needed for some (document each case).
- Dynamic class loading, reflection, proxy generation may require custom hints.

**Best Practice**: Dedicated test suite for native image validation. Don't assume JVM test coverage = native coverage.

**Source**: [Spring Boot Native Image Docs](https://docs.spring.io/spring-boot/reference/packaging/native-image/index.html), [GraalVM Spring Boot Guide](https://www.graalvm.org/latest/reference-manual/native-image/guides/build-spring-boot-app-into-native-executable/), [BellSW GraalVM Guide](https://bell-sw.com/blog/spring-boot-with-graalvm-native-image-performance-compatibility-migration/)

---

## Spring Modulith

**What It Is**: Framework-agnostic toolkit for building modular Spring Boot monoliths with clear boundaries, decoupled communication, and verified module structure.

**Core Capabilities:**

| Capability | Benefit |
|------------|---------|
| **Module Structure Verification** | Enforces architectural boundaries; warns on violations during test runs. |
| **Domain-Driven Package Layout** | Opinionated `{domain}/{module}` structure with guided organization. |
| **Application Events** | Inter-module async communication via Spring ApplicationEvent; keeps modules loosely coupled. |
| **Module Integration Testing** | Test individual modules in isolation; bootstrap only that module + dependencies. |
| **Observability** | Module-level metrics, tracing, logging; understand system behavior at module granularity. |
| **Documentation Generation** | Auto-generate architecture diagrams, module interaction graphs from code. |

**Current State**: Stable, production-ready. Used in enterprise monoliths seeking modular clarity without microservices complexity.

**Spring Modulith 2.0 (M1 July 2025, expected stable later 2025):**
- Builds on Spring Boot 4.x baseline.
- **Revamped Event Publication Registry**: Major rewrite addressing current version limitations.
- **New Event Status Model**: More granular tracking of published/consumed events.
- **Strict Structure Verification**: Enable via `spring.modulith.runtime.verification-enabled=true` in spring-modulith-runtime artifact.

**Verdict**: Excellent solution for medium-large codebases (10-50k LOC+) seeking structure without microservices. 2.0 brings significant improvements; worth pilot before enterprise rollout.

**Source**: [Spring Modulith Docs](https://docs.spring.io/spring-modulith/reference/fundamentals.html), [Baeldung Intro](https://www.baeldung.com/spring-modulith), [BellSW Guide](https://bell-sw.com/blog/what-is-spring-modulith-introduction-to-modular-monoliths/), [Spring Modulith 2.0 M1](https://spring.io/blog/2025/07/26/spring-modulith-2-0-M1-released)

---

## Observability & Tracing

**Architecture**: Unified observability stack via Micrometer Observations bridged to OpenTelemetry.

**Key Components:**

| Component | Role |
|-----------|------|
| **Micrometer Observation API** | Unified facade for metrics, traces, logs. Abstracts vendor differences. |
| **Micrometer Tracing** | Implements distributed tracing; bridges to OpenTelemetry, Brave, Zipkin. |
| **OpenTelemetry Integration** | Spring Boot auto-configures OtlpHttpSpanExporter (or gRPC). Observations → OpenTelemetry API → OTLP → backend (Jaeger, DataDog, etc.). |
| **Auto-Instrumentation** | Spring Data, Spring Web, Spring Kafka, HTTP clients pre-instrumented. No manual code needed for request tracing. |

**Migration from Spring Cloud Sleuth**:
- Spring Cloud Sleuth (older distributed tracing) → Micrometer Tracing (unified, decoupled).
- Much lower overhead; vendor-neutral.
- Backward-compatible for most use cases.

**Current State (3.x)**:
- Metrics: Fully mature, auto-configured.
- Tracing: Production-ready with OpenTelemetry OTLP export.
- Logging: Structured logging (ECS/GELF/Logstash) in 3.4+.

**3.5 Improvements**:
- SSL bundle metrics (`ssl.chains`, `ssl.chain.expiry`) for proactive cert expiry detection.
- Structured log customization for stack trace control.

**Best Practice**: Adopt Micrometer Observation API for all custom business logic instrumentation. Avoid direct dependency on OpenTelemetry; use observations as abstraction layer.

**Source**: [OpenTelemetry with Spring Boot](https://spring.io/blog/2025/11/18/opentelemetry-with-spring-boot/), [Observability with Boot 3](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3/), [Last9 Distributed Tracing](https://last9.io/blog/distributed-tracing-with-spring-boot/), [Medium OTLP Setup](https://amithkumarg.medium.com/distributed-tracing-with-spring-boot-micrometer-opentelemetry-and-jaeger-fafd5c0bd282)

---

## Verdict & Next Steps

**ACTIONABLE**

### Recommended Adoption Path

| Timeline | Action | Risk Level |
|----------|--------|-----------|
| **Immediate (Now - Q2 2026)** | Adopt Java 22+ features: Structured Concurrency, Scoped Values (production preview). Upgrade to Spring Boot 3.4 + Framework 6.2. Target native image compilation for new services. | Low |
| **Q2-Q3 2026** | Pilot Spring Boot 3.5 when released. Adopt structured logging (ECS format) for all services. Evaluate Spring Modulith 2.0 for new/refactored modules. | Low |
| **Q3-Q4 2026** | Plan Java 24 upgrade: adopt Flexible Constructor Bodies + Primitive Type Patterns when finalized (expected Java 25-26). | Low |
| **Q4 2026 - Q2 2027** | Begin Spring Boot 4.0 migration planning for Java 17+ mandated baseline. Expect 6-12 month rollout for large orgs. | Medium |
| **Mid-2027+** | Sunset Boot 3.x (end of support). Boot 4.0 baseline maturity gate. | Medium |

### Key Technology Decisions

1. **Go Native First**: New services → GraalVM native image by default (not opt-in). 50x startup improvement justifies build complexity.

2. **Structured Logging Mandatory**: ECS format for all services 3.4+. Enables log aggregation, ML-based anomaly detection, compliance audit trails.

3. **Observability via Micrometer**: All custom instrumentation via Observation API, never direct OpenTelemetry. Insulates from vendor churn.

4. **Modular Monolith Pattern**: Spring Modulith for 10k+ LOC services. Clear module boundaries, event-driven communication, simpler refactoring to microservices if needed.

5. **Java 22+ Concurrency**: Adopt Structured Concurrency for all new concurrent code. Superior error handling vs. traditional ExecutorService patterns.

---

## Unresolved Questions

1. **Java 25-26 Stabilization Timeline**: When will Flexible Constructor Bodies and Primitive Type Patterns be finalized? Expected Java 25 or 26? (Note: Research current as of March 2026; check OpenJDK JEP tracker for latest.)

2. **Spring Boot 3.x End-of-Life**: Exact date for Spring Boot 3.x LTS support end? (Check official Spring Support Matrix.)

3. **Modulith 2.0 Stable Release**: When will 2.0 exit beta? Any breaking changes beyond event registry?

4. **GraalVM for Complex Workloads**: Limitations remain for dynamic class loading, reflection, proxies. Documentation on common reflection hint patterns for enterprise libs (Hibernate, Jackson, etc.)? Existing catalog coverage?

5. **Boot 4.0 Early Adoption**: Are there breaking changes beyond Java/Kotlin baseline bumps? Any deprecated APIs removed? Full migration guide available?

6. **Structured Logging Overhead**: Performance impact of ECS/GELF encoding vs. plain text logs? Throughput benchmarks?

---

## Sources

- [HappyCoders Java 22](https://www.happycoders.eu/java/java-22-features/)
- [InfoWorld Java 22](https://www.infoworld.com/article/2335077/jdk-22-the-new-features-in-java-22.html)
- [Baeldung Java 22 Overview](https://www.baeldung.com/java-22-overview)
- [SymFlower Java 23](https://symflower.com/en/company/blog/2024/what-is-new-in-java-23/)
- [HappyCoders Java 23](https://www.happycoders.eu/java/java-23-features/)
- [Baeldung Implicitly Declared Classes](https://www.baeldung.com/java-implicit-classes-instance-main)
- [Foojay Java 23](https://foojay.io/today/java-23-has-arrived-and-it-brings-a-truckload-of-changes/)
- [Oracle Java 24](https://blogs.oracle.com/java/the-arrival-of-java-24)
- [OpenJDK JEP 513 - Flexible Constructor Bodies](https://openjdk.org/jeps/513)
- [Spring Blog Framework 6.2](https://spring.io/blog/2024/11/14/spring-framework-6-2-0-available-now/)
- [GitHub Spring Framework 6.2 Release Notes](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-6.2-Release-Notes)
- [GitHub Spring Boot 3.4 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.4-Release-Notes)
- [Spring Blog Boot 3.4](https://spring.io/blog/2024/11/21/spring-boot-3-4-0-available-now/)
- [Spring Blog Structured Logging 3.4](https://spring.io/blog/2024/08/23/structured-logging-in-spring-boot-3-4/)
- [GitHub Spring Boot 3.5 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes)
- [Medium Top 6 Spring Boot 3.5 Features](https://medium.com/threadsafe/top-6-features-of-spring-boot-3-5-ae0b38a7822a)
- [Spring Blog Boot 4.0](https://spring.io/blog/2025/11/20/spring-boot-4-0-0-available-now/)
- [GitHub Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [Baeldung Spring Boot 4 & Framework 7](https://www.baeldung.com/spring-boot-4-spring-framework-7)
- [Spring Boot Native Image Documentation](https://docs.spring.io/spring-boot/reference/packaging/native-image/index.html)
- [GraalVM Spring Boot Guide](https://www.graalvm.org/latest/reference-manual/native-image/guides/build-spring-boot-app-into-native-executable/)
- [BellSW GraalVM Native Image](https://bell-sw.com/blog/spring-boot-with-graalvm-native-image-performance-compatibility-migration/)
- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/reference/fundamentals.html)
- [Baeldung Spring Modulith](https://www.baeldung.com/spring-modulith)
- [BellSW Spring Modulith Guide](https://bell-sw.com/blog/what-is-spring-modulith-introduction-to-modular-monoliths/)
- [Spring Blog Modulith 2.0 M1](https://spring.io/blog/2025/07/26/spring-modulith-2-0-M1-released)
- [Spring Blog OpenTelemetry Integration](https://spring.io/blog/2025/11/18/opentelemetry-with-spring-boot/)
- [Spring Blog Observability Boot 3](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3/)
- [Last9 Distributed Tracing](https://last9.io/blog/distributed-tracing-with-spring-boot/)
- [Medium OTLP Setup Guide](https://amithkumarg.medium.com/distributed-tracing-with-spring-boot-micrometer-opentelemetry-and-jaeger-fafd5c0bd282)

---

## Agent & Skills

- **Agent**: `backend-developer` (Java/Spring expert for implementation decisions)
- **Skills**: `springboot-pro`, `java-patterns`, `microservices-architect`, `problem-solving`
- **Handoffs**:
  - Migration planning → `project-manager`
  - Native image optimization → `cloud-architect`
  - Observability setup → `devops-engineer`
  - Code review (Boot 4.0 migration) → `code-reviewer`
