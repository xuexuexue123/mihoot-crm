package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomMassTask;

public interface CrmWecomMassTaskMapper
{
    List<CrmWecomMassTask> selectList(CrmWecomMassTask query);

    /**
     * 待同步群发任务：已落库 msg_id、仍为 created（未通过详情接口或定时任务拉取过结果）。
     */
    List<Long> selectIdsPendingSync(@Param("limit") int limit);

    CrmWecomMassTask selectById(Long id);

    int insert(CrmWecomMassTask row);

    int update(CrmWecomMassTask row);
}
