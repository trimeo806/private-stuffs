---
type: llm
focus: trace
weight: 1
---

The tag-list rewriting logic is in a separate function or static method that takes the tags and returns a new collection without calling the repository, and MergeTags is the only code that calls the repository Save method.
