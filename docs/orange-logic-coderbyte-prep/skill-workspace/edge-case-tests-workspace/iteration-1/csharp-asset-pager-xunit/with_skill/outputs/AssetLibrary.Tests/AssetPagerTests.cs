using AssetLibrary;
using Xunit;

namespace AssetLibrary.Tests;

public class AssetPagerTests
{
    [Fact]
    public void Returns_first_page_in_name_order()
    {
        var assets = new List<Asset>
        {
            new(1, "beta.png", 10),
            new(2, "alpha.png", 20),
            new(3, "gamma.png", 30),
        };

        var result = AssetPager.GetPage(assets, 1, 2);

        Assert.Equal(new[] { "alpha.png", "beta.png" }, result.Items.Select(a => a.Name));
    }

    private static List<Asset> MakeAssets(int count) =>
        Enumerable.Range(1, count).Select(i => new Asset(i, $"asset-{i:D4}.png", i)).ToList();

    [Fact]
    public void Throws_when_assets_is_null()
    {
        var ex = Assert.Throws<ArgumentNullException>(() => AssetPager.GetPage(null!, 1, 10));

        Assert.Equal("assets", ex.ParamName);
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-1)]
    [InlineData(int.MinValue)]
    public void Throws_when_page_is_below_one(int page)
    {
        var ex = Assert.Throws<ArgumentOutOfRangeException>(() => AssetPager.GetPage(MakeAssets(3), page, 10));

        Assert.Equal("page", ex.ParamName);
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-1)]
    [InlineData(101)]
    [InlineData(int.MaxValue)]
    public void Throws_when_page_size_is_outside_one_to_one_hundred(int pageSize)
    {
        var ex = Assert.Throws<ArgumentOutOfRangeException>(() => AssetPager.GetPage(MakeAssets(3), 1, pageSize));

        Assert.Equal("pageSize", ex.ParamName);
    }

    [Theory]
    [InlineData(1, 1)]
    [InlineData(100, 100)]
    public void Accepts_page_size_at_both_limits(int pageSize, int expectedItems)
    {
        var result = AssetPager.GetPage(MakeAssets(150), 1, pageSize);

        Assert.Equal(expectedItems, result.Items.Count);
        Assert.Equal(pageSize, result.PageSize);
    }

    [Theory]
    [InlineData(0, 10, 0)]
    [InlineData(1, 10, 1)]
    [InlineData(9, 10, 1)]
    [InlineData(10, 10, 1)]
    [InlineData(11, 10, 2)]
    [InlineData(250, 100, 3)]
    [InlineData(7, 1, 7)]
    public void Computes_total_pages_rounding_up(int count, int pageSize, int expectedTotalPages)
    {
        var result = AssetPager.GetPage(MakeAssets(count), 1, pageSize);

        Assert.Equal(count, result.TotalCount);
        Assert.Equal(expectedTotalPages, result.TotalPages);
    }

    [Fact]
    public void Returns_empty_result_for_empty_assets()
    {
        var result = AssetPager.GetPage(new List<Asset>(), 1, 10);

        Assert.Empty(result.Items);
        Assert.Equal(0, result.TotalCount);
        Assert.Equal(0, result.TotalPages);
    }

    [Fact]
    public void Returns_only_remaining_items_on_last_partial_page()
    {
        var result = AssetPager.GetPage(MakeAssets(11), 3, 5);

        Assert.Equal(new[] { 11 }, result.Items.Select(a => a.Id));
        Assert.Equal(3, result.Page);
        Assert.Equal(3, result.TotalPages);
    }

    [Theory]
    [InlineData(3)]
    [InlineData(4)]
    [InlineData(1000)]
    [InlineData(int.MaxValue)]
    public void Returns_empty_items_with_totals_when_page_is_past_the_end(int page)
    {
        var result = AssetPager.GetPage(MakeAssets(4), page, 2);

        Assert.Empty(result.Items);
        Assert.Equal(page, result.Page);
        Assert.Equal(4, result.TotalCount);
        Assert.Equal(2, result.TotalPages);
    }

    [Fact]
    public void Orders_names_case_insensitively()
    {
        var assets = new List<Asset>
        {
            new(1, "zeta.png", 1),
            new(2, "Beta.png", 1),
            new(3, "alpha.png", 1),
            new(4, "Gamma.png", 1),
        };

        var result = AssetPager.GetPage(assets, 1, 10);

        Assert.Equal(new[] { "alpha.png", "Beta.png", "Gamma.png", "zeta.png" }, result.Items.Select(a => a.Name));
    }

    [Fact]
    public void Breaks_name_ties_by_id_ascending_across_case_variants()
    {
        var assets = new List<Asset>
        {
            new(30, "logo.png", 1),
            new(10, "LOGO.png", 1),
            new(20, "Logo.png", 1),
            new(5, "banner.png", 1),
        };

        var firstPage = AssetPager.GetPage(assets, 1, 2);
        var secondPage = AssetPager.GetPage(assets, 2, 2);

        Assert.Equal(new[] { 5, 10 }, firstPage.Items.Select(a => a.Id));
        Assert.Equal(new[] { 20, 30 }, secondPage.Items.Select(a => a.Id));
    }

    [Fact]
    public void Does_not_modify_the_input_list()
    {
        var assets = new List<Asset>
        {
            new(3, "gamma.png", 1),
            new(1, "alpha.png", 1),
            new(2, "beta.png", 1),
        };
        var snapshot = assets.ToList();

        AssetPager.GetPage(assets, 1, 2);

        Assert.Equal(snapshot, assets);
    }
}
