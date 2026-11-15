namespace Uploads;

public sealed class DuplicateFileException : Exception
{
    public DuplicateFileException(int existingId)
        : base($"A file with this checksum already exists (id {existingId}).")
    {
        ExistingId = existingId;
    }

    public int ExistingId { get; }
}
