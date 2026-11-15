I added 10 new edge-case tests (19 new cases), and they found a real bug. **`TotalPages` is wrong whenever the last page is only partly full.** Hidden tests will almost certainly check this.

## The bug (fix this first)

`AssetLibrary/AssetPager.cs`:

```csharp
var totalPages = ordered.Count / pageSize;
```

Integer division rounds down. With 5 assets and a page size of 2 you get 2 pages, but 3 are needed. With 1 asset and a page size of 10 you get 0 pages, but 1 is needed. The fix is ceiling division:

```csharp
var totalPages = (ordered.Count + pageSize - 1) / pageSize;
```

This still gives 0 for an empty list, and `pageSize` is capped at 100, so it can't overflow. I didn't change production code (the skill only writes tests), so this one-line edit is yours to make.

## Edge cases checked against TASK.md

| # | Case | Input | Expected | Source |
|---|---|---|---|---|
| 1 | null list | `assets = null` | `ArgumentNullException` | explicit |
| 2 | page below 1 | page 0, -1, `int.MinValue` | `ArgumentOutOfRangeException` | explicit |
| 3 | pageSize out of range | 0, -5, 101, `int.MaxValue` | `ArgumentOutOfRangeException` | explicit |
| 4 | pageSize exactly at limits | 1 and 100 (150 assets) | accepted, 1 / 100 items | explicit |
| 5 | empty list | `[]`, page 1 | empty Items, TotalCount 0, TotalPages 0 | explicit |
| 6 | partial last page counts | 5 items, size 2 | TotalPages 3 | explicit |
| 7 | exact multiple | 4 items, size 2 | TotalPages 2 | explicit |
| 8 | one item | 1 item, size 10 | TotalPages 1 | explicit |
| 9 | one over max page size | 101 items, size 100 | TotalPages 2 | explicit |
| 10 | partial last page items | 5 items, page 3, size 2 | only item 5 | explicit |
| 11 | page after last | 5 items, page 4, size 2 | empty Items, TotalCount 5, TotalPages 3, Page 4 | explicit (echoing `Page` is assumed) |
| 12 | case-insensitive order + Id tie-break | `alpha/Alpha/ALPHA/beta/Beta` with mixed Ids | Ids `1, 3, 5, 2, 7` | explicit |
| 13 | input not modified | unsorted list | same order after the call | explicit |

## Run result

`dotnet test`: **20 cases in total, 16 passed and 4 failed.** All 4 failures come from the `TotalPages` bug:

- `Total_pages_rounds_up_to_cover_every_asset(5, 2, 3)`: got 2
- `Total_pages_rounds_up_to_cover_every_asset(1, 10, 1)`: got 0
- `Total_pages_rounds_up_to_cover_every_asset(101, 100, 2)`: got 1
- `Returns_empty_items_with_totals_for_page_after_last`: TotalPages was 2 instead of 3

Validation, ordering, the empty list, paging past the end, pageSize limits and leaving the input unchanged all pass. Once you apply the one-line fix, all 20 should pass.

Note: this machine only has the .NET 11 runtime, so I ran the tests with `DOTNET_ROLL_FORWARD=Major`. No project files were changed for that, and Coderbyte's net8.0 runner doesn't need it.

## Test file

`AssetLibrary.Tests/AssetPagerTests.cs` (your original test is kept):

```csharp
using AssetLibrary;
using Xunit;

namespace AssetLibrary.Tests;

public class AssetPagerTests
{
    private static List<Asset> MakeAssets(int count) =>
        Enumerable.Range(1, count).Select(i => new Asset(i, $"asset-{i:D3}.png", i)).ToList();

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
    [InlineData(1, 1)]
    [InlineData(100, 100)]
    public void Accepts_page_size_at_the_limits(int pageSize, int expectedItems)
    {
        var result = AssetPager.GetPage(MakeAssets(150), 1, pageSize);

        Assert.Equal(expectedItems, result.Items.Count);
        Assert.Equal(pageSize, result.PageSize);
    }

    [Fact]
    public void Returns_empty_result_with_zero_pages_for_empty_list()
    {
        var result = AssetPager.GetPage(new List<Asset>(), 1, 10);

        Assert.Empty(result.Items);
        Assert.Equal(0, result.TotalCount);
        Assert.Equal(0, result.TotalPages);
    }

    [Theory]
    [InlineData(5, 2, 3)]
    [InlineData(4, 2, 2)]
    [InlineData(1, 10, 1)]
    [InlineData(101, 100, 2)]
    public void Total_pages_rounds_up_to_cover_every_asset(int count, int pageSize, int expectedPages)
    {
        var result = AssetPager.GetPage(MakeAssets(count), 1, pageSize);

        Assert.Equal(count, result.TotalCount);
        Assert.Equal(expectedPages, result.TotalPages);
    }

    [Fact]
    public void Returns_remaining_assets_on_partial_last_page()
    {
        var result = AssetPager.GetPage(MakeAssets(5), 3, 2);

        Assert.Equal(new[] { 5 }, result.Items.Select(a => a.Id));
        Assert.Equal(3, result.Page);
    }

    [Fact]
    public void Returns_empty_items_with_totals_for_page_after_last()
    {
        var result = AssetPager.GetPage(MakeAssets(5), 4, 2);

        Assert.Empty(result.Items);
        Assert.Equal(4, result.Page);
        Assert.Equal(5, result.TotalCount);
        Assert.Equal(3, result.TotalPages);
    }

    [Fact]
    public void Orders_names_case_insensitively_and_breaks_ties_by_id()
    {
        var assets = new List<Asset>
        {
            new(7, "beta.png", 1),
            new(3, "Alpha.png", 1),
            new(5, "ALPHA.png", 1),
            new(1, "alpha.png", 1),
            new(2, "Beta.png", 1),
        };

        var result = AssetPager.GetPage(assets, 1, 10);

        Assert.Equal(new[] { 1, 3, 5, 2, 7 }, result.Items.Select(a => a.Id));
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
        var snapshot = assets.ToList();

        AssetPager.GetPage(assets, 1, 2);

        Assert.Equal(snapshot, assets);
    }
}
```

One thing I didn't cover: very large page numbers such as `int.MaxValue`. With a page size of 2 or more, `(page - 1) * pageSize` can overflow and hand `Skip` a negative number. For example, `page = int.MaxValue, pageSize = 2` returns page 1's items instead of an empty page. If you have 30 seconds after the `TotalPages` fix, compute the skip as `(long)(page - 1) * pageSize` and return empty `Items` when it is at or past the asset count.
