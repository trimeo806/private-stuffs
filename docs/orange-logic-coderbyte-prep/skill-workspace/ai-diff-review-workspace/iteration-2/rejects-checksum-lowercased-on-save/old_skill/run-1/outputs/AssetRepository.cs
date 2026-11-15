namespace Dam.Core.Assets;

public class AssetRepository : IAssetRepository
{
    private readonly Dictionary<Guid, Asset> _assets;

    public AssetRepository(IEnumerable<Asset> existing)
    {
        _assets = existing.ToDictionary(a => a.Id);
    }

    public IEnumerable<Asset> All() => _assets.Values;

    public Asset? GetById(Guid id) => _assets.TryGetValue(id, out var asset) ? asset : null;

    public void Save(Asset asset)
    {
        _assets[asset.Id] = asset;
    }
}
