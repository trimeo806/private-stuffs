namespace AssetLibrary;

public sealed record Asset(
    int Id,
    string FileName,
    long SizeBytes,
    string Checksum,
    IReadOnlyCollection<string> Tags,
    DateTime UploadedAt);
