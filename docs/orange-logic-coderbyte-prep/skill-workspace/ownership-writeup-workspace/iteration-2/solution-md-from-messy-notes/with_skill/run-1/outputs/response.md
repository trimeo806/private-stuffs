Saved `SOLUTION.md`: **222 words** (`wc -w SOLUTION.md`), under the 250 limit with room to spare.

It has Approach, a How I used AI table (plan kept, upscaling rejected -> `withoutEnlargement: true`, swallowed try/catch changed -> `UnsupportedImageError`), Verification (your 4 tests, 4/4 pass), Trade-offs (`Promise.all` vs ~3x memory, 20 MB limit) and Known limits (WebP, HEIC).

Before you submit, check these:
- **One gap:** your notes don't record the prompt that asked the agent to write the code, so the table's second row reads `[gap: prompt that asked for the implementation]`. What did you ask for there (one line is enough)? If you don't remember, change it to "Implementation prompt" rather than inventing wording.
- **My lines, not from your notes:** the "-> next step" parts of Known limits ("add it as a format", "install sharp with libheif"), the "Not verified: memory peak under load" bullet (your notes give ~3x as an estimate, not a measurement), and the reason "Swallowed errors hide failures". Delete or reword any you don't agree with.
- One risk you may want to add if there is room: the 20 MB limit is the compressed file size. Decoded pixels can be much larger, so "fine with 20 MB" is your judgement, not a measured result.
