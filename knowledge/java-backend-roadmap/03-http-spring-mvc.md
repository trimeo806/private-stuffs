# Phase 3: HTTP Through Spring MVC

## Purpose

Make the network path and HTTP contract concrete, then build a bounded Trackr service with Spring MVC, embedded Tomcat, Jackson, Bean Validation, deliberate client/server timeouts, and graceful shutdown. Distinguish protocol behavior from framework behavior and cancellation wishes from observed outcomes.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Philosophy and Design Rationale](#philosophy-and-design-rationale)
- [DNS, TCP, TLS, and Proxies](#dns-tcp-tls-and-proxies)
- [HTTP Semantics](#http-semantics)
- [Spring MVC and Tomcat Request Path](#spring-mvc-and-tomcat-request-path)
- [Jackson and Input Boundaries](#jackson-and-input-boundaries)
- [Bean Validation and Domain Validation](#bean-validation-and-domain-validation)
- [Filters, Interceptors, and Advice](#filters-interceptors-and-advice)
- [Outbound HTTP with RestClient](#outbound-http-with-restclient)
- [Timeouts, Disconnects, and Idempotency](#timeouts-disconnects-and-idempotency)
- [MVC, WebFlux, Platform Threads, and Virtual Threads](#mvc-webflux-platform-threads-and-virtual-threads)
- [Graceful Shutdown](#graceful-shutdown)
- [Techniques](#techniques)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [AI-Assisted Workflow](#ai-assisted-workflow)
- [Common Pitfalls](#common-pitfalls)
- [Hands-On Exercises](#hands-on-exercises)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

An endpoint is the end of a chain: name resolution, connection establishment, TLS, proxy handling, HTTP parsing, servlet dispatch, JSON binding, validation, application work, response serialization, buffering, and connection reuse. Each step has independent limits and failure modes. A client timeout does not prove the request never arrived, and a disconnected client does not guarantee server or database work stopped.

This phase first runs Trackr with an in-memory issue port so HTTP behavior remains visible. Persistence arrives after the contract and lifecycle are understood.

## Agent & Skills

- **Agents:** `developer`, `debugger`
- **Skills:** `core`, `debug`, `test`
- **Handoff:** give Phase 4 a running Spring MVC slice, request/response examples, an error matrix, timeout/disconnect observations, and graceful-shutdown evidence.

## Philosophy and Design Rationale

Treat HTTP as a public protocol, not a controller method call. Define semantics before annotations: safety, idempotency, status, representation, caching, conditional requests, and retry ambiguity. Bound untrusted work before parsing or allocation. Translate failures once at the transport boundary.

Use framework defaults only after identifying what they mean for the deployed version and proxy topology. Keep controllers thin enough that a behavior can be exercised without confusing binding, policy, persistence, and response mapping.

## DNS, TCP, TLS, and Proxies

A typical HTTPS request involves:

1. DNS resolving a name, possibly through cached or multiple records;
2. TCP establishing a connection and handling loss/retransmission;
3. TLS authenticating the server and negotiating encryption/protocols;
4. a reverse proxy or load balancer terminating TLS, buffering, routing, and forwarding;
5. Tomcat parsing the HTTP message and assigning servlet work;
6. the application calling storage or another service;
7. the response traveling back, possibly after the client has stopped waiting.

Measure the phases separately when diagnosing latency. Connection reuse avoids repeated handshakes. HTTP/2 multiplexes streams but does not make application work non-blocking or eliminate transport-level loss. A proxy can impose smaller header/body/idle limits than the application.

Trust forwarded host, scheme, and client-IP headers only from configured trusted proxies. Otherwise clients can spoof identity, audit data, secure-link generation, or rate-limit keys. Decide which layer owns TLS, request-size rejection, and idle timeouts; inconsistent layers create confusing failures.

## HTTP Semantics

Choose methods according to observable meaning:

- `GET` is safe and idempotent;
- `POST` is not inherently idempotent; use a scoped idempotency key when clients may retry creation;
- `PUT` replaces a known resource representation and is idempotent;
- `PATCH` applies a defined partial-update format and is not automatically idempotent;
- `DELETE` is idempotent in intended state, although repeated responses may differ.

Use status codes consistently:

- `200 OK` for a returned successful representation;
- `201 Created` with `Location` after creation;
- `204 No Content` only when no representation is returned;
- `400 Bad Request` for malformed syntax or transport shape;
- `401 Unauthorized` when authentication is missing/invalid;
- `403 Forbidden` for authenticated but disallowed access;
- `404 Not Found` when the resource is not exposed to the caller;
- `409 Conflict` for state/version/idempotency conflicts;
- `412 Precondition Failed` for failed HTTP preconditions;
- `415 Unsupported Media Type` for unacceptable request media;
- `422 Unprocessable Content` only if the API deliberately separates semantic validation from malformed shape;
- `429 Too Many Requests` for enforced rate limits;
- `503 Service Unavailable` for temporary inability, optionally with safe retry guidance.

Define response media types, character encoding, and cache behavior. Use ETags or an explicit version field for conditional updates when appropriate. Do not return `200` with a success-shaped body for failures.

## Spring MVC and Tomcat Request Path

For a normal request:

1. Tomcat accepts/parses the connection and assigns work to a request thread.
2. The servlet filter chain runs.
3. Spring's dispatcher selects a controller method through handler mappings.
4. argument resolvers and message converters bind path/query/header/body data;
5. Bean Validation may reject the transport object;
6. the controller calls an application service;
7. the return value or exception is converted to an HTTP response;
8. interceptors and filters complete according to their lifecycle.

Controllers should perform transport work only: bind, invoke a use case, map the result. Do not place database queries, authorization policy, retries, or multi-step transactions in controllers.

Use typed path/query parameters and explicit defaults. Reject unknown sort/filter fields rather than constructing dynamic behavior from unchecked strings. Treat all header and proxy metadata as untrusted until a trusted boundary validates it.

Embedded Tomcat is a managed server with connector/thread/queue/size settings. Understand the platform's outer limits before tuning inner ones. Increasing request threads can increase contention and exhaust database or downstream pools.

## Jackson and Input Boundaries

Use explicit request/response DTOs rather than serializing persistence objects. Records work well for closed transport values when their null and collection behavior is understood.

Decide and verify:

- whether unknown properties are rejected or ignored;
- how absent fields differ from explicit `null`;
- whether blank text is distinct from missing text;
- date/time format, time zone, and precision;
- enum compatibility and unknown values;
- numeric bounds and coercion;
- duplicate keys and trailing content policy;
- maximum body and decompressed size;
- response field stability and accidental data exposure.

Spring's configured `ObjectMapper` is part of the API behavior. Do not create a second mapper casually; modules and features may diverge. Avoid polymorphic deserialization from untrusted type identifiers. Never bind arbitrary client fields directly onto a domain or persistence object.

A parser error should map to a stable client error without exposing class names, paths, SQL, or stack traces.

## Bean Validation and Domain Validation

Bean Validation handles transport constraints such as required fields, lengths, and simple formats. Use `@Valid` at the binding boundary and choose constraints that match the actual contract. Validate nested collections deliberately.

Domain invariants belong in domain/application behavior too, because imports, scheduled work, tests, and future transports can bypass MVC. Database constraints later protect durable cross-request truth. These layers are complementary:

| Layer | Owns | Does not prove |
|---|---|---|
| HTTP binding | Syntactic shape and supported representation | Business authorization or durable uniqueness |
| Bean Validation | DTO field constraints | Concurrent database invariants |
| Domain/application | State transitions and use-case policy | Storage state unchanged by another writer |
| Database | Durable constraints and atomic state | Friendly client messages or all authorization |

Return field errors with stable public names and machine-readable codes. Do not expose implementation property paths if they differ from the API contract.

## Filters, Interceptors, and Advice

Use each extension point for its lifecycle:

- **Servlet filter:** work around the servlet chain, such as correlation context, trusted-forwarding handling, coarse request limits, or security integration.
- **Spring MVC interceptor:** handler-aware cross-cutting behavior such as selected request timing; account for async dispatch if enabled.
- **Argument resolver/converter:** focused transport conversion.
- **`@RestControllerAdvice`:** centralized exception-to-response translation.
- **Application service:** authorization, business invariants, transaction ownership, and orchestration.

Avoid reading the request body in a filter unless a bounded caching wrapper and clear need justify it. Avoid logging full bodies, cookies, authorization headers, or tokens. Correlation IDs aid diagnosis but are not authentication.

Phase 4 establishes the final error advice; this phase should at least prove malformed JSON and validation failures produce stable, non-leaking responses.

## Outbound HTTP with RestClient

Use Spring's `RestClient` for synchronous MVC calls by default, or the JDK `HttpClient` when direct control and company conventions favor it. Construct a configured client once; do not create one per request. The underlying request factory/client controls connection pooling and connect/read behavior, so inspect the actual implementation rather than assuming a `RestClient` method configures every timeout.

For each dependency define:

- base URI and trusted TLS behavior;
- connection acquisition, connect, read/response, and total operation budgets;
- maximum request and response bytes;
- authentication without secret logging;
- accepted status/media types and error-body limits;
- connection-pool bounds and stale/idle behavior;
- retry eligibility, idempotency, backoff, jitter, and remaining deadline;
- metrics/traces with low-cardinality labels.

Close response bodies according to the selected client contract so connections can be reused. Never retry every `IOException`: the server may have committed before the response was lost.

## Timeouts, Disconnects, and Idempotency

A timeout is a local decision to stop waiting. It does not establish remote state. Budget the full path:

```text
proxy budget > application request budget > downstream/database budget + response margin
```

A shorter inner bound leaves time to classify and return failure. Timeouts should include connection acquisition where that wait can dominate. Apply server request, downstream operation, database query/transaction, and queue bounds; one global number does not cover every phase.

Servlet client disconnect behavior is not equivalent to a propagated cancellation token. The server may notice a failed write only after application work completes. Some drivers respond to thread interruption or cancellation; others require explicit timeout APIs. Verify behavior for the actual stack.

For retryable issue creation, store a tenant-scoped idempotency key, request fingerprint, and result atomically with the operation. Reusing the key with an equivalent request returns the original outcome; a different fingerprint conflicts. Even with idempotency, bound retention and protect the key from cross-tenant use.

## MVC, WebFlux, Platform Threads, and Virtual Threads

**Spring MVC** uses the servlet model and is the default for Trackr. It matches blocking JDBC and common Spring Data MongoDB access. Simplicity and ecosystem fit usually outweigh theoretical concurrency advantages.

**WebFlux** is a separate reactive programming model. Choose it only when the complete path—including clients and data access—is non-blocking or streaming, concurrency/load evidence shows a need, and the team can operate Reactor correctly. Wrapping blocking work in reactive types does not create a non-blocking system; it adds scheduler and context complexity.

**Platform threads** are the baseline execution model. They consume more memory at high concurrency but work with familiar tooling and blocking libraries.

**Virtual threads** can support many blocking I/O tasks with simpler sequential code. They improve concurrency/throughput, not dependency latency. They do not increase database connections, remove remote rate limits, or fix shared mutable state. Avoid pooling virtual threads; constrain scarce resources explicitly and measure carrier pinning, memory, and downstream saturation before adoption.

These are separate decisions: MVC can run with platform or virtual threads; WebFlux is not “virtual-thread MVC.” Keep preview concurrency APIs outside the required baseline.

## Graceful Shutdown

On termination Trackr should:

1. become unready so new external traffic stops;
2. let the server reject or drain new work according to the platform;
3. wait within the configured Spring Boot graceful-shutdown phase for in-flight requests;
4. stop accepting background tasks and signal owned executors;
5. let bounded work complete or interrupt/cancel according to policy;
6. close HTTP clients, database clients/pools, and observability exporters;
7. exit before the orchestrator's termination deadline.

Test through the real process and, when available, the real proxy/load balancer. A context-close unit test does not prove traffic draining. Include one request that finishes within the grace period and one that exceeds it. Check whether side effects committed and how the client observed each case.

## Techniques

- Use `curl` or another explicit client to capture status, headers, body, and timing.
- Resolve the same route through direct service access and the actual proxy to expose header and timeout differences.
- Trace one request through filter, dispatcher, controller, service, and response conversion.
- Use a controllable slow dependency rather than arbitrary sleeps hidden in product code.
- Test the configured `ObjectMapper`, not a separately constructed mapper.
- Inspect active request threads, connector queues, client-pool usage, and downstream timing under bounded load.
- Make malformed content, oversized bodies, unsupported media types, and lost responses observable without leaking input.

## Decision Rules and Trade-offs

| Decision | Default | Alternative and cost |
|---|---|---|
| MVC vs WebFlux | Spring MVC | WebFlux only for measured end-to-end reactive/streaming need; higher cognitive cost |
| Platform vs virtual thread | Platform baseline | Virtual threads for measured blocking concurrency; scarce dependencies still need bounds |
| `RestClient` vs JDK client | `RestClient` in Spring MVC | JDK `HttpClient` offers direct API but less Spring integration |
| Filter vs interceptor | Filter for servlet-chain concern | Interceptor when handler metadata matters; async dispatch needs care |
| Bean vs domain validation | Both at their boundaries | One layer alone leaves bypasses or weaker client errors |
| Strict vs tolerant JSON input | Reject unsupported shape for command requests | Tolerance aids compatible evolution but can hide client mistakes |
| Synchronous completion vs async job | Complete in request for bounded work | Async job for work exceeding request budget; adds durable state/polling |
| Retry vs return ambiguity | Retry only transient and idempotent/deduplicated work | Returning failure avoids amplification but caller must reconcile |
| More server threads | Size from latency, concurrency, and downstream capacity | More threads can deepen queues and contention |

## AI-Assisted Workflow

Give AI the exact Spring Boot generation, selected HTTP client implementation, proxy topology, and endpoint contract. Ask for an error matrix, conflicting timeout scenarios, unsafe deserialization assumptions, and cases where a client observes failure after a commit.

Independently verify generated configuration against primary documentation for the exact version, then run the actual server. Inspect effective behavior with real requests, configured Jackson, the real request factory, the proxy when available, and process termination. Reject properties copied from another Boot generation and claims that compilation proves a timeout, disconnect, or graceful-shutdown path.

## Common Pitfalls

- Explaining a request only from the controller inward.
- Assuming client timeout or disconnect cancels server/database work.
- Using unbounded request bodies, decompression, headers, or downstream responses.
- Returning persistence objects and exposing fields accidentally.
- Treating Bean Validation as a domain or database invariant.
- Creating a new `ObjectMapper` or HTTP client for each request.
- Retrying non-idempotent work after ambiguous transport failure.
- Trusting forwarded headers from arbitrary clients.
- Logging authorization headers, cookies, full bodies, or validation secrets.
- Doing application policy in filters/controllers.
- Choosing WebFlux because JavaScript is event-driven.
- Blocking inside WebFlux and assuming the type makes it non-blocking.
- Treating virtual threads as infinite capacity.
- Increasing Tomcat threads while a smaller database pool is saturated.
- Calling context closure a graceful-shutdown proof.

## Hands-On Exercises

### Exercise 1: Request-path timing

Run Trackr directly and through a local reverse proxy. Capture DNS/connection/TLS or connection-reuse timing where applicable, request duration, forwarding headers, and server correlation. Repeat on a reused connection.

**Evidence:** a path diagram, timing table, trusted-header decision, and explanation of what changed under reuse/proxying.

### Exercise 2: Strict input matrix

Implement create-issue binding with a dedicated record and the configured Jackson mapper plus Bean Validation. Exercise valid JSON, malformed JSON, unknown field, duplicate key according to chosen policy, absent/null/blank title, wrong media type, oversized body, and trailing content.

**Evidence:** input/status/body matrix and a note separating binding, DTO validation, and domain validation.

### Exercise 3: Timeout and lost-response observation

Call a controllably slow dependency through the configured `RestClient`. Exercise connection failure, pool acquisition pressure, slow response, client timeout, and a response lost after the server-side mutation begins.

**Evidence:** measured elapsed time, exception/cause, server-side final state, connection reuse observation, and retry/idempotency decision for each case.

### Exercise 4: Execution-model comparison

Run the same blocking Trackr endpoint with the baseline platform-thread configuration and an explicitly enabled virtual-thread experiment if the selected Boot version supports it. Bound the downstream resource identically. Do not rewrite it in WebFlux.

**Evidence:** concurrency, latency, thread/pool observations, downstream saturation, and a decision. Also write why WebFlux would require an end-to-end redesign rather than an annotation change.

### Exercise 5: Graceful termination

Start the actual packaged service, send one short and one over-budget request, initiate termination, and observe readiness, client result, application completion, resource closure, and process exit.

**Evidence:** timestamped lifecycle observations and final side-effect state for both requests.

## Exit Exercise

Build the in-memory Trackr issue API with create, get, list, and update routes on Spring MVC/Tomcat. Use explicit DTOs, Jackson policy, Bean Validation, domain validation, thin controllers, stable non-leaking errors, body limits, cursor-shaped list parameters, a configured reusable outbound `RestClient`, and graceful shutdown.

Exercise through the actual running server:

- valid and malformed requests;
- every supported status category;
- unknown/oversized input;
- one downstream timeout and ambiguous completion;
- concurrent requests sufficient to observe execution bounds;
- connection reuse;
- shutdown with short and over-budget requests.

**Exit gate:** the evidence packet contains the request-path diagram, error/input matrix, effective timeout behavior, final state after ambiguity, thread/pool observations, and real process-shutdown result. You can explain MVC versus WebFlux, platform versus virtual threads, and why servlet disconnect is not automatic end-to-end cancellation.

## Related Documents

- [Previous: Java and JVM Mental Model](02-java-jvm-mental-model.md)
- [Roadmap Navigation](README.md)
- [Next: API Architecture](04-api-architecture.md)
