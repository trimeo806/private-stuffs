using AssetLibrary.Models;
using AssetLibrary.Repositories;
using AssetLibrary.Services;
using Xunit;

namespace AssetLibrary.Tests;

public class MergeTagsTests
{
    [Fact]
    public void Replaces_the_source_tag_with_the_target_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "draft" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("draft", "final");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "final" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void Keeps_the_other_tags_and_their_order()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "brand", "draft", "print" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        service.MergeTags("draft", "final");

        Assert.Equal(new[] { "brand", "final", "print" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void Matches_the_source_tag_case_insensitively()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Draft" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("DRAFT", "final");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "final" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void Keeps_a_single_copy_when_the_asset_already_has_the_target_tag_in_another_case()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Final", "draft" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("draft", "final");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "Final" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void Removes_every_copy_of_the_source_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "draft", "Draft" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("draft", "final");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "final" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void Counts_only_the_assets_that_had_the_source_tag()
    {
        var first = new Asset(Guid.NewGuid(), "a.png", new[] { "draft" });
        var second = new Asset(Guid.NewGuid(), "b.png", new[] { "draft", "final" });
        var onlyTarget = new Asset(Guid.NewGuid(), "c.png", new[] { "final" });
        var untagged = new Asset(Guid.NewGuid(), "d.png", new[] { "brand" });
        var repository = new InMemoryAssetRepository(first, second, onlyTarget, untagged);
        var service = new TagService(repository);

        var changed = service.MergeTags("draft", "final");

        Assert.Equal(2, changed);
        Assert.Equal(new[] { "final" }, repository.GetById(first.Id)!.Tags);
        Assert.Equal(new[] { "final" }, repository.GetById(second.Id)!.Tags);
        Assert.Equal(new[] { "final" }, repository.GetById(onlyTarget.Id)!.Tags);
        Assert.Equal(new[] { "brand" }, repository.GetById(untagged.Id)!.Tags);
    }

    [Fact]
    public void Returns_zero_when_no_asset_has_the_source_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "brand" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        Assert.Equal(0, service.MergeTags("draft", "final"));
        Assert.Equal(new[] { "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void Trims_the_tag_names()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "draft" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags(" draft ", " final ");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "final" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void Changes_nothing_when_source_and_target_are_the_same_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Brand" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        Assert.Equal(0, service.MergeTags("brand", "BRAND"));
        Assert.Equal(new[] { "Brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Theory]
    [InlineData(null)]
    [InlineData("")]
    [InlineData("   ")]
    public void Throws_when_the_source_tag_is_blank(string? sourceTag)
    {
        var service = new TagService(new InMemoryAssetRepository());

        var error = Assert.Throws<ArgumentException>(() => service.MergeTags(sourceTag!, "final"));
        Assert.Equal("sourceTag", error.ParamName);
    }

    [Theory]
    [InlineData(null)]
    [InlineData("")]
    [InlineData("   ")]
    public void Throws_when_the_target_tag_is_blank(string? targetTag)
    {
        var service = new TagService(new InMemoryAssetRepository());

        var error = Assert.Throws<ArgumentException>(() => service.MergeTags("draft", targetTag!));
        Assert.Equal("targetTag", error.ParamName);
    }
}
