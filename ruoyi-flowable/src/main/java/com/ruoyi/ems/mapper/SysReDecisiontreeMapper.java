package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysReDecisiontree;

/**
 * 决策树Mapper接口
 * 
 * @author luohd
 * @date 2023-03-25
 */
public interface SysReDecisiontreeMapper 
{
    /**
     * 查询决策树
     * 
     * @param pyId 决策树主键
     * @return 决策树
     */
    public SysReDecisiontree selectSysReDecisiontreeByPyId(String pyId);

    /**
     * 查询决策树列表
     * 
     * @param sysReDecisiontree 决策树
     * @return 决策树集合
     */
    public List<SysReDecisiontree> selectSysReDecisiontreeList(SysReDecisiontree sysReDecisiontree);

    /**
     * 新增决策树
     * 
     * @param sysReDecisiontree 决策树
     * @return 结果
     */
    public int insertSysReDecisiontree(SysReDecisiontree sysReDecisiontree);

    /**
     * 修改决策树
     * 
     * @param sysReDecisiontree 决策树
     * @return 结果
     */
    public int updateSysReDecisiontree(SysReDecisiontree sysReDecisiontree);

    /**
     * 删除决策树
     * 
     * @param pyId 决策树主键
     * @return 结果
     */
    public int deleteSysReDecisiontreeByPyId(String pyId);

    /**
     * 批量删除决策树
     * 
     * @param pyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReDecisiontreeByPyIds(String[] pyIds);
}
