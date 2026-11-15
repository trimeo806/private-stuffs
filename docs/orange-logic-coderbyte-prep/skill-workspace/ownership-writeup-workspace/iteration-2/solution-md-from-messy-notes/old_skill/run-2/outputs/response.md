Saved `SOLUTION.md` (224 words, under the 250 limit).

It has five sections: Approach, How I used AI (table: plan kept, upscaling rejected → `withoutEnlargement: true`, swallowed errors changed → `UnsupportedImageError`), Verification (the 4 tests, 4/4 pass), Trade-offs (`Promise.all` speed vs ~3x memory, OK under the 20 MB limit), and Known limits (WebP, HEIC/libheif).

I used only what was in your notes. Two things to check if you have 30 seconds:
- Your notes don't give the exact prompts for the resize and error-handling steps, so those rows describe what the agent produced instead of what you asked.
- I left out a "manual checks" line because your notes list none. If you ran it on a real upload, add one line.
