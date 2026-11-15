## Approach
The service makes 150, 600 and 1200 px thumbnails for each uploaded image and skips non-images. Sharp resizes all three in parallel (Promise.all) instead of sequentially.

## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| Plan from thumbnail.ts and the task text, no code | Kept: sharp, parallel resizes | I reviewed the plan before any code existed |
| Implement resizing | Rejected upscaling images below target size; added `withoutEnlargement: true` | A 200px image upscaled to 1200 looks blurry and wastes storage |
| Error handling | Changed: its try/catch swallowed errors and returned `[]`; now throws `UnsupportedImageError` | The caller can mark the asset failed instead of a silent empty result |

## Verification
- Tests (4/4 pass): 2000px PNG gives 3 thumbnails, 100px GIF is not upscaled, PDF is skipped, corrupted JPG throws `UnsupportedImageError`.
- Not verified: [gap: whether the ~3x memory peak was measured]

## Trade-offs
- Promise.all over sequential resizing: faster, but ~3x peak memory, which I judged fine with the 20 MB upload limit.

## Known limits
- No WebP output -> add it.
- No HEIC support: sharp needs libheif -> build sharp with libheif.
- 20 MB is compressed size; decoded images are larger -> measure peak memory on a 20 MB upload.
