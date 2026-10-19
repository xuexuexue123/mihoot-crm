package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleStat;

public interface CrmWechatMpArticleStatMapper
{
    /** 批量 upsert 统计数据（ON CONFLICT DO UPDATE） */
    int batchUpsert(@Param("list") List<CrmWechatMpArticleStat> list);

    /** 按文章和日期范围查询统计数据 */
    List<CrmWechatMpArticleStat> selectByArticleAndDateRange(
            @Param("articleId") String articleId,
            @Param("indexInMsg") int indexInMsg,
            @Param("beginDate") String beginDate,
            @Param("endDate") String endDate);

    /**
     * 按 appId 批量查询所有文章的聚合统计（按 article_id + index_in_msg 分组）。
     * 每行包含 stat_key, total_read, total_share, total_like, total_comment, total_collect。
     */
    List<Map<String, Object>> selectAggregatedByAppId(@Param("appId") String appId);
}
