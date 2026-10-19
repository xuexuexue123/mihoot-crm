package com.ruoyi.crm.service.wecom.contactgroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.CrmWecomGroupReminder;
import com.ruoyi.crm.domain.CrmWecomGroupReminderRule;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupReminderReportRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupReminderSendContextVo;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderRuleMapper;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 群提醒规则 CRUD + aibot pending/report（调试兼容）。
 * <p>
 * MVP 主投递为 {@link WecomGroupReminderStaffNotifyService}（Quartz Job + 应用消息通知员工）；
 * {@code GET .../pending} 非主路径，勿再依赖 aibot 向客户群 chatId 发送。
 */
@Service
public class WecomGroupReminderService
{
    private static final Logger log = LoggerFactory.getLogger(WecomGroupReminderService.class);

    private static final String PERM_REMINDER_LIST = "crm:wecom:reminder:list";
    private static final String PERM_ALL = "*:*:*";
    private static final int HTTP_UNPROCESSABLE = 422;

    @Autowired
    private CrmWecomGroupReminderRuleMapper ruleMapper;

    @Autowired
    private CrmWecomGroupReminderMapper reminderMapper;

    @Autowired
    private CrmWecomContactGroupMapper contactGroupMapper;

    @Autowired
    private WecomGroupReminderRuleSystemSeeder seeder;

    @Autowired
    private WecomContactGroupService contactGroupService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private com.ruoyi.crm.mapper.CrmContactMapper contactMapper;

    @Autowired
    private com.ruoyi.system.mapper.SysUserMapper sysUserMapper;

    public List<CrmWecomGroupReminderRule> listRules(CrmWecomGroupReminderRule query)
    {
        String tenantId = requireTenant();
        seeder.ensure(tenantId);
        if (query == null)
        {
            query = new CrmWecomGroupReminderRule();
        }
        query.setTenantId(tenantId);
        return ruleMapper.selectList(query);
    }

    public CrmWecomGroupReminderRule getRule(String ruleId)
    {
        String tenantId = requireTenant();
        seeder.ensure(tenantId);
        return ruleMapper.selectById(tenantId, ruleId);
    }

    public int insertRule(CrmWecomGroupReminderRule row)
    {
        String tenantId = requireTenant();
        if (StringUtils.isBlank(row.getName()) || StringUtils.isBlank(row.getAnchorType())
                || StringUtils.isBlank(row.getTemplateBody()))
        {
            throw new ServiceException("name / anchorType / templateBody 不能为空");
        }
        row.setRuleId(StringUtils.isBlank(row.getRuleId()) ? IdUtils.fastUUID() : row.getRuleId());
        row.setTenantId(tenantId);
        row.setEnabled(StringUtils.isBlank(row.getEnabled()) ? WecomContactGroupConstants.ENABLED : row.getEnabled());
        row.setIsSystem(WecomContactGroupConstants.DISABLED);
        row.setPriority(row.getPriority() == null ? 100 : row.getPriority());
        row.setOffsetValue(row.getOffsetValue() == null ? 0 : row.getOffsetValue());
        row.setOffsetUnit(StringUtils.isBlank(row.getOffsetUnit()) ? WecomContactGroupConstants.OFFSET_DAYS
                : row.getOffsetUnit());
        row.setRequireActiveGroup(StringUtils.isBlank(row.getRequireActiveGroup())
                ? WecomContactGroupConstants.ENABLED : row.getRequireActiveGroup());
        row.setDelFlag("0");
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateTime(DateUtils.getNowDate());
        return ruleMapper.insert(row);
    }

    public int updateRule(CrmWecomGroupReminderRule row)
    {
        String tenantId = requireTenant();
        CrmWecomGroupReminderRule existing = ruleMapper.selectById(tenantId, row.getRuleId());
        if (existing == null)
        {
            throw new ServiceException("规则不存在");
        }
        if (WecomContactGroupConstants.IS_SYSTEM.equals(existing.getIsSystem()))
        {
            // system: only allow enabled / template / match / sendTime / offset
            CrmWecomGroupReminderRule patch = new CrmWecomGroupReminderRule();
            patch.setTenantId(tenantId);
            patch.setRuleId(existing.getRuleId());
            patch.setEnabled(row.getEnabled());
            patch.setTemplateBody(row.getTemplateBody());
            patch.setMatchJson(row.getMatchJson());
            patch.setSendTime(row.getSendTime());
            patch.setOffsetValue(row.getOffsetValue());
            patch.setOffsetUnit(row.getOffsetUnit());
            patch.setRequireActiveGroup(row.getRequireActiveGroup());
            patch.setUpdateBy(row.getUpdateBy());
            patch.setUpdateTime(DateUtils.getNowDate());
            return ruleMapper.update(patch);
        }
        row.setTenantId(tenantId);
        row.setUpdateTime(DateUtils.getNowDate());
        return ruleMapper.update(row);
    }

    public int deleteRule(String ruleId)
    {
        String tenantId = requireTenant();
        CrmWecomGroupReminderRule existing = ruleMapper.selectById(tenantId, ruleId);
        if (existing == null)
        {
            return 0;
        }
        if (WecomContactGroupConstants.IS_SYSTEM.equals(existing.getIsSystem()))
        {
            throw new ServiceException("系统规则不可删除");
        }
        return ruleMapper.softDelete(tenantId, ruleId);
    }

    public List<CrmWecomGroupReminder> listReminders(CrmWecomGroupReminder query)
    {
        String tenantId = requireTenant();
        if (query == null)
        {
            query = new CrmWecomGroupReminder();
        }
        query.setTenantId(tenantId);
        return reminderMapper.selectList(query);
    }

    /**
     * 跨租户拉取到期待发（短租约）。调试兼容；MVP 主路径为 StaffNotify Job。
     */
    public List<CrmWecomGroupReminder> pollPendingAcrossTenants(Integer limit)
    {
        int lim = limit == null || limit <= 0 ? WecomContactGroupConstants.DEFAULT_PENDING_LIMIT : Math.min(limit, 200);
        List<CrmWecomGroupReminder> out = new ArrayList<>();
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
            if (out.size() >= lim)
            {
                break;
            }
            String tenantId = tenant.getTenant();
            if (StringUtils.isBlank(tenantId))
            {
                continue;
            }
            try
            {
                DynamicDataSourceContextHolder.setDataSourceKey(tenantId);
                seeder.ensure(tenantId);
                int remain = lim - out.size();
                List<CrmWecomGroupReminder> due = reminderMapper.selectDuePending(tenantId, now, remain);
                for (CrmWecomGroupReminder r : due)
                {
                    CrmWecomContactGroup group = contactGroupMapper.selectByContactId(tenantId, r.getContactId());
                    if (!WecomGroupReminderSupport.isServiceGroupReady(group))
                    {
                        r.setStatus(WecomContactGroupConstants.REMINDER_SKIPPED);
                        r.setSkippedReason(WecomContactGroupConstants.SKIP_NO_SERVICE_GROUP);
                        r.setUpdateTime(now);
                        reminderMapper.update(r);
                        try
                        {
                            contactGroupService.renotifyForUnreadyGroup(r.getContactId());
                        }
                        catch (Exception e)
                        {
                            log.warn("renotify after skip failed: {}", e.getMessage());
                        }
                        continue;
                    }
                    int claimed = reminderMapper.claimLease(r.getId(), leaseUntil, now);
                    if (claimed <= 0)
                    {
                        continue;
                    }
                    r.setChatId(group.getChatId());
                    r.setClaimLeaseUntil(leaseUntil);
                    r.setGroupName(WecomGroupReminderSupport.displayGroupName(group));
                    try
                    {
                        com.ruoyi.crm.domain.CrmContact contact = contactMapper
                                .selectCrmContactByContactId(r.getContactId());
                        if (contact != null)
                        {
                            r.setContactName(contact.getName());
                            r.setTouser(WecomGroupReminderSupport.resolveSnUserid(contact, sysUserMapper));
                        }
                    }
                    catch (Exception ignored)
                    {
                    }
                    CrmWecomGroupReminder patch = new CrmWecomGroupReminder();
                    patch.setId(r.getId());
                    patch.setChatId(group.getChatId());
                    patch.setUpdateTime(now);
                    reminderMapper.update(patch);
                    out.add(r);
                    if (out.size() >= lim)
                    {
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                log.warn("group reminder pending tenant {}: {}", tenantId, e.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
        return out;
    }

    public void report(WecomGroupReminderReportRequest req)
    {
        if (req == null || StringUtils.isBlank(req.getId()) || StringUtils.isBlank(req.getStatus()))
        {
            throw new ServiceException("id / status 不能为空");
        }
        String tenantHint = req.getTenant();
        if (StringUtils.isNotBlank(tenantHint))
        {
            try
            {
                DynamicDataSourceContextHolder.setDataSourceKey(tenantHint);
                doReport(req);
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
            return;
        }
        // fallback: try current DS
        doReport(req);
    }

    private void doReport(WecomGroupReminderReportRequest req)
    {
        CrmWecomGroupReminder row = reminderMapper.selectById(req.getId());
        if (row == null)
        {
            throw new ServiceException("reminder 不存在");
        }
        Date now = DateUtils.getNowDate();
        String status = req.getStatus().trim().toLowerCase();
        if (WecomContactGroupConstants.REMINDER_SENT.equals(status))
        {
            row.setStatus(WecomContactGroupConstants.REMINDER_SENT);
            row.setSentAt(now);
            row.setErrcode(req.getErrcode() == null ? 0 : req.getErrcode());
            row.setErrmsg(req.getErrmsg());
            row.setClaimLeaseUntil(null);
        }
        else if (WecomContactGroupConstants.REMINDER_FAILED.equals(status))
        {
            int retry = row.getRetryCount() == null ? 0 : row.getRetryCount();
            retry++;
            row.setRetryCount(retry);
            row.setErrcode(req.getErrcode());
            row.setErrmsg(req.getErrmsg());
            if (retry >= WecomContactGroupConstants.MAX_RETRY)
            {
                row.setStatus(WecomContactGroupConstants.REMINDER_FAILED);
            }
            else
            {
                row.setStatus(WecomContactGroupConstants.REMINDER_PENDING);
            }
            row.setClaimLeaseUntil(null);
            // unactivated session: revert group
            if (req.getErrmsg() != null && req.getErrmsg().toLowerCase().contains("activ"))
            {
                CrmWecomContactGroup g = contactGroupMapper.selectByContactId(row.getTenantId(), row.getContactId());
                if (g != null && WecomContactGroupConstants.GROUP_ACTIVE.equals(g.getStatus()))
                {
                    g.setStatus(WecomContactGroupConstants.GROUP_AWAITING_BOT);
                    g.setLastError(req.getErrmsg());
                    g.setUpdateTime(now);
                    contactGroupMapper.update(g);
                    contactGroupService.renotifyForUnreadyGroup(row.getContactId());
                }
            }
        }
        else
        {
            throw new ServiceException("非法 status: " + req.getStatus());
        }
        row.setUpdateTime(now);
        reminderMapper.update(row);
    }

    /**
     * H5 发送页上下文：list 权限或 contact owner 的 snUserid 可访问；cancelled 仍返回。
     */
    public WecomGroupReminderSendContextVo getSendContext(String id)
    {
        if (StringUtils.isBlank(id))
        {
            throw new ServiceException("reminder 不存在", HttpStatus.NOT_FOUND);
        }
        CrmWecomGroupReminder row = reminderMapper.selectById(id.trim());
        if (row == null)
        {
            throw new ServiceException("reminder 不存在", HttpStatus.NOT_FOUND);
        }
        if (StringUtils.isBlank(row.getRenderedText()))
        {
            throw new ServiceException("话术为空，无法打开发送页", HTTP_UNPROCESSABLE);
        }

        CrmContact contact = null;
        try
        {
            contact = contactMapper.selectCrmContactByContactId(row.getContactId());
        }
        catch (Exception ignored)
        {
        }
        assertSendContextAccess(contact);

        CrmWecomContactGroup group = null;
        try
        {
            group = contactGroupMapper.selectByContactId(row.getTenantId(), row.getContactId());
        }
        catch (Exception ignored)
        {
        }

        WecomGroupReminderSendContextVo vo = new WecomGroupReminderSendContextVo();
        vo.setId(row.getId());
        vo.setRenderedText(row.getRenderedText());
        vo.setContactId(row.getContactId());
        vo.setContactName(contact != null && StringUtils.isNotBlank(contact.getName()) ? contact.getName()
                : row.getContactId());
        vo.setStatus(row.getStatus());
        vo.setDueAt(row.getDueAt());
        vo.setRuleId(row.getRuleId());

        if (group != null && WecomGroupReminderSupport.isServiceGroupReady(group))
        {
            vo.setChatId(group.getChatId());
            vo.setGroupName(WecomGroupReminderSupport.displayGroupName(group));
        }
        else
        {
            // dismissed / 无群：仍可返回历史 chatId，groupName 可空
            if (StringUtils.isNotBlank(row.getChatId()))
            {
                vo.setChatId(row.getChatId());
            }
            if (group != null && StringUtils.isNotBlank(group.getGroupName()))
            {
                vo.setGroupName(group.getGroupName().trim());
            }
        }
        return vo;
    }

    private void assertSendContextAccess(CrmContact contact)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null && loginUser.getPermissions() != null
                && (loginUser.getPermissions().contains(PERM_ALL)
                        || loginUser.getPermissions().contains(PERM_REMINDER_LIST)))
        {
            return;
        }
        String snUserid = null;
        if (loginUser != null && loginUser.getUser() != null)
        {
            snUserid = loginUser.getUser().getSnUserid();
        }
        String ownerTouser = WecomGroupReminderSupport.resolveSnUserid(contact, sysUserMapper);
        if (StringUtils.isNotBlank(snUserid) && StringUtils.isNotBlank(ownerTouser)
                && snUserid.trim().equalsIgnoreCase(ownerTouser.trim()))
        {
            return;
        }
        throw new ServiceException("无权查看该群提醒发送上下文", HttpStatus.FORBIDDEN);
    }

    private String requireTenant()
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户上下文");
        }
        return tenantId;
    }
}
