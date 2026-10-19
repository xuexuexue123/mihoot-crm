package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysReActivity;

/**
 * activityMapper接口
 * 
 * @author luohd
 * @date 2023-03-13
 */
public interface SysReActivityMapper 
{
    /**
     * 查询activity
     * 
     * @param pyId activity主键
     * @return activity
     */
    public SysReActivity selectSysReActivityByPyId(String pyId);

    /**
     * 查询activity列表
     * 
     * @param sysReActivity activity
     * @return activity集合
     */
    public List<SysReActivity> selectSysReActivityList(SysReActivity sysReActivity);

    /**
     * 新增activity
     * 
     * @param sysReActivity activity
     * @return 结果
     */
    public int insertSysReActivity(SysReActivity sysReActivity);

    /**
     * 修改activity
     * 
     * @param sysReActivity activity
     * @return 结果
     */
    public int updateSysReActivity(SysReActivity sysReActivity);

    /**
     * 删除activity
     * 
     * @param pyId activity主键
     * @return 结果
     */
    public int deleteSysReActivityByPyId(String pyId);

    /**
     * 批量删除activity
     * 
     * @param pyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReActivityByPyIds(String[] pyIds);
}
