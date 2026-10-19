package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReDecisiontableMapper;
import com.ruoyi.ems.domain.SysReDecisiontable;
import com.ruoyi.ems.service.ISysReDecisiontableService;

/**
 * 决策表Service业务层处理
 * 
 * @author luohd
 * @date 2023-03-30
 */
@Service
public class SysReDecisiontableServiceImpl implements ISysReDecisiontableService 
{
    @Autowired
    private SysReDecisiontableMapper sysReDecisiontableMapper;

    /**
     * 查询决策表
     * 
     * @param pyId 决策表主键
     * @return 决策表
     */
    @Override
    public SysReDecisiontable selectSysReDecisiontableByPyId(String pyId)
    {
        return sysReDecisiontableMapper.selectSysReDecisiontableByPyId(pyId);
    }

    /**
     * 查询决策表列表
     * 
     * @param sysReDecisiontable 决策表
     * @return 决策表
     */
    @Override
    public List<SysReDecisiontable> selectSysReDecisiontableList(SysReDecisiontable sysReDecisiontable)
    {
        return sysReDecisiontableMapper.selectSysReDecisiontableList(sysReDecisiontable);
    }

    /**
     * 新增决策表
     * 
     * @param sysReDecisiontable 决策表
     * @return 结果
     */
    @Override
    public int insertSysReDecisiontable(SysReDecisiontable sysReDecisiontable)
    {
        sysReDecisiontable.setCreateTime(DateUtils.getNowDate());
        return sysReDecisiontableMapper.insertSysReDecisiontable(sysReDecisiontable);
    }

    /**
     * 修改决策表
     * 
     * @param sysReDecisiontable 决策表
     * @return 结果
     */
    @Override
    public int updateSysReDecisiontable(SysReDecisiontable sysReDecisiontable)
    {
        sysReDecisiontable.setUpdateTime(DateUtils.getNowDate());
        return sysReDecisiontableMapper.updateSysReDecisiontable(sysReDecisiontable);
    }

    /**
     * 批量删除决策表
     * 
     * @param pyIds 需要删除的决策表主键
     * @return 结果
     */
    @Override
    public int deleteSysReDecisiontableByPyIds(String[] pyIds)
    {
        return sysReDecisiontableMapper.deleteSysReDecisiontableByPyIds(pyIds);
    }

    /**
     * 删除决策表信息
     * 
     * @param pyId 决策表主键
     * @return 结果
     */
    @Override
    public int deleteSysReDecisiontableByPyId(String pyId)
    {
        return sysReDecisiontableMapper.deleteSysReDecisiontableByPyId(pyId);
    }
}
