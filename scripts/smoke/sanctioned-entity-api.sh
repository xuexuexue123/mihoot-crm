#!/usr/bin/env bash
# Smoke test: sanctioned entity CRUD on 9090
#
# Usage:
#   TOKEN=xxx TENANT=mega_demo ./scripts/smoke/sanctioned-entity-api.sh
#   CRM_BASE_URL=http://127.0.0.1:9090 TOKEN=xxx TENANT=xxx ./scripts/smoke/sanctioned-entity-api.sh
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
curl -sS "${AUTH[@]}" "$BASE/crm/sanctioned-entity/list?pageNum=1&pageSize=10" | jq '{code, total, first: .rows[0].nameCn}'

ID="sanc-smoke-$(date +%s)"
ENTITY_TYPE="企业"
RISK_CATEGORY="美国OFAC/EU/UN全球制裁与UFLPA"
RISK_LEVEL="红灯 (高危)"

BODY=$(jq -n \
  --arg id "$ID" \
  --arg entityType "$ENTITY_TYPE" \
  --arg riskCategory "$RISK_CATEGORY" \
  --arg riskLevel "$RISK_LEVEL" \
  '{
  id: $id,
  nameCn: "冒烟制裁主体",
  nameVn: "Smoke Sanction VN",
  nameEn: "Smoke Sanction Entity",
  aka: ["Smoke Alias"],
  entityType: $entityType,
  taxCode: "0123456789",
  riskCategory: $riskCategory,
  riskReason: "冒烟制裁原因",
  issuingAgency: "OFAC",
  riskLevel: $riskLevel,
  effectiveDate: "2024-01-01",
  matchedEnterpriseCode: "0123456789",
  notes: "冒烟应对建议"
}')

echo "==> CREATE $ID"
CREATE_RESP=$(curl -sS "${AUTH[@]}" -X POST "$BASE/crm/sanctioned-entity" -d "$BODY")
echo "$CREATE_RESP" | jq '{code, id: .data.id, nameCn: .data.nameCn, aka: .data.aka}'

echo "==> GET"
curl -sS "${AUTH[@]}" "$BASE/crm/sanctioned-entity/$ID" | jq '{code, nameCn: .data.nameCn, riskLevel: .data.riskLevel, aka: .data.aka}'

echo "==> UPDATE"
UPD=$(echo "$BODY" | jq '.nameCn = "冒烟制裁主体-已改" | .aka = ["Smoke Alias", "Former Name"]')
curl -sS "${AUTH[@]}" -X PUT "$BASE/crm/sanctioned-entity" -d "$UPD" | jq '{code, nameCn: .data.nameCn, aka: .data.aka}'

echo "==> BATCH upsert"
BATCH=$(jq -n \
  --arg id "$ID" \
  --arg entityType "$ENTITY_TYPE" \
  --arg riskCategory "$RISK_CATEGORY" \
  --arg riskLevel "$RISK_LEVEL" \
  '{
  mode: "upsert",
  rows: [{
    id: $id,
    nameCn: "冒烟批量更新",
    nameVn: "Smoke Sanction VN",
    nameEn: "Smoke Sanction Entity",
    aka: ["Batch Alias"],
    entityType: $entityType,
    riskCategory: $riskCategory,
    riskReason: "批量制裁原因",
    issuingAgency: "OFAC",
    riskLevel: $riskLevel,
    effectiveDate: "2024-01-01"
  }]
}')
curl -sS "${AUTH[@]}" -X POST "$BASE/crm/sanctioned-entity/batch" -d "$BATCH" | jq .

echo "==> FILTER by riskLevel"
curl -sS "${AUTH[@]}" "$BASE/crm/sanctioned-entity/list?pageNum=1&pageSize=10&riskLevel=$(printf %s "$RISK_LEVEL" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> FILTER by riskCategory"
curl -sS "${AUTH[@]}" "$BASE/crm/sanctioned-entity/list?pageNum=1&pageSize=10&riskCategory=$(printf %s "$RISK_CATEGORY" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> FILTER by entityType"
curl -sS "${AUTH[@]}" "$BASE/crm/sanctioned-entity/list?pageNum=1&pageSize=10&entityType=$(printf %s "$ENTITY_TYPE" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> DELETE (soft)"
curl -sS "${AUTH[@]}" -X DELETE "$BASE/crm/sanctioned-entity/$ID" | jq .

echo "==> GET after delete (expect null data)"
curl -sS "${AUTH[@]}" "$BASE/crm/sanctioned-entity/$ID" | jq '{code, data}'

echo "Done."
