package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmAccountrelation;
import com.ruoyi.crm.domain.CrmLead;

/**
 * 业务线索Service接口
 * 
 * @author luohd
 * @date 2024-01-30
 */
public interface ICrmLeadService 
{
    /**
     * 查询业务线索
     * 
     * @param leadId 业务线索主键
     * @return 业务线索
     */
    public CrmLead selectCrmLeadByLeadId(String leadId);

    /**
     * 查询业务线索列表
     * 
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadList(CrmLead crmLead);

    /**
     * 查询最所有业务线索列表导出
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListExport(CrmLead crmLead);

    /**
     * 查询跟基金相关的查询项目列表
     *
     * @param crmAccountrelation 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListByRelation(CrmAccountrelation crmAccountrelation);

    /**
     * 根据业务条件，查询业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListByCriteria(CrmLead crmLead);

    /**
     * 查询本周业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListWeekly(CrmLead crmLead);

    /**
     * 查询本周已转化的业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListWeeklyConverted(CrmLead crmLead);

    /**
     * 查询最近三个月业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListQuarterly(CrmLead crmLead);
    /**
     * 查找所有没有被转化，也没有被合并的线索
     * @param crmLead
     * @return
     */
    public List<CrmLead> selectCrmLeadPendingList(CrmLead crmLead);

    /**
     * 新增业务线索
     * 
     * @param crmLead 业务线索
     * @return 结果
     */
    public int insertCrmLead(CrmLead crmLead);

    /**
     * 修改业务线索
     * 
     * @param crmLead 业务线索
     * @return 结果
     */
    public int updateCrmLead(CrmLead crmLead);

    /**
     * 批量删除业务线索
     * 
     * @param leadIds 需要删除的业务线索主键集合
     * @return 结果
     */
    public int deleteCrmLeadByLeadIds(String[] leadIds);

    /**
     * 删除业务线索信息
     * 
     * @param leadId 业务线索主键
     * @return 结果
     */
    public int deleteCrmLeadByLeadId(String leadId);
}
