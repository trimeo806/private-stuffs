using AssetLibrary;
using Xunit;

namespace AssetLibrary.Tests;

public class AssetPagerTests
{
    private static List<Asset> MakeAssets(int count) =>
        Enumerable.Range(1, count).Select(i => new Asset(i, $"asset-{i:D4}", i)).ToList();

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
        Assert.Throws<ArgumentOutOfRangeException>(() => AssetPager.GetPage(MakeAssets(3), page, 10));
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-5)]
    [InlineData(101)]
    [InlineData(int.MaxValue)]
    public void Throws_out_of_range_when_page_size_is_outside_one_to_one_hundred(int pageSize)
    {
        Assert.Throws<ArgumentOutOfRangeException>(() => AssetPager.GetPage(MakeAssets(3), 1, pageSize));
    }

    [Theory]
    [InlineData(1, 1, 150)]
    [InlineData(100, 100, 2)]
    public void Accepts_page_size_at_both_limits(int pageSize, int expectedItemCount, int expectedTotalPages)
    {
        var result = AssetPager.GetPage(MakeAssets(150), 1, pageSize);

        Assert.Equal(expectedItemCount, result.Items.Count);
        Assert.Equal(expectedTotalPages, result.TotalPages);
    }

    [Fact]
    public void Returns_empty_page_with_zero_totals_for_empty_list()
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
    [InlineData(9, 10, 1)]
    [InlineData(11, 10, 2)]
    public void Total_pages_rounds_up_to_include_partial_last_page(int count, int pageSize, int expectedTotalPages)
    {
        var result = AssetPager.GetPage(MakeAssets(count), 1, pageSize);

        Assert.Equal(count, result.TotalCount);
        Assert.Equal(expectedTotalPages, result.TotalPages);
    }

    [Fact]
    public void Returns_remaining_items_on_partial_last_page()
    {
        var result = AssetPager.GetPage(MakeAssets(5), 3, 2);

        Assert.Equal(new[] { 5 }, result.Items.Select(a => a.Id));
        Assert.Equal(3, result.Page);
        Assert.Equal(2, result.PageSize);
    }

    [Fact]
    public void Returns_empty_items_with_totals_for_page_after_last()
    {
        var result = AssetPager.GetPage(MakeAssets(5), 4, 2);

        Assert.Empty(result.Items);
        Assert.Equal(5, result.TotalCount);
        Assert.Equal(3, result.TotalPages);
    }

    [Fact]
    public void Orders_by_name_ignoring_case_then_by_id_ascending()
    {
        var assets = new List<Asset>
        {
            new(4, "Banana", 1),
            new(3, "apple", 1),
            new(2, "banana", 1),
            new(1, "Cherry", 1),
            new(5, "APPLE", 1),
        };

        var result = AssetPager.GetPage(assets, 1, 10);

        Assert.Equal(new[] { 3, 5, 2, 4, 1 }, result.Items.Select(a => a.Id));
    }

    [Fact]
    public void Does_not_modify_input_list()
    {
        var assets = new List<Asset>
        {
            new(2, "zeta", 1),
            new(1, "alpha", 1),
            new(3, "mid", 1),
        };
        var snapshot = assets.ToList();

        AssetPager.GetPage(assets, 1, 2);

        Assert.Equal(snapshot, assets);
    }
}
