package com.ruoyi.crm.wechatmp.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/**
 * 内置公众号菜单 Starter 模板
 */
public final class WechatMpMenuTemplates
{
    private WechatMpMenuTemplates() {}

    public static List<Map<String, Object>> listTemplates()
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> dental = new HashMap<>();
        dental.put("templateId", "starter_dental_v1");
        dental.put("name", "口腔诊所通用（了解我们/项目中心/立即预约）");
        dental.put("description", "一级菜单：了解我们、项目中心、立即预约");
        dental.put("menuJson", buildDentalMenu());
        list.add(dental);
        return list;
    }

    private static JSONObject buildDentalMenu()
    {
        JSONObject menu = new JSONObject();
        JSONArray buttons = new JSONArray();

        JSONObject about = new JSONObject();
        about.put("name", "了解我们");
        about.put("type", "view");
        about.put("url", "{{BASE_URL}}/about");

        JSONObject projects = new JSONObject();
        projects.put("name", "项目中心");
        JSONArray sub = new JSONArray();
        JSONObject implant = new JSONObject();
        implant.put("type", "click");
        implant.put("name", "种植牙");
        implant.put("key", "MENU_IMPLANT");
        sub.add(implant);
        projects.put("sub_button", sub);

        JSONObject book = new JSONObject();
        book.put("name", "立即预约");
        book.put("type", "view");
        book.put("url", "{{BASE_URL}}/book?event_id={{EVENT_ID}}");

        buttons.add(about);
        buttons.add(projects);
        buttons.add(book);
        menu.put("button", buttons);
        return menu;
    }

    public static void validateMenuJson(String menuJsonStr)
    {
        JSONObject menu = JSON.parseObject(menuJsonStr);
        JSONArray buttons = menu.getJSONArray("button");
        if (buttons == null || buttons.isEmpty())
        {
            throw new IllegalArgumentException("菜单至少包含一个一级按钮");
        }
        if (buttons.size() > 3)
        {
            throw new IllegalArgumentException("一级菜单不能超过 3 个");
        }
        for (int i = 0; i < buttons.size(); i++)
        {
            JSONObject btn = buttons.getJSONObject(i);
            JSONArray sub = btn.getJSONArray("sub_button");
            if (sub != null && sub.size() > 5)
            {
                throw new IllegalArgumentException("二级菜单不能超过 5 个");
            }
        }
    }

    public static void syncMenuKeyLabels(String appId, String menuJsonStr,
            com.ruoyi.crm.wechatmp.mapper.CrmWechatMpQrcodeMapper qrcodeMapper)
    {
        JSONObject menu = JSON.parseObject(menuJsonStr);
        JSONArray buttons = menu.getJSONArray("button");
        if (buttons == null)
        {
            return;
        }
        collectClickButtons(appId, buttons, qrcodeMapper);
    }

    private static void collectClickButtons(String appId, JSONArray buttons,
            com.ruoyi.crm.wechatmp.mapper.CrmWechatMpQrcodeMapper qrcodeMapper)
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            JSONObject btn = buttons.getJSONObject(i);
            JSONArray sub = btn.getJSONArray("sub_button");
            if (sub != null)
            {
                collectClickButtons(appId, sub, qrcodeMapper);
            }
            else if ("click".equals(btn.getString("type")))
            {
                String key = btn.getString("key");
                String name = btn.getString("name");
                if (key != null && name != null)
                {
                    qrcodeMapper.upsertMenuKeyLabel(appId, key, name);
                }
            }
        }
    }
}
