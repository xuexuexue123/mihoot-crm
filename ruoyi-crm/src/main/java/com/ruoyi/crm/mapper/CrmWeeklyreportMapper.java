package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmWeeklyreport;
import com.ruoyi.crm.domain.CrmReportdetail;

/**
 * 周报Mapper接口
 * 
 * @author luohd
 * @date 2024-04-16
 */
public interface CrmWeeklyreportMapper 
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
     * 删除周报
     * 
     * @param reportId 周报主键
     * @return 结果
     */
    public int deleteCrmWeeklyreportByReportId(String reportId);

    /**
     * 批量删除周报
     * 
     * @param reportIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmWeeklyreportByReportIds(String[] reportIds);

    /**
     * 批量删除周报详细
     * 
     * @param reportIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmReportdetailByReportIds(String[] reportIds);
    
    /**
     * 批量新增周报详细
     * 
     * @param crmReportdetailList 周报详细列表
     * @return 结果
     */
    public int batchCrmReportdetail(List<CrmReportdetail> crmReportdetailList);
    

    /**
     * 通过周报主键删除周报详细信息
     * 
     * @param reportId 周报ID
     * @return 结果
     */
    public int deleteCrmReportdetailByReportId(String reportId);
}
