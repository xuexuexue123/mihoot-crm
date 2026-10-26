package com.ruoyi.crm.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.SmartPushExecutionLog;
import com.ruoyi.crm.domain.SmartPushStrategy;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.SmartPushExecutionLogMapper;
import com.ruoyi.crm.mapper.SmartPushStrategyMapper;
import com.ruoyi.crm.rule.SmartPushExecutor;
import com.ruoyi.crm.service.support.MessageTemplateResolver;
import com.ruoyi.crm.wecom.WecomMessageService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 生日提醒定时任务，每天 08:00 检查当天生日的联系人并根据策略配置发送提醒消息。
 * <p>
 * 支持两种 targetMode：
 * <ul>
 *   <li>owner —— 解析联系人负责人，通过自建应用消息发送提醒给内部成员</li>
 *   <li>customer —— 直接通过客户群发消息接口发送给外部联系人</li>
 * </ul>
 *
 * @deprecated 由 {@link SmartPushScheduler} 和 {@link SmartPushExecutor} 替代，
 *             保留此类仅用于向后兼容 SmartPushController.triggerBirthday() 接口。
 */
@Deprecated
@Component
public class BirthdayReminderScheduler
{
    private static final Logger log = LoggerFactory.getLogger(BirthdayReminderScheduler.class);

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private SmartPushStrategyMapper smartPushStrategyMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SmartPushExecutionLogMapper smartPushExecutionLogMapper;

    @Autowired
    private WecomMessageService wecomMessageService;

    @Autowired
    private SmartPushScheduler smartPushScheduler;

    public void processBirthdayReminders()
    {
        smartPushScheduler.processScheduledPushes();
    }

    private void processBirthdayForTenant(String tenantKey)
    {
        List<SmartPushStrategy> strategies = smartPushStrategyMapper.selectEnabledByType("birthday");
        if (strategies == null || strategies.isEmpty())
        {
            return;
        }

        CrmWecomConfig wecomConfig = crmWecomConfigMapper.selectFirst();
        if (wecomConfig == null || StringUtils.isBlank(wecomConfig.getCorpId()))
        {
            log.warn("BirthdayReminder tenant {}: no WecomConfig configured, skip", tenantKey);
            return;
        }

        CrmContact contactQuery = new CrmContact();
        List<CrmContact> birthdayContacts = crmContactMapper.selectCrmContactBirthdayList(contactQuery);
        if (birthdayContacts == null || birthdayContacts.isEmpty())
        {
            return;
        }

        Date now = DateUtils.getNowDate();
        for (SmartPushStrategy strategy : strategies)
        {
            for (CrmContact contact : birthdayContacts)
            {
                if ("customer".equals(strategy.getTargetMode()))
                {
                    processCustomerTarget(tenantKey, strategy, contact, wecomConfig, now);
                }
                else
                {
                    processOwnerTarget(tenantKey, strategy, contact, wecomConfig, now);
                }
            }
        }
    }

    private void processOwnerTarget(String tenantKey, SmartPushStrategy strategy,
            CrmContact contact, CrmWecomConfig wecomConfig, Date now)
    {
        String ownerSnUserid = null;
        String ownerNickName = "";
        if (contact.getUserId() != null)
        {
            SysUser user = sysUserMapper.selectUserById(contact.getUserId());
            if (user != null && StringUtils.isNotBlank(user.getSnUserid()))
            {
                ownerSnUserid = user.getSnUserid();
                ownerNickName = StringUtils.isNotBlank(user.getNickName()) ? user.getNickName() : "";
            }
        }

        if (StringUtils.isBlank(ownerSnUserid))
        {
            SmartPushExecutionLog failLog = buildLog(strategy, "", "",
                    contact.getContactId(), contact.getName(), false,
                    "负责人未配置企业微信 userid", now);
            smartPushExecutionLogMapper.insert(failLog);
            return;
        }

        try
        {
            String messageContent = MessageTemplateResolver.resolve(
                    strategy.getMessageTemplate(), contact);
            wecomMessageService.sendTextToUser(wecomConfig, ownerSnUserid, messageContent);

            SmartPushExecutionLog successLog = buildLog(strategy, ownerSnUserid, ownerNickName,
                    contact.getContactId(), contact.getName(), true, null, now);
            smartPushExecutionLogMapper.insert(successLog);
        }
        catch (Exception ex)
        {
            log.warn("BirthdayReminder tenant {}: send failed for contact {} -> owner {}: {}",
                    tenantKey, contact.getName(), ownerSnUserid, ex.getMessage());
            SmartPushExecutionLog failLog = buildLog(strategy, ownerSnUserid, ownerNickName,
                    contact.getContactId(), contact.getName(), false, ex.getMessage(), now);
            smartPushExecutionLogMapper.insert(failLog);
        }
    }

    private void processCustomerTarget(String tenantKey, SmartPushStrategy strategy,
            CrmContact contact, CrmWecomConfig wecomConfig, Date now)
    {
        String externalUserid = contact.getWecomExternalUserid();
        if (StringUtils.isBlank(externalUserid))
        {
            SmartPushExecutionLog failLog = buildLog(strategy, "", "",
                    contact.getContactId(), contact.getName(), false,
                    "联系人未配置企业微信 external_userid", now);
            smartPushExecutionLogMapper.insert(failLog);
            return;
        }

        String followUserid = contact.getWecomFollowUserid();
        if (StringUtils.isBlank(followUserid))
        {
            SmartPushExecutionLog failLog = buildLog(strategy, externalUserid, "",
                    contact.getContactId(), contact.getName(), false,
                    "联系人未配置企业微信跟进人 userid", now);
            smartPushExecutionLogMapper.insert(failLog);
            return;
        }

        try
        {
            String messageContent = MessageTemplateResolver.resolve(
                    strategy.getMessageTemplate(), contact);
            wecomMessageService.sendTextToExternalUser(wecomConfig, externalUserid, followUserid, messageContent);

            SmartPushExecutionLog successLog = buildLog(strategy, externalUserid, contact.getName(),
                    contact.getContactId(), contact.getName(), true, null, now);
            smartPushExecutionLogMapper.insert(successLog);
        }
        catch (Exception ex)
        {
            log.warn("BirthdayReminder tenant {}: send failed for contact {} -> external {}: {}",
                    tenantKey, contact.getName(), externalUserid, ex.getMessage());
            SmartPushExecutionLog failLog = buildLog(strategy, externalUserid, contact.getName(),
                    contact.getContactId(), contact.getName(), false, ex.getMessage(), now);
            smartPushExecutionLogMapper.insert(failLog);
        }
    }

    private SmartPushExecutionLog buildLog(SmartPushStrategy strategy, String targetUserSn,
            String targetUserName, String contactId, String contactName,
            boolean success, String errorMsg, Date executedAt)
    {
        SmartPushExecutionLog logEntry = new SmartPushExecutionLog();
        logEntry.setStrategyId(strategy.getId());
        logEntry.setStrategyName(strategy.getName());
        logEntry.setTargetUserSn(targetUserSn);
        logEntry.setTargetUserName(targetUserName);
        logEntry.setContactId(contactId);
        logEntry.setContactName(contactName);
        logEntry.setSuccess(success);
        logEntry.setErrorMsg(errorMsg);
        logEntry.setExecutedAt(executedAt);
        logEntry.setCreatedAt(executedAt);
        return logEntry;
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
