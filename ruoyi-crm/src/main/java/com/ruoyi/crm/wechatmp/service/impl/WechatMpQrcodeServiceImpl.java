package com.ruoyi.crm.wechatmp.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpQrcode;
import com.ruoyi.crm.wechatmp.dto.WechatMpQrcodeCreateRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpQrcodeMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpQrcodeService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

@Service
public class WechatMpQrcodeServiceImpl implements IWechatMpQrcodeService
{
    @Autowired
    private CrmWechatMpQrcodeMapper qrcodeMapper;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Override
    public List<CrmWechatMpQrcode> selectList(CrmWechatMpQrcode query)
    {
        return qrcodeMapper.selectList(query);
    }

    @Override
    public CrmWechatMpQrcode getById(Long id)
    {
        CrmWechatMpQrcode row = qrcodeMapper.selectById(id);
        if (row == null)
        {
            throw new ServiceException("推广码不存在");
        }
        return row;
    }

    @Override
    public CrmWechatMpQrcode create(WechatMpQrcodeCreateRequest req)
    {
        if (req.getAppId() == null || req.getSceneStr() == null || req.getName() == null)
        {
            throw new ServiceException("appId、name、sceneStr 不能为空");
        }
        validateEventWechatMpAppId(req.getCrmEventId(), req.getAppId());
        CrmWechatMpQrcode existing = qrcodeMapper.selectByScene(req.getAppId(), req.getSceneStr());
        if (existing != null)
        {
            existing.setName(req.getName());
            if (req.getCrmEventId() != null)
            {
                existing.setCrmEventId(req.getCrmEventId());
            }
            if (req.getCampaignId() != null)
            {
                existing.setCampaignId(req.getCampaignId());
            }
            existing.setUpdateTime(DateUtils.getNowDate());
            qrcodeMapper.update(existing);
            return existing;
        }
        String qrType = req.getQrType() != null ? req.getQrType() : "permanent";
        JSONObject body = new JSONObject();
        if ("temporary".equals(qrType))
        {
            body.put("expire_seconds", req.getExpireSeconds() != null ? req.getExpireSeconds() : 604800);
            body.put("action_name", "QR_SCENE");
            JSONObject scene = new JSONObject();
            scene.put("scene_id", 0);
            scene.put("scene_str", req.getSceneStr());
            body.put("action_info", new JSONObject().fluentPut("scene", scene));
        }
        else
        {
            body.put("action_name", "QR_LIMIT_STR_SCENE");
            JSONObject scene = new JSONObject();
            scene.put("scene_str", req.getSceneStr());
            body.put("action_info", new JSONObject().fluentPut("scene", scene));
        }
        JSONObject res = callCreateQrCode(req.getAppId(), body, false);
        if (res == null || (res.containsKey("errcode") && res.getIntValue("errcode") != 0))
        {
            throw new ServiceException("qrcode/create 失败: " + (res != null ? res.getString("errmsg") : "null"));
        }
        String ticket = res.getString("ticket");
        CrmWechatMpQrcode row = new CrmWechatMpQrcode();
        row.setAppId(req.getAppId());
        row.setName(req.getName());
        row.setSceneStr(req.getSceneStr());
        row.setQrType(qrType);
        row.setExpireSeconds(req.getExpireSeconds());
        row.setTicket(ticket);
        row.setQrUrl(buildQrUrl(ticket));
        row.setCrmEventId(req.getCrmEventId());
        row.setCampaignId(req.getCampaignId());
        row.setStatsJson("{}");
        row.setCreateBy(SecurityUtils.getUsername());
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateTime(DateUtils.getNowDate());
        qrcodeMapper.insert(row);
        return row;
    }

    @Override
    public void update(Long id, CrmWechatMpQrcode patch)
    {
        CrmWechatMpQrcode existing = getById(id);
        if (patch.getName() != null)
        {
            existing.setName(patch.getName());
        }
        if (patch.getCrmEventId() != null)
        {
            validateEventWechatMpAppId(patch.getCrmEventId(), existing.getAppId());
            existing.setCrmEventId(patch.getCrmEventId());
        }
        if (patch.getCampaignId() != null)
        {
            existing.setCampaignId(patch.getCampaignId());
        }
        existing.setUpdateTime(DateUtils.getNowDate());
        qrcodeMapper.update(existing);
    }

    @Override
    public void delete(Long id)
    {
        qrcodeMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> getStats(Long id)
    {
        CrmWechatMpQrcode qr = getById(id);
        String sceneStr = qr.getSceneStr();
        String qrCodeId = String.valueOf(qr.getId());
        int subscribeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM crm_activity WHERE channel = 'wechat_mp' " +
                        "AND event_type = 'channel.wechat_mp.follow' " +
                        "AND (context::jsonb ->> 'scene_str' = ? OR payload::jsonb ->> 'event_key' = ?)",
                Integer.class, sceneStr, "qrscene_" + sceneStr);
        int scanCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM crm_activity WHERE channel = 'wechat_mp' " +
                        "AND event_type = 'channel.wechat_mp.scan' " +
                        "AND (context::jsonb ->> 'qr_code_id' = ? " +
                        "OR context::jsonb ->> 'scene_str' = ? " +
                        "OR payload::jsonb ->> 'event_key' = ?)",
                Integer.class, qrCodeId, sceneStr, sceneStr);
        Map<String, Object> stats = new HashMap<>();
        stats.put("scanCount", scanCount);
        stats.put("subscribeCount", subscribeCount);
        stats.put("recentSubscribes", new ArrayList<>());
        return stats;
    }

    @Override
    public CrmWechatMpQrcode findByScene(String appId, String sceneStr)
    {
        return qrcodeMapper.selectByScene(appId, sceneStr);
    }

    private void validateEventWechatMpAppId(String crmEventId, String appId)
    {
        if (StringUtils.isBlank(crmEventId) || StringUtils.isBlank(appId))
        {
            return;
        }
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(crmEventId);
        if (event == null || StringUtils.isBlank(event.getWechatMpAppId()))
        {
            return;
        }
        if (!event.getWechatMpAppId().equals(appId))
        {
            throw new ServiceException("wechat_mp_app_id_mismatch", HttpStatus.BAD_REQUEST);
        }
    }

    private JSONObject callCreateQrCode(String appId, JSONObject body, boolean retried)
    {
        String token = retried ? tokenService.refreshAccessToken(appId) : tokenService.getAccessToken(appId);
        JSONObject res = tencentApiClient.createQrCode(token, body);
        if (!retried && tencentApiClient.isTokenInvalid(res))
        {
            return callCreateQrCode(appId, body, true);
        }
        return res;
    }

    private String buildQrUrl(String ticket)
    {
        try
        {
            return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="
                    + URLEncoder.encode(ticket, StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e)
        {
            return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
        }
    }
}
