package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReDatafield;

/**
 * 字段Service接口
 * 
 * @author luohd
 * @date 2023-04-19
 */
public interface ISysReDatafieldService 
{
    /**
     * 查询字段
     * 
     * @param fieldId 字段主键
     * @return 字段
     */
    public SysReDatafield selectSysReDatafieldByFieldId(String fieldId);

    /**
     * 查询字段列表
     * 
     * @param sysReDatafield 字段
     * @return 字段集合
     */
    public List<SysReDatafield> selectSysReDatafieldList(SysReDatafield sysReDatafield);

    /**
     * 新增字段
     * 
     * @param sysReDatafield 字段
     * @return 结果
     */
    public int insertSysReDatafield(SysReDatafield sysReDatafield);

    /**
     * 修改字段
     * 
     * @param sysReDatafield 字段
     * @return 结果
     */
    public int updateSysReDatafield(SysReDatafield sysReDatafield);

    /**
     * 批量删除字段
     * 
     * @param fieldIds 需要删除的字段主键集合
     * @return 结果
     */
    public int deleteSysReDatafieldByFieldIds(String[] fieldIds);

    /**
     * 删除字段信息
     * 
     * @param fieldId 字段主键
     * @return 结果
     */
    public int deleteSysReDatafieldByFieldId(String fieldId);

    /**
     * 批量插入数据表字段
     * @param sysReDatafieldList
     * @return
     */
    public int batchInsertSysReDatafieldList(List<SysReDatafield> sysReDatafieldList);
}
