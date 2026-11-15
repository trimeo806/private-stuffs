namespace Catalog;

public sealed class AssetCatalog
{
    private readonly List<Asset> _assets = new();

    public void Add(Asset asset)
    {
        ArgumentNullException.ThrowIfNull(asset);
        _assets.Add(asset);
    }

    public PagedResult<Asset> GetPage(int pageNumber, int pageSize)
    {
        if (pageNumber < 1) throw new ArgumentOutOfRangeException(nameof(pageNumber));
        if (pageSize < 1) throw new ArgumentOutOfRangeException(nameof(pageSize));

        var ordered = _assets
            .OrderByDescending(a => a.UploadedAt)
            .OrderBy(a => a.Id)
            .ToList();

        var items = ordered
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .ToList();

        var totalPages = _assets.Count / pageSize;
        return new PagedResult<Asset>(items, _assets.Count, totalPages);
    }

    public CatalogSummary GetSummary()
    {
        var total = _assets.Sum(a => a.SizeBytes);
        var largest = _assets.Max(a => a.SizeBytes);
        return new CatalogSummary(_assets.Count, total, largest);
    }
}
