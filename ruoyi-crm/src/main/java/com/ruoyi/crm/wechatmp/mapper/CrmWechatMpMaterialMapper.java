package com.ruoyi.crm.wechatmp.mapper;

import java.time.OffsetDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMaterial;

public interface CrmWechatMpMaterialMapper
{
    /** 查询某公众号某类型的最大 update_time，用于增量同步 */
    OffsetDateTime selectMaxUpdateTime(@Param("appId") String appId, @Param("type") String type);

    /** 分页查询（按 update_time 倒序） */
    List<CrmWechatMpMaterial> selectPageByAppIdAndType(@Param("appId") String appId,
            @Param("type") String type, @Param("offset") int offset, @Param("limit") int limit);

    /** 统计某公众号某类型素材总数 */
    long countByAppIdAndType(@Param("appId") String appId, @Param("type") String type);

    /** 按 media_id 查询 */
    CrmWechatMpMaterial selectByMediaId(@Param("appId") String appId, @Param("mediaId") String mediaId);

    /** 批量 upsert（ON CONFLICT DO UPDATE） */
    int batchUpsert(@Param("list") List<CrmWechatMpMaterial> list);

    /** 单条 upsert */
    int upsert(CrmWechatMpMaterial row);

    /** 按 media_id 删除 */
    int deleteByMediaId(@Param("appId") String appId, @Param("mediaId") String mediaId);

    /** 查询公众号默认素材 */
    CrmWechatMpMaterial selectDefaultByAppId(@Param("appId") String appId);

    /** 清除公众号下全部默认标记 */
    int clearDefaultByAppId(@Param("appId") String appId);

    /** 将指定素材设为默认 */
    int setDefaultByMediaId(@Param("appId") String appId, @Param("mediaId") String mediaId);
}
