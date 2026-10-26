package com.ruoyi.crm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmCapture;
import com.ruoyi.crm.domain.dto.CaptureSubmitRequest;
import com.ruoyi.crm.domain.vo.CaptureSubmitResultVo;
import com.ruoyi.crm.domain.vo.PublicCaptureDefinitionVo;
import com.ruoyi.crm.mapper.CrmCaptureMapper;
import com.ruoyi.crm.service.ICrmPublicCaptureService;
import com.ruoyi.crm.service.support.CaptureCustomFieldSupport;
import com.ruoyi.crm.service.support.CaptureLeadAttributionService;
import com.ruoyi.crm.service.support.CaptureOriginGuard;
import com.ruoyi.crm.service.support.CaptureSurveyDataMapper;
import com.ruoyi.crm.service.support.CaptureSurveyDataMapper.MappedLeadFields;
import com.ruoyi.crm.service.support.OutreachRegistrationIdentityResolution;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebsiteLeadIdentityResolver;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@Service
public class CrmPublicCaptureServiceImpl implements ICrmPublicCaptureService
{
    private static final Logger log = LoggerFactory.getLogger(CrmPublicCaptureServiceImpl.class);

    @Autowired
    private PublicTenantSiteResolver publicTenantSiteResolver;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private CrmCaptureMapper captureMapper;

    @Autowired
    private WebsiteLeadIdentityResolver identityResolver;

    @Autowired
    private CaptureLeadAttributionService leadAttributionService;

    @Autowired
    private WebsiteVisitorSessionService visitorSessionService;

    @Override
    public PublicCaptureDefinitionVo getPublished(String captureId, String tenantSlug, String requestOrigin)
    {
        if (StringUtils.isBlank(tenantSlug))
        {
            throw new ServiceException("tenantSlug 不能为空", HttpStatus.BAD_REQUEST);
        }
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(tenantSlug.trim());
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            CrmCapture capture = requirePublishedCapture(captureId);
            assertOriginAllowed(capture, requestOrigin);
            PublicCaptureDefinitionVo vo = new PublicCaptureDefinitionVo();
            vo.setCaptureId(capture.getCaptureId());
            vo.setTitle(capture.getTitle());
            vo.setSurveyJson(capture.getContentSnapshot());
            vo.setSuccessMessage(capture.getSuccessMessage());
            return vo;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public CaptureSubmitResultVo submit(String captureId, CaptureSubmitRequest request, String requestOrigin)
    {
        if (request == null || StringUtils.isBlank(request.getTenantSlug()))
        {
            throw new ServiceException("tenantSlug 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (request.getSurveyData() == null || request.getSurveyData().isEmpty())
        {
            throw new ServiceException("surveyData 不能为空", HttpStatus.BAD_REQUEST);
        }

        MappedLeadFields fields = CaptureSurveyDataMapper.map(request.getSurveyData());
        if (StringUtils.isBlank(fields.getLeadName()) || StringUtils.isBlank(fields.getMobilePhone()))
        {
            throw new ServiceException("姓名与手机号不能为空", HttpStatus.BAD_REQUEST);
        }

        String tenantSlug = request.getTenantSlug().trim();
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(tenantSlug);

        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            CrmCapture capture = requirePublishedCapture(captureId);
            assertOriginAllowed(capture, requestOrigin);

            String customField = CaptureCustomFieldSupport.buildCustomField(
                    fields.getExtraFields(), capture.getCaptureId(), tenantSlug, request.getPageUrl());
            String pagePath = StringUtils.isNotBlank(request.getPagePath())
                    ? request.getPagePath() : request.getPageUrl();

            OutreachRegistrationIdentityResolution identity = identityResolver.resolve(
                    site.getTenantName(), fields.getMobilePhone());

            CaptureSubmitResultVo result = new CaptureSubmitResultVo();
            if (identity.isContactKnown())
            {
                result.setResolution("contact_known");
                result.setContactId(identity.getContactId());
                safeStitch(request.get_sessionId(), request.get_browseHistory(),
                        "Contact", identity.getContactId());
                logSubmit(tenantSlug, capture.getCaptureId(), fields.getMobilePhone(), "contact_known");
                return result;
            }

            if (identity.getKind() == OutreachRegistrationIdentityResolution.Kind.LEAD_UPSERT)
            {
                String leadId = leadAttributionService.submitLeadUpdate(fields, customField,
                        identity.getExistingLead());
                result.setLeadId(leadId);
                result.setResolution("updated");
                safeStitch(request.get_sessionId(), request.get_browseHistory(), "Lead", leadId);
                logSubmit(tenantSlug, capture.getCaptureId(), fields.getMobilePhone(), "updated");
                return result;
            }

            String leadId = leadAttributionService.submitNewLead(fields, customField, pagePath);
            result.setLeadId(leadId);
            result.setResolution("created");
            safeStitch(request.get_sessionId(), request.get_browseHistory(), "Lead", leadId);
            logSubmit(tenantSlug, capture.getCaptureId(), fields.getMobilePhone(), "created");
            return result;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private CrmCapture requirePublishedCapture(String captureId)
    {
        if (StringUtils.isBlank(captureId))
        {
            throw new ServiceException("Capture 不存在", HttpStatus.NOT_FOUND);
        }
        CrmCapture capture = captureMapper.selectByCaptureId(captureId.trim());
        if (capture == null || !capture.isPublished() || StringUtils.isBlank(capture.getContentSnapshot()))
        {
            throw new ServiceException("Capture 不存在或未发布", HttpStatus.NOT_FOUND);
        }
        return capture;
    }

    private void assertOriginAllowed(CrmCapture capture, String requestOrigin)
    {
        if (!CaptureOriginGuard.isOriginAllowed(capture.getAllowedOrigins(), requestOrigin))
        {
            throw new ServiceException("Origin 不在白名单", HttpStatus.FORBIDDEN);
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
            log.warn("Visitor session stitch failed after capture submit: sessionId={}, subjectType={}, subjectId={}",
                    sessionId, subjectType, subjectId, e);
        }
    }

    private void logSubmit(String tenantSlug, String captureId, String mobilePhone, String resolution)
    {
        log.info("capture submit tenantSlug={}, captureId={}, mobile={}, resolution={}",
                tenantSlug, captureId, maskPhone(mobilePhone), resolution);
    }

    private static String maskPhone(String phone)
    {
        if (StringUtils.isBlank(phone) || phone.length() < 7)
        {
            return "***";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
