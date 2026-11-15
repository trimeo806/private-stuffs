using AssetLibrary.Models;
using AssetLibrary.Repositories;
using AssetLibrary.Services;
using Xunit;

namespace AssetLibrary.Tests;

public class TagServiceTests
{
    [Fact]
    public void AddTag_adds_a_new_tag_and_saves_it()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png");
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        service.AddTag(asset.Id, " brand ");

        Assert.Equal(new[] { "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void AddTag_ignores_a_tag_the_asset_already_has_in_another_case()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Brand" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        service.AddTag(asset.Id, "brand");

        Assert.Equal(new[] { "Brand" }, repository.GetById(asset.Id)!.Tags);
    }
}
