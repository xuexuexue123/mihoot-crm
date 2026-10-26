package com.ruoyi.crm.wechatmp.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.mapper.ActivityUnmatchedMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMenuKeyLabelMapper;
import com.ruoyi.crm.wechatmp.mapper.WechatMpStatsMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpStatsService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

@Service
public class WechatMpStatsServiceImpl implements IWechatMpStatsService
{
    @Autowired
    private WechatMpStatsMapper statsMapper;

    @Autowired
    private ActivityUnmatchedMapper unmatchedMapper;

    @Autowired
    private CrmWechatMpConfigMapper configMapper;

    @Autowired
    private CrmWechatMpMenuKeyLabelMapper menuKeyLabelMapper;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Override
    public Map<String, Object> getStats(String appId, Date beginTime, Date endTime)
    {
        if (appId == null || appId.isEmpty())
        {
            throw new ServiceException("appId 不能为空");
        }
        CrmWechatMpConfig cfg = configMapper.selectByAppId(appId);
        if (cfg == null)
        {
            throw new ServiceException("公众号配置不存在");
        }

        Date begin = beginTime;
        Date end = endTime;
        if (begin == null)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            begin = cal.getTime();
        }
        if (end == null)
        {
            end = new Date();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("appId", appId);
        data.put("connected", Boolean.TRUE.equals(cfg.getConnected()));
        data.put("accountType", cfg.getAccountType());
        data.put("followCount", statsMapper.countEvents(appId, "channel.wechat_mp.follow", begin, end));
        data.put("unfollowCount", statsMapper.countEvents(appId, "channel.wechat_mp.unfollow", begin, end));
        data.put("menuClickCount", statsMapper.countEvents(appId, "channel.wechat_mp.menu.click", begin, end));
        data.put("messageReceivedCount", statsMapper.countEvents(appId, "channel.wechat_mp.message.received", begin, end));
        data.put("unmatchedPendingCount", unmatchedMapper.countPending());

        List<Map<String, Object>> rawTop = statsMapper.selectTopMenuKeys(appId, begin, end, 10);
        List<Map<String, Object>> topMenuKeys = new ArrayList<>();
        for (Map<String, Object> row : rawTop)
        {
            Map<String, Object> item = new HashMap<>();
            String menuKey = row.get("menukey") != null ? row.get("menukey").toString()
                    : (row.get("menuKey") != null ? row.get("menuKey").toString() : "unknown");
            Object cnt = row.get("count");
            item.put("menuKey", menuKey);
            item.put("menuName", menuKeyLabelMapper.selectNameByKey(appId, menuKey));
            item.put("count", cnt);
            topMenuKeys.add(item);
        }
        data.put("topMenuKeys", topMenuKeys);
        return data;
    }

    @Override
    public void syncAccountInfo(String appId)
    {
        CrmWechatMpConfig cfg = configMapper.selectByAppId(appId);
        if (cfg == null)
        {
            throw new ServiceException("公众号配置不存在");
        }
        try
        {
            String token = tokenService.getAccessToken(appId);
            JSONObject info = tencentApiClient.getAccountBasicInfo(token);
            if (info != null && info.containsKey("errcode") && info.getIntValue("errcode") != 0)
            {
                if (tencentApiClient.isTokenInvalid(info))
                {
                    tokenService.invalidateCache(appId);
                }
                return;
            }
            if (info != null)
            {
                int accountType = info.getIntValue("account_type");
                cfg.setAccountType(accountType == 2 ? "service" : "subscription");
                cfg.setAccountName(info.getString("nickname"));
                cfg.setAccountSyncedAt(DateUtils.getNowDate());
                configMapper.update(cfg);
            }
        }
        catch (Exception e)
        {
            // non-fatal
        }
    }
}
