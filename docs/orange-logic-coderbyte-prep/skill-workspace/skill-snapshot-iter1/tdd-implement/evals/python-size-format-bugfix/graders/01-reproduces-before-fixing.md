---
type: llm
focus: trace
weight: 1
---

Before any edit to sizes.py, the trace shows a pytest run in which a test fails because a size at an exact unit boundary is formatted in the smaller unit (such as "1024.0 KB" or "1024 B").
