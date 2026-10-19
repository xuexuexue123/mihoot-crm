package com.ruoyi.crm.sfexpress;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmSfExpressConfig;
import com.ruoyi.crm.mapper.CrmSfExpressConfigMapper;

/**
 * 顺丰丰桥业务封装（读取租户配置、解密 checkword、归一化响应）。
 */
@Service
public class SfExpressApiService
{
    @Autowired
    private CrmSfExpressConfigMapper crmSfExpressConfigMapper;

    @Autowired
    private SfExpressFengqiaoApiClient fengqiaoApiClient;

    public Map<String, Object> createOrder(JSONObject msgData)
    {
        ResolvedConfig resolved = requireConfig();
        JSONObject orderBody = msgData != null ? new JSONObject(msgData) : new JSONObject();
        if (StringUtils.isNotBlank(resolved.config.getMonthlyCard()) && !orderBody.containsKey("monthlyCard"))
        {
            orderBody.put("monthlyCard", resolved.config.getMonthlyCard());
        }

        JSONObject top = fengqiaoApiClient.call(resolved.config, resolved.checkword,
                SfExpressFengqiaoApiClient.SERVICE_CREATE_ORDER, orderBody.toJSONString());
        JSONObject inner = fengqiaoApiClient.parseBusinessResult(top, true);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", orderBody.getString("orderId"));
        result.put("waybillNo", extractWaybillNo(inner));
        result.put("filterResult", extractFilterResult(inner));
        result.put("raw", buildRaw(top, inner));
        return result;
    }

    public Map<String, Object> searchRoutes(String waybillNo)
    {
        if (StringUtils.isBlank(waybillNo))
        {
            throw new ServiceException("waybillNo 不能为空");
        }
        ResolvedConfig resolved = requireConfig();

        JSONObject msgData = new JSONObject();
        msgData.put("language", "0");
        msgData.put("trackingType", 1);
        msgData.put("trackingNumber", new String[] { waybillNo.trim() });
        msgData.put("methodType", 1);

        JSONObject top = fengqiaoApiClient.call(resolved.config, resolved.checkword,
                SfExpressFengqiaoApiClient.SERVICE_SEARCH_ROUTES, msgData.toJSONString());
        JSONObject inner = fengqiaoApiClient.parseBusinessResult(top, true);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("waybillNo", waybillNo.trim());
        result.put("routes", extractRoutes(inner, waybillNo.trim()));
        result.put("raw", buildRaw(top, inner));
        return result;
    }

    public Map<String, Object> cancelOrder(String waybillNo, String orderId)
    {
        if (StringUtils.isBlank(waybillNo) && StringUtils.isBlank(orderId))
        {
            throw new ServiceException("waybillNo 与 orderId 至少填一项");
        }
        ResolvedConfig resolved = requireConfig();

        JSONObject msgData = new JSONObject();
        msgData.put("dealType", 2);
        if (StringUtils.isNotBlank(orderId))
        {
            msgData.put("orderId", orderId.trim());
        }
        if (StringUtils.isNotBlank(waybillNo))
        {
            JSONArray waybillList = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("waybillNo", waybillNo.trim());
            item.put("waybillType", 1);
            waybillList.add(item);
            msgData.put("waybillNoInfoList", waybillList);
        }

        JSONObject top = fengqiaoApiClient.call(resolved.config, resolved.checkword,
                SfExpressFengqiaoApiClient.SERVICE_UPDATE_ORDER, msgData.toJSONString());
        JSONObject inner = fengqiaoApiClient.parseBusinessResult(top, true);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", inner == null || !inner.containsKey("success") || Boolean.TRUE.equals(inner.getBoolean("success")));
        if (StringUtils.isNotBlank(waybillNo))
        {
            result.put("waybillNo", waybillNo.trim());
        }
        result.put("raw", buildRaw(top, inner));
        return result;
    }

    public boolean probeConnectivity(CrmSfExpressConfig config, String checkwordPlain)
    {
        return fengqiaoApiClient.probeConnectivity(config, checkwordPlain);
    }

    private ResolvedConfig requireConfig()
    {
        CrmSfExpressConfig config = crmSfExpressConfigMapper.selectFirst();
        if (config == null || StringUtils.isBlank(config.getCustomerCode())
                || StringUtils.isBlank(config.getCheckwordEnc()))
        {
            throw new ServiceException("请先配置顺丰集成", 500);
        }
        try
        {
            String checkword = AESUtil.decrypt(config.getCheckwordEnc());
            return new ResolvedConfig(config, checkword);
        }
        catch (Exception e)
        {
            throw new ServiceException("顺丰校验码解密失败", 500);
        }
    }

    private static String extractWaybillNo(JSONObject inner)
    {
        if (inner == null)
        {
            return null;
        }
        JSONObject msgData = inner.getJSONObject("msgData");
        if (msgData == null)
        {
            return null;
        }
        JSONArray list = msgData.getJSONArray("waybillNoInfoList");
        if (list == null || list.isEmpty())
        {
            return msgData.getString("waybillNo");
        }
        JSONObject first = list.getJSONObject(0);
        return first != null ? first.getString("waybillNo") : null;
    }

    private static String extractFilterResult(JSONObject inner)
    {
        if (inner == null)
        {
            return null;
        }
        JSONObject msgData = inner.getJSONObject("msgData");
        if (msgData == null)
        {
            return null;
        }
        return msgData.getString("filterResult");
    }

    private static List<Map<String, Object>> extractRoutes(JSONObject inner, String waybillNo)
    {
        List<Map<String, Object>> routes = new ArrayList<>();
        if (inner == null)
        {
            return routes;
        }
        JSONObject msgData = inner.getJSONObject("msgData");
        if (msgData == null)
        {
            return routes;
        }
        JSONArray routeResps = msgData.getJSONArray("routeResps");
        if (routeResps == null)
        {
            return routes;
        }
        for (int i = 0; i < routeResps.size(); i++)
        {
            JSONObject resp = routeResps.getJSONObject(i);
            if (resp == null)
            {
                continue;
            }
            String mailNo = resp.getString("mailNo");
            if (StringUtils.isNotBlank(waybillNo) && StringUtils.isNotBlank(mailNo)
                    && !waybillNo.equalsIgnoreCase(mailNo))
            {
                continue;
            }
            JSONArray routeList = resp.getJSONArray("routes");
            if (routeList == null)
            {
                continue;
            }
            for (int j = 0; j < routeList.size(); j++)
            {
                JSONObject route = routeList.getJSONObject(j);
                if (route == null)
                {
                    continue;
                }
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("acceptTime", route.getString("acceptTime"));
                item.put("acceptAddress", route.getString("acceptAddress"));
                item.put("remark", route.getString("remark"));
                item.put("opcode", route.getString("opcode"));
                routes.add(item);
            }
        }
        return routes;
    }

    private static Map<String, Object> buildRaw(JSONObject top, JSONObject inner)
    {
        Map<String, Object> raw = new LinkedHashMap<>();
        raw.put("platform", top);
        raw.put("business", inner);
        return raw;
    }

    private static final class ResolvedConfig
    {
        private final CrmSfExpressConfig config;
        private final String checkword;

        private ResolvedConfig(CrmSfExpressConfig config, String checkword)
        {
            this.config = config;
            this.checkword = checkword;
        }
    }
}
