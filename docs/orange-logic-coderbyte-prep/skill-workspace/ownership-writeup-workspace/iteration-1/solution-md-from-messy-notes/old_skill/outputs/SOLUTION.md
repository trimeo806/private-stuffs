# Solution: Thumbnail Service

## Approach
For each uploaded image, the service makes three thumbnails: small (150px), medium (600px) and large (1200px). Non-images are skipped. It uses sharp and resizes all three sizes in parallel with `Promise.all`.

## How I used AI
| Step | AI output | What I did and why |
|---|---|---|
| Recon | I gave the agent `thumbnail.ts` and the task text and asked for a plan only, no code. It proposed sharp with three parallel resizes. | Kept the plan. |
| Implement | Upscaled images smaller than the target size. | Rejected. A 200px image blown up to 1200px looks blurry and wastes storage. Added `withoutEnlargement: true`. |
| Implement | Wrapped sharp in a try/catch that swallowed errors and returned `[]`. | Changed. It now throws `UnsupportedImageError`, so the caller can mark the asset as failed instead of silently getting no thumbnails. |

## Verification
- Tests: 4 added, 4/4 pass.
  - 2000px PNG → 3 thumbnails
  - 100px GIF → no upscale
  - PDF → skipped
  - Corrupted JPG → throws `UnsupportedImageError`

## Trade-offs
- `Promise.all` over sequential resizes: faster, but peak memory is about 3x. Acceptable with the 20 MB upload limit.

## Known limits / next steps
- No WebP output → add it as an output format.
- No HEIC input → needs sharp built with libheif.
