package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysStage;
import com.ruoyi.ems.domain.SysProcess;

/**
 * stageMapper接口
 * 
 * @author ruoyi
 * @date 2022-12-13
 */
public interface SysStageMapper 
{
    /**
     * 查询stage
     * 
     * @param stageId stage主键
     * @return stage
     */
    public SysStage selectSysStageByStageId(Long stageId);

    /**
     * 查询stage列表
     * 
     * @param sysStage stage
     * @return stage集合
     */
    public List<SysStage> selectSysStageList(SysStage sysStage);

    /**
     * 新增stage
     * 
     * @param sysStage stage
     * @return 结果
     */
    public int insertSysStage(SysStage sysStage);

    /**
     * 修改stage
     * 
     * @param sysStage stage
     * @return 结果
     */
    public int updateSysStage(SysStage sysStage);

    /**
     * 删除stage
     * 
     * @param stageId stage主键
     * @return 结果
     */
    public int deleteSysStageByStageId(Long stageId);

    /**
     * 批量删除stage
     * 
     * @param stageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysStageByStageIds(Long[] stageIds);

    /**
     * 批量删除流程定义
     * 
     * @param stageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysProcessByStageIds(Long[] stageIds);
    
    /**
     * 批量新增流程定义
     * 
     * @param sysProcessList 流程定义列表
     * @return 结果
     */
    public int batchSysProcess(List<SysProcess> sysProcessList);
    

    /**
     * 通过stage主键删除流程定义信息
     * 
     * @param stageId stageID
     * @return 结果
     */
    public int deleteSysProcessByStageId(Long stageId);
}
