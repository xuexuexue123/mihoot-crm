package com.ruoyi.ems.mapper;

import java.util.List;

import com.ruoyi.ems.domain.SysReDatafield;
import com.ruoyi.ems.domain.SysReDtitem;

/**
 * 数据转换项Mapper接口
 * 
 * @author luohd
 * @date 2023-03-09
 */
public interface SysReDtitemMapper 
{
    /**
     * 查询数据转换项
     * 
     * @param pyId 数据转换项主键
     * @return 数据转换项
     */
    public SysReDtitem selectSysReDtitemByPyId(String pyId);

    /**
     * 查询数据转换项列表
     * 
     * @param sysReDtitem 数据转换项
     * @return 数据转换项集合
     */
    public List<SysReDtitem> selectSysReDtitemList(SysReDtitem sysReDtitem);

    /**
     * 新增数据转换项
     * 
     * @param sysReDtitem 数据转换项
     * @return 结果
     */
    public int insertSysReDtitem(SysReDtitem sysReDtitem);

    /**
     * 修改数据转换项
     * 
     * @param sysReDtitem 数据转换项
     * @return 结果
     */
    public int updateSysReDtitem(SysReDtitem sysReDtitem);

    /**
     * 删除数据转换项
     * 
     * @param pyId 数据转换项主键
     * @return 结果
     */
    public int deleteSysReDtitemByPyId(String pyId);


    /**
     * 删除数据转换项dt
     *
     * @param dtId 数据转换项dt
     * @return 结果
     */
    public int deleteSysReDtitemByDtId(Long dtId);


    /**
     * 批量删除数据转换项
     * 
     * @param pyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReDtitemByPyIds(String[] pyIds);

    /**
     * 批量新增字段
     *
     * @param sysReDtitemList 字段列表
     * @return 结果
     */
    public int batchSysReDtitemList(List<SysReDtitem> sysReDtitemList);

}
