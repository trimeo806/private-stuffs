# Iteration 2 edit brief (shared by all skill editors)

Paths:
- SC (skill-creator, quote it, has spaces): "/Users/nguyenthanhtri/Library/Application Support/Claude/local-agent-mode-sessions/skills-plugin/86bf2377-bd11-4ce7-a911-874a13f5906f/cc2e9e87-a3a1-4804-a2dd-2eabb70d41a6/skills/skill-creator"
- R (skills): /Users/nguyenthanhtri/.claude/skills/coderbyte-prep/skills
- W (workspace): /Users/nguyenthanhtri/workspace/private-stuffs/docs/orange-logic-coderbyte-prep/skill-workspace
- Original baseline: W/skill-snapshot/<skill> (do not edit). Iteration-1 copy: W/skill-snapshot-iter1/<skill> (do not edit).

## Goal
User finding: the rebuilt skills cost +4-13k tokens and +7-16s per run versus the originals, with no clear quality gain except edge-case-tests. Make each skill cheaper AND fix its known misses, without losing what made it catch real bugs.

## Part A: edit the skill in R/<skill>/
1. Read SC/SKILL.md section "Improving the skill" and "Skill Writing Guide" first.
2. SKILL.md body target: about 900-1100 words (measure with `wc -w`, report before/after). Keep: when it runs, inputs, steps, the output template, the in-IDE prompt template(s), the hand-off. Move out: worked examples, long catalogues, per-language detail -> references/ (existing file or a new references/examples.md), with a one-line pointer saying when to read it. Cut repetition and explanation that does not change behavior; keep one short "why" where it prevents a known mistake.
3. Apply the skill-specific fixes in your task message. Generalize them: write the rule, never the eval's specific input (no asset names, no case-specific numbers from evals).
4. Frontmatter: only name + description; description <= 1024 chars, no angle brackets. You may tighten it but keep it "pushy" with trigger phrases.
5. Code in examples: no comments, no doc comments, no ticket/spec IDs in test names.
6. Validate: `uv run --offline --no-project --with pyyaml python3 "$SC/scripts/quick_validate.py" R/<skill>` must print "Skill is valid!".

## Part B: tighten the eval checks in R/<skill>/evals/<case>/graders/
- Read the iteration-1 results first: W/<skill>-workspace/iteration-1/benchmark.json (`notes`) and each W/<skill>-workspace/iteration-1/<case>/*/grading.json (`eval_feedback`, failed expectations).
- Fix flawed assertions named there. For each case where both configs passed every assertion (non-discriminating), replace or add ONE assertion that a careful, high-quality answer passes and a sloppy one fails (e.g. invents nothing beyond the inputs, states a default for every ambiguity, word count measured, catches a second-order bug). Keep 3-5 graders per case. Keep the file format exactly as the existing graders (same frontmatter keys). Assertions must be objectively checkable from the reply and files.
- Do not change prompt.md, case.yaml or files/.

## Part C: set up the run workspace
Iteration dir: W/<skill>-workspace/iteration-2/ (for tdd-implement: W/tdd-implement-workspace/iteration-1/).
For each case:
- `<case>/task-prompt.txt`: the prompt.md text without its settings block (copy from iteration-1 if it exists).
- `<case>/eval_metadata.json`: {"eval_name": "<case>", "prompt": "<task-prompt text>", "assertions": [<one string per grader, the assertion sentence>]}.
- Run dirs, each with an `outputs/` folder holding the case's input files (if the case has files/, run its place-files.sh from inside that outputs/ folder; then delete any __pycache__, bin, obj, node_modules that appear):
  - with_skill/run-1, run-2, run-3
  - baseline: for the 4 existing skills `old_skill/run-1` = copy of iteration-1 `<case>/old_skill/outputs/` and `timing.json` (NOT grading.json); plus `old_skill/run-2`, `old_skill/run-3` fresh. For tdd-implement: `without_skill/run-1..3` fresh.
- Do not run any evals. Do not create grading.json.

## Report back (under 15 lines)
Word counts before/after (SKILL.md and references), each fix applied (one line each), each assertion changed/added (case: old -> new), validator output, and the list of run dirs created.
