---
type: llm
focus: trace
weight: 1
---

Among the new tests, at least one calls the extracted tag-rewriting function directly without a repository, and at least one calls MergeTags through TagService backed by InMemoryAssetRepository and asserts the stored tags; every new test method name describes a behavior and contains no ticket, task, step or spec ID.
