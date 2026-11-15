---
type: llm
focus: trace
weight: 1
---

The edge cases include all four of: merging a tag into itself, a source tag that does not exist, an asset that already carries both the source and the target tag, and every rendition being wider than maxWidth; each is given a concrete expected behaviour (a return value, an exception type or a no-op), and none is left as 'per A<n>', 'TBD' or 'decide after reading the code' without a chosen default.
