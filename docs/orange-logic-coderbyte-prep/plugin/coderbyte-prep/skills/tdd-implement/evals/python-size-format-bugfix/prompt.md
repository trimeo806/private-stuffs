---
tags: [skill-creator]
max_turns: 20
timeout_seconds: 600
allowed_tools: [Read, Glob, Grep, Skill, Write, Edit, Bash]
---

Bug from support: the storage page shows "1024.0 KB" for a file that is exactly 1 MB, and test_formats_one_megabyte in tests/test_sizes.py is red. Fix it, I only have 15 minutes left on this take-home. pytest is set up, run it with python -m pytest.

Input files: README.md, pyproject.toml, sizes.py, storage_report.py, tests/test_sizes.py
