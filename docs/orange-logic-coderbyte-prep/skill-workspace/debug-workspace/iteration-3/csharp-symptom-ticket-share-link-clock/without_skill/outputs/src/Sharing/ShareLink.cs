namespace Sharing;

public sealed record ShareLink(string Token, int AssetId, DateTime CreatedAtUtc, DateTime ExpiresAtUtc);
