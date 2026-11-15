# Question bank for free-form answers

Read this when the user wants to prepare Section 2 answers before seeing the questions, or when a pasted question is close to one below and you want the angle a grader is looking for. Each entry gives what the grader is checking, which part of the notes to pull from, and a skeleton in the example, action, reason, result pattern. Keep every answer 60 to 120 words and fill the skeleton only with events from the notes.

## 1. How did you use AI in this task? Which prompts worked best and why?

- Grader checks: deliberate context preparation and task splitting, not "I asked it to solve the problem".
- Pull from: key prompts, the first planning prompt, any prompt you had to rewrite.
- Skeleton: I started by giving the agent <context: files, constraints, examples>. I asked it to <plan / implement one piece> rather than the whole task because <reason>. The most effective prompt was <prompt intent>, since <why it worked: scoped, had a test case, named the file>. Result: <what it produced and what I kept>.

## 2. Give an example where AI output was wrong or suboptimal. How did you detect it, and what did you change?

- Grader checks: real critical review with a specific defect and a specific detection method.
- Pull from: the rejected or changed rows of the How I used AI table.
- Skeleton: The agent proposed <exact suggestion>. I noticed <how: read the diff, a failing test, an edge input> that it <defect>. I <rejected / rewrote / re-prompted with X> because <reason tied to requirements>. Result: <test or behaviour that now passes>.
- If the notes contain no rejection, say so honestly and describe the checks the output passed; do not invent one.

## 3. How do you verify AI-generated code before you ship it?

- Grader checks: verification as a habit with concrete tools, not "I test it".
- Pull from: tests added, edge cases, manual runs, diff review notes.
- Skeleton: In this task I <read every line of the diff / ran X>. I wrote tests for <behaviours and edge inputs> before accepting <change>. I also <manual check>. This matters because <AI failure mode it guards against>. Result: <N tests passing, one bug caught>.

## 4. How do you give an AI agent context in a large codebase?

- Grader checks: context preparation, which Orange Logic names explicitly.
- Pull from: recon notes, files you pointed the agent at, conventions you stated.
- Skeleton: Here I pointed the agent at <specific files, README, an existing similar function> and stated <constraints: language version, style, no new dependencies>. I kept each request to <one function / one file> so the diff stayed reviewable. In a larger codebase I would <add: architecture notes, test commands, examples of the house pattern>. Result: <the agent's output matched existing patterns / needed one fix>.

## 5. What are the risks of AI-assisted coding in production, and how do you mitigate them?

- Grader checks: awareness of real failure modes plus concrete mitigations.
- Lead with two or three risks, each with a mitigation, and use the session as one piece of evidence.
- Common risks and mitigations:
  - Hallucinated APIs or options -> compile, run, check the docs.
  - Subtly wrong logic that looks right -> tests for edge cases, read the diff line by line.
  - Security holes (injection, missing auth checks, unsafe deserialisation) -> security review of touched paths.
  - Secrets or customer data pasted into prompts -> never paste credentials or real data, use the approved tool.
  - Licensing of generated code -> rely on approved tools with IP terms.
  - Over-trust and skill erosion -> the author still owns and can explain every line.
  - Style and architecture drift -> give the agent house patterns, review against them.
  - Untested code -> no merge without tests that would have failed before the change.

## 6. With more time, what would you improve?

- Grader checks: honest self-assessment and sensible priorities.
- Pull from: known limits, skipped edge cases, trade-offs.
- Skeleton: The biggest gap is <limit>, which matters because <impact>. I would first <fix>, then <next item>. I chose not to do it now because <time / scope reason>.

## 7. How would you review a teammate's AI-generated pull request?

- Grader checks: reviewing implementation, PR-quality delivery.
- Skeleton: I would treat it like any PR but read more sceptically: check that the description says what the agent did and what the author verified, run the tests and look for missing edge cases, check for invented APIs, unused code and silent behaviour changes, and ask the author to explain any part they cannot justify. In this task I applied the same check to my own diff when <example>.

## Other questions that come up

- "How did you decide when not to use AI?" -> pull a moment you wrote something by hand and say why.
- "How did you split the task between you and the agent?" -> recon and decisions were mine, boilerplate and first drafts were the agent's, review was mine.
- "What would you tell a junior engineer about using AI?" -> one rule plus the example from this session that taught it.
