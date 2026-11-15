namespace AssetLibrary;

public interface IAssetRepository
{
    void Save(Asset asset);

    Asset? FindById(int id);

    Asset? FindByChecksum(string checksum);

    IReadOnlyList<Asset> All();
}
