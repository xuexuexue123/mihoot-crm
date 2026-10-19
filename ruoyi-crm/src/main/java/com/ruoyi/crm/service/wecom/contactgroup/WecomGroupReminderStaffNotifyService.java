package com.ruoyi.crm.service.wecom.contactgroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.CrmWecomGroupReminder;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderMapper;
import com.ruoyi.crm.service.support.TenantPublicAppUrlResolver;
import com.ruoyi.crm.wecom.WecomMessageService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 群提醒到期：claim + 应用消息通知员工手发（主投递路径，不依赖 aibot）。
 */
@Service
public class WecomGroupReminderStaffNotifyService
{
    private static final Logger log = LoggerFactory.getLogger(WecomGroupReminderStaffNotifyService.class);

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private CrmWecomGroupReminderMapper reminderMapper;

    @Autowired
    private CrmWecomContactGroupMapper contactGroupMapper;

    @Autowired
    private CrmContactMapper contactMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private WecomMessageService wecomMessageService;

    @Autowired
    private TenantPublicAppUrlResolver publicAppUrlResolver;

    /**
     * 跨租户 claim 到期 pending 并通知员工。
     *
     * @return 本轮处理条数（含 skipped / sent / failed / retry）
     */
    public int dispatchDueAcrossTenants(int limit)
    {
        int lim = limit <= 0 ? WecomContactGroupConstants.DEFAULT_PENDING_LIMIT : Math.min(limit, 200);
        int processed = 0;
        MasterTenant filter = new MasterTenant();
        filter.setStatus(TenantStatus.NORMAL.getCode());
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(filter);
        Date now = DateUtils.getNowDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, WecomContactGroupConstants.CLAIM_LEASE_MINUTES);
        Date leaseUntil = cal.getTime();

        for (MasterTenant tenant : tenants)
        {
            if (processed >= lim)
            {
                break;
            }
            String tenantId = tenant.getTenant();
            if (StringUtils.isBlank(tenantId))
            {
                continue;
            }
            if (tenant.getExpirationDate() != null && tenant.getExpirationDate().before(now))
            {
                continue;
            }
            try
            {
                DynamicDataSourceContextHolder.setDataSourceKey(tenantId);
                int remain = lim - processed;
                List<CrmWecomGroupReminder> due = reminderMapper.selectDuePending(tenantId, now, remain);
                for (CrmWecomGroupReminder r : due)
                {
                    int claimed = reminderMapper.claimLease(r.getId(), leaseUntil, now);
                    if (claimed <= 0)
                    {
                        continue;
                    }
                    r.setClaimLeaseUntil(leaseUntil);
                    processOne(r);
                    processed++;
                    if (processed >= lim)
                    {
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                log.warn("group reminder staff-notify tenant {}: {}", tenantId, e.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
        return processed;
    }

    /**
     * 处理已 claim 的单条（包可见便于单测）。
     */
    void processOne(CrmWecomGroupReminder reminder)
    {
        if (reminder == null || StringUtils.isBlank(reminder.getId()))
        {
            return;
        }
        Date now = DateUtils.getNowDate();
        CrmWecomGroupReminder latest = reminderMapper.selectById(reminder.getId());
        if (latest == null || !WecomContactGroupConstants.REMINDER_PENDING.equals(latest.getStatus()))
        {
            return;
        }

        String tenantId = latest.getTenantId();
        CrmContact contact = null;
        try
        {
            contact = contactMapper.selectCrmContactByContactId(latest.getContactId());
        }
        catch (Exception ignored)
        {
        }
        String contactName = contact != null ? contact.getName() : latest.getContactId();
        String touser = WecomGroupReminderSupport.resolveSnUserid(contact, sysUserMapper);

        CrmWecomContactGroup group = contactGroupMapper.selectByContactId(tenantId, latest.getContactId());
        if (!WecomGroupReminderSupport.isServiceGroupReady(group))
        {
            markSkipped(latest, WecomContactGroupConstants.SKIP_NO_SERVICE_GROUP, now);
            if (StringUtils.isNotBlank(touser))
            {
                trySendNudge(touser, WecomGroupReminderSupport.buildNoGroupNudgeContent(contactName));
            }
            return;
        }

        if (StringUtils.isBlank(touser))
        {
            markSkipped(latest, WecomContactGroupConstants.SKIP_NO_WECOM_USERID, now);
            return;
        }

        String chatId = StringUtils.isNotBlank(group.getChatId()) ? group.getChatId() : latest.getChatId();
        String groupName = WecomGroupReminderSupport.displayGroupName(group);
        String description = WecomGroupReminderSupport.buildTextcardDescription(contactName, groupName,
                latest.getRenderedText());
        String origin = publicAppUrlResolver.resolve(tenantId);
        String url = WecomGroupReminderSupport.buildSendPageUrl(origin, latest.getId());
        if (StringUtils.isBlank(url))
        {
            markSendFailure(latest, null, "tenant public app url missing", now);
            return;
        }

        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || StringUtils.isBlank(cfg.getAgentId()))
        {
            markSendFailure(latest, null, "wecom config missing", now);
            return;
        }

        try
        {
            wecomMessageService.sendTextcardToUser(cfg, touser, WecomGroupReminderSupport.TEXTCARD_TITLE,
                    description, url, WecomGroupReminderSupport.TEXTCARD_BTN);
            latest.setStatus(WecomContactGroupConstants.REMINDER_SENT);
            latest.setSentAt(now);
            latest.setChatId(chatId);
            latest.setErrcode(0);
            latest.setErrmsg(null);
            latest.setSkippedReason(null);
            latest.setUpdateTime(now);
            reminderMapper.update(latest);
            log.info("group reminder staff-notify textcard sent id={} contact={} touser={} url={}", latest.getId(),
                    latest.getContactId(), touser, url);
        }
        catch (Exception ex)
        {
            markSendFailure(latest, null, ex.getMessage(), now);
        }
    }

    private void markSkipped(CrmWecomGroupReminder row, String reason, Date now)
    {
        row.setStatus(WecomContactGroupConstants.REMINDER_SKIPPED);
        row.setSkippedReason(reason);
        row.setUpdateTime(now);
        reminderMapper.update(row);
        log.info("group reminder staff-notify skipped id={} reason={}", row.getId(), reason);
    }

    private void markSendFailure(CrmWecomGroupReminder row, Integer errcode, String errmsg, Date now)
    {
        int retry = row.getRetryCount() == null ? 0 : row.getRetryCount();
        retry++;
        row.setRetryCount(retry);
        row.setErrcode(errcode);
        row.setErrmsg(errmsg != null && errmsg.length() > 500 ? errmsg.substring(0, 500) : errmsg);
        if (retry >= WecomContactGroupConstants.MAX_RETRY)
        {
            row.setStatus(WecomContactGroupConstants.REMINDER_FAILED);
        }
        else
        {
            row.setStatus(WecomContactGroupConstants.REMINDER_PENDING);
            // keep claim_lease_until as short backoff until lease expires
        }
        row.setUpdateTime(now);
        reminderMapper.update(row);
        log.warn("group reminder staff-notify send fail id={} retry={} errmsg={}", row.getId(), retry, errmsg);
    }

    private void trySendNudge(String touser, String content)
    {
        try
        {
            CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
            if (cfg == null || StringUtils.isBlank(cfg.getAgentId()))
            {
                return;
            }
            wecomMessageService.sendTextToUser(cfg, touser, content);
        }
        catch (Exception e)
        {
            log.warn("group reminder provision nudge failed: {}", e.getMessage());
        }
    }
}
