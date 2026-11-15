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
        throw new NotImplementedException();
    }
}
