package com.ruoyi.crm.mapper;

import java.util.List;

import com.ruoyi.crm.domain.CrmWecomWelcomeLog;

public interface CrmWecomWelcomeLogMapper
{
    int insert(CrmWecomWelcomeLog row);

    List<CrmWecomWelcomeLog> selectList(CrmWecomWelcomeLog query);
}
