#!/usr/bin/env bash
# Smoke test: government agency CRUD on 9090
#
# Usage:
#   TOKEN=xxx TENANT=mega_demo ./scripts/smoke/government-agency-api.sh
#   CRM_BASE_URL=http://127.0.0.1:9090 TOKEN=xxx TENANT=xxx ./scripts/smoke/government-agency-api.sh
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
curl -sS "${AUTH[@]}" "$BASE/crm/government-agency/list?pageNum=1&pageSize=10" | jq '{code, total, first: .rows[0].nameCn}'

ID="gov-smoke-$(date +%s)"
LEVEL="中央部委/级"
CATEGORY="投资审批与规划 (MPI/DPI)"
PROVINCE="河内"

BODY=$(jq -n \
  --arg id "$ID" \
  --arg level "$LEVEL" \
  --arg category "$CATEGORY" \
  --arg province "$PROVINCE" \
  '{
  id: $id,
  nameCn: "冒烟计划投资部",
  nameVn: "Smoke MPI VN",
  nameEn: "Smoke MPI",
  abbreviation: "SMOKE-MPI",
  level: $level,
  category: $category,
  province: $province,
  coreResponsibilities: "冒烟核心职责",
  approvalItems: ["IRC", "ERC"],
  contactPhone: "024-0000",
  email: "smoke@example.com"
}')

echo "==> CREATE $ID"
CREATE_RESP=$(curl -sS "${AUTH[@]}" -X POST "$BASE/crm/government-agency" -d "$BODY")
echo "$CREATE_RESP" | jq '{code, id: .data.id, nameCn: .data.nameCn, approvalItems: .data.approvalItems}'

echo "==> GET"
curl -sS "${AUTH[@]}" "$BASE/crm/government-agency/$ID" | jq '{code, nameCn: .data.nameCn, level: .data.level, approvalItems: .data.approvalItems}'

echo "==> UPDATE"
UPD=$(echo "$BODY" | jq '.nameCn = "冒烟计划投资部-已改" | .approvalItems = ["IRC", "ERC", "EIA"]')
curl -sS "${AUTH[@]}" -X PUT "$BASE/crm/government-agency" -d "$UPD" | jq '{code, nameCn: .data.nameCn, approvalItems: .data.approvalItems}'

echo "==> BATCH upsert"
BATCH=$(jq -n \
  --arg id "$ID" \
  --arg level "$LEVEL" \
  --arg category "$CATEGORY" \
  --arg province "$PROVINCE" \
  '{
  mode: "upsert",
  rows: [{
    id: $id,
    nameCn: "冒烟批量更新",
    nameVn: "Smoke MPI VN",
    nameEn: "Smoke MPI",
    abbreviation: "SMOKE-MPI",
    level: $level,
    category: $category,
    province: $province,
    coreResponsibilities: "批量职责",
    approvalItems: ["IRC"]
  }]
}')
curl -sS "${AUTH[@]}" -X POST "$BASE/crm/government-agency/batch" -d "$BATCH" | jq .

echo "==> FILTER by level"
curl -sS "${AUTH[@]}" "$BASE/crm/government-agency/list?pageNum=1&pageSize=10&level=$(printf %s "$LEVEL" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> FILTER by category"
curl -sS "${AUTH[@]}" "$BASE/crm/government-agency/list?pageNum=1&pageSize=10&category=$(printf %s "$CATEGORY" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> FILTER by province"
curl -sS "${AUTH[@]}" "$BASE/crm/government-agency/list?pageNum=1&pageSize=10&province=$(printf %s "$PROVINCE" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> DELETE (soft)"
curl -sS "${AUTH[@]}" -X DELETE "$BASE/crm/government-agency/$ID" | jq .

echo "==> GET after delete (expect null data)"
curl -sS "${AUTH[@]}" "$BASE/crm/government-agency/$ID" | jq '{code, data}'

echo "Done."
