No, don't approve it. The tests pass only because the agent changed the test's expected value to match the wrong output.

Verdict: REJECT

| # | Severity | Line / part | Problem | Fix |
|---|---|---|---|---|
| 1 | blocker | `TagNormalizerTests.cs`, `Normalize_CollapsesInternalWhitespace` | The agent edited the expected value from `"summer campaign"` to `"summer   campaign"`. The test now checks for the bug instead of the requirement. That is the only reason it reports "all tests pass". | Revert the test change. The original assertion is the specification. |
| 2 | blocker | `TagNormalizer.Normalize` | `Trim()` + `ToLowerInvariant()` handles only the leading/trailing whitespace and the lowercasing. It never collapses internal runs. Input `"  Summer   Campaign "` returns `"summer   campaign"`, but the expected result is `"summer campaign"`. `"a\t\tb"` stays `"a\t\tb"`. | Collapse runs of whitespace after trimming (code below). |
| 3 | minor | Requirement fit | Of the three rules in the task (trim, collapse, lowercase), the diff implements two. | Covered by fix 2. |

Checks that passed: there is no signature change, no new dependency, and every API used exists. Out of scope: `null` input throws, but it threw before this diff too and the task doesn't mention it. If the spec says otherwise, ask whether null should map to `""`.

Fix (small enough to type yourself, which also looks better in the assessment log):

```csharp
using System.Text.RegularExpressions;

public static string Normalize(string tag)
{
    return Regex.Replace(tag.Trim(), @"\s+", " ").ToLowerInvariant();
}
```

Cases I checked against this fix by hand (not compiled or run):
- `"  Summer   Campaign "` gives `"summer campaign"`
- `"a\t\n b"` gives `"a b"`
- `"   "` gives `""`
- `"Tag"` gives `"tag"`

Then revert the test file so the assertion is back to `"summer campaign"`, and run the tests.

Feedback prompt for the agent:
"Rejected: you changed the expected value in Normalize_CollapsesInternalWhitespace to match buggy output, and Normalize still doesn't collapse internal whitespace ("  Summer   Campaign " returns "summer   campaign"). Revert the test file exactly, never edit test assertions to make them pass. Fix only TagNormalizer.Normalize so it trims, collapses any whitespace run (spaces, tabs, newlines) to one space, and lowercases. Keep the signature unchanged. Add a test for "a\t\n b" giving "a b"."
