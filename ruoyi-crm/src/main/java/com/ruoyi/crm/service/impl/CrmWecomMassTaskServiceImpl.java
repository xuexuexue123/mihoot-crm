package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomMassTask;
import com.ruoyi.crm.domain.dto.wecom.WecomMassSendCreateRequest;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomMassTaskMapper;
import com.ruoyi.crm.service.ICrmWecomMassTaskService;
import com.ruoyi.crm.service.support.SurveyOutreachLogService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomTencentApiClient;

@Service
public class CrmWecomMassTaskServiceImpl implements ICrmWecomMassTaskService
{
    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private CrmWecomMassTaskMapper crmWecomMassTaskMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    @Autowired
    private SurveyOutreachLogService surveyOutreachLogService;

    @Override
    public JSONObject createMassSend(WecomMassSendCreateRequest req)
    {
        CrmWecomConfig cfg = crmWecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            throw new ServiceException("请先配置企业微信集成");
        }
        if (req.getExternalUserid() == null || req.getExternalUserid().isEmpty())
        {
            throw new ServiceException("externalUserid 不能为空");
        }
        String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
        JSONObject body = new JSONObject();
        String chatType = req.getChatType() != null ? req.getChatType() : "single";
        body.put("chat_type", chatType);
        body.put("external_userid", req.getExternalUserid());
        if (req.getSender() != null && !req.getSender().isEmpty())
        {
            body.put("sender", req.getSender().get(0));
        }
        if (req.getText() != null && !req.getText().isEmpty())
        {
            JSONObject text = new JSONObject();
            text.put("content", req.getText());
            body.put("text", text);
        }
        JSONObject res = wecomTencentApiClient.externalContactAddMsgTemplate(accessToken, body);
        int err = res.getIntValue("errcode");
        if (err != 0)
        {
            throw new ServiceException("add_msg_template 失败 errcode=" + err + " errmsg=" + res.getString("errmsg"));
        }
        CrmWecomMassTask row = new CrmWecomMassTask();
        row.setMsgId(res.getString("msgid"));
        row.setChatType(chatType);
        row.setExternalUseridsJson(JSON.toJSONString(req.getExternalUserid()));
        row.setSenderListJson(req.getSender() != null ? JSON.toJSONString(req.getSender()) : null);
        row.setContentJson(body.toJSONString());
        row.setStatus("created");
        if (req.getSurveyId() != null && !req.getSurveyId().isEmpty())
        {
            row.setCrmSurveyId(req.getSurveyId());
        }
        row.setCreateTime(DateUtils.getNowDate());
        row.setCreateBy(SecurityUtils.getUsername());
        crmWecomMassTaskMapper.insert(row);
        if (req.getSurveyId() != null && !req.getSurveyId().isEmpty())
        {
            for (String externalUserid : req.getExternalUserid())
            {
                surveyOutreachLogService.recordSent(
                        req.getSurveyId(),
                        SurveyOutreachLogService.CHANNEL_WECOM_MASS,
                        "external_userid",
                        externalUserid,
                        row.getId() != null ? String.valueOf(row.getId()) : null);
            }
        }
        return res;
    }

    @Override
    public List<CrmWecomMassTask> selectTaskList(CrmWecomMassTask query)
    {
        return crmWecomMassTaskMapper.selectList(query);
    }

    @Override
    public List<Long> selectPendingSyncTaskIds(int limit)
    {
        int cap = limit <= 0 ? 20 : Math.min(limit, 200);
        return crmWecomMassTaskMapper.selectIdsPendingSync(cap);
    }

    @Override
    public JSONObject syncSendResultForTask(Long id, String updateBy)
    {
        CrmWecomMassTask task = crmWecomMassTaskMapper.selectById(id);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        if (task.getMsgId() == null || task.getMsgId().isEmpty())
        {
            throw new ServiceException("任务缺少 msg_id，无法同步发送结果");
        }
        CrmWecomConfig cfg = crmWecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            throw new ServiceException("请先配置企业微信集成");
        }
        String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
        JSONObject req = new JSONObject();
        req.put("msgid", task.getMsgId());
        JSONObject res = wecomTencentApiClient.externalContactGetGroupMsgSendResult(accessToken, req);
        task.setSendResultJson(res.toJSONString());
        task.setStatus("synced");
        task.setUpdateTime(DateUtils.getNowDate());
        task.setUpdateBy(updateBy != null && !updateBy.isEmpty() ? updateBy : "system");
        crmWecomMassTaskMapper.update(task);
        JSONObject out = new JSONObject();
        out.put("task", task);
        out.put("wecom", res);
        return out;
    }

    @Override
    public JSONObject loadTaskDetailSync(Long id)
    {
        return syncSendResultForTask(id, SecurityUtils.getUsername());
    }
}
