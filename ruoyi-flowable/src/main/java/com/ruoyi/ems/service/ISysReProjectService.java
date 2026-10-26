package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReProject;

/**
 * 项目Service接口
 * 
 * @author luohd
 * @date 2023-04-07
 */
public interface ISysReProjectService 
{
    /**
     * 查询项目
     * 
     * @param pyId 项目主键
     * @return 项目
     */
    public SysReProject selectSysReProjectByPyId(String pyId);

    /**
     * 查询项目列表
     * 
     * @param sysReProject 项目
     * @return 项目集合
     */
    public List<SysReProject> selectSysReProjectList(SysReProject sysReProject);


    /**
     * 根据当前人员，查询项目列表
     *
     * @param partName 当前人员名称
     * @return 项目集合
     */
    public List<SysReProject> selectSysReProjectListByPartName(String partName);

    /**
     * 新增项目
     * 
     * @param sysReProject 项目
     * @return 结果
     */
    public int insertSysReProject(SysReProject sysReProject);

    /**
     * 修改项目
     * 
     * @param sysReProject 项目
     * @return 结果
     */
    public int updateSysReProject(SysReProject sysReProject);

    /**
     * 批量删除项目
     * 
     * @param pyIds 需要删除的项目主键集合
     * @return 结果
     */
    public int deleteSysReProjectByPyIds(String[] pyIds);

    /**
     * 删除项目信息
     * 
     * @param pyId 项目主键
     * @return 结果
     */
    public int deleteSysReProjectByPyId(String pyId);

    public int deleteSysReBoardByPyId(String pyId);

    public int deleteSysReWorktaskByPyId(String pyId);

    public int deleteSysReAcceptanceByPyId(String pyId);

    public int deleteSysRePmriskByPyId(String pyId);

    public int deleteSysRePmchangeByPyId(String pyId);

    public int deleteSysReParticipantByPyId(String pyId);

    public int deleteSysReTalkingplanByPyId(String pyId);

    public int deleteSysRePmlogByPyId(String pyId);

    public int deleteSysCorresByPyId(String pyId);
}
