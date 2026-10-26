package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.CrmSalesgoal;
import com.ruoyi.crm.domain.dto.SalesgoalAssessBatchRequest;
import com.ruoyi.crm.domain.vo.CrmSalesgoalListVo;

/**
 * 销售目标Service接口
 * 
 * @author luohd
 * @date 2024-01-19
 */
public interface ICrmSalesgoalService 
{
    public CrmSalesgoal selectCrmSalesgoalByGoalId(String goalId);

    public List<CrmSalesgoal> selectCrmSalesgoalList(CrmSalesgoal crmSalesgoal);

    /**
     * KPI 列表：目标行 + actualValue / completionRate。
     */
    public List<CrmSalesgoalListVo> selectCrmSalesgoalListWithActual(CrmSalesgoal crmSalesgoal);

    public List<CrmSalesgoal> selectMyCrmSalesgoal(CrmSalesgoal crmSalesgoal);

    public int insertCrmSalesgoal(CrmSalesgoal crmSalesgoal);

    public int updateCrmSalesgoal(CrmSalesgoal crmSalesgoal);

    public int resetCrmSalesgoal(CrmSalesgoal crmSalesgoal);

    public int deleteCrmSalesgoalByGoalIds(String[] goalIds);

    public int deleteCrmSalesgoalByGoalId(String goalId);

    /**
     * 批量考核：将该月 + KPI 下已存在行全部更新 isActive。
     * @return updated 行数
     */
    public int assessBatch(SalesgoalAssessBatchRequest request);

    /**
     * 预建行：缺失的 (owner, period, kpi) 插入 goalValue=0 / isActive=1。
     * @return created / existing
     */
    public Map<String, Integer> ensureRows(SalesgoalAssessBatchRequest request);
}
