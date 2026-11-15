---
tags: [skill-creator]
max_turns: 20
timeout_seconds: 600
allowed_tools: [Read, Glob, Grep, Skill, Write, Edit, Bash]
---

summarize_by_extension in storage_report.py is done and the one test passes. The spec is in README.md. Harden this before I submit: write the edge case tests the graders would probably run.

Input files: README.md, pyproject.toml, storage_report.py, tests/test_storage_report.py
