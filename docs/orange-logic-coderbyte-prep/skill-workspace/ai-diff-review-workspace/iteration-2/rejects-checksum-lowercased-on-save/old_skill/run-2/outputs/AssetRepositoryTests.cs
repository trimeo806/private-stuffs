using Dam.Core.Assets;
using Xunit;

namespace Dam.Core.Tests.Assets;

public class AssetRepositoryTests
{
    [Fact]
    public void GetById_ReturnsAssetExactlyAsSaved()
    {
        var repository = new AssetRepository(Array.Empty<Asset>());
        var asset = new Asset { Id = Guid.NewGuid(), FileName = "logo.png", Checksum = "9F86D081884C7D65" };

        repository.Save(asset);

        Assert.Equal("9F86D081884C7D65", repository.GetById(asset.Id)!.Checksum);
    }
}
