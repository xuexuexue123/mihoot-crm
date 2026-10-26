package com.ruoyi.crm.rule;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.SmartPushExecutionLog;
import com.ruoyi.crm.domain.SmartPushStrategy;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.SmartPushExecutionLogMapper;
import com.ruoyi.crm.service.support.MessageTemplateResolver;
import com.ruoyi.crm.wecom.WecomMessageService;
import com.ruoyi.system.mapper.SysUserMapper;

/**
 * 智能推送执行器，负责单条推送的执行：构建消息 → 发送 → 写日志。
 * 从 BirthdayReminderScheduler 中提取出的核心推送逻辑。
 */
@Service
public class SmartPushExecutor
{
    private static final Logger log = LoggerFactory.getLogger(SmartPushExecutor.class);

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private SmartPushExecutionLogMapper smartPushExecutionLogMapper;

    @Autowired
    private WecomMessageService wecomMessageService;

    /**
     * 对单个联系人执行推送。
     *
     * @param strategy  推送策略
     * @param contact   目标联系人
     * @param eventType 触发事件类型（可为 null）
     */
    public void pushOneContact(SmartPushStrategy strategy, CrmContact contact, String eventType)
    {
        CrmWecomConfig wecomConfig = crmWecomConfigMapper.selectFirst();
        if (wecomConfig == null || StringUtils.isBlank(wecomConfig.getCorpId()))
        {
            log.warn("SmartPushExecutor: no WecomConfig configured, skip push for contact [{}]", contact.getName());
            return;
        }

        Date now = DateUtils.getNowDate();

        if ("customer".equals(strategy.getTargetMode()))
        {
            pushToCustomer(strategy, contact, wecomConfig, eventType, now);
        }
        else
        {
            pushToOwner(strategy, contact, wecomConfig, eventType, now);
        }
    }

    private void pushToOwner(SmartPushStrategy strategy, CrmContact contact,
            CrmWecomConfig wecomConfig, String eventType, Date now)
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
                    "负责人未配置企业微信 userid", eventType, now);
            smartPushExecutionLogMapper.insert(failLog);
            return;
        }

        try
        {
            String messageContent = MessageTemplateResolver.resolve(
                    strategy.getMessageTemplate(), contact);
            wecomMessageService.sendTextToUser(wecomConfig, ownerSnUserid, messageContent);

            SmartPushExecutionLog successLog = buildLog(strategy, ownerSnUserid, ownerNickName,
                    contact.getContactId(), contact.getName(), true, null, eventType, now);
            smartPushExecutionLogMapper.insert(successLog);
        }
        catch (Exception ex)
        {
            log.warn("SmartPushExecutor: send failed for contact [{}] -> owner [{}]: {}",
                    contact.getName(), ownerSnUserid, ex.getMessage());
            SmartPushExecutionLog failLog = buildLog(strategy, ownerSnUserid, ownerNickName,
                    contact.getContactId(), contact.getName(), false, ex.getMessage(), eventType, now);
            smartPushExecutionLogMapper.insert(failLog);
        }
    }

    private void pushToCustomer(SmartPushStrategy strategy, CrmContact contact,
            CrmWecomConfig wecomConfig, String eventType, Date now)
    {
        String externalUserid = contact.getWecomExternalUserid();
        if (StringUtils.isBlank(externalUserid))
        {
            SmartPushExecutionLog failLog = buildLog(strategy, "", "",
                    contact.getContactId(), contact.getName(), false,
                    "联系人未配置企业微信 external_userid", eventType, now);
            smartPushExecutionLogMapper.insert(failLog);
            return;
        }

        String followUserid = contact.getWecomFollowUserid();
        if (StringUtils.isBlank(followUserid))
        {
            SmartPushExecutionLog failLog = buildLog(strategy, externalUserid, "",
                    contact.getContactId(), contact.getName(), false,
                    "联系人未配置企业微信跟进人 userid", eventType, now);
            smartPushExecutionLogMapper.insert(failLog);
            return;
        }

        try
        {
            String messageContent = MessageTemplateResolver.resolve(
                    strategy.getMessageTemplate(), contact);
            wecomMessageService.sendTextToExternalUser(wecomConfig, externalUserid, followUserid, messageContent);

            SmartPushExecutionLog successLog = buildLog(strategy, externalUserid, contact.getName(),
                    contact.getContactId(), contact.getName(), true, null, eventType, now);
            smartPushExecutionLogMapper.insert(successLog);
        }
        catch (Exception ex)
        {
            log.warn("SmartPushExecutor: send failed for contact [{}] -> external [{}]: {}",
                    contact.getName(), externalUserid, ex.getMessage());
            SmartPushExecutionLog failLog = buildLog(strategy, externalUserid, contact.getName(),
                    contact.getContactId(), contact.getName(), false, ex.getMessage(), eventType, now);
            smartPushExecutionLogMapper.insert(failLog);
        }
    }

    private SmartPushExecutionLog buildLog(SmartPushStrategy strategy, String targetUserSn,
            String targetUserName, String contactId, String contactName,
            boolean success, String errorMsg, String eventType, Date executedAt)
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
        logEntry.setEventType(eventType);
        logEntry.setExecutedAt(executedAt);
        logEntry.setCreatedAt(executedAt);
        return logEntry;
    }
}
