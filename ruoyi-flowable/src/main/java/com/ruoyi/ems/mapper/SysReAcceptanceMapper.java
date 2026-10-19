package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysReAcceptance;

/**
 * 任务验收Mapper接口
 * 
 * @author luohd
 * @date 2023-05-19
 */
public interface SysReAcceptanceMapper 
{
    /**
     * 查询任务验收
     * 
     * @param acceptId 任务验收主键
     * @return 任务验收
     */
    public SysReAcceptance selectSysReAcceptanceByAcceptId(String acceptId);

    /**
     * 查询任务验收列表
     * 
     * @param sysReAcceptance 任务验收
     * @return 任务验收集合
     */
    public List<SysReAcceptance> selectSysReAcceptanceList(SysReAcceptance sysReAcceptance);

    /**
     * 新增任务验收
     * 
     * @param sysReAcceptance 任务验收
     * @return 结果
     */
    public int insertSysReAcceptance(SysReAcceptance sysReAcceptance);

    /**
     * 修改任务验收
     * 
     * @param sysReAcceptance 任务验收
     * @return 结果
     */
    public int updateSysReAcceptance(SysReAcceptance sysReAcceptance);

    /**
     * 删除任务验收
     * 
     * @param acceptId 任务验收主键
     * @return 结果
     */
    public int deleteSysReAcceptanceByAcceptId(String acceptId);

    /**
     *
     * @param taskIds
     * @return
     */
    public int deleteSysReAcceptanceByTaskIds(String[] taskIds);

    /**
     * 批量删除任务验收
     * 
     * @param acceptIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReAcceptanceByAcceptIds(String[] acceptIds);
}
