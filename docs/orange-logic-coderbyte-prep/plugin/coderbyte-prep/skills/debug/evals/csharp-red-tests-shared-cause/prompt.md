---
tags: [skill-creator]
max_turns: 25
timeout_seconds: 600
allowed_tools: [Read, Glob, Grep, Skill, Write, Edit, Bash]
---

Coderbyte AI fluency assessment, section 1, task 1 in TASK.md: some visible tests fail, fix the code not the tests. dotnet test shows 4 red out of 8. Before I prompt the AI panel to change anything I want to understand why each one fails, so the fix prompt is precise. .NET 8 + xUnit, dotnet test works here.

Input files: TASK.md, src/Catalog/Asset.cs, src/Catalog/AssetCatalog.cs, src/Catalog/Catalog.csproj, src/Catalog/CatalogSummary.cs, src/Catalog/PagedResult.cs, tests/Catalog.Tests/AssetCatalogTests.cs, tests/Catalog.Tests/Catalog.Tests.csproj
