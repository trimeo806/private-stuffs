<#
.SYNOPSIS
    Syncs .claude/agents/ to .opencode/agents/ with frontmatter transformation.

.DESCRIPTION
    Reads every .md agent file from .claude/agents/, strips Claude Code-specific
    frontmatter fields, maps the model name to OpenCode's provider format,
    adds OpenCode-specific fields (mode, permission), and writes the result
    to .opencode/agents/.

    Transformation rules:
      Strip : name, color, skills, memory, permissionMode, handoffs, tools
      Keep  : description (verbatim, including multi-line block scalars)
      Map   : model sonnet/opus → github-copilot/claude-sonnet-4.6
                    haiku       → github-copilot/claude-haiku-4.5
                    inherit     → (omit field)
      Add   : mode: subagent
              permission: edit: allow
              [git-manager only] bash: "*": allow / "git push --force*": ask
      Special: knowledge-graph-guide.md — omit model field entirely

.EXAMPLE
    .\scripts\sync-to-opencode.ps1
#>

$ErrorActionPreference = 'Stop'

$RepoRoot = Join-Path $PSScriptRoot '..'
$SrcDir   = Join-Path (Join-Path $RepoRoot '.claude')   'agents'
$DstDir   = Join-Path (Join-Path $RepoRoot '.opencode') 'agents'

# ── Validate source ──────────────────────────────────────────────────────────

if (-not (Test-Path $SrcDir)) {
    Write-Error "Source directory not found: $SrcDir"
    exit 1
}

# ── Ensure destination exists ────────────────────────────────────────────────

if (-not (Test-Path $DstDir)) {
    New-Item -ItemType Directory -Path $DstDir -Force | Out-Null
    Write-Host "Created $DstDir"
}

# ── Model mapping ─────────────────────────────────────────────────────────────

function Map-Model([string]$src) {
    switch ($src.Trim().ToLower()) {
        'haiku'   { return 'github-copilot/claude-haiku-4.5' }
        'inherit' { return $null }                            # omit field
        default   { return 'github-copilot/claude-sonnet-4.6' }  # sonnet + opus
    }
}

# ── Frontmatter parser ────────────────────────────────────────────────────────
# Returns a hashtable: key → string[] (all lines for that key, including
# indented continuation lines for multi-line values like description: |).

function Parse-Frontmatter([string[]]$lines) {
    $fields = [ordered]@{}
    $currentKey = $null
    $currentBlock = [System.Collections.Generic.List[string]]::new()

    foreach ($line in $lines) {
        if ($line -match '^([A-Za-z][A-Za-z0-9_-]*):\s*(.*)') {
            if ($null -ne $currentKey) {
                $fields[$currentKey] = $currentBlock.ToArray()
            }
            $currentKey = $Matches[1]
            $currentBlock = [System.Collections.Generic.List[string]]::new()
            $currentBlock.Add($line)
        }
        elseif ($line -match '^\s') {
            # Indented continuation — belongs to the current key's block
            if ($null -ne $currentKey) {
                $currentBlock.Add($line)
            }
        }
        # Blank lines at top level (between keys) are silently dropped
    }

    if ($null -ne $currentKey) {
        $fields[$currentKey] = $currentBlock.ToArray()
    }

    return $fields
}

# ── Build new frontmatter lines ───────────────────────────────────────────────

function Build-Frontmatter($fields, [string]$fileName) {
    $out = [System.Collections.Generic.List[string]]::new()

    # 1. description (preserved verbatim — may be multi-line)
    if ($fields.Contains('description')) {
        foreach ($l in $fields['description']) { $out.Add($l) }
    }

    # 2. model (omit for knowledge-graph-guide; otherwise map)
    if ($fileName -ne 'knowledge-graph-guide.md') {
        $srcModelLine = if ($fields.Contains('model')) { $fields['model'][0] } else { 'model: sonnet' }
        $srcModel     = ($srcModelLine -replace '^model:\s*', '').Trim()
        $mapped       = Map-Model $srcModel
        if ($mapped) { $out.Add("model: $mapped") }
    }

    # 3. mode
    $out.Add('mode: subagent')

    # 4. permission
    if ($fileName -eq 'git-manager.md') {
        $out.Add('permission:')
        $out.Add('  edit: allow')
        $out.Add('  bash:')
        $out.Add('    "*": allow')
        $out.Add('    "git push --force*": ask')
    }
    else {
        $out.Add('permission:')
        $out.Add('  edit: allow')
    }

    return $out.ToArray()
}

# ── Main loop ─────────────────────────────────────────────────────────────────

$agentFiles = Get-ChildItem -Path $SrcDir -Filter '*.md' | Sort-Object Name
$synced  = 0
$skipped = 0

Write-Host ""
Write-Host "Syncing $($agentFiles.Count) agent(s) from .claude/agents/ → .opencode/agents/"
Write-Host ""

foreach ($file in $agentFiles) {
    # Read raw content; strip BOM if present
    $raw = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
    $raw = $raw.TrimStart([char]0xFEFF)

    # Normalize line endings to LF for consistent regex matching
    $raw = $raw -replace '\r\n', "`n" -replace '\r', "`n"

    # Split: opening ---, frontmatter, closing ---, body
    if ($raw -notmatch '(?s)^---[ \t]*\n(.*?)\n---[ \t]*\n(.*)$') {
        Write-Warning "  SKIP $($file.Name): could not parse frontmatter (missing --- delimiters)"
        $skipped++
        continue
    }

    $fmText = $Matches[1]
    $body   = $Matches[2]

    # Parse and transform
    $fmLines  = $fmText -split '\n'
    $fields   = Parse-Frontmatter $fmLines
    $newFMArr = Build-Frontmatter $fields $file.Name

    # Reassemble
    $newContent = "---`n" + ($newFMArr -join "`n") + "`n---`n" + $body

    # Write to destination (UTF-8 without BOM)
    $dstPath = Join-Path $DstDir $file.Name
    [System.IO.File]::WriteAllText($dstPath, $newContent, (New-Object System.Text.UTF8Encoding $false))

    Write-Host "  OK  $($file.Name)"
    $synced++
}

Write-Host ""
Write-Host "Done: $synced synced, $skipped skipped."
Write-Host "Output: $DstDir"
