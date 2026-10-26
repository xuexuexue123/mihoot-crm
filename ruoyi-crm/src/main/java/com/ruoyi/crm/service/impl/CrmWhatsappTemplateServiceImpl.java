package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappTemplate;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateMapper;
import com.ruoyi.crm.service.ICrmWhatsappTemplateSendTaskService;
import com.ruoyi.crm.service.ICrmWhatsappTemplateService;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsAppClient;
import com.ruoyi.crm.whatsapp.WhatsAppTemplateStatuses;

/**
 * 模板缓存同步。群发已迁至 {@link ICrmWhatsappTemplateSendTaskService}。
 */
@Service
public class CrmWhatsappTemplateServiceImpl implements ICrmWhatsappTemplateService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappTemplateServiceImpl.class);

    @Autowired
    private CrmWhatsappTemplateMapper templateMapper;

    @Autowired
    private CrmWhatsappConfigMapper configMapper;

    @Autowired
    private WhatsAppClient whatsappClient;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private ICrmWhatsappTemplateSendTaskService sendTaskService;

    @Override
    public void syncTemplates(String tenantId)
    {
        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null || config.getAccessTokenEnc() == null)
        {
            throw new ServiceException("WhatsApp configuration not found", 400);
        }
        String accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());
        String wabaId = config.getWabaId();
        if (wabaId == null || wabaId.isEmpty())
        {
            throw new ServiceException("wabaId not configured", 400);
        }

        JSONObject response = whatsappClient.getMessageTemplates(wabaId, accessToken, null, null, 200);
        JSONArray data = response.getJSONArray("data");
        if (data == null || data.isEmpty())
        {
            log.info("No templates returned from Meta for waba {}", wabaId);
            return;
        }

        Date now = new Date();
        for (int i = 0; i < data.size(); i++)
        {
            JSONObject tpl = data.getJSONObject(i);
            CrmWhatsappTemplate row = new CrmWhatsappTemplate();
            row.setTenantId(tenantId);
            row.setTemplateName(tpl.getString("name"));
            row.setLanguage(tpl.getString("language"));
            row.setCategory(tpl.getString("category"));
            row.setStatus(WhatsAppTemplateStatuses.normalize(tpl.getString("status")));
            row.setRejectionReason(tpl.getString("rejection_reason"));
            row.setComponents(tpl.getString("components") != null
                    ? tpl.getString("components") : tpl.getJSONArray("components").toJSONString());
            row.setPreviewUrl(tpl.getString("preview_url"));
            row.setLastSyncTime(now);
            row.setCreateTime(now);
            row.setUpdateTime(now);
            templateMapper.insertOrUpdate(row);
        }
        log.info("Synced {} WhatsApp templates for tenant {}", data.size(), tenantId);
    }

    @Override
    @Deprecated
    public Map<String, Object> sendTemplateMessage(String tenantId, String templateName, String language,
                                                   List<String> waIds, String phoneNumberId,
                                                   List<Map<String, Object>> components)
    {
        return sendTaskService.enqueueSend(tenantId, templateName, language, waIds, null, null,
                phoneNumberId, components);
    }
}
