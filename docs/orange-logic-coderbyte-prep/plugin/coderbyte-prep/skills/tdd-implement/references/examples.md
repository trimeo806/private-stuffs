# Worked example (C#, xUnit)

Read when you want to see one full step end to end: design notes, a unit test on the pure core, an integration test through the public method, and the thin shell that owns the only write.

Task: add `AssetService.ArchiveExpired(DateOnly today)`, which archives every asset whose `ExpiresOn` is on or before `today` and returns how many it archived. xUnit, an `InMemoryAssetRepository` already exists, `Asset` is a record.

Design: model is the existing `Asset(Guid Id, string Name, DateOnly? ExpiresOn, bool IsArchived)`. Flow: `repository -> assets -> keep expired and not archived (pure) -> save archived copy -> count`. Boundaries: the repository, and the clock, which the signature already turns into a `today` parameter, so no clock abstraction is needed.

Red 1, unit test on the pure rule, with a stub `IsExpired` that returns `false`:

```csharp
[Theory]
[InlineData(2026, 3, 1, true)]
[InlineData(2026, 3, 2, false)]
public void Asset_expires_on_its_expiry_date_and_not_before(int year, int month, int day, bool expected)
{
    var asset = new Asset(Guid.NewGuid(), "a.png", new DateOnly(year, month, day), false);

    Assert.Equal(expected, Expiry.IsExpired(asset, new DateOnly(2026, 3, 1)));
}
```

Green 1:

```csharp
public static class Expiry
{
    public static bool IsExpired(Asset asset, DateOnly today) =>
        asset.ExpiresOn is { } expiresOn && expiresOn <= today;
}
```

Red 2, integration test through the public method and the real in-memory repository; it fails with `NotImplementedException`:

```csharp
[Fact]
public void Archives_only_expired_assets_and_returns_the_count()
{
    var repository = new InMemoryAssetRepository(
        new Asset(Guid.NewGuid(), "old.png", new DateOnly(2026, 1, 1), false),
        new Asset(Guid.NewGuid(), "new.png", new DateOnly(2026, 12, 1), false),
        new Asset(Guid.NewGuid(), "forever.png", null, false));

    var archived = new AssetService(repository).ArchiveExpired(new DateOnly(2026, 3, 1));

    Assert.Equal(1, archived);
    Assert.Equal(new[] { "old.png" }, repository.GetAll().Where(a => a.IsArchived).Select(a => a.Name));
}
```

Green 2, the thin shell that owns the only write:

```csharp
public int ArchiveExpired(DateOnly today)
{
    var expired = _repository.GetAll()
        .Where(asset => !asset.IsArchived && Expiry.IsExpired(asset, today))
        .ToList();

    foreach (var asset in expired)
    {
        _repository.Save(asset with { IsArchived = true });
    }

    return expired.Count;
}
```

Refactor: nothing to extract; `Expiry` stays a static class in the service's file because it has one caller. No `IClock`, no `IExpiryPolicy`: the date is already a parameter and the rule is one line.

Edge cases handled: no expiry date never expires, an already archived asset is not counted again. Open risk: the task does not say whether `today` is in UTC; the caller decides.
