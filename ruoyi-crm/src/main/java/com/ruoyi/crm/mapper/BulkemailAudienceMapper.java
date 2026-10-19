package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.dto.BulkemailAudiencePreviewRow;
import com.ruoyi.crm.service.bulkemail.BulkemailAudienceQuery;

/**
 * 邮件营销受众筛选
 */
public interface BulkemailAudienceMapper
{
    long countFiltered(@Param("query") BulkemailAudienceQuery query);

    long countValidEmailFiltered(@Param("query") BulkemailAudienceQuery query);

    List<BulkemailAudiencePreviewRow> selectFiltered(@Param("query") BulkemailAudienceQuery query,
            @Param("offset") int offset,
            @Param("limit") int limit);
}
