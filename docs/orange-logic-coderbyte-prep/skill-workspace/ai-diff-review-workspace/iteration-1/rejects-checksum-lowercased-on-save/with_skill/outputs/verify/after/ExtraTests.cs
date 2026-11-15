using Dam.Core.Assets;
using Xunit;

namespace Dam.Core.Tests.Assets;

public class ExtraTests
{
    [Fact]
    public void Probe_ExistingUppercase_QueriedLowercase()
    {
        var existing = new Asset { Id = Guid.NewGuid(), Checksum = "A1B2C3D4" };
        var detector = new DuplicateDetector(new AssetRepository(new[] { existing }));
        Assert.True(detector.IsDuplicate("a1b2c3d4"));
    }

    [Fact]
    public void Probe_SavedUppercase_QueriedUppercase()
    {
        var repo = new AssetRepository(Array.Empty<Asset>());
        repo.Save(new Asset { Id = Guid.NewGuid(), Checksum = "A1B2C3D4" });
        Assert.True(new DuplicateDetector(repo).IsDuplicate("A1B2C3D4"));
    }

    [Fact]
    public void Probe_NullChecksum()
    {
        var detector = new DuplicateDetector(new AssetRepository(Array.Empty<Asset>()));
        Assert.False(detector.IsDuplicate(null!));
    }
}
