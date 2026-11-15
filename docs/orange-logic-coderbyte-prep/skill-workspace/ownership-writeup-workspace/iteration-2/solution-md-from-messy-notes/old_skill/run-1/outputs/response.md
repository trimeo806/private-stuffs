I wrote and saved `SOLUTION.md`. It is 229 words, so it fits your 250-word limit.

What it covers:
- **Approach:** the three sizes, skipping non-images, and sharp with `Promise.all`.
- **How I used AI:** a table with one row you kept and two you changed:
  - **Kept:** the plan-only first prompt and the plan it produced.
  - **Rejected:** upscaling small images. You added `withoutEnlargement: true`.
  - **Changed:** the try/catch that swallowed errors. It now throws `UnsupportedImageError`.
- **Verification:** your 4 tests, with the input and expected result for each. All 4 pass.
- **Trade-offs:** `Promise.all` uses about 3x the peak memory, which is fine under the 20 MB upload limit.
- **Known limits:** no WebP output, and no HEIC support because sharp needs libheif.

Everything in it comes from your notes; I added nothing. Your notes don't say what you typed for the two implementation steps, so that table shows what the AI produced in those steps and what you decided.

Before you submit, please check two things:
- Did you do any manual checks, such as opening the output files? If so, add one line under Verification.
- If the graders can see test names, put the real names next to the 4 cases.
