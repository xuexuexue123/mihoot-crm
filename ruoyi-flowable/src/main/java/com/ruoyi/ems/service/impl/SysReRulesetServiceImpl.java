package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReRulesetMapper;
import com.ruoyi.ems.domain.SysReRuleset;
import com.ruoyi.ems.service.ISysReRulesetService;

/**
 * 规则集Service业务层处理
 * 
 * @author luohd
 * @date 2023-03-25
 */
@Service
public class SysReRulesetServiceImpl implements ISysReRulesetService 
{
    @Autowired
    private SysReRulesetMapper sysReRulesetMapper;

    /**
     * 查询规则集
     * 
     * @param pyId 规则集主键
     * @return 规则集
     */
    @Override
    public SysReRuleset selectSysReRulesetByPyId(Long pyId)
    {
        return sysReRulesetMapper.selectSysReRulesetByPyId(pyId);
    }

    /**
     * 查询规则集列表
     * 
     * @param sysReRuleset 规则集
     * @return 规则集
     */
    @Override
    public List<SysReRuleset> selectSysReRulesetList(SysReRuleset sysReRuleset)
    {
        return sysReRulesetMapper.selectSysReRulesetList(sysReRuleset);
    }

    /**
     * 新增规则集
     * 
     * @param sysReRuleset 规则集
     * @return 结果
     */
    @Override
    public int insertSysReRuleset(SysReRuleset sysReRuleset)
    {
        sysReRuleset.setCreateTime(DateUtils.getNowDate());
        return sysReRulesetMapper.insertSysReRuleset(sysReRuleset);
    }

    /**
     * 修改规则集
     * 
     * @param sysReRuleset 规则集
     * @return 结果
     */
    @Override
    public int updateSysReRuleset(SysReRuleset sysReRuleset)
    {
        sysReRuleset.setUpdateTime(DateUtils.getNowDate());
        return sysReRulesetMapper.updateSysReRuleset(sysReRuleset);
    }

    /**
     * 批量删除规则集
     * 
     * @param pyIds 需要删除的规则集主键
     * @return 结果
     */
    @Override
    public int deleteSysReRulesetByPyIds(Long[] pyIds)
    {
        return sysReRulesetMapper.deleteSysReRulesetByPyIds(pyIds);
    }

    /**
     * 删除规则集信息
     * 
     * @param pyId 规则集主键
     * @return 结果
     */
    @Override
    public int deleteSysReRulesetByPyId(Long pyId)
    {
        return sysReRulesetMapper.deleteSysReRulesetByPyId(pyId);
    }
}
