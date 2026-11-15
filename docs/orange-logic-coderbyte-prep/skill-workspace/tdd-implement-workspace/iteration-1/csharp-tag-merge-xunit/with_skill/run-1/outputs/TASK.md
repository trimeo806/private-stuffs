# Task 2: merge tags

Implement `TagService.MergeTags(string sourceTag, string targetTag)` in `src/AssetLibrary/Services/TagService.cs`.

- Every asset tagged `sourceTag` ends up tagged `targetTag` and is no longer tagged `sourceTag`.
- Tag names are compared case-insensitively. An asset that already has the target tag keeps a single copy of it.
- Return the number of assets that changed.
- A blank tag name throws `ArgumentException`.
- Keep the public API as it is: hidden tests compile against it.

Run the tests with `dotnet test tests/AssetLibrary.Tests`.
