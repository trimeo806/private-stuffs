using Xunit;

namespace AssetLibrary.Tests;

public class TagNormalizerTests
{
    private static readonly DateTime BaseTime = new(2026, 1, 1, 0, 0, 0, DateTimeKind.Utc);

    [Fact]
    public void Normalize_trims_and_lowercases_tags()
    {
        var result = TagNormalizer.Normalize(new[] { " Beach ", "SUNSET\t" });

        Assert.Equal(new[] { "beach", "sunset" }, result);
    }

    [Theory]
    [InlineData("")]
    [InlineData("   ")]
    [InlineData("\t")]
    [InlineData(null)]
    public void Normalize_drops_blank_whitespace_and_null_tags(string? blank)
    {
        var result = TagNormalizer.Normalize(new[] { "beach", blank! });

        Assert.Equal(new[] { "beach" }, result);
    }

    [Fact]
    public void Normalize_treats_tags_differing_only_by_case_or_padding_as_one()
    {
        var result = TagNormalizer.Normalize(new[] { " Beach", "beach ", "BEACH" });

        Assert.Equal(new[] { "beach" }, result);
    }

    [Fact]
    public void Normalize_returns_empty_for_a_null_collection()
    {
        var result = TagNormalizer.Normalize(null!);

        Assert.Empty(result);
    }

    [Fact]
    public void Upload_stores_only_the_trimmed_non_blank_tags()
    {
        var service = new AssetService(new InMemoryAssetRepository());

        var asset = service.Upload("a.jpg", 10, "aa11", new[] { " Beach ", "  ", "" }, BaseTime);

        Assert.Equal(new[] { "beach" }, asset.Tags);
    }
}
