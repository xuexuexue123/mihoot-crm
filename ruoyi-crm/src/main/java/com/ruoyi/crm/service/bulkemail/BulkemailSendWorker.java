package com.ruoyi.crm.service.bulkemail;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.dto.BulkemailRenderResult;
import com.ruoyi.crm.mapper.CrmBulkemailMapper;
import com.ruoyi.crm.mapper.CrmCampaignContactsMapper;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.manager.AsyncManager;

/**
 * 单活动异步发送 Worker
 */
@Component
public class BulkemailSendWorker
{
    private static final Logger log = LoggerFactory.getLogger(BulkemailSendWorker.class);

    @Autowired
    private CrmBulkemailMapper crmBulkemailMapper;

    @Autowired
    private CrmCampaignContactsMapper crmCampaignContactsMapper;

    @Autowired
    private BulkemailRenderService renderService;

    @Autowired
    private BulkemailTrackingInjector trackingInjector;

    @Autowired
    private BulkemailMailGateway mailGateway;

    @Autowired
    private IBulkemailProviderConfigService providerConfigService;

    @Autowired
    private CrmContactMapper crmContactMapper;

    public void sendAsync(String bulkId, String tenantKey)
    {
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(tenantKey);
                    send(bulkId, tenantKey);
                }
                catch (Exception ex)
                {
                    log.error("Bulkemail send failed bulkId={} tenant={}", bulkId, tenantKey, ex);
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
    }

    public void send(String bulkId, String tenantKey)
    {
        CrmBulkemail campaign = crmBulkemailMapper.selectCrmBulkemailByBulkId(bulkId);
        if (campaign == null || !BulkemailLifecycleConstants.STATUS_SENDING.equals(campaign.getStatus()))
        {
            return;
        }

        try
        {
            providerConfigService.requireActive();
        }
        catch (Exception ex)
        {
            Date now = DateUtils.getNowDate();
            int failed = crmCampaignContactsMapper.failAllPendingByCampaignId(bulkId,
                    BulkemailLifecycleConstants.DELIVERY_FAILED,
                    BulkemailProviderConfigServiceImpl.PROVIDER_NOT_CONFIGURED,
                    now);
            crmBulkemailMapper.updateSendResult(bulkId, BulkemailLifecycleConstants.STATUS_FAILED, 0, failed, now);
            log.warn("Bulkemail {} failed: {}", bulkId, BulkemailProviderConfigServiceImpl.PROVIDER_NOT_CONFIGURED);
            return;
        }

        int ok = 0;
        int fail = 0;
        boolean hasPending = false;

        while (true)
        {
            List<CrmCampaignContacts> batch = crmCampaignContactsMapper.selectPendingByCampaignId(
                    bulkId, BulkemailLifecycleConstants.SEND_BATCH_SIZE);
            if (batch.isEmpty())
            {
                break;
            }
            hasPending = true;
            for (CrmCampaignContacts contact : batch)
            {
                try
                {
                    sendOne(campaign, contact, tenantKey);
                    crmCampaignContactsMapper.updateDeliveryResult(contact.getId(),
                            BulkemailLifecycleConstants.DELIVERY_SENT, null, DateUtils.getNowDate());
                    ok++;
                }
                catch (Exception ex)
                {
                    String reason = StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : ex.getClass().getSimpleName();
                    crmCampaignContactsMapper.updateDeliveryResult(contact.getId(),
                            BulkemailLifecycleConstants.DELIVERY_FAILED, truncate(reason, 500), DateUtils.getNowDate());
                    fail++;
                    log.warn("Bulkemail contact send failed bulkId={} contactId={}: {}",
                            bulkId, contact.getId(), reason);
                }
                sleepQuietly(100);
            }
        }

        Date now = DateUtils.getNowDate();
        if (!hasPending)
        {
            crmBulkemailMapper.updateSendResult(bulkId, BulkemailLifecycleConstants.STATUS_FAILED, 0, 0, now);
            log.warn("Bulkemail {} failed: no_contacts", bulkId);
            return;
        }

        String finalStatus = fail > 0 && ok == 0
                ? BulkemailLifecycleConstants.STATUS_FAILED
                : BulkemailLifecycleConstants.STATUS_SENT;
        crmBulkemailMapper.updateSendResult(bulkId, finalStatus, ok, fail, now);
    }

    private void sendOne(CrmBulkemail campaign, CrmCampaignContacts contact, String tenantKey) throws Exception
    {
        CrmContact crmContact = resolveCrmContact(campaign, contact);
        BulkemailRenderContext ctx = renderService.buildContext(campaign, contact, crmContact, tenantKey, null);
        BulkemailRenderResult rendered = renderService.render(
                campaign.getSubject(), campaign.getEmailContent(), ctx);
        String html = trackingInjector.inject(rendered.getEmailContent(), contact, campaign, tenantKey);
        mailGateway.send(contact.getEmail(), rendered.getSubject(), html, campaign);
    }

    private CrmContact resolveCrmContact(CrmBulkemail campaign, CrmCampaignContacts contact)
    {
        if (!BulkemailMetadataSupport.isPersonalizationEnabled(campaign))
        {
            return null;
        }
        if (contact == null || StringUtils.isBlank(contact.getContactId()))
        {
            return null;
        }
        return crmContactMapper.selectCrmContactByContactId(contact.getContactId());
    }

    private static void sleepQuietly(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    private static String truncate(String value, int maxLen)
    {
        if (value == null || value.length() <= maxLen)
        {
            return value;
        }
        return value.substring(0, maxLen);
    }
}
