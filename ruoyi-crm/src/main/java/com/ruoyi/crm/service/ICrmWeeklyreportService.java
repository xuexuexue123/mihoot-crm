package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmWeeklyreport;

/**
 * 周报Service接口
 * 
 * @author luohd
 * @date 2024-04-16
 */
public interface ICrmWeeklyreportService 
{
    /**
     * 查询周报
     * 
     * @param reportId 周报主键
     * @return 周报
     */
    public CrmWeeklyreport selectCrmWeeklyreportByReportId(String reportId);

    /**
     * 查询周报列表
     * 
     * @param crmWeeklyreport 周报
     * @return 周报集合
     */
    public List<CrmWeeklyreport> selectCrmWeeklyreportList(CrmWeeklyreport crmWeeklyreport);

    /**
     * 查询本周周报列表
     *
     * @param crmWeeklyreport 周报
     * @return 周报集合
     */
    public List<CrmWeeklyreport> selectCrmWeeklyreportListWeekly(CrmWeeklyreport crmWeeklyreport);

    /**
     * 新增周报
     * 
     * @param crmWeeklyreport 周报
     * @return 结果
     */
    public int insertCrmWeeklyreport(CrmWeeklyreport crmWeeklyreport);

    /**
     * 修改周报
     * 
     * @param crmWeeklyreport 周报
     * @return 结果
     */
    public int updateCrmWeeklyreport(CrmWeeklyreport crmWeeklyreport);

    /**
     * 批量删除周报
     * 
     * @param reportIds 需要删除的周报主键集合
     * @return 结果
     */
    public int deleteCrmWeeklyreportByReportIds(String[] reportIds);

    /**
     * 删除周报信息
     * 
     * @param reportId 周报主键
     * @return 结果
     */
    public int deleteCrmWeeklyreportByReportId(String reportId);
}
