package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReApplication;

/**
 * 应用Service接口
 * 
 * @author luohd
 * @date 2023-03-28
 */
public interface ISysReApplicationService 
{
    /**
     * 查询应用
     * 
     * @param pyId 应用主键
     * @return 应用
     */
    public SysReApplication selectSysReApplicationByPyId(String pyId);

    /**
     * 查询应用列表
     * 
     * @param sysReApplication 应用
     * @return 应用集合
     */
    public List<SysReApplication> selectSysReApplicationList(SysReApplication sysReApplication);

    /**
     * 新增应用
     * 
     * @param sysReApplication 应用
     * @return 结果
     */
    public int insertSysReApplication(SysReApplication sysReApplication);

    /**
     * 修改应用
     * 
     * @param sysReApplication 应用
     * @return 结果
     */
    public int updateSysReApplication(SysReApplication sysReApplication);

    /**
     * 批量删除应用
     * 
     * @param pyIds 需要删除的应用主键集合
     * @return 结果
     */
    public int deleteSysReApplicationByPyIds(String[] pyIds);

    /**
     * 删除应用信息
     * 
     * @param pyId 应用主键
     * @return 结果
     */
    public int deleteSysReApplicationByPyId(String pyId);
}
