package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmWorkflowTrigger;

/**
 * worflowtriggerService接口
 * 
 * @author luohd
 * @date 2025-06-05
 */
public interface ICrmWorkflowTriggerService 
{
    /**
     * 查询worflowtrigger
     * 
     * @param id worflowtrigger主键
     * @return worflowtrigger
     */
    public CrmWorkflowTrigger selectCrmWorkflowTriggerById(Long id);

    /**
     * 查询worflowtrigger列表
     * 
     * @param crmWorkflowTrigger worflowtrigger
     * @return worflowtrigger集合
     */
    public List<CrmWorkflowTrigger> selectCrmWorkflowTriggerList(CrmWorkflowTrigger crmWorkflowTrigger);

    /**
     * 新增worflowtrigger
     * 
     * @param crmWorkflowTrigger worflowtrigger
     * @return 结果
     */
    public int insertCrmWorkflowTrigger(CrmWorkflowTrigger crmWorkflowTrigger);

    /**
     * 修改worflowtrigger
     * 
     * @param crmWorkflowTrigger worflowtrigger
     * @return 结果
     */
    public int updateCrmWorkflowTrigger(CrmWorkflowTrigger crmWorkflowTrigger);

    /**
     * 批量删除worflowtrigger
     * 
     * @param ids 需要删除的worflowtrigger主键集合
     * @return 结果
     */
    public int deleteCrmWorkflowTriggerByIds(Long[] ids);

    /**
     * 删除worflowtrigger信息
     * 
     * @param id worflowtrigger主键
     * @return 结果
     */
    public int deleteCrmWorkflowTriggerById(Long id);
}
