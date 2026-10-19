package com.ruoyi.crm.service.wecom.contactgroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomAiBotConfig;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.CrmWecomGroupProvisionTask;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarGroupResolveResultDto;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomAiBotConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupProvisionTaskMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderMapper;
import com.ruoyi.crm.service.support.MasterDataSourceSupport;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.service.support.TenantPublicAppUrlResolver;
import com.ruoyi.crm.wecom.WecomMessageService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;

/**
 * 一客一群：provision / bind / activate / 查询
 */
@Service
public class WecomContactGroupService
{
    private static final Logger log = LoggerFactory.getLogger(WecomContactGroupService.class);

    @Autowired
    private CrmWecomContactGroupMapper contactGroupMapper;

    @Autowired
    private CrmWecomGroupProvisionTaskMapper provisionTaskMapper;

    @Autowired
    private CrmWecomGroupReminderMapper reminderMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private CrmWecomAiBotConfigMapper aiBotConfigMapper;

    @Autowired
    private WecomMessageService wecomMessageService;

    @Autowired
    private TenantPublicAppUrlResolver publicAppUrlResolver;

    @Autowired(required = false)
    @Qualifier("threadPoolTaskExecutor")
    private Executor executor;

    public CrmWecomContactGroup byContact(String contactId)
    {
        String tenantId = requireTenant();
        return contactGroupMapper.selectByContactId(tenantId, contactId);
    }

    /**
     * 按 chatId 查绑定；0 行返回 null；多行抛出 ServiceException（禁止静默取一条）。
     */
    public CrmWecomContactGroup byChat(String chatId)
    {
        if (StringUtils.isBlank(chatId))
        {
            throw new ServiceException("chatId 不能为空", 400);
        }
        String tenantId = requireTenant();
        List<CrmWecomContactGroup> list = contactGroupMapper.selectListByChatId(tenantId, chatId.trim());
        if (list == null || list.isEmpty())
        {
            return null;
        }
        if (list.size() > 1)
        {
            throw new ServiceException(
                    WecomSidebarGroupResolveResultDto.MSG_DUPLICATE_BINDING, 409);
        }
        return list.get(0);
    }

    public Map<String, Object> provisionContext(String contactId, String taskId)
    {
        String tenantId = requireTenant();
        CrmWecomGroupProvisionTask task = null;
        if (StringUtils.isNotBlank(taskId))
        {
            task = provisionTaskMapper.selectByTaskId(taskId);
        }
        if (task == null && StringUtils.isNotBlank(contactId))
        {
            task = provisionTaskMapper.selectPendingByContact(tenantId, contactId);
        }
        if (task == null)
        {
            throw new ServiceException("建群任务不存在");
        }
        CrmContact contact = crmContactMapper.selectCrmContactByContactId(task.getContactId());
        if (contact == null)
        {
            throw new ServiceException("联系人不存在");
        }
        CrmWecomContactGroup group = contactGroupMapper.selectByContactId(tenantId, task.getContactId());
        String botDisplayName = "智能机器人";
        try
        {
            CrmWecomAiBotConfig botCfg = aiBotConfigMapper.selectFirst();
            if (botCfg != null && StringUtils.isNotBlank(botCfg.getBotId()))
            {
                botDisplayName = botCfg.getBotId();
            }
        }
        catch (Exception ignored)
        {
        }

        Map<String, Object> data = new HashMap<>();
        data.put("taskId", task.getTaskId());
        data.put("contactId", task.getContactId());
        data.put("contactName", contact.getName());
        data.put("externalUserid", contact.getWecomExternalUserid());
        data.put("ownerUserid", task.getAssigneeUserid());
        data.put("groupStatus", group != null ? group.getStatus() : WecomContactGroupConstants.GROUP_PENDING);
        data.put("chatId", group != null ? group.getChatId() : null);
        data.put("groupName", group != null && StringUtils.isNotBlank(group.getGroupName())
                ? group.getGroupName()
                : defaultGroupName(contact.getName()));
        data.put("botDisplayName", botDisplayName);
        data.put("taskStatus", task.getStatus());
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> provision(String contactId)
    {
        String tenantId = requireTenant();
        if (StringUtils.isBlank(contactId))
        {
            throw new ServiceException("contactId 不能为空");
        }
        CrmContact contact = crmContactMapper.selectCrmContactByContactId(contactId);
        if (contact == null)
        {
            throw new ServiceException("联系人不存在");
        }
        if (StringUtils.isBlank(contact.getWecomExternalUserid()))
        {
            throw new ServiceException("联系人未绑定企微外部联系人");
        }

        CrmWecomContactGroup existing = contactGroupMapper.selectByContactId(tenantId, contactId);
        if (existing != null)
        {
            String st = existing.getStatus();
            if (WecomContactGroupConstants.GROUP_ACTIVE.equals(st)
                    || WecomContactGroupConstants.GROUP_CREATED.equals(st)
                    || WecomContactGroupConstants.GROUP_AWAITING_BOT.equals(st))
            {
                Map<String, Object> data = new HashMap<>();
                data.put("group", existing);
                data.put("reused", true);
                return data;
            }
        }

        Assignee assignee = resolveAssignee(contact);
        if (StringUtils.isBlank(assignee.snUserid))
        {
            throw new ServiceException("无法解析负责员工企微 userid");
        }

        Date now = DateUtils.getNowDate();
        CrmWecomGroupProvisionTask pendingTask = provisionTaskMapper.selectPendingByContact(tenantId, contactId);
        if (pendingTask == null)
        {
            pendingTask = new CrmWecomGroupProvisionTask();
            pendingTask.setTaskId(IdUtils.fastUUID());
            pendingTask.setTenantId(tenantId);
            pendingTask.setContactId(contactId);
            pendingTask.setAssigneeUserid(assignee.snUserid);
            pendingTask.setAssigneeCrmUserId(assignee.crmUserId);
            pendingTask.setStatus(WecomContactGroupConstants.TASK_PENDING);
            pendingTask.setRemindCount(0);
            pendingTask.setCreateTime(now);
            pendingTask.setUpdateTime(now);
            provisionTaskMapper.insert(pendingTask);
        }

        CrmWecomContactGroup group = existing;
        if (group == null)
        {
            group = new CrmWecomContactGroup();
            group.setId(IdUtils.fastUUID());
            group.setTenantId(tenantId);
            group.setContactId(contactId);
            group.setOwnerUserid(assignee.snUserid);
            group.setGroupName(defaultGroupName(contact.getName()));
            group.setStatus(WecomContactGroupConstants.GROUP_PENDING);
            group.setProvisionTaskId(pendingTask.getTaskId());
            group.setCreateTime(now);
            group.setUpdateTime(now);
            contactGroupMapper.insert(group);
        }
        else
        {
            group.setOwnerUserid(assignee.snUserid);
            group.setStatus(WecomContactGroupConstants.GROUP_PENDING);
            group.setProvisionTaskId(pendingTask.getTaskId());
            group.setLastError(null);
            group.setUpdateTime(now);
            contactGroupMapper.update(group);
        }

        notifyAssigneeAfterCommit(tenantId, contact, pendingTask, false);

        Map<String, Object> data = new HashMap<>();
        data.put("group", group);
        data.put("task", pendingTask);
        data.put("reused", false);
        return data;
    }

    /**
     * 在租户事务提交后再发企微通知。
     * <p>
     * 仅 afterCommit 不够：提交后请求线程上仍可能挂着租户 ConnectionHolder，
     * {@code @DataSource(MASTER)} / ThreadLocal 切主库仍会复用租户物理连接，
     * 导致 {@code master_tenant.website_slug} 查到租户库报错。必须换线程执行。
     */
    private void notifyAssigneeAfterCommit(String tenantId, CrmContact contact,
            CrmWecomGroupProvisionTask task, boolean renotify)
    {
        Runnable run = () -> runNotifyWithTenantKey(tenantId, contact, task, renotify);
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    runNotifyAsync(run);
                }
            });
        }
        else
        {
            runNotifyAsync(run);
        }
    }

    private void runNotifyAsync(Runnable run)
    {
        if (executor != null)
        {
            executor.execute(run);
        }
        else
        {
            // 无线程池时也必须换线程，避免复用请求线程上的租户 ConnectionHolder
            CompletableFuture.runAsync(run);
        }
    }

    private void runNotifyWithTenantKey(String tenantId, CrmContact contact,
            CrmWecomGroupProvisionTask task, boolean renotify)
    {
        String previousKey = MasterDataSourceSupport.currentKey();
        try
        {
            TenantDataSourceSupport.bindTenantKey(tenantId);
            notifyAssignee(tenantId, contact, task, renotify);
        }
        finally
        {
            MasterDataSourceSupport.restoreKey(previousKey);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public CrmWecomContactGroup bind(String taskId, String chatId, String groupName)
    {
        String tenantId = requireTenant();
        if (StringUtils.isBlank(taskId) || StringUtils.isBlank(chatId))
        {
            throw new ServiceException("taskId 与 chatId 不能为空");
        }
        CrmWecomGroupProvisionTask task = provisionTaskMapper.selectByTaskId(taskId);
        if (task == null || !tenantId.equals(task.getTenantId()))
        {
            throw new ServiceException("建群任务不存在");
        }
        CrmWecomContactGroup group = contactGroupMapper.selectByContactId(tenantId, task.getContactId());
        if (group == null)
        {
            throw new ServiceException("群绑定不存在");
        }
        CrmWecomContactGroup byChat = contactGroupMapper.selectByChatId(tenantId, chatId);
        if (byChat != null && !byChat.getId().equals(group.getId()))
        {
            log.error(
                    "contact-group bind conflict: chatId={} already bound to contactId={} refused for contactId={} taskId={}",
                    chatId, byChat.getContactId(), group.getContactId(), taskId);
            throw new ServiceException("该 chatId 已绑定到其他联系人");
        }
        if (byChat != null && chatId.equals(group.getChatId()))
        {
            markProvisionTaskDone(group, DateUtils.getNowDate());
            log.info("contact-group bind idempotent: chatId={} contactId={} taskId={}", chatId,
                    group.getContactId(), taskId);
            return group;
        }
        applyChatIdBinding(group, chatId, groupName, DateUtils.getNowDate());
        markProvisionTaskDone(group, DateUtils.getNowDate());
        log.info("contact-group bind ok: chatId={} contactId={} groupId={} taskId={}", chatId,
                group.getContactId(), group.getId(), taskId);
        return group;
    }

    @Transactional(rollbackFor = Exception.class)
    public CrmWecomContactGroup activate(String taskId, String contactId, String chatId)
    {
        String tenantId = requireTenant();
        CrmWecomContactGroup group = null;
        CrmWecomGroupProvisionTask task = null;
        if (StringUtils.isNotBlank(taskId))
        {
            task = provisionTaskMapper.selectByTaskId(taskId);
            if (task != null)
            {
                group = contactGroupMapper.selectByContactId(tenantId, task.getContactId());
            }
        }
        if (group == null && StringUtils.isNotBlank(contactId))
        {
            group = contactGroupMapper.selectByContactId(tenantId, contactId);
        }
        if (group == null && StringUtils.isNotBlank(chatId))
        {
            group = contactGroupMapper.selectByChatId(tenantId, chatId);
        }
        if (group == null)
        {
            throw new ServiceException("群绑定不存在");
        }
        if (StringUtils.isNotBlank(chatId) && StringUtils.isBlank(group.getChatId()))
        {
            group.setChatId(chatId);
        }
        Date now = DateUtils.getNowDate();
        group.setStatus(WecomContactGroupConstants.GROUP_ACTIVE);
        group.setBotActivatedAt(now);
        group.setLastError(null);
        group.setUpdateTime(now);
        contactGroupMapper.update(group);

        if (task == null && StringUtils.isNotBlank(group.getProvisionTaskId()))
        {
            task = provisionTaskMapper.selectByTaskId(group.getProvisionTaskId());
        }
        if (task != null)
        {
            task.setStatus(WecomContactGroupConstants.TASK_DONE);
            task.setUpdateTime(now);
            provisionTaskMapper.update(task);
        }
        return group;
    }

    /**
     * 群未就绪时再次通知员工（reminder skipped 路径）。
     */
    public void renotifyForUnreadyGroup(String contactId)
    {
        String tenantId = requireTenant();
        CrmContact contact = crmContactMapper.selectCrmContactByContactId(contactId);
        if (contact == null)
        {
            return;
        }
        CrmWecomGroupProvisionTask task = provisionTaskMapper.selectPendingByContact(tenantId, contactId);
        if (task == null && contactGroupMapper.selectByContactId(tenantId, contactId) != null)
        {
            CrmWecomContactGroup g = contactGroupMapper.selectByContactId(tenantId, contactId);
            if (g != null && StringUtils.isNotBlank(g.getProvisionTaskId()))
            {
                task = provisionTaskMapper.selectByTaskId(g.getProvisionTaskId());
            }
        }
        if (task == null)
        {
            try
            {
                provision(contactId);
            }
            catch (Exception e)
            {
                log.warn("renotify provision failed contact={}: {}", contactId, e.getMessage());
            }
            return;
        }
        notifyAssigneeAfterCommit(tenantId, contact, task, true);
    }

    public void markDismissedByChatId(String chatId)
    {
        String tenantId = requireTenant();
        if (StringUtils.isBlank(chatId))
        {
            return;
        }
        CrmWecomContactGroup group = contactGroupMapper.selectByChatId(tenantId, chatId);
        if (group == null)
        {
            return;
        }
        Date now = DateUtils.getNowDate();
        group.setStatus(WecomContactGroupConstants.GROUP_DISMISSED);
        group.setUpdateTime(now);
        contactGroupMapper.update(group);
        reminderMapper.cancelPendingByContact(tenantId, group.getContactId());
        if (StringUtils.isNotBlank(group.getProvisionTaskId()))
        {
            CrmWecomGroupProvisionTask task = provisionTaskMapper.selectByTaskId(group.getProvisionTaskId());
            if (task != null && WecomContactGroupConstants.TASK_PENDING.equals(task.getStatus()))
            {
                task.setStatus(WecomContactGroupConstants.TASK_CANCELLED);
                task.setUpdateTime(now);
                provisionTaskMapper.update(task);
            }
        }
    }

    /**
     * 回调补绑：owner + type=2 external_userid 列表匹配 chat_id 仍空的 pending/created 行。
     */
    public void bindFromCallback(String ownerUserid, String chatId, List<String> externalUserids, String groupName)
    {
        String tenantId = requireTenant();
        if (StringUtils.isBlank(chatId))
        {
            return;
        }
        List<String> externals = normalizeExternals(externalUserids);
        CrmWecomContactGroup byChat = contactGroupMapper.selectByChatId(tenantId, chatId);
        if (byChat != null)
        {
            log.info(
                    "contact-group callback bind idempotent: matched=true chatId={} contactId={} groupId={} provisionTaskId={}",
                    chatId, byChat.getContactId(), byChat.getId(), byChat.getProvisionTaskId());
            return;
        }
        if (StringUtils.isBlank(ownerUserid))
        {
            log.warn(
                    "contact-group callback bind matched=false chatId={} owner=null externalUserids={} reason=missing_owner",
                    chatId, externals);
            return;
        }

        List<CrmWecomContactGroup> candidates = contactGroupMapper.selectPendingOrCreatedByOwnerAndExternal(tenantId,
                ownerUserid);
        if (candidates == null)
        {
            candidates = Collections.emptyList();
        }

        CrmWecomContactGroup matched = selectBestCallbackMatch(candidates, externals);
        if (matched == null && candidates.size() == 1 && externals.isEmpty())
        {
            matched = candidates.get(0);
            log.warn(
                    "contact-group callback bind weak-match: chatId={} owner={} groupId={} contactId={} reason=single_candidate_no_external",
                    chatId, ownerUserid, matched.getId(), matched.getContactId());
        }
        if (matched == null)
        {
            log.warn(
                    "contact-group callback bind matched=false chatId={} owner={} externalUserids={} candidateCount={}",
                    chatId, ownerUserid, externals, candidates.size());
            return;
        }

        Date now = DateUtils.getNowDate();
        applyChatIdBinding(matched, chatId, groupName, now);
        markProvisionTaskDone(matched, now);
        log.info(
                "contact-group callback bind matched=true chatId={} owner={} contactId={} groupId={} provisionTaskId={} externalUserids={}",
                chatId, ownerUserid, matched.getContactId(), matched.getId(), matched.getProvisionTaskId(),
                externals);
    }

    private CrmWecomContactGroup selectBestCallbackMatch(List<CrmWecomContactGroup> candidates,
            List<String> externals)
    {
        if (candidates.isEmpty() || externals.isEmpty())
        {
            return null;
        }
        Set<String> externalSet = new HashSet<>(externals);
        List<CrmWecomContactGroup> hits = new ArrayList<>();
        for (CrmWecomContactGroup g : candidates)
        {
            if (StringUtils.isNotBlank(g.getChatId()))
            {
                continue;
            }
            CrmContact c = crmContactMapper.selectCrmContactByContactId(g.getContactId());
            if (c == null || StringUtils.isBlank(c.getWecomExternalUserid()))
            {
                continue;
            }
            if (externalSet.contains(c.getWecomExternalUserid()))
            {
                hits.add(g);
            }
        }
        if (hits.isEmpty())
        {
            return null;
        }
        hits.sort(Comparator
                .comparing((CrmWecomContactGroup g) -> !isProvisionTaskPending(g))
                .thenComparing(CrmWecomContactGroup::getUpdateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())));
        return hits.get(0);
    }

    private boolean isProvisionTaskPending(CrmWecomContactGroup group)
    {
        if (group == null || StringUtils.isBlank(group.getProvisionTaskId()))
        {
            return false;
        }
        CrmWecomGroupProvisionTask task = provisionTaskMapper.selectByTaskId(group.getProvisionTaskId());
        return task != null && WecomContactGroupConstants.TASK_PENDING.equals(task.getStatus());
    }

    private void applyChatIdBinding(CrmWecomContactGroup group, String chatId, String groupName, Date now)
    {
        group.setChatId(chatId);
        if (StringUtils.isNotBlank(groupName))
        {
            group.setGroupName(groupName);
        }
        if (WecomContactGroupConstants.GROUP_PENDING.equals(group.getStatus())
                || WecomContactGroupConstants.GROUP_FAILED.equals(group.getStatus()))
        {
            group.setStatus(WecomContactGroupConstants.GROUP_CREATED);
        }
        group.setUpdateTime(now);
        contactGroupMapper.update(group);
    }

    private void markProvisionTaskDone(CrmWecomContactGroup group, Date now)
    {
        if (group == null || StringUtils.isBlank(group.getProvisionTaskId()))
        {
            return;
        }
        CrmWecomGroupProvisionTask task = provisionTaskMapper.selectByTaskId(group.getProvisionTaskId());
        if (task == null)
        {
            return;
        }
        if (WecomContactGroupConstants.TASK_DONE.equals(task.getStatus())
                || WecomContactGroupConstants.TASK_CANCELLED.equals(task.getStatus()))
        {
            return;
        }
        task.setStatus(WecomContactGroupConstants.TASK_DONE);
        task.setUpdateTime(now);
        provisionTaskMapper.update(task);
    }

    private static List<String> normalizeExternals(List<String> externalUserids)
    {
        if (externalUserids == null || externalUserids.isEmpty())
        {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String id : externalUserids)
        {
            if (StringUtils.isNotBlank(id))
            {
                result.add(id.trim());
            }
        }
        return result;
    }

    private void notifyAssignee(String tenantId, CrmContact contact, CrmWecomGroupProvisionTask task, boolean renotify)
    {
        try
        {
            CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
            if (cfg == null || StringUtils.isBlank(cfg.getAgentId()))
            {
                log.warn("skip provision notify: wecom config missing");
                return;
            }
            String base = publicAppUrlResolver.resolve(tenantId);
            if (StringUtils.isBlank(base))
            {
                log.warn("skip provision notify: public app url unresolved tenant={}", tenantId);
                return;
            }
            String url = base + "/wecom/provision-group?contactId=" + contact.getContactId()
                    + "&taskId=" + task.getTaskId();
            String title = renotify ? "【提醒未能发送】请完成服务群" : "【待建服务群】";
            String desc = renotify
                    ? "客户 " + nullToEmpty(contact.getName()) + " 的群提醒未能发送，请完成建群/激活。"
                    : "客户 " + nullToEmpty(contact.getName()) + " 需专属服务群以开启自动提醒。请点击一键建群。";
            wecomMessageService.sendTextcardToUser(cfg, task.getAssigneeUserid(), title, desc, url);
            Date now = DateUtils.getNowDate();
            task.setNotifiedAt(now);
            int count = task.getRemindCount() == null ? 0 : task.getRemindCount();
            if (renotify)
            {
                task.setRemindCount(count + 1);
            }
            task.setUpdateTime(now);
            provisionTaskMapper.update(task);
        }
        catch (Exception e)
        {
            log.warn("provision notify failed task={}: {}", task.getTaskId(), e.getMessage());
        }
    }

    private Assignee resolveAssignee(CrmContact contact)
    {
        Assignee a = new Assignee();
        if (contact.getUserId() != null)
        {
            SysUser user = sysUserMapper.selectUserById(contact.getUserId());
            if (user != null)
            {
                a.crmUserId = String.valueOf(user.getUserId());
                if (StringUtils.isNotBlank(user.getSnUserid()))
                {
                    a.snUserid = user.getSnUserid();
                    return a;
                }
            }
        }
        if (StringUtils.isNotBlank(contact.getWecomFollowUserid()))
        {
            a.snUserid = contact.getWecomFollowUserid();
        }
        return a;
    }

    private static String defaultGroupName(String contactName)
    {
        String name = StringUtils.isBlank(contactName) ? "客户" : contactName.trim();
        if (name.length() > 100)
        {
            name = name.substring(0, 100);
        }
        return name + "-服务群";
    }

    private static String nullToEmpty(String s)
    {
        return s == null ? "" : s;
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

    private static class Assignee
    {
        String snUserid;
        String crmUserId;
    }
}
