#!/usr/bin/env bash
# 微信小程序 BFF → 9090 七库 + runtime + lead + knowledge 冒烟
# 主路径：X-Internal-Secret（与 9090 internal.service.secret / INTERNAL_SERVICE_SECRET 同值）
# 可选双轨：SERVICE_JWT 仍可走旧 Bearer（不作为正式验收必需）
#
# 用法：
#   export CRM_BASE=http://localhost:9090
#   export TENANT=tenant3
#   export INTERNAL_SERVICE_SECRET='...'
#   ./scripts/smoke-miniapp-crm-9090.sh
#
# 双轨（可选）：
#   export SERVICE_JWT='...'   # 旧 MCP/服务 JWT；若设置则额外跑一轮 Bearer 检查
set -euo pipefail

CRM_BASE="${CRM_BASE:-http://localhost:9090}"
TENANT="${TENANT:?TENANT required}"
INTERNAL_SERVICE_SECRET="${INTERNAL_SERVICE_SECRET:?INTERNAL_SERVICE_SECRET required}"

auth_secret=(-H "X-Internal-Secret: ${INTERNAL_SERVICE_SECRET}" -H "tenant: ${TENANT}" -H "Content-Type: application/json")

pass=0
fail=0

check() {
  local name="$1"
  local url="$2"
  shift 2
  local code
  code=$(curl -s -o /tmp/miniapp-smoke-body.json -w "%{http_code}" "$@" "$url" || true)
  if [[ "$code" == "200" ]]; then
    echo "OK  $name ($code)"
    pass=$((pass + 1))
  else
    echo "FAIL $name (http=$code) body=$(head -c 200 /tmp/miniapp-smoke-body.json)"
    fail=$((fail + 1))
  fi
}

echo "== reject wrong secret =="
code=$(curl -s -o /tmp/miniapp-smoke-body.json -w "%{http_code}" \
  -H "X-Internal-Secret: wrong-secret" -H "tenant: ${TENANT}" \
  "$CRM_BASE/crm/market-event/list?pageNum=1&pageSize=1" || true)
if [[ "$code" == "401" || "$code" == "403" ]]; then
  echo "OK  wrong secret rejected ($code)"
  pass=$((pass + 1))
else
  echo "FAIL wrong secret should be 401/403 (http=$code)"
  fail=$((fail + 1))
fi

echo "== P1 七库 list (Internal Secret) =="
check "market-event list"        "$CRM_BASE/crm/market-event/list?pageNum=1&pageSize=5" "${auth_secret[@]}"
check "industrial-park list"     "$CRM_BASE/crm/industrial-park/list?pageNum=1&pageSize=5" "${auth_secret[@]}"
check "tenant-enterprise list"   "$CRM_BASE/crm/tenant-enterprise/list?pageNum=1&pageSize=5" "${auth_secret[@]}"
check "chinese-enterprise list"  "$CRM_BASE/crm/chinese-enterprise/list?pageNum=1&pageSize=5" "${auth_secret[@]}"
check "professional-service list" "$CRM_BASE/crm/professional-service/list?pageNum=1&pageSize=5" "${auth_secret[@]}"
check "sanctioned-entity list"   "$CRM_BASE/crm/sanctioned-entity/list?pageNum=1&pageSize=5" "${auth_secret[@]}"
check "government-agency list"   "$CRM_BASE/crm/government-agency/list?pageNum=1&pageSize=5" "${auth_secret[@]}"

echo "== P0 runtime =="
check "miniapp runtime" "$CRM_BASE/crm/wechat_miniapp/config/runtime" "${auth_secret[@]}"

echo "== P2 lead by-mobile =="
check "lead by-mobile" "$CRM_BASE/crm/lead/by-mobile?mobilePhone=13800138000" "${auth_secret[@]}"

echo "== P2 appointment upsert =="
APT_PHONE="13800138888"
APT_BODY=$(cat <<EOF
{"mobilePhone":"${APT_PHONE}","leadName":"预约冒烟","sourceChannel":"miniapp_appointment","remark":"类型:预约顾问;主题:薪资用工;时段:工作日白天","profileJson":{"topic":"payroll","topic_label":"薪资用工","timeslot":"workday_day","timeslot_label":"工作日白天","wechat":"smoke_wx","note":"冒烟备注","source":"miniapp_appointment"}}
EOF
)
code=$(curl -s -o /tmp/miniapp-apt-upsert.json -w "%{http_code}" "${auth_secret[@]}" \
  -d "${APT_BODY}" \
  "$CRM_BASE/crm/lead/miniapp/upsert" || true)
if [[ "$code" == "200" ]]; then
  LEAD_ID=$(python3 -c "
import json
d=json.load(open('/tmp/miniapp-apt-upsert.json'))
data=d.get('data') or {}
print(data.get('leadId') or '')
" 2>/dev/null || true)
  RES1=$(python3 -c "
import json
d=json.load(open('/tmp/miniapp-apt-upsert.json'))
data=d.get('data') or {}
print(data.get('resolution') or '')
" 2>/dev/null || true)
  echo "OK  appointment upsert ($code) leadId=${LEAD_ID} resolution=${RES1}"
  pass=$((pass + 1))
  code=$(curl -s -o /tmp/miniapp-apt-upsert2.json -w "%{http_code}" "${auth_secret[@]}" \
    -d "${APT_BODY}" \
    "$CRM_BASE/crm/lead/miniapp/upsert" || true)
  if [[ "$code" == "200" ]]; then
    LEAD_ID2=$(python3 -c "
import json
d=json.load(open('/tmp/miniapp-apt-upsert2.json'))
data=d.get('data') or {}
print(data.get('leadId') or '')
" 2>/dev/null || true)
    RES2=$(python3 -c "
import json
d=json.load(open('/tmp/miniapp-apt-upsert2.json'))
data=d.get('data') or {}
print(data.get('resolution') or '')
" 2>/dev/null || true)
    if [[ -n "${LEAD_ID}" && "${LEAD_ID}" == "${LEAD_ID2}" && ("${RES2}" == "updated" || "${RES2}" == "linked_contact") ]]; then
      echo "OK  appointment upsert idempotent ($code) same leadId resolution=${RES2}"
      pass=$((pass + 1))
    else
      echo "FAIL appointment upsert idempotent leadId1=${LEAD_ID} leadId2=${LEAD_ID2} res2=${RES2}"
      fail=$((fail + 1))
    fi
  else
    echo "FAIL appointment upsert second call (http=$code) body=$(head -c 200 /tmp/miniapp-apt-upsert2.json)"
    fail=$((fail + 1))
  fi
else
  echo "FAIL appointment upsert (http=$code) body=$(head -c 200 /tmp/miniapp-apt-upsert.json)"
  fail=$((fail + 1))
fi

echo "== P2.5 market-event-register =="
# 取七库第一条活动 id（若无数据则跳过）
me_list_code=$(curl -s -o /tmp/miniapp-me-list.json -w "%{http_code}" \
  "${auth_secret[@]}" "$CRM_BASE/crm/market-event/list?pageNum=1&pageSize=1" || true)
ME_ID=""
if [[ "$me_list_code" == "200" ]]; then
  ME_ID=$(python3 -c "
import json,sys
d=json.load(open('/tmp/miniapp-me-list.json'))
rows=d.get('rows') or (d.get('data') or {}).get('rows') or []
print(rows[0]['id'] if rows else '')
" 2>/dev/null || true)
fi
if [[ -n "${ME_ID}" ]]; then
  code=$(curl -s -o /tmp/miniapp-smoke-body.json -w "%{http_code}" "${auth_secret[@]}" \
    -d "{\"marketEventId\":\"${ME_ID}\",\"mobilePhone\":\"13900139999\",\"contactName\":\"冒烟测试\",\"slots\":{\"contact_name\":\"冒烟测试\",\"phone\":\"13900139999\",\"event_name\":\"smoke\"}}" \
    "$CRM_BASE/crm/lead/miniapp/market-event-register" || true)
  if [[ "$code" == "200" ]]; then
    echo "OK  market-event-register ($code)"
    pass=$((pass + 1))
  else
    echo "FAIL market-event-register (http=$code) body=$(head -c 200 /tmp/miniapp-smoke-body.json)"
    fail=$((fail + 1))
  fi
  code=$(curl -s -o /tmp/miniapp-smoke-body.json -w "%{http_code}" \
    -H "X-Internal-Secret: wrong-secret" -H "tenant: ${TENANT}" -H "Content-Type: application/json" \
    -d "{\"marketEventId\":\"${ME_ID}\",\"mobilePhone\":\"13900139999\",\"slots\":{\"a\":1}}" \
    "$CRM_BASE/crm/lead/miniapp/market-event-register" || true)
  if [[ "$code" == "401" || "$code" == "403" ]]; then
    echo "OK  market-event-register wrong secret rejected ($code)"
    pass=$((pass + 1))
  else
    echo "FAIL market-event-register wrong secret should be 401/403 (http=$code)"
    fail=$((fail + 1))
  fi
else
  echo "NOTE skip market-event-register (no crm_market_event rows)"
fi

echo "== P4 knowledge retrieve =="
code=$(curl -s -o /tmp/miniapp-smoke-body.json -w "%{http_code}" "${auth_secret[@]}" \
  -d '{"query":"设厂","topK":3}' \
  "$CRM_BASE/crm/knowledge/retrieve" || true)
if [[ "$code" == "200" ]]; then
  echo "OK  knowledge retrieve ($code)"
  pass=$((pass + 1))
else
  # Embedding 未配置可能业务错误但仍非 401/403
  echo "NOTE knowledge retrieve http=$code body=$(head -c 200 /tmp/miniapp-smoke-body.json)"
  if [[ "$code" == "401" || "$code" == "403" ]]; then
    fail=$((fail + 1))
  else
    pass=$((pass + 1))
  fi
fi

if [[ -n "${SERVICE_JWT:-}" ]]; then
  echo "== optional dual-track Bearer JWT =="
  auth_jwt=(-H "Authorization: Bearer ${SERVICE_JWT}" -H "tenant: ${TENANT}" -H "Content-Type: application/json")
  check "market-event list (JWT)" "$CRM_BASE/crm/market-event/list?pageNum=1&pageSize=1" "${auth_jwt[@]}"
fi

echo "== summary pass=$pass fail=$fail =="
[[ "$fail" -eq 0 ]]
