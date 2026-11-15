namespace AssetLibrary;

public static class TagNormalizer
{
    public static IReadOnlyCollection<string> Normalize(IEnumerable<string> tags) =>
        tags
            .Where(tag => tag is not null)
            .Select(tag => tag.Trim().ToLowerInvariant())
            .Where(tag => tag.Length > 0)
            .Distinct()
            .ToList();
}
