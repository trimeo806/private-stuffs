namespace Dam.Core.Assets;

public interface IAssetRepository
{
    IEnumerable<Asset> All();
    Asset? GetById(Guid id);
    void Save(Asset asset);
}
