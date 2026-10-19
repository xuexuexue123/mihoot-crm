#!/usr/bin/env bash
# Smoke test: sanction source tag CRUD on 9090
#
# Usage:
#   TOKEN=xxx TENANT=mega_demo ./scripts/smoke/sanction-source-tag-api.sh
#   CRM_BASE_URL=http://127.0.0.1:9090 TOKEN=xxx TENANT=xxx ./scripts/smoke/sanction-source-tag-api.sh
#
# Requires: curl, jq

set -euo pipefail

BASE="${CRM_BASE_URL:-http://127.0.0.1:9090}"
TOKEN="${TOKEN:-}"
TENANT="${TENANT:-}"

if [[ -z "$TOKEN" || -z "$TENANT" ]]; then
  echo "Error: TOKEN and TENANT are required." >&2
  exit 1
fi

AUTH=(-H "Authorization: Bearer $TOKEN" -H "tenant: $TENANT" -H "Content-Type: application/json")

echo "==> LIST"
curl -sS "${AUTH[@]}" "$BASE/crm/sanction-source-tag/list?pageNum=1&pageSize=10" | jq '{code, total, first: .rows[0].tagCode}'

ID="tag-smoke-$(date +%s)"
TAG_CODE="SMOKE-OFAC-$ID"

BODY=$(jq -n \
  --arg id "$ID" \
  --arg tagCode "$TAG_CODE" \
  '{
  id: $id,
  tagCode: $tagCode,
  tagName: "冒烟制裁标签",
  authority: "U.S. Treasury OFAC",
  description: "冒烟标签判定标准"
}')

echo "==> CREATE $ID"
CREATE_RESP=$(curl -sS "${AUTH[@]}" -X POST "$BASE/crm/sanction-source-tag" -d "$BODY")
echo "$CREATE_RESP" | jq '{code, id: .data.id, tagCode: .data.tagCode, tagName: .data.tagName}'

echo "==> GET"
curl -sS "${AUTH[@]}" "$BASE/crm/sanction-source-tag/$ID" | jq '{code, tagCode: .data.tagCode, authority: .data.authority}'

echo "==> UPDATE"
UPD=$(echo "$BODY" | jq '.tagName = "冒烟制裁标签-已改"')
curl -sS "${AUTH[@]}" -X PUT "$BASE/crm/sanction-source-tag" -d "$UPD" | jq '{code, tagName: .data.tagName}'

echo "==> BATCH upsert"
BATCH=$(jq -n \
  --arg id "$ID" \
  --arg tagCode "$TAG_CODE" \
  '{
  mode: "upsert",
  rows: [{
    id: $id,
    tagCode: $tagCode,
    tagName: "冒烟批量更新",
    authority: "U.S. Treasury OFAC",
    description: "批量描述"
  }]
}')
curl -sS "${AUTH[@]}" -X POST "$BASE/crm/sanction-source-tag/batch" -d "$BATCH" | jq .

echo "==> SEARCH"
curl -sS "${AUTH[@]}" "$BASE/crm/sanction-source-tag/list?pageNum=1&pageSize=10&searchQuery=SMOKE" \
  | jq '{code, total}'

echo "==> DELETE (soft)"
curl -sS "${AUTH[@]}" -X DELETE "$BASE/crm/sanction-source-tag/$ID" | jq .

echo "==> GET after delete (expect null data)"
curl -sS "${AUTH[@]}" "$BASE/crm/sanction-source-tag/$ID" | jq '{code, data}'

echo "Done."
