package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysReRuleset;

/**
 * 规则集Mapper接口
 * 
 * @author luohd
 * @date 2023-03-25
 */
public interface SysReRulesetMapper 
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
     * 删除规则集
     * 
     * @param pyId 规则集主键
     * @return 结果
     */
    public int deleteSysReRulesetByPyId(Long pyId);

    /**
     * 批量删除规则集
     * 
     * @param pyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReRulesetByPyIds(Long[] pyIds);
}
