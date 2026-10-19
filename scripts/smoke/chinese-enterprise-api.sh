#!/usr/bin/env bash
# Smoke test: chinese enterprise CRUD on 9090
#
# Usage:
#   TOKEN=xxx TENANT=mega_demo ./scripts/smoke/chinese-enterprise-api.sh
#   CRM_BASE_URL=http://127.0.0.1:9090 TOKEN=xxx TENANT=xxx PARK_ID=vn-ip-xxx ./scripts/smoke/chinese-enterprise-api.sh
#
# Requires: curl, jq
# Optional: PARK_ID — existing crm_industrial_park.id for parkNameCn backfill check

set -euo pipefail

BASE="${CRM_BASE_URL:-http://127.0.0.1:9090}"
TOKEN="${TOKEN:-}"
TENANT="${TENANT:-}"
PARK_ID="${PARK_ID:-vn-ip-smoke-park}"

if [[ -z "$TOKEN" || -z "$TENANT" ]]; then
  echo "Error: TOKEN and TENANT are required." >&2
  exit 1
fi

AUTH=(-H "Authorization: Bearer $TOKEN" -H "tenant: $TENANT" -H "Content-Type: application/json")

echo "==> LIST"
curl -sS "${AUTH[@]}" "$BASE/crm/chinese-enterprise/list?pageNum=1&pageSize=10" | jq '{code, total, first: .rows[0].vietnamEntityNameCn}'

ID="cn-ent-smoke-$(date +%s)"
BODY=$(jq -n \
  --arg id "$ID" \
  --arg parkId "$PARK_ID" \
  '{
  id: $id,
  parentCompanyChina: "冒烟母公司",
  parentProvinceChina: "广东",
  vietnamEntityName: "Smoke CN Ent Vietnam",
  vietnamEntityNameCn: "冒烟中资企业",
  parkId: $parkId,
  parkNameCn: "冒烟园区名",
  provinceVietnam: "北宁",
  industry: "电子",
  stockListingStatus: "A股上市",
  investmentMode: "独资自建厂房",
  status: "运营中",
  taxCode: "SMOKE-CN-TAX-001"
}')

echo "==> CREATE $ID (parkId=$PARK_ID)"
CREATE_RESP=$(curl -sS "${AUTH[@]}" -X POST "$BASE/crm/chinese-enterprise" -d "$BODY")
echo "$CREATE_RESP" | jq '{code, id: .data.id, nameCn: .data.vietnamEntityNameCn, parkNameCn: .data.parkNameCn}'

echo "==> GET"
curl -sS "${AUTH[@]}" "$BASE/crm/chinese-enterprise/$ID" | jq '{code, nameCn: .data.vietnamEntityNameCn, parkId: .data.parkId, parkNameCn: .data.parkNameCn}'

echo "==> UPDATE"
UPD=$(echo "$BODY" | jq '.vietnamEntityNameCn = "冒烟中资企业-已改" | .employeeCount = 500')
curl -sS "${AUTH[@]}" -X PUT "$BASE/crm/chinese-enterprise" -d "$UPD" | jq '{code, nameCn: .data.vietnamEntityNameCn, emp: .data.employeeCount}'

echo "==> BATCH upsert"
BATCH=$(jq -n \
  --arg id "$ID" \
  --arg parkId "$PARK_ID" \
  '{
  mode: "upsert",
  rows: [{
    id: $id,
    parentCompanyChina: "冒烟母公司",
    parentProvinceChina: "广东",
    vietnamEntityName: "Smoke CN Ent Vietnam",
    vietnamEntityNameCn: "冒烟批量更新",
    parkId: $parkId,
    parkNameCn: "批量园区",
    provinceVietnam: "北宁",
    industry: "电子",
    stockListingStatus: "A股上市",
    investmentMode: "独资自建厂房",
    status: "建设中"
  }]
}')
curl -sS "${AUTH[@]}" -X POST "$BASE/crm/chinese-enterprise/batch" -d "$BATCH" | jq .

echo "==> FILTER by parkId"
curl -sS "${AUTH[@]}" "$BASE/crm/chinese-enterprise/list?pageNum=1&pageSize=10&parkId=$(printf %s "$PARK_ID" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> DELETE (soft)"
curl -sS "${AUTH[@]}" -X DELETE "$BASE/crm/chinese-enterprise/$ID" | jq .

echo "==> GET after delete (expect null data)"
curl -sS "${AUTH[@]}" "$BASE/crm/chinese-enterprise/$ID" | jq '{code, data}'

echo "Done."
