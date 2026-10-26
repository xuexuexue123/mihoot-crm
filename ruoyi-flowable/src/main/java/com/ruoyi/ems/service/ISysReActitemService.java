package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReActitem;

/**
 * activity项Service接口
 * 
 * @author luohd
 * @date 2023-03-14
 */
public interface ISysReActitemService 
{
    /**
     * 查询activity项
     * 
     * @param pyId activity项主键
     * @return activity项
     */
    public SysReActitem selectSysReActitemByPyId(String pyId);

    /**
     * 查询activity项列表
     * 
     * @param sysReActitem activity项
     * @return activity项集合
     */
    public List<SysReActitem> selectSysReActitemList(SysReActitem sysReActitem);

    /**
     * 新增activity项
     * 
     * @param sysReActitem activity项
     * @return 结果
     */
    public int insertSysReActitem(SysReActitem sysReActitem);

    /**
     * 修改activity项
     * 
     * @param sysReActitem activity项
     * @return 结果
     */
    public int updateSysReActitem(SysReActitem sysReActitem);

    /**
     * 批量删除activity项
     * 
     * @param pyIds 需要删除的activity项主键集合
     * @return 结果
     */
    public int deleteSysReActitemByPyIds(String[] pyIds);

    /**
     * 删除activity项信息
     * 
     * @param pyId activity项主键
     * @return 结果
     */
    public int deleteSysReActitemByPyId(String pyId);

    /**
     * 删除activity项信息
     *
     * @param actId activity id
     * @return 结果
     */
    public int deleteSysReActitemByActId(String actId);

    /**
     * 批量新增字段
     *
     * @param sysReActitemList 字段列表
     * @return 结果
     */
    public int batchSysReActitemList(List<SysReActitem> sysReActitemList);
}
