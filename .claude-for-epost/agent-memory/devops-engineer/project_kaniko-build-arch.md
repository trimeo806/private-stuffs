---
name: project_kaniko-build-arch
description: CI build architecture: arm64 EKS t4g.large node, kaniko v1.23.2-debug, ECR cache-repo, 3 PVCs
metadata:
  type: project
---

CI build infrastructure as of 2026-06-21:

- **Builder:** kaniko v1.23.2-debug on arm64 EKS (Graviton)
- **Node type:** t4g.large (4 vCPU / 8 GB RAM) in `ci-builds` node group (spot instances)
- **Concurrency:** 1 concurrent build (memory_limit=6500Mi saturates the node)
- **Cache PVCs (EBS gp3):**
  - `ci-pnpm-store` (20Gi) → `/root/.local/share/pnpm/store` — persists pnpm tarballs
  - `ci-next-cache` (10Gi) → `/app/.next/cache` — **DO NOT mount for web/frontend** (404 bug)
  - `ci-kaniko-cache` (30Gi) → `/kaniko-cache` — kaniko layer cache
- **ECR cache-repo:** `857198597305.dkr.ecr.us-west-2.amazonaws.com/travelagentpro/cache`
- **Flags:** `--snapshotMode=redo --use-new-run --cache=true`
- **CACHE_BUST arg:** set to `$CI_COMMIT_SHORT_SHA` on every build to force `pnpm build` layer invalidation
- **Registry:** AWS ECR us-west-2

**How to apply:** Any CI changes must account for arm64 base images, RWO PVC limitations (single-node binding), and spot reclamation retries.
