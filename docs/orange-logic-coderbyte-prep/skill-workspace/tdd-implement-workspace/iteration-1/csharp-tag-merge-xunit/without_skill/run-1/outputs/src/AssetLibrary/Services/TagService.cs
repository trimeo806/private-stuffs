using AssetLibrary.Repositories;

namespace AssetLibrary.Services;

public sealed class TagService
{
    private readonly IAssetRepository _repository;

    public TagService(IAssetRepository repository)
    {
        _repository = repository;
    }

    public void AddTag(Guid assetId, string tag)
    {
        if (string.IsNullOrWhiteSpace(tag))
        {
            throw new ArgumentException("Tag must not be empty.", nameof(tag));
        }

        var asset = _repository.GetById(assetId)
            ?? throw new KeyNotFoundException($"Asset {assetId} was not found.");

        var name = tag.Trim();
        if (asset.Tags.Any(existing => string.Equals(existing, name, StringComparison.OrdinalIgnoreCase)))
        {
            return;
        }

        asset.Tags.Add(name);
        _repository.Save(asset);
    }

    public int MergeTags(string sourceTag, string targetTag)
    {
        if (string.IsNullOrWhiteSpace(sourceTag))
        {
            throw new ArgumentException("Source tag must not be empty.", nameof(sourceTag));
        }

        if (string.IsNullOrWhiteSpace(targetTag))
        {
            throw new ArgumentException("Target tag must not be empty.", nameof(targetTag));
        }

        var source = sourceTag.Trim();
        var target = targetTag.Trim();
        if (string.Equals(source, target, StringComparison.OrdinalIgnoreCase))
        {
            return 0;
        }

        var changed = 0;
        foreach (var asset in _repository.GetAll())
        {
            var sourceIndex = asset.Tags.FindIndex(tag => IsSameTag(tag, source));
            if (sourceIndex < 0)
            {
                continue;
            }

            if (!asset.Tags.Any(tag => IsSameTag(tag, target)))
            {
                asset.Tags[sourceIndex] = target;
            }

            asset.Tags.RemoveAll(tag => IsSameTag(tag, source));
            _repository.Save(asset);
            changed++;
        }

        return changed;
    }

    private static bool IsSameTag(string tag, string name) =>
        string.Equals(tag.Trim(), name, StringComparison.OrdinalIgnoreCase);
}
