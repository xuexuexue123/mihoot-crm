package com.ruoyi.flowable.service.impl;

import java.util.List;
import java.util.Objects;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysDeployForm;
import com.ruoyi.system.mapper.SysDeployFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysFormMapper;
import com.ruoyi.system.domain.SysForm;
import com.ruoyi.flowable.service.ISysFormService;

/**
 * 流程表单Service业务层处理
 * 
 * @author XuanXuan Xuan
 * @date 2021-04-03
 */
@Service
public class SysFormServiceImpl implements ISysFormService 
{
    @Autowired
    private SysFormMapper sysFormMapper;

    /**
     * 查询流程表单
     * 
     * @param formId 流程表单ID
     * @return 流程表单
     */
    @Override
    public SysForm selectSysFormById(Long formId)
    {
        return sysFormMapper.selectSysFormById(formId);
    }

    @Override
    public SysForm selectSysFormByProcDefId(String procDefId) {
        return sysFormMapper.selectSysFormByProcDefId(procDefId);
    }


    @Override
    public SysForm selectSysFormByTaskId(String taskId) {
        return sysFormMapper.selectSysFormByTaskId(taskId);
    }

    /**
     * 查询流程表单列表
     * 
     * @param sysForm 流程表单
     * @return 流程表单
     */
    @Override
    public List<SysForm> selectSysFormList(SysForm sysForm)
    {
        return sysFormMapper.selectSysFormList(sysForm);
    }

    /**
     * 新增流程表单
     * 
     * @param sysForm 流程表单
     * @return 结果
     */
    @Override
    public Long insertSysForm(SysForm sysForm)
    {
        //1. 先创建一个表单记录
        sysForm.setCreateTime(DateUtils.getNowDate());
        //2. 如果表单记录创建成功，那就插入一条记录到关系表里

        int result = sysFormMapper.insertSysForm(sysForm);
        System.out.println("new form id = " + sysForm.getCaseTypeId());

        if (result==1) {
            Long formId = sysForm.getFormId();
            return formId;
        } else {
            return 1L;
        }


    }

    /**
     * 修改流程表单
     * 
     * @param sysForm 流程表单
     * @return 结果
     */
    @Override
    public int updateSysForm(SysForm sysForm)
    {
        sysForm.setUpdateTime(DateUtils.getNowDate());
        return sysFormMapper.updateSysForm(sysForm);
    }

    /**
     * 批量删除流程表单
     * 
     * @param formIds 需要删除的流程表单ID
     * @return 结果
     */
    @Override
    public int deleteSysFormByIds(Long[] formIds)
    {
        return sysFormMapper.deleteSysFormByIds(formIds);
    }

    /**
     * 删除流程表单信息
     * 
     * @param formId 流程表单ID
     * @return 结果
     */
    @Override
    public int deleteSysFormById(Long formId)
    {
        return sysFormMapper.deleteSysFormById(formId);
    }
}
