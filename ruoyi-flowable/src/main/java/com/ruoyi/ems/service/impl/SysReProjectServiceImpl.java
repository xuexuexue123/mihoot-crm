package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReProjectMapper;
import com.ruoyi.ems.domain.SysReProject;
import com.ruoyi.ems.service.ISysReProjectService;

/**
 * 项目Service业务层处理
 * 
 * @author luohd
 * @date 2023-04-07
 */
@Service
public class SysReProjectServiceImpl implements ISysReProjectService 
{
    @Autowired
    private SysReProjectMapper sysReProjectMapper;

    /**
     * 查询项目
     * 
     * @param pyId 项目主键
     * @return 项目
     */
    @Override
    public SysReProject selectSysReProjectByPyId(String pyId)
    {
        return sysReProjectMapper.selectSysReProjectByPyId(pyId);
    }

    /**
     * 查询项目列表
     * 
     * @param sysReProject 项目
     * @return 项目
     */
    @Override
    public List<SysReProject> selectSysReProjectList(SysReProject sysReProject)
    {
        return sysReProjectMapper.selectSysReProjectList(sysReProject);
    }

    /**
     * 查询项目列表
     *
     * @param partName 成员名称
     * @return 项目
     */
    @Override
    public List<SysReProject> selectSysReProjectListByPartName(String partName)
    {
        return sysReProjectMapper.selectSysReProjectListByPartName(partName);
    }

    /**
     * 新增项目
     * 
     * @param sysReProject 项目
     * @return 结果
     */
    @Override
    public int insertSysReProject(SysReProject sysReProject)
    {
        sysReProject.setCreateTime(DateUtils.getNowDate());
        return sysReProjectMapper.insertSysReProject(sysReProject);
    }

    /**
     * 修改项目
     * 
     * @param sysReProject 项目
     * @return 结果
     */
    @Override
    public int updateSysReProject(SysReProject sysReProject)
    {
        sysReProject.setUpdateTime(DateUtils.getNowDate());
        return sysReProjectMapper.updateSysReProject(sysReProject);
    }

    /**
     * 批量删除项目
     * 
     * @param pyIds 需要删除的项目主键
     * @return 结果
     */
    @Override
    public int deleteSysReProjectByPyIds(String[] pyIds)
    {
        return sysReProjectMapper.deleteSysReProjectByPyIds(pyIds);
    }

    /**
     * 删除项目信息
     * 
     * @param pyId 项目主键
     * @return 结果
     */
    @Override
    public int deleteSysReProjectByPyId(String pyId)
    {
        return sysReProjectMapper.deleteSysReProjectByPyId(pyId);
    }

    @Override
    public int deleteSysReBoardByPyId(String pyId) {
        return sysReProjectMapper.deleteSysReBoardByPyId(pyId);
    }

    @Override
    public int deleteSysReWorktaskByPyId(String pyId) {
        return sysReProjectMapper.deleteSysReWorktaskByPyId(pyId);
    }

    @Override
    public int deleteSysReAcceptanceByPyId(String pyId) {
        return sysReProjectMapper.deleteSysReAcceptanceByPyId(pyId);
    }

    @Override
    public int deleteSysRePmriskByPyId(String pyId) {
        return sysReProjectMapper.deleteSysRePmriskByPyId(pyId);
    }

    @Override
    public int deleteSysRePmchangeByPyId(String pyId) {
        return sysReProjectMapper.deleteSysRePmchangeByPyId(pyId);
    }

    @Override
    public int deleteSysReParticipantByPyId(String pyId) {
        return sysReProjectMapper.deleteSysReParticipantByPyId(pyId);
    }

    @Override
    public int deleteSysReTalkingplanByPyId(String pyId) {
        return sysReProjectMapper.deleteSysReTalkingplanByPyId(pyId);
    }

    @Override
    public int deleteSysRePmlogByPyId(String pyId) {
        return sysReProjectMapper.deleteSysRePmlogByPyId(pyId);
    }

    @Override
    public int deleteSysCorresByPyId(String pyId) {
        return sysReProjectMapper.deleteSysCorresByPyId(pyId);
    }
}
