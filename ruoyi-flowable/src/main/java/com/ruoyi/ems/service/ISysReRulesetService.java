package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReRuleset;

/**
 * 规则集Service接口
 * 
 * @author luohd
 * @date 2023-03-25
 */
public interface ISysReRulesetService 
{
    /**
     * 查询规则集
     * 
     * @param pyId 规则集主键
     * @return 规则集
     */
    public SysReRuleset selectSysReRulesetByPyId(Long pyId);

    /**
     * 查询规则集列表
     * 
     * @param sysReRuleset 规则集
     * @return 规则集集合
     */
    public List<SysReRuleset> selectSysReRulesetList(SysReRuleset sysReRuleset);

    /**
     * 新增规则集
     * 
     * @param sysReRuleset 规则集
     * @return 结果
     */
    public int insertSysReRuleset(SysReRuleset sysReRuleset);

    /**
     * 修改规则集
     * 
     * @param sysReRuleset 规则集
     * @return 结果
     */
    public int updateSysReRuleset(SysReRuleset sysReRuleset);

    /**
     * 批量删除规则集
     * 
     * @param pyIds 需要删除的规则集主键集合
     * @return 结果
     */
    public int deleteSysReRulesetByPyIds(Long[] pyIds);

    /**
     * 删除规则集信息
     * 
     * @param pyId 规则集主键
     * @return 结果
     */
    public int deleteSysReRulesetByPyId(Long pyId);
}
