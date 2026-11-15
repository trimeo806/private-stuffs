namespace AssetLibrary;

public static class StorageReportCalculator
{
    public const string NoExtension = "(none)";

    public static StorageReport Build(IReadOnlyCollection<Asset> assets, int topN)
    {
        if (topN < 0)
        {
            throw new ArgumentOutOfRangeException(nameof(topN), "Top N cannot be negative.");
        }

        var byExtension = assets
            .GroupBy(asset => ExtensionOf(asset.FileName), StringComparer.Ordinal)
            .Select(group => new ExtensionUsage(group.Key, group.Count(), group.Sum(asset => asset.SizeBytes)))
            .OrderByDescending(usage => usage.TotalBytes)
            .ThenBy(usage => usage.Extension, StringComparer.Ordinal)
            .ToList();

        var largest = assets
            .OrderByDescending(asset => asset.SizeBytes)
            .ThenBy(asset => asset.FileName, StringComparer.Ordinal)
            .ThenBy(asset => asset.Id)
            .Take(topN)
            .ToList();

        return new StorageReport(assets.Sum(asset => asset.SizeBytes), assets.Count, byExtension, largest);
    }

    public static string ExtensionOf(string fileName)
    {
        var dot = fileName.LastIndexOf('.');
        if (dot <= 0 || dot == fileName.Length - 1)
        {
            return NoExtension;
        }

        return fileName[(dot + 1)..].ToLowerInvariant();
    }
}
