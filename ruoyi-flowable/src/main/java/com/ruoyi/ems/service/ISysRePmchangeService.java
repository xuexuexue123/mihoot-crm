package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysRePmchange;

/**
 * 项目变更Service接口
 * 
 * @author luohd
 * @date 2023-05-19
 */
public interface ISysRePmchangeService 
{
    /**
     * 查询项目变更
     * 
     * @param changeId 项目变更主键
     * @return 项目变更
     */
    public SysRePmchange selectSysRePmchangeByChangeId(String changeId);

    /**
     * 查询项目变更列表
     * 
     * @param sysRePmchange 项目变更
     * @return 项目变更集合
     */
    public List<SysRePmchange> selectSysRePmchangeList(SysRePmchange sysRePmchange);

    /**
     * 新增项目变更
     * 
     * @param sysRePmchange 项目变更
     * @return 结果
     */
    public int insertSysRePmchange(SysRePmchange sysRePmchange);

    /**
     * 修改项目变更
     * 
     * @param sysRePmchange 项目变更
     * @return 结果
     */
    public int updateSysRePmchange(SysRePmchange sysRePmchange);

    /**
     * 批量删除项目变更
     * 
     * @param changeIds 需要删除的项目变更主键集合
     * @return 结果
     */
    public int deleteSysRePmchangeByChangeIds(String[] changeIds);

    /**
     * 删除项目变更信息
     * 
     * @param changeId 项目变更主键
     * @return 结果
     */
    public int deleteSysRePmchangeByChangeId(String changeId);
}
