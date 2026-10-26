package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReTalkingplan;

/**
 * 沟通计划Service接口
 * 
 * @author luohd
 * @date 2023-05-19
 */
public interface ISysReTalkingplanService 
{
    /**
     * 查询沟通计划
     * 
     * @param planId 沟通计划主键
     * @return 沟通计划
     */
    public SysReTalkingplan selectSysReTalkingplanByPlanId(String planId);

    /**
     * 查询沟通计划列表
     * 
     * @param sysReTalkingplan 沟通计划
     * @return 沟通计划集合
     */
    public List<SysReTalkingplan> selectSysReTalkingplanList(SysReTalkingplan sysReTalkingplan);

    /**
     * 新增沟通计划
     * 
     * @param sysReTalkingplan 沟通计划
     * @return 结果
     */
    public int insertSysReTalkingplan(SysReTalkingplan sysReTalkingplan);

    /**
     * 修改沟通计划
     * 
     * @param sysReTalkingplan 沟通计划
     * @return 结果
     */
    public int updateSysReTalkingplan(SysReTalkingplan sysReTalkingplan);

    /**
     * 批量删除沟通计划
     * 
     * @param planIds 需要删除的沟通计划主键集合
     * @return 结果
     */
    public int deleteSysReTalkingplanByPlanIds(String[] planIds);

    /**
     * 删除沟通计划信息
     * 
     * @param planId 沟通计划主键
     * @return 结果
     */
    public int deleteSysReTalkingplanByPlanId(String planId);
}
