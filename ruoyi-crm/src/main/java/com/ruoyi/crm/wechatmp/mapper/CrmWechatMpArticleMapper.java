package com.ruoyi.crm.wechatmp.mapper;

import java.time.OffsetDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticle;

public interface CrmWechatMpArticleMapper
{
    /** 查询某公众号的最大 update_time，用于增量同步判断 */
    OffsetDateTime selectMaxUpdateTime(@Param("appId") String appId);

    /** 分页查询已发布图文（按 update_time 倒序，过滤已删除） */
    List<CrmWechatMpArticle> selectPageByAppId(@Param("appId") String appId,
            @Param("offset") int offset, @Param("limit") int limit);

    /** 统计某公众号图文总数（过滤已删除） */
    long countByAppId(@Param("appId") String appId);

    /** 批量 upsert（ON CONFLICT DO UPDATE 基本字段） */
    int batchUpsert(@Param("list") List<CrmWechatMpArticle> list);

    /** 更新单条图文的详细数据（来自 getarticle） */
    int updateDetail(@Param("row") CrmWechatMpArticle row);

    /** 检查 article_id 是否已存在 */
    boolean existsByArticleId(@Param("appId") String appId, @Param("articleId") String articleId,
            @Param("indexInMsg") int indexInMsg);

    /** 按 article_id + index 查单篇（留言 sync 回填标题） */
    CrmWechatMpArticle selectByArticleId(@Param("appId") String appId, @Param("articleId") String articleId,
            @Param("indexInMsg") int indexInMsg);
}
