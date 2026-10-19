package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReActivityMapper;
import com.ruoyi.ems.domain.SysReActivity;
import com.ruoyi.ems.service.ISysReActivityService;

/**
 * activityService业务层处理
 * 
 * @author luohd
 * @date 2023-03-13
 */
@Service
public class SysReActivityServiceImpl implements ISysReActivityService 
{
    @Autowired
    private SysReActivityMapper sysReActivityMapper;

    /**
     * 查询activity
     * 
     * @param pyId activity主键
     * @return activity
     */
    @Override
    public SysReActivity selectSysReActivityByPyId(String pyId)
    {
        return sysReActivityMapper.selectSysReActivityByPyId(pyId);
    }

    /**
     * 查询activity列表
     * 
     * @param sysReActivity activity
     * @return activity
     */
    @Override
    public List<SysReActivity> selectSysReActivityList(SysReActivity sysReActivity)
    {
        return sysReActivityMapper.selectSysReActivityList(sysReActivity);
    }

    /**
     * 新增activity
     * 
     * @param sysReActivity activity
     * @return 结果
     */
    @Override
    public int insertSysReActivity(SysReActivity sysReActivity)
    {
        sysReActivity.setCreateTime(DateUtils.getNowDate());
        return sysReActivityMapper.insertSysReActivity(sysReActivity);
    }

    /**
     * 修改activity
     * 
     * @param sysReActivity activity
     * @return 结果
     */
    @Override
    public int updateSysReActivity(SysReActivity sysReActivity)
    {
        sysReActivity.setUpdateTime(DateUtils.getNowDate());
        return sysReActivityMapper.updateSysReActivity(sysReActivity);
    }

    /**
     * 批量删除activity
     * 
     * @param pyIds 需要删除的activity主键
     * @return 结果
     */
    @Override
    public int deleteSysReActivityByPyIds(String[] pyIds)
    {
        return sysReActivityMapper.deleteSysReActivityByPyIds(pyIds);
    }

    /**
     * 删除activity信息
     * 
     * @param pyId activity主键
     * @return 结果
     */
    @Override
    public int deleteSysReActivityByPyId(String pyId)
    {
        return sysReActivityMapper.deleteSysReActivityByPyId(pyId);
    }
}
