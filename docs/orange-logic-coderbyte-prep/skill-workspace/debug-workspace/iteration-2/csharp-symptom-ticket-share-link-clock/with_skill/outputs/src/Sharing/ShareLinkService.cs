namespace Sharing;

public sealed class ShareLinkService
{
    private readonly IClock _clock;
    private readonly Dictionary<string, ShareLink> _links = new(StringComparer.Ordinal);

    public ShareLinkService(IClock clock)
    {
        _clock = clock;
    }

    public ShareLink Create(int assetId, TimeSpan lifetime)
    {
        if (lifetime <= TimeSpan.Zero) throw new ArgumentOutOfRangeException(nameof(lifetime));

        var now = _clock.UtcNow;
        var link = new ShareLink(Guid.NewGuid().ToString("N"), assetId, now, now.Add(lifetime));
        _links[link.Token] = link;
        return link;
    }

    public bool IsValid(string token)
    {
        if (!_links.TryGetValue(token, out var link)) return false;
        return DateTime.Now < link.ExpiresAtUtc;
    }

    public IReadOnlyList<ShareLink> ActiveLinksFor(int assetId) =>
        _links.Values
            .Where(l => l.AssetId == assetId && l.ExpiresAtUtc > DateTime.Now)
            .OrderBy(l => l.CreatedAtUtc)
            .ToList();

    public int PurgeExpired()
    {
        var expired = _links.Values
            .Where(l => l.ExpiresAtUtc <= _clock.UtcNow)
            .Select(l => l.Token)
            .ToList();

        foreach (var token in expired) _links.Remove(token);
        return expired.Count;
    }
}
