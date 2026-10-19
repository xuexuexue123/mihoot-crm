package com.ruoyi.crm.feishu.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.feishu.FeishuTokenService;
import com.ruoyi.crm.feishu.domain.CrmFeishuConfig;
import com.ruoyi.crm.feishu.dto.FeishuConfigSaveRequest;
import com.ruoyi.crm.feishu.mapper.CrmFeishuConfigMapper;
import com.ruoyi.crm.feishu.service.ICrmFeishuConfigService;

@Service
public class CrmFeishuConfigServiceImpl implements ICrmFeishuConfigService
{
    @Autowired
    private CrmFeishuConfigMapper configMapper;

    @Autowired
    private FeishuTokenService feishuTokenService;

    @Override
    public Map<String, Object> getConfigMasked()
    {
        CrmFeishuConfig cfg = configMapper.selectSingleton();
        if (cfg == null)
        {
            return null;
        }
        return buildConfigMap(cfg);
    }

    private static Map<String, Object> buildConfigMap(CrmFeishuConfig cfg)
    {
        Map<String, Object> m = new HashMap<>();
        m.put("id", cfg.getId());
        m.put("appId", cfg.getAppId());
        m.put("name", cfg.getName());
        m.put("secretConfigured", cfg.getAppSecretEnc() != null && !cfg.getAppSecretEnc().isEmpty());
        m.put("connected", Boolean.TRUE.equals(cfg.getConnected()));
        m.put("lastError", cfg.getLastError());
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
    public Map<String, Object> saveOrUpdate(FeishuConfigSaveRequest body)
    {
        if (body == null || body.getAppId() == null || body.getAppId().trim().isEmpty())
        {
            throw new ServiceException("appId 不能为空");
        }

        CrmFeishuConfig existing = configMapper.selectSingleton();
        String previousAppId = existing != null ? existing.getAppId() : null;
        CrmFeishuConfig row = existing != null ? existing : new CrmFeishuConfig();
        row.setAppId(body.getAppId().trim());

        if (body.getName() != null)
        {
            String name = body.getName().trim();
            row.setName(name.isEmpty() ? null : name);
        }

        boolean secretProvided = body.getSecret() != null && !body.getSecret().trim().isEmpty();
        if (existing == null && !secretProvided)
        {
            throw new ServiceException("首次保存必须提供 secret");
        }

        try
        {
            if (secretProvided)
            {
                row.setAppSecretEnc(AESUtil.encrypt(body.getSecret().trim()));
            }
            else if (existing != null)
            {
                row.setAppSecretEnc(existing.getAppSecretEnc());
            }
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("Secret 加密失败");
        }

        if (row.getAppSecretEnc() == null || row.getAppSecretEnc().isEmpty())
        {
            throw new ServiceException("secret 未配置");
        }

        String plainSecret;
        try
        {
            plainSecret = AESUtil.decrypt(row.getAppSecretEnc());
        }
        catch (Exception e)
        {
            throw new ServiceException("Secret 解密失败");
        }

        // 探针：成功/失败均保存配置
        try
        {
            FeishuTokenService.TokenResult token = feishuTokenService.getTenantAccessToken(
                    row.getAppId(), plainSecret, true);
            if (token != null && token.accessToken != null && !token.accessToken.isEmpty())
            {
                row.setConnected(true);
                row.setLastError(null);
            }
            else
            {
                row.setConnected(false);
                row.setLastError("tenant_access_token returned empty");
            }
        }
        catch (Exception e)
        {
            row.setConnected(false);
            String msg = e.getMessage() != null ? e.getMessage() : "tenant_access_token failed";
            row.setLastError(msg.substring(0, Math.min(500, msg.length())));
        }

        String username = resolveUsername();
        row.setUpdateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);

        if (existing == null)
        {
            row.setCreateTime(DateUtils.getNowDate());
            row.setCreateBy(username);
            configMapper.insert(row);
        }
        else
        {
            row.setId(existing.getId());
            configMapper.update(row);
            if (previousAppId != null && !previousAppId.equals(row.getAppId()))
            {
                feishuTokenService.invalidate(previousAppId);
            }
        }

        return buildConfigMap(row);
    }

    @Override
    public Map<String, Object> testConnection()
    {
        CrmFeishuConfig cfg = configMapper.selectSingleton();
        if (cfg == null)
        {
            throw new ServiceException("请先配置飞书集成");
        }
        if (cfg.getAppSecretEnc() == null || cfg.getAppSecretEnc().isEmpty())
        {
            throw new ServiceException("请先配置飞书集成：secret 缺失");
        }

        Map<String, Object> data = new LinkedHashMap<>();
        try
        {
            FeishuTokenService.TokenResult token = feishuTokenService.forceRefresh(cfg);
            if (token == null || token.accessToken == null || token.accessToken.isEmpty())
            {
                cfg.setConnected(false);
                cfg.setLastError("tenant_access_token returned empty");
                persistConnectedState(cfg);
                data.put("connected", false);
                throw new ServiceException(cfg.getLastError());
            }
            cfg.setConnected(true);
            cfg.setLastError(null);
            persistConnectedState(cfg);
            data.put("connected", true);
            return data;
        }
        catch (ServiceException e)
        {
            cfg.setConnected(false);
            String msg = e.getMessage() != null ? e.getMessage() : "test failed";
            cfg.setLastError(msg.substring(0, Math.min(500, msg.length())));
            persistConnectedState(cfg);
            throw e;
        }
        catch (Exception e)
        {
            cfg.setConnected(false);
            String msg = e.getMessage() != null ? e.getMessage() : "test failed";
            cfg.setLastError(msg.substring(0, Math.min(500, msg.length())));
            persistConnectedState(cfg);
            throw new ServiceException(cfg.getLastError());
        }
    }

    private void persistConnectedState(CrmFeishuConfig cfg)
    {
        cfg.setUpdateTime(DateUtils.getNowDate());
        cfg.setUpdateBy(resolveUsername());
        configMapper.update(cfg);
    }

    @Override
    public void delete()
    {
        CrmFeishuConfig cfg = configMapper.selectSingleton();
        if (cfg == null)
        {
            throw new ServiceException("飞书配置不存在");
        }
        String appId = cfg.getAppId();
        configMapper.deleteSingleton();
        feishuTokenService.invalidate(appId);
    }

    @Override
    public CrmFeishuConfig requireConnected()
    {
        CrmFeishuConfig cfg = configMapper.selectSingleton();
        if (cfg == null)
        {
            throw new ServiceException("请先配置飞书集成");
        }
        if (cfg.getAppSecretEnc() == null || cfg.getAppSecretEnc().isEmpty())
        {
            throw new ServiceException("请先配置飞书集成：secret 缺失");
        }
        if (!Boolean.TRUE.equals(cfg.getConnected()))
        {
            throw new ServiceException("飞书集成未连通，请先测试或重新保存配置"
                    + (cfg.getLastError() != null ? "：" + cfg.getLastError() : ""));
        }
        return cfg;
    }

    @Override
    public CrmFeishuConfig getRawConfig()
    {
        return configMapper.selectSingleton();
    }

    private static String resolveUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
