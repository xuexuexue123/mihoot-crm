package com.ruoyi.crm.wechatmp.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import com.ruoyi.crm.wechatmp.mapper.ActivityUnmatchedMapper;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 未匹配身份异步解析器
 * 每 5 分钟按租户扫描 crm_activity_unmatched 表，重试 Identity Resolution。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MpUnmatchedIdentityResolver
{
    private final ActivityUnmatchedMapper unmatchedMapper;
    private final ICrmCustomerIdentityService identityService;
    private final WechatMpIdentityResolutionSupport resolutionSupport;
    private final WechatMpUserProfileResolver userProfileResolver;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Scheduled(fixedDelay = 300_000)
    public void resolveUnmatched()
    {
        MasterTenant query = new MasterTenant();
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(query);
        for (MasterTenant tenant : tenants)
        {
            if (!isActiveTenant(tenant))
            {
                continue;
            }
            try
            {
                ensureTenantDataSourceReady(tenant);
                DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
                resolveForCurrentTenant();
            }
            catch (Exception e)
            {
                log.warn("MpUnmatchedIdentityResolver tenant {} failed: {}", tenant.getTenant(), e.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
    }

    private void resolveForCurrentTenant()
    {
        List<ActivityUnmatched> pending = unmatchedMapper.selectPendingRetry(
                LocalDateTime.now(), 100);

        if (pending.isEmpty())
        {
            return;
        }

        log.info("MpUnmatchedIdentityResolver: processing {} pending unmatched records", pending.size());

        for (ActivityUnmatched record : pending)
        {
            try
            {
                processOne(record);
            }
            catch (Exception e)
            {
                log.error("Failed to resolve unmatched record id={}, pulseId={}",
                        record.getId(), record.getPulseId(), e);
                markFailed(record);
            }
        }
    }

    private void processOne(ActivityUnmatched record)
    {
        String contactId = identityService.findContactByIdentity(
                record.getIdentityType(),
                record.getIdentityValue()
        );

        if (contactId != null)
        {
            resolutionSupport.markUnmatchedMatched(record, contactId, record.getPulseId(), false);
            log.info("Unmatched record matched: pulseId={}, contactId={}, identityType={}, identityValue={}",
                    record.getPulseId(), contactId, record.getIdentityType(), record.getIdentityValue());
            return;
        }

        if ("wechat_mp_openid".equals(record.getIdentityType()))
        {
            contactId = resolveViaUnionId(record);
            if (contactId != null)
            {
                resolutionSupport.bindOpenIdIfNeeded(
                        record.getIdentityValue(), contactId, "unmatched_resolver");
                resolutionSupport.markUnmatchedMatched(record, contactId, record.getPulseId(), false);
                log.info("Unmatched record matched via unionid: pulseId={}, contactId={}, openId={}",
                        record.getPulseId(), contactId, record.getIdentityValue());
                return;
            }
        }

        int newRetryCount = record.getRetryCount() + 1;
        if (newRetryCount >= record.getMaxRetries())
        {
            unmatchedMapper.markAbandoned(record.getId());
            log.warn("Unmatched record {} abandoned after {} retries, identityType={}, identityValue={}",
                    record.getPulseId(), newRetryCount,
                    record.getIdentityType(), record.getIdentityValue());
        }
        else
        {
            LocalDateTime nextRetry = LocalDateTime.now().plusMinutes(5);
            unmatchedMapper.updateRetry(record.getId(), newRetryCount, nextRetry);
        }
    }

    private String resolveViaUnionId(ActivityUnmatched record)
    {
        String unionId = record.getUnionId();
        if (StringUtils.isNotEmpty(unionId))
        {
            String contactId = identityService.findContactByIdentity("wechat_unionid", unionId);
            if (contactId != null)
            {
                return contactId;
            }
        }

        if (!"wechat_mp_openid".equals(record.getIdentityType())
                || StringUtils.isEmpty(record.getAppId()))
        {
            return null;
        }

        Optional<String> apiUnionId = userProfileResolver.fetchUnionIdFromApi(
                record.getAppId(), record.getIdentityValue());
        if (!apiUnionId.isPresent())
        {
            return null;
        }
        unmatchedMapper.updateUnionId(record.getId(), apiUnionId.get(),
                WechatMpUserProfileResolver.UNION_ID_SOURCE_API);
        record.setUnionId(apiUnionId.get());
        return identityService.findContactByIdentity("wechat_unionid", apiUnionId.get());
    }

    private void markFailed(ActivityUnmatched record)
    {
        int newRetryCount = record.getRetryCount() + 1;
        if (newRetryCount >= record.getMaxRetries())
        {
            unmatchedMapper.markAbandoned(record.getId());
        }
        else
        {
            unmatchedMapper.updateRetry(record.getId(), newRetryCount,
                    LocalDateTime.now().plusMinutes(5));
        }
    }

    private boolean isActiveTenant(MasterTenant tenant)
    {
        if (tenant == null || StringUtils.isBlank(tenant.getTenant()))
        {
            return false;
        }
        if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus()))
        {
            return false;
        }
        return tenant.getExpirationDate() == null || !tenant.getExpirationDate().before(DateUtils.getNowDate());
    }

    private void ensureTenantDataSourceReady(MasterTenant tenant)
    {
        String tenantId = tenant.getTenant();
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", tenant.getUrl());
            map.put("username", tenant.getUsername());
            map.put("password", tenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
    }
}
