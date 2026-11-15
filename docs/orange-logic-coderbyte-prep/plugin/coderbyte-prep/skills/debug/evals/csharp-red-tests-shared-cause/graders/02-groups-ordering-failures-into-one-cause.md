---
type: llm
focus: trace
weight: 1
---

The response attributes GetPage_FirstPage_ReturnsNewestFirst and GetPage_SecondPage_ReturnsNextNewest to one shared cause: the second OrderBy(a => a.Id) in AssetCatalog.GetPage discarding the UploadedAt sort, with ThenBy as the fix direction.
