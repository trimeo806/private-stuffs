#!/usr/bin/env bash
# Upload a bug-bash session's artifacts to the team S3 bucket under
# bug-bash/<timestamp>/ (PRIVATE — the objects are not made public).
#
#   upload-s3.sh <SESSION_DIR> [bucket]
#
# Bucket precedence: $BUGBASH_S3_BUCKET  >  arg 2  >  afterbook-artifacts
#
# It does the `aws s3 cp`, then prints THREE things for the Jira story:
#   1. a clickable HTTPS console FOLDER url (opens the whole session)   <- put THIS on the story
#   2. a clickable HTTPS console url per object
#   3. the raw s3:// paths (for CLI / aws s3 cp)
# The console urls open for anyone logged into the AWS account — that is the
# shareable link the team uses (the bucket blocks public access, so the plain
# https://<bucket>.s3... object url 403s without a presign).
#
# It deliberately does NOT generate a presigned (anyone-with-link) URL — that is
# blocked as data exfiltration and user approval can't clear it. It PRINTS the
# ready-to-run `aws s3 presign` command so the user can mint one themselves.
#
# Written for macOS bash 3.2.
set -u

DIR="${1:-}"
if [ -z "$DIR" ] || [ ! -d "$DIR" ]; then
	echo "usage: upload-s3.sh <SESSION_DIR> [bucket]" >&2
	exit 64
fi
BUCKET="${BUGBASH_S3_BUCKET:-${2:-afterbook-artifacts}}"
TS=$(date -u +%Y%m%d-%H%M%S)
PREFIX="bug-bash/$TS"

# Resolve the bucket's region so the console urls are correct (us-east-1 returns
# the literal "None" from the API).
REGION=$(aws s3api get-bucket-location --bucket "$BUCKET" --query LocationConstraint --output text 2>/dev/null)
if [ -z "$REGION" ] || [ "$REGION" = "None" ]; then
	REGION="us-east-1"
fi

echo "uploading session artifacts to s3://$BUCKET/$PREFIX/ ..."
if ! aws s3 cp "$DIR" "s3://$BUCKET/$PREFIX/" --recursive \
	--exclude "*" --include "*.webm" --include "*.jsonl" --include "*.md" --include "*.png" \
	--only-show-errors; then
	echo "ERROR: upload failed (check AWS creds / bucket access)." >&2
	exit 1
fi

CONSOLE="https://${REGION}.console.aws.amazon.com/s3"

echo ""
echo "==> SESSION FOLDER (put THIS clickable url on the Jira story):"
echo "  ${CONSOLE}/buckets/${BUCKET}?region=${REGION}&prefix=${PREFIX}/&showversions=false"

echo ""
echo "==> Per-object console urls (clickable for anyone with AWS login):"
aws s3api list-objects-v2 --bucket "$BUCKET" --prefix "$PREFIX/" \
	--query 'Contents[].Key' --output text 2>/dev/null \
	| tr '\t' '\n' | while read -r K; do
		[ -z "$K" ] && continue
		echo "  ${CONSOLE}/object/${BUCKET}?region=${REGION}&prefix=${K}"
	done

echo ""
echo "==> Raw s3:// paths (for CLI / aws s3 cp):"
aws s3 ls "s3://$BUCKET/$PREFIX/" --recursive | awk -v b="$BUCKET" '{print "  s3://" b "/" $4}'

echo ""
echo "The console urls above open for anyone logged into the AWS account. To mint a"
echo "shareable (7-day, anyone-with-link) URL, run this YOURSELF (agent is blocked):"
for f in "$DIR"/*.webm; do
	[ -f "$f" ] || continue
	echo "  aws s3 presign s3://$BUCKET/$PREFIX/$(basename "$f") --expires-in 604800 --region $REGION"
done
