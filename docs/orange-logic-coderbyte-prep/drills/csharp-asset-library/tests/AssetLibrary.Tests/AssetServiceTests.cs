using Xunit;

namespace AssetLibrary.Tests;

public class AssetServiceTests
{
    private static readonly DateTime BaseTime = new(2026, 1, 1, 0, 0, 0, DateTimeKind.Utc);

    private static AssetService NewService() => new(new InMemoryAssetRepository());

    [Fact]
    public void Upload_assigns_incrementing_ids()
    {
        var service = NewService();

        var first = service.Upload("a.jpg", 10, "aa11", new[] { "x" }, BaseTime);
        var second = service.Upload("b.jpg", 20, "bb22", new[] { "y" }, BaseTime);

        Assert.Equal(1, first.Id);
        Assert.Equal(2, second.Id);
    }

    [Fact]
    public void Upload_rejects_an_existing_checksum()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", Array.Empty<string>(), BaseTime);

        var error = Assert.Throws<DuplicateAssetException>(
            () => service.Upload("copy.jpg", 10, "aa11", Array.Empty<string>(), BaseTime));

        Assert.Equal(1, error.ExistingId);
    }

    [Fact]
    public void Upload_stores_tags_lowercased_and_distinct()
    {
        var service = NewService();

        var asset = service.Upload("a.jpg", 10, "aa11", new[] { "Beach", "beach", "SUNSET" }, BaseTime);

        Assert.Equal(new[] { "beach", "sunset" }, asset.Tags.OrderBy(t => t));
    }

    [Fact]
    public void Search_with_match_any_returns_assets_having_any_tag()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach" }, BaseTime);
        service.Upload("b.jpg", 10, "bb22", new[] { "sunset" }, BaseTime);
        service.Upload("c.jpg", 10, "cc33", new[] { "city" }, BaseTime);

        var result = service.SearchByTags(new[] { "beach", "sunset" }, matchAll: false);

        Assert.Equal(new[] { 1, 2 }, result.Select(a => a.Id));
    }

    [Fact]
    public void Search_with_match_all_returns_only_assets_having_every_tag()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach", "sunset" }, BaseTime);
        service.Upload("b.jpg", 10, "bb22", new[] { "beach" }, BaseTime);

        var result = service.SearchByTags(new[] { "beach", "sunset" }, matchAll: true);

        Assert.Equal(new[] { 1 }, result.Select(a => a.Id));
    }

    [Fact]
    public void First_page_contains_the_newest_assets()
    {
        var service = NewService();
        for (var i = 0; i < 5; i++)
        {
            service.Upload($"f{i}.jpg", 10, $"c{i}", Array.Empty<string>(), BaseTime.AddMinutes(i));
        }

        var page = service.GetPage(1, 2);

        Assert.Equal(new[] { 5, 4 }, page.Items.Select(a => a.Id));
    }

    [Fact]
    public void Total_pages_rounds_up_for_a_partial_last_page()
    {
        var service = NewService();
        for (var i = 0; i < 5; i++)
        {
            service.Upload($"f{i}.jpg", 10, $"c{i}", Array.Empty<string>(), BaseTime.AddMinutes(i));
        }

        var page = service.GetPage(1, 2);

        Assert.Equal(5, page.TotalCount);
        Assert.Equal(3, page.TotalPages);
    }

    [Fact]
    public void Storage_report_sums_sizes_and_groups_by_extension()
    {
        var service = NewService();
        service.Upload("a.jpg", 100, "aa11", Array.Empty<string>(), BaseTime);
        service.Upload("b.png", 300, "bb22", Array.Empty<string>(), BaseTime);
        service.Upload("c.jpg", 50, "cc33", Array.Empty<string>(), BaseTime);

        var report = service.GetStorageReport(topN: 1);

        Assert.Equal(450, report.TotalBytes);
        Assert.Equal(3, report.AssetCount);
        Assert.Equal(
            new[] { new ExtensionUsage("png", 1, 300), new ExtensionUsage("jpg", 2, 150) },
            report.ByExtension);
        Assert.Equal(new[] { 2 }, report.Largest.Select(a => a.Id));
    }
}
