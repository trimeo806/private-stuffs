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

    [Fact]
    public void MergeTags_retags_every_source_asset_with_the_target_and_returns_the_count()
    {
        var logo = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo", "print" });
        var icon = new Asset(Guid.NewGuid(), "icon.png", new[] { "web", "logo" });
        var banner = new Asset(Guid.NewGuid(), "banner.png", new[] { "web" });
        var repository = new InMemoryAssetRepository(logo, icon, banner);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(2, changed);
        Assert.Equal(new[] { "brand", "print" }, repository.GetById(logo.Id)!.Tags);
        Assert.Equal(new[] { "web", "brand" }, repository.GetById(icon.Id)!.Tags);
        Assert.Equal(new[] { "web" }, repository.GetById(banner.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_matches_tags_in_any_case_and_keeps_a_single_copy_of_an_existing_target()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Logo", "print", "BRAND" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "print", "BRAND" }, repository.GetById(asset.Id)!.Tags);
    }

    [Theory]
    [InlineData("", "brand")]
    [InlineData("   ", "brand")]
    [InlineData(null, "brand")]
    [InlineData("logo", "")]
    [InlineData("logo", "   ")]
    [InlineData("logo", null)]
    public void MergeTags_rejects_a_blank_tag_name_and_changes_nothing(string? sourceTag, string? targetTag)
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        Assert.Throws<ArgumentException>(() => service.MergeTags(sourceTag!, targetTag!));
        Assert.Equal(new[] { "logo" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_into_the_same_tag_in_another_case_changes_nothing()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo", "print" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", " LOGO ");

        Assert.Equal(0, changed);
        Assert.Equal(new[] { "logo", "print" }, repository.GetById(asset.Id)!.Tags);
    }
}
