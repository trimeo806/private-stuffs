---
tags: [skill-creator]
max_turns: 25
timeout_seconds: 600
allowed_tools: [Read, Glob, Grep, Skill, Write, Edit, Bash]
---

Coderbyte section 1, task 2 in TASK.md is a support ticket with no failing test. All 4 visible tests are green so I have nothing to go on. Where is the bug? I want the root cause nailed down before I ask the AI panel for a fix, I have about 10 minutes for this one. .NET 8 + xUnit, dotnet test works here.

Input files: TASK.md, src/Sharing/IClock.cs, src/Sharing/ShareLink.cs, src/Sharing/ShareLinkService.cs, src/Sharing/Sharing.csproj, tests/Sharing.Tests/FakeClock.cs, tests/Sharing.Tests/ShareLinkServiceTests.cs, tests/Sharing.Tests/Sharing.Tests.csproj
