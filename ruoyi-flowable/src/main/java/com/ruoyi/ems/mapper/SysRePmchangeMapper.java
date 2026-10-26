package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysRePmchange;

/**
 * 项目变更Mapper接口
 * 
 * @author luohd
 * @date 2023-05-19
 */
public interface SysRePmchangeMapper 
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
     * 删除项目变更
     * 
     * @param changeId 项目变更主键
     * @return 结果
     */
    public int deleteSysRePmchangeByChangeId(String changeId);

    /**
     * 批量删除项目变更
     * 
     * @param changeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysRePmchangeByChangeIds(String[] changeIds);
}
