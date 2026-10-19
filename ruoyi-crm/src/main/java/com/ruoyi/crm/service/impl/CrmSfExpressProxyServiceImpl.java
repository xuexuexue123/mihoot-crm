package com.ruoyi.crm.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.service.ICrmSfExpressProxyService;
import com.ruoyi.crm.sfexpress.SfExpressApiService;

@Service
public class CrmSfExpressProxyServiceImpl implements ICrmSfExpressProxyService
{
    @Autowired
    private SfExpressApiService sfExpressApiService;

    @Override
    public Map<String, Object> createOrder(JSONObject msgData)
    {
        return sfExpressApiService.createOrder(msgData);
    }

    @Override
    public Map<String, Object> searchRoutes(String waybillNo)
    {
        return sfExpressApiService.searchRoutes(waybillNo);
    }

    @Override
    public Map<String, Object> cancelOrder(String waybillNo, String orderId)
    {
        return sfExpressApiService.cancelOrder(waybillNo, orderId);
    }
}
