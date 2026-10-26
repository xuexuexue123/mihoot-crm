package com.ruoyi.crm.wechatmp.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService;
import com.ruoyi.crm.service.support.SurveyOutreachLogService;
import com.ruoyi.crm.service.support.SurveyOutreachUrlService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wechatmp.WechatMpMessageNewsBuilder;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessageTask;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessageTaskItem;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageTemplateRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMessageTaskMapper;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

@Component
public class WechatMpMessageBatchWorker
{
    private static final int BATCH_SIZE = 20;

    @Autowired
    private CrmWechatMpMessageTaskMapper taskMapper;

    @Autowired
    private CrmWechatMpFanMapper fanMapper;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    @Autowired
    private SurveyOutreachUrlService surveyOutreachUrlService;

    @Autowired
    private IWechatMpMessageService messageService;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    public void processTask(Long taskId)
    {
        CrmWechatMpMessageTask task = taskMapper.selectTaskById(taskId);
        if (task == null || "done".equals(task.getStatus()) || "failed".equals(task.getStatus()))
        {
            return;
        }
        if (task.getStartedAt() == null)
        {
            task.setStartedAt(DateUtils.getNowDate());
            task.setStatus("running");
            task.setUpdateTime(DateUtils.getNowDate());
            taskMapper.updateTask(task);
        }
        List<CrmWechatMpMessageTaskItem> pending = taskMapper.selectPendingItems(taskId, BATCH_SIZE);
        if (pending.isEmpty())
        {
            task.setStatus("done");
            task.setFinishedAt(DateUtils.getNowDate());
            task.setUpdateTime(DateUtils.getNowDate());
            taskMapper.updateTask(task);
            return;
        }
        int success = task.getSuccessCount() != null ? task.getSuccessCount() : 0;
        int fail = task.getFailCount() != null ? task.getFailCount() : 0;
        int skip = task.getSkipCount() != null ? task.getSkipCount() : 0;
        for (CrmWechatMpMessageTaskItem item : pending)
        {
            if (!isReachableFan(task.getAppId(), item.getOpenId()))
            {
                item.setStatus("skipped");
                item.setErrmsg("未关注或不可触达");
                skip++;
                taskMapper.updateTaskItem(item);
                continue;
            }
            try
            {
                sendOne(task, item);
                item.setStatus("sent");
                item.setSentAt(new Date());
                success++;
            }
            catch (Exception e)
            {
                item.setStatus("failed");
                item.setErrmsg(e.getMessage());
                fail++;
            }
            taskMapper.updateTaskItem(item);
            sleepQuietly(100);
        }
        task.setSuccessCount(success);
        task.setFailCount(fail);
        task.setSkipCount(skip);
        task.setUpdateTime(DateUtils.getNowDate());
        taskMapper.updateTask(task);
        if (!taskMapper.selectPendingItems(taskId, 1).isEmpty())
        {
            processTask(taskId);
        }
        else
        {
            task.setStatus("done");
            task.setFinishedAt(DateUtils.getNowDate());
            taskMapper.updateTask(task);
        }
    }

    private void sendOne(CrmWechatMpMessageTask task, CrmWechatMpMessageTaskItem item)
    {
        if ("template".equals(task.getTaskType()))
        {
            WechatMpMessageTemplateRequest req = new WechatMpMessageTemplateRequest();
            req.setAppId(task.getAppId());
            req.setOpenId(item.getOpenId());
            req.setTemplateId(task.getTemplateId());
            JSONObject content = JSONObject.parseObject(task.getContentJson());
            if (content != null)
            {
                req.setData(content.getJSONObject("data"));
                String eventId = content.getString("eventId");
                String surveyId = content.getString("surveyId");
                Long presetId = content.getLong("presetId");
                String sharedUrl = content.getString("url");
                if (StringUtils.hasText(surveyId) && !StringUtils.hasText(sharedUrl))
                {
                    req.setUrl(resolveSurveyUrl(content, surveyId, item.getOpenId(), task.getAppId()));
                    req.setSurveyId(surveyId);
                    req.setOutreachChannel(SurveyOutreachLogService.CHANNEL_MP_BATCH);
                }
                else if (StringUtils.hasText(eventId) && !StringUtils.hasText(sharedUrl))
                {
                    req.setUrl(resolvePersonalizedRegistrationUrl(content, eventId, item.getOpenId(), task.getAppId()));
                    req.setEventId(eventId);
                    req.setPresetId(presetId);
                }
                else
                {
                    req.setUrl(sharedUrl);
                    if (StringUtils.hasText(surveyId))
                    {
                        req.setSurveyId(surveyId);
                        req.setOutreachChannel(SurveyOutreachLogService.CHANNEL_MP_BATCH);
                    }
                }
            }
            messageService.sendTemplate(req);
            return;
        }
        if ("news".equals(task.getTaskType()))
        {
            JSONObject contentObj = JSONObject.parseObject(task.getContentJson());
            JSONArray articles = contentObj != null ? contentObj.getJSONArray("articles") : null;
            if (articles == null || articles.isEmpty())
            {
                throw new ServiceException("图文消息 articles 不能为空", HttpStatus.BAD_REQUEST);
            }
            WechatMpMessageNewsBuilder.validateArticlesFromJsonArray(articles);
            String token = tokenService.getAccessToken(task.getAppId());
            JSONObject body = WechatMpMessageNewsBuilder.buildCustomMessageBody(item.getOpenId(), articles);
            JSONObject res = tencentApiClient.sendCustomMessage(token, body);
            if (res != null && res.containsKey("errcode") && res.getIntValue("errcode") != 0)
            {
                throw new ServiceException("客服图文消息发送失败: " + res.getString("errmsg"));
            }
            return;
        }
        JSONObject content = JSONObject.parseObject(task.getContentJson());
        String text = content != null ? content.getString("text") : "";
        com.ruoyi.crm.wechatmp.dto.WechatMpMessageReplyRequest req =
                new com.ruoyi.crm.wechatmp.dto.WechatMpMessageReplyRequest();
        req.setAppId(task.getAppId());
        req.setOpenId(item.getOpenId());
        req.setMsgType("text");
        req.setContent(text);
        messageService.reply(req);
    }

    private String resolvePersonalizedRegistrationUrl(JSONObject content, String eventId,
            String openId, String appId)
    {
        if (!outreachRegistrationTokenService.isSigningConfigured())
        {
            return "";
        }
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            return "";
        }
        String tenantId = content.getString("tenantId");
        if (!StringUtils.hasText(tenantId))
        {
            tenantId = TenantDataSourceSupport.currentTenantKey();
        }
        if (!StringUtils.hasText(tenantId))
        {
            return "";
        }
        long exp = outreachRegistrationTokenService.computeExp(event);
        return outreachRegistrationTokenService.buildRegistrationUrl(
                tenantId, eventId, exp, openId, appId);
    }

    private String resolveSurveyUrl(JSONObject content, String surveyId, String openId, String appId)
    {
        String tenantId = content.getString("tenantId");
        if (!StringUtils.hasText(tenantId))
        {
            tenantId = TenantDataSourceSupport.currentTenantKey();
        }
        if (!StringUtils.hasText(tenantId))
        {
            return "";
        }
        return surveyOutreachUrlService.issueUrlForPreset(
                tenantId, surveyId, openId, appId, content.getString("contactId")).getSurveyUrl();
    }

    private boolean isReachableFan(String appId, String openId)
    {
        CrmWechatMpFan fan = fanMapper.selectByOpenId(appId, openId);
        return fan != null && Boolean.TRUE.equals(fan.getSubscribe());
    }

    private void sleepQuietly(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
