namespace Catalog;

public sealed record Asset(int Id, string FileName, long SizeBytes, DateTime UploadedAt);
