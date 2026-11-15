# Task 2 - Bugfix: support ticket

> "Customers upload the same file twice and both copies are stored. Our desktop client sends
> checksums in uppercase, the web uploader in lowercase."

Do not change public signatures or record shapes.

## Spec

- `Upload(fileName, checksum, sizeBytes)`: `fileName` and `checksum` required (not blank) -> `ArgumentException`. `sizeBytes` >= 0, files can be larger than 2 GB.
- Checksums compare case-insensitively. Duplicate -> `DuplicateFileException` carrying the existing id.
- `TotalBytes()` = sum of all sizes.
