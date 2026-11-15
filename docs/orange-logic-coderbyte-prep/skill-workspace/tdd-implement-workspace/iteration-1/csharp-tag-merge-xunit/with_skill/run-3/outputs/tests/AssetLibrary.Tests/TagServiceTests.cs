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
    public void MergeTags_moves_every_asset_from_the_source_tag_to_the_target_and_counts_them()
    {
        var logo = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo", "print" });
        var banner = new Asset(Guid.NewGuid(), "banner.png", new[] { "web", "logo" });
        var photo = new Asset(Guid.NewGuid(), "photo.jpg", new[] { "print" });
        var repository = new InMemoryAssetRepository(logo, banner, photo);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(2, changed);
        Assert.Equal(new[] { "brand", "print" }, repository.GetById(logo.Id)!.Tags);
        Assert.Equal(new[] { "web", "brand" }, repository.GetById(banner.Id)!.Tags);
        Assert.Equal(new[] { "print" }, repository.GetById(photo.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_matches_the_source_tag_in_any_case()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "LOGO", "print" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "brand", "print" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_keeps_a_single_copy_of_a_target_the_asset_already_has_in_another_case()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo", "Brand", "print" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "Brand", "print" }, repository.GetById(asset.Id)!.Tags);
    }

    [Theory]
    [InlineData("logo")]
    [InlineData("LOGO")]
    public void MergeTags_into_the_same_tag_changes_nothing(string target)
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo", "print" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", target);

        Assert.Equal(0, changed);
        Assert.Equal(new[] { "logo", "print" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_trims_the_tag_names()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags(" logo ", " brand ");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Theory]
    [InlineData(null)]
    [InlineData("")]
    [InlineData("   ")]
    public void MergeTags_rejects_a_blank_source_tag_and_changes_nothing(string? blank)
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var error = Assert.Throws<ArgumentException>(() => service.MergeTags(blank!, "brand"));

        Assert.Equal("sourceTag", error.ParamName);
        Assert.Equal(new[] { "logo" }, repository.GetById(asset.Id)!.Tags);
    }

    [Theory]
    [InlineData(null)]
    [InlineData("")]
    [InlineData("   ")]
    public void MergeTags_rejects_a_blank_target_tag_and_changes_nothing(string? blank)
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var error = Assert.Throws<ArgumentException>(() => service.MergeTags("logo", blank!));

        Assert.Equal("targetTag", error.ParamName);
        Assert.Equal(new[] { "logo" }, repository.GetById(asset.Id)!.Tags);
    }
}
