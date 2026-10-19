package com.ruoyi.crm.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.CrmWecomCallbackEvent;

public interface CrmWecomCallbackEventMapper
{
    int insert(CrmWecomCallbackEvent row);

    List<CrmWecomCallbackEvent> selectList(CrmWecomCallbackEvent query);

    List<Map<String, Object>> countByEventType(CrmWecomCallbackEvent query);
}
