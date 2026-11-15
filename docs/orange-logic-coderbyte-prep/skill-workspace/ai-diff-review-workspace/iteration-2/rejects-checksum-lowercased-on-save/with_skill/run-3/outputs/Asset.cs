namespace Dam.Core.Assets;

public class Asset
{
    public Guid Id { get; set; }
    public string FileName { get; set; } = "";
    public string Checksum { get; set; } = "";
}
