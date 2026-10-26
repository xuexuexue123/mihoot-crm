package com.ruoyi.crm.wechatmp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.dto.WechatMpConfigSaveRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.service.ICrmWechatMpConfigService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.tenant.service.ISysWechatMpTenantService;

@Service
public class CrmWechatMpConfigServiceImpl implements ICrmWechatMpConfigService
{
    @Autowired
    private CrmWechatMpConfigMapper crmWechatMpConfigMapper;

    @Autowired
    private ISysWechatMpTenantService sysWechatMpTenantService;

    @Autowired
    private WechatMpTokenService wechatMpTokenService;

    @Override
    public List<Map<String, Object>> listConfigs()
    {
        List<CrmWechatMpConfig> rows = crmWechatMpConfigMapper.selectAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (CrmWechatMpConfig cfg : rows)
        {
            result.add(buildConfigMap(cfg));
        }
        return result;
    }

    @Override
    public Map<String, Object> getConfigMasked(Long id)
    {
        CrmWechatMpConfig cfg = crmWechatMpConfigMapper.selectById(id);
        if (cfg == null)
        {
            throw new ServiceException("公众号配置不存在");
        }
        return buildConfigMap(cfg);
    }

    private static Map<String, Object> buildConfigMap(CrmWechatMpConfig cfg)
    {
        Map<String, Object> m = new HashMap<>();
        m.put("id", cfg.getId());
        m.put("appId", cfg.getAppId());
        m.put("secretConfigured", cfg.getSecretEnc() != null && !cfg.getSecretEnc().isEmpty());
        m.put("tokenConfigured", cfg.getToken() != null && !cfg.getToken().isEmpty());
        m.put("encodingAesKeyConfigured", cfg.getEncodingAesKey() != null && !cfg.getEncodingAesKey().isEmpty());
        m.put("verifySignature", Boolean.TRUE.equals(cfg.getVerifySignature()));
        m.put("connected", Boolean.TRUE.equals(cfg.getConnected()));
        m.put("lastError", cfg.getLastError());
        m.put("callbackPathHint", "/crm/wechat_mp/callback/" + cfg.getAppId());
        m.put("accountType", cfg.getAccountType());
        m.put("accountName", cfg.getAccountName());
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
    public void saveOrUpdate(WechatMpConfigSaveRequest body, String tenantId)
    {
        if (body.getAppId() == null || body.getAppId().isEmpty())
        {
            throw new ServiceException("appId 不能为空");
        }

        CrmWechatMpConfig existing = crmWechatMpConfigMapper.selectByAppId(body.getAppId().trim());
        CrmWechatMpConfig row = existing != null ? existing : new CrmWechatMpConfig();
        row.setAppId(body.getAppId().trim());

        if (body.getToken() != null)
        {
            row.setToken(body.getToken().trim());
        }
        if (body.getEncodingAesKey() != null)
        {
            row.setEncodingAesKey(body.getEncodingAesKey().trim());
        }
        if (body.getVerifySignature() != null)
        {
            row.setVerifySignature(body.getVerifySignature());
        }

        boolean needSecret = existing == null || body.getSecret() != null && !body.getSecret().isEmpty();
        if (existing == null && (body.getSecret() == null || body.getSecret().isEmpty()))
        {
            throw new ServiceException("首次保存必须提供 secret");
        }
        try
        {
            if (needSecret && body.getSecret() != null && !body.getSecret().isEmpty())
            {
                row.setSecretEnc(AESUtil.encrypt(body.getSecret().trim()));
            }
            else if (existing != null)
            {
                row.setSecretEnc(existing.getSecretEnc());
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("Secret 加密失败");
        }

        row.setUpdateTime(DateUtils.getNowDate());
        row.setUpdateBy(SecurityUtils.getUsername());

        // Verify connectivity by getting access_token
        try
        {
            String tokenVal = wechatMpTokenService.getAccessToken(row.getAppId(), AESUtil.decrypt(row.getSecretEnc()));
            if (tokenVal != null && !tokenVal.isEmpty())
            {
                row.setConnected(true);
                row.setLastError(null);
            }
        }
        catch (Exception e)
        {
            row.setConnected(false);
            row.setLastError(e.getMessage() != null
                    ? e.getMessage().substring(0, Math.min(500, e.getMessage().length()))
                    : "gettoken failed");
        }

        if (existing == null)
        {
            row.setCreateTime(DateUtils.getNowDate());
            row.setCreateBy(SecurityUtils.getUsername());
            crmWechatMpConfigMapper.insert(row);
        }
        else
        {
            row.setId(existing.getId());
            crmWechatMpConfigMapper.update(row);
        }

        sysWechatMpTenantService.replaceMappingForTenant(tenantId, row.getAppId());
    }

    @Override
    public void delete(Long id, String tenantId)
    {
        CrmWechatMpConfig cfg = crmWechatMpConfigMapper.selectById(id);
        if (cfg == null)
        {
            throw new ServiceException("公众号配置不存在");
        }
        crmWechatMpConfigMapper.deleteById(id);
        sysWechatMpTenantService.deleteByTenant(tenantId);
    }

    @Override
    public boolean isConnected(String appId)
    {
        if (StringUtils.isBlank(appId))
        {
            return false;
        }
        CrmWechatMpConfig cfg = crmWechatMpConfigMapper.selectByAppId(appId.trim());
        return cfg != null
                && Boolean.TRUE.equals(cfg.getConnected())
                && StringUtils.isNotBlank(cfg.getSecretEnc());
    }

    @Override
    public List<String> listConnectedAppIds()
    {
        List<String> result = new ArrayList<>();
        for (CrmWechatMpConfig cfg : crmWechatMpConfigMapper.selectAll())
        {
            if (cfg != null
                    && Boolean.TRUE.equals(cfg.getConnected())
                    && StringUtils.isNotBlank(cfg.getAppId())
                    && StringUtils.isNotBlank(cfg.getSecretEnc()))
            {
                result.add(cfg.getAppId());
            }
        }
        return result;
    }
}
