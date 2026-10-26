package com.ruoyi.crm.wechatmp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.domain.vo.EventAudienceOpenIdResultVo;
import com.ruoyi.crm.domain.vo.SegmentMemberVo;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.IEventAudienceService;
import com.ruoyi.crm.service.ICrmSegmentService;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompileOptions;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompileResult;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompiler;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessageTask;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessageTaskItem;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageBatchRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMessageTaskMapper;
import com.ruoyi.crm.wechatmp.service.EventWechatMpAppIdResolver;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessageTaskService;
import com.ruoyi.crm.wechatmp.service.WechatMpMessageBatchWorker;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.manager.AsyncManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WechatMpMessageTaskServiceImpl implements IWechatMpMessageTaskService
{
    private static final int TASK_ITEM_BATCH_SIZE = 500;

    @Autowired
    private CrmWechatMpMessageTaskMapper taskMapper;

    @Autowired
    private ICrmSegmentService segmentService;

    @Autowired
    private IEventAudienceService eventAudienceService;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private FanAudienceCompiler fanAudienceCompiler;

    @Autowired
    private WechatMpMessageBatchWorker batchWorker;

    @Autowired
    private EventWechatMpAppIdResolver eventWechatMpAppIdResolver;

    @Override
    public CrmWechatMpMessageTask createBatch(String tenantId, WechatMpMessageBatchRequest req)
    {
        resolveBatchAppId(req);
        if (req.getAppId() == null || req.getTaskType() == null)
        {
            throw new ServiceException("appId 和 taskType 不能为空");
        }
        AudienceResolveResult resolved = resolveAudience(tenantId, req);
        Set<String> distinctOpenIds = new LinkedHashSet<>(resolved.openIds);
        List<String> openIds = new ArrayList<>(distinctOpenIds);
        JSONObject contentJson = req.getContentJson() != null
                ? req.getContentJson()
                : new JSONObject();
        if (StringUtils.isNotEmpty(tenantId) && StringUtils.isNotEmpty(contentJson.getString("eventId")))
        {
            contentJson.put("tenantId", tenantId);
        }
        if (StringUtils.isNotEmpty(tenantId) && StringUtils.isNotEmpty(contentJson.getString("surveyId")))
        {
            contentJson.put("tenantId", tenantId);
        }
        CrmWechatMpMessageTask task = new CrmWechatMpMessageTask();
        task.setAppId(req.getAppId());
        task.setTaskType(req.getTaskType());
        task.setAudienceType(req.getAudienceType());
        task.setAudienceRef(req.getAudienceRef());
        if (req.getFilterJson() != null)
        {
            task.setFilterJson(req.getFilterJson().toJSONString());
        }
        task.setTemplateId(req.getTemplateId());
        task.setContentJson(contentJson.toJSONString());
        task.setStatus("pending");
        task.setTotalCount(openIds.size());
        task.setSuccessCount(0);
        task.setFailCount(0);
        task.setSkipCount(resolved.skipCount);
        task.setScheduledAt(req.getScheduledAt());
        task.setCreateBy(SecurityUtils.getUsername());
        task.setCreateTime(DateUtils.getNowDate());
        task.setUpdateTime(DateUtils.getNowDate());
        taskMapper.insertTask(task);
        insertTaskItems(task.getId(), openIds);
        final Long taskId = task.getId();
        final String tenantKey = tenantId;
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(tenantKey);
                    batchWorker.processTask(taskId);
                }
                catch (Exception e)
                {
                    log.error("WeChat MP message batch failed for taskId={}, tenant={}", taskId, tenantKey, e);
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
        return task;
    }

    @Override
    public List<CrmWechatMpMessageTask> selectTaskList(CrmWechatMpMessageTask query)
    {
        return taskMapper.selectTaskList(query);
    }

    @Override
    public CrmWechatMpMessageTask getTaskDetail(Long id)
    {
        CrmWechatMpMessageTask task = taskMapper.selectTaskById(id);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        task.setItems(taskMapper.selectItemsByTaskId(id));
        return task;
    }

    @Override
    public void processPendingTasks()
    {
        List<Long> ids = taskMapper.selectPendingTaskIds(20);
        for (Long id : ids)
        {
            batchWorker.processTask(id);
        }
    }

    private static final class AudienceResolveResult
    {
        private final List<String> openIds;
        private final int skipCount;

        private AudienceResolveResult(List<String> openIds, int skipCount)
        {
            this.openIds = openIds;
            this.skipCount = skipCount;
        }
    }

    private AudienceResolveResult resolveAudience(String tenantId, WechatMpMessageBatchRequest req)
    {
        List<String> openIds = new ArrayList<>();
        int skipCount = 0;
        if ("segment".equals(req.getAudienceType()))
        {
            if (req.getAudienceRef() == null)
            {
                throw new ServiceException("segment 受众需要 audienceRef");
            }
            Long segmentId = Long.parseLong(req.getAudienceRef());
            int page = 1;
            int pageSize = 500;
            while (true)
            {
                List<SegmentMemberVo> members = segmentService.selectMembers(
                        tenantId, segmentId, page, pageSize);
                if (members == null || members.isEmpty())
                {
                    break;
                }
                for (SegmentMemberVo m : members)
                {
                    String openId = findOpenId(m.getContactId());
                    if (openId != null)
                    {
                        openIds.add(openId);
                    }
                }
                if (members.size() < pageSize)
                {
                    break;
                }
                page++;
            }
        }
        else if ("event_contacts".equals(req.getAudienceType()))
        {
            if (req.getAudienceRef() == null)
            {
                throw new ServiceException("event_contacts 受众需要 audienceRef（eventId）");
            }
            EventAudienceOpenIdResultVo resolved = eventAudienceService.resolveMpOpenIds(
                    tenantId, req.getAudienceRef());
            openIds.addAll(resolved.getOpenIds());
            skipCount = resolved.getSkipCount();
        }
        else if ("openid_list".equals(req.getAudienceType()) && req.getContentJson() != null)
        {
            com.alibaba.fastjson2.JSONArray arr = req.getContentJson().getJSONArray("openIds");
            if (arr != null)
            {
                for (int i = 0; i < arr.size(); i++)
                {
                    openIds.add(arr.getString(i));
                }
            }
        }
        else if ("fan_filter".equals(req.getAudienceType()))
        {
            if (req.getFilterJson() == null)
            {
                throw new ServiceException("fan_filter 受众需要 filterJson");
            }
            FanAudienceCompileResult compiled = fanAudienceCompiler.compile(
                    req.getAppId(), req.getFilterJson(), FanAudienceCompileOptions.forSend());
            openIds.addAll(compiled.getOpenIds());
            skipCount = compiled.getSkipCount();
        }
        else
        {
            throw new ServiceException("不支持的 audienceType: " + req.getAudienceType());
        }
        return new AudienceResolveResult(openIds, skipCount);
    }

    private void insertTaskItems(Long taskId, List<String> openIds)
    {
        if (openIds == null || openIds.isEmpty())
        {
            return;
        }
        Set<String> distinct = new LinkedHashSet<>(openIds);
        List<CrmWechatMpMessageTaskItem> batch = new ArrayList<>(TASK_ITEM_BATCH_SIZE);
        for (String openId : distinct)
        {
            CrmWechatMpMessageTaskItem item = new CrmWechatMpMessageTaskItem();
            item.setTaskId(taskId);
            item.setOpenId(openId);
            item.setStatus("pending");
            batch.add(item);
            if (batch.size() >= TASK_ITEM_BATCH_SIZE)
            {
                taskMapper.insertTaskItemsBatch(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty())
        {
            taskMapper.insertTaskItemsBatch(batch);
        }
    }

    private String findOpenId(String contactId)
    {
        if (contactId == null)
        {
            return null;
        }
        List<CrmCustomerIdentity> identities = identityService.listIdentitiesByContact(contactId);
        for (CrmCustomerIdentity id : identities)
        {
            if ("wechat_mp_openid".equals(id.getIdentityType()))
            {
                return id.getIdentityValue();
            }
        }
        return null;
    }

    private void resolveBatchAppId(WechatMpMessageBatchRequest req)
    {
        if (StringUtils.isNotEmpty(req.getAppId()))
        {
            return;
        }
        if (!"event_contacts".equals(req.getAudienceType()) || StringUtils.isEmpty(req.getAudienceRef()))
        {
            return;
        }
        String resolved = eventWechatMpAppIdResolver.resolve(req.getAudienceRef());
        if (StringUtils.isEmpty(resolved))
        {
            throw new ServiceException("活动未配置默认公众号", HttpStatus.BAD_REQUEST);
        }
        req.setAppId(resolved);
    }
}
