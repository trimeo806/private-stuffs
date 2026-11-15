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
}
