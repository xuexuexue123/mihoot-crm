package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.ems.domain.SysProcess;
import com.ruoyi.ems.mapper.SysStageMapper;
import com.ruoyi.ems.domain.SysStage;
import com.ruoyi.ems.service.ISysStageService;

/**
 * stageService业务层处理
 * 
 * @author ruoyi
 * @date 2022-12-13
 */
@Slf4j
@Service
public class SysStageServiceImpl implements ISysStageService 
{
    @Autowired
    private SysStageMapper sysStageMapper;

    /**
     * 查询stage
     * 
     * @param stageId stage主键
     * @return stage
     */
    @Override
    public SysStage selectSysStageByStageId(Long stageId)
    {
        return sysStageMapper.selectSysStageByStageId(stageId);
    }

    /**
     * 查询stage列表
     * 
     * @param sysStage stage
     * @return stage
     */
    @Override
    public List<SysStage> selectSysStageList(SysStage sysStage)
    {
        return sysStageMapper.selectSysStageList(sysStage);
    }

    /**
     * 新增stage
     * 
     * @param sysStage stage
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSysStage(SysStage sysStage)
    {
        sysStage.setCreateTime(DateUtils.getNowDate());
        int rows = sysStageMapper.insertSysStage(sysStage);
//        log.info("stageId = " + sysStage.getStageId());
        insertSysProcess(sysStage);
        return sysStage.getStageId().intValue();
    }

    /**
     * 修改stage
     * 
     * @param sysStage stage
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSysStage(SysStage sysStage)
    {
        sysStage.setUpdateTime(DateUtils.getNowDate());
        sysStageMapper.deleteSysProcessByStageId(sysStage.getStageId());
        insertSysProcess(sysStage);
        return sysStageMapper.updateSysStage(sysStage);
    }

    /**
     * 批量删除stage
     * 
     * @param stageIds 需要删除的stage主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSysStageByStageIds(Long[] stageIds)
    {
        sysStageMapper.deleteSysProcessByStageIds(stageIds);
        return sysStageMapper.deleteSysStageByStageIds(stageIds);
    }

    /**
     * 删除stage信息
     * 
     * @param stageId stage主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSysStageByStageId(Long stageId)
    {
        sysStageMapper.deleteSysProcessByStageId(stageId);
        return sysStageMapper.deleteSysStageByStageId(stageId);
    }

    /**
     * 新增流程定义信息
     * 
     * @param sysStage stage对象
     */
    public void insertSysProcess(SysStage sysStage)
    {
        List<SysProcess> sysProcessList = sysStage.getSysProcessList();
        String stageId = sysStage.getStageId().toString();
        if (StringUtils.isNotNull(sysProcessList))
        {
            List<SysProcess> list = new ArrayList<SysProcess>();
            for (SysProcess sysProcess : sysProcessList)
            {
                sysProcess.setStageId(stageId);
                list.add(sysProcess);
            }
            if (list.size() > 0)
            {
                sysStageMapper.batchSysProcess(list);
            }
        }
    }
}
