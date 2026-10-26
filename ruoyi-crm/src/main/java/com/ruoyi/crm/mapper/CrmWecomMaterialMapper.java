package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomMaterial;
import com.ruoyi.crm.domain.dto.wecom.WecomMaterialQueryRequest;

public interface CrmWecomMaterialMapper
{
    int insert(CrmWecomMaterial row);

    CrmWecomMaterial selectById(@Param("id") Long id);

    List<CrmWecomMaterial> selectPage(WecomMaterialQueryRequest query);

    int updateWecomUploadResult(@Param("id") Long id,
            @Param("status") String status,
            @Param("mediaId") String mediaId,
            @Param("mediaCreatedAt") Long mediaCreatedAt,
            @Param("expiresAt") java.util.Date expiresAt,
            @Param("wecomUploadedAt") java.util.Date wecomUploadedAt,
            @Param("errmsg") String errmsg);

    int deleteById(@Param("id") Long id);

    /** 查询已过期但状态未更新的记录 */
    List<CrmWecomMaterial> selectExpiredUploaded();

    /** 批量更新过期状态 */
    int batchUpdateExpired(@Param("ids") List<Long> ids);
}
