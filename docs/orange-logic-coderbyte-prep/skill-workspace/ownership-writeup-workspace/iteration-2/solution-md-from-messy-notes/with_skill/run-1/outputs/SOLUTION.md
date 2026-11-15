## Approach
The thumbnail service makes three sizes (small 150, medium 600, large 1200) per uploaded image and skips non-images. I resize with sharp, all three sizes in parallel.

## How I used AI
| Prompt intent | Kept / rejected / changed | Reason |
|---|---|---|
| Plan only, no code, from `thumbnail.ts` and the task text | Kept: sharp, parallel resizes via `Promise.all` | Faster; memory cost under Trade-offs |
| [gap: prompt that asked for the implementation] | Rejected: upscaling images smaller than the target size | A 200px image blown up to 1200 looks blurry and wastes storage. I added `withoutEnlargement: true` |
| Same implementation step | Changed: a try/catch around sharp that swallowed errors and returned `[]` | Swallowed errors hide failures; it now throws `UnsupportedImageError` so the caller can mark the asset failed |

## Verification
- Tests, 4/4 pass: 2000px PNG -> 3 thumbnails; 100px GIF -> no upscale; PDF -> skipped; corrupted JPG -> `UnsupportedImageError`.
- Not verified: memory peak under load.

## Trade-offs
- `Promise.all` over resizing one size at a time: faster, but about 3x peak memory, which is acceptable with the 20 MB upload limit.

## Known limits
- No WebP output -> add it as a format.
- No HEIC input (sharp needs libheif) -> install sharp with libheif.
