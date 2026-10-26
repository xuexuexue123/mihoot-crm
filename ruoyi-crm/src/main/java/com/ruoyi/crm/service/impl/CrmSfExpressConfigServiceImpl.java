package com.ruoyi.crm.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmSfExpressConfig;
import com.ruoyi.crm.domain.dto.sfexpress.SfExpressConfigSaveRequest;
import com.ruoyi.crm.mapper.CrmSfExpressConfigMapper;
import com.ruoyi.crm.mapper.CrmSfExpressTokenMapper;
import com.ruoyi.crm.service.ICrmSfExpressConfigService;
import com.ruoyi.crm.sfexpress.SfExpressTokenService;
import com.ruoyi.crm.sfexpress.SfExpressTokenService.TokenStatus;

@Service
public class CrmSfExpressConfigServiceImpl implements ICrmSfExpressConfigService
{
    private static final String CALLBACK_PATH_HINT = "/crm/sf-express/callback/route";

    @Autowired
    private CrmSfExpressConfigMapper crmSfExpressConfigMapper;

    @Autowired
    private CrmSfExpressTokenMapper crmSfExpressTokenMapper;

    @Autowired
    private SfExpressTokenService sfExpressTokenService;

    @Override
    public Map<String, Object> getConfigMasked()
    {
        CrmSfExpressConfig cfg = crmSfExpressConfigMapper.selectFirst();
        String tenantId = com.ruoyi.crm.service.support.TenantDataSourceSupport.currentTenantKey();
        TokenStatus tokenStatus = StringUtils.isNotBlank(tenantId)
                ? sfExpressTokenService.getTokenStatus(tenantId)
                : TokenStatus.empty();
        return buildConfigMap(cfg, tokenStatus);
    }

    static Map<String, Object> buildConfigMap(CrmSfExpressConfig cfg, TokenStatus tokenStatus)
    {
        Map<String, Object> m = new HashMap<>();
        if (cfg == null)
        {
            m.put("customerCode", null);
            m.put("monthlyCard", null);
            m.put("sandbox", true);
            m.put("connected", false);
            m.put("checkwordConfigured", false);
            m.put("tokenValid", false);
            m.put("tokenExpiresAt", null);
            m.put("lastTokenRefreshAt", null);
            m.put("callbackPathHint", CALLBACK_PATH_HINT);
            m.put("lastError", null);
            m.put("updatedAt", null);
            return m;
        }

        boolean tokenValid = tokenStatus != null && tokenStatus.isTokenValid();
        m.put("customerCode", cfg.getCustomerCode());
        m.put("monthlyCard", cfg.getMonthlyCard());
        m.put("sandbox", cfg.getSandbox() == null || Boolean.TRUE.equals(cfg.getSandbox()));
        m.put("checkwordConfigured", StringUtils.isNotBlank(cfg.getCheckwordEnc()));
        m.put("tokenValid", tokenValid);
        m.put("tokenExpiresAt", SfExpressTokenService.toIso8601(
                tokenStatus != null ? tokenStatus.getTokenExpiresAt() : null));
        m.put("lastTokenRefreshAt", SfExpressTokenService.toIso8601(
                tokenStatus != null ? tokenStatus.getLastTokenRefreshAt() : null));
        m.put("connected", tokenValid);
        m.put("callbackPathHint", CALLBACK_PATH_HINT);
        String lastError = cfg.getLastError();
        if (tokenStatus != null && StringUtils.isNotBlank(tokenStatus.getLastError()))
        {
            lastError = tokenStatus.getLastError();
        }
        m.put("lastError", lastError);
        if (cfg.getUpdateTime() != null)
        {
            m.put("updatedAt", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, cfg.getUpdateTime()));
        }
        else
        {
            m.put("updatedAt", null);
        }
        return m;
    }

    @Override
    public void saveOrUpdate(SfExpressConfigSaveRequest body, String tenantId)
    {
        if (body == null || StringUtils.isBlank(body.getCustomerCode()))
        {
            throw new ServiceException("customerCode 不能为空");
        }

        CrmSfExpressConfig existing = crmSfExpressConfigMapper.selectFirst();
        boolean sandboxChanged = existing != null && body.getSandbox() != null
                && !body.getSandbox().equals(existing.getSandbox());
        boolean customerChanged = existing != null
                && !body.getCustomerCode().trim().equals(existing.getCustomerCode());
        boolean checkwordChanged = StringUtils.isNotBlank(body.getCheckword());

        CrmSfExpressConfig row = existing != null ? existing : new CrmSfExpressConfig();
        row.setCustomerCode(body.getCustomerCode().trim());

        if (body.getMonthlyCard() != null)
        {
            String card = body.getMonthlyCard().trim();
            row.setMonthlyCard(card.isEmpty() ? null : card);
        }
        else if (existing != null)
        {
            row.setMonthlyCard(existing.getMonthlyCard());
        }

        if (body.getSandbox() != null)
        {
            row.setSandbox(body.getSandbox());
        }
        else if (existing != null)
        {
            row.setSandbox(existing.getSandbox());
        }
        else
        {
            row.setSandbox(true);
        }

        if (existing == null && StringUtils.isBlank(body.getCheckword()))
        {
            throw new ServiceException("首次保存必须提供 checkword");
        }

        try
        {
            if (StringUtils.isNotBlank(body.getCheckword()))
            {
                row.setCheckwordEnc(AESUtil.encrypt(body.getCheckword().trim()));
            }
            else if (existing != null)
            {
                row.setCheckwordEnc(existing.getCheckwordEnc());
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("checkword 加密/解密失败");
        }

        row.setUpdateTime(DateUtils.getNowDate());
        row.setUpdateBy(SecurityUtils.getUsername());

        if (existing == null)
        {
            row.setCreateTime(DateUtils.getNowDate());
            row.setCreateBy(SecurityUtils.getUsername());
            row.setConnected(false);
            row.setLastError(null);
            crmSfExpressConfigMapper.insert(row);
        }
        else
        {
            row.setId(existing.getId());
            crmSfExpressConfigMapper.update(row);
        }

        if (sandboxChanged || customerChanged || checkwordChanged)
        {
            sfExpressTokenService.invalidate(tenantId);
            crmSfExpressTokenMapper.deleteAll();
        }

        Map<String, Object> refreshResult = sfExpressTokenService.refreshToken(tenantId, true);
        boolean tokenValid = Boolean.TRUE.equals(refreshResult.get("tokenValid"));
        row.setConnected(tokenValid);
        Object refreshErr = refreshResult.get("lastError");
        row.setLastError(refreshErr != null ? String.valueOf(refreshErr) : null);
        row.setUpdateTime(DateUtils.getNowDate());
        crmSfExpressConfigMapper.update(row);
    }
}
