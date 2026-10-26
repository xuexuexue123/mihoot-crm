package com.ruoyi.crm.service;

import java.util.Map;

import com.alibaba.fastjson2.JSONObject;

public interface ICrmSfExpressProxyService
{
    Map<String, Object> createOrder(JSONObject msgData);

    Map<String, Object> searchRoutes(String waybillNo);

    Map<String, Object> cancelOrder(String waybillNo, String orderId);
}
