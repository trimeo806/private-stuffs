# Prompt patterns for the coding agent

Read this when writing the first prompt (step 8) or when the user asks what to send the agent next. In an AI-fluency assessment the prompt log is graded, so each prompt should show that the user set context, scoped the change and stated constraints.

## Anatomy of a scoped prompt

A good step prompt has five parts, in this order. Leave out a part only when it truly does not apply.

1. **Context**: the files to read, named by path. Naming them stops the agent from guessing and from editing files it never read.
2. **One change**: one bug or one function. Several changes in one prompt produce a diff too large to review properly, and a single rejected part forces rejecting the whole.
3. **Constraints**: keep public signatures, no new dependencies, follow the existing style and error handling, do not touch named files (tests, harness, generated code).
4. **Edge cases for this step**: copy the relevant rows from the recon table with their expected behaviour. The agent handles cases it is told about far more reliably than cases it has to infer.
5. **Output request**: ask for the cause or approach in one or two sentences before the diff. That gives the user something to check before reading the code.

## Weak versus scoped

Weak: "Fix the bugs and add the rename feature."

Scoped: "Read Services/FolderService.cs and Models/Folder.cs. Add RenameFolder(Guid folderId, string newName). Names compare case-insensitively among siblings only. Renaming to the current name is a no-op; a sibling with the same name throws InvalidOperationException; a missing folder throws KeyNotFoundException; a blank name throws ArgumentException. Keep the existing public methods unchanged, no new packages, follow the exception style already used in FolderService. Explain your approach in two sentences before the diff."

## Follow-up prompts

The recon makes these easy because the requirement and edge-case IDs already exist.

- **Next step**: "Step 2 of the plan: <change>. Same constraints. Edge cases: <rows>."
- **Reject with a reason**: "Rejected: <what is wrong> because <consequence, e.g. breaks the empty-input case R3>. Instead, <the direction>." Stating the reason is the signal graders look for.
- **Partial accept**: "Keeping the change to <file>, reverting <file> because <reason>. Now <next instruction>."
- **Tests**: "Add tests for these rows of the edge-case table: <rows>. Use the existing test framework and naming style; name each test after the behaviour it checks."
- **Explain before accepting**: "Before I accept: why does <line or choice> handle <edge case>?"
- **Write-up**: "Draft README notes: approach, assumptions A1 to An with the chosen defaults, what was cut and why, known limits."

## Prompts to avoid

- "Do the whole task": one huge diff, nothing reviewable, nothing in the log that shows judgement.
- "Make the tests pass": invites the agent to edit the tests or special-case test inputs.
- "Improve the code": unscoped, changes untouched behaviour that hidden tests may rely on.
