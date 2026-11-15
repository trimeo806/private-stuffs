namespace AssetLibrary;

public sealed class DuplicateAssetException : Exception
{
    public DuplicateAssetException(string checksum, int existingId)
        : base($"An asset with checksum '{checksum}' already exists (id {existingId}).")
    {
        Checksum = checksum;
        ExistingId = existingId;
    }

    public string Checksum { get; }

    public int ExistingId { get; }
}
