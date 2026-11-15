# In-IDE Prompts for Reviewing Agent Edits

For agents that cannot load skills (Coderbyte's in-IDE agent, Copilot Chat, Cursor). Fill the angle-bracket slots and paste. Every prompt names concrete inputs and an explicit scope, because a vague prompt ("make it better", "fix the edge cases") invites the agent to rewrite more than it should, and graders read the prompt log.

The two core prompts, self-review and rejection, are in SKILL.md. These cover the other moments in a review.

## Before the agent writes code: protect the tests

Use when the task has a failing test. It removes the agent's easiest wrong path.

```
Make <test name> pass by changing production code only. Do not edit, skip or
delete any test, and do not change public signatures. If you think a test is
wrong, stop and tell me why instead of changing it.
```

## The agent edited a test

```
Rejected: you changed the expected value in <test name> from <old> to <new>,
which makes the test agree with the bug. Revert that test change and fix
<function> so that <input> returns <old expected value>.
```

## Fix applied in the wrong layer

```
Rejected: <fix> changes stored data in <file/method>, so records saved before
this change still fail (<input> gives <actual>). Leave <storage method> as it
was and do the <comparison/normalization> inside <function> only.
```

## Accept with a small follow-up

Use when the core change is right and one gap remains.

```
Accepted the change to <function>. One gap: <input> gives <actual>, expected
<expected>. Change only <line or method> to handle it, and add a test for <input>.
Keep everything else as it is.
```

## Ask the agent to explain before you accept

Use when the diff is larger than the requirement suggests, or touches files you did not expect.

```
Before I accept: for each file you changed, say in one line why the requirement
needs that change. Mark any change that is not strictly required, and do not
edit anything yet.
```

## Ask for the edge-case tests

Use after accepting, to turn the review's breaking inputs into proof.

```
Add tests to <test file>, in its existing style, for: <input 1> -> <expected 1>,
<input 2> -> <expected 2>, <input 3> -> <expected 3>. Name each test after the
behavior it checks. Do not change production code.
```

## Check an API the agent used

Use when a method or overload looks unfamiliar.

```
You used <API>. Confirm it exists in <language/framework version> with this
exact signature, and say what it returns for <edge input>. If you are not sure,
rewrite that line using <known alternative>.
```
