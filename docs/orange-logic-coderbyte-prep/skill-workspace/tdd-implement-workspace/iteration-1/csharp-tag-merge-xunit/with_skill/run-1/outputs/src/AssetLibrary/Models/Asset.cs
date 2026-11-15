namespace AssetLibrary.Models;

public sealed class Asset
{
    public Asset(Guid id, string name, IEnumerable<string>? tags = null)
    {
        Id = id;
        Name = name;
        Tags = new List<string>(tags ?? Array.Empty<string>());
    }

    public Guid Id { get; }

    public string Name { get; }

    public List<string> Tags { get; }
}
