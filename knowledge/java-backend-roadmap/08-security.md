# Phase 8: Secure Spring Backend Defaults

## Purpose

Protect Trackr’s users, tenant data, servlet endpoints, dependencies, and operations with threat-driven Spring Security controls that are demonstrated against abuse rather than inferred from configuration.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Security Philosophy and Threat Model](#security-philosophy-and-threat-model)
- [Spring Security Servlet Architecture](#spring-security-servlet-architecture)
- [Authentication, Passwords, and Sessions](#authentication-passwords-and-sessions)
- [Authorization and Tenant Isolation](#authorization-and-tenant-isolation)
- [CSRF, CORS, and Browser Boundaries](#csrf-cors-and-browser-boundaries)
- [Input, Errors, and Abuse Controls](#input-errors-and-abuse-controls)
- [Secrets, Dependencies, and SBOMs](#secrets-dependencies-and-sboms)
- [Audit and Verification](#audit-and-verification)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [Common Pitfalls](#common-pitfalls)
- [AI-Assisted Workflow](#ai-assisted-workflow)
- [Hands-On Exercises and Evidence](#hands-on-exercises-and-evidence)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

Security preserves explicit properties under adversarial input. Start from Trackr’s assets, actors, trust boundaries, and tenant invariants; then use Spring Security’s servlet stack as one enforcement layer. A successful login or a protected route never substitutes for authorization inside each project-scoped use case and query.

## Agent & Skills

- **Agents:** `security-auditor`, `developer`
- **Skills:** `security-reviewer`, `code-review`, `test`
- **Handoff:** give Phase 9 the threat model, filter-chain design, session lifecycle, authorization matrix, abuse evidence, safe-error policy, secret inventory, dependency/SBOM reports, audit schema, and residual-risk register.

## Security Philosophy and Threat Model

Map Trackr before selecting controls:

- **assets:** accounts, password hashes, opaque sessions, project data, audit evidence, signing/encryption material;
- **actors:** anonymous visitor, member, project administrator, operator, compromised account, malicious dependency;
- **boundaries:** browser/reverse proxy, proxy/Spring MVC, application/PostgreSQL, CI/artifact registry, application/outbound provider;
- **entry points:** JSON, cookies, headers, query parameters, redirects, uploads, migrations, dependencies, management endpoints;
- **properties:** tenant confidentiality, mutation integrity, availability, accountability, and recoverability.

For each high-risk threat record attack, prevention, detection, recovery, owner, and residual risk. Deny by default, minimize privilege and data, layer controls, and make safe failure observable. Separate environment, runtime, migration, and read-only operational database privileges.

## Spring Security Servlet Architecture

Use the Spring Security servlet stack with an explicit `SecurityFilterChain`. Keep routing/authentication mechanics at the HTTP boundary and business authorization in application services. Configure public routes intentionally; require authentication for everything else; use stable handlers for unauthenticated and denied responses.

Understand the request path: servlet container, security filters, `SecurityContext`, controller, application service, and repository. Filter ordering matters. Do not add a custom authentication filter until standard session authentication cannot express the requirement. Enable method security only where it clarifies defense in depth; scattered annotations must not become the sole policy model.

Use the normal servlet/Spring MVC stack. WebFlux security is a different execution model and is not an upgrade. Adopt it only with a justified end-to-end reactive architecture.

## Authentication, Passwords, and Sessions

Use Spring Security’s `PasswordEncoder` abstraction and a delegating encoded format so algorithm identifiers support controlled upgrades. Choose company parameters first; otherwise use a current adaptive password hash supported by Spring Security, tune its work factor on deployment hardware, and re-encode on successful login when policy changes. Never use plain SHA, reversible encryption, custom salts, or raw password logging.

For Trackr’s browser, prefer a high-entropy opaque server-side session identifier in a `Secure`, `HttpOnly`, appropriately scoped `SameSite` cookie. The cookie contains no user authority. Rotate the session on authentication and privilege changes; enforce idle and absolute expiry; invalidate on logout/password reset/administrative revocation; and prevent session fixation. Use a durable shared session store if multiple replicas must resolve sessions, and store token material defensively according to the chosen Spring session mechanism.

JWT is not the default. It trades simple revocation and current authority for independently verifiable claims, key rotation, issuer/audience validation, claim staleness, and leakage risk. Earn it through a real cross-service requirement, never the word “stateless.”

Return uniform authentication failure behavior. Rate-limit and observe login, reset, and session creation without making account enumeration easier. Recovery tokens are single-use, short-lived, stored safely, and revoked after password change.

## Authorization and Tenant Isolation

Define policy in application vocabulary:

| Action | Member | Project admin | Non-member |
|---|---:|---:|---:|
| Read project issues | Yes | Yes | No |
| Create issue/comment | Yes | Yes | No |
| Assign issue | Policy-defined | Yes | No |
| Change membership | No | Yes | No |
| Delete project | No | Yes | No |

Every use case derives the authenticated principal server-side and checks current membership, role, resource project, and action. Never accept a client-supplied tenant or role as authority. Scope SQL by both `project_id` and resource ID; use database constraints for relationships and versioned writes. A pre-authorized controller call cannot protect a later unscoped repository lookup.

Choose `404` when revealing cross-tenant existence would leak information and apply that rule consistently; otherwise `403` may explain a known-resource denial. Test horizontal access between two ordinary members, not only anonymous versus administrator. Administrative rights remain project-scoped.

## CSRF, CORS, and Browser Boundaries

Cookie authentication means browsers attach credentials automatically, so state-changing requests require CSRF protection. Keep Spring Security CSRF enabled for browser flows. Expose the chosen token safely to the JavaScript client, submit it on mutations, rotate/manage it according to the selected repository, and test missing, invalid, stale, and cross-session tokens. `SameSite` is useful defense in depth, not a universal replacement.

CORS controls which browser origins may read or send cross-origin requests; it is not authentication or authorization. Configure an exact deployment allowlist, allowed methods/headers, and credential behavior. Never combine credentialed access with wildcard origins. Verify preflight and actual requests through the deployed proxy.

Use HTTPS, trusted proxy configuration, narrow cookie domain/path, appropriate security headers, and a client Content Security Policy. Treat forwarded headers as untrusted unless they come through an explicitly trusted proxy.

## Input, Errors, and Abuse Controls

Validate type, size, format, allowed values, and relationships at the request boundary and enforce the same durable invariant in PostgreSQL when applicable. Bound body size, decompression, page size, query complexity, uploads, executor queues, and outbound concurrency. Parameterize values with JdbcClient/named SQL and allowlist dynamic identifiers; never concatenate user values into SQL.

Public errors use a stable envelope and safe codes. Map validation, unauthenticated, denied, not found, conflict, rate-limit, and unavailable cases without returning stack traces, SQL, class names, upstream bodies, credentials, or tenant existence. Internal telemetry records a controlled category, trace/request ID, and useful exception cause with redaction.

Layer abuse controls by account, route, trusted client context, and risk; IP-only limits are both bypassable and unfair to shared networks. Limit login attempts, expensive searches, exports, and LLM requests separately. Define safe recovery and operator visibility.

## Secrets, Dependencies, and SBOMs

Load secrets at the runtime boundary, never from source, image layers, build logs, test reports, or command history. Rotate them and document consumers. Restrict Actuator and diagnostic access; heap dumps and JFR can contain sensitive values.

Use Maven Wrapper with repository and plugin policies from the company build. Pin direct dependencies through the Spring Boot BOM/company platform; minimize overrides. CI should:

1. resolve from approved repositories and verify policy-relevant provenance;
2. scan dependencies for known vulnerabilities with a maintained scanner;
3. generate a CycloneDX or SPDX SBOM for the shipped artifact/image;
4. scan the container and base JRE;
5. retain reports with artifact digest and source revision;
6. triage reachability, exploitability, fix version, owner, and deadline.

A clean scan proves neither safety nor reachability; a finding is not automatically exploitable. Patch the owning dependency or platform rather than hiding findings without a documented risk decision.

## Audit and Verification

Audit login outcomes, session creation/revocation, password/reset changes, membership/role changes, project deletion, sensitive exports, and administrative actions. Record timestamp, actor, action, tenant/target, outcome, request/trace ID, and safe reason category. Keep records append-oriented, access-controlled, retained by policy, and free of passwords, raw cookies, authorization headers, CSRF tokens, and unnecessary issue text.

Verify at three levels: focused policy tests, Spring MVC security tests with real filter behavior, and deployed browser/proxy abuse exercises. A mocked service call does not prove filter ordering, cookie attributes, CSRF, CORS, proxy trust, or SQL scoping.

## Decision Rules and Trade-offs

| Decision | Default | Change when |
|---|---|---|
| Servlet session vs JWT | Opaque revocable session | Independent verification across a boundary is required |
| Framework feature vs custom filter | Spring Security standard mechanism | Requirement cannot be expressed safely and custom lifecycle is justified |
| Controller vs service authorization | Application service plus boundary checks | Never weaken to controller-only |
| `404` vs `403` | Hide cross-tenant existence | Existence is non-sensitive and remediation helps |
| CSRF enabled vs disabled | Enabled for cookie-authenticated mutations | Endpoint uses a non-browser credential that cannot be ambiently attached |
| CORS same-origin vs allowlist | Same-origin when possible | Separate trusted frontend origin is operationally required |
| Dependency override vs managed BOM | Managed company/Boot versions | Verified fix or compatibility need justifies and documents override |

## Common Pitfalls

- Disabling CSRF because the controller returns JSON while still using cookies.
- Treating CORS, a hidden button, URL shape, or possession of an issue ID as authorization.
- Accepting a role or project from request data instead of current server-side membership.
- Applying `@PreAuthorize` broadly while one repository query remains tenant-unscoped.
- Using a fast general-purpose hash or never calibrating/upgrading password hashes.
- Returning different login/reset errors that enumerate accounts.
- Logging raw sessions, passwords, CSRF tokens, stack traces, SQL parameters, or upstream bodies.
- Exposing Actuator, heap dumps, JFR, or `jcmd` attach capability without operational controls.
- Declaring the service secure because dependency and image scans are green.

## AI-Assisted Workflow

Ask AI to enumerate abuse cases, challenge a filter-chain design, generate a role/tenant matrix, or review a bounded diff. Provide no secrets, tokens, production records, private logs, or vulnerability details outside approved systems. Independently verify suggestions against current Spring Security and OWASP guidance, inspect the effective filter behavior, send adversarial requests through the real boundary, inspect repository scoping, and review scan/SBOM output yourself. AI approval is not security evidence.

## Hands-On Exercises and Evidence

1. **Threat model:** draw Trackr’s trust boundaries and rank threats. **Evidence:** prevention, detection, recovery, owner, and residual risk for each high-risk item.
2. **Session lifecycle:** register, authenticate, rotate, expire, log out, and revoke. **Evidence:** an old or stolen token fails after each invalidating event and cookie attributes are observed.
3. **Tenant matrix:** exercise every action as two same-project roles and an unrelated user. **Evidence:** deny-by-default behavior and no cross-project disclosure.
4. **Browser attacks:** omit/forge CSRF tokens, vary Origin, and forge forwarding headers. **Evidence:** safe rejection through the proxy with expected audit/metrics and no sensitive output.
5. **Abuse and errors:** attempt injection, oversized bodies, account enumeration, and login saturation. **Evidence:** bounded, uniform responses and useful redacted telemetry.
6. **Supply chain:** generate an SBOM and triage one sample or real finding. **Evidence:** artifact identity, dependency path, risk decision, and remediation owner.

## Exit Exercise

Threat-model Trackr and implement the Spring Security servlet configuration, adaptive password encoding, opaque session lifecycle, CSRF/CORS policy, and project-scoped authorization for issues, comments, and membership. Demonstrate cross-tenant denial, injection resistance, body limits, session invalidation, CSRF rejection, brute-force control, safe errors, audit events, dependency scanning, and an SBOM for the shipped artifact.

**Exit gate:** filter behavior and application authorization agree; every protected data path is tenant-scoped; old sessions become unusable; browser controls work through the deployed origin/proxy; public responses and telemetry reveal no secrets or tenant details; dependency findings have explicit dispositions; and residual risks remain visible.

## Related Documents

- [Previous: Reliability, Concurrency, and Testing](07-reliability-concurrency-testing.md)
- [Roadmap Navigation](README.md)
- [Next: Delivery and Observability](09-delivery-observability.md)
