# Executor brief (eval run)

Execute a user task as Claude Code would.
- Task: the user's message is the full text of `<case>/task-prompt.txt` (path given in your prompt). Read it and do what it asks.
- Working directory (the user's repo): the `outputs/` folder given in your prompt. Input files are already there. Read and write only inside it, with absolute paths. Do not read any other file in the parent folders (they hold grading data). Never open any `evals/` folder.
- Toolchain: dotnet on PATH (/opt/homebrew/bin/dotnet). pytest is not on PATH: use `uv run --offline --no-project --with pytest python3 -m pytest ...` from the outputs folder. For Node projects you may run `npm install --prefer-offline --no-audit --no-fund` once if node_modules is missing; add no new packages.
- No user can answer mid-task: if you would ask the user something, put the question in your final reply and continue with sensible defaults.
- When done, delete build artefacts you created (bin, obj, node_modules, __pycache__, .pytest_cache) inside outputs/.
- Finish by writing your final reply to the user, verbatim, to `outputs/response.md`. Then report back in one line.
