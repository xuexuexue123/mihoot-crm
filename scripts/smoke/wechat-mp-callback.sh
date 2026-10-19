#!/usr/bin/env bash
# Smoke test for WeChat MP callback pipeline (requires running backend on port 9090).
set -euo pipefail

BASE_URL="${BASE_URL:-http://127.0.0.1:9090}"
APP_ID="${APP_ID:-wx_test_appid}"

echo "==> POST subscribe event"
curl -sS -X POST "${BASE_URL}/crm/wechat_mp/callback/${APP_ID}" \
  -H "Content-Type: text/xml" \
  -d "<xml><ToUserName><![CDATA[gh_test]]></ToUserName><FromUserName><![CDATA[o_smoke_subscribe]]></FromUserName><CreateTime>1718697600</CreateTime><MsgType><![CDATA[event]]></MsgType><Event><![CDATA[subscribe]]></Event><EventKey><![CDATA[qrscene_test]]></EventKey><MsgId><![CDATA[smoke_sub_001]]></MsgId></xml>"
echo ""

echo "==> POST text message"
curl -sS -X POST "${BASE_URL}/crm/wechat_mp/callback/${APP_ID}" \
  -H "Content-Type: text/xml" \
  -d "<xml><ToUserName><![CDATA[gh_test]]></ToUserName><FromUserName><![CDATA[o_smoke_subscribe]]></FromUserName><CreateTime>1718697700</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[hello smoke]]></Content><MsgId><![CDATA[smoke_msg_001]]></MsgId></xml>"
echo ""

echo "==> POST menu CLICK"
curl -sS -X POST "${BASE_URL}/crm/wechat_mp/callback/${APP_ID}" \
  -H "Content-Type: text/xml" \
  -d "<xml><ToUserName><![CDATA[gh_test]]></ToUserName><FromUserName><![CDATA[o_smoke_subscribe]]></FromUserName><CreateTime>1718697800</CreateTime><MsgType><![CDATA[event]]></MsgType><Event><![CDATA[CLICK]]></Event><EventKey><![CDATA[MENU_TEST]]></EventKey><MsgId><![CDATA[smoke_click_001]]></MsgId></xml>"
echo ""

echo "Smoke requests sent. Verify crm_wechat_mp_callback_log and crm_activity (channel=wechat_mp)."
