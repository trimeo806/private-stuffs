---
tags: [skill-creator]
max_turns: 20
timeout_seconds: 600
allowed_tools: [Read, Glob, Grep, Skill, Write, Edit, Bash]
---

Bug ticket: "Collection overview shows a negative total size for collections larger than 2 GB. CollectionStats.TotalSizeBytes must return the correct total." Asset.SizeBytes is an int (single files are capped below 2 GB by the upload service), and TotalSizeBytes already returns long. Existing tests are TotalSizeBytes_ReturnsZero_ForEmptyCollection and TotalSizeBytes_SumsAllAssetSizes.

Copilot wrote the change below and said "Changed the accumulator to long so large totals no longer wrap, and added a regression test." The agent wrote this, looks fine?

```diff
--- a/src/Dam.Core/Collections/CollectionStats.cs
+++ b/src/Dam.Core/Collections/CollectionStats.cs
@@ -11,7 +11,7 @@ public class CollectionStats
 
     public long TotalSizeBytes()
     {
-        int total = 0;
+        long total = 0;
         foreach (var asset in _assets)
         {
             total += asset.SizeBytes;
--- a/tests/Dam.Core.Tests/Collections/CollectionStatsTests.cs
+++ b/tests/Dam.Core.Tests/Collections/CollectionStatsTests.cs
@@ -19,4 +19,12 @@ public class CollectionStatsTests
 
         Assert.Equal(350L, stats.TotalSizeBytes());
     }
+
+    [Fact]
+    public void TotalSizeBytes_DoesNotOverflow_WhenTotalExceedsIntMaxValue()
+    {
+        var stats = new CollectionStats(new[] { new Asset { SizeBytes = int.MaxValue }, new Asset { SizeBytes = int.MaxValue } });
+
+        Assert.Equal(2L * int.MaxValue, stats.TotalSizeBytes());
+    }
 }
```

For context, the method before the change:

```csharp
public long TotalSizeBytes()
{
    int total = 0;
    foreach (var asset in _assets)
    {
        total += asset.SizeBytes;
    }
    return total;
}
```
