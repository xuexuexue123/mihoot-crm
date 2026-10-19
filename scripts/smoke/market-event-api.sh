#!/usr/bin/env bash
# Smoke test: market event CRUD on 9090
#
# Usage:
#   TOKEN=xxx TENANT=mega_demo ./scripts/smoke/market-event-api.sh
#   CRM_BASE_URL=http://127.0.0.1:9090 TOKEN=xxx TENANT=xxx ./scripts/smoke/market-event-api.sh
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
curl -sS "${AUTH[@]}" "$BASE/crm/market-event/list?pageNum=1&pageSize=10" | jq '{code, total, first: .rows[0].titleCn}'

ID="evt-smoke-$(date +%s)"
BODY=$(jq -n \
  --arg id "$ID" \
  '{
  id: $id,
  titleCn: "冒烟测试市场活动",
  titleVn: "Sự kiện khói thử nghiệm",
  titleEn: "Smoke Test Market Event",
  eventType: "展会/博览会 (Exhibition)",
  status: "即将举办 (Upcoming)",
  organizer: "Smoke Organizer",
  industry: "制造业",
  startDate: "2026-10-01",
  endDate: "2026-10-03",
  province: "河内",
  venue: "Smoke Venue Hall",
  targetAudience: "制造业企业",
  scale: "约 500 人",
  entryFee: "免费",
  highlights: "冒烟测试亮点与核心议题",
  notes: "赴越提示",
  coverUrl: "/profile/upload/smoke-cover.jpg"
}')

echo "==> CREATE $ID"
CREATE_RESP=$(curl -sS "${AUTH[@]}" -X POST "$BASE/crm/market-event" -d "$BODY")
echo "$CREATE_RESP" | jq '{code, id: .data.id, titleCn: .data.titleCn, eventType: .data.eventType, status: .data.status, coverUrl: .data.coverUrl}'

echo "==> GET"
curl -sS "${AUTH[@]}" "$BASE/crm/market-event/$ID" | jq '{code, titleCn: .data.titleCn, province: .data.province, venue: .data.venue, coverUrl: .data.coverUrl}'

echo "==> UPDATE (status=进行中 + coverUrl)"
UPD=$(echo "$BODY" | jq '.titleCn = "冒烟测试市场活动-已改" | .status = "进行中 (Ongoing)" | .coverUrl = "/profile/upload/smoke-cover-v2.jpg"')
curl -sS "${AUTH[@]}" -X PUT "$BASE/crm/market-event" -d "$UPD" | jq '{code, titleCn: .data.titleCn, status: .data.status, coverUrl: .data.coverUrl}'

echo "==> BATCH upsert"
BATCH=$(jq -n \
  --arg id "$ID" \
  '{
  mode: "upsert",
  rows: [{
    id: $id,
    titleCn: "冒烟批量更新",
    titleVn: "Sự kiện khói thử nghiệm",
    titleEn: "Smoke Test Market Event",
    eventType: "展会/博览会 (Exhibition)",
    status: "已结束 (Completed)",
    organizer: "Smoke Organizer",
    industry: "制造业",
    startDate: "2026-10-01",
    endDate: "2026-10-03",
    province: "河内",
    venue: "Smoke Venue Hall",
    targetAudience: "制造业企业",
    highlights: "批量更新亮点"
  }]
}')
curl -sS "${AUTH[@]}" -X POST "$BASE/crm/market-event/batch" -d "$BATCH" | jq .

echo "==> FILTER by eventType"
ET_ENC=$(printf %s '展会/博览会 (Exhibition)' | jq -sRr @uri)
curl -sS "${AUTH[@]}" "$BASE/crm/market-event/list?pageNum=1&pageSize=10&eventType=${ET_ENC}" \
  | jq '{code, total}'

echo "==> FILTER by status"
ST_ENC=$(printf %s '已结束 (Completed)' | jq -sRr @uri)
curl -sS "${AUTH[@]}" "$BASE/crm/market-event/list?pageNum=1&pageSize=10&status=${ST_ENC}" \
  | jq '{code, total}'

echo "==> FILTER by province"
curl -sS "${AUTH[@]}" "$BASE/crm/market-event/list?pageNum=1&pageSize=10&province=$(printf %s '河内' | jq -sRr @uri)" \
  | jq '{code, total}'

echo "==> SEARCH"
curl -sS "${AUTH[@]}" "$BASE/crm/market-event/list?pageNum=1&pageSize=10&searchQuery=$(printf %s '冒烟批量' | jq -sRr @uri)" \
  | jq '{code, total, title: .rows[0].titleCn}'

echo "==> DELETE (soft)"
curl -sS "${AUTH[@]}" -X DELETE "$BASE/crm/market-event/$ID" | jq .

echo "==> GET after delete (expect null data)"
curl -sS "${AUTH[@]}" "$BASE/crm/market-event/$ID" | jq '{code, data}'

echo "Done."
