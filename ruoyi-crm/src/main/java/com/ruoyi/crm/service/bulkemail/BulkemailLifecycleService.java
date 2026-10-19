package com.ruoyi.crm.service.bulkemail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.domain.dto.BulkemailCreateRequest;
import com.ruoyi.crm.domain.dto.BulkemailRenderResult;
import com.ruoyi.crm.mapper.CrmBulkemailMapper;
import com.ruoyi.crm.mapper.CrmCampaignContactsMapper;
import com.ruoyi.crm.service.ICrmBulkemailService;

/**
 * 邮件活动发送生命周期
 */
@Service
public class BulkemailLifecycleService
{
    private static final Logger log = LoggerFactory.getLogger(BulkemailLifecycleService.class);

    private static final List<String> SENDABLE_FROM = Arrays.asList(
            BulkemailLifecycleConstants.STATUS_DRAFT,
            BulkemailLifecycleConstants.STATUS_SCHEDULED);

    private static final List<String> CANCELLABLE_FROM = Arrays.asList(
            BulkemailLifecycleConstants.STATUS_DRAFT,
            BulkemailLifecycleConstants.STATUS_SCHEDULED);

    @Autowired
    private ICrmBulkemailService crmBulkemailService;

    @Autowired
    private CrmBulkemailMapper crmBulkemailMapper;

    @Autowired
    private CrmCampaignContactsMapper crmCampaignContactsMapper;

    @Autowired
    private BulkemailSendWorker bulkemailSendWorker;

    @Autowired
    private BulkemailRenderService renderService;

    @Autowired
    private BulkemailMailGateway mailGateway;

    @Autowired
    private IBulkemailProviderConfigService providerConfigService;

    @Transactional
    public int create(BulkemailCreateRequest request, String tenantKey, String operator)
    {
        String sendMode = resolveSendMode(request);
        validateCreateRequest(sendMode, request);

        request.setSendMode(sendMode);
        request.setCreateBy(operator);
        request.setCreateTime(DateUtils.getNowDate());
        request.setMetadata(BulkemailMetadataSupport.buildMetadataJson(request));

        switch (sendMode)
        {
            case BulkemailLifecycleConstants.SEND_MODE_DRAFT:
                request.setStatus(BulkemailLifecycleConstants.STATUS_DRAFT);
                break;
            case BulkemailLifecycleConstants.SEND_MODE_SCHEDULED:
                request.setStatus(BulkemailLifecycleConstants.STATUS_SCHEDULED);
                break;
            case BulkemailLifecycleConstants.SEND_MODE_IMMEDIATE:
                request.setStatus(BulkemailLifecycleConstants.STATUS_SENDING);
                break;
            default:
                throw new ServiceException("sendMode 无效", HttpStatus.BAD_REQUEST);
        }

        if (request.isDisableInsert())
        {
            if (BulkemailLifecycleConstants.SEND_MODE_IMMEDIATE.equals(sendMode))
            {
                providerConfigService.requireActive();
                bulkemailSendWorker.sendAsync(request.getBulkId(), tenantKey);
            }
            return 1;
        }

        int rows = crmBulkemailService.insertCrmBulkemail(request);
        if (BulkemailLifecycleConstants.SEND_MODE_IMMEDIATE.equals(sendMode))
        {
            providerConfigService.requireActive();
            bulkemailSendWorker.sendAsync(request.getBulkId(), tenantKey);
        }
        return rows;
    }

    @Transactional
    public void schedule(String bulkId, Date scheduledAt, String operator)
    {
        if (scheduledAt == null || !scheduledAt.after(DateUtils.getNowDate()))
        {
            throw new ServiceException("scheduledAt 必须大于当前时间", HttpStatus.BAD_REQUEST);
        }
        requireCampaign(bulkId);
        assertHasContacts(bulkId);

        int updated = crmBulkemailMapper.tryTransitionStatus(bulkId,
                Arrays.asList(BulkemailLifecycleConstants.STATUS_DRAFT, BulkemailLifecycleConstants.STATUS_SCHEDULED),
                BulkemailLifecycleConstants.STATUS_SCHEDULED,
                BulkemailLifecycleConstants.SEND_MODE_SCHEDULED,
                scheduledAt, null);
        if (updated == 0)
        {
            throw new ServiceException("当前状态不允许设定定时", HttpStatus.CONFLICT);
        }

        CrmBulkemail patch = new CrmBulkemail();
        patch.setBulkId(bulkId);
        patch.setScheduledAt(scheduledAt);
        patch.setSendMode(BulkemailLifecycleConstants.SEND_MODE_SCHEDULED);
        patch.setUpdateBy(operator);
        patch.setUpdateTime(DateUtils.getNowDate());
        crmBulkemailMapper.updateCrmBulkemail(patch);
    }

    @Transactional
    public void sendNow(String bulkId, String tenantKey, String operator)
    {
        requireCampaign(bulkId);
        assertHasContacts(bulkId);
        providerConfigService.requireActive();

        int updated = crmBulkemailMapper.tryTransitionStatus(bulkId, SENDABLE_FROM,
                BulkemailLifecycleConstants.STATUS_SENDING,
                BulkemailLifecycleConstants.SEND_MODE_IMMEDIATE,
                null, null);
        if (updated == 0)
        {
            throw new ServiceException("当前状态不允许发送", HttpStatus.CONFLICT);
        }

        CrmBulkemail patch = new CrmBulkemail();
        patch.setBulkId(bulkId);
        patch.setSendMode(BulkemailLifecycleConstants.SEND_MODE_IMMEDIATE);
        patch.setUpdateBy(operator);
        patch.setUpdateTime(DateUtils.getNowDate());
        crmBulkemailMapper.updateCrmBulkemail(patch);

        bulkemailSendWorker.sendAsync(bulkId, tenantKey);
    }

    @Transactional
    public void cancel(String bulkId, String operator)
    {
        requireCampaign(bulkId);
        Date now = DateUtils.getNowDate();
        int updated = crmBulkemailMapper.tryTransitionStatus(bulkId, CANCELLABLE_FROM,
                BulkemailLifecycleConstants.STATUS_CANCELLED,
                null, null, now);
        if (updated == 0)
        {
            throw new ServiceException("当前状态不允许取消", HttpStatus.CONFLICT);
        }

        CrmBulkemail patch = new CrmBulkemail();
        patch.setBulkId(bulkId);
        patch.setCancelledAt(now);
        patch.setUpdateBy(operator);
        patch.setUpdateTime(now);
        crmBulkemailMapper.updateCrmBulkemail(patch);
    }

    public void testSend(String bulkId, String toEmail, String tenantKey)
    {
        if (StringUtils.isBlank(toEmail))
        {
            throw new ServiceException("toEmail 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmBulkemail campaign = requireCampaign(bulkId);
        providerConfigService.requireActive();

        CrmCampaignContacts pseudo = new CrmCampaignContacts();
        pseudo.setEmail(toEmail.trim());
        pseudo.setContactName("Test Recipient");

        BulkemailRenderContext ctx = renderService.buildContext(campaign, pseudo, null, tenantKey, null);
        BulkemailRenderResult rendered = renderService.render(
                campaign.getSubject(), campaign.getEmailContent(), ctx);
        try
        {
            mailGateway.send(toEmail.trim(), rendered.getSubject(), rendered.getEmailContent(), campaign);
        }
        catch (Exception ex)
        {
            throw new ServiceException("测试发送失败: " + ex.getMessage(), HttpStatus.ERROR);
        }
    }

    public CrmBulkemail getNormalized(String bulkId)
    {
        CrmBulkemail campaign = crmBulkemailService.selectCrmBulkemailByBulkId(bulkId);
        BulkemailLifecycleConstants.normalizeLegacyStatus(campaign);
        return campaign;
    }

    public List<CrmBulkemail> listNormalized(CrmBulkemail query)
    {
        List<CrmBulkemail> list = crmBulkemailService.selectCrmBulkemailList(query);
        if (list != null)
        {
            list.forEach(BulkemailLifecycleConstants::normalizeLegacyStatus);
        }
        return list;
    }

    private String resolveSendMode(BulkemailCreateRequest request)
    {
        if (StringUtils.isNotBlank(request.getSendMode()))
        {
            return request.getSendMode().trim().toLowerCase();
        }
        if (BulkemailLifecycleConstants.STATUS_ACTIVE_LEGACY.equalsIgnoreCase(request.getStatus()))
        {
            log.warn("Deprecated bulkemail create without sendMode (status=active); mapped to immediate. bulkId={}",
                    request.getBulkId());
            return BulkemailLifecycleConstants.SEND_MODE_IMMEDIATE;
        }
        throw new ServiceException("sendMode 必填", HttpStatus.BAD_REQUEST);
    }

    private void validateCreateRequest(String sendMode, BulkemailCreateRequest request)
    {
        if (BulkemailLifecycleConstants.SEND_MODE_SCHEDULED.equals(sendMode))
        {
            if (request.getScheduledAt() == null || !request.getScheduledAt().after(DateUtils.getNowDate()))
            {
                throw new ServiceException("sendMode=scheduled 时 scheduledAt 必填且必须大于当前时间",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    private CrmBulkemail requireCampaign(String bulkId)
    {
        CrmBulkemail campaign = crmBulkemailMapper.selectCrmBulkemailByBulkId(bulkId);
        if (campaign == null)
        {
            throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
        }
        BulkemailLifecycleConstants.normalizeLegacyStatus(campaign);
        return campaign;
    }

    private void assertHasContacts(String bulkId)
    {
        int count = crmCampaignContactsMapper.countValidEmailByCampaignId(bulkId);
        if (count < 1)
        {
            throw new ServiceException("至少需要一个有效联系人邮箱", HttpStatus.BAD_REQUEST);
        }
    }
}
