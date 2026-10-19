package com.ruoyi.crm.wechatmp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleStat;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpPublishTask;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpArticleStatMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpPublishTaskMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleStatService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

/**
 * 微信公众号文章统计服务
 * 调用 getarticletotaldetail 接口获取文章统计数据，通过 msg_data_id 关联
 * publish_task 和 article 表后写入 stat 表。
 */
@Service
public class WechatMpArticleStatServiceImpl implements IWechatMpArticleStatService
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpArticleStatServiceImpl.class);

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Autowired
    private CrmWechatMpPublishTaskMapper publishTaskMapper;

    @Autowired
    private CrmWechatMpArticleStatMapper articleStatMapper;

    @Override
    public AjaxResult fetchArticleStat(String appId, String beginDate, String endDate)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }
        if (!StringUtils.hasText(beginDate) || !StringUtils.hasText(endDate))
        {
            return AjaxResult.error("beginDate 和 endDate 不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;

        JSONObject res = tencentApiClient.getArticleTotalDetail(token, beginDate, endDate);
        if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
        {
            token = tokenService.refreshAccessToken(appId);
            tokenRefreshed = true;
            res = tencentApiClient.getArticleTotalDetail(token, beginDate, endDate);
        }

        if (!isWeChatSuccess(res))
        {
            return AjaxResult.error("获取文章统计数据失败: " + res.getString("errmsg"));
        }

        JSONArray list = res.getJSONArray("list");
        if (list == null || list.isEmpty())
        {
            return AjaxResult.success("选定日期无发表文章，统计数据为空");
        }

        List<CrmWechatMpArticleStat> stats = new ArrayList<>();
        int skipped = 0;

        for (int i = 0; i < list.size(); i++)
        {
            JSONObject article = list.getJSONObject(i);
            if (article == null)
            {
                continue;
            }

            String msgid = article.getString("msgid"); // 格式: "msg_data_id_index"，如 "12003_3"
            String refDateStr = article.getString("ref_date");

            // 解析 msgid → msg_data_id + index(1-based)
            String msgDataId = null;
            int index1Based = 1;
            if (StringUtils.hasText(msgid) && msgid.contains("_"))
            {
                int lastUnderscore = msgid.lastIndexOf('_');
                msgDataId = msgid.substring(0, lastUnderscore);
                try
                {
                    index1Based = Integer.parseInt(msgid.substring(lastUnderscore + 1));
                }
                catch (NumberFormatException e)
                {
                    log.warn("Failed to parse index from msgid={}", msgid);
                }
            }
            else
            {
                // 无下划线，将整个 msgid 作为 msg_data_id，index=1
                msgDataId = msgid;
            }
            int index0Based = Math.max(index1Based - 1, 0);

            // 通过 msg_data_id 查找 publish_task 获取 article_id
            List<CrmWechatMpPublishTask> tasks = publishTaskMapper.selectByMsgDataId(msgDataId);
            if (tasks == null || tasks.isEmpty())
            {
                log.debug("No publish_task found for msg_data_id={}, skipping", msgDataId);
                skipped++;
                continue;
            }
            // 取最近一条发布任务
            CrmWechatMpPublishTask task = tasks.get(0);
            String articleId = task.getArticleId();
            if (!StringUtils.hasText(articleId))
            {
                log.debug("Publish task msg_data_id={} has no article_id yet, skipping", msgDataId);
                skipped++;
                continue;
            }

            JSONArray detailList = article.getJSONArray("detail_list");
            if (detailList == null || detailList.isEmpty())
            {
                continue;
            }

            for (int j = 0; j < detailList.size(); j++)
            {
                JSONObject detail = detailList.getJSONObject(j);
                if (detail == null)
                {
                    continue;
                }

                CrmWechatMpArticleStat stat = new CrmWechatMpArticleStat();
                stat.setAppId(appId);
                stat.setArticleId(articleId);
                stat.setIndexInMsg(index0Based);
                stat.setRefDate(StringUtils.hasText(refDateStr)
                        ? LocalDate.parse(refDateStr) : null);
                stat.setStatDate(StringUtils.hasText(detail.getString("stat_date"))
                        ? LocalDate.parse(detail.getString("stat_date")) : null);
                stat.setReadUser(detail.getInteger("read_user"));
                stat.setShareUser(detail.getInteger("share_user"));
                stat.setZaikanUser(detail.getInteger("zaikan_user"));
                stat.setLikeUser(detail.getInteger("like_user"));
                stat.setCommentCount(detail.getInteger("comment_count"));
                stat.setCollectionUser(detail.getInteger("collection_user"));
                stat.setPraiseMoney(detail.getInteger("praise_money"));
                stat.setReadSubscribeUser(detail.getInteger("read_subscribe_user"));
                stat.setReadDeliveryRate(toBigDecimal(detail, "read_delivery_rate"));
                stat.setReadFinishRate(toBigDecimal(detail, "read_finish_rate"));
                stat.setReadAvgActivetime(toBigDecimal(detail, "read_avg_activetime"));
                stats.add(stat);
            }
        }

        if (!stats.isEmpty())
        {
            articleStatMapper.batchUpsert(stats);
        }

        return AjaxResult.success(String.format("统计数据拉取完成，成功 %d 条，跳过 %d 条", stats.size(), skipped));
    }

    private static boolean isWeChatSuccess(JSONObject res)
    {
        return res != null && res.getIntValue("errcode") == 0;
    }

    private static BigDecimal toBigDecimal(JSONObject json, String key)
    {
        if (json == null || !json.containsKey(key))
        {
            return null;
        }
        return json.getBigDecimal(key);
    }
}
