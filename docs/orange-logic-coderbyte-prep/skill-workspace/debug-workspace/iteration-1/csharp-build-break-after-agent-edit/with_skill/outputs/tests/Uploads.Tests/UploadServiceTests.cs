using Xunit;
using Uploads;

namespace Uploads.Tests;

public class UploadServiceTests
{
    [Fact]
    public void Upload_StoresFileWithTrimmedName()
    {
        var service = new UploadService();

        var stored = service.Upload("  photo.jpg ", "abc123", 2048);

        Assert.Equal("photo.jpg", stored.FileName);
    }

    [Fact]
    public void Upload_BlankChecksum_Throws()
    {
        var service = new UploadService();

        Assert.Throws<ArgumentException>(() => service.Upload("photo.jpg", "   ", 10));
    }

    [Fact]
    public void Upload_SameChecksum_ThrowsDuplicate()
    {
        var service = new UploadService();
        var first = service.Upload("a.jpg", "abc123", 10);

        var ex = Assert.Throws<DuplicateFileException>(() => service.Upload("b.jpg", "abc123", 10));

        Assert.Equal(first.Id, ex.ExistingId);
    }

    [Fact]
    public void TotalBytes_SupportsFilesLargerThanTwoGigabytes()
    {
        var service = new UploadService();
        service.Upload("movie.mp4", "f00d", 3_000_000_000L);
        service.Upload("clip.mp4", "beef", 1_000_000_000L);

        Assert.Equal(4_000_000_000L, service.TotalBytes());
    }
}
