package com.ruoyi.crm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmOrg;
import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;
import com.ruoyi.crm.domain.vo.WebsiteLeadSubmitResultVo;
import com.ruoyi.crm.service.ICrmOrgService;
import com.ruoyi.crm.service.IWebsiteOnlineRegisterService;
import com.ruoyi.crm.service.support.OutreachRegistrationIdentityResolution;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebsiteBookingDualWriteSupport;
import com.ruoyi.crm.service.support.WebsiteLeadAttributionService;
import com.ruoyi.crm.service.support.WebsiteLeadIdentityResolver;
import com.ruoyi.crm.service.support.WebsiteLeadValidationSupport;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@Service
public class WebsiteOnlineRegisterServiceImpl implements IWebsiteOnlineRegisterService
{
    private static final Logger log = LoggerFactory.getLogger(WebsiteOnlineRegisterServiceImpl.class);

    @Autowired
    private PublicTenantSiteResolver publicTenantSiteResolver;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private ICrmOrgService crmOrgService;

    @Autowired
    private WebsiteLeadIdentityResolver identityResolver;

    @Autowired
    private WebsiteLeadAttributionService leadAttributionService;

    @Autowired
    private WebsiteVisitorSessionService visitorSessionService;

    @Autowired
    private WebsiteBookingDualWriteSupport websiteBookingDualWriteSupport;

    @Override
    public WebsiteLeadSubmitResultVo submit(WebsiteLeadSubmitRequest request)
    {
        WebsiteLeadValidationSupport.validateSubmission(request);
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(request.getTenantSlug());

        try
        {
            // 须在任意租户库 SQL 之前绑定；勿在方法上加 @Transactional，否则事务会在 bind 前固定主库连接
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            validateOrganization(request.getOrgId());

            OutreachRegistrationIdentityResolution identity = identityResolver.resolve(
                    site.getTenantName(), request.getMobilePhone());

            WebsiteLeadSubmitResultVo result = new WebsiteLeadSubmitResultVo();
            if (identity.isContactKnown())
            {
                result.setResolution("contact_known");
                result.setContactId(identity.getContactId());
                safeStitch(request.get_sessionId(), request.get_browseHistory(),
                        "Contact", identity.getContactId());
                return result;
            }

            if (identity.getKind() == OutreachRegistrationIdentityResolution.Kind.LEAD_UPSERT)
            {
                String leadId = leadAttributionService.submitLeadUpdate(request, identity.getExistingLead());
                result.setLeadId(leadId);
                result.setResolution("updated");
                websiteBookingDualWriteSupport.dualWriteAfterLeadSuccess(request, leadId);
                safeStitch(request.get_sessionId(), request.get_browseHistory(), "Lead", leadId);
                return result;
            }

            String leadId = leadAttributionService.submitNewLead(request);
            result.setLeadId(leadId);
            result.setResolution("created");
            websiteBookingDualWriteSupport.dualWriteAfterLeadSuccess(request, leadId);
            safeStitch(request.get_sessionId(), request.get_browseHistory(), "Lead", leadId);
            return result;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private void safeStitch(String sessionId, String browseHistory, String subjectType, String subjectId)
    {
        try
        {
            visitorSessionService.stitch(sessionId, browseHistory, subjectType, subjectId);
        }
        catch (Exception e)
        {
            log.warn("Visitor session stitch failed after websitelead: sessionId={}, subjectType={}, subjectId={}",
                    sessionId, subjectType, subjectId, e);
        }
    }

    private void validateOrganization(String orgId)
    {
        if (StringUtils.isBlank(orgId))
        {
            throw new ServiceException("orgId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmOrg organization = crmOrgService.selectCrmOrgByOrgId(orgId);
        if (organization == null)
        {
            throw new ServiceException("无效的 orgId", HttpStatus.BAD_REQUEST);
        }
    }
}
