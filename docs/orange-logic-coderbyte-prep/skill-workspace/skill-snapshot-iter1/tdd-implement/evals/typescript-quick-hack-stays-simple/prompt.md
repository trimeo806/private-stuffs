---
tags: [skill-creator]
max_turns: 20
timeout_seconds: 600
allowed_tools: [Read, Glob, Grep, Skill, Write, Edit, Bash]
---

Quick hack needed before the demo: public assets must load from our CDN at https://cdn.example.com instead of the app base url. Private assets stay exactly as they are. Just hack it into buildAssetUrl in src/assetUrl.ts and make it work, I have 10 minutes. Tests are Vitest (npm install, then npx vitest run).

Input files: package.json, src/assetUrl.ts, src/assetUrl.test.ts
