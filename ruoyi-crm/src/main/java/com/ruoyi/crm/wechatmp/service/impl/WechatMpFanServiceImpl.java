package com.ruoyi.crm.wechatmp.service.impl;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.dto.WechatMpFanSyncRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpFanService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.manager.AsyncManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WechatMpFanServiceImpl implements IWechatMpFanService
{
    private final ConcurrentHashMap<String, Boolean> syncing = new ConcurrentHashMap<>();

    @Autowired
    private CrmWechatMpFanMapper fanMapper;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Override
    public List<CrmWechatMpFan> selectFanList(CrmWechatMpFan query)
    {
        return fanMapper.selectFanList(query);
    }

    @Override
    public void scheduleSync(WechatMpFanSyncRequest req)
    {
        if (req.getAppId() == null)
        {
            throw new ServiceException("appId 不能为空");
        }
        if (syncing.putIfAbsent(req.getAppId(), true) != null)
        {
            throw new ServiceException("粉丝同步任务进行中，请稍后再试");
        }
        final String syncAppId = req.getAppId();
        final String tenantKey = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isEmpty(tenantKey))
        {
            syncing.remove(syncAppId);
            throw new ServiceException("tenant 数据源未设置，无法提交粉丝同步任务");
        }
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(tenantKey);
                    runSyncAsync(syncAppId);
                }
                catch (Exception e)
                {
                    log.error("WeChat MP fan sync failed for appId={}, tenant={}", syncAppId, tenantKey, e);
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
    }

    public void runSyncAsync(String appId)
    {
        try
        {
            syncFans(appId);
        }
        finally
        {
            syncing.remove(appId);
        }
    }

    public void syncFans(String appId)
    {
        String token = tokenService.getAccessToken(appId);
        String nextOpenId = "";
        int total = 0;
        do
        {
            JSONObject page = tencentApiClient.getFollowers(token, nextOpenId.isEmpty() ? null : nextOpenId);
            if (page == null || (page.containsKey("errcode") && page.getIntValue("errcode") != 0))
            {
                break;
            }
            total = page.getIntValue("total");
            nextOpenId = page.getString("next_openid");
            JSONObject data = page.getJSONObject("data");
            if (data == null)
            {
                break;
            }
            JSONArray openids = data.getJSONArray("openid");
            if (openids == null || openids.isEmpty())
            {
                break;
            }
            batchUpsertFans(appId, token, openids);
        }
        while (nextOpenId != null && !nextOpenId.isEmpty() && total > 0);
    }

    private void batchUpsertFans(String appId, String token, JSONArray openids)
    {
        JSONObject body = new JSONObject();
        JSONArray userList = new JSONArray();
        int limit = Math.min(openids.size(), 100);
        for (int i = 0; i < limit; i++)
        {
            JSONObject u = new JSONObject();
            u.put("openid", openids.getString(i));
            u.put("lang", "zh_CN");
            userList.add(u);
        }
        body.put("user_list", userList);
        JSONObject res = tencentApiClient.batchGetUserInfo(token, body);
        if (res == null)
        {
            return;
        }
        JSONArray infos = res.getJSONArray("user_info_list");
        if (infos == null)
        {
            return;
        }
        Date now = DateUtils.getNowDate();
        for (int i = 0; i < infos.size(); i++)
        {
            JSONObject info = infos.getJSONObject(i);
            CrmWechatMpFan fan = new CrmWechatMpFan();
            fan.setAppId(appId);
            fan.setOpenId(info.getString("openid"));
            fan.setUnionId(info.getString("unionid"));
            fan.setNickname(info.getString("nickname"));
            fan.setAvatarUrl(info.getString("headimgurl"));
            fan.setSubscribe(info.getIntValue("subscribe") == 1);
            if (info.getLong("subscribe_time") != null)
            {
                fan.setSubscribeTime(new Date(info.getLongValue("subscribe_time") * 1000L));
            }
            fan.setSex(info.getInteger("sex"));
            fan.setCountry(info.getString("country"));
            fan.setProvince(info.getString("province"));
            fan.setCity(info.getString("city"));
            fan.setRemark(info.getString("remark"));
            fan.setSubscribeScene(info.getString("subscribe_scene"));
            JSONArray tagidList = info.getJSONArray("tagid_list");
            if (tagidList != null && !tagidList.isEmpty())
            {
                fan.setTagIds(tagidList.toJSONString());
            }
            String contactId = identityService.findContactByIdentity("wechat_mp_openid", fan.getOpenId());
            fan.setContactId(contactId);
            fan.setLastSyncAt(now);
            fanMapper.upsert(fan);
        }
    }
}
