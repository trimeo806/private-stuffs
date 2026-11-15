using Xunit;
using Sharing;

namespace Sharing.Tests;

public class ShareLinkServiceTests
{
    private static readonly DateTime Start = new(2026, 6, 1, 9, 0, 0, DateTimeKind.Utc);

    [Fact]
    public void Create_SetsExpiryFromClock()
    {
        var service = new ShareLinkService(new FakeClock(Start));

        var link = service.Create(7, TimeSpan.FromHours(1));

        Assert.Equal(Start, link.CreatedAtUtc);
        Assert.Equal(Start.AddHours(1), link.ExpiresAtUtc);
    }

    [Fact]
    public void Create_NonPositiveLifetime_Throws()
    {
        var service = new ShareLinkService(new FakeClock(Start));

        Assert.Throws<ArgumentOutOfRangeException>(() => service.Create(7, TimeSpan.Zero));
    }

    [Fact]
    public void IsValid_UnknownToken_ReturnsFalse()
    {
        var service = new ShareLinkService(new FakeClock(Start));

        Assert.False(service.IsValid("missing"));
    }

    [Fact]
    public void PurgeExpired_RemovesOnlyExpiredLinks()
    {
        var clock = new FakeClock(Start);
        var service = new ShareLinkService(clock);
        service.Create(1, TimeSpan.FromMinutes(30));
        service.Create(2, TimeSpan.FromHours(2));

        clock.Advance(TimeSpan.FromHours(1));

        Assert.Equal(1, service.PurgeExpired());
    }
}
