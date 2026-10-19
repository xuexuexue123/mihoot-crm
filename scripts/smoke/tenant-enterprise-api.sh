#!/usr/bin/env bash
# Smoke test: tenant enterprise CRUD on 9090
#
# Usage:
#   TOKEN=xxx TENANT=mega_demo ./scripts/smoke/tenant-enterprise-api.sh
#   CRM_BASE_URL=http://127.0.0.1:9090 TOKEN=xxx TENANT=xxx PARK_ID=vn-ip-xxx ./scripts/smoke/tenant-enterprise-api.sh
#
# Requires: curl, jq
# Optional: PARK_ID — existing crm_industrial_park.id for parkNameCn backfill check
#
# CSV 导入模板表头（前端对齐，UTF-8-BOM；9090 batch 走 JSON VO）:
#   企业编号, 企业外文名, 中文译名, 企业税务代码, 法定代表人, 所属园区, 行业分类, 主营产品,
#   资本类型, 母国/资本来源, 已知投资额($M), 员工人数, 租用面积(㎡), 投产年份, 运营状态,
#   联系电话, 电子邮箱, 官方网站, 地址

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
curl -sS "${AUTH[@]}" "$BASE/crm/tenant-enterprise/list?pageNum=1&pageSize=10" | jq '{code, total, first: .rows[0].nameCn}'

ID="ent-smoke-$(date +%s)"
BODY=$(jq -n \
  --arg id "$ID" \
  --arg parkId "$PARK_ID" \
  '{
  id: $id,
  parkId: $parkId,
  parkNameCn: "冒烟园区名",
  name: "Smoke Test Co Ltd",
  nameCn: "冒烟测试企业",
  establishedYear: 2020,
  registeredAddress: "Bac Ninh, Vietnam",
  industry: "电子",
  enterpriseType: "外商独资企业 (WFOE)",
  countryOfOrigin: "韩国",
  status: "运营中",
  taxCode: "SMOKE-TAX-001",
  legalRepresentative: "Nguyen Van Smoke"
}')

echo "==> CREATE $ID (parkId=$PARK_ID; WFOE should normalize to FDI)"
CREATE_RESP=$(curl -sS "${AUTH[@]}" -X POST "$BASE/crm/tenant-enterprise" -d "$BODY")
echo "$CREATE_RESP" | jq '{code, id: .data.id, nameCn: .data.nameCn, parkNameCn: .data.parkNameCn, enterpriseType: .data.enterpriseType, taxCode: .data.taxCode, legalRepresentative: .data.legalRepresentative}'

echo "==> GET"
curl -sS "${AUTH[@]}" "$BASE/crm/tenant-enterprise/$ID" | jq '{code, nameCn: .data.nameCn, parkId: .data.parkId, parkNameCn: .data.parkNameCn, enterpriseType: .data.enterpriseType, taxCode: .data.taxCode, legalRepresentative: .data.legalRepresentative}'

echo "==> UPDATE (status=已停止经营活动)"
UPD=$(echo "$BODY" | jq '.nameCn = "冒烟测试企业-已改" | .employeeCount = 500 | .status = "已停止经营活动" | .enterpriseType = "外国直接投资 (FDI)"')
curl -sS "${AUTH[@]}" -X PUT "$BASE/crm/tenant-enterprise" -d "$UPD" | jq '{code, nameCn: .data.nameCn, emp: .data.employeeCount, status: .data.status}'

echo "==> BATCH upsert"
BATCH=$(jq -n \
  --arg id "$ID" \
  --arg parkId "$PARK_ID" \
  '{
  mode: "upsert",
  rows: [{
    id: $id,
    parkId: $parkId,
    parkNameCn: "批量园区",
    name: "Smoke Test Co Ltd",
    nameCn: "冒烟批量更新",
    establishedYear: 2020,
    registeredAddress: "Bac Ninh, Vietnam",
    industry: "电子",
    enterpriseType: "外国直接投资 (FDI)",
    status: "已注销",
    taxCode: "SMOKE-TAX-001",
    legalRepresentative: "Tran Thi Batch"
  }]
}')
curl -sS "${AUTH[@]}" -X POST "$BASE/crm/tenant-enterprise/batch" -d "$BATCH" | jq .

echo "==> FILTER by parkId"
curl -sS "${AUTH[@]}" "$BASE/crm/tenant-enterprise/list?pageNum=1&pageSize=10&parkId=$(printf %s "$PARK_ID" | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> FILTER by enterpriseType=外国直接投资 (FDI) (should hit normalized row)"
ET_ENC=$(printf %s '外国直接投资 (FDI)' | jq -sRr @uri)
curl -sS "${AUTH[@]}" "$BASE/crm/tenant-enterprise/list?pageNum=1&pageSize=10&enterpriseType=${ET_ENC}" \
  | jq '{code, total}'

echo "==> SEARCH taxCode / legalRepresentative"
curl -sS "${AUTH[@]}" "$BASE/crm/tenant-enterprise/list?pageNum=1&pageSize=10&searchQuery=SMOKE-TAX-001" \
  | jq '{code, total, tax: .rows[0].taxCode}'
curl -sS "${AUTH[@]}" "$BASE/crm/tenant-enterprise/list?pageNum=1&pageSize=10&searchQuery=$(printf %s 'Tran Thi Batch' | jq -sRr @uri)" \
  | jq '{code, total, legal: .rows[0].legalRepresentative}'

echo "==> DELETE (soft)"
curl -sS "${AUTH[@]}" -X DELETE "$BASE/crm/tenant-enterprise/$ID" | jq .

echo "==> GET after delete (expect null data)"
curl -sS "${AUTH[@]}" "$BASE/crm/tenant-enterprise/$ID" | jq '{code, data}'

echo "Done."
