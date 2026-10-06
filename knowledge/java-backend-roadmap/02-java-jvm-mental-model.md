# Phase 2: Java and JVM Backend Mental Model

## Purpose

Build the Java/JVM model needed to predict backend behavior: reference identity and value equality, immutability, type erasure, exception causes, resource ownership, class loading, JIT warm-up, garbage collection, the Java Memory Model, interruption, and Spring proxy/singleton consequences. This is not a syntax tour.

## Table of Contents

- [Overview](#overview)
- [Agent & Skills](#agent--skills)
- [Philosophy and Design Rationale](#philosophy-and-design-rationale)
- [References, Aliasing, and Equality](#references-aliasing-and-equality)
- [Immutability, Records, and Null Boundaries](#immutability-records-and-null-boundaries)
- [Interfaces and Composition](#interfaces-and-composition)
- [Generics and Erasure](#generics-and-erasure)
- [Exceptions, Causes, and Rollback](#exceptions-causes-and-rollback)
- [Resource and Executor Ownership](#resource-and-executor-ownership)
- [Classpath, Bytecode, JIT, and GC](#classpath-bytecode-jit-and-gc)
- [Java Memory Model and Shared State](#java-memory-model-and-shared-state)
- [Interruption and Cancellation](#interruption-and-cancellation)
- [Spring Singleton and Proxy Awareness](#spring-singleton-and-proxy-awareness)
- [Backend Techniques](#backend-techniques)
- [Decision Rules and Trade-offs](#decision-rules-and-trade-offs)
- [AI-Assisted Workflow](#ai-assisted-workflow)
- [Common Pitfalls](#common-pitfalls)
- [Hands-On Exercises](#hands-on-exercises)
- [Exit Exercise](#exit-exercise)
- [Related Documents](#related-documents)

## Overview

Java source is only the visible layer. A backend request crosses objects that may alias, shared singleton beans, dynamically dispatched interfaces, erased generic types, proxy advice, thread boundaries, managed resources, bytecode, JIT-compiled code, and garbage-collected memory. Misunderstanding any of these boundaries can create data races, lost exception causes, unexpected commits, leaked resources, or misleading performance conclusions.

Learn each mechanism through a small Trackr importer or issue workflow. The target is the ability to explain behavior and ownership, not memorization of keywords.

## Agent & Skills

- **Agents:** `developer`, `debugger`
- **Skills:** `core`, `debug`, `problem-solving`
- **Handoff:** give Phase 3 executable probes and a design note that explains aliasing/equality, exception causes, resource ownership, interruption, the JMM, and where Spring proxies apply.

## Philosophy and Design Rationale

Prefer types and ownership that make invalid or surprising states harder to express. Use immutable values across boundaries, narrow interfaces at genuine seams, explicit exception translation, and bounded lifetimes for resources and executors. Avoid abstraction whose only purpose is making Java resemble JavaScript or a generic enterprise template.

The JVM is optimized dynamically. Correctness must never depend on debug timing, accidental publication, a particular garbage collector, or current JIT decisions. Measure warm systems for performance, inspect retained ownership before tuning GC, and establish happens-before relationships for shared state.

Spring adds useful indirection, not magic. An annotation changes behavior only when the call crosses the corresponding managed boundary.

## References, Aliasing, and Equality

A variable of reference type holds a reference to an object. Assignment and argument passing copy that reference, so callers and callees can observe the same mutable object. Java is pass-by-value; the value copied can be a reference.

```java
var labels = new ArrayList<>(List.of("backend"));
var issue = new CreateIssue("Timeout", labels);
labels.add("urgent"); // issue may now observe the mutation unless it copied
```

Ask three questions:

1. Who owns this object?
2. May anyone mutate it?
3. Must the recipient retain an independent snapshot?

`==` compares primitive values or reference identity. `equals` expresses logical equality when the type defines it. `hashCode` must remain consistent with `equals`; keys placed in a `HashMap` or `HashSet` must not change the state used by either method while stored. Entity identity, value equality, and database identity are separate design decisions.

Arrays and mutable collections expose aliases. `Collections.unmodifiableList` blocks mutation through one view but does not copy the backing list. `List.copyOf` produces an unmodifiable snapshot of elements, but element objects can still be mutable. Defensive copying should match the depth of the invariant.

## Immutability, Records, and Null Boundaries

Records are good candidates for immutable transport values and small domain values because they state components and provide value-oriented equality. Their references can still point to mutable objects, so use a compact constructor to validate and defensively copy collections.

```java
public record CreateIssue(String title, List<String> labels) {
    public CreateIssue {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        labels = List.copyOf(labels);
    }
}
```

Do not force records onto mutable lifecycle entities when state transitions and identity need richer control. Keep request DTO validation separate from domain invariants: transport validation improves client errors; domain construction protects every caller.

Treat `null` as a boundary decision. Distinguish absent JSON, explicit `null`, blank text, and a legitimate empty collection. Use `Optional` mainly as a return type for a possibly absent value. Avoid it in fields, request DTOs, and parameters unless the surrounding framework and team conventions explicitly support that contract.

## Interfaces and Composition

Use interfaces where a consumer needs a stable behavior boundary, not for every class. A plain application service can depend on a narrow port:

```java
public interface IssueStore {
    Issue save(Issue issue);
    Optional<Issue> find(ProjectId projectId, IssueId issueId);
}
```

An in-memory adapter, Mongo adapter, and later PostgreSQL adapter may implement that behavior, but do not design one lowest-common-denominator generic repository across them. Persistence models and capabilities differ. Keep domain policy in plain Java objects; adapters translate transport or storage representations.

Prefer constructor injection. Required dependencies become visible, instances can be immutable, and tests can assemble the real object without a Spring context. Use inheritance only for a true substitutable relationship with stable shared behavior. Composition usually exposes ownership and variability more clearly.

## Generics and Erasure

Generics provide compile-time type safety, but most type arguments are erased from runtime representation. Consequences include:

- `List<String>` and `List<Issue>` share the same raw runtime class;
- code cannot test `instanceof List<String>`;
- unchecked casts can postpone failure until a distant use;
- serialization libraries need preserved type information such as a parameterized type reference;
- arrays and generics have different runtime type behavior;
- bridge methods may appear in bytecode to preserve polymorphism.

Use bounded wildcards at APIs according to intent: producers are often `? extends T`; consumers are often `? super T`. Do not scatter wildcards through domain code when a concrete type is clearer. Avoid generic CRUD services that erase operations such as “close issue” or “add project member.”

Inspect dependency generics and serializer types when JSON unexpectedly becomes maps or loses element types. Compiler warnings about raw types and unchecked casts are design signals, not noise to suppress broadly.

## Exceptions, Causes, and Rollback

Exceptions move control non-locally. Use them for exceptional failure, not every expected alternative. A not-found lookup may be an `Optional` internally; a domain conflict can be a typed unchecked exception when it must abort a use case and be translated at the API boundary.

Preserve the cause:

```java
try {
    return parser.read(input);
} catch (IOException cause) {
    throw new IssueImportException("cannot read issue import", cause);
}
```

Catch only when you can recover, add useful context, translate at an owned boundary, or perform cleanup that structured resource management cannot express. Never log and rethrow at every layer; that creates duplicates without ownership.

Checked exceptions can make a recoverable requirement explicit, but broad checked declarations often leak infrastructure through application APIs. Use them when callers can take a meaningful recovery action. Otherwise prefer a typed unchecked exception with the original cause.

Spring declarative transactions roll back by default for unchecked exceptions and `Error`, but not ordinary checked exceptions. Configure `rollbackFor` deliberately when a checked failure must abort. Do not infer rollback from a method annotation alone: the call must cross the Spring transaction proxy, and exceptions must escape the boundary in a form covered by the rollback rule. Never catch and convert a failure inside the transaction without deciding the commit consequence.

## Resource and Executor Ownership

Use try-with-resources for every `AutoCloseable` whose lifetime fits lexical scope:

```java
try (var input = Files.newInputStream(path);
     var reader = new BufferedReader(new InputStreamReader(input))) {
    return parse(reader);
}
```

Resources close in reverse declaration order. If work and close both fail, the work exception remains primary and close failures are available through `getSuppressed()`. Inspect suppressed exceptions when cleanup matters.

The component that creates an executor owns its shutdown unless ownership is explicitly transferred. Do not create an executor per request. Give every executor a bound, rejection policy, task owner, observability, and shutdown path. Spring-managed executors need the same design reasoning even when lifecycle hooks perform cleanup.

Avoid finalizers and do not rely on GC to close files, sockets, cursors, or pools. Reachability controls memory reclamation, not external resource lifetime.

## Classpath, Bytecode, JIT, and GC

The compiler emits bytecode and records symbolic references. The runtime loads, verifies, links, and initializes classes through class loaders. The classpath and module path determine what definitions are available; Maven dependency mediation determines which transitive version is packaged. Diagnose `ClassNotFoundException`, `NoClassDefFoundError`, `NoSuchMethodError`, and linkage errors by inspecting the packaged artifact and dependency tree, not by adding random versions.

Use the checked-in Maven Wrapper and Spring Boot dependency management. Do not hand-pin transitive versions without an explicit compatibility reason. Do not blend Spring Boot 3 and 4 examples: their Spring Framework, Jakarta, package, and starter assumptions differ.

The JVM interprets and JIT-compiles hot code using runtime profiles. Startup behavior and warmed throughput differ; microbenchmarks need a proper harness and representative conditions. Never “optimize” from one stopwatch loop.

GC reclaims unreachable objects. A memory leak in Java usually means unwanted reachability through caches, static fields, listeners, queues, `ThreadLocal`, or retained graphs. Allocation rate can create pressure even without a leak. Observe heap, allocation, pause, and application latency together; capture a profile or recording before changing collector flags.

## Java Memory Model and Shared State

The Java Memory Model defines when one thread's writes are visible to another. Correct code establishes a **happens-before** relationship through synchronization, locks, volatile access, thread start/join, concurrent utilities, or safe publication of immutable state.

Key distinctions:

- atomicity does not imply visibility, and visibility does not make compound actions atomic;
- `volatile` is useful for independent state publication, not `count++`;
- `synchronized` and locks protect invariants spanning related state;
- atomics fit focused lock-free state transitions, not arbitrary multi-field invariants;
- concurrent collections make their own operations safe, not a multi-step workflow automatically atomic;
- final fields help safe immutable construction but do not deeply freeze referenced objects.

Spring singleton beans are shared across concurrent request threads. Keep them stateless or protect intentional shared mutable state. Local variables are per invocation; referenced shared objects are not magically thread-confined.

A stress test can reveal violations but cannot prove a race absent. Explain the happens-before relationship in addition to showing test output.

## Interruption and Cancellation

Interruption is a cooperative request, not forced task termination. Blocking methods may throw `InterruptedException`; code should stop or propagate it and restore the flag when it cannot rethrow:

```java
try {
    queue.put(issue);
} catch (InterruptedException cause) {
    Thread.currentThread().interrupt();
    throw new ImportInterruptedException("issue import interrupted", cause);
}
```

`Future.cancel(true)` requests interruption. It does not guarantee that arbitrary CPU work, JDBC, MongoDB, or remote systems stop or roll back. Code that catches and discards interruption breaks shutdown and cancellation. Design explicit timeout budgets at each blocking boundary and define whether a side effect may already have happened.

A JavaScript `AbortSignal` is a useful first analogy, but Java interruption is thread state observed by cooperating code. `CompletableFuture` is not a Promise, streams are not asynchronous, and `parallelStream()` uses shared execution resources unsuitable for casual server work.

Platform threads are the baseline. Virtual threads can improve throughput for many blocking I/O tasks; they do not reduce dependency latency, remove connection-pool limits, or make shared state safe. Do not pool virtual threads. If concurrency must be limited, use a semaphore or bounded downstream resource and verify with load evidence.

## Spring Singleton and Proxy Awareness

Most Spring beans are singletons per application context. They are not automatically thread-safe. Never store request IDs, authenticated users, mutable builders, or per-request collections in bean fields.

Proxy-based features such as `@Transactional`, method security, caching, and some async behavior apply when a call crosses the proxy. Common failures include:

- self-invocation bypassing advice;
- constructing the class with `new` outside Spring;
- putting annotations on methods that the selected proxy cannot intercept;
- swallowing the exception before it reaches transaction advice;
- assuming a new thread inherits transaction or security context.

Keep public application-service methods as clear use-case boundaries. Verify proxy behavior with an integration observation against the real database when commit/rollback is the contract. Do not add interfaces solely to force a proxy style.

## Backend Techniques

- Use records and defensive copies for stable request/response values.
- Model domain transitions with named methods rather than public setters.
- Keep adapters responsible for Jackson, Spring, and storage mapping.
- Inspect `mvnw dependency:tree` output when runtime/linkage behavior is surprising.
- Capture a thread dump or Java Flight Recorder view for blocked or saturated execution.
- Use focused executable probes for erasure, equality, suppressed exceptions, and interruption.
- Use an integration scenario for proxy/transaction behavior; a unit test cannot demonstrate Spring advice.
- State resource and executor ownership in code review.

## Decision Rules and Trade-offs

| Decision | Default | Alternative and cost |
|---|---|---|
| Mutable vs immutable boundary value | Immutable record/value with defensive copies | Mutation may reduce copying but expands aliasing and synchronization burden |
| `==` vs `equals` | Identity only when identity is the contract; otherwise defined value equality | Custom equality becomes part of collection and compatibility behavior |
| Interface vs concrete dependency | Interface at a real consumer boundary | Premature interfaces add indirection and mock-heavy tests |
| Checked vs unchecked exception | Checked only for realistic caller recovery; typed unchecked otherwise | Unchecked flow is easier to ignore and affects default rollback |
| Exception vs explicit alternative | Exception for exceptional abort; result/optional for expected absence | Result types make branches explicit but add API surface |
| Lock vs atomic vs immutable | Immutable first; lock for compound invariant; atomic for focused transition | Lock-free code is not automatically simpler or faster |
| Platform vs virtual thread | Platform baseline | Virtual threads need measured concurrency benefit and bounded dependencies |
| GC tuning vs ownership fix | Find retention/allocation cause first | Collector tuning may mask rather than remove application pressure |
| Spring-managed vs plain object | Plain Java domain/application behavior where possible | Managed beans earn lifecycle/proxy/configuration features but add indirection |

## AI-Assisted Workflow

Give AI the exact JDK, Spring Boot generation, and dependency context. Ask it to propose cases that distinguish identity from equality, expose mutable aliases, test rollback rules, or reveal interruption loss. Ask for mechanisms and primary-source locations, not just annotations.

Independently verify with compiler warnings, bytecode/dependency inspection where relevant, a focused executable probe, a real Spring transaction scenario, and observed thread/resource state. Generated code must preserve exception causes, suppressed failures, the interruption flag, defensive copies, and executor shutdown. Reject a claim about proxies, JIT, GC, or the JMM that is supported only by plausible prose.

## Common Pitfalls

- Saying Java is pass-by-reference rather than pass-by-value of references.
- Mutating a list after giving it to an allegedly immutable record.
- Using mutable fields in `equals`/`hashCode` while an object is a hash key.
- Treating an unmodifiable view as a defensive copy.
- Using raw types or unchecked casts to silence erasure-related design problems.
- Dropping the original exception cause or ignoring suppressed close failures.
- Assuming checked exceptions trigger Spring rollback by default.
- Relying on self-invoked `@Transactional`, security, or async methods.
- Storing request state in singleton bean fields.
- Treating `volatile` as a lock or a concurrent collection as a transaction.
- Swallowing `InterruptedException` or clearing the interrupt status.
- Creating unowned executors or using `parallelStream()` in server code.
- Treating virtual threads as unlimited database capacity.
- Tuning GC before identifying allocation or unwanted reachability.
- Benchmarking cold and warm JVM behavior as though they were equivalent.

## Hands-On Exercises

### Exercise 1: Aliasing and equality lab

Build issue values using a mutable label list, an unmodifiable view, and `List.copyOf`. Mutate the source and element objects. Compare record equality, reference identity, and hash-based lookup before and after mutation.

**Evidence:** predictions, observed values, an ownership diagram, and a final rule for Trackr request/domain values.

### Exercise 2: Narrow port and adapters

Create a Spring-free `IssueStore` port used by an import service. Implement in-memory and file-backed adapters without a generic base repository. Use records for import DTOs and a domain type for validated issues.

**Evidence:** dependency diagram, adapter swap at the composition root, and an explanation of which behavior belongs in the port.

### Exercise 3: Causes and transaction rollback

Create typed checked and unchecked failures with preserved causes. In a minimal Spring/database probe, observe committed state when each escapes a proxied `@Transactional` application-service method, then configure the intended checked-exception rollback rule. Include a self-invocation comparison.

**Evidence:** initial predictions, database state after each case, cause chain, and corrected transaction rule. Do not claim unit-only output proves proxy behavior.

### Exercise 4: Resources, interruption, and shared state

Import issues through an owned executor from a closeable input. Interrupt while idle, blocked, and actively parsing. Prove resource closure and interrupt handling. Separately compare an unsafe shared counter/list with a synchronized or atomic design.

**Evidence:** bounded completion, no continuing owned task, observed interrupt state, close evidence, stress results, and a stated happens-before relationship.

### Exercise 5: Runtime diagnosis

Create a deliberate dependency-version mismatch in an isolated probe or inspect a provided failure; identify whether it is compile-time, class-loading, linkage, or initialization failure. Record a short JFR run or thread dump during importer load.

**Evidence:** dependency/classpath explanation and one runtime observation tied to application behavior, not a tuning recommendation.

## Exit Exercise

Build a cancellable Trackr issue importer with records, defensive collection copies, domain validation, a narrow `IssueStore`, preserved exception causes, try-with-resources, and one owned executor. Include valid, malformed, missing/null/blank, duplicate, interrupted, and resource-close-failure scenarios.

Write a design note explaining:

- which references alias and where snapshots are taken;
- why each equality rule is stable;
- what erasure means for JSON collection deserialization;
- why each failure is explicit state, checked, or unchecked;
- how causes and suppressed exceptions remain inspectable;
- who owns and shuts down every resource/thread;
- what establishes happens-before for shared results;
- which behavior would require a Spring proxy and which remains plain Java.

**Exit gate:** all scenarios have reproducible evidence; interruption completes within a stated bound and is not swallowed; resources and the executor close; no mutable request state leaks; exception causes survive; and you can explain the behavior without using JavaScript analogies as the final model.

## Related Documents

- [Previous: Learning Method](01-learning-method.md)
- [Roadmap Navigation](README.md)
- [Next: HTTP and Spring MVC](03-http-spring-mvc.md)
