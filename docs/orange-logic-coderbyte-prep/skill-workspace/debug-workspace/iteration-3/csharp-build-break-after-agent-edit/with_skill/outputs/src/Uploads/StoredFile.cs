namespace Uploads;

public sealed record StoredFile(int Id, string FileName, string Checksum, long SizeBytes);
