package com.ruoyi.crm.wechatmp.mapper;

import java.time.OffsetDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleDraft;

public interface CrmWechatMpArticleDraftMapper
{
    /** 查询某公众号的最大 update_time，用于增量同步判断 */
    OffsetDateTime selectMaxUpdateTime(@Param("appId") String appId);

    /** 分页查询草稿（按 update_time 倒序） */
    List<CrmWechatMpArticleDraft> selectPageByAppId(@Param("appId") String appId,
            @Param("offset") int offset, @Param("limit") int limit);

    /** 按 media_id 查询单条草稿 */
    CrmWechatMpArticleDraft selectByMediaId(@Param("appId") String appId, @Param("mediaId") String mediaId);

    /** 统计某公众号草稿总数 */
    long countByAppId(@Param("appId") String appId);

    /** 批量 upsert（ON CONFLICT DO UPDATE） */
    int batchUpsert(@Param("list") List<CrmWechatMpArticleDraft> list);

    /** 按 media_id 删除所有相关记录 */
    int deleteByMediaId(@Param("appId") String appId, @Param("mediaId") String mediaId);

    /** 更新同步状态和 media_id（用于本地草稿同步到微信后更新） */
    int updateSyncedByMediaId(@Param("appId") String appId, @Param("mediaId") String mediaId,
            @Param("newMediaId") String newMediaId, @Param("synced") Boolean synced);

    /** 更新草稿的发布状态 */
    int updatePublished(@Param("appId") String appId, @Param("mediaId") String mediaId,
            @Param("published") Boolean published);
}
