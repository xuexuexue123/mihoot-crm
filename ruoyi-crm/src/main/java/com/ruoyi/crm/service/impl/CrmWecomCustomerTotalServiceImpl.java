package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomCustomerTotalResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomStaffCustomerItem;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmWecomCustomerTotalService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomTencentApiClient;

@Service
public class CrmWecomCustomerTotalServiceImpl implements ICrmWecomCustomerTotalService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomCustomerTotalServiceImpl.class);
    private static final int BATCH_SIZE = 20;
    private static final int BATCH_INTERVAL_MS = 1000;
    private static final int MAX_TOKEN_RETRY = 2;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    @Override
    public WecomCustomerTotalResponse pullCustomerTotal()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || !Boolean.TRUE.equals(cfg.getConnected()))
        {
            log.info("WeCom config not found or not connected, return empty customer total");
            return new WecomCustomerTotalResponse();
        }

        String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);

        // 1. 获取有客户联系权限的成员列表
        JSONObject followRes = wecomTencentApiClient.externalContactGetFollowUserList(accessToken);
        if (followRes == null || followRes.getIntValue("errcode") != 0)
        {
            String errmsg = followRes != null ? followRes.getString("errmsg") : "null response";
            throw new ServiceException("get_follow_user_list failed: " + errmsg);
        }

        JSONArray followUserArr = followRes.getJSONArray("follow_user");
        if (followUserArr == null || followUserArr.isEmpty())
        {
            return new WecomCustomerTotalResponse();
        }

        List<String> staffList = new ArrayList<>();
        for (int i = 0; i < followUserArr.size(); i++)
        {
            String uid = followUserArr.getString(i);
            if (StringUtils.isNotEmpty(uid))
            {
                staffList.add(uid);
            }
        }

        if (staffList.isEmpty())
        {
            return new WecomCustomerTotalResponse();
        }

        // 2. 分批并发拉取每个员工的客户列表
        Set<String> uniqueCustomerSet = new HashSet<>();
        int rawTotal = 0;
        List<WecomStaffCustomerItem> items = new ArrayList<>();

        int threadCount = Math.min(BATCH_SIZE, staffList.size());
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try
        {
            for (int batchStart = 0; batchStart < staffList.size(); batchStart += BATCH_SIZE)
            {
                int batchEnd = Math.min(batchStart + BATCH_SIZE, staffList.size());
                List<String> batch = staffList.subList(batchStart, batchEnd);

                List<CompletableFuture<StaffResult>> futures = new ArrayList<>();
                for (String userid : batch)
                {
                    String token = accessToken;
                    String corpId = cfg.getCorpId();
                    futures.add(CompletableFuture.supplyAsync(
                            () -> pullStaffCustomers(token, corpId, userid), executor));
                }

                CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                        futures.toArray(new CompletableFuture[0]));

                // 等待本批完成
                allFutures.join();

                for (CompletableFuture<StaffResult> future : futures)
                {
                    try
                    {
                        StaffResult sr = future.get();
                        if (sr != null && sr.externalUserids != null)
                        {
                            uniqueCustomerSet.addAll(sr.externalUserids);
                            rawTotal += sr.externalUserids.size();

                            WecomStaffCustomerItem item = new WecomStaffCustomerItem();
                            item.setUserid(sr.userid);
                            item.setName(sr.name != null ? sr.name : "");
                            item.setCustomerCnt(sr.externalUserids.size());
                            item.setHasFollowPermission(true);
                            items.add(item);
                        }
                    }
                    catch (Exception e)
                    {
                        log.warn("Failed to get staff customer result", e);
                    }
                }

                // 批间间隔
                if (batchEnd < staffList.size())
                {
                    try
                    {
                        Thread.sleep(BATCH_INTERVAL_MS);
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        finally
        {
            executor.shutdown();
        }

        // 3. 按 customerCnt 降序排列
        items.sort((a, b) -> Integer.compare(b.getCustomerCnt(), a.getCustomerCnt()));

        WecomCustomerTotalResponse resp = new WecomCustomerTotalResponse();
        resp.setTotalStaff(items.size());
        resp.setTotalUniqueCustomers(uniqueCustomerSet.size());
        resp.setTotalRawCustomers(rawTotal);
        resp.setItems(items);

        return resp;
    }

    private StaffResult pullStaffCustomers(String accessToken, String corpId, String userid)
    {
        for (int retry = 0; retry <= MAX_TOKEN_RETRY; retry++)
        {
            try
            {
                JSONObject res = wecomTencentApiClient.externalContactList(accessToken, userid);
                if (res == null)
                {
                    log.warn("externalcontact/list returned null for userid={}", userid);
                    return null;
                }

                int errcode = res.getIntValue("errcode");
                if (errcode == 0)
                {
                    JSONArray ids = res.getJSONArray("external_userid");
                    List<String> useridList = new ArrayList<>();
                    if (ids != null)
                    {
                        for (int i = 0; i < ids.size(); i++)
                        {
                            String id = ids.getString(i);
                            if (StringUtils.isNotEmpty(id))
                            {
                                useridList.add(id);
                            }
                        }
                    }

                    // 获取用户姓名
                    String name = fetchUserName(accessToken, corpId, userid);

                    return new StaffResult(userid, name, useridList);
                }
                else if (WecomTencentApiClient.isAccessTokenInvalid(errcode) && retry < MAX_TOKEN_RETRY)
                {
                    // Token过期，刷新后重试
                    log.warn("Access token invalid for userid={}, errcode={}, refreshing token", userid, errcode);
                    wecomAccessTokenService.invalidate(corpId);
                    CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
                    if (cfg != null)
                    {
                        accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
                    }
                    continue;
                }
                else
                {
                    log.warn("externalcontact/list failed for userid={}: errcode={}, errmsg={}",
                            userid, errcode, res.getString("errmsg"));
                    return null;
                }
            }
            catch (Exception e)
            {
                log.warn("externalcontact/list exception for userid={}: {}", userid, e.getMessage());
                return null;
            }
        }
        return null;
    }

    private String fetchUserName(String accessToken, String corpId, String userid)
    {
        for (int retry = 0; retry <= MAX_TOKEN_RETRY; retry++)
        {
            try
            {
                JSONObject res = wecomTencentApiClient.userGet(accessToken, userid);
                if (res != null && res.getIntValue("errcode") == 0)
                {
                    return res.getString("name");
                }
                else if (res != null && WecomTencentApiClient.isAccessTokenInvalid(res.getIntValue("errcode"))
                        && retry < MAX_TOKEN_RETRY)
                {
                    wecomAccessTokenService.invalidate(corpId);
                    CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
                    if (cfg != null)
                    {
                        accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
                    }
                    continue;
                }
            }
            catch (Exception e)
            {
                log.warn("userGet failed for userid={}: {}", userid, e.getMessage());
            }
            return "";
        }
        return "";
    }

    private static class StaffResult
    {
        final String userid;
        final String name;
        final List<String> externalUserids;

        StaffResult(String userid, String name, List<String> externalUserids)
        {
            this.userid = userid;
            this.name = name;
            this.externalUserids = externalUserids;
        }
    }
}
