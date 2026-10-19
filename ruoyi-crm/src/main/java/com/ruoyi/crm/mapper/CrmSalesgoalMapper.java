package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmSalesgoal;

/**
 * 销售目标Mapper接口
 * 
 * @author luohd
 * @date 2024-01-19
 */
public interface CrmSalesgoalMapper 
{
    public CrmSalesgoal selectCrmSalesgoalByGoalId(String goalId);

    public List<CrmSalesgoal> selectCrmSalesgoalList(CrmSalesgoal crmSalesgoal);

    public List<CrmSalesgoal> selectMyCrmSalesgoal(CrmSalesgoal crmSalesgoal);

    public int insertCrmSalesgoal(CrmSalesgoal crmSalesgoal);

    public int updateCrmSalesgoal(CrmSalesgoal crmSalesgoal);

    public int resetCrmSalesgoal(CrmSalesgoal crmSalesgoal);

    public int deleteCrmSalesgoalByGoalId(String goalId);

    public int deleteCrmSalesgoalByGoalIds(String[] goalIds);

    int countByOwnerPeriodKpi(@Param("ownerId") String ownerId,
            @Param("periodId") String periodId,
            @Param("kpiCode") String kpiCode,
            @Param("excludeGoalId") String excludeGoalId);

    int updateAssessBatch(@Param("periodId") String periodId,
            @Param("kpiCode") String kpiCode,
            @Param("isActive") Long isActive,
            @Param("updateBy") String updateBy,
            @Param("updateTime") Date updateTime);

    List<String> selectExistingOwnerIds(@Param("periodId") String periodId,
            @Param("kpiCode") String kpiCode,
            @Param("ownerIds") List<String> ownerIds);

    int batchInsertCrmSalesgoal(@Param("list") List<CrmSalesgoal> list);
}
