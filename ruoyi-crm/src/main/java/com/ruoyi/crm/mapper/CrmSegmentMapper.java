package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmSegment;
import com.ruoyi.crm.domain.vo.SegmentVo;

public interface CrmSegmentMapper
{
    List<SegmentVo> selectList(@Param("tenantId") String tenantId);

    SegmentVo selectById(@Param("tenantId") String tenantId, @Param("id") Long id);

    CrmSegment selectEntityById(@Param("tenantId") String tenantId, @Param("id") Long id);

    int insertCrmSegment(CrmSegment segment);

    int updateCrmSegment(CrmSegment segment);

    int deleteById(@Param("tenantId") String tenantId, @Param("id") Long id);
}
