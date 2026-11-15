# Worked Review Example

Read when unsure how a filled template looks. The shape matters more than the domain: concrete breaking inputs, `file:line`, a "Next step" line, and the reply ending on the feedback prompt.

## REJECT: case-sensitive extension filter

Requirement: `FilterByExtension(assets, ext)` returns assets whose file extension equals `ext`, ignoring case; callers pass `"jpg"`.

Agent diff in `src/Dam.Core/Search/AssetFilter.cs`:

```csharp
public static IEnumerable<Asset> FilterByExtension(IEnumerable<Asset> assets, string ext)
{
    return assets.Where(a => Path.GetExtension(a.FileName) == "." + ext);
}
```

Review:

**Requirement:** return assets whose extension equals `ext`, case-insensitive.
**Verdict:** REJECT - extension compare is case-sensitive, so uppercase files are missed.

| # | Severity | Where | Problem | Breaking input: actual vs expected | Fix |
|---|---|---|---|---|---|
| 1 | blocker | src/Dam.Core/Search/AssetFilter.cs:14 | `==` is an ordinal, case-sensitive compare | `"photo.JPG"`, ext `"jpg"`: excluded vs included | `string.Equals(..., StringComparison.OrdinalIgnoreCase)` |
| 2 | major | src/Dam.Core/Search/AssetFilter.cs:14 | Assumes `ext` has no leading dot | ext `".jpg"`: compares to `"..jpg"`, nothing matches | `TrimStart('.')` on both sides |

**Edge cases traced:** `"a.jpg"` -> match (ok); `"a.JPG"` -> no match (wrong); `"noext"` -> `""`, no match (ok); ext `".jpg"` -> no match (wrong)
**Checks passed:** hallucination, contract, complexity, security
**Not verified:** null `FileName`, depends on whether the model allows it; callers that pass `ext` with a dot (search for `FilterByExtension(`)
**Next step:** send the prompt below; log "rejected: case-sensitive extension compare misses .JPG files".

**Feedback prompt for the agent:**
> Rejected: "photo.JPG" with ext "jpg" is excluded because the compare is case-sensitive; in FilterByExtension only, compare Path.GetExtension(name).TrimStart('.') to ext.TrimStart('.') with StringComparison.OrdinalIgnoreCase, keep the signature, and add tests for "photo.JPG" and ext ".jpg".

## ACCEPT: what "visibly earned" looks like

For a clean diff the table has the single `none` row, "Edge cases traced" lists the boundary inputs with old vs new output, "Checks passed" names all nine checks, "Not verified" names any caller or stored-data effect you could not see and what to search for, and the prompt is `none, accept as is`.
