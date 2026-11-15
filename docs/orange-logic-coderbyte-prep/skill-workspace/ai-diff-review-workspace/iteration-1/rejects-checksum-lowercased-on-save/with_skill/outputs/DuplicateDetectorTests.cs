using Dam.Core.Assets;
using Xunit;

namespace Dam.Core.Tests.Assets;

public class DuplicateDetectorTests
{
    [Fact]
    public void IsDuplicate_ReturnsTrue_WhenSameChecksumExists()
    {
        var existing = new Asset { Id = Guid.NewGuid(), FileName = "hero.jpg", Checksum = "A1B2C3D4" };
        var detector = new DuplicateDetector(new AssetRepository(new[] { existing }));

        Assert.True(detector.IsDuplicate("A1B2C3D4"));
    }

    [Fact]
    public void IsDuplicate_ReturnsTrue_WhenChecksumDiffersOnlyInLetterCase()
    {
        var existing = new Asset { Id = Guid.NewGuid(), FileName = "hero.jpg", Checksum = "a1b2c3d4" };
        var detector = new DuplicateDetector(new AssetRepository(new[] { existing }));

        Assert.True(detector.IsDuplicate("A1B2C3D4"));
    }

    [Fact]
    public void IsDuplicate_ReturnsFalse_WhenNoAssetHasTheChecksum()
    {
        var existing = new Asset { Id = Guid.NewGuid(), FileName = "hero.jpg", Checksum = "a1b2c3d4" };
        var detector = new DuplicateDetector(new AssetRepository(new[] { existing }));

        Assert.False(detector.IsDuplicate("ffff0000"));
    }
}
