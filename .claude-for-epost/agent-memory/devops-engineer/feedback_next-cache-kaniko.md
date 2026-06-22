---
name: feedback_next-cache-kaniko
description: Never mount ci-next-cache PVC for web build — causes 404 on /_next/static due to stale chunk hashes
metadata:
  type: feedback
---

Never mount the `ci-next-cache` PVC (`/app/.next/cache`) inside the kaniko web build pod.

**Why:** Kaniko's `--snapshotMode=redo` + Turbopack generates content-addressed chunk hashes at build time. A stale `.next/cache` from a prior commit causes the build to reuse pre-compiled modules whose chunk hashes differ from what the new manifest expects — resulting in `/_next/static/*.js` 404s in production.

**How to apply:** When configuring the gitlab-runner PVC mounts or kaniko `--cache-dir`, always omit the `.next/cache` path for the web/frontend job. The `ci-next-cache` PVC exists but should NOT be mounted for `build:frontend`. It can be mounted for CMS only if CMS uses webpack (not Turbopack).
