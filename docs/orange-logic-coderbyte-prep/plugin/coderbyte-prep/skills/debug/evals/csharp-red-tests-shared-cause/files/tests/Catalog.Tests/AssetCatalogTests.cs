using Xunit;
using Catalog;

namespace Catalog.Tests;

public class AssetCatalogTests
{
    private static AssetCatalog FiveAssets()
    {
        var catalog = new AssetCatalog();
        catalog.Add(new Asset(1, "a.png", 100, new DateTime(2026, 1, 2)));
        catalog.Add(new Asset(2, "b.png", 200, new DateTime(2026, 1, 4)));
        catalog.Add(new Asset(3, "c.png", 300, new DateTime(2026, 1, 1)));
        catalog.Add(new Asset(4, "d.png", 400, new DateTime(2026, 1, 5)));
        catalog.Add(new Asset(5, "e.png", 500, new DateTime(2026, 1, 3)));
        return catalog;
    }

    [Fact]
    public void GetPage_FirstPage_ReturnsNewestFirst()
    {
        var page = FiveAssets().GetPage(1, 2);

        Assert.Equal(new[] { 4, 2 }, page.Items.Select(a => a.Id));
    }

    [Fact]
    public void GetPage_SecondPage_ReturnsNextNewest()
    {
        var page = FiveAssets().GetPage(2, 2);

        Assert.Equal(new[] { 5, 1 }, page.Items.Select(a => a.Id));
    }

    [Fact]
    public void GetPage_SameUploadTime_OrdersById()
    {
        var catalog = new AssetCatalog();
        var at = new DateTime(2026, 3, 1);
        catalog.Add(new Asset(9, "x.png", 1, at));
        catalog.Add(new Asset(7, "y.png", 1, at));

        var page = catalog.GetPage(1, 10);

        Assert.Equal(new[] { 7, 9 }, page.Items.Select(a => a.Id));
    }

    [Fact]
    public void GetPage_TotalPages_RoundsUp()
    {
        var page = FiveAssets().GetPage(1, 2);

        Assert.Equal(5, page.TotalCount);
        Assert.Equal(3, page.TotalPages);
    }

    [Fact]
    public void GetPage_EmptyCatalog_HasZeroPages()
    {
        var page = new AssetCatalog().GetPage(1, 2);

        Assert.Empty(page.Items);
        Assert.Equal(0, page.TotalPages);
    }

    [Fact]
    public void GetPage_InvalidPageNumber_Throws()
    {
        Assert.Throws<ArgumentOutOfRangeException>(() => FiveAssets().GetPage(0, 2));
    }

    [Fact]
    public void GetSummary_SumsSizesAndFindsLargest()
    {
        var summary = FiveAssets().GetSummary();

        Assert.Equal(5, summary.Count);
        Assert.Equal(1500, summary.TotalBytes);
        Assert.Equal(500, summary.LargestBytes);
    }

    [Fact]
    public void GetSummary_EmptyCatalog_IsAllZero()
    {
        var summary = new AssetCatalog().GetSummary();

        Assert.Equal(new CatalogSummary(0, 0, 0), summary);
    }
}
