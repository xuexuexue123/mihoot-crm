package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReWhenMapper;
import com.ruoyi.ems.domain.SysReWhen;
import com.ruoyi.ems.service.ISysReWhenService;

/**
 * WhenRuleService业务层处理
 * 
 * @author luohd
 * @date 2023-04-02
 */
@Service
public class SysReWhenServiceImpl implements ISysReWhenService 
{
    @Autowired
    private SysReWhenMapper sysReWhenMapper;

    /**
     * 查询WhenRule
     * 
     * @param pyId WhenRule主键
     * @return WhenRule
     */
    @Override
    public SysReWhen selectSysReWhenByPyId(String pyId)
    {
        return sysReWhenMapper.selectSysReWhenByPyId(pyId);
    }

    /**
     * 查询WhenRule列表
     * 
     * @param sysReWhen WhenRule
     * @return WhenRule
     */
    @Override
    public List<SysReWhen> selectSysReWhenList(SysReWhen sysReWhen)
    {
        return sysReWhenMapper.selectSysReWhenList(sysReWhen);
    }

    /**
     * 新增WhenRule
     * 
     * @param sysReWhen WhenRule
     * @return 结果
     */
    @Override
    public int insertSysReWhen(SysReWhen sysReWhen)
    {
        sysReWhen.setCreateTime(DateUtils.getNowDate());
        return sysReWhenMapper.insertSysReWhen(sysReWhen);
    }

    /**
     * 修改WhenRule
     * 
     * @param sysReWhen WhenRule
     * @return 结果
     */
    @Override
    public int updateSysReWhen(SysReWhen sysReWhen)
    {
        sysReWhen.setUpdateTime(DateUtils.getNowDate());
        return sysReWhenMapper.updateSysReWhen(sysReWhen);
    }

    /**
     * 批量删除WhenRule
     * 
     * @param pyIds 需要删除的WhenRule主键
     * @return 结果
     */
    @Override
    public int deleteSysReWhenByPyIds(String[] pyIds)
    {
        return sysReWhenMapper.deleteSysReWhenByPyIds(pyIds);
    }

    /**
     * 删除WhenRule信息
     * 
     * @param pyId WhenRule主键
     * @return 结果
     */
    @Override
    public int deleteSysReWhenByPyId(String pyId)
    {
        return sysReWhenMapper.deleteSysReWhenByPyId(pyId);
    }
}
