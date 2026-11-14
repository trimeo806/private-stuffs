# learning-kit

Skills for self-study.

| Skill | Source | Use |
|-------|--------|-----|
| `socratic-tutor` | [janestreet/agent_skills](https://github.com/janestreet/agent_skills) @ `7c6715c` (MIT) | "teach me X", "quiz me on X", "drill me on X" |

## Install

```bash
claude plugin marketplace add /Users/nguyenthanhtri/workspace/private-stuffs/learning-kit
claude plugin install learning-kit@learning-kit
```

Then: `/learning-kit:socratic-tutor teach me <topic>` or just ask "teach me <topic>".

In-session commands: `show progress`, `harder` / `easier`, `just tell me`, `save state`, `resume from: <state>`.

## Update from upstream

```bash
git clone --depth 1 https://github.com/janestreet/agent_skills.git /tmp/agent_skills
cp -R /tmp/agent_skills/socratic-tutor plugins/learning-kit/skills/
```
