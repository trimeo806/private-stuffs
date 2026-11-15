namespace Dam.Core.Assets;

public class DuplicateDetector
{
    private readonly IAssetRepository _repository;

    public DuplicateDetector(IAssetRepository repository)
    {
        _repository = repository;
    }

    public bool IsDuplicate(string checksum)
    {
        return _repository.All().Any(a => string.Equals(a.Checksum, checksum, StringComparison.OrdinalIgnoreCase));
    }
}
