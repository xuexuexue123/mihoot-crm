package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysReDecisiontable;

/**
 * 决策表Mapper接口
 * 
 * @author luohd
 * @date 2023-03-30
 */
public interface SysReDecisiontableMapper 
{
    /**
     * 查询决策表
     * 
     * @param pyId 决策表主键
     * @return 决策表
     */
    public SysReDecisiontable selectSysReDecisiontableByPyId(String pyId);

    /**
     * 查询决策表列表
     * 
     * @param sysReDecisiontable 决策表
     * @return 决策表集合
     */
    public List<SysReDecisiontable> selectSysReDecisiontableList(SysReDecisiontable sysReDecisiontable);

    /**
     * 新增决策表
     * 
     * @param sysReDecisiontable 决策表
     * @return 结果
     */
    public int insertSysReDecisiontable(SysReDecisiontable sysReDecisiontable);

    /**
     * 修改决策表
     * 
     * @param sysReDecisiontable 决策表
     * @return 结果
     */
    public int updateSysReDecisiontable(SysReDecisiontable sysReDecisiontable);

    /**
     * 删除决策表
     * 
     * @param pyId 决策表主键
     * @return 结果
     */
    public int deleteSysReDecisiontableByPyId(String pyId);

    /**
     * 批量删除决策表
     * 
     * @param pyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReDecisiontableByPyIds(String[] pyIds);
}
