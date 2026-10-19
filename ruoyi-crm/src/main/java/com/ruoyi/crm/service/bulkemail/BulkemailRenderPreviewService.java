package com.ruoyi.crm.service.bulkemail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.dto.BulkemailRenderPreviewRequest;
import com.ruoyi.crm.domain.dto.BulkemailRenderResult;
import com.ruoyi.crm.mapper.CrmBulkemailMapper;
import com.ruoyi.crm.mapper.CrmContactMapper;

/**
 * 邮件渲染 preview（与 Worker 同一 render 入口，不含 tracking pixel）
 */
@Service
public class BulkemailRenderPreviewService
{
    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CrmBulkemailMapper crmBulkemailMapper;

    @Autowired
    private BulkemailRenderService renderService;

    public BulkemailRenderResult preview(BulkemailRenderPreviewRequest request, String tenantId)
    {
        if (request == null || StringUtils.isBlank(request.getContactId()))
        {
            throw new ServiceException("contactId 不能为空", HttpStatus.BAD_REQUEST);
        }

        CrmContact crmContact = crmContactMapper.selectCrmContactByContactId(request.getContactId().trim());
        if (crmContact == null)
        {
            throw new ServiceException("联系人不存在", HttpStatus.NOT_FOUND);
        }

        String subject = request.getSubject();
        String emailContent = request.getEmailContent();
        Boolean personalizationEnabled = request.getPersonalizationEnabled();
        CrmBulkemail campaign = null;

        if (StringUtils.isNotBlank(request.getBulkId()))
        {
            campaign = crmBulkemailMapper.selectCrmBulkemailByBulkId(request.getBulkId().trim());
            if (campaign == null)
            {
                throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
            }
            if (StringUtils.isBlank(subject))
            {
                subject = campaign.getSubject();
            }
            if (StringUtils.isBlank(emailContent))
            {
                emailContent = campaign.getEmailContent();
            }
            if (personalizationEnabled == null)
            {
                personalizationEnabled = BulkemailMetadataSupport.isPersonalizationEnabled(campaign);
            }
        }

        CrmCampaignContacts pseudo = new CrmCampaignContacts();
        pseudo.setContactId(crmContact.getContactId());
        pseudo.setContactName(crmContact.getName());
        pseudo.setEmail(crmContact.getWorkEmail());

        BulkemailRenderContext ctx = renderService.buildContext(
                campaign, pseudo, crmContact, tenantId, personalizationEnabled);
        return renderService.render(subject, emailContent, ctx);
    }
}
