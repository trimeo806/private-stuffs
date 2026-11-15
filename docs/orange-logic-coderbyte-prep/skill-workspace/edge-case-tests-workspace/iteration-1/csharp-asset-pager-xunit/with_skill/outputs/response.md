## Edge cases: `AssetPager.GetPage(assets, page, pageSize)`

Framework: xUnit 2.9 (detected from `AssetLibrary.Tests/AssetLibrary.Tests.csproj`) | Test file: `AssetLibrary.Tests/AssetPagerTests.cs` (added to the existing class) | Run: `dotnet test`

Short answer: the tests found **two real bugs** that hidden tests are very likely to hit. `TotalPages` rounds down instead of up, and a very large `page` overflows and returns page 1 instead of an empty page. Details and a two-line fix are below. I did not change the production code.

| # | Case | Input | Expected | Why it matters | Source |
|---|------|-------|----------|----------------|--------|
| 1 | null list | `assets = null` | `ArgumentNullException`, param `assets` | missing guard, NRE inside LINQ | spec |
| 2 | page below 1 | `page = 0, -1, int.MinValue` | `ArgumentOutOfRangeException`, param `page` | 0-based mix-up, `< 0` instead of `< 1` | spec |
| 3 | pageSize out of range | `pageSize = 0, -1, 101, int.MaxValue` | `ArgumentOutOfRangeException`, param `pageSize` | missing upper bound, divide by zero | spec |
| 4 | pageSize at limits | `pageSize = 1` and `100`, 150 assets | accepted; 1 and 100 items | `<=` written as `<` on the bounds | spec |
| 5 | TotalPages rounds up | (count, size) = (0,10), (1,10), (9,10), (10,10), (11,10), (250,100), (7,1) | 0, 1, 1, 1, 2, 3, 7 | floor division instead of ceiling | spec |
| 6 | empty list | `[]`, page 1, size 10 | empty `Items`, `TotalCount` 0, `TotalPages` 0 | `TotalPages = 1` on empty | spec |
| 7 | last partial page | 11 assets, page 3, size 5 | only asset 11, `TotalPages` 3 | partial page dropped or miscounted | spec |
| 8 | page past the end | 4 assets, size 2, page 3, 4, 1000, `int.MaxValue` | empty `Items`, `TotalCount` 4, `TotalPages` 2, `Page` echoed | exception thrown, or `(page-1)*pageSize` overflowing to a negative skip | spec |
| 9 | case-insensitive order | `zeta, Beta, alpha, Gamma` | `alpha, Beta, Gamma, zeta` | ordinal compare puts uppercase first | spec |
| 10 | ties broken by Id | `logo`(30), `LOGO`(10), `Logo`(20), `banner`(5), size 2 | page 1: ids 5, 10; page 2: ids 20, 30 | missing or wrong tie-breaker, items repeated across pages | spec |
| 11 | input not modified | unsorted list | same order after the call | in-place `Sort()` on the caller's list | spec |

### Tests

Added to `AssetLibrary.Tests/AssetPagerTests.cs` next to the existing `Returns_first_page_in_name_order` test (same namespace, same naming style, no new packages):

```csharp
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
```

### Result

21 passed, 6 failed (27 test cases from 12 new test methods plus the existing one), run with `dotnet test`. My machine only has the .NET 11 runtime, so I ran it with `DOTNET_ROLL_FORWARD=Major`; on the Coderbyte runner plain `dotnet test` is enough.

Failing: case 5 (four rows: 1/10, 9/10, 11/10, 250/100), case 7, and case 8 (the `int.MaxValue` row).

### Suspected bugs

- **Case 5 and case 7: `TotalPages` rounds down.** Expected 1 for 1 asset at size 10, got 0; expected 2 for 11 at size 10, got 1; expected 3 for 250 at size 100, got 2; expected 3 for 11 at size 5, got 2. The task says `TotalPages` is "the number of pages needed to show all of them", which is a ceiling. Likely cause: `AssetLibrary/AssetPager.cs:24`, `var totalPages = ordered.Count / pageSize;`. It only looks correct when the count is an exact multiple of the page size, which is why the existing test did not catch it. This is the bug hidden tests are most likely to check.
- **Case 8, `page = int.MaxValue`: expected empty `Items`, got the first two assets.** `(page - 1) * pageSize` overflows `int` to a negative number, and `Skip` with a negative count skips nothing, so you get page 1. Likely cause: `AssetLibrary/AssetPager.cs:26`, `.Skip((page - 1) * pageSize)`.

Production code is left unchanged. If you want to fix both in the time you have left, this is the smallest change in `AssetPager.cs`:

```csharp
var totalPages = (ordered.Count + pageSize - 1) / pageSize;
var skip = (long)(page - 1) * pageSize;
var items = skip >= ordered.Count
    ? new List<Asset>()
    : ordered.Skip((int)skip).Take(pageSize).ToList();
```

I tried this fix on a scratch copy (not your files) and all 27 test cases passed. After you apply it, run `dotnet test` again to confirm.

### Assumptions

- Case 8: `Page` in the result echoes the requested page even past the end (the task only says `TotalCount` and `TotalPages` are still filled in). If a hidden test expects something else here it would be unusual; drop the `Assert.Equal(page, result.Page)` line if you disagree.
- Case 9: "case-insensitive" is read as ordinal-ignore-case, which is what the code uses. The tests only use ASCII letters, so they pass under either ordinal or culture-aware comparison and do not lock in one of them.

Question for you: do you want me to apply the fix above to `AssetPager.cs`, or will you make the change yourself in the Coderbyte editor?
