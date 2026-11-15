using AssetLibrary.Models;

namespace AssetLibrary.Repositories;

public sealed class InMemoryAssetRepository : IAssetRepository
{
    private readonly Dictionary<Guid, Asset> _assets = new();

    public InMemoryAssetRepository(params Asset[] assets)
    {
        foreach (var asset in assets)
        {
            _assets[asset.Id] = Copy(asset);
        }
    }

    public IReadOnlyList<Asset> GetAll() => _assets.Values.Select(Copy).ToList();

    public Asset? GetById(Guid id) => _assets.TryGetValue(id, out var asset) ? Copy(asset) : null;

    public void Save(Asset asset) => _assets[asset.Id] = Copy(asset);

    private static Asset Copy(Asset asset) => new(asset.Id, asset.Name, asset.Tags);
}
