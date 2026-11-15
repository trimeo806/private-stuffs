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
    public void MergeTags_moves_tagged_assets_to_the_target_tag_and_counts_only_those()
    {
        var tagged = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo", "print" });
        var untagged = new Asset(Guid.NewGuid(), "banner.png", new[] { "web" });
        var repository = new InMemoryAssetRepository(tagged, untagged);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "brand", "print" }, repository.GetById(tagged.Id)!.Tags);
        Assert.Equal(new[] { "web" }, repository.GetById(untagged.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_matches_the_source_tag_in_any_case()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "LOGO", "print", "Logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "brand", "print" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_keeps_a_single_target_tag_when_the_asset_already_has_it_in_another_case()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo", "Brand", "print" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "Brand", "print" }, repository.GetById(asset.Id)!.Tags);
    }

    [Theory]
    [InlineData("", "brand", "sourceTag")]
    [InlineData("   ", "brand", "sourceTag")]
    [InlineData("logo", "", "targetTag")]
    [InlineData("logo", "   ", "targetTag")]
    public void MergeTags_rejects_a_blank_tag_name_and_changes_nothing(string sourceTag, string targetTag, string paramName)
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var error = Assert.Throws<ArgumentException>(() => service.MergeTags(sourceTag, targetTag));

        Assert.Equal(paramName, error.ParamName);
        Assert.Equal(new[] { "logo" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_ignores_surrounding_whitespace_in_tag_names()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags(" logo ", " brand ");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_into_the_same_tag_in_another_case_changes_nothing()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Brand", "print" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("brand", "BRAND");

        Assert.Equal(0, changed);
        Assert.Equal(new[] { "Brand", "print" }, repository.GetById(asset.Id)!.Tags);
    }
}
