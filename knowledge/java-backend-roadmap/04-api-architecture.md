# Phase 4: Contract-First API Architecture

## Purpose

Turn Trackr's Spring MVC endpoints into a stable public contract and a capability-packaged modular monolith. Keep controllers thin, application use cases explicit, domain policy framework-light, and persistence behind narrow ports so the MongoDB implementation can later be replaced cleanly by PostgreSQL.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Philosophy and Design Rationale](#philosophy-and-design-rationale)
- [Trackr Requirements and Invariants](#trackr-requirements-and-invariants)
- [Contract-First HTTP Design](#contract-first-http-design)
- [OpenAPI as a Reviewed Contract](#openapi-as-a-reviewed-contract)
- [Capability Packages and Dependency Rules](#capability-packages-and-dependency-rules)
- [Controllers and Transport Mapping](#controllers-and-transport-mapping)
- [Application Services, Ports, and Adapters](#application-services-ports-and-adapters)
- [Constructor Injection and Composition](#constructor-injection-and-composition)
- [Error Translation with Controller Advice](#error-translation-with-controller-advice)
- [Configuration Properties and Secrets](#configuration-properties-and-secrets)
- [Transactions, Idempotency, and Concurrency](#transactions-idempotency-and-concurrency)
- [Techniques and Architecture Evidence](#techniques-and-architecture-evidence)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [AI-Assisted Workflow](#ai-assisted-workflow)
- [Common Pitfalls](#common-pitfalls)
- [Hands-On Exercises](#hands-on-exercises)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

An API is a long-lived agreement between clients and a service. Architecture exists to protect that agreement and the product invariants behind it. A controller/service/repository naming convention is not an architecture if every package can reach every implementation and the same mutable object crosses HTTP, domain, and storage.

Design Trackr outside-in: user journeys and invariants, then OpenAPI, then application use cases and ports, then adapters. Keep one deployable Spring Boot application. Package by capability so `issues`, `projects`, and `auth` own their behavior and publish narrow entry points.

## Agent & Skills

- **Agents:** `backend-architect`, `developer`
- **Skills:** `plan`, `core`, `code-review`
- **Handoff:** give Phase 5 the reviewed OpenAPI document, dependency map, error catalog, use-case/port contracts, configuration schema, and a working in-memory adapter that MongoDB must replace without changing public behavior.

## Philosophy and Design Rationale

Architecture should make the important change cheap and the dangerous dependency visible. Trackr intentionally changes persistence in Phases 5–6; that earns a narrow persistence boundary. It does not earn a universal storage abstraction or parallel data models pretending MongoDB and PostgreSQL have identical capabilities.

Rules:

- public behavior comes from user journeys and invariants, not current tables/documents;
- dependencies point from adapters toward application/domain policy;
- application services own use-case orchestration and transaction intent;
- controllers own transport mapping only;
- domain code does not depend on Spring MVC, Jackson, MongoDB, or JDBC types;
- configuration is typed, validated, externalized, and safe to inspect without secrets;
- compatible evolution is preferred to version proliferation;
- module boundaries are proven, not inferred from directory names.

## Trackr Requirements and Invariants

Write rules before routes or storage shapes. At minimum:

- only authenticated members can see a project;
- only authorized project roles can create, edit, close, or label issues;
- issue numbers are unique within a project;
- a title is required and bounded;
- state transitions are explicit; closing an already closed issue has defined behavior;
- an edit based on a stale version conflicts rather than silently overwriting;
- project membership and tenant scope apply inside every use case and query;
- idempotency keys cannot be reused across tenants or for a different request;
- audit facts required for security are appended with the state change they describe where feasible;
- deleting or deactivating related data has a documented policy.

For each invariant, record three owners:

| Owner | Responsibility |
|---|---|
| API contract | Observable request, response, and error semantics |
| Application/domain | Authorization, transition, orchestration, and conflict policy |
| Persistence | Atomicity, durable uniqueness, and conditional writes supported by the store |

Do not rely on controller validation for a use case that can be called by imports, scheduled work, or another adapter.

## Contract-First HTTP Design

Model resources and relationships:

```text
POST   /projects
GET    /projects/{projectId}
POST   /projects/{projectId}/issues
GET    /projects/{projectId}/issues?status=&label=&after=&limit=
GET    /projects/{projectId}/issues/{issueId}
PATCH  /projects/{projectId}/issues/{issueId}
POST   /projects/{projectId}/issues/{issueId}/comments
```

Define before implementation:

- path and method semantics;
- authentication and project authorization;
- request/response media types and examples;
- required/nullable fields and bounds;
- stable identifiers and timestamps;
- status and error code for each failure;
- idempotency and conditional update behavior;
- cursor ordering and continuation rules;
- compatibility policy for fields/enums;
- maximum list size and request body size.

Avoid action-heavy routes where a state-oriented resource or transition is clearer. A close transition may be a constrained patch or a named subresource; choose one contract and define repeated behavior.

Use cursor pagination for issue feeds that change concurrently. Order by a stable tuple such as `updatedAt DESC, id DESC`; the cursor encodes the last tuple and query filters strictly after it. Treat cursors as opaque, validated, versionable input. Never expose a cursor that can bypass tenant filters.

## OpenAPI as a Reviewed Contract

Keep a reviewed OpenAPI document in the repository. It should describe:

- operations, parameters, security requirements, and media types;
- closed request/response schemas where appropriate;
- formats, bounds, examples, and required/nullability semantics;
- reusable error envelope and stable error codes;
- pagination metadata and opaque cursor;
- idempotency/precondition headers;
- relevant success and error responses.

Choose one authority: design-first OpenAPI with implementation conformance, or code-first generation with a reviewed committed artifact. Do not manually maintain two truths. Generated server interfaces are optional; use them only when they reduce drift without pushing generator types into application/domain code.

Contract checks should compare observable runtime responses, not simply prove a generator ran. Review compatibility when removing/renaming fields, tightening validation, changing defaults, adding required properties, or changing enum behavior.

## Capability Packages and Dependency Rules

A practical layout:

```text
com.example.trackr
  auth/
    domain/
    application/
    inbound/http/
    outbound/
  projects/
    domain/
    application/
    inbound/http/
    outbound/
  issues/
    domain/
    application/
    inbound/http/
    outbound/
  platform/
    configuration/
    http/
    observability/
  TrackrApplication.java
```

Use only the subpackages a capability actually needs. A small capability can stay flatter. Avoid global `controller`, `service`, `repository`, `dto`, and `model` buckets: they encourage cross-capability reach and name collisions.

Dependency direction:

```text
HTTP controller -> application service -> domain policy
                                     -> required port
Mongo/JDBC adapter ------------------> required port
Spring configuration ----------------> concrete assembly
```

An outbound adapter depends on the port it implements. The application service does not import `MongoTemplate`, `JdbcClient`, `ResponseEntity`, or Jackson annotations. Cross-capability calls use an explicit published application interface or event policy, not direct repository access.

Prove boundaries with package visibility, focused architecture checks if the project already uses them, and dependency review. Do not add a framework solely to draw modules.

## Controllers and Transport Mapping

A controller should:

1. bind validated transport input;
2. obtain authenticated actor/context through the approved security boundary;
3. create an application command/query;
4. invoke one use case;
5. map the application result to the public response.

It should not contain storage queries, authorization rules, retry loops, transaction choreography, or exception stacks. Keep HTTP DTOs distinct from domain and persistence records when their responsibilities differ. Explicit mapping is often cheaper than accidental coupling.

Do not trust a client-supplied user or tenant identifier as the actor. Route project identifiers scope the target but do not prove membership. The application service rechecks authorization at the use-case boundary, and the persistence query includes tenant/project scope where omission could expose data.

## Application Services, Ports, and Adapters

Name application methods after use cases:

```java
public final class UpdateIssue {
    private final IssueStore issues;
    private final ProjectAccess access;
    private final Clock clock;

    public IssueView handle(Actor actor, UpdateIssueCommand command) {
        // authorize, load, transition, persist conditionally, map result
    }
}
```

A port exposes behavior the application needs, such as conditional issue save, not every operation a database supports. Return domain/application values and typed outcomes, not driver documents, rows, or generic maps.

Adapters own:

- conversion between domain and storage/transport representation;
- database-specific query and atomic-update features;
- driver exception classification without exposing secrets;
- metrics around the boundary;
- resource conventions required by the implementation.

Do not force MongoDB and PostgreSQL adapters to coexist. Phase 5 replaces the in-memory adapter. Phase 6 replaces MongoDB, migrates data, switches configuration, and removes MongoDB runtime code.

## Constructor Injection and Composition

Use constructor injection for required dependencies. Prefer immutable fields and reject incomplete construction. Avoid field injection and service-locator access to the application context.

Plain application/domain classes can be assembled in Spring configuration methods; they need not carry stereotypes. Adapters and configuration are naturally framework-aware. This separation allows fast plain-Java tests for policy while leaving proxy-dependent behavior to Spring integration evidence.

Keep one visible composition root. Avoid scattered conditional beans that silently choose persistence. During the Phase 6 cutover, one configured `IssueStore` must be active, and the final graph must contain only PostgreSQL persistence.

Circular dependencies indicate confused ownership. Resolve the capability/use-case boundary rather than enabling circular references or lazy injection.

## Error Translation with Controller Advice

Define a stable envelope such as:

```json
{
  "code": "issue_version_conflict",
  "message": "The issue changed; reload and retry.",
  "requestId": "public-correlation-id",
  "fieldErrors": []
}
```

A `@RestControllerAdvice` translates known transport, application, and dependency categories once. Preserve causes internally for diagnosis, but expose only safe, actionable information. Map:

- malformed/binding failures to the selected client-error status;
- validation failures to stable field codes;
- unauthenticated/forbidden/not-exposed outcomes consistently;
- missing resources to not found;
- uniqueness/version/idempotency conflicts to conflict or precondition failure;
- temporary dependency unavailability to a safe service failure;
- unknown defects to a generic internal error with correlation, never a stack trace.

Do not classify errors by message text. Do not return a storage exception, SQL state, collection name, class name, or internal path to clients. Log once at the boundary that owns the operational event, with redaction and no duplicate stack at every layer.

## Configuration Properties and Secrets

Use typed `@ConfigurationProperties` for related settings: public URL, timeouts, limits, persistence connection/pool behavior, and feature policy. Validate required values and semantic relationships at startup. Use duration/data-size types rather than ambiguous integers.

Rules:

- external configuration supplies environment-specific values;
- secrets come from the approved secret mechanism, never committed files or defaults;
- defaults are safe for local use and explicit for production-sensitive behavior;
- configuration classes contain no business policy;
- startup diagnostics reveal effective non-secret settings and reject invalid combinations;
- test overrides use the same property contract.

Avoid scattered `@Value` strings, silent fallback credentials, and environment-name conditionals throughout application code.

## Transactions, Idempotency, and Concurrency

An application service owns the complete atomic use case. Persistence adapters implement the store-specific mechanism. Do not begin/commit transactions inside unrelated repository calls that make a multi-step use case appear atomic when it is not.

In the in-memory phase, simulate conditional version updates so the public conflict contract exists before storage. Phase 5 will use MongoDB's single-document atomic update or a justified short transaction. Phase 6 will use a proxied `@Transactional` PostgreSQL use-case boundary plus constraints/locking as needed.

Idempotency is product state, not middleware caching. Scope keys to actor/tenant and operation, bind them to a request fingerprint, store the final identity/outcome atomically, and define retention. A duplicate key for different input is a conflict.

Never hold a database transaction open across an outbound network call unless a deliberately analyzed consistency protocol demands it. Prefer ordering, idempotency, or an outbox for later side effects.

## Techniques and Architecture Evidence

- Begin with five user journeys and an invariant catalog.
- Draw dependencies for one write and one list query.
- Maintain an error/status matrix beside OpenAPI review.
- Use package-private implementation details where possible.
- Exercise application services without MVC for domain/authorization cases.
- Exercise MVC for public binding/status/body behavior.
- Review the runtime bean graph for a single adapter implementation.
- Use mutation scenarios—stale version, repeated idempotency key, cross-project ID—to challenge the boundary.
- Record alternatives rejected and the condition that would reverse the decision.

## Decision Rules and Trade-offs

| Decision | Default | Alternative and cost |
|---|---|---|
| Contract-first vs implementation-first | Specify journeys/OpenAPI first | Code-first can move quickly but needs committed reviewed output and drift checks |
| Capability vs technical packages | Capability packages | Technical layers simplify initial navigation but encourage cross-domain coupling |
| Modular monolith vs services | One deployable modular monolith | Services add network, consistency, delivery, and observability costs |
| Constructor vs field injection | Constructor injection | Field injection hides incomplete construction and complicates plain tests |
| Plain class vs Spring stereotype | Plain domain/application class when practical | Stereotype provides scanning/proxy features but couples to container behavior |
| Narrow port vs generic repository | Use-case-shaped port | Generic CRUD reduces declarations but erases domain/storage semantics |
| Explicit mapper vs shared model | Separate types at responsibility boundaries | Shared type reduces code but couples public/domain/storage evolution |
| Advice vs controller catches | Central advice for translation | Local catches only for endpoint-specific recovery |
| Cursor vs offset pagination | Stable cursor for changing feeds | Offset is simpler for small admin lists but drifts and slows at depth |
| Compatible change vs version | Additive compatible evolution | Version only for genuinely incompatible semantic change |

## AI-Assisted Workflow

Give AI the user journeys, invariant catalog, OpenAPI diff, dependency rules, and exact Spring generation. Ask it to find ambiguous semantics, missing status cases, authorization gaps, unstable pagination, leaky types, proxy assumptions, and persistence details that escaped into ports.

Independently verify by reviewing the rendered contract, sending real requests, tracing dependencies from each adapter, inspecting the Spring bean graph, and exercising stale/cross-project/idempotency cases. Generated architecture diagrams are hypotheses. Reject extra layers, generic repositories, annotations, and patterns that do not protect a named change or invariant.

## Common Pitfalls

- Designing routes from collection/table names rather than user behavior.
- Maintaining OpenAPI and implementation as two unverified truths.
- Putting authorization or transaction ownership in controllers.
- Using persistence objects as HTTP and domain types.
- Organizing the whole service into global technical-layer packages.
- Defining an interface for every class or one generic repository for every aggregate.
- Letting one capability access another capability's repository.
- Assuming `@Transactional` works on self-invocation or arbitrary objects.
- Catching an exception inside a transaction without deciding rollback.
- Returning exception messages or stack traces to clients.
- Scattering raw configuration strings and defaults.
- Trusting route tenant/user IDs instead of authenticated context.
- Adding microservices, messaging, or module frameworks before a measured need.

## Hands-On Exercises

### Exercise 1: Journey and invariant review

Write five Trackr journeys: create project, invite member, create issue, update with an expected version, and list issues by filter/cursor. Identify authorization, atomicity, uniqueness, and compatibility rules before drawing routes.

**Evidence:** each invariant has API, application, and persistence ownership plus a failure response.

### Exercise 2: OpenAPI and error matrix

Specify the core project/issue routes, DTO schemas, bounds, statuses, error envelope, cursor, idempotency, and version precondition. Compare it with the running Phase 3 behavior.

**Evidence:** reviewed contract plus requests showing conformance for success, malformed input, validation, not-found/not-exposed, forbidden, stale version, and duplicate idempotency key.

### Exercise 3: Capability refactor

Move Trackr into `auth`, `projects`, `issues`, and narrowly shared `platform` capabilities. Implement controller → application service → port → in-memory adapter flow with constructor injection.

**Evidence:** dependency diagram and a review showing domain/application code imports no MVC/Jackson/storage types and only one adapter is assembled.

### Exercise 4: Advice and configuration

Implement typed configuration for request limits, public URL, timeouts, and one adapter setting. Reject invalid startup configuration. Centralize safe exception translation in `@RestControllerAdvice`.

**Evidence:** startup failure for one invalid combination and an error matrix whose responses contain stable codes but no internal exception detail.

### Exercise 5: Architecture pressure test

Add a stale update, cross-project lookup, and repeated idempotent create. Then sketch how MongoDB and PostgreSQL would each implement the required port without reducing it to generic CRUD.

**Evidence:** observable outcomes, application-owned policy, and database-specific adapter notes. No second runtime adapter is added yet.

## Exit Exercise

Produce a reviewed OpenAPI contract and implement Trackr's create project, create issue, get issue, list issues, and versioned update flows through thin Spring MVC controllers, plain application services, narrow ports, and one in-memory adapter. Use capability packages, constructor injection, typed validated configuration, stable `@RestControllerAdvice`, tenant-safe authorization context, cursor semantics, and an idempotency contract.

**Exit gate:** runtime requests match the OpenAPI success/error matrix; stale and cross-project operations fail safely; dependency evidence shows transport and storage do not leak into domain/application code; exactly one adapter is wired; invalid configuration fails at startup; and the Phase 5 handoff names every persistence behavior MongoDB must prove.

## Related Documents

- [Previous: HTTP and Spring MVC](03-http-spring-mvc.md)
- [Roadmap Navigation](README.md)
- [Next: MongoDB in Production](05-mongodb-production.md)
