package com.ruoyi.crm.service.impl;

import java.util.List;


import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.enums.EventType;
import com.ruoyi.crm.event.EventDispatcher;
import com.ruoyi.flowable.service.IFlowDefinitionService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmWorkflowTriggerMapper;
import com.ruoyi.crm.domain.CrmWorkflowTrigger;
import com.ruoyi.crm.service.ICrmWorkflowTriggerService;

/**
 * worflowtriggerService业务层处理
 * 
 * @author luohd
 * @date 2025-06-05
 */
@Service
public class CrmWorkflowTriggerServiceImpl implements ICrmWorkflowTriggerService 
{
    @Autowired
    private CrmWorkflowTriggerMapper crmWorkflowTriggerMapper;

    @Autowired
    private EventDispatcher eventDispatcher;

    @Autowired
    private IFlowDefinitionService flowDefinitionService;
    /**
     * 查询worflowtrigger
     * 
     * @param id worflowtrigger主键
     * @return worflowtrigger
     */
    @Override
    public CrmWorkflowTrigger selectCrmWorkflowTriggerById(Long id)
    {
        return crmWorkflowTriggerMapper.selectCrmWorkflowTriggerById(id);
    }

    /**
     * 查询worflowtrigger列表
     * 
     * @param crmWorkflowTrigger worflowtrigger
     * @return worflowtrigger
     */
    @Override
    public List<CrmWorkflowTrigger> selectCrmWorkflowTriggerList(CrmWorkflowTrigger crmWorkflowTrigger)
    {
        return crmWorkflowTriggerMapper.selectCrmWorkflowTriggerList(crmWorkflowTrigger);
    }

    /**
     * 新增worflowtrigger
     * 
     * @param crmWorkflowTrigger worflowtrigger
     * @return 结果
     */
    @Override
    public int insertCrmWorkflowTrigger(CrmWorkflowTrigger crmWorkflowTrigger)
    {
        crmWorkflowTrigger.setCreateTime(DateUtils.getNowDate());

        int result  = crmWorkflowTriggerMapper.insertCrmWorkflowTrigger(crmWorkflowTrigger);

        if (result != 0) {
            EventType eventType = EventType.valueOf(crmWorkflowTrigger.getEventType());
            String sourceId = crmWorkflowTrigger.getTriggerSourceId();
            String processDefinitionKey = crmWorkflowTrigger.getProcessDefinitionKey();

            eventDispatcher.register(eventType, sourceId, (payload) -> {
                // 再次设置上下文以确保执行流程时使用正确的租户数据源
                DynamicDataSourceContextHolder.setDataSourceKey(crmWorkflowTrigger.getTenantId());
                flowDefinitionService.startProcessInstanceByProcDefId(processDefinitionKey, "", payload);
            });

        }
        return result;
    }

    /**
     * 修改worflowtrigger
     * 
     * @param crmWorkflowTrigger worflowtrigger
     * @return 结果
     */
    @Override
    public int updateCrmWorkflowTrigger(CrmWorkflowTrigger crmWorkflowTrigger)
    {
        crmWorkflowTrigger.setUpdateTime(DateUtils.getNowDate());
        return crmWorkflowTriggerMapper.updateCrmWorkflowTrigger(crmWorkflowTrigger);
    }

    /**
     * 批量删除worflowtrigger
     * 
     * @param ids 需要删除的worflowtrigger主键
     * @return 结果
     */
    @Override
    public int deleteCrmWorkflowTriggerByIds(Long[] ids)
    {
        return crmWorkflowTriggerMapper.deleteCrmWorkflowTriggerByIds(ids);
    }

    /**
     * 删除worflowtrigger信息
     * 
     * @param id worflowtrigger主键
     * @return 结果
     */
    @Override
    public int deleteCrmWorkflowTriggerById(Long id)
    {
        return crmWorkflowTriggerMapper.deleteCrmWorkflowTriggerById(id);
    }
}
