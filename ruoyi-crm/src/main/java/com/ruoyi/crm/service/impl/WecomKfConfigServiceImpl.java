package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomKfConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomKfConfigSaveRequest;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomKfConfigMapper;
import com.ruoyi.crm.service.IWecomKfConfigService;
import com.ruoyi.tenant.service.ISysWecomCorpTenantService;

/**
 * 微信客服配置服务。
 * <p>
 * Secret / Token / EncodingAESKey 统一使用自建应用（crm_wecom_config）的配置，无需单独存储。
 */
@Service
public class WecomKfConfigServiceImpl implements IWecomKfConfigService
{
    @Autowired
    private CrmWecomKfConfigMapper kfConfigMapper;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private ISysWecomCorpTenantService sysWecomCorpTenantService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WecomKfConfigServiceImpl.class);

    @Override
    public Map<String, Object> getConfigMasked()
    {
        CrmWecomKfConfig cfg = kfConfigMapper.selectFirst();
        return buildConfigMap(cfg);
    }

    static Map<String, Object> buildConfigMap(CrmWecomKfConfig cfg)
    {
        Map<String, Object> m = new HashMap<>();
        if (cfg == null)
        {
            m.put("corpId", null);
            m.put("webhookUrl", null);
            m.put("openKfids", null);
            m.put("enabled", false);
            m.put("workflowId", null);
            m.put("callbackPathHint", null);
            m.put("updatedAt", null);
            return m;
        }

        m.put("corpId", cfg.getCorpId());
        m.put("webhookUrl", cfg.getWebhookUrl());
        m.put("openKfids", cfg.getOpenKfids());
        m.put("enabled", cfg.getEnabled() != null && cfg.getEnabled() == 1);
        m.put("workflowId", cfg.getWorkflowId());
        m.put("callbackPathHint", "/crm/wecom/callback/" + cfg.getCorpId());
        if (cfg.getUpdatedAt() != null)
        {
            m.put("updatedAt", cfg.getUpdatedAt().toString());
        }
        else
        {
            m.put("updatedAt", null);
        }
        return m;
    }

    @Override
    public void saveOrUpdate(WecomKfConfigSaveRequest body, String tenantId)
    {
        if (body.getWebhookUrl() == null || body.getWebhookUrl().isEmpty())
        {
            throw new ServiceException("webhookUrl 不能为空");
        }

        // corpId 从 wecom_config 获取（KF 复用自建应用的 corpId）
        CrmWecomConfig wecomCfg = wecomConfigMapper.selectFirst();
        if (wecomCfg == null || wecomCfg.getCorpId() == null || wecomCfg.getCorpId().isEmpty())
        {
            throw new ServiceException("请先完成企业微信自建应用配置（应用管理 → 创建应用 → 获取 Secret），KF 功能复用自建应用的 Secret / Token / EncodingAESKey");
        }
        String corpId = wecomCfg.getCorpId();

        // 查现有 KF 配置（按 corpId）
        CrmWecomKfConfig existing = kfConfigMapper.selectByCorpId(corpId);
        CrmWecomKfConfig row = existing != null ? existing : new CrmWecomKfConfig();
        row.setCorpId(corpId);

        // webhookUrl 必填
        row.setWebhookUrl(body.getWebhookUrl().trim());

        // openKfids 可选
        if (body.getOpenKfids() != null)
        {
            row.setOpenKfids(body.getOpenKfids().trim());
        }

        // workflowId: 仅当请求明确提供时更新，否则保留现有值
        if (body.getWorkflowId() != null)
        {
            row.setWorkflowId(StringUtils.hasText(body.getWorkflowId()) ? body.getWorkflowId().trim() : null);
        }
        else if (existing != null)
        {
            row.setWorkflowId(existing.getWorkflowId());
        }

        // enabled 可选，默认 0
        if (body.getEnabled() != null)
        {
            row.setEnabled(body.getEnabled());
        }
        else if (existing == null)
        {
            row.setEnabled(0);
        }

        Date now = new Date();
        row.setUpdatedAt(now);

        if (existing == null)
        {
            row.setCreatedAt(now);
            kfConfigMapper.insert(row);
        }
        else
        {
            row.setId(existing.getId());
            kfConfigMapper.update(row);
        }

        // 更新 corpId 到租户映射
        sysWecomCorpTenantService.replaceMappingForTenant(tenantId, corpId);

        log.info("WecomKfConfig saved for corpId={} tenant={}", corpId, tenantId);
    }
}
