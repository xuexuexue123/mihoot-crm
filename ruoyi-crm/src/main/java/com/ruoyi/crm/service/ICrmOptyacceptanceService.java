package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmOptyacceptance;

/**
 * 销售任务Service接口
 * 
 * @author luohd
 * @date 2024-03-04
 */
public interface ICrmOptyacceptanceService 
{
    /**
     * 查询销售任务
     * 
     * @param acceptId 销售任务主键
     * @return 销售任务
     */
    public CrmOptyacceptance selectCrmOptyacceptanceByAcceptId(String acceptId);

    /**
     * 查询销售任务列表
     * 
     * @param crmOptyacceptance 销售任务
     * @return 销售任务集合
     */
    public List<CrmOptyacceptance> selectCrmOptyacceptanceList(CrmOptyacceptance crmOptyacceptance);

    /**
     * 新增销售任务
     * 
     * @param crmOptyacceptance 销售任务
     * @return 结果
     */
    public int insertCrmOptyacceptance(CrmOptyacceptance crmOptyacceptance);

    /**
     * 修改销售任务
     * 
     * @param crmOptyacceptance 销售任务
     * @return 结果
     */
    public int updateCrmOptyacceptance(CrmOptyacceptance crmOptyacceptance);

    /**
     * 批量删除销售任务
     * 
     * @param acceptIds 需要删除的销售任务主键集合
     * @return 结果
     */
    public int deleteCrmOptyacceptanceByAcceptIds(String[] acceptIds);

    /**
     * 删除销售任务信息
     * 
     * @param acceptId 销售任务主键
     * @return 结果
     */
    public int deleteCrmOptyacceptanceByAcceptId(String acceptId);
}
