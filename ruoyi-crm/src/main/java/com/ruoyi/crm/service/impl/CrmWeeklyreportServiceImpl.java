package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.domain.CrmReportdetail;
import com.ruoyi.crm.mapper.CrmWeeklyreportMapper;
import com.ruoyi.crm.domain.CrmWeeklyreport;
import com.ruoyi.crm.service.ICrmWeeklyreportService;

/**
 * 周报Service业务层处理
 * 
 * @author luohd
 * @date 2024-04-16
 */
@Service
public class CrmWeeklyreportServiceImpl implements ICrmWeeklyreportService 
{
    @Autowired
    private CrmWeeklyreportMapper crmWeeklyreportMapper;

    /**
     * 查询周报
     * 
     * @param reportId 周报主键
     * @return 周报
     */
    @Override
    public CrmWeeklyreport selectCrmWeeklyreportByReportId(String reportId)
    {
        return crmWeeklyreportMapper.selectCrmWeeklyreportByReportId(reportId);
    }

    /**
     * 查询周报列表
     * 
     * @param crmWeeklyreport 周报
     * @return 周报
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmWeeklyreport> selectCrmWeeklyreportList(CrmWeeklyreport crmWeeklyreport)
    {
        return crmWeeklyreportMapper.selectCrmWeeklyreportList(crmWeeklyreport);
    }

    /**
     * 查询本周周报列表
     *
     * @param crmWeeklyreport 周报
     * @return 周报
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmWeeklyreport> selectCrmWeeklyreportListWeekly(CrmWeeklyreport crmWeeklyreport)
    {
        return crmWeeklyreportMapper.selectCrmWeeklyreportListWeekly(crmWeeklyreport);
    }
    /**
     * 新增周报
     * 
     * @param crmWeeklyreport 周报
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCrmWeeklyreport(CrmWeeklyreport crmWeeklyreport)
    {
        crmWeeklyreport.setCreateTime(DateUtils.getNowDate());
        int rows = crmWeeklyreportMapper.insertCrmWeeklyreport(crmWeeklyreport);
        insertCrmReportdetail(crmWeeklyreport);
        return rows;
    }

    /**
     * 修改周报
     * 
     * @param crmWeeklyreport 周报
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCrmWeeklyreport(CrmWeeklyreport crmWeeklyreport)
    {
        crmWeeklyreport.setUpdateTime(DateUtils.getNowDate());
        crmWeeklyreportMapper.deleteCrmReportdetailByReportId(crmWeeklyreport.getReportId());
        insertCrmReportdetail(crmWeeklyreport);
        return crmWeeklyreportMapper.updateCrmWeeklyreport(crmWeeklyreport);
    }

    /**
     * 批量删除周报
     * 
     * @param reportIds 需要删除的周报主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmWeeklyreportByReportIds(String[] reportIds)
    {
        crmWeeklyreportMapper.deleteCrmReportdetailByReportIds(reportIds);
        return crmWeeklyreportMapper.deleteCrmWeeklyreportByReportIds(reportIds);
    }

    /**
     * 删除周报信息
     * 
     * @param reportId 周报主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmWeeklyreportByReportId(String reportId)
    {
        crmWeeklyreportMapper.deleteCrmReportdetailByReportId(reportId);
        return crmWeeklyreportMapper.deleteCrmWeeklyreportByReportId(reportId);
    }

    /**
     * 新增周报详细信息
     * 
     * @param crmWeeklyreport 周报对象
     */
    public void insertCrmReportdetail(CrmWeeklyreport crmWeeklyreport)
    {
        List<CrmReportdetail> crmReportdetailList = crmWeeklyreport.getCrmReportdetailList();
        String reportId = crmWeeklyreport.getReportId();
        if (StringUtils.isNotNull(crmReportdetailList))
        {
            List<CrmReportdetail> list = new ArrayList<CrmReportdetail>();
            for (CrmReportdetail crmReportdetail : crmReportdetailList)
            {
                crmReportdetail.setReportId(reportId);
                list.add(crmReportdetail);
            }
            if (list.size() > 0)
            {
                crmWeeklyreportMapper.batchCrmReportdetail(list);
            }
        }
    }
}
