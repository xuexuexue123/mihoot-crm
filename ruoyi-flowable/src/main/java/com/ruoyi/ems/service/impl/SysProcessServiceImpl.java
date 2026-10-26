package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.flowable.util.BpmnXmlRepairUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysProcessMapper;
import com.ruoyi.ems.domain.SysProcess;
import com.ruoyi.ems.service.ISysProcessService;

/**
 * processService业务层处理
 * 
 * @author luohd
 * @date 2022-12-13
 */
@Service
public class SysProcessServiceImpl implements ISysProcessService 
{
    @Autowired
    private SysProcessMapper sysProcessMapper;

    /**
     * 查询process
     * 
     * @param processId process主键
     * @return process
     */
    @Override
    public SysProcess selectSysProcessByProcessId(String processId)
    {
        return sysProcessMapper.selectSysProcessByProcessId(processId);
    }

    /**
     *
     * @param procDefId
     * @return
     */
    @Override
    public SysProcess selectSysProcessByProcDefId(String procDefId)
    {
        return sysProcessMapper.selectSysProcessByProcDefId(procDefId);
    }

    /**
     * 查询process列表
     * 
     * @param sysProcess process
     * @return process
     */
    @Override
    public List<SysProcess> selectSysProcessList(SysProcess sysProcess)
    {
        return sysProcessMapper.selectSysProcessList(sysProcess);
    }

    /**
     * 新增process
     * 
     * @param sysProcess process
     * @return 结果
     */
    @Override
    public int insertSysProcess(SysProcess sysProcess)
    {
        sysProcess.setCreateTime(DateUtils.getNowDate());
        repairBpmnXml(sysProcess);
        return sysProcessMapper.insertSysProcess(sysProcess);
    }

    /**
     * 修改process
     * 
     * @param sysProcess process
     * @return 结果
     */
    @Override
    public int updateSysProcess(SysProcess sysProcess)
    {
        sysProcess.setUpdateTime(DateUtils.getNowDate());
        repairBpmnXml(sysProcess);
        return sysProcessMapper.updateSysProcess(sysProcess);
    }

    /**
     * 批量删除process
     * 
     * @param processIds 需要删除的process主键
     * @return 结果
     */
    @Override
    public int deleteSysProcessByProcessIds(String[] processIds)
    {
        return sysProcessMapper.deleteSysProcessByProcessIds(processIds);
    }

    /**
     * 删除process信息
     * 
     * @param processId process主键
     * @return 结果
     */
    @Override
    public int deleteSysProcessByProcessId(String processId)
    {
        return sysProcessMapper.deleteSysProcessByProcessId(processId);
    }

    private void repairBpmnXml(SysProcess sysProcess)
    {
        if (sysProcess != null && sysProcess.getBpmnXml() != null)
        {
            sysProcess.setBpmnXml(BpmnXmlRepairUtils.repairReactFlowBpmnXml(sysProcess.getBpmnXml()));
        }
    }
}
