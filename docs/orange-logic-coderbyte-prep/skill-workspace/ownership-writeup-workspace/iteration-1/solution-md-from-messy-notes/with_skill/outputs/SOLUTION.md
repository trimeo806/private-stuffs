## Approach
The service makes three thumbnails (small 150, medium 600, large 1200) per uploaded image and skips non-images. I use sharp and resize all three sizes in parallel with Promise.all, which is faster than resizing them one by one.

## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| Plan only, no code, from thumbnail.ts and the task text | Kept: sharp, three parallel resizes with Promise.all | Faster; the extra memory fits the 20 MB upload limit |
| Implement the plan [gap: exact prompt] | Rejected upscaling images smaller than the target | A 200px image blown up to 1200 looks blurry and wastes storage; I added `withoutEnlargement: true` |
| Implement the plan [gap: exact prompt] | Changed a try/catch that swallowed sharp errors and returned `[]` | It now throws `UnsupportedImageError`, so the caller can mark the asset failed |

## Verification
- Tests: 4/4 pass. 2000px PNG gives 3 thumbnails; 100px GIF is not upscaled; PDF is skipped; corrupted JPG throws `UnsupportedImageError`.
- Edge cases: source smaller than target, non-image, corrupted file.
- Manual checks: [gap: any manual run or visual check]

## Trade-offs
- Promise.all over sequential resizing: faster, but about 3x peak memory. Fine with the 20 MB upload limit.
- Throwing over returning `[]`: a broken upload gets marked failed instead of silently having no thumbnails.

## Known limits
- No WebP output -> add it as an output format.
- No HEIC input, because sharp needs libheif -> add libheif to the build, plus a HEIC test.
