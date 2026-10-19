package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.domain.CrmTagDispatchTask;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.vo.SegmentMemberVo;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmTagGovernanceMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmSegmentService;
import com.ruoyi.crm.service.support.CrmTagValidationSupport;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagBatchRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpTagService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomTencentApiClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 标签渠道下发后台 Worker
 */
@Slf4j
@Component
public class CrmTagDispatchWorker
{
    private static final int PAGE_SIZE = 500;
    private static final int MP_BATCH = 50;

    @Autowired
    private CrmTagGovernanceMapper governanceMapper;

    @Autowired
    private ICrmSegmentService segmentService;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private IWechatMpTagService wechatMpTagService;

    @Autowired
    private CrmContactMapper contactMapper;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomApiClient;

    public void processTask(String tenantId, Long taskId)
    {
        CrmTagDispatchTask task = governanceMapper.selectDispatchTaskById(taskId);
        if (task == null)
        {
            return;
        }
        task.setStatus("running");
        task.setUpdateTime(DateUtils.getNowDate());
        governanceMapper.updateDispatchTask(task);

        try
        {
            if (CrmTagValidationSupport.CHANNEL_WECHAT_MP.equals(task.getChannel()))
            {
                processWechatMp(tenantId, task);
            }
            else if (CrmTagValidationSupport.CHANNEL_WECOM.equals(task.getChannel()))
            {
                processWecom(tenantId, task);
            }
            else
            {
                fail(task, "不支持的 channel: " + task.getChannel());
            }
        }
        catch (Exception e)
        {
            log.error("dispatch worker error taskId={}", taskId, e);
            fail(task, e.getMessage());
        }
    }

    private void processWechatMp(String tenantId, CrmTagDispatchTask task)
    {
        Long tagId;
        try
        {
            tagId = Long.parseLong(task.getChannelTagId());
        }
        catch (NumberFormatException e)
        {
            fail(task, "公众号 channel_tag_id 非法");
            return;
        }
        List<String> openIds = new ArrayList<>();
        int page = 1;
        while (true)
        {
            List<SegmentMemberVo> members = segmentService.selectMembers(tenantId, task.getSegmentId(), page, PAGE_SIZE);
            if (members == null || members.isEmpty())
            {
                break;
            }
            for (SegmentMemberVo m : members)
            {
                String openId = findIdentity(m.getContactId(), "wechat_mp_openid");
                if (openId != null)
                {
                    openIds.add(openId);
                }
            }
            if (members.size() < PAGE_SIZE)
            {
                break;
            }
            page++;
        }
        task.setTotal(openIds.size());
        int succeeded = 0;
        int failed = 0;
        for (int i = 0; i < openIds.size(); i += MP_BATCH)
        {
            List<String> batch = openIds.subList(i, Math.min(i + MP_BATCH, openIds.size()));
            WechatMpTagBatchRequest req = new WechatMpTagBatchRequest();
            req.setOpenidList(new ArrayList<>(batch));
            req.setTagId(tagId);
            AjaxResult result = wechatMpTagService.batchTagging(task.getAppId(), req);
            Object code = result == null ? null : result.get(AjaxResult.CODE_TAG);
            boolean ok = code instanceof Number && ((Number) code).intValue() == HttpStatus.SUCCESS;
            if (ok)
            {
                succeeded += batch.size();
            }
            else
            {
                failed += batch.size();
            }
            task.setSucceeded(succeeded);
            task.setFailed(failed);
            task.setUpdateTime(DateUtils.getNowDate());
            governanceMapper.updateDispatchTask(task);
        }
        task.setStatus("done");
        task.setUpdateTime(DateUtils.getNowDate());
        governanceMapper.updateDispatchTask(task);
    }

    private void processWecom(String tenantId, CrmTagDispatchTask task)
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || StringUtils.isBlank(cfg.getCorpId()))
        {
            fail(task, "未配置企业微信");
            return;
        }
        String token = wecomAccessTokenService.getAgentAccessToken(cfg);
        List<WecomMarkTarget> targets = new ArrayList<>();
        int page = 1;
        while (true)
        {
            List<SegmentMemberVo> members = segmentService.selectMembers(tenantId, task.getSegmentId(), page, PAGE_SIZE);
            if (members == null || members.isEmpty())
            {
                break;
            }
            for (SegmentMemberVo m : members)
            {
                String externalUserid = findIdentity(m.getContactId(), "wecom_external_userid");
                if (externalUserid == null)
                {
                    CrmContact contact = contactMapper.selectCrmContactByContactId(m.getContactId());
                    if (contact != null)
                    {
                        externalUserid = contact.getWecomExternalUserid();
                    }
                }
                if (StringUtils.isBlank(externalUserid))
                {
                    continue;
                }
                String userid = null;
                CrmContact contact = contactMapper.selectCrmContactByContactId(m.getContactId());
                if (contact != null)
                {
                    userid = contact.getWecomFollowUserid();
                }
                if (StringUtils.isBlank(userid))
                {
                    continue;
                }
                targets.add(new WecomMarkTarget(userid, externalUserid));
            }
            if (members.size() < PAGE_SIZE)
            {
                break;
            }
            page++;
        }
        task.setTotal(targets.size());
        int succeeded = 0;
        int failed = 0;
        JSONArray addTag = new JSONArray();
        addTag.add(task.getChannelTagId());
        for (WecomMarkTarget target : targets)
        {
            JSONObject body = new JSONObject();
            body.put("userid", target.userid);
            body.put("external_userid", target.externalUserid);
            body.put("add_tag", addTag);
            body.put("remove_tag", new JSONArray());
            JSONObject res = wecomApiClient.markExternalContactTag(token, body);
            if (res != null && res.getIntValue("errcode") == 0)
            {
                succeeded++;
            }
            else
            {
                failed++;
            }
            if ((succeeded + failed) % 20 == 0)
            {
                task.setSucceeded(succeeded);
                task.setFailed(failed);
                task.setUpdateTime(DateUtils.getNowDate());
                governanceMapper.updateDispatchTask(task);
            }
        }
        task.setSucceeded(succeeded);
        task.setFailed(failed);
        task.setStatus("done");
        task.setUpdateTime(DateUtils.getNowDate());
        governanceMapper.updateDispatchTask(task);
    }

    private String findIdentity(String contactId, String type)
    {
        if (contactId == null)
        {
            return null;
        }
        List<CrmCustomerIdentity> identities = identityService.listIdentitiesByContact(contactId);
        for (CrmCustomerIdentity id : identities)
        {
            if (type.equals(id.getIdentityType()))
            {
                return id.getIdentityValue();
            }
        }
        return null;
    }

    private void fail(CrmTagDispatchTask task, String message)
    {
        task.setStatus("failed");
        task.setErrorMessage(StringUtils.substring(message, 0, 500));
        task.setUpdateTime(DateUtils.getNowDate());
        governanceMapper.updateDispatchTask(task);
    }

    private static final class WecomMarkTarget
    {
        private final String userid;
        private final String externalUserid;

        private WecomMarkTarget(String userid, String externalUserid)
        {
            this.userid = userid;
            this.externalUserid = externalUserid;
        }
    }
}
