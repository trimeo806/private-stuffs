# Thumbnail Service

## Approach
Each uploaded image gets three thumbnails: small (150px), medium (600px) and large (1200px). Non-images are skipped. I used sharp and resize the three sizes in parallel with `Promise.all`.

## How I used AI
| Step | What I asked | What I did with the output |
|---|---|---|
| Plan | Gave the agent `thumbnail.ts` and the task text. Asked for a plan only, no code. | Kept: sharp, three sizes in parallel with `Promise.all`. |
| Resize | Agent's code upscaled images smaller than the target. | Rejected. A 200px image blown up to 1200px looks blurry and wastes storage. Added `withoutEnlargement: true`. |
| Errors | Agent wrapped sharp in a try/catch that swallowed errors and returned `[]`. | Changed. It now throws `UnsupportedImageError`, so the caller can mark the asset as failed. |

## Verification
Tests: 4 added, 4/4 pass.
- 2000px PNG → 3 thumbnails
- 100px GIF → not upscaled
- PDF → skipped
- Corrupted JPG → throws `UnsupportedImageError`

## Trade-offs
- `Promise.all` over sequential resizes: faster, but about 3x peak memory. Fine with the 20 MB upload limit.

## Known limits / next steps
- No WebP output → add WebP as an output format.
- No HEIC input: sharp needs libheif → install sharp with libheif and add a HEIC test.
