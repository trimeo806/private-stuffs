using Xunit;

namespace AssetLibrary.Tests;

public class StorageReportTests
{
    private static readonly DateTime BaseTime = new(2026, 1, 1, 0, 0, 0, DateTimeKind.Utc);

    private static AssetService NewService() => new(new InMemoryAssetRepository());

    private static Asset NewAsset(int id, string fileName, long sizeBytes) =>
        new(id, fileName, sizeBytes, $"c{id}", Array.Empty<string>(), BaseTime);

    [Theory]
    [InlineData("backup.tar.gz", "gz")]
    [InlineData("PHOTO.JPG", "jpg")]
    [InlineData("README", "(none)")]
    [InlineData("draft.", "(none)")]
    [InlineData(".gitignore", "(none)")]
    public void Extension_is_the_lowercased_last_segment_or_none(string fileName, string expected)
    {
        Assert.Equal(expected, StorageReportCalculator.ExtensionOf(fileName));
    }

    [Fact]
    public void Negative_top_n_is_rejected()
    {
        var service = NewService();

        var error = Assert.Throws<ArgumentOutOfRangeException>(() => service.GetStorageReport(topN: -1));

        Assert.Equal("topN", error.ParamName);
    }

    [Theory]
    [InlineData(0, new int[0])]
    [InlineData(99, new[] { 2, 3, 1 })]
    public void Largest_holds_at_most_top_n_assets_by_size(int topN, int[] expectedIds)
    {
        var service = NewService();
        service.Upload("a.jpg", 100, "aa11", Array.Empty<string>(), BaseTime);
        service.Upload("b.jpg", 300, "bb22", Array.Empty<string>(), BaseTime);
        service.Upload("c.jpg", 200, "cc33", Array.Empty<string>(), BaseTime);

        var report = service.GetStorageReport(topN);

        Assert.Equal(expectedIds, report.Largest.Select(a => a.Id));
    }

    [Fact]
    public void Total_bytes_can_exceed_int_max_value()
    {
        var service = NewService();
        service.Upload("a.bin", 2_000_000_000, "aa11", Array.Empty<string>(), BaseTime);
        service.Upload("b.bin", 2_000_000_000, "bb22", Array.Empty<string>(), BaseTime);

        var report = service.GetStorageReport(topN: 0);

        Assert.Equal(4_000_000_000L, report.TotalBytes);
        Assert.Equal(new[] { new ExtensionUsage("bin", 2, 4_000_000_000L) }, report.ByExtension);
    }

    [Fact]
    public void Extensions_with_equal_bytes_are_ordered_by_extension()
    {
        var service = NewService();
        service.Upload("a.png", 100, "aa11", Array.Empty<string>(), BaseTime);
        service.Upload("b.gif", 100, "bb22", Array.Empty<string>(), BaseTime);
        service.Upload("c.jpg", 100, "cc33", Array.Empty<string>(), BaseTime);

        var report = service.GetStorageReport(topN: 0);

        Assert.Equal(new[] { "gif", "jpg", "png" }, report.ByExtension.Select(row => row.Extension));
    }

    [Fact]
    public void Extensions_differing_only_in_case_share_one_row()
    {
        var service = NewService();
        service.Upload("a.JPG", 100, "aa11", Array.Empty<string>(), BaseTime);
        service.Upload("b.jpg", 50, "bb22", Array.Empty<string>(), BaseTime);

        var report = service.GetStorageReport(topN: 0);

        Assert.Equal(new[] { new ExtensionUsage("jpg", 2, 150) }, report.ByExtension);
    }

    [Fact]
    public void Largest_breaks_size_ties_by_file_name_ordinal()
    {
        var service = NewService();
        service.Upload("a.jpg", 100, "aa11", Array.Empty<string>(), BaseTime);
        service.Upload("B.jpg", 100, "bb22", Array.Empty<string>(), BaseTime);

        var report = service.GetStorageReport(topN: 2);

        Assert.Equal(new[] { "B.jpg", "a.jpg" }, report.Largest.Select(a => a.FileName));
    }

    [Fact]
    public void Largest_breaks_size_and_name_ties_by_id()
    {
        var assets = new[] { NewAsset(2, "same.jpg", 100), NewAsset(1, "same.jpg", 100) };

        var report = StorageReportCalculator.Build(assets, topN: 2);

        Assert.Equal(new[] { 1, 2 }, report.Largest.Select(a => a.Id));
    }

    [Fact]
    public void Empty_library_reports_zeros_and_empty_lists()
    {
        var service = NewService();

        var report = service.GetStorageReport(topN: 5);

        Assert.Equal(0, report.TotalBytes);
        Assert.Equal(0, report.AssetCount);
        Assert.Empty(report.ByExtension);
        Assert.Empty(report.Largest);
    }
}
