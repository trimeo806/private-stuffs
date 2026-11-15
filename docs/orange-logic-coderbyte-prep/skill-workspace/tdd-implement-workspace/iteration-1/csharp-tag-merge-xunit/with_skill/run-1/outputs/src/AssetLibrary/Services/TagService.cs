using AssetLibrary.Models;
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
        var source = RequireTagName(sourceTag, nameof(sourceTag));
        var target = RequireTagName(targetTag, nameof(targetTag));
        if (IsSameTag(source, target))
        {
            return 0;
        }

        var tagged = _repository.GetAll()
            .Where(asset => asset.Tags.Any(tag => IsSameTag(tag, source)))
            .ToList();

        foreach (var asset in tagged)
        {
            _repository.Save(new Asset(asset.Id, asset.Name, MergeTagList(asset.Tags, source, target)));
        }

        return tagged.Count;
    }

    private static string RequireTagName(string tag, string paramName)
    {
        if (string.IsNullOrWhiteSpace(tag))
        {
            throw new ArgumentException("Tag must not be empty.", paramName);
        }

        return tag.Trim();
    }

    private static List<string> MergeTagList(IReadOnlyList<string> tags, string source, string target)
    {
        var hasTarget = tags.Any(tag => IsSameTag(tag, target));
        var merged = new List<string>();
        foreach (var tag in tags)
        {
            if (!IsSameTag(tag, source))
            {
                merged.Add(tag);
            }
            else if (!hasTarget)
            {
                merged.Add(target);
                hasTarget = true;
            }
        }

        return merged;
    }

    private static bool IsSameTag(string tag, string other) =>
        string.Equals(tag, other, StringComparison.OrdinalIgnoreCase);
}
