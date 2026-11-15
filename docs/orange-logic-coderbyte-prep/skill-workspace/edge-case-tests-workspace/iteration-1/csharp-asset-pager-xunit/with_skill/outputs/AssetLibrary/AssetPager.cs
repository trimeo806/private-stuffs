namespace AssetLibrary;

public static class AssetPager
{
    public const int MaxPageSize = 100;

    public static PagedResult<Asset> GetPage(IReadOnlyList<Asset> assets, int page, int pageSize)
    {
        ArgumentNullException.ThrowIfNull(assets);
        if (page < 1)
        {
            throw new ArgumentOutOfRangeException(nameof(page));
        }
        if (pageSize < 1 || pageSize > MaxPageSize)
        {
            throw new ArgumentOutOfRangeException(nameof(pageSize));
        }

        var ordered = assets
            .OrderBy(a => a.Name, StringComparer.OrdinalIgnoreCase)
            .ThenBy(a => a.Id)
            .ToList();

        var totalPages = ordered.Count / pageSize;
        var items = ordered
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .ToList();

        return new PagedResult<Asset>(items, page, pageSize, ordered.Count, totalPages);
    }
}
