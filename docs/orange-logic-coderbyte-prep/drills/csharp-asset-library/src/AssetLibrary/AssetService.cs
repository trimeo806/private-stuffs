namespace AssetLibrary;

public sealed class AssetService
{
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

        var offset = (long)(pageNumber - 1) * pageSize;
        var items = ordered.Skip((int)Math.Min(offset, ordered.Count)).Take(pageSize).ToList();
        var totalPages = ordered.Count / pageSize + (ordered.Count % pageSize == 0 ? 0 : 1);

        return new PagedResult<Asset>(items, pageNumber, pageSize, ordered.Count, totalPages);
    }

    public StorageReport GetStorageReport(int topN)
    {
        return StorageReportCalculator.Build(_repository.All(), topN);
    }
}
