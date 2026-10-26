package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.vo.StandupAgendaItem;
import com.ruoyi.crm.domain.vo.StandupDailyVo;
import com.ruoyi.crm.domain.vo.StandupMemberDetailVo;
import com.ruoyi.crm.domain.vo.StandupRiskItem;
import com.ruoyi.crm.domain.vo.StandupWinItem;

/**
 * 销售晨会报告。
 */
public interface ICrmStandupService
{
    StandupDailyVo daily(String date, Long deptId, String ownerId, Boolean includeKpi, String kpiCodes);

    List<StandupAgendaItem> agenda(String date, String type, Long deptId, String ownerId, Boolean includeCancelled);

    List<StandupRiskItem> risks(String date, String types, Long deptId, String ownerId);

    List<StandupWinItem> wins(String date, Long deptId, String ownerId);

    StandupMemberDetailVo memberDetail(String ownerId, String date);
}
