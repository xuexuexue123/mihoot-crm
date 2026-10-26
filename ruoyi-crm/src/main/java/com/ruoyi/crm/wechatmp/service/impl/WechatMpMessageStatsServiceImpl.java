package com.ruoyi.crm.wechatmp.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessageStatsService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WechatMpMessageStatsServiceImpl implements IWechatMpMessageStatsService
{
    private static final int WECHAT_ERR_OK = 0;
    private static final int WECHAT_ERR_SYSTEM = -1;
    private static final int WECHAT_ERR_DATE_FORMAT = 61500;
    private static final int WECHAT_ERR_DATE_RANGE = 61501;
    private static final int WECHAT_ERR_DATA_NOT_READY = 61503;

    private static final String CACHE_KEY_PREFIX = "wechat_mp:datacube";
    private static final int CACHE_TTL_MINUTES = 10;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Autowired
    private RedisCache redisCache;

    @Override
    public AjaxResult getMessageStats(String appId, String beginDate, String endDate, String endpoint)
    {
        validateParams(appId, beginDate, endDate, endpoint);
        return fetchWithCache(appId, beginDate, endDate, endpoint);
    }

    private void validateParams(String appId, String beginDate, String endDate, String endpoint)
    {
        if (appId == null || appId.isEmpty())
        {
            throw new ServiceException("appId 不能为空");
        }
        if (beginDate == null || beginDate.isEmpty() || endDate == null || endDate.isEmpty())
        {
            throw new ServiceException("beginDate 和 endDate 不能为空");
        }

        Date begin = parseDate(beginDate, "起始日期");
        Date end = parseDate(endDate, "结束日期");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date yesterday = cal.getTime();
        if (end.after(yesterday))
        {
            throw new ServiceException("结束日期不能超过昨日");
        }
        if (begin.after(end))
        {
            throw new ServiceException("起始日期不能晚于结束日期");
        }

        long diffDays = (end.getTime() - begin.getTime()) / (1000L * 60 * 60 * 24);
        validateDateSpan(endpoint, diffDays, begin, end);
    }

    private Date parseDate(String dateStr, String label)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);
            return sdf.parse(dateStr);
        }
        catch (Exception e)
        {
            throw new ServiceException(label + "格式错误，应为 yyyy-MM-dd");
        }
    }

    private void validateDateSpan(String endpoint, long diffDays, Date begin, Date end)
    {
        if (endpoint == null)
        {
            throw new ServiceException("endpoint 不能为空");
        }

        if (endpoint.contains("upstream-dist"))
        {
            if (diffDays > 15)
            {
                throw new ServiceException("分布类接口日期跨度不能超过 15 天");
            }
        }
        else if (endpoint.contains("upstream-week"))
        {
            if (diffDays > 0)
            {
                throw new ServiceException("周粒度接口起始和结束日期必须为同一天");
            }
        }
        else if (endpoint.contains("upstream-month"))
        {
            if (diffDays > 0)
            {
                throw new ServiceException("月粒度接口起始和结束日期必须为同一天");
            }
        }
        else if (endpoint.contains("upstream-hour"))
        {
            if (diffDays > 0)
            {
                throw new ServiceException("分时接口起始和结束日期必须为同一天");
            }
        }
        else if (endpoint.equals("getupstreammsg"))
        {
            if (diffDays >= 7)
            {
                throw new ServiceException("日粒度概况接口日期跨度不能超过 7 天");
            }
        }
    }

    private AjaxResult fetchWithCache(String appId, String beginDate, String endDate, String endpoint)
    {
        String cacheKey = buildCacheKey(appId, endpoint, beginDate, endDate);

        Object cached = redisCache.getCacheObject(cacheKey);
        if (cached != null)
        {
            return AjaxResult.success(cached);
        }

        return fetchFromWechat(appId, beginDate, endDate, endpoint, cacheKey);
    }

    private AjaxResult fetchFromWechat(String appId, String beginDate, String endDate, String endpoint, String cacheKey)
    {
        String token = tokenService.getAccessToken(appId);
        JSONObject resp = tencentApiClient.callDatacube(token, endpoint, beginDate, endDate);

        if (resp == null)
        {
            return AjaxResult.error("微信接口返回为空");
        }

        int errcode = resp.getIntValue("errcode");
        if (errcode == WECHAT_ERR_OK)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("list", resp.get("list"));
            redisCache.setCacheObject(cacheKey, data, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            return AjaxResult.success(data);
        }

        if (tencentApiClient.isTokenInvalid(resp))
        {
            tokenService.invalidateCache(appId);
            String newToken = tokenService.getAccessToken(appId);
            JSONObject retryResp = tencentApiClient.callDatacube(newToken, endpoint, beginDate, endDate);
            if (retryResp != null && retryResp.getIntValue("errcode") == WECHAT_ERR_OK)
            {
                Map<String, Object> data = new HashMap<>();
                data.put("list", retryResp.get("list"));
                redisCache.setCacheObject(cacheKey, data, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
                return AjaxResult.success(data);
            }
            if (retryResp != null)
            {
                return mapWechatError(retryResp);
            }
        }

        return mapWechatError(resp);
    }

    private AjaxResult mapWechatError(JSONObject resp)
    {
        int errcode = resp.getIntValue("errcode");
        String errmsg = resp.getString("errmsg");

        switch (errcode)
        {
            case WECHAT_ERR_SYSTEM:
                return AjaxResult.error("系统繁忙，请稍后重试");
            case WECHAT_ERR_DATE_FORMAT:
                return AjaxResult.error("日期格式错误");
            case WECHAT_ERR_DATE_RANGE:
                return AjaxResult.error("时间跨度超限");
            case WECHAT_ERR_DATA_NOT_READY:
                return AjaxResult.error("数据未完成统计处理，建议8点后查询昨日数据");
            default:
                log.warn("WeChat datacube error: errcode={}, errmsg={}", errcode, errmsg);
                return AjaxResult.error(errmsg != null ? errmsg : "微信接口返回错误，errcode=" + errcode);
        }
    }

    private String buildCacheKey(String appId, String endpoint, String beginDate, String endDate)
    {
        return CACHE_KEY_PREFIX + ":" + appId + ":" + endpoint + ":" + beginDate + ":" + endDate;
    }
}
