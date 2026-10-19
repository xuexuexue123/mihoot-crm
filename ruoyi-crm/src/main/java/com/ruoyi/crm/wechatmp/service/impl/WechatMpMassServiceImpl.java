package com.ruoyi.crm.wechatmp.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMassTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassPreviewRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassQuotaVo;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassSendRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMassTaskMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpMassService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WechatMpMassServiceImpl implements IWechatMpMassService
{
    public static final String STATUS_SUBMITTED = "submitted";
    public static final String STATUS_SENDING = "sending";
    public static final String STATUS_FINISHED = "finished";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_DELETED = "deleted";

    public static final String MSG_TYPE_TEXT = "text";
    public static final String MSG_TYPE_MPNEWS = "mpnews";
    public static final String AUDIENCE_ALL = "all";
    public static final String AUDIENCE_TAG = "tag";

    private static final int ERR_NO_QUOTA = 45028;
    private static final int ERR_UNAUTHORIZED = 48001;
    private static final int LIMIT_SERVICE = 4;
    private static final int LIMIT_SUBSCRIPTION = 1;

    @Autowired
    private CrmWechatMpMassTaskMapper massTaskMapper;

    @Autowired
    private CrmWechatMpConfigMapper configMapper;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Override
    public JSONObject preview(WechatMpMassPreviewRequest req)
    {
        if (req == null)
        {
            throw invalidParam("request body required");
        }
        requireConfig(req.getAppId());
        validateMsgContent(req.getMsgType(), req.getContent());
        if (!StringUtils.hasText(req.getTouser()) && !StringUtils.hasText(req.getTowxname()))
        {
            throw invalidParam("touser or towxname required");
        }

        JSONObject body = buildMessageBody(req.getMsgType(), req.getContent());
        if (StringUtils.hasText(req.getTouser()))
        {
            body.put("touser", req.getTouser());
        }
        if (StringUtils.hasText(req.getTowxname()))
        {
            body.put("towxname", req.getTowxname());
        }

        String token = tokenService.getAccessToken(req.getAppId());
        JSONObject res = tencentApiClient.massPreview(token, body);
        int err = res.getIntValue("errcode");
        if (err != 0)
        {
            throw massWxError("MP_MASS_PREVIEW_FAILED", err, res.getString("errmsg"));
        }
        JSONObject out = new JSONObject();
        out.put("msgId", String.valueOf(res.get("msg_id")));
        return out;
    }

    @Override
    public CrmWechatMpMassTask send(WechatMpMassSendRequest req)
    {
        if (req == null)
        {
            throw invalidParam("request body required");
        }
        requireConfig(req.getAppId());
        validateMsgContent(req.getMsgType(), req.getContent());
        normalizeAudience(req);

        if (StringUtils.hasText(req.getClientMsgId()))
        {
            if (req.getClientMsgId().getBytes(StandardCharsets.UTF_8).length > 32)
            {
                throw invalidParam("clientMsgId max 32 bytes");
            }
            CrmWechatMpMassTask existing = massTaskMapper.selectByClientMsgId(req.getAppId(), req.getClientMsgId());
            if (existing != null)
            {
                return existing;
            }
        }

        Date now = DateUtils.getNowDate();
        CrmWechatMpMassTask task = new CrmWechatMpMassTask();
        task.setAppId(req.getAppId());
        task.setMsgType(req.getMsgType());
        task.setContentJson(req.getContent() != null ? req.getContent().toJSONString() : "{}");
        task.setAudienceType(req.getAudienceType());
        task.setTagId(req.getTagId());
        task.setIsToAll(Boolean.TRUE.equals(req.getIsToAll()));
        task.setClientMsgId(StringUtils.hasText(req.getClientMsgId()) ? req.getClientMsgId() : null);
        task.setStatus(STATUS_SUBMITTED);
        task.setCreateBy(safeUsername());
        task.setCreateTime(now);
        task.setUpdateTime(now);
        massTaskMapper.insert(task);

        JSONObject body = buildSendAllBody(req);
        try
        {
            String token = tokenService.getAccessToken(req.getAppId());
            JSONObject res = tencentApiClient.massSendAll(token, body);
            int err = res.getIntValue("errcode");
            if (err != 0)
            {
                task.setStatus(STATUS_FAILED);
                task.setErrorCode(err);
                task.setErrorMsg(trimErr(res.getString("errmsg")));
                task.setFinishTime(DateUtils.getNowDate());
                task.setUpdateTime(task.getFinishTime());
                massTaskMapper.update(task);
                throw massWxError(mapSendErrorPrefix(err), err, res.getString("errmsg"));
            }
            task.setMsgId(stringifyId(res.get("msg_id")));
            Object dataId = res.get("msg_data_id");
            if (dataId != null)
            {
                task.setMsgDataId(String.valueOf(dataId));
            }
            task.setStatus(STATUS_SENDING);
            task.setUpdateTime(DateUtils.getNowDate());
            massTaskMapper.update(task);
            return task;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            task.setStatus(STATUS_FAILED);
            task.setErrorMsg(trimErr(e.getMessage()));
            task.setFinishTime(DateUtils.getNowDate());
            task.setUpdateTime(task.getFinishTime());
            massTaskMapper.update(task);
            throw new ServiceException("MP_MASS_SEND_FAILED: " + e.getMessage());
        }
    }

    @Override
    public List<CrmWechatMpMassTask> selectTaskList(CrmWechatMpMassTask query)
    {
        if (query == null || !StringUtils.hasText(query.getAppId()))
        {
            throw invalidParam("appId required");
        }
        return massTaskMapper.selectList(query);
    }

    @Override
    public CrmWechatMpMassTask getTask(Long id)
    {
        CrmWechatMpMassTask task = requireTask(id);
        return task;
    }

    @Override
    public CrmWechatMpMassTask refreshTask(Long id)
    {
        CrmWechatMpMassTask task = requireTask(id);
        if (!StringUtils.hasText(task.getMsgId()))
        {
            throw invalidParam("task has no msgId");
        }
        try
        {
            String token = tokenService.getAccessToken(task.getAppId());
            JSONObject res = tencentApiClient.massGet(token, task.getMsgId());
            int err = res.getIntValue("errcode");
            if (err != 0)
            {
                throw massWxError("MP_MASS_REFRESH_FAILED", err, res.getString("errmsg"));
            }
            String msgStatus = res.getString("msg_status");
            applyWxMsgStatus(task, msgStatus);
            task.setUpdateTime(DateUtils.getNowDate());
            massTaskMapper.update(task);
            return task;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("MP_MASS_REFRESH_FAILED: " + e.getMessage());
        }
    }

    @Override
    public CrmWechatMpMassTask deleteTask(Long id, Integer articleIdx)
    {
        CrmWechatMpMassTask task = requireTask(id);
        if (!StringUtils.hasText(task.getMsgId()))
        {
            throw invalidParam("task has no msgId");
        }
        if (STATUS_DELETED.equals(task.getStatus()))
        {
            return task;
        }
        try
        {
            String token = tokenService.getAccessToken(task.getAppId());
            JSONObject res = tencentApiClient.massDelete(token, task.getMsgId(), articleIdx != null ? articleIdx : 0);
            int err = res.getIntValue("errcode");
            if (err != 0)
            {
                throw massWxError("MP_MASS_DELETE_FAILED", err, res.getString("errmsg"));
            }
            task.setStatus(STATUS_DELETED);
            task.setUpdateTime(DateUtils.getNowDate());
            massTaskMapper.update(task);
            return task;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("MP_MASS_DELETE_FAILED: " + e.getMessage());
        }
    }

    @Override
    public WechatMpMassQuotaVo getQuota(String appId)
    {
        CrmWechatMpConfig cfg = requireConfig(appId);
        WechatMpMassQuotaVo vo = new WechatMpMassQuotaVo();
        vo.setAppId(appId);
        vo.setAccountType(cfg.getAccountType());
        vo.setNote("估值为 CRM 本地 is_to_all=true 成功提交次数；不含仅在公众平台后台操作的次数。");

        String accountType = cfg.getAccountType();
        if ("service".equals(accountType))
        {
            vo.setPeriod("month");
            vo.setIsToAllLimitEstimate(LIMIT_SERVICE);
            vo.setQuotaHint("服务号每月约 4 次全体群发（与公众平台后台共用）；单用户每月最多接收 4 条。");
        }
        else if ("subscription".equals(accountType))
        {
            vo.setPeriod("day");
            vo.setIsToAllLimitEstimate(LIMIT_SUBSCRIPTION);
            vo.setQuotaHint("订阅号每天约 1 次全体群发（与公众平台后台共用）。");
        }
        else
        {
            vo.setPeriod(null);
            vo.setIsToAllLimitEstimate(null);
            vo.setIsToAllUsedEstimate(null);
            vo.setRemainEstimate(null);
            vo.setQuotaHint("账号类型未知，无法估算配额；真实耗尽以提交时 45028 为准。");
            return vo;
        }

        Date[] period = currentPeriodBounds(vo.getPeriod());
        int used = massTaskMapper.countIsToAllInPeriod(appId, period[0], period[1]);
        vo.setIsToAllUsedEstimate(used);
        vo.setRemainEstimate(Math.max(0, vo.getIsToAllLimitEstimate() - used));
        return vo;
    }

    @Override
    public void applyMassSendJobFinish(String appId, String msgId, String wxStatus,
            Integer totalCount, Integer filterCount, Integer sentCount, Integer errorCount)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(msgId))
        {
            log.warn("MASSSENDJOBFINISH missing appId or msgId: appId={}, msgId={}", appId, msgId);
            return;
        }
        CrmWechatMpMassTask task = massTaskMapper.selectByMsgId(appId, msgId);
        if (task == null)
        {
            log.warn("MASSSENDJOBFINISH no local task: appId={}, msgId={}", appId, msgId);
            return;
        }
        if (STATUS_DELETED.equals(task.getStatus()))
        {
            // keep deleted; still refresh counts for audit
        }
        task.setTotalCount(totalCount);
        task.setFilterCount(filterCount);
        task.setSentCount(sentCount);
        task.setErrorCount(errorCount);
        if (!STATUS_DELETED.equals(task.getStatus()))
        {
            if (isWxMassFailed(wxStatus))
            {
                task.setStatus(STATUS_FAILED);
                task.setErrorMsg(trimErr(wxStatus));
            }
            else
            {
                task.setStatus(STATUS_FINISHED);
            }
            if (task.getFinishTime() == null)
            {
                task.setFinishTime(DateUtils.getNowDate());
            }
        }
        task.setUpdateTime(DateUtils.getNowDate());
        massTaskMapper.update(task);
        log.info("MASSSENDJOBFINISH updated: appId={}, msgId={}, status={}, sent={}",
                appId, msgId, task.getStatus(), sentCount);
    }

    // ======================== helpers ========================

    CrmWechatMpConfig requireConfig(String appId)
    {
        if (!StringUtils.hasText(appId))
        {
            throw invalidParam("appId required");
        }
        CrmWechatMpConfig cfg = configMapper.selectByAppId(appId);
        if (cfg == null)
        {
            throw new ServiceException("MP_MASS_INVALID_PARAM: appId not configured");
        }
        return cfg;
    }

    void validateMsgContent(String msgType, JSONObject content)
    {
        if (!MSG_TYPE_TEXT.equals(msgType) && !MSG_TYPE_MPNEWS.equals(msgType))
        {
            throw invalidParam("msgType must be text or mpnews");
        }
        if (content == null)
        {
            throw invalidParam("content required");
        }
        if (MSG_TYPE_TEXT.equals(msgType))
        {
            if (!StringUtils.hasText(content.getString("text")))
            {
                throw invalidParam("content.text required for text");
            }
        }
        else if (!StringUtils.hasText(content.getString("mediaId")))
        {
            throw invalidParam("content.mediaId required for mpnews");
        }
    }

    void normalizeAudience(WechatMpMassSendRequest req)
    {
        if (!AUDIENCE_ALL.equals(req.getAudienceType()) && !AUDIENCE_TAG.equals(req.getAudienceType()))
        {
            throw invalidParam("audienceType must be all or tag");
        }
        if (AUDIENCE_TAG.equals(req.getAudienceType()))
        {
            if (req.getTagId() == null || req.getTagId() <= 0)
            {
                throw invalidParam("tagId required for audienceType=tag");
            }
            if (Boolean.TRUE.equals(req.getIsToAll()))
            {
                throw invalidParam("isToAll=true only allowed with audienceType=all");
            }
            req.setIsToAll(false);
        }
        else
        {
            if (req.getIsToAll() == null)
            {
                req.setIsToAll(true);
            }
        }
    }

    JSONObject buildMessageBody(String msgType, JSONObject content)
    {
        JSONObject body = new JSONObject();
        body.put("msgtype", msgType);
        if (MSG_TYPE_TEXT.equals(msgType))
        {
            JSONObject text = new JSONObject();
            text.put("content", content.getString("text"));
            body.put("text", text);
        }
        else
        {
            JSONObject mpnews = new JSONObject();
            mpnews.put("media_id", content.getString("mediaId"));
            body.put("mpnews", mpnews);
        }
        return body;
    }

    JSONObject buildSendAllBody(WechatMpMassSendRequest req)
    {
        JSONObject body = buildMessageBody(req.getMsgType(), req.getContent());
        JSONObject filter = new JSONObject();
        boolean isToAll = Boolean.TRUE.equals(req.getIsToAll());
        filter.put("is_to_all", isToAll);
        if (!isToAll && req.getTagId() != null)
        {
            filter.put("tag_id", req.getTagId());
        }
        body.put("filter", filter);
        if (StringUtils.hasText(req.getClientMsgId()))
        {
            body.put("clientmsgid", req.getClientMsgId());
        }
        if (MSG_TYPE_MPNEWS.equals(req.getMsgType()))
        {
            body.put("send_ignore_reprint", req.getSendIgnoreReprint() != null ? req.getSendIgnoreReprint() : 0);
        }
        return body;
    }

    CrmWechatMpMassTask requireTask(Long id)
    {
        if (id == null)
        {
            throw invalidParam("id required");
        }
        CrmWechatMpMassTask task = massTaskMapper.selectById(id);
        if (task == null)
        {
            throw new ServiceException("MP_MASS_INVALID_PARAM: task not found");
        }
        return task;
    }

    void applyWxMsgStatus(CrmWechatMpMassTask task, String msgStatus)
    {
        if (!StringUtils.hasText(msgStatus))
        {
            return;
        }
        if (STATUS_DELETED.equals(task.getStatus()))
        {
            return;
        }
        String s = msgStatus.toUpperCase();
        if (s.contains("SEND_SUCCESS") || "SUCCESS".equals(s) || s.contains("SEND SUCCESS"))
        {
            task.setStatus(STATUS_FINISHED);
            if (task.getFinishTime() == null)
            {
                task.setFinishTime(DateUtils.getNowDate());
            }
        }
        else if (s.contains("SENDING") || s.contains("WAIT"))
        {
            task.setStatus(STATUS_SENDING);
        }
        else if (isWxMassFailed(msgStatus))
        {
            task.setStatus(STATUS_FAILED);
            task.setErrorMsg(trimErr(msgStatus));
            if (task.getFinishTime() == null)
            {
                task.setFinishTime(DateUtils.getNowDate());
            }
        }
    }

    static boolean isWxMassFailed(String wxStatus)
    {
        if (!StringUtils.hasText(wxStatus))
        {
            return false;
        }
        String s = wxStatus.toUpperCase();
        return s.contains("FAIL") || s.contains("ERR") || s.contains("REJECT");
    }

    static Date[] currentPeriodBounds(String period)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start;
        Date end;
        if ("month".equals(period))
        {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            start = cal.getTime();
            cal.add(Calendar.MONTH, 1);
            end = cal.getTime();
        }
        else
        {
            start = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            end = cal.getTime();
        }
        return new Date[] { start, end };
    }

    static String mapSendErrorPrefix(int errcode)
    {
        if (errcode == ERR_NO_QUOTA)
        {
            return "MP_MASS_NO_QUOTA";
        }
        if (errcode == ERR_UNAUTHORIZED)
        {
            return "MP_MASS_UNAUTHORIZED";
        }
        return "MP_MASS_SEND_FAILED";
    }

    static ServiceException invalidParam(String detail)
    {
        return new ServiceException("MP_MASS_INVALID_PARAM: " + detail);
    }

    static ServiceException massWxError(String prefix, int errcode, String errmsg)
    {
        return new ServiceException(prefix + ": " + errcode + " " + (errmsg != null ? errmsg : ""));
    }

    static String stringifyId(Object id)
    {
        return id == null ? null : String.valueOf(id);
    }

    static String trimErr(String msg)
    {
        if (msg == null)
        {
            return null;
        }
        return msg.length() > 512 ? msg.substring(0, 512) : msg;
    }

    static String safeUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return "system";
        }
    }

}
