using AssetLibrary.Models;

namespace AssetLibrary.Repositories;

public interface IAssetRepository
{
    IReadOnlyList<Asset> GetAll();

    Asset? GetById(Guid id);

    void Save(Asset asset);
}
