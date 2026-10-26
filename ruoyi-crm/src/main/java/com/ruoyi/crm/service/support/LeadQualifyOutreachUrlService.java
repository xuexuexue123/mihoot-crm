package com.ruoyi.crm.service.support;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.domain.vo.LeadQualifyOutreachUrlVo;
import com.ruoyi.crm.service.ICrmLeadPoolConfigService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

/**
 * 认定潜客外投链接签发
 */
@Service
public class LeadQualifyOutreachUrlService
{
    private static final DateTimeFormatter ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private ICrmLeadPoolConfigService poolConfigService;

    @Autowired
    private ISysReTemplateService templateService;

    @Autowired
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    public LeadQualifyOutreachUrlVo issueUrl(String tenantId, String leadId)
    {
        if (!StringUtils.hasText(leadId))
        {
            throw new ServiceException("Lead not found", HttpStatus.NOT_FOUND);
        }
        CrmLead lead = crmLeadService.selectCrmLeadByLeadId(leadId);
        if (lead == null)
        {
            throw new ServiceException("Lead not found", HttpStatus.NOT_FOUND);
        }

        CrmLeadPoolConfig config = poolConfigService.getConfig();
        String templateId = config != null ? config.getQualifyTemplateId() : null;
        if (!StringUtils.hasText(templateId))
        {
            throw new ServiceException("未配置认定问卷模板", HttpStatus.BAD_REQUEST);
        }

        SysReTemplate template = templateService.selectSysReTemplateByTemplateId(templateId.trim());
        if (template == null)
        {
            throw new ServiceException("认定问卷模板不存在", HttpStatus.BAD_REQUEST);
        }

        ensureSigningConfigured(tenantId);

        long exp = outreachRegistrationTokenService.computeQualifyExp();
        String qualifyUrl = outreachRegistrationTokenService.buildQualifySurveyUrl(
                tenantId, leadId, templateId.trim(), exp);
        if (!StringUtils.hasText(qualifyUrl))
        {
            throw new ServiceException("public app url not configured", HttpStatus.ERROR);
        }

        LeadQualifyOutreachUrlVo vo = new LeadQualifyOutreachUrlVo();
        vo.setQualifyUrl(qualifyUrl);
        vo.setExpiresAt(formatExpiresAt(exp));
        return vo;
    }

    private void ensureSigningConfigured(String tenantId)
    {
        if (!outreachRegistrationTokenService.isSigningConfigured())
        {
            throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
        }
        if (!outreachRegistrationTokenService.hasPublicBaseUrl(tenantId))
        {
            throw new ServiceException("public app url not configured", HttpStatus.ERROR);
        }
    }

    static String formatExpiresAt(long expEpochSec)
    {
        return ISO_OFFSET.format(Instant.ofEpochSecond(expEpochSec).atZone(ZoneId.systemDefault()));
    }
}
