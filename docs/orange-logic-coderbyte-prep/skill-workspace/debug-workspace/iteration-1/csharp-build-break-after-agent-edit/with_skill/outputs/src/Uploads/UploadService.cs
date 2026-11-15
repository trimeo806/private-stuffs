namespace Uploads;

public sealed class UploadService
{
    private readonly List<StoredFile> _files = new();

    public StoredFile Upload(string fileName, string checksum, int sizeBytes)
    {
        if (string.IsNullOrBlank(fileName)) throw new ArgumentException("File name is required.", nameof(fileName));
        if (string.IsNullOrBlank(checksum)) throw new ArgumentException("Checksum is required.", nameof(checksum));
        if (sizeBytes < 0) throw new ArgumentOutOfRangeException(nameof(sizeBytes));

        var existing = _files.FirstOrDefault(f => string.Equals(f.Checksum, checksum, StringComparison.OrdinalIgnoreCase));
        if (existing is not null) throw new DuplicateFileException(existing.Id);

        var stored = new StoredFile(_files.Count + 1, fileName.Trim(), checksum, sizeBytes);
        _files.Add(stored);
        return stored;
    }

    public long TotalBytes() => _files.Sum(f => f.SizeBytes);
}
