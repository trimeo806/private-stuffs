## Approach
I generate 150, 600 and 1200 px thumbnails per image with sharp, resized in parallel, and skip non-images. Images are never enlarged; unreadable images throw UnsupportedImageError.

## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| Plan only, no code, from thumbnail.ts and the task text | Kept: sharp, three resizes with Promise.all | The 2000px PNG test yields all three thumbnails |
| Implementation | Rejected: upscaling images below target size | A 200px image blown up to 1200 is blurry and wastes storage; I added withoutEnlargement: true |
| Implementation | Changed: try/catch that swallowed errors and returned [] | It now throws UnsupportedImageError so the caller can mark the asset failed |

## Verification
- Tests: 2000px PNG gives 3 thumbnails, 100px GIF not upscaled, PDF skipped, corrupted JPG throws UnsupportedImageError. 4/4 pass.
- Not verified: peak memory; ~3x is an estimate, not measured.

## Trade-offs
- Promise.all over sequential resizes: faster, but ~3x peak memory; I judged it fine given the 20 MB upload limit.

## Known limits
- No WebP output -> add it as an output format.
- No HEIC: sharp needs libheif -> build sharp with libheif.
- 20 MB caps file size, not decoded pixels -> measure memory on a large image.
