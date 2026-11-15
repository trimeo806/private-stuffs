namespace AssetLibrary;

public sealed class InMemoryAssetRepository : IAssetRepository
{
    private readonly Dictionary<int, Asset> _assets = new();

    public void Save(Asset asset) => _assets[asset.Id] = asset;

    public Asset? FindById(int id) => _assets.TryGetValue(id, out var asset) ? asset : null;

    public Asset? FindByChecksum(string checksum) =>
        _assets.Values.FirstOrDefault(asset => string.Equals(asset.Checksum, checksum, StringComparison.OrdinalIgnoreCase));

    public IReadOnlyList<Asset> All() => _assets.Values.ToList();
}
