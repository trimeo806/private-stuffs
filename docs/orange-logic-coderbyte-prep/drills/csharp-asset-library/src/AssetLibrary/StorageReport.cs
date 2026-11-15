namespace AssetLibrary;

public sealed record ExtensionUsage(string Extension, int Count, long TotalBytes);

public sealed record StorageReport(
    long TotalBytes,
    int AssetCount,
    IReadOnlyList<ExtensionUsage> ByExtension,
    IReadOnlyList<Asset> Largest);
