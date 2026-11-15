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
        if (SameTag(source, target))
        {
            return 0;
        }

        var changed = 0;
        foreach (var asset in _repository.GetAll())
        {
            if (!asset.Tags.Any(tag => SameTag(tag, source)))
            {
                continue;
            }

            _repository.Save(new Asset(asset.Id, asset.Name, MergeTagList(asset.Tags, source, target)));
            changed++;
        }

        return changed;
    }

    private static List<string> MergeTagList(IReadOnlyList<string> tags, string source, string target)
    {
        var merged = new List<string>();
        var hasTarget = tags.Any(tag => SameTag(tag, target));
        foreach (var tag in tags)
        {
            if (!SameTag(tag, source))
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

    private static string RequireTagName(string tag, string paramName)
    {
        if (string.IsNullOrWhiteSpace(tag))
        {
            throw new ArgumentException("Tag must not be empty.", paramName);
        }

        return tag.Trim();
    }

    private static bool SameTag(string left, string right) =>
        string.Equals(left, right, StringComparison.OrdinalIgnoreCase);
}
