package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOrg;
import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmOrgService;

/**
 * 官网在线预约线索写入
 */
@Service
public class WebsiteLeadAttributionService
{
    private static final String DEFAULT_LEAD_SOURCE = "Website Online Register";
    private static final String DEFAULT_LEAD_TYPE = "business";

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private ICrmOrgService crmOrgService;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    public String submitNewLead(WebsiteLeadSubmitRequest request)
    {
        CrmLead lead = toCrmLead(request);
        lead.setLeadId(UUID.randomUUID().toString());
        lead.setStage(LeadDefaults.DEFAULT_WEBSITE_STAGE);
        lead.setStatus(AudienceLeadVisibility.STATUS_OPEN);
        lead.setCreateTime(DateUtils.getNowDate());
        int rows = crmLeadService.insertCrmLead(lead);
        if (rows <= 0)
        {
            throw new ServiceException("预约提交失败", HttpStatus.ERROR);
        }
        activityEventProducer.produceWebsiteLeadCreated(lead.getLeadId(), lead.getLeadName(),
                lead.getLeadSource(), request.getPagePath());
        return lead.getLeadId();
    }

    public String submitLeadUpdate(WebsiteLeadSubmitRequest request, CrmLead existing)
    {
        CrmLead lead = toCrmLead(request);
        lead.setLeadId(existing.getLeadId());
        lead.setStatus(existing.getStatus());
        lead.setCustomField(WebsiteLeadCustomFieldSupport.mergeCustomField(
                existing.getCustomField(), WebsiteLeadCustomFieldSupport.buildCustomField(request)));
        lead.setUpdateTime(DateUtils.getNowDate());
        int rows = crmLeadService.updateCrmLead(lead);
        if (rows <= 0)
        {
            throw new ServiceException("预约提交失败", HttpStatus.ERROR);
        }
        activityEventProducer.produceLeadDuplicated(lead.getLeadId(), "mobilePhone", DEFAULT_LEAD_SOURCE);
        return lead.getLeadId();
    }

    private CrmLead toCrmLead(WebsiteLeadSubmitRequest request)
    {
        CrmLead lead = new CrmLead();
        lead.setLeadName(request.getLeadName());
        lead.setMobilePhone(normalizePhone(request.getMobilePhone()));
        lead.setWorkEmail(request.getWorkEmail());
        lead.setOrgId(request.getOrgId());
        lead.setFullName(resolveOrgFullName(request.getOrgId()));
        lead.setLeadSource(StringUtils.isNotBlank(request.getLeadSource())
                ? request.getLeadSource() : DEFAULT_LEAD_SOURCE);
        lead.setLeadType(StringUtils.isNotBlank(request.getLeadType())
                ? request.getLeadType() : DEFAULT_LEAD_TYPE);
        lead.setStage(LeadDefaults.DEFAULT_STAGE);
        lead.setOwner(request.getOwner());
        lead.setCreateBy(request.getCreateBy());
        lead.setUserId(request.getUserId());
        lead.setDeptId(request.getDeptId());
        lead.setCustomField(WebsiteLeadCustomFieldSupport.buildCustomField(request));
        return lead;
    }

    private String resolveOrgFullName(String orgId)
    {
        if (StringUtils.isBlank(orgId))
        {
            return null;
        }
        CrmOrg org = crmOrgService.selectCrmOrgByOrgId(orgId);
        if (org == null)
        {
            return null;
        }
        if (StringUtils.isNotBlank(org.getFullName()))
        {
            return org.getFullName();
        }
        return StringUtils.isNotBlank(org.getShortName()) ? org.getShortName() : null;
    }

    private static String normalizePhone(String mobilePhone)
    {
        if (StringUtils.isBlank(mobilePhone))
        {
            return mobilePhone;
        }
        String normalized = PhoneNormalizer.normalize(mobilePhone);
        return StringUtils.isNotBlank(normalized) ? normalized : mobilePhone.trim();
    }
}
