using Xunit;

namespace AssetLibrary.Tests;

public class PagingTests
{
    private static readonly DateTime BaseTime = new(2026, 1, 1, 0, 0, 0, DateTimeKind.Utc);

    private static AssetService ServiceWithAssets(int count)
    {
        var service = new AssetService(new InMemoryAssetRepository());
        for (var i = 0; i < count; i++)
        {
            service.Upload($"f{i}.jpg", 10, $"c{i}", Array.Empty<string>(), BaseTime.AddMinutes(i));
        }

        return service;
    }

    [Theory]
    [InlineData(5, 3, 2, new[] { 1 }, 3)]
    [InlineData(4, 2, 2, new[] { 2, 1 }, 2)]
    [InlineData(3, 1, 10, new[] { 3, 2, 1 }, 1)]
    public void Page_returns_its_slice_of_newest_first_assets_with_rounded_up_total_pages(
        int assetCount, int pageNumber, int pageSize, int[] expectedIds, int expectedTotalPages)
    {
        var service = ServiceWithAssets(assetCount);

        var page = service.GetPage(pageNumber, pageSize);

        Assert.Equal(expectedIds, page.Items.Select(a => a.Id));
        Assert.Equal(pageNumber, page.PageNumber);
        Assert.Equal(pageSize, page.PageSize);
        Assert.Equal(assetCount, page.TotalCount);
        Assert.Equal(expectedTotalPages, page.TotalPages);
    }

    [Theory]
    [InlineData(4, 2)]
    [InlineData(int.MaxValue, 2)]
    public void Page_past_the_end_returns_no_items_but_correct_totals(int pageNumber, int pageSize)
    {
        var service = ServiceWithAssets(5);

        var page = service.GetPage(pageNumber, pageSize);

        Assert.Empty(page.Items);
        Assert.Equal(5, page.TotalCount);
        Assert.Equal(3, page.TotalPages);
    }

    [Fact]
    public void Empty_library_has_no_items_and_zero_total_pages()
    {
        var service = ServiceWithAssets(0);

        var page = service.GetPage(1, 10);

        Assert.Empty(page.Items);
        Assert.Equal(0, page.TotalCount);
        Assert.Equal(0, page.TotalPages);
    }

    [Fact]
    public void Assets_uploaded_at_the_same_time_are_ordered_by_id()
    {
        var service = new AssetService(new InMemoryAssetRepository());
        service.Upload("a.jpg", 10, "aa11", Array.Empty<string>(), BaseTime);
        service.Upload("b.jpg", 10, "bb22", Array.Empty<string>(), BaseTime);
        service.Upload("c.jpg", 10, "cc33", Array.Empty<string>(), BaseTime.AddMinutes(1));
        service.Upload("d.jpg", 10, "dd44", Array.Empty<string>(), BaseTime);

        var page = service.GetPage(1, 4);

        Assert.Equal(new[] { 3, 1, 2, 4 }, page.Items.Select(a => a.Id));
    }

    [Fact]
    public void Maximum_page_size_fits_every_asset_on_one_page()
    {
        var service = ServiceWithAssets(5);

        var page = service.GetPage(1, int.MaxValue);

        Assert.Equal(new[] { 5, 4, 3, 2, 1 }, page.Items.Select(a => a.Id));
        Assert.Equal(1, page.TotalPages);
    }
}
