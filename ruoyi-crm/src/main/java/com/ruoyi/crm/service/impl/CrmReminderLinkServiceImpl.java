package com.ruoyi.crm.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmReminderLink;
import com.ruoyi.crm.domain.dto.ReminderLinkCreateRequest;
import com.ruoyi.crm.domain.vo.ReminderLinkCreateResultVo;
import com.ruoyi.crm.domain.vo.ReminderLinkPublicBrandingVo;
import com.ruoyi.crm.domain.vo.ReminderLinkPublicVo;
import com.ruoyi.crm.mapper.CrmReminderLinkMapper;
import com.ruoyi.crm.service.ICrmReminderLinkService;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.whatsapp.ReminderLinkTokenGenerator;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.tenant.config.TenantWebsiteProperties;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@Service
public class CrmReminderLinkServiceImpl implements ICrmReminderLinkService
{
    private static final Logger log = LoggerFactory.getLogger(CrmReminderLinkServiceImpl.class);

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_EXPIRED = "expired";
    public static final String STATUS_REVOKED = "revoked";

    private static final long DEFAULT_EXPIRES_IN_SECONDS = 604800L;
    private static final int TOKEN_INSERT_MAX_RETRIES = 5;
    private static final String NOT_FOUND_MSG = "链接无效";

    @Autowired
    private CrmReminderLinkMapper reminderLinkMapper;

    @Autowired
    private TenantWebsiteProperties tenantWebsiteProperties;

    @Autowired
    private PublicTenantSiteResolver publicTenantSiteResolver;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReminderLinkCreateResultVo create(String tenantId, String createdBy, ReminderLinkCreateRequest request)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("Missing tenant", HttpStatus.BAD_REQUEST);
        }
        if (request == null || StringUtils.isBlank(request.getType()))
        {
            throw new ServiceException("type 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getWebsiteSlug()))
        {
            throw new ServiceException("websiteSlug 不能为空", HttpStatus.BAD_REQUEST);
        }

        String type = request.getType().trim();
        String websiteSlug = request.getWebsiteSlug().trim();
        boolean revokeSameRef = Boolean.TRUE.equals(request.getRevokeSameRef());
        if (revokeSameRef)
        {
            if (StringUtils.isBlank(request.getRefType()) || StringUtils.isBlank(request.getRefId()))
            {
                throw new ServiceException("revokeSameRef 需要 refType 与 refId", HttpStatus.BAD_REQUEST);
            }
            reminderLinkMapper.revokeActiveByRef(tenantId, request.getRefType().trim(), request.getRefId().trim());
        }

        long expiresIn = request.getExpiresInSeconds() != null && request.getExpiresInSeconds() > 0
                ? request.getExpiresInSeconds()
                : DEFAULT_EXPIRES_IN_SECONDS;
        Date expiresAt = new Date(System.currentTimeMillis() + expiresIn * 1000L);

        String payloadJson = request.getPayload() != null
                ? JSON.toJSONString(request.getPayload())
                : "{}";
        String actionsJson = request.getActions() != null
                ? JSON.toJSONString(request.getActions())
                : null;

        CrmReminderLink row = null;
        for (int attempt = 0; attempt < TOKEN_INSERT_MAX_RETRIES; attempt++)
        {
            CrmReminderLink candidate = new CrmReminderLink();
            candidate.setTenantId(tenantId);
            candidate.setToken(ReminderLinkTokenGenerator.nextToken());
            candidate.setType(type);
            candidate.setContactId(blankToNull(request.getContactId()));
            candidate.setWaId(blankToNull(request.getWaId()));
            candidate.setRefType(blankToNull(request.getRefType()));
            candidate.setRefId(blankToNull(request.getRefId()));
            candidate.setPayload(payloadJson);
            candidate.setActions(actionsJson);
            candidate.setStatus(STATUS_ACTIVE);
            candidate.setExpiresAt(expiresAt);
            candidate.setOpenCount(0);
            candidate.setCreatedBy(createdBy);

            try
            {
                reminderLinkMapper.insert(candidate);
                row = candidate;
                break;
            }
            catch (DuplicateKeyException ex)
            {
                log.warn("reminder link token collision tenant={} attempt={}", tenantId, attempt + 1);
            }
        }
        if (row == null || row.getId() == null)
        {
            throw new ServiceException("创建 reminder link 失败，请重试", HttpStatus.ERROR);
        }

        ReminderLinkCreateResultVo vo = new ReminderLinkCreateResultVo();
        vo.setId(row.getId());
        vo.setToken(row.getToken());
        vo.setExpiresAt(expiresAt);
        vo.setPublicUrl(buildPublicUrl(websiteSlug, row.getToken()));
        return vo;
    }

    @Override
    public ReminderLinkPublicVo getPublicByToken(String token, String tenantSlug)
    {
        if (StringUtils.isBlank(tenantSlug))
        {
            throw new ServiceException("tenantSlug 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!ReminderLinkTokenGenerator.isValidFormat(token))
        {
            throw new ServiceException(NOT_FOUND_MSG, HttpStatus.NOT_FOUND);
        }

        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(tenantSlug.trim());
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());

            CrmReminderLink link = reminderLinkMapper.selectByTenantAndToken(site.getTenantName(), token);
            if (link == null)
            {
                throw new ServiceException(NOT_FOUND_MSG, HttpStatus.NOT_FOUND);
            }

            String status = link.getStatus();
            if (STATUS_ACTIVE.equals(status) && link.getExpiresAt() != null
                    && link.getExpiresAt().before(new Date()))
            {
                reminderLinkMapper.markExpired(link.getId(), site.getTenantName());
                status = STATUS_EXPIRED;
            }

            if (STATUS_ACTIVE.equals(status))
            {
                try
                {
                    reminderLinkMapper.markOpened(link.getId(), site.getTenantName());
                }
                catch (Exception ex)
                {
                    log.warn("reminder link markOpened failed id={}: {}", link.getId(), ex.getMessage());
                }
            }

            ReminderLinkPublicVo vo = new ReminderLinkPublicVo();
            vo.setType(link.getType());
            vo.setStatus(status);
            vo.setExpiresAt(link.getExpiresAt());
            vo.setPayload(parsePayload(link.getPayload()));
            vo.setActions(parseActions(link.getActions()));

            ReminderLinkPublicBrandingVo branding = new ReminderLinkPublicBrandingVo();
            branding.setTenantSlug(site.getWebsiteSlug());
            branding.setDisplayName(StringUtils.isNotBlank(site.getCompanyName())
                    ? site.getCompanyName()
                    : site.getWebsiteSlug());
            vo.setBranding(branding);
            return vo;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    /**
     * 用请求传入的 websiteSlug + 配置基域拼公开 URL，不查 master_tenant。
     */
    String buildPublicUrl(String websiteSlug, String token)
    {
        String origin = buildOriginFromSlug(websiteSlug);
        if (StringUtils.isBlank(origin))
        {
            throw new ServiceException("租户公开站点域名未配置，无法生成 publicUrl", HttpStatus.ERROR);
        }
        return origin + "/reminder/" + token;
    }

    private String buildOriginFromSlug(String websiteSlug)
    {
        if (StringUtils.isBlank(websiteSlug))
        {
            return "";
        }
        String protocol = tenantWebsiteProperties.getPublicBaseProtocol();
        if (StringUtils.isBlank(protocol))
        {
            protocol = "https";
        }
        String domain = tenantWebsiteProperties.getEffectivePublicBaseDomain();
        if (StringUtils.isBlank(domain))
        {
            return "";
        }
        return protocol + "://" + websiteSlug.trim() + "." + domain.trim();
    }

    private static Map<String, Object> parsePayload(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return Collections.emptyMap();
        }
        try
        {
            Map<String, Object> map = JSON.parseObject(json, new TypeReference<Map<String, Object>>() {});
            return map != null ? map : Collections.emptyMap();
        }
        catch (Exception ex)
        {
            return Collections.emptyMap();
        }
    }

    private static List<String> parseActions(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        try
        {
            return JSON.parseArray(json, String.class);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private static String blankToNull(String value)
    {
        return StringUtils.isBlank(value) ? null : value.trim();
    }
}
