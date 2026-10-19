#!/usr/bin/env bash
# Smoke test: professional service CRUD on 9090
#
# Usage:
#   TOKEN=xxx TENANT=mega_demo ./scripts/smoke/professional-service-api.sh
#   CRM_BASE_URL=http://127.0.0.1:9090 TOKEN=xxx TENANT=xxx ./scripts/smoke/professional-service-api.sh
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
curl -sS "${AUTH[@]}" "$BASE/crm/professional-service/list?pageNum=1&pageSize=10" | jq '{code, total, first: .rows[0].nameCn}'

ID="srv-smoke-$(date +%s)"
CATEGORY="法律服务 (Law)"
BACKGROUND="国际大厂/Big4"
PROVINCE="胡志明市"

BODY=$(jq -n \
  --arg id "$ID" \
  --arg category "$CATEGORY" \
  --arg background "$BACKGROUND" \
  --arg province "$PROVINCE" \
  '{
  id: $id,
  nameCn: "冒烟贝克麦坚时",
  nameVn: "Smoke Baker VN",
  nameEn: "Smoke Baker",
  category: $category,
  background: $background,
  province: $province,
  keyCapabilities: "冒烟核心能力",
  coreServices: ["公司设立", "劳动合规"],
  targetClients: "中资出海",
  contactPhone: "028-0000",
  email: "smoke@example.com"
}')

echo "==> CREATE $ID"
CREATE_RESP=$(curl -sS "${AUTH[@]}" -X POST "$BASE/crm/professional-service" -d "$BODY")
echo "$CREATE_RESP" | jq '{code, id: .data.id, nameCn: .data.nameCn, coreServices: .data.coreServices}'

echo "==> GET"
curl -sS "${AUTH[@]}" "$BASE/crm/professional-service/$ID" | jq '{code, nameCn: .data.nameCn, category: .data.category, coreServices: .data.coreServices}'

echo "==> UPDATE"
UPD=$(echo "$BODY" | jq '.nameCn = "冒烟贝克麦坚时-已改" | .coreServices = ["公司设立", "劳动合规", "并购"]')
curl -sS "${AUTH[@]}" -X PUT "$BASE/crm/professional-service" -d "$UPD" | jq '{code, nameCn: .data.nameCn, coreServices: .data.coreServices}'

echo "==> BATCH upsert"
BATCH=$(jq -n \
  --arg id "$ID" \
  --arg category "$CATEGORY" \
  --arg background "$BACKGROUND" \
  --arg province "$PROVINCE" \
  '{
  mode: "upsert",
  rows: [{
    id: $id,
    nameCn: "冒烟批量更新",
    nameVn: "Smoke Baker VN",
    nameEn: "Smoke Baker",
    category: $category,
    background: $background,
    province: $province,
    keyCapabilities: "批量能力",
    coreServices: ["公司设立"]
  }]
}')
curl -sS "${AUTH[@]}" -X POST "$BASE/crm/professional-service/batch" -d "$BATCH" | jq .

echo "==> FILTER by category"
curl -sS "${AUTH[@]}" "$BASE/crm/professional-service/list?pageNum=1&pageSize=10&category=$(printf %s "$CATEGORY" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> FILTER by background"
curl -sS "${AUTH[@]}" "$BASE/crm/professional-service/list?pageNum=1&pageSize=10&background=$(printf %s "$BACKGROUND" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> FILTER by province"
curl -sS "${AUTH[@]}" "$BASE/crm/professional-service/list?pageNum=1&pageSize=10&province=$(printf %s "$PROVINCE" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> DELETE (soft)"
curl -sS "${AUTH[@]}" -X DELETE "$BASE/crm/professional-service/$ID" | jq .

echo "==> GET after delete (expect null data)"
curl -sS "${AUTH[@]}" "$BASE/crm/professional-service/$ID" | jq '{code, data}'

echo "Done."
