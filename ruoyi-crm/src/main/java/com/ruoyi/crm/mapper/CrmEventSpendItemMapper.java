package com.ruoyi.crm.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEventSpendItem;

public interface CrmEventSpendItemMapper
{
    List<CrmEventSpendItem> selectByEventId(@Param("tenantId") String tenantId, @Param("eventId") String eventId);

    int deleteByEventId(@Param("tenantId") String tenantId, @Param("eventId") String eventId);

    int insertBatch(@Param("items") List<CrmEventSpendItem> items);

    List<Map<String, Object>> selectSpendByChannel(@Param("tenantId") String tenantId, @Param("eventId") String eventId);

    BigDecimal selectTotalAmount(@Param("tenantId") String tenantId, @Param("eventId") String eventId);
}
