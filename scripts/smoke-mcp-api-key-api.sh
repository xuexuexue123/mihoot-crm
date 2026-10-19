#!/usr/bin/env bash
# Smoke test for inbound MCP API Key API (docs/mcp-server-backend-9090.md §8)
# Usage:
#   export CRM_BASE_URL=http://127.0.0.1:9090
#   export CRM_TENANT=tenant3
#   export CRM_TOKEN="<Bearer token from login>"
#   export MCP_VALIDATE_INTERNAL_SECRET=your-secret
# Or: ./scripts/smoke-mcp-api-key-api.sh --login zhou@mihoot.cn <your-password>
# Note: tenant seed user admin@mihoot.cn default password is SuperMihoot123$ (see TenantRegisterService)

set -euo pipefail

CRM_BASE_URL="${CRM_BASE_URL:-http://127.0.0.1:9090}"
CRM_TENANT="${CRM_TENANT:-tenant3}"
# 默认与 application.yml webhook.bff-secret / mcp.validate-internal-secret 回退值一致（本地开发）
MCP_VALIDATE_INTERNAL_SECRET="${MCP_VALIDATE_INTERNAL_SECRET:-wx138c52243086d3a2}"

if [[ "${1:-}" == "--login" ]]; then
  shift
  USER="${1:?username}"
  PASS="${2:?password}"
  CRM_TOKEN=$(python3 - <<PY
import json, urllib.request, urllib.error, redis, sys

def req(method, path, headers=None, body=None):
    h = dict(headers or {})
    data = None
    if body is not None:
        data = json.dumps(body).encode()
        h['Content-Type'] = 'application/json;charset=utf-8'
    r = urllib.request.Request('${CRM_BASE_URL}' + path, data=data, headers=h, method=method)
    try:
        with urllib.request.urlopen(r) as resp:
            return json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        raw = e.read().decode()
        try:
            payload = json.loads(raw)
        except json.JSONDecodeError:
            payload = {'msg': raw}
        print('Login HTTP %s: %s' % (e.code, payload.get('msg', raw)), file=sys.stderr)
        sys.exit(1)

try:
    r = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)
except Exception as ex:
    print('Redis required for captcha (--login): %s' % ex, file=sys.stderr)
    print('Alternatively: login in browser, copy token, export CRM_TOKEN=...', file=sys.stderr)
    sys.exit(1)

cap = req('GET', '/captchaImage')
if cap.get('captchaEnabled'):
    uuid = cap['uuid']
    code = r.get('captcha_codes:%s' % uuid)
    if not code:
        print('Captcha missing in redis (key captcha_codes:%s). Retry /captchaImage.' % uuid, file=sys.stderr)
        sys.exit(1)
    code = code.strip('"')
    body = {'username': '${USER}', 'password': '${PASS}', 'code': code, 'uuid': uuid}
else:
    body = {'username': '${USER}', 'password': '${PASS}', 'code': '', 'uuid': ''}
login = req('POST', '/login', {'tenant': '${CRM_TENANT}'}, body)
token = login.get('token')
if not token:
    print('Login failed: %s' % json.dumps(login, ensure_ascii=False), file=sys.stderr)
    sys.exit(1)
print(token)
PY
)
  export CRM_TOKEN
fi

if [[ -z "${CRM_TOKEN:-}" ]]; then
  echo "CRM_TOKEN is required (or use --login)" >&2
  exit 1
fi

AUTH_HEADER="Authorization: Bearer ${CRM_TOKEN}"
TENANT_HEADER="tenant: ${CRM_TENANT}"

echo "== POST /crm/mcp/api-key (create) =="
CREATE_RESP=$(curl -s -X POST "${CRM_BASE_URL}/crm/mcp/api-key" \
  -H "Content-Type: application/json" \
  -H "${AUTH_HEADER}" \
  -H "${TENANT_HEADER}" \
  -d '{"name":"smoke-mcp-key-'$(date +%s)'","allowedTools":["search_account"]}')
echo "$CREATE_RESP" | python3 -m json.tool

CODE=$(echo "$CREATE_RESP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',0))")
if [[ "$CODE" != "200" ]]; then
  echo "create failed" >&2
  if [[ "$CODE" == "403" ]]; then
    echo "Hint: run scripts/sql/seed-crm-mcp-api-key-menu.sql on the tenant DB," >&2
    echo "      then re-login so permissions reload (crm:mcp:apikey:edit)." >&2
  fi
  exit 1
fi

API_KEY=$(echo "$CREATE_RESP" | python3 -c "import sys,json; d=json.load(sys.stdin).get('data') or {}; print(d.get('apiKey',''))")
KEY_ID=$(echo "$CREATE_RESP" | python3 -c "import sys,json; d=json.load(sys.stdin).get('data') or {}; print(d.get('id',''))")

if [[ -z "$API_KEY" ]]; then
  echo "missing apiKey in create response" >&2
  exit 1
fi

echo "== assert tenantCode-in-key format =="
python3 - <<PY
import re, sys
api_key = """${API_KEY}"""
pattern = r"^mcp_[A-Za-z0-9_-]{8}_[a-f0-9]{12}_.+$"
if not re.match(pattern, api_key):
    print(f"apiKey does not match tenantCode-in-key format: {api_key!r}", file=sys.stderr)
    sys.exit(1)
print("apiKey format OK")
PY

echo "== GET /crm/mcp/api-key/list =="
curl -s "${CRM_BASE_URL}/crm/mcp/api-key/list" \
  -H "${AUTH_HEADER}" -H "${TENANT_HEADER}" | python3 -m json.tool

if [[ -n "$MCP_VALIDATE_INTERNAL_SECRET" ]]; then
  echo "== POST /crm/mcp/api-key/validate =="
  VALIDATE_RESP=$(curl -s -X POST "${CRM_BASE_URL}/crm/mcp/api-key/validate" \
    -H "Content-Type: application/json" \
    -H "X-Mcp-Internal-Secret: ${MCP_VALIDATE_INTERNAL_SECRET}" \
    -d "{\"apiKey\":\"${API_KEY}\"}")
  echo "$VALIDATE_RESP" | python3 -m json.tool
  VCODE=$(echo "$VALIDATE_RESP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',0))")
  TOKEN=$(echo "$VALIDATE_RESP" | python3 -c "import sys,json; d=json.load(sys.stdin).get('data') or {}; print(d.get('serviceToken',''))")
  if [[ "$VCODE" != "200" || -z "$TOKEN" ]]; then
    echo "validate failed" >&2
    if echo "$VALIDATE_RESP" | grep -q 'internal secret is not configured'; then
      echo "Hint: restart 9090 after setting mcp.validate-internal-secret or MCP_VALIDATE_INTERNAL_SECRET" >&2
    fi
    exit 1
  fi
  echo "validate OK (serviceToken present)"

  VALIDATE_TENANT=$(echo "$VALIDATE_RESP" | python3 -c "import sys,json; d=json.load(sys.stdin).get('data') or {}; print(d.get('tenant',''))")
  if [[ "$VALIDATE_TENANT" != "$CRM_TENANT" ]]; then
    echo "validate tenant mismatch: expected ${CRM_TENANT}, got ${VALIDATE_TENANT}" >&2
    exit 1
  fi
  echo "validate tenant OK (${VALIDATE_TENANT})"

  echo "== POST /crm/mcp/api-key/validate (no-segment old format negative) =="
  OLD_FORMAT_KEY=$(python3 - <<PY
import re
api_key = """${API_KEY}"""
m = re.match(r"^mcp_([A-Za-z0-9_-]{8})_([a-f0-9]{12})_(.+)$", api_key)
if not m:
    raise SystemExit("cannot derive old-format key from new key")
print(f"mcp_{m.group(2)}_{m.group(3)}")
PY
)
  OLD_VALIDATE_RESP=$(curl -s -X POST "${CRM_BASE_URL}/crm/mcp/api-key/validate" \
    -H "Content-Type: application/json" \
    -H "X-Mcp-Internal-Secret: ${MCP_VALIDATE_INTERNAL_SECRET}" \
    -d "{\"apiKey\":\"${OLD_FORMAT_KEY}\"}")
  echo "$OLD_VALIDATE_RESP" | python3 -m json.tool
  OLD_VCODE=$(echo "$OLD_VALIDATE_RESP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',0))")
  if [[ "$OLD_VCODE" == "200" ]]; then
    echo "expected no-segment old-format key to be rejected" >&2
    exit 1
  fi
  echo "no-segment old-format key rejected OK (code=${OLD_VCODE})"

  echo "== POST /crm/mcp/api-key/validate (plaintext tenant negative) =="
  PLAINTEXT_KEY=$(python3 - <<PY
import re
api_key = """${API_KEY}"""
tenant = """${CRM_TENANT}"""
m = re.match(r"^mcp_([A-Za-z0-9_-]{8})_([a-f0-9]{12})_(.+)$", api_key)
if not m:
    raise SystemExit("cannot derive plaintext-tenant key from new key")
print(f"mcp_{tenant}_{m.group(2)}_{m.group(3)}")
PY
)
  PLAINTEXT_VALIDATE_RESP=$(curl -s -X POST "${CRM_BASE_URL}/crm/mcp/api-key/validate" \
    -H "Content-Type: application/json" \
    -H "X-Mcp-Internal-Secret: ${MCP_VALIDATE_INTERNAL_SECRET}" \
    -d "{\"apiKey\":\"${PLAINTEXT_KEY}\"}")
  echo "$PLAINTEXT_VALIDATE_RESP" | python3 -m json.tool
  PLAINTEXT_VCODE=$(echo "$PLAINTEXT_VALIDATE_RESP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',0))")
  if [[ "$PLAINTEXT_VCODE" == "200" ]]; then
    echo "expected plaintext-tenant key to be rejected" >&2
    exit 1
  fi
  echo "plaintext-tenant key rejected OK (code=${PLAINTEXT_VCODE})"
else
  echo "skip validate (set MCP_VALIDATE_INTERNAL_SECRET)"
fi

if [[ -n "${KEY_ID}" ]]; then
  echo "== DELETE /crm/mcp/api-key/${KEY_ID} (soft disable) =="
  curl -s -X DELETE "${CRM_BASE_URL}/crm/mcp/api-key/${KEY_ID}" \
    -H "${AUTH_HEADER}" -H "${TENANT_HEADER}" | python3 -m json.tool
fi

echo "smoke-mcp-api-key-api.sh done"
