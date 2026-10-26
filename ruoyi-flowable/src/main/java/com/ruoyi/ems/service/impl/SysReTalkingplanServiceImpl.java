package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReTalkingplanMapper;
import com.ruoyi.ems.domain.SysReTalkingplan;
import com.ruoyi.ems.service.ISysReTalkingplanService;

/**
 * 沟通计划Service业务层处理
 * 
 * @author luohd
 * @date 2023-05-19
 */
@Service
public class SysReTalkingplanServiceImpl implements ISysReTalkingplanService 
{
    @Autowired
    private SysReTalkingplanMapper sysReTalkingplanMapper;

    /**
     * 查询沟通计划
     * 
     * @param planId 沟通计划主键
     * @return 沟通计划
     */
    @Override
    public SysReTalkingplan selectSysReTalkingplanByPlanId(String planId)
    {
        return sysReTalkingplanMapper.selectSysReTalkingplanByPlanId(planId);
    }

    /**
     * 查询沟通计划列表
     * 
     * @param sysReTalkingplan 沟通计划
     * @return 沟通计划
     */
    @Override
    public List<SysReTalkingplan> selectSysReTalkingplanList(SysReTalkingplan sysReTalkingplan)
    {
        return sysReTalkingplanMapper.selectSysReTalkingplanList(sysReTalkingplan);
    }

    /**
     * 新增沟通计划
     * 
     * @param sysReTalkingplan 沟通计划
     * @return 结果
     */
    @Override
    public int insertSysReTalkingplan(SysReTalkingplan sysReTalkingplan)
    {
        sysReTalkingplan.setCreateTime(DateUtils.getNowDate());
        return sysReTalkingplanMapper.insertSysReTalkingplan(sysReTalkingplan);
    }

    /**
     * 修改沟通计划
     * 
     * @param sysReTalkingplan 沟通计划
     * @return 结果
     */
    @Override
    public int updateSysReTalkingplan(SysReTalkingplan sysReTalkingplan)
    {
        sysReTalkingplan.setUpdateTime(DateUtils.getNowDate());
        return sysReTalkingplanMapper.updateSysReTalkingplan(sysReTalkingplan);
    }

    /**
     * 批量删除沟通计划
     * 
     * @param planIds 需要删除的沟通计划主键
     * @return 结果
     */
    @Override
    public int deleteSysReTalkingplanByPlanIds(String[] planIds)
    {
        return sysReTalkingplanMapper.deleteSysReTalkingplanByPlanIds(planIds);
    }

    /**
     * 删除沟通计划信息
     * 
     * @param planId 沟通计划主键
     * @return 结果
     */
    @Override
    public int deleteSysReTalkingplanByPlanId(String planId)
    {
        return sysReTalkingplanMapper.deleteSysReTalkingplanByPlanId(planId);
    }
}
