using AssetLibrary;
using Xunit;

namespace AssetLibrary.Tests;

public class AssetPagerTests
{
    private static List<Asset> Assets(int count) =>
        Enumerable.Range(1, count).Select(i => new Asset(i, $"asset-{i:D3}", i)).ToList();

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

    [Fact]
    public void Throws_argument_null_when_assets_is_null()
    {
        Assert.Throws<ArgumentNullException>(() => AssetPager.GetPage(null!, 1, 10));
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-1)]
    [InlineData(int.MinValue)]
    public void Throws_out_of_range_when_page_is_below_one(int page)
    {
        Assert.Throws<ArgumentOutOfRangeException>(() => AssetPager.GetPage(Assets(3), page, 10));
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-1)]
    [InlineData(101)]
    public void Throws_out_of_range_when_page_size_is_outside_one_to_hundred(int pageSize)
    {
        Assert.Throws<ArgumentOutOfRangeException>(() => AssetPager.GetPage(Assets(3), 1, pageSize));
    }

    [Theory]
    [InlineData(1)]
    [InlineData(100)]
    public void Accepts_page_size_at_the_limits(int pageSize)
    {
        var result = AssetPager.GetPage(Assets(150), 1, pageSize);

        Assert.Equal(pageSize, result.Items.Count);
    }

    [Fact]
    public void Returns_zero_totals_and_no_items_for_empty_list()
    {
        var result = AssetPager.GetPage(new List<Asset>(), 1, 10);

        Assert.Empty(result.Items);
        Assert.Equal(0, result.TotalCount);
        Assert.Equal(0, result.TotalPages);
    }

    [Theory]
    [InlineData(1, 10, 1)]
    [InlineData(4, 2, 2)]
    [InlineData(5, 2, 3)]
    [InlineData(101, 100, 2)]
    public void Total_pages_rounds_up_to_cover_every_asset(int count, int pageSize, int expectedPages)
    {
        var result = AssetPager.GetPage(Assets(count), 1, pageSize);

        Assert.Equal(count, result.TotalCount);
        Assert.Equal(expectedPages, result.TotalPages);
    }

    [Fact]
    public void Last_partial_page_holds_the_remaining_assets()
    {
        var result = AssetPager.GetPage(Assets(5), 3, 2);

        Assert.Equal(new[] { 5 }, result.Items.Select(a => a.Id));
    }

    [Fact]
    public void Page_after_the_last_returns_empty_items_with_totals_filled()
    {
        var result = AssetPager.GetPage(Assets(4), 3, 2);

        Assert.Empty(result.Items);
        Assert.Equal(4, result.TotalCount);
        Assert.Equal(2, result.TotalPages);
    }

    [Fact]
    public void Orders_names_case_insensitively()
    {
        var assets = new List<Asset>
        {
            new(1, "beta.png", 1),
            new(2, "Gamma.png", 1),
            new(3, "alpha.png", 1),
            new(4, "Delta.png", 1),
        };

        var result = AssetPager.GetPage(assets, 1, 10);

        Assert.Equal(new[] { 3, 1, 4, 2 }, result.Items.Select(a => a.Id));
    }

    [Fact]
    public void Breaks_name_ties_by_id_ascending_ignoring_case()
    {
        var assets = new List<Asset>
        {
            new(7, "logo.png", 1),
            new(2, "LOGO.png", 1),
            new(5, "Logo.png", 1),
        };

        var result = AssetPager.GetPage(assets, 1, 10);

        Assert.Equal(new[] { 2, 5, 7 }, result.Items.Select(a => a.Id));
    }

    [Fact]
    public void Does_not_modify_the_input_list()
    {
        var assets = new List<Asset>
        {
            new(3, "c.png", 1),
            new(1, "a.png", 1),
            new(2, "b.png", 1),
        };
        var before = assets.ToList();

        AssetPager.GetPage(assets, 1, 2);

        Assert.Equal(before, assets);
    }
}
