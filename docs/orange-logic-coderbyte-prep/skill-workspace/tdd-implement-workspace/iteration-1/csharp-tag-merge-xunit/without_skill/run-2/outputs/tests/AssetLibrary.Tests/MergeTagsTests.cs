using AssetLibrary.Models;
using AssetLibrary.Repositories;
using AssetLibrary.Services;
using Xunit;

namespace AssetLibrary.Tests;

public class MergeTagsTests
{
    [Fact]
    public void MergeTags_replaces_the_source_tag_with_the_target_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo", "brand" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "branding");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "branding", "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_matches_the_source_tag_case_insensitively()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("LOGO", "brand");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_keeps_a_single_copy_when_the_asset_already_has_the_target_in_another_case()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Brand", "logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "Brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_removes_every_case_variant_of_the_source_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo", "LOGO", "web" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "brand", "web" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_counts_only_assets_that_had_the_source_tag()
    {
        var tagged = new Asset(Guid.NewGuid(), "a.png", new[] { "logo" });
        var alsoTagged = new Asset(Guid.NewGuid(), "b.png", new[] { "Logo", "brand" });
        var untouched = new Asset(Guid.NewGuid(), "c.png", new[] { "brand" });
        var repository = new InMemoryAssetRepository(tagged, alsoTagged, untouched);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(2, changed);
        Assert.Equal(new[] { "brand" }, repository.GetById(tagged.Id)!.Tags);
        Assert.Equal(new[] { "brand" }, repository.GetById(alsoTagged.Id)!.Tags);
        Assert.Equal(new[] { "brand" }, repository.GetById(untouched.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_returns_zero_when_no_asset_has_the_source_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "brand" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("logo", "brand");

        Assert.Equal(0, changed);
        Assert.Equal(new[] { "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_trims_tag_names_like_AddTag_does()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "logo" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags(" logo ", " brand ");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_changes_nothing_when_source_and_target_differ_only_in_case()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Brand" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("brand", "BRAND");

        Assert.Equal(0, changed);
        Assert.Equal(new[] { "Brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Theory]
    [InlineData(null)]
    [InlineData("")]
    [InlineData("   ")]
    public void MergeTags_throws_for_a_blank_source_tag(string? sourceTag)
    {
        var service = new TagService(new InMemoryAssetRepository());

        Assert.Throws<ArgumentException>(() => service.MergeTags(sourceTag!, "brand"));
    }

    [Theory]
    [InlineData(null)]
    [InlineData("")]
    [InlineData("   ")]
    public void MergeTags_throws_for_a_blank_target_tag(string? targetTag)
    {
        var service = new TagService(new InMemoryAssetRepository());

        Assert.Throws<ArgumentException>(() => service.MergeTags("logo", targetTag!));
    }
}
