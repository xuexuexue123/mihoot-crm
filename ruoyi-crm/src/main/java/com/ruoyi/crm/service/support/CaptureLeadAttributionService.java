package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.support.CaptureSurveyDataMapper.MappedLeadFields;

/**
 * Captures 公开提交线索写入
 */
@Service
public class CaptureLeadAttributionService
{
    public static final String LEAD_SOURCE = "Capture";
    private static final String DEFAULT_LEAD_TYPE = "business";
    private static final String SYSTEM_OPERATOR = "System";

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    public String submitNewLead(MappedLeadFields fields, String customField, String pagePath)
    {
        CrmLead lead = toCrmLead(fields, customField);
        lead.setLeadId(UUID.randomUUID().toString());
        lead.setStatus("Initial");
        lead.setCreateTime(DateUtils.getNowDate());
        int rows = crmLeadService.insertCrmLead(lead);
        if (rows <= 0)
        {
            throw new ServiceException("提交失败", HttpStatus.ERROR);
        }
        activityEventProducer.produceWebsiteLeadCreated(lead.getLeadId(), lead.getLeadName(),
                LEAD_SOURCE, pagePath);
        return lead.getLeadId();
    }

    public String submitLeadUpdate(MappedLeadFields fields, String customField, CrmLead existing)
    {
        CrmLead lead = toCrmLead(fields, customField);
        lead.setLeadId(existing.getLeadId());
        lead.setStatus(existing.getStatus());
        lead.setCustomField(CaptureCustomFieldSupport.mergeCustomField(existing.getCustomField(), customField));
        lead.setUpdateTime(DateUtils.getNowDate());
        int rows = crmLeadService.updateCrmLead(lead);
        if (rows <= 0)
        {
            throw new ServiceException("提交失败", HttpStatus.ERROR);
        }
        activityEventProducer.produceLeadDuplicated(lead.getLeadId(), "mobilePhone", LEAD_SOURCE);
        return lead.getLeadId();
    }

    private CrmLead toCrmLead(MappedLeadFields fields, String customField)
    {
        CrmLead lead = new CrmLead();
        lead.setLeadName(fields.getLeadName());
        lead.setMobilePhone(normalizePhone(fields.getMobilePhone()));
        lead.setWorkEmail(fields.getWorkEmail());
        lead.setFullName(fields.getFullName());
        lead.setLeadSource(LEAD_SOURCE);
        lead.setLeadType(DEFAULT_LEAD_TYPE);
        lead.setStage(LeadDefaults.DEFAULT_STAGE);
        lead.setStatus("Initial");
        lead.setOwner(SYSTEM_OPERATOR);
        lead.setCreateBy(SYSTEM_OPERATOR);
        lead.setCustomField(customField);
        return lead;
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
