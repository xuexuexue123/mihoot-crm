package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReBoard;

/**
 * 项目阶段Service接口
 * 
 * @author luohd
 * @date 2023-04-07
 */
public interface ISysReBoardService 
{
    /**
     * 查询项目阶段
     * 
     * @param pyId 项目阶段主键
     * @return 项目阶段
     */
    public SysReBoard selectSysReBoardByPyId(String pyId);

    /**
     *
     * @param projectId
     * @return
     */
    public SysReBoard selectSysReBoardByProjectId(String projectId);

    /**
     * 查询项目阶段列表
     * 
     * @param sysReBoard 项目阶段
     * @return 项目阶段集合
     */
    public List<SysReBoard> selectSysReBoardList(SysReBoard sysReBoard);

    /**
     *
     * @param sysReBoard
     * @return
     */
    public List<SysReBoard> selectSysReBoardOnlyList(SysReBoard sysReBoard);

    /**
     * 新增项目阶段
     * 
     * @param sysReBoard 项目阶段
     * @return 结果
     */
    public int insertSysReBoard(SysReBoard sysReBoard);

    /**
     * 修改项目阶段
     * 
     * @param sysReBoard 项目阶段
     * @return 结果
     */
    public int updateSysReBoard(SysReBoard sysReBoard);

    /**
     * 批量删除项目阶段
     * 
     * @param pyIds 需要删除的项目阶段主键集合
     * @return 结果
     */
    public int deleteSysReBoardByPyIds(String[] pyIds);

    /**
     * 删除项目阶段信息
     * 
     * @param pyId 项目阶段主键
     * @return 结果
     */
    public int deleteSysReBoardByPyId(String pyId);
}
