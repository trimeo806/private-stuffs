using Xunit;

namespace AssetLibrary.Tests;

public class SearchByTagsEdgeCaseTests
{
    private static readonly DateTime BaseTime = new(2026, 1, 1, 0, 0, 0, DateTimeKind.Utc);

    private static AssetService NewService() => new(new InMemoryAssetRepository());

    private sealed class ReverseOrderRepository : IAssetRepository
    {
        private readonly List<Asset> _assets = new();

        public void Save(Asset asset) => _assets.Add(asset);

        public Asset? FindById(int id) => _assets.FirstOrDefault(asset => asset.Id == id);

        public Asset? FindByChecksum(string checksum) => _assets.FirstOrDefault(asset => asset.Checksum == checksum);

        public IReadOnlyList<Asset> All() => _assets.AsEnumerable().Reverse().ToList();
    }

    [Fact]
    public void Match_all_includes_an_asset_that_has_extra_tags()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach", "sunset", "city" }, BaseTime);
        service.Upload("b.jpg", 10, "bb22", new[] { "beach" }, BaseTime);

        var result = service.SearchByTags(new[] { "beach", "sunset" }, matchAll: true);

        Assert.Equal(new[] { 1 }, result.Select(a => a.Id));
    }

    [Fact]
    public void Match_all_never_returns_an_asset_without_tags()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", Array.Empty<string>(), BaseTime);
        service.Upload("b.jpg", 10, "bb22", new[] { "beach" }, BaseTime);

        var result = service.SearchByTags(new[] { "beach" }, matchAll: true);

        Assert.Equal(new[] { 2 }, result.Select(a => a.Id));
    }

    [Fact]
    public void Match_all_returns_empty_when_no_asset_has_every_tag()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach" }, BaseTime);
        service.Upload("b.jpg", 10, "bb22", new[] { "sunset" }, BaseTime);

        var result = service.SearchByTags(new[] { "beach", "sunset" }, matchAll: true);

        Assert.Empty(result);
    }

    [Fact]
    public void Match_all_treats_case_variant_query_tags_as_one_tag()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach" }, BaseTime);
        service.Upload("b.jpg", 10, "bb22", new[] { "city" }, BaseTime);

        var result = service.SearchByTags(new[] { "Beach", "BEACH", "beach" }, matchAll: true);

        Assert.Equal(new[] { 1 }, result.Select(a => a.Id));
    }

    [Theory]
    [InlineData(true)]
    [InlineData(false)]
    public void Query_tags_are_trimmed_before_matching(bool matchAll)
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach" }, BaseTime);
        service.Upload("b.jpg", 10, "bb22", new[] { "city" }, BaseTime);

        var result = service.SearchByTags(new[] { "  Beach " }, matchAll);

        Assert.Equal(new[] { 1 }, result.Select(a => a.Id));
    }

    [Theory]
    [InlineData(true)]
    [InlineData(false)]
    public void Returns_empty_when_query_has_only_blank_tags(bool matchAll)
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach" }, BaseTime);
        service.Upload("b.jpg", 10, "bb22", Array.Empty<string>(), BaseTime);

        var result = service.SearchByTags(new[] { "", "   ", "\t" }, matchAll);

        Assert.Empty(result);
    }

    [Theory]
    [InlineData(true)]
    [InlineData(false)]
    public void Returns_empty_when_query_has_no_tags(bool matchAll)
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach" }, BaseTime);
        service.Upload("b.jpg", 10, "bb22", Array.Empty<string>(), BaseTime);

        var result = service.SearchByTags(Array.Empty<string>(), matchAll);

        Assert.Empty(result);
    }

    [Theory]
    [InlineData(true)]
    [InlineData(false)]
    public void Results_are_ordered_by_id_even_when_the_repository_returns_them_unordered(bool matchAll)
    {
        var service = new AssetService(new ReverseOrderRepository());
        service.Upload("a.jpg", 10, "aa11", new[] { "beach" }, BaseTime);
        service.Upload("b.jpg", 10, "bb22", new[] { "beach" }, BaseTime);
        service.Upload("c.jpg", 10, "cc33", new[] { "beach" }, BaseTime);

        var result = service.SearchByTags(new[] { "beach" }, matchAll);

        Assert.Equal(new[] { 1, 2, 3 }, result.Select(a => a.Id));
    }

    [Fact]
    public void Match_any_ignores_a_null_query_tag()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach" }, BaseTime);

        var result = service.SearchByTags(new[] { null!, "beach" }, matchAll: false);

        Assert.Equal(new[] { 1 }, result.Select(a => a.Id));
    }

    [Fact]
    public void Returns_empty_when_query_tags_are_null()
    {
        var service = NewService();
        service.Upload("a.jpg", 10, "aa11", new[] { "beach" }, BaseTime);

        var result = service.SearchByTags(null!, matchAll: false);

        Assert.Empty(result);
    }
}
