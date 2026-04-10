---
phase: 3
title: "Register in skill-index + verify"
effort: 0.5h
depends: [2]
---

# Phase 3 — Register in skill-index + Verify

## Goal

Add `nestjs-pro` to `skill-index.json` and confirm activation triggers work.

## Agent & Skills

- **Agent**: `backend-developer` (or inline)
- **Skills**: —
- **Handoffs**: None — this is the final phase

## Steps

### Step 3a — Check for generator script

```bash
ls D:/projects/private-stuffs/.claude/scripts/generate-skill-index.cjs
```

If it exists, run it:
```bash
node .claude/scripts/generate-skill-index.cjs
```

This reads every `SKILL.md` under `.claude/skills/`, extracts frontmatter, and overwrites `skill-index.json`. The `nestjs-pro` entry will be injected automatically.

**If the script does NOT exist**: manually add the entry to `skill-index.json` following the existing entry format. The entry should include:
```json
{
  "name": "nestjs-pro",
  "description": "Use when building NestJS applications...",
  "category": "backend-development",
  "triggers": "NestJS, @Module, @Controller, @Injectable, nest-cli.json, *.module.ts, *.controller.ts, *.service.ts, ...",
  "path": "nestjs-pro\\SKILL.md"
}
```

### Step 3b — Verification checklist

- [ ] `nestjs-pro` entry exists in `skill-index.json`
- [ ] `path` field is `nestjs-pro\SKILL.md` (matches Windows path separator used by other entries)
- [ ] `triggers` string in the index matches what's in `SKILL.md` frontmatter
- [ ] SKILL.md YAML parses cleanly: `node -e "const fs=require('fs'); const content=fs.readFileSync('.claude/skills/nestjs-pro/SKILL.md','utf8'); console.log('OK')"`
- [ ] All 8 reference files exist in `.claude/skills/nestjs-pro/references/`
- [ ] Total skill count in index header incremented by 1

### Step 3c — Smoke test

Trigger phrase test — if the skill system evaluates trigger strings, verify these phrases activate `nestjs-pro`:
- `"I have a NestJS @Module with circular dependencies"`
- `"My *.module.ts is throwing an error"`
- `"How do I use ValidationPipe with class-validator?"`

### Step 3d — Activate the plan (MANDATORY)

```bash
node .claude/scripts/set-active-plan.cjs plans/260409-1037-nestjs-pro-skill
```

This stamps `status: active` in `plan.md` and registers the plan in session state.

## Validation

- [ ] `skill-index.json` is valid JSON after modification
- [ ] Entry count is correct
- [ ] No duplicate `nestjs-pro` entries
- [ ] Smoke test phrases map to `nestjs-pro`
