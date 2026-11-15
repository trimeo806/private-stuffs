using AssetLibrary;
using Xunit;

namespace AssetLibrary.Tests;

public class AssetPagerEdgeCaseTests
{
    private static List<Asset> MakeAssets(int count) =>
        Enumerable.Range(1, count).Select(i => new Asset(i, $"asset-{i:D4}.png", i)).ToList();

    [Fact]
    public void Throws_argument_null_when_assets_is_null()
    {
        var ex = Assert.Throws<ArgumentNullException>(() => AssetPager.GetPage(null!, 1, 10));
        Assert.Equal("assets", ex.ParamName);
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-1)]
    [InlineData(int.MinValue)]
    public void Throws_out_of_range_when_page_is_below_one(int page)
    {
        var ex = Assert.Throws<ArgumentOutOfRangeException>(() => AssetPager.GetPage(MakeAssets(3), page, 10));
        Assert.Equal("page", ex.ParamName);
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-1)]
    [InlineData(101)]
    [InlineData(int.MaxValue)]
    public void Throws_out_of_range_when_page_size_is_outside_one_to_one_hundred(int pageSize)
    {
        var ex = Assert.Throws<ArgumentOutOfRangeException>(() => AssetPager.GetPage(MakeAssets(3), 1, pageSize));
        Assert.Equal("pageSize", ex.ParamName);
    }

    [Theory]
    [InlineData(1, 1)]
    [InlineData(100, 100)]
    public void Accepts_page_size_at_the_limits(int pageSize, int expectedItems)
    {
        var result = AssetPager.GetPage(MakeAssets(150), 1, pageSize);

        Assert.Equal(expectedItems, result.Items.Count);
    }

    [Theory]
    [InlineData(0, 10, 0)]
    [InlineData(10, 5, 2)]
    [InlineData(11, 5, 3)]
    [InlineData(1, 100, 1)]
    [InlineData(5, 10, 1)]
    public void Total_pages_is_the_count_divided_by_page_size_rounded_up(int count, int pageSize, int expectedTotalPages)
    {
        var result = AssetPager.GetPage(MakeAssets(count), 1, pageSize);

        Assert.Equal(count, result.TotalCount);
        Assert.Equal(expectedTotalPages, result.TotalPages);
    }

    [Fact]
    public void Returns_the_remaining_items_on_the_last_partial_page()
    {
        var result = AssetPager.GetPage(MakeAssets(5), 3, 2);

        Assert.Equal(new[] { 5 }, result.Items.Select(a => a.Id));
    }

    [Fact]
    public void Returns_empty_items_with_totals_when_page_is_past_the_end()
    {
        var result = AssetPager.GetPage(MakeAssets(4), 3, 2);

        Assert.Empty(result.Items);
        Assert.Equal(3, result.Page);
        Assert.Equal(4, result.TotalCount);
        Assert.Equal(2, result.TotalPages);
    }

    [Theory]
    [InlineData(int.MaxValue, 2)]
    [InlineData(int.MaxValue, 100)]
    [InlineData(21474837, 100)]
    public void Returns_empty_items_when_page_number_is_huge(int page, int pageSize)
    {
        var result = AssetPager.GetPage(MakeAssets(5), page, pageSize);

        Assert.Empty(result.Items);
    }

    [Fact]
    public void Orders_names_case_insensitively()
    {
        var assets = new List<Asset>
        {
            new(1, "beta.png", 1),
            new(2, "Alpha.png", 1),
            new(3, "charlie.png", 1),
        };

        var result = AssetPager.GetPage(assets, 1, 10);

        Assert.Equal(new[] { "Alpha.png", "beta.png", "charlie.png" }, result.Items.Select(a => a.Name));
    }

    [Fact]
    public void Breaks_name_ties_that_differ_only_in_case_by_id_ascending()
    {
        var assets = new List<Asset>
        {
            new(3, "logo.png", 1),
            new(1, "LOGO.png", 1),
            new(2, "Logo.png", 1),
        };

        var result = AssetPager.GetPage(assets, 1, 10);

        Assert.Equal(new[] { 1, 2, 3 }, result.Items.Select(a => a.Id));
    }

    [Fact]
    public void Does_not_modify_the_input_list()
    {
        var assets = new List<Asset>
        {
            new(2, "beta.png", 1),
            new(1, "alpha.png", 1),
        };
        var before = assets.ToList();

        AssetPager.GetPage(assets, 1, 10);

        Assert.Equal(before, assets);
    }
}
