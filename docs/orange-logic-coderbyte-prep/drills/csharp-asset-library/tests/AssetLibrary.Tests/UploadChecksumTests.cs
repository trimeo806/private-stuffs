using Xunit;

namespace AssetLibrary.Tests;

public class UploadChecksumTests
{
    private static readonly DateTime BaseTime = new(2026, 1, 1, 0, 0, 0, DateTimeKind.Utc);

    [Theory]
    [InlineData("aa11", "AA11")]
    [InlineData("AA11", "aa11")]
    [InlineData("aa11", "Aa11")]
    public void Upload_rejects_a_checksum_that_differs_only_in_case(string storedChecksum, string incomingChecksum)
    {
        var service = new AssetService(new InMemoryAssetRepository());
        service.Upload("a.jpg", 10, storedChecksum, Array.Empty<string>(), BaseTime);

        var error = Assert.Throws<DuplicateAssetException>(
            () => service.Upload("copy.jpg", 10, incomingChecksum, Array.Empty<string>(), BaseTime));

        Assert.Equal(1, error.ExistingId);
        Assert.Equal(incomingChecksum, error.Checksum);
    }

    [Fact]
    public void Upload_does_not_store_a_rejected_case_variant_duplicate()
    {
        var repository = new InMemoryAssetRepository();
        var service = new AssetService(repository);
        service.Upload("a.jpg", 10, "aa11", Array.Empty<string>(), BaseTime);

        Assert.Throws<DuplicateAssetException>(
            () => service.Upload("copy.jpg", 10, "AA11", Array.Empty<string>(), BaseTime));

        var stored = Assert.Single(repository.All());
        Assert.Equal("a.jpg", stored.FileName);
    }

    [Theory]
    [InlineData(null)]
    [InlineData("")]
    [InlineData("   ")]
    public void Upload_rejects_a_blank_checksum_and_stores_nothing(string? checksum)
    {
        var repository = new InMemoryAssetRepository();
        var service = new AssetService(repository);

        var error = Assert.Throws<ArgumentException>(
            () => service.Upload("a.jpg", 10, checksum!, Array.Empty<string>(), BaseTime));

        Assert.Equal("checksum", error.ParamName);
        Assert.Empty(repository.All());
    }

    [Fact]
    public void Upload_reports_a_blank_checksum_before_a_negative_size()
    {
        var service = new AssetService(new InMemoryAssetRepository());

        var error = Assert.Throws<ArgumentException>(
            () => service.Upload("a.jpg", -1, "   ", Array.Empty<string>(), BaseTime));

        Assert.Equal("checksum", error.ParamName);
    }

    [Fact]
    public void Upload_keeps_the_checksum_casing_it_was_given()
    {
        var repository = new InMemoryAssetRepository();
        var service = new AssetService(repository);

        var asset = service.Upload("a.jpg", 10, "AbC123", Array.Empty<string>(), BaseTime);

        Assert.Equal("AbC123", asset.Checksum);
        Assert.Equal("AbC123", repository.FindById(asset.Id)!.Checksum);
    }
}
