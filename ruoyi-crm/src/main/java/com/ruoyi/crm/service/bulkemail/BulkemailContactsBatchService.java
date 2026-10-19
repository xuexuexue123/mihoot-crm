package com.ruoyi.crm.service.bulkemail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import com.ruoyi.crm.domain.dto.BulkemailContactsBatchRequest;
import com.ruoyi.crm.domain.dto.BulkemailContactsBatchResult;
import com.ruoyi.crm.mapper.CrmBulkemailMapper;
import com.ruoyi.crm.mapper.CrmCampaignContactsMapper;

/**
 * 批量写入活动联系人
 */
@Service
public class BulkemailContactsBatchService
{
    private static final List<String> WRITABLE_STATUSES = Arrays.asList(
            BulkemailLifecycleConstants.STATUS_DRAFT,
            BulkemailLifecycleConstants.STATUS_SCHEDULED);

    @Autowired
    private CrmBulkemailMapper crmBulkemailMapper;

    @Autowired
    private CrmCampaignContactsMapper crmCampaignContactsMapper;

    @Transactional
    public BulkemailContactsBatchResult batchInsert(String bulkId, BulkemailContactsBatchRequest request, String operator)
    {
        CrmBulkemail campaign = requireCampaign(bulkId);
        assertWritableStatus(campaign);

        List<BulkemailContactsBatchRequest.BulkemailContactItem> items = request.getContacts();
        if (items == null)
        {
            items = Collections.emptyList();
        }
        if (items.size() > BulkemailLifecycleConstants.BATCH_CONTACTS_MAX)
        {
            throw new ServiceException("单次最多写入 " + BulkemailLifecycleConstants.BATCH_CONTACTS_MAX + " 条联系人", 413);
        }

        if (request.isReplaceExisting())
        {
            crmCampaignContactsMapper.deletePendingByCampaignId(bulkId);
        }

        BulkemailContactsBatchResult result = new BulkemailContactsBatchResult();
        List<CrmCampaignContacts> toInsert = new ArrayList<>();
        Date now = DateUtils.getNowDate();

        for (BulkemailContactsBatchRequest.BulkemailContactItem item : items)
        {
            if (item == null || StringUtils.isBlank(item.getEmail()))
            {
                result.addSkipped(item != null ? item.getEmail() : "", "empty_email");
                continue;
            }
            CrmCampaignContacts row = new CrmCampaignContacts();
            row.setId(StringUtils.isNotBlank(item.getId()) ? item.getId() : UUID.randomUUID().toString());
            row.setCampaignId(bulkId);
            row.setContactId(StringUtils.isNotBlank(item.getContactId()) ? item.getContactId() : row.getId());
            row.setContactName(item.getContactName());
            row.setEmail(item.getEmail().trim());
            row.setStatus(BulkemailLifecycleConstants.DELIVERY_PENDING);
            row.setDeliveryStatus(BulkemailLifecycleConstants.DELIVERY_PENDING);
            row.setCreateBy(operator);
            row.setCreateTime(now);
            toInsert.add(row);
        }

        if (!toInsert.isEmpty())
        {
            crmCampaignContactsMapper.batchInsert(toInsert);
            result.setInserted(toInsert.size());
        }

        int pendingCount = crmCampaignContactsMapper.countValidEmailByCampaignId(bulkId);
        CrmBulkemail update = new CrmBulkemail();
        update.setBulkId(bulkId);
        update.setTotal((long) pendingCount);
        update.setUpdateBy(operator);
        update.setUpdateTime(now);
        crmBulkemailMapper.updateCrmBulkemail(update);

        return result;
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

    private void assertWritableStatus(CrmBulkemail campaign)
    {
        if (!WRITABLE_STATUSES.contains(campaign.getStatus()))
        {
            throw new ServiceException("当前状态不允许写入联系人", HttpStatus.CONFLICT);
        }
    }
}
