# Worked example

A filled-in edge-case table, test file and result for a small pytest function. Read it when unsure how the output should look.

Function: `normalize_tags(tags: list[str]) -> list[str]`. Contract: trim each tag, lowercase it, drop empty ones, remove duplicates, return sorted. `None` raises `TypeError`. Repo has `pyproject.toml` with pytest.

| # | Case | Input | Expected | Why it matters | Source |
|---|------|-------|----------|----------------|--------|
| 1 | empty list | `[]` | `[]` | loops that assume one item | spec |
| 2 | case variants | `["Logo", "logo", "LOGO"]` | `["logo"]` | dedupe before lowercasing | spec |
| 3 | whitespace duplicates | `[" logo", "logo "]` | `["logo"]` | dedupe before trimming | spec |
| 4 | whitespace only | `["  ", ""]` | `[]` | empty tags kept after trim | spec |
| 5 | unicode | `["Café"]` | `["café"]` | ASCII-only lowercasing | assumed |
| 6 | null input | `None` | `TypeError` | missing guard | spec |

```python
import pytest

from tags import normalize_tags


@pytest.mark.parametrize(
    ("tags", "expected"),
    [
        ([], []),
        (["Logo", "logo", "LOGO"], ["logo"]),
        ([" logo", "logo "], ["logo"]),
        (["  ", ""], []),
        (["Café"], ["café"]),
    ],
    ids=["empty", "case-variants", "whitespace-duplicates", "whitespace-only", "unicode"],
)
def test_normalizes_tags(tags, expected):
    assert normalize_tags(tags) == expected


def test_rejects_none():
    with pytest.raises(TypeError):
        normalize_tags(None)
```

Result: 5 passed, 1 failed. Suspected bug: case 3 returns `["logo", "logo"]` because `tags.py` dedupes before `strip()`. Production code left unchanged.
