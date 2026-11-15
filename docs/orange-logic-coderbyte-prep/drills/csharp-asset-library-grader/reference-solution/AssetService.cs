namespace AssetLibrary;

public sealed class AssetService
{
    private const string NoExtension = "(none)";

    private readonly IAssetRepository _repository;
    private int _nextId = 1;

    public AssetService(IAssetRepository repository)
    {
        _repository = repository;
    }

    public Asset Upload(string fileName, long sizeBytes, string checksum, IEnumerable<string> tags, DateTime uploadedAt)
    {
        if (string.IsNullOrWhiteSpace(fileName))
        {
            throw new ArgumentException("File name is required.", nameof(fileName));
        }

        if (string.IsNullOrWhiteSpace(checksum))
        {
            throw new ArgumentException("Checksum is required.", nameof(checksum));
        }

        if (sizeBytes < 0)
        {
            throw new ArgumentOutOfRangeException(nameof(sizeBytes), "Size cannot be negative.");
        }

        var existing = _repository.FindByChecksum(checksum);
        if (existing is not null)
        {
            throw new DuplicateAssetException(checksum, existing.Id);
        }

        var asset = new Asset(_nextId++, fileName, sizeBytes, checksum, TagNormalizer.Normalize(tags), uploadedAt);
        _repository.Save(asset);
        return asset;
    }

    public IReadOnlyList<Asset> SearchByTags(IEnumerable<string> tags, bool matchAll)
    {
        var wanted = TagNormalizer.Normalize(tags);
        if (wanted.Count == 0)
        {
            return Array.Empty<Asset>();
        }

        return _repository.All()
            .Where(asset => matchAll
                ? wanted.All(tag => asset.Tags.Contains(tag))
                : wanted.Any(tag => asset.Tags.Contains(tag)))
            .OrderBy(asset => asset.Id)
            .ToList();
    }

    public PagedResult<Asset> GetPage(int pageNumber, int pageSize)
    {
        if (pageNumber < 1)
        {
            throw new ArgumentOutOfRangeException(nameof(pageNumber), "Page number starts at 1.");
        }

        if (pageSize < 1)
        {
            throw new ArgumentOutOfRangeException(nameof(pageSize), "Page size must be positive.");
        }

        var ordered = _repository.All()
            .OrderByDescending(asset => asset.UploadedAt)
            .ThenBy(asset => asset.Id)
            .ToList();

        var items = ordered.Skip((pageNumber - 1) * pageSize).Take(pageSize).ToList();
        var totalPages = (ordered.Count + pageSize - 1) / pageSize;

        return new PagedResult<Asset>(items, pageNumber, pageSize, ordered.Count, totalPages);
    }

    public StorageReport GetStorageReport(int topN)
    {
        if (topN < 0)
        {
            throw new ArgumentOutOfRangeException(nameof(topN), "Top N cannot be negative.");
        }

        var assets = _repository.All();

        var byExtension = assets
            .GroupBy(asset => ExtensionOf(asset.FileName))
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

    private static string ExtensionOf(string fileName)
    {
        var name = Path.GetFileName(fileName);
        var dot = name.LastIndexOf('.');
        return dot <= 0 || dot == name.Length - 1
            ? NoExtension
            : name[(dot + 1)..].ToLowerInvariant();
    }
}
