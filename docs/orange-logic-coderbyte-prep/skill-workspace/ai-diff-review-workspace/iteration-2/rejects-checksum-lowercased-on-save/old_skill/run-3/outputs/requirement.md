# Task 1: duplicate uploads are not detected

Uploading the same file twice is supposed to be flagged as a duplicate. The Mac client sends checksums as uppercase hex and the web client sends lowercase hex, so the same file uploaded from both clients is not detected.

Make `DuplicateDetector.IsDuplicate` treat checksums that differ only in letter case as equal. The test `IsDuplicate_ReturnsTrue_WhenChecksumDiffersOnlyInLetterCase` is currently failing.

Do not change the public API. Existing assets are loaded into `AssetRepository` through its constructor when the service starts.
