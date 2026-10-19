#!/usr/bin/env bash
# Smoke test for MCP Server API + MCP_CLIENT node definition (docs/mcp-client-node-backend-9090.md §8.1)
# Usage:
#   export CRM_BASE_URL=http://127.0.0.1:9090
#   export CRM_TENANT=tenant3
#   export CRM_TOKEN="<Bearer token from login>"
# Or run with auto-login (requires redis-cli or python redis, captcha enabled):
#   ./scripts/smoke-mcp-server-api.sh --login zhou@mihoot.cn <your-password>

set -euo pipefail

CRM_BASE_URL="${CRM_BASE_URL:-http://127.0.0.1:9090}"
CRM_TENANT="${CRM_TENANT:-tenant3}"

if [[ "${1:-}" == "--login" ]]; then
  shift
  USER="${1:?username}"
  PASS="${2:?password}"
  CRM_TOKEN=$(python3 - <<PY
import json, urllib.request, redis, sys

def req(method, path, headers=None, body=None):
    h = dict(headers or {})
    data = None
    if body is not None:
        data = json.dumps(body).encode()
        h['Content-Type'] = 'application/json;charset=utf-8'
    r = urllib.request.Request('${CRM_BASE_URL}' + path, data=data, headers=h, method=method)
    with urllib.request.urlopen(r) as resp:
        return json.loads(resp.read().decode())

r = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)
cap = req('GET', '/captchaImage')
if cap.get('captchaEnabled'):
    uuid = cap['uuid']
    code = r.get(f'captcha_codes:{uuid}').strip('"')
    body = {'username': '${USER}', 'password': '${PASS}', 'code': code, 'uuid': uuid}
else:
    body = {'username': '${USER}', 'password': '${PASS}', 'code': '', 'uuid': ''}
login = req('POST', '/login', {'tenant': '${CRM_TENANT}'}, body)
token = login.get('token')
if not token:
    print(json.dumps(login), file=sys.stderr)
    sys.exit(1)
print(token)
PY
)
  export CRM_TOKEN
fi

: "${CRM_TOKEN:?Set CRM_TOKEN or use --login user pass}"

AUTH_HEADER="Authorization: Bearer ${CRM_TOKEN}"

echo "== GET /crm/mcp/server/list =="
curl -sf -H "$AUTH_HEADER" -H "tenant: $CRM_TENANT" \
  "$CRM_BASE_URL/crm/mcp/server/list" | python3 -m json.tool

echo "== GET /crm/mcp/server/credentials =="
curl -sf -H "$AUTH_HEADER" -H "tenant: $CRM_TENANT" \
  "$CRM_BASE_URL/crm/mcp/server/credentials" | python3 -m json.tool

echo "== GET /crm/wnodedefinition/list?type=MCP_CLIENT =="
curl -sf -H "$AUTH_HEADER" -H "tenant: $CRM_TENANT" \
  "$CRM_BASE_URL/crm/wnodedefinition/list?type=MCP_CLIENT" | python3 -m json.tool

echo "Smoke checks completed."
