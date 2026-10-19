#!/usr/bin/env bash
# Smoke test: industrial park CRUD on 9090
#
# Usage:
#   TOKEN=xxx TENANT=mega_demo ./scripts/smoke/industrial-park-api.sh
#   CRM_BASE_URL=http://127.0.0.1:9090 TOKEN=xxx TENANT=xxx ./scripts/smoke/industrial-park-api.sh
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
curl -sS "${AUTH[@]}" "$BASE/crm/industrial-park/list?pageNum=1&pageSize=10" | jq '{code, total, first: .rows[0].nameCn}'

ID="vn-ip-smoke-$(date +%s)"
BODY=$(jq -n --arg id "$ID" '{
  id: $id,
  nameCn: "冒烟测试工业园",
  nameNative: "Smoke Test Park",
  region: "北部",
  province: "Bac Ninh",
  dominantIndustries: ["电子"],
  encouragedIndustries: ["半导体", "新能源"],
  anchorTenants: ["Samsung"],
  photos: [],
  isSez: false,
  nearestHighwayName: "国道1号",
  distanceToHighwayKm: 3.5,
  hasNationalGrid: true,
  hasStandbyPower: false,
  competitiveAdvantages: "靠近港口与高速",
  verificationStatus: "未核实"
}')

echo "==> CREATE $ID"
curl -sS "${AUTH[@]}" -X POST "$BASE/crm/industrial-park" -d "$BODY" | jq '{code, id: .data.id, nameCn: .data.nameCn}'

echo "==> GET"
curl -sS "${AUTH[@]}" "$BASE/crm/industrial-park/$ID" | jq '{
  code,
  nameCn: .data.nameCn,
  industries: .data.dominantIndustries,
  encouraged: .data.encouragedIndustries,
  highway: .data.nearestHighwayName,
  hasNationalGrid: .data.hasNationalGrid
}'

echo "==> UPDATE"
UPD=$(echo "$BODY" | jq '.nameCn = "冒烟测试工业园-已改" | .occupancyRate = 66.5 | .hasStandbyPower = true | .operationExpiryDate = "2058-01-29"')
curl -sS "${AUTH[@]}" -X PUT "$BASE/crm/industrial-park" -d "$UPD" | jq '{
  code,
  nameCn: .data.nameCn,
  occ: .data.occupancyRate,
  hasStandbyPower: .data.hasStandbyPower,
  operationExpiryDate: .data.operationExpiryDate
}'

echo "==> BATCH upsert"
BATCH=$(jq -n --arg id "$ID" '{
  mode: "upsert",
  rows: [{
    id: $id,
    nameCn: "冒烟批量更新",
    region: "北部",
    dominantIndustries: ["电子","机械"],
    encouragedIndustries: ["物流"],
    photos: [],
    waterSupplySource: "水库",
    waterSupplyCapacity: "300,000 m³/日"
  }]
}')
curl -sS "${AUTH[@]}" -X POST "$BASE/crm/industrial-park/batch" -d "$BATCH" | jq .

echo "==> DELETE (soft)"
curl -sS "${AUTH[@]}" -X DELETE "$BASE/crm/industrial-park/$ID" | jq .

echo "==> GET after delete (expect null data)"
curl -sS "${AUTH[@]}" "$BASE/crm/industrial-park/$ID" | jq '{code, data}'

echo "Done."
