# Cover letters — draft for review

Notice period used in all letters: 2 months. Salary: negotiable.

---

## 1. Remote People — Full Stack Engineer (APAC)

Dear Remote People Hiring Team,

I'm applying for the Full Stack Engineer (APAC) role. Your stack of React and TypeScript on the front end with Java on the back end is the stack I work in every day, and building infrastructure for borderless teams is a mission I connect with: I already work daily with a Swiss client team across time zones.

At Axon Active, on the Swiss Post ePost platform, I work full-stack on Java 21/Quarkus microservices and Next.js. I built a multi-tenant letter lifecycle with async fan-out, retries and correlation IDs, a scheduled data-retention job with per-tenant failure isolation, and secure third-party integrations with SSRF protection and bounded retries. I also built the feature frontends in Next.js with RTK Query. Before that, I spent nearly three years at DXC building React/TypeScript micro-frontends for a high-traffic banking teller system.

AI tooling is central to how I work: I use Claude, Copilot and MCP-based agents daily, and I designed and own the platform's centralized LLM gateway. I pair that with a test-first habit (JUnit, integration tests, Jest, React Testing Library), which matters when reviewing generated code.

I communicate well asynchronously across time zones and would bring strong ownership to your squads. My notice period is two months.

Best regards,
Nguyen Thanh Tri

---

## 2. Qualifyze — AI Platform Engineer

Dear Qualifyze Hiring Team,

I'm applying for the AI Platform Engineer role. Taking LLM applications from prototype to governed, reliable production is exactly what I have been doing for the past year.

At Axon Active (Swiss Post ePost), I initiated, designed and own a centralized AI gateway microservice: the single, governed path from the platform to the LLM provider. It has versioned server-owned prompts, cost labelling, and one resilience boundary shared by all product modules. I built an async document-processing pipeline (GCS staging, job IDs before any provider call), and applied circuit breakers, bounded workers with deadlines, per-module concurrency limits, kill switches and admission checks before paid provider calls. A per-pod in-flight cap drives Kubernetes HPA autoscaling. New AI agents ship as validated YAML/Markdown bundles with per-tenant canary prompt rollout, documented in ADRs.

I run this on GCP (GKE, Vertex AI, Cloud Build) with Kubernetes manifests, encrypted secrets via SOPS, and a load-testing harness used as the performance exit gate. My backend language is Java/Quarkus rather than Python, and I'm actively extending into RAG and evaluation tooling; I'm confident I can ramp up quickly on your Python services.

Governance and auditability in a regulated domain like pharma quality fit the way I already build. My notice period is two months.

Best regards,
Nguyen Thanh Tri

---

## 3. Katalon — Senior Software Engineer (Java, Python, TypeScript)

Dear Katalon Hiring Team,

I'm applying for the Senior Software Engineer role. Your backend stack (Java with Quarkus, PostgreSQL, Docker and Kubernetes) matches my day-to-day work, and the LLM integration work you describe is where I have spent most of the past year.

At Axon Active (Swiss Post ePost), I build Java 21/Quarkus microservices on GKE. I designed and own the platform's centralized LLM gateway, with cost labelling, circuit breakers, per-module concurrency limits and admission checks to control cost and latency of provider calls. I also built multi-tenant features: async fan-out with retries and correlation IDs, a data-retention job with per-tenant isolation, and secure outbound integrations. I work with PostgreSQL/AlloyDB (partitioned schemas) and MongoDB, with CI/CD on Cloud Build.

On the frontend I have about 4.5 years of React/TypeScript, including WebSocket-based real-time features, which fits Katalon's browser-automation and real-time products. I use AI coding tools daily and care about tests and code review.

My notice period is two months.

Best regards,
Nguyen Thanh Tri

---

## 4. EPOS — Senior Java Software Engineer (Strong AI Integration)

Dear EPOS Hiring Team,

I'm applying for the Senior Java Software Engineer – Strong AI Integration role. Integrating LLMs into Java backend systems in production is the core of my current work.

At Axon Active (Swiss Post ePost), I designed and own a centralized AI gateway microservice in Java 21/Quarkus: the single path from the platform to the LLM provider, with versioned prompts, cost labelling, and a shared resilience boundary (circuit breaker, bounded workers, concurrency limits, kill switches). New AI agents are added as configuration bundles with tool definitions, validated at startup and rolled out per tenant. I also built REST microservices, webhooks and secure third-party API integrations, running on GCP with Docker, Kubernetes and CI/CD.

I work mainly with Quarkus rather than Spring, but the concepts transfer directly and I'm comfortable picking up Spring Boot. I bring about 4.5 years in total, with full-stack depth (React/Next.js) as a bonus.

My notice period is two months.

Best regards,
Nguyen Thanh Tri

---

## 5. EPOS — Senior AI Engineer (LLM, Java, Microservices, API)

Dear EPOS Hiring Team,

I'm applying for the Senior AI Engineer role. Building production LLM systems on a Java microservices platform is what I do now.

At Axon Active (Swiss Post ePost), I initiated the idea for a centralized LLM gateway, wrote the architecture ADR, and built it end to end as owner. It covers versioned server-owned prompts, cost labelling, async document processing, and resilience and backpressure controls that also drive Kubernetes autoscaling. AI agents ship as YAML/Markdown bundles without code changes, with per-tenant canary prompt rollout. I also build my own AI agents and MCP tooling to speed up development.

I'm expanding into RAG and agent frameworks such as LangGraph, and my platform background (GCP, Docker, CI/CD, load testing) fits the distributed-systems side of the role.

My notice period is two months.

Best regards,
Nguyen Thanh Tri
