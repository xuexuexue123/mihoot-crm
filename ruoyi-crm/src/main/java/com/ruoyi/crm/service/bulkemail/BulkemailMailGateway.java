package com.ruoyi.crm.service.bulkemail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashape.unirest.http.JsonNode;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.mailgun.MailgunClient;
import com.ruoyi.crm.domain.CrmBulkemail;

/**
 * Mailgun 发信网关（租户级配置）
 */
@Component
public class BulkemailMailGateway
{
    @Autowired
    private IBulkemailProviderConfigService providerConfigService;

    public void send(String toAddress, String subject, String htmlBody, CrmBulkemail campaign) throws Exception
    {
        BulkemailProviderRuntimeConfig cfg = providerConfigService.resolveForSend();
        String from = resolveFrom(campaign, cfg);
        JsonNode res = MailgunClient.sendHtml(cfg.getRegion(), cfg.getDomain(), cfg.getApiKey(),
                from, toAddress, subject, htmlBody);
        if (res == null)
        {
            throw new IllegalStateException("Mail gateway returned empty response");
        }
    }

    static String resolveFrom(CrmBulkemail campaign, BulkemailProviderRuntimeConfig cfg)
    {
        if (campaign != null && StringUtils.isNotBlank(campaign.getSender()))
        {
            return campaign.getSender().trim();
        }
        return MailgunClient.formatFrom(cfg.getFromName(), cfg.getFromEmail());
    }
}
