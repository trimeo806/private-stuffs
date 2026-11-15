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
    public void MergeTags_replaces_the_source_tag_with_the_target_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "draft", "brand" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("draft", "final");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "final", "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_matches_the_source_tag_case_insensitively()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Draft" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("DRAFT", "final");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "final" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_keeps_a_single_copy_when_the_asset_already_has_the_target_in_another_case()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Final", "draft" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("draft", "final");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "Final" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_removes_every_copy_of_the_source_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "draft", "brand", "DRAFT" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        service.MergeTags("draft", "final");

        Assert.Equal(new[] { "final", "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_returns_the_number_of_changed_assets_and_leaves_the_others_alone()
    {
        var first = new Asset(Guid.NewGuid(), "a.png", new[] { "draft" });
        var second = new Asset(Guid.NewGuid(), "b.png", new[] { "final", "Draft" });
        var untouched = new Asset(Guid.NewGuid(), "c.png", new[] { "brand" });
        var repository = new InMemoryAssetRepository(first, second, untouched);
        var service = new TagService(repository);

        var changed = service.MergeTags("draft", "final");

        Assert.Equal(2, changed);
        Assert.Equal(new[] { "final" }, repository.GetById(first.Id)!.Tags);
        Assert.Equal(new[] { "final" }, repository.GetById(second.Id)!.Tags);
        Assert.Equal(new[] { "brand" }, repository.GetById(untouched.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_returns_zero_when_no_asset_has_the_source_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "brand" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("draft", "final");

        Assert.Equal(0, changed);
        Assert.Equal(new[] { "brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_trims_the_tag_names()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "draft" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags(" draft ", " final ");

        Assert.Equal(1, changed);
        Assert.Equal(new[] { "final" }, repository.GetById(asset.Id)!.Tags);
    }

    [Fact]
    public void MergeTags_is_a_no_op_when_source_and_target_are_the_same_tag()
    {
        var asset = new Asset(Guid.NewGuid(), "logo.png", new[] { "Brand" });
        var repository = new InMemoryAssetRepository(asset);
        var service = new TagService(repository);

        var changed = service.MergeTags("brand", "BRAND");

        Assert.Equal(0, changed);
        Assert.Equal(new[] { "Brand" }, repository.GetById(asset.Id)!.Tags);
    }

    [Theory]
    [InlineData(null, "final")]
    [InlineData("", "final")]
    [InlineData("   ", "final")]
    [InlineData("draft", null)]
    [InlineData("draft", "")]
    [InlineData("draft", "   ")]
    public void MergeTags_throws_for_a_blank_tag_name(string? sourceTag, string? targetTag)
    {
        var service = new TagService(new InMemoryAssetRepository());

        Assert.Throws<ArgumentException>(() => service.MergeTags(sourceTag!, targetTag!));
    }
}
