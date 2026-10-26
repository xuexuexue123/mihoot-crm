package com.ruoyi.crm.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.crm.service.ICrmSfExpressTokenService;
import com.ruoyi.crm.sfexpress.SfExpressTokenService;

@Service
public class CrmSfExpressTokenServiceImpl implements ICrmSfExpressTokenService
{
    @Autowired
    private SfExpressTokenService sfExpressTokenService;

    @Override
    public Map<String, Object> refreshToken(String tenantId, boolean force)
    {
        return sfExpressTokenService.refreshToken(tenantId, force);
    }
}
