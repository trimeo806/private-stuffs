# SOLUTION

## Approach
A thumbnail service that makes three sizes (small 150, medium 600, large 1200) for each uploaded image and skips non-images. It uses sharp and resizes all three sizes in parallel with `Promise.all`, because that is faster than three resizes in a row.

## How I used AI
| Step | What I asked / what the AI gave | What I did with the output |
|---|---|---|
| Plan | Gave the agent `thumbnail.ts` and the task text. Asked for a plan only, no code. | Kept the plan: sharp, all 3 sizes in parallel with `Promise.all`. |
| Implement | Agent upscaled images smaller than the target size. | Rejected. A 200px image blown up to 1200px looks blurry and wastes storage. Added `withoutEnlargement: true`. |
| Implement | Agent wrapped sharp in a try/catch that swallowed errors and returned `[]`. | Changed it. It now throws `UnsupportedImageError`, so the caller can mark the asset as failed. |

## Verification
- Tests: 4 added, 4/4 pass.
  - 2000px PNG → 3 thumbnails
  - 100px GIF → no upscale
  - PDF → skipped
  - Corrupted JPG → throws `UnsupportedImageError`

## Trade-offs
- `Promise.all` over sequential resizes: faster, but about 3x peak memory. Fine with the 20 MB upload limit.

## Known limits / next steps
- No WebP output → add WebP as an output format.
- No HEIC support → sharp needs libheif; add it, then add a HEIC test.
