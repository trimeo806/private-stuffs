# Memory Index

- [Next Cache Kaniko](feedback_next-cache-kaniko.md) — NEVER cache .next/cache (PVC ci-next-cache) for web build; causes 404 on /_next/static
- [Standalone Output Blocked](project_standalone-output.md) — output:'standalone' disabled; re-enable after Next 16 stable + EC2 setup confirmed
- [Kaniko Build Architecture](project_kaniko-build-arch.md) — arm64 EKS, t4g.large ci-builds node, kaniko v1.23.2, ECR cache-repo, 3 PVCs
