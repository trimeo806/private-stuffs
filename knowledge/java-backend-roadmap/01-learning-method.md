# Phase 1: Evidence-Driven Backend Learning

## Purpose

Turn existing JavaScript, current Java, and MongoDB habits into a repeatable learning system: predict, inspect, build, perturb, explain, and revisit. The goal is better engineering on current work from the first week while building evidence that transfers to Spring, PostgreSQL, reliability, and operations.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Philosophy and Design Rationale](#philosophy-and-design-rationale)
- [Current-Job Baseline](#current-job-baseline)
- [Transfer Map](#transfer-map)
- [The Evidence Loop](#the-evidence-loop)
- [Experiment and Debugging Techniques](#experiment-and-debugging-techniques)
- [Recall, Code Reading, and Review](#recall-code-reading-and-review)
- [Trackr Evidence System](#trackr-evidence-system)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [AI-Assisted Workflow](#ai-assisted-workflow)
- [Common Pitfalls](#common-pitfalls)
- [Hands-On Exercises](#hands-on-exercises)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

Backend knowledge becomes useful when it predicts observable behavior under valid input, bad input, concurrency, dependency slowness, and change. Familiarity with Java annotations or MongoDB queries is a starting point, not proof. This phase establishes a method for turning real tickets, unfamiliar code, incidents, and roadmap exercises into corrected mental models.

Trackr is the shared laboratory. Every later phase leaves a small evidence packet so that progress is measured by behavior you can explain, not pages read or code generated.

## Agent & Skills

- **Agent:** `researcher`
- **Skills:** `research`, `problem-solving`, `knowledge-retrieval`
- **Handoff:** give the Phase 2 `developer` and `debugger` a current-job convention inventory, a JavaScript/Java/Mongo transfer map, the evidence template, and one corrected misconception supported by an observation.

## Philosophy and Design Rationale

Use prior experience as a source of hypotheses, not as a template. JavaScript makes asynchronous boundaries familiar but can hide shared-memory hazards. Existing Java work makes framework code recognizable but can encourage annotation-level explanations. MongoDB teaches access-pattern modeling but can underexpose relational constraints and joins.

A claim is learned at the required level when you can:

- predict a novel case before running it;
- identify the mechanism and relevant boundary;
- produce evidence that could disprove the prediction;
- explain the failure mode and trade-off;
- apply the corrected model to Trackr after a delay.

Prefer narrow experiments and end-to-end slices in alternation. A small probe isolates causality; a Trackr slice proves that the model survives integration.

## Current-Job Baseline

Before adopting roadmap defaults, inspect the repository and team guidance you actually use. Record:

| Area | Questions to answer | Evidence |
|---|---|---|
| Runtime | Which JDK and vendor? Which support window? | Build configuration and CI/runtime declaration |
| Spring | Which Boot line and starters? How are upgrades approved? | Dependency management and service template |
| Build | Maven Wrapper, Gradle Wrapper, or company tooling? | Clean build command used by CI |
| Architecture | Capability packages or technical layers? Where are transaction boundaries? | Dependency sketch from one request path |
| HTTP | MVC or WebFlux? Tomcat or another server? | Runtime dependencies and one traced request |
| MongoDB | Driver/Spring Data versions, concerns, timeouts, indexes, backup ownership? | Configuration plus database metadata |
| Delivery | Container/runtime limits, health checks, shutdown budget? | Deployment manifest or platform standard |
| Security | Approved identity, secret, logging, and data-handling practices? | Team policy links without copied secrets |

Company conventions override the roadmap's greenfield choices. Do not “upgrade” a work repository merely to match this curriculum. When practicing outside company constraints, use JDK 25 LTS, the current stable Spring Boot 4.1 service release, and the Maven Wrapper.

## Transfer Map

Rewrite this table as evidence corrects your assumptions.

| Existing habit | Useful transfer | Risky assumption | New question |
|---|---|---|---|
| JavaScript object references | Mutation can be observed through aliases | `==` and `equals` describe the same thing | Which identity/value semantics does this Java type promise? |
| Promises and `AbortSignal` | Async work needs ownership, timeout, and cancellation | Java interruption automatically stops work | Which operation observes interruption, and what cleanup follows? |
| Event-loop concurrency | Interleavings and stale data matter | A Spring singleton is single-thread confined | Is mutable state shared across request threads? |
| Dynamic JSON | External shape requires validation | Missing, `null`, blank, and default are interchangeable | Which boundary distinguishes each state? |
| Java classes and interfaces | Types can express behavior and contracts | Every class needs an interface or inheritance tree | Which boundary benefits from a narrow port? |
| Spring annotations | Cross-cutting behavior can be declarative | Self-invocation or arbitrary construction triggers the proxy | Which object owns the call and transaction? |
| MongoDB documents | Model from access and atomicity needs | Embedding is always faster or references are relational joins | What changes together, grows without bound, and must be atomic? |
| MongoDB indexes | Reads trade against write/storage cost | A plausible index is necessarily used | What does `explain` show with representative data? |

An analogy is temporary scaffolding. Retire it when it hides the Java, JVM, Spring, MongoDB, or PostgreSQL mechanism.

## The Evidence Loop

Use a 60–90 minute loop:

1. **Question:** choose one observable uncertainty.
2. **Prediction:** write expected return value, exception/cause, state change, timing, and cleanup.
3. **Primary reading:** inspect company guidance, official documentation, or source narrowly.
4. **Probe:** create the smallest executable case or Trackr request that distinguishes hypotheses.
5. **Perturb:** change one factor—aliasing, invalid input, timeout, concurrent update, or unavailable dependency.
6. **Observe:** retain the exact input and relevant output, trace, plan, stack, or state.
7. **Explain:** close the source and narrate cause and effect.
8. **Correct:** record what the prior model missed.
9. **Transfer:** change or review the corresponding Trackr boundary.
10. **Recall:** revisit after about one day, one week, and one month.

A green result is not automatically decisive. If both competing explanations predict the same result, improve the experiment.

## Experiment and Debugging Techniques

Good evidence is minimal, discriminating, and reproducible:

- a tiny Java program comparing record equality with mutable-class identity;
- a Spring request showing whether an exception crosses a proxied transaction boundary;
- a timed client/server request separating client timeout from server completion;
- a MongoDB query plan before and after an index;
- a PostgreSQL two-session lock experiment;
- a restore into an isolated target followed by invariant checks.

For meaningful defects, keep a compact journal:

```text
Symptom and impact:
Expected observable behavior:
Smallest reproduction:
Competing hypotheses:
Evidence gathered:
Root cause versus trigger:
Fix and why it works:
Corrected model:
Regression protection or reason not to keep a test:
```

A stack trace is evidence of a path, not automatically the root cause. A timeout is an observation, not proof that the server did not commit. Separate trigger, mechanism, and product impact.

## Recall, Code Reading, and Review

Twice each week, read one bounded path in the current service or an official example:

1. identify input and output contracts;
2. mark thread, transaction, resource, and data ownership;
3. predict exception and cleanup paths;
4. inspect focused tests for promised behavior;
5. trace one framework abstraction to the underlying mechanism;
6. summarize one reusable idea and one repository-specific convention.

Ask reviewers targeted questions:

- Which invariant or failure state is unstated?
- What proves this call is inside the intended transaction or proxy?
- What happens when the dependency is slow after side effects begin?
- Which mutable object escapes its owner?
- Does this query enforce tenant scope as well as retrieve data?
- What is the cheapest experiment that could disprove my model?

A review gate passes when you can restate the concern, reproduce behavioral claims, and justify the chosen change.

## Trackr Evidence System

For each phase, store:

- a one-paragraph behavior specification;
- predictions made before implementation;
- exact requests or commands needed to reproduce the behavior;
- the smallest relevant output, query plan, trace, or screenshot;
- one deliberate failure or boundary case;
- one decision with an alternative and trade-off;
- one corrected misconception, if observation differed;
- a handoff note naming what the next phase may assume.

Do not preserve credentials, production data, giant logs, generated transcripts, or unverifiable screenshots. Redact safely and retain enough context to reproduce locally.

## Decision Rules and Trade-offs

| Situation | Prefer | Trade-off or exception |
|---|---|---|
| Uncertain language/runtime rule | Disposable executable probe | Integration effects remain untested |
| Uncertain product behavior | Thin Trackr vertical slice | More setup may obscure causality |
| Factual contract | Company guidance and primary documentation | Source or experiment may still be needed for ambiguity |
| Important stable invariant | Permanent focused test | Tests add maintenance and runtime cost |
| One-off semantic question | Probe plus journal entry | Do not burden the product suite |
| Architecture/security judgment | Human review with explicit constraints | Reviewer needs context and evidence |
| Broad idea generation | AI-generated alternatives | Every material claim still needs independent verification |
| Limited study time | Reduce breadth | Never skip failure injection or explanation |

## AI-Assisted Workflow

Write your own specification and prediction first. Ask AI for competing hypotheses, counterexamples, likely blind spots, or a review of a bounded diff. Request primary-source pointers, then open and evaluate the sources yourself.

Independently verify by compiling with the configured JDK/build, running the discriminating probe or Trackr path, inspecting actual output and state, and explaining the result without the prompt. Generated code is an untrusted patch: review exception causes, resource lifetime, transactions, SQL/query shape, thread safety, authorization, and data exposure before executing it.

Never paste secrets, production records, internal credentials, or unapproved proprietary code into an AI tool. “The model agreed” and “the code compiled” are not behavioral evidence.

## Common Pitfalls

- Measuring tutorials, notes, or prompts instead of explained behavior.
- Replacing a current company convention with a fashionable greenfield choice.
- Asking AI before forming a hypothesis, then anchoring on its explanation.
- Changing several variables and assigning causality to one.
- Testing only the successful path.
- Treating a framework annotation as a complete explanation.
- Keeping huge logs without the exact input or relevant state.
- Writing permanent tests for implementation details rather than observable contracts.
- Using JavaScript or MongoDB vocabulary after the analogy stops matching.
- Recording a trigger such as “concurrent requests” as the root cause instead of the missing invariant.

## Hands-On Exercises

### Exercise 1: Current-job convention map

Trace one real request from HTTP entry to MongoDB and response. Record runtime/build versions, packages, framework boundaries, validation, exception translation, transaction behavior, timeout configuration, and observability.

**Evidence:** a sanitized dependency/ownership diagram, references to the repository locations that support each claim, and three open questions. Do not change the work repository.

### Exercise 2: Transfer inventory

Choose at least ten concepts from JavaScript, current Java/Spring, and MongoDB. For each, write the useful transfer, risky assumption, and a discriminating experiment relevant to Trackr.

**Evidence:** ten behavior questions rather than vocabulary definitions; at least three must cover failure, concurrency, or cleanup.

### Exercise 3: Correct a prediction

Run one tiny Java or current-service experiment for which two outcomes are plausible. Capture your prediction first, observe the result, minimize any surprise, and correct the model.

**Evidence:** exact input, relevant output, the rejected hypothesis, the mechanism supported by evidence, and a recall prompt.

### Exercise 4: Specify Trackr failure behavior

Before implementation, define observable outcomes for malformed JSON, missing title, cross-project access, concurrent edits, a client timeout after dispatch, and unavailable storage.

**Evidence:** expected status/error/state for each case plus assumptions to revisit in later phases.

## Exit Exercise

Complete one full evidence loop with a small Java program that reads issue-shaped JSON using the same JSON library/version as the company stack or greenfield Spring Boot default. Distinguish malformed JSON, absent title, explicit `null`, blank title, and valid input. Add one mutable-aliasing case that could surprise a JavaScript developer.

Capture the initial predictions, minimal source, execution inputs/results, one mismatch or deliberately falsified hypothesis, corrected explanation, and the Trackr implication. If all predictions were right, alter one boundary until the experiment distinguishes two real models rather than inventing a mistake.

**Exit gate:** the current-job baseline is complete; evidence is reproducible and sanitized; at least one model was tested against a meaningful alternative; you can explain why the observation supports the conclusion; and the Phase 2 handoff contains no unresolved tooling or version assumption.

## Related Documents

- [Roadmap Navigation](README.md)
- [Next: Java and JVM Mental Model](02-java-jvm-mental-model.md)
