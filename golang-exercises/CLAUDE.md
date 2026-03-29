# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run an exercise
go run ./01-variables/exercise/

# Run a solution
go run ./01-variables/solution/

# Run with race detector (useful for goroutine exercises)
go run -race ./11-goroutines/exercise/
```

## Structure

14 standalone exercises, each in a numbered folder (`01-variables` through `14-strings-runes`). Every folder has:
- `exercise/main.go` — TODOs for the learner to implement; the commented-out test code in `main()` shows expected behavior
- `solution/main.go` — complete working implementation

Each `main.go` is its own `package main` with no shared code between exercises. The module (`golang-exercises`) has no external dependencies.

## Exercise Conventions

- TODOs are numbered and include hints in comments
- The `main()` function contains commented-out test calls — uncomment them as you implement each TODO
- Some exercises pre-declare types/variables that must be used (e.g., `var counter int` and `var mu sync.Mutex` in exercise 11)
- Exercises 01–10 have only `solution/`; exercises 11–14 have both `exercise/` and `solution/`
