package com.ruoyi.crm.wechatminiapp.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.wechatminiapp.domain.CrmWechatMiniappConfig;
import com.ruoyi.crm.wechatminiapp.dto.WechatMiniappConfigSaveRequest;
import com.ruoyi.crm.wechatminiapp.mapper.CrmWechatMiniappConfigMapper;
import com.ruoyi.crm.wechatminiapp.service.ICrmWechatMiniappConfigService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

@Service
public class CrmWechatMiniappConfigServiceImpl implements ICrmWechatMiniappConfigService
{
    @Autowired
    private CrmWechatMiniappConfigMapper configMapper;

    @Autowired
    private WechatMpTokenService wechatMpTokenService;

    @Override
    public Map<String, Object> getConfigMasked()
    {
        CrmWechatMiniappConfig cfg = configMapper.selectSingleton();
        if (cfg == null)
        {
            return null;
        }
        return buildConfigMap(cfg);
    }

    @Override
    public Map<String, Object> getConfigRuntime()
    {
        CrmWechatMiniappConfig cfg = configMapper.selectSingleton();
        if (cfg == null)
        {
            return null;
        }
        Map<String, Object> m = new HashMap<>();
        m.put("appId", cfg.getAppId());
        m.put("name", cfg.getName());
        m.put("connected", Boolean.TRUE.equals(cfg.getConnected()));
        String plainSecret = null;
        if (cfg.getAppSecretEnc() != null && !cfg.getAppSecretEnc().isEmpty())
        {
            try
            {
                plainSecret = AESUtil.decrypt(cfg.getAppSecretEnc());
            }
            catch (Exception e)
            {
                throw new ServiceException("Secret 解密失败");
            }
        }
        m.put("appSecret", plainSecret);
        return m;
    }

    private static Map<String, Object> buildConfigMap(CrmWechatMiniappConfig cfg)
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
    public void saveOrUpdate(WechatMiniappConfigSaveRequest body)
    {
        if (body == null || body.getAppId() == null || body.getAppId().trim().isEmpty())
        {
            throw new ServiceException("appId 不能为空");
        }

        CrmWechatMiniappConfig existing = configMapper.selectSingleton();
        CrmWechatMiniappConfig row = existing != null ? existing : new CrmWechatMiniappConfig();
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
            String tokenVal = wechatMpTokenService.getAccessToken(row.getAppId(), plainSecret);
            if (tokenVal != null && !tokenVal.isEmpty())
            {
                row.setConnected(true);
                row.setLastError(null);
            }
            else
            {
                row.setConnected(false);
                row.setLastError("gettoken returned empty");
            }
        }
        catch (Exception e)
        {
            row.setConnected(false);
            String msg = e.getMessage() != null ? e.getMessage() : "gettoken failed";
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
        }
    }

    @Override
    public void delete()
    {
        CrmWechatMiniappConfig cfg = configMapper.selectSingleton();
        if (cfg == null)
        {
            throw new ServiceException("小程序配置不存在");
        }
        configMapper.deleteSingleton();
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
