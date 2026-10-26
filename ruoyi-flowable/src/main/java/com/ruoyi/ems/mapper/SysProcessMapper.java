package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysProcess;

/**
 * processMapper接口
 * 
 * @author luohd
 * @date 2022-12-13
 */
public interface SysProcessMapper 
{
    /**
     * 查询process
     * 
     * @param processId process主键
     * @return process
     */
    public SysProcess selectSysProcessByProcessId(String processId);

    /**
     *
     * @param procDefId
     * @return
     */
    public SysProcess selectSysProcessByProcDefId(String procDefId);

    /**
     * 查询process列表
     * 
     * @param sysProcess process
     * @return process集合
     */
    public List<SysProcess> selectSysProcessList(SysProcess sysProcess);

    /**
     * 新增process
     * 
     * @param sysProcess process
     * @return 结果
     */
    public int insertSysProcess(SysProcess sysProcess);

    /**
     * 修改process
     * 
     * @param sysProcess process
     * @return 结果
     */
    public int updateSysProcess(SysProcess sysProcess);

    /**
     * 删除process
     * 
     * @param processId process主键
     * @return 结果
     */
    public int deleteSysProcessByProcessId(String processId);

    /**
     * 批量删除process
     * 
     * @param processIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysProcessByProcessIds(String[] processIds);
}
