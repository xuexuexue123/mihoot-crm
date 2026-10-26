package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysStage;

/**
 * stageService接口
 * 
 * @author ruoyi
 * @date 2022-12-13
 */
public interface ISysStageService 
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
     * 批量删除stage
     * 
     * @param stageIds 需要删除的stage主键集合
     * @return 结果
     */
    public int deleteSysStageByStageIds(Long[] stageIds);

    /**
     * 删除stage信息
     * 
     * @param stageId stage主键
     * @return 结果
     */
    public int deleteSysStageByStageId(Long stageId);
}
