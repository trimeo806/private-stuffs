using Xunit;

namespace AssetLibrary.GraderTests;

public class GraderTests
{
    private static readonly DateTime BaseTime = new(2026, 1, 1, 0, 0, 0, DateTimeKind.Utc);

    private static AssetService NewService() => new(new InMemoryAssetRepository());

    private static AssetService ServiceWith(int count)
    {
        var service = NewService();
        for (var i = 0; i < count; i++)
        {
            service.Upload($"f{i}.jpg", 10, $"c{i}", Array.Empty<string>(), BaseTime.AddMinutes(i));
        }

        return service;
    }

    [Fact]
    public void Duplicate_checksum_is_rejected_regardless_of_case()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "ABCDEF", Array.Empty<string>(), BaseTime);

        Assert.Throws<DuplicateAssetException>(
            () => service.Upload("b.jpg", 10, "abcdef", Array.Empty<string>(), BaseTime));
    }

    [Theory]
    [InlineData("")]
    [InlineData("   ")]
    public void Blank_checksum_is_rejected(string checksum)
    {
        var service = NewService();

        Assert.Throws<ArgumentException>(
            () => service.Upload("a.jpg", 10, checksum, Array.Empty<string>(), BaseTime));
    }

    [Fact]
    public void Tags_are_trimmed_and_blank_tags_dropped()
    {
        var service = NewService();

        var asset = service.Upload("a.jpg", 10, "aa", new[] { " Beach ", "beach", "   ", "" }, BaseTime);

        Assert.Equal(new[] { "beach" }, asset.Tags);
    }

    [Fact]
    public void Search_normalizes_query_tags()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa", new[] { "beach", "sunset" }, BaseTime);

        var result = service.SearchByTags(new[] { "  BEACH", "Sunset " }, matchAll: true);

        Assert.Equal(new[] { 1 }, result.Select(a => a.Id));
    }

    [Theory]
    [InlineData(true)]
    [InlineData(false)]
    public void Search_with_only_blank_tags_returns_nothing(bool matchAll)
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa", new[] { "beach" }, BaseTime);

        Assert.Empty(service.SearchByTags(new[] { " ", "" }, matchAll));
    }

    [Fact]
    public void Last_partial_page_returns_remaining_items()
    {
        var page = ServiceWith(5).GetPage(3, 2);

        Assert.Equal(new[] { 1 }, page.Items.Select(a => a.Id));
        Assert.Equal(3, page.TotalPages);
    }

    [Fact]
    public void Page_beyond_last_is_empty_with_correct_totals()
    {
        var page = ServiceWith(5).GetPage(4, 2);

        Assert.Empty(page.Items);
        Assert.Equal(5, page.TotalCount);
        Assert.Equal(3, page.TotalPages);
    }

    [Fact]
    public void Exact_multiple_of_page_size_does_not_add_a_page()
    {
        Assert.Equal(2, ServiceWith(4).GetPage(1, 2).TotalPages);
    }

    [Fact]
    public void Empty_library_has_zero_pages()
    {
        var page = NewService().GetPage(1, 10);

        Assert.Empty(page.Items);
        Assert.Equal(0, page.TotalPages);
    }

    [Fact]
    public void Assets_uploaded_at_same_time_are_ordered_by_id()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa", Array.Empty<string>(), BaseTime);
        service.Upload("b.jpg", 10, "bb", Array.Empty<string>(), BaseTime);
        service.Upload("c.jpg", 10, "cc", Array.Empty<string>(), BaseTime);

        Assert.Equal(new[] { 1, 2, 3 }, service.GetPage(1, 10).Items.Select(a => a.Id));
    }

    [Fact]
    public void Storage_report_of_empty_library_is_zeroed()
    {
        var report = NewService().GetStorageReport(5);

        Assert.Equal(0, report.TotalBytes);
        Assert.Equal(0, report.AssetCount);
        Assert.Empty(report.ByExtension);
        Assert.Empty(report.Largest);
    }

    [Fact]
    public void Storage_report_normalizes_extensions()
    {
        var service = NewService();
        service.Upload("A.JPG", 10, "a1", Array.Empty<string>(), BaseTime);
        service.Upload("b.jpg", 10, "a2", Array.Empty<string>(), BaseTime);
        service.Upload("README", 5, "a3", Array.Empty<string>(), BaseTime);
        service.Upload("draft.", 5, "a4", Array.Empty<string>(), BaseTime);
        service.Upload(".gitignore", 5, "a5", Array.Empty<string>(), BaseTime);
        service.Upload("backup.tar.gz", 1, "a6", Array.Empty<string>(), BaseTime);

        var report = service.GetStorageReport(0);

        Assert.Equal(
            new[]
            {
                new ExtensionUsage("jpg", 2, 20),
                new ExtensionUsage("(none)", 3, 15),
                new ExtensionUsage("gz", 1, 1),
            },
            report.ByExtension);
    }

    [Fact]
    public void Extensions_with_equal_bytes_are_ordered_alphabetically()
    {
        var service = NewService();
        service.Upload("a.png", 10, "a1", Array.Empty<string>(), BaseTime);
        service.Upload("b.gif", 10, "a2", Array.Empty<string>(), BaseTime);

        Assert.Equal(new[] { "gif", "png" }, service.GetStorageReport(0).ByExtension.Select(e => e.Extension));
    }

    [Fact]
    public void Largest_breaks_size_ties_by_file_name_then_id()
    {
        var service = NewService();
        service.Upload("b.jpg", 50, "a1", Array.Empty<string>(), BaseTime);
        service.Upload("a.jpg", 50, "a2", Array.Empty<string>(), BaseTime);
        service.Upload("a.jpg", 50, "a3", Array.Empty<string>(), BaseTime);
        service.Upload("z.jpg", 99, "a4", Array.Empty<string>(), BaseTime);

        Assert.Equal(new[] { 4, 2, 3, 1 }, service.GetStorageReport(10).Largest.Select(a => a.Id));
    }

    [Fact]
    public void Largest_with_zero_top_n_is_empty()
    {
        var service = ServiceWith(3);

        Assert.Empty(service.GetStorageReport(0).Largest);
    }

    [Fact]
    public void Negative_top_n_is_rejected()
    {
        Assert.Throws<ArgumentOutOfRangeException>(() => ServiceWith(1).GetStorageReport(-1));
    }

    [Fact]
    public void Total_bytes_does_not_overflow_int()
    {
        var service = NewService();
        service.Upload("a.mov", int.MaxValue, "a1", Array.Empty<string>(), BaseTime);
        service.Upload("b.mov", int.MaxValue, "a2", Array.Empty<string>(), BaseTime);

        var report = service.GetStorageReport(1);

        Assert.Equal(2L * int.MaxValue, report.TotalBytes);
        Assert.Equal(2L * int.MaxValue, report.ByExtension.Single().TotalBytes);
    }
}
