package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysReWhen;

/**
 * WhenRuleMapper接口
 * 
 * @author luohd
 * @date 2023-04-02
 */
public interface SysReWhenMapper 
{
    /**
     * 查询WhenRule
     * 
     * @param pyId WhenRule主键
     * @return WhenRule
     */
    public SysReWhen selectSysReWhenByPyId(String pyId);

    /**
     * 查询WhenRule列表
     * 
     * @param sysReWhen WhenRule
     * @return WhenRule集合
     */
    public List<SysReWhen> selectSysReWhenList(SysReWhen sysReWhen);

    /**
     * 新增WhenRule
     * 
     * @param sysReWhen WhenRule
     * @return 结果
     */
    public int insertSysReWhen(SysReWhen sysReWhen);

    /**
     * 修改WhenRule
     * 
     * @param sysReWhen WhenRule
     * @return 结果
     */
    public int updateSysReWhen(SysReWhen sysReWhen);

    /**
     * 删除WhenRule
     * 
     * @param pyId WhenRule主键
     * @return 结果
     */
    public int deleteSysReWhenByPyId(String pyId);

    /**
     * 批量删除WhenRule
     * 
     * @param pyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReWhenByPyIds(String[] pyIds);
}
