package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReDecisiontreeMapper;
import com.ruoyi.ems.domain.SysReDecisiontree;
import com.ruoyi.ems.service.ISysReDecisiontreeService;

/**
 * 决策树Service业务层处理
 * 
 * @author luohd
 * @date 2023-03-25
 */
@Service
public class SysReDecisiontreeServiceImpl implements ISysReDecisiontreeService 
{
    @Autowired
    private SysReDecisiontreeMapper sysReDecisiontreeMapper;

    /**
     * 查询决策树
     * 
     * @param pyId 决策树主键
     * @return 决策树
     */
    @Override
    public SysReDecisiontree selectSysReDecisiontreeByPyId(String pyId)
    {
        return sysReDecisiontreeMapper.selectSysReDecisiontreeByPyId(pyId);
    }

    /**
     * 查询决策树列表
     * 
     * @param sysReDecisiontree 决策树
     * @return 决策树
     */
    @Override
    public List<SysReDecisiontree> selectSysReDecisiontreeList(SysReDecisiontree sysReDecisiontree)
    {
        return sysReDecisiontreeMapper.selectSysReDecisiontreeList(sysReDecisiontree);
    }

    /**
     * 新增决策树
     * 
     * @param sysReDecisiontree 决策树
     * @return 结果
     */
    @Override
    public int insertSysReDecisiontree(SysReDecisiontree sysReDecisiontree)
    {
        sysReDecisiontree.setCreateTime(DateUtils.getNowDate());
        return sysReDecisiontreeMapper.insertSysReDecisiontree(sysReDecisiontree);
    }

    /**
     * 修改决策树
     * 
     * @param sysReDecisiontree 决策树
     * @return 结果
     */
    @Override
    public int updateSysReDecisiontree(SysReDecisiontree sysReDecisiontree)
    {
        sysReDecisiontree.setUpdateTime(DateUtils.getNowDate());
        return sysReDecisiontreeMapper.updateSysReDecisiontree(sysReDecisiontree);
    }

    /**
     * 批量删除决策树
     * 
     * @param pyIds 需要删除的决策树主键
     * @return 结果
     */
    @Override
    public int deleteSysReDecisiontreeByPyIds(String[] pyIds)
    {
        return sysReDecisiontreeMapper.deleteSysReDecisiontreeByPyIds(pyIds);
    }

    /**
     * 删除决策树信息
     * 
     * @param pyId 决策树主键
     * @return 结果
     */
    @Override
    public int deleteSysReDecisiontreeByPyId(String pyId)
    {
        return sysReDecisiontreeMapper.deleteSysReDecisiontreeByPyId(pyId);
    }
}
