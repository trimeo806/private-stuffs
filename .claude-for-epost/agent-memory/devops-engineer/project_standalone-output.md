---
name: project_standalone-output
description: output:'standalone' is commented out in next.config.ts; blocked on Next 16 stable + EC2/EKS runner setup
metadata:
  type: project
---

`output: 'standalone'` is intentionally disabled in `apps/web/next.config.ts`.

**Why:** Comment says "TODO: re-enable after Next.js 16 stable / EC2 setup". As of 2026-06-21, Next.js 16.2.9 is in use — Next 16 is current stable, so the blocker is the EC2/EKS runner environment verification, not the Next.js version itself.

**How to apply:** Enabling standalone would eliminate the ~1-2 GB `node_modules` COPY in the runner stage and cut final image size by ~60-70%. This is the highest-impact single optimization available, but requires testing that `src/lib`, `documentation/`, and `next.config.ts` are correctly handled by standalone's trace-based file inclusion.
