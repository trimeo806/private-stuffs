#!/usr/bin/env bash
# Upload files to a Jira issue's attachments via the REST API (the Atlassian MCP
# has no attachment tool). No prompting — the token is read from the environment,
# falling back to the git-credential store for the Jira host.
#
#   attach.sh <ISSUE_KEY> <file> [file ...]
#
# Credentials (in priority order, never prompted):
#   JIRA_API_TOKEN   Jira API token  (else: git credential fill for $JIRA_HOST)
#   JIRA_EMAIL       account email   (default: pablo@easygds.com)
#   JIRA_HOST        site host       (default: easygds.atlassian.net)
#
# Written for macOS bash 3.2 — no bash-4 idioms.
set -u

KEY="${1:-}"
shift || true
if [ -z "$KEY" ] || [ "$#" -eq 0 ]; then
	echo "usage: attach.sh <ISSUE_KEY> <file> [file ...]" >&2
	exit 64
fi

HOST="${JIRA_HOST:-easygds.atlassian.net}"
EMAIL="${JIRA_EMAIL:-pablo@easygds.com}"
TOKEN="${JIRA_API_TOKEN:-}"
if [ -z "$TOKEN" ]; then
	TOKEN=$(printf 'protocol=https\nhost=%s\n\n' "$HOST" | git credential fill 2>/dev/null | sed -n 's/^password=//p')
fi
if [ -z "$TOKEN" ]; then
	echo "ERROR: no Jira API token found." >&2
	echo "  Set it once (persists in your shell profile):  export JIRA_API_TOKEN=<token>" >&2
	echo "  or store it:  printf 'protocol=https\\nhost=%s\\nusername=%s\\npassword=<token>\\n' '$HOST' '$EMAIL' | git credential approve" >&2
	exit 2
fi

# Jira Cloud default attachment cap is 10 MB — warn before a doomed upload.
LIMIT=$((10 * 1024 * 1024))
rc=0
for f in "$@"; do
	if [ ! -f "$f" ]; then
		echo "skip (missing): $f" >&2
		continue
	fi
	sz=$(wc -c <"$f" | tr -d ' ')
	if [ "$sz" -gt "$LIMIT" ]; then
		echo "WARN: $(basename "$f") is ${sz} bytes (> 10 MB) — may exceed Jira's attachment limit." >&2
	fi
	echo "uploading $(basename "$f") (${sz} bytes) ..."
	if curl -sS --fail -u "$EMAIL:$TOKEN" -X POST \
		-H "X-Atlassian-Token: no-check" \
		-F "file=@$f" \
		"https://$HOST/rest/api/3/issue/$KEY/attachments" \
		| python3 -c 'import sys,json
for a in json.load(sys.stdin): print("  attached:", a["filename"], a.get("size"), "bytes")' 2>/dev/null; then
		:
	else
		echo "  FAILED: $f" >&2
		rc=1
	fi
done
exit "$rc"
