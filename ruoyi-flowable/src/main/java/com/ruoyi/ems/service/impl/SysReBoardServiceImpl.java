package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReBoardMapper;
import com.ruoyi.ems.domain.SysReBoard;
import com.ruoyi.ems.service.ISysReBoardService;

/**
 * 项目阶段Service业务层处理
 * 
 * @author luohd
 * @date 2023-04-07
 */
@Service
public class SysReBoardServiceImpl implements ISysReBoardService 
{
    @Autowired
    private SysReBoardMapper sysReBoardMapper;

    /**
     * 查询项目阶段
     * 
     * @param pyId 项目阶段主键
     * @return 项目阶段
     */
    @Override
    public SysReBoard selectSysReBoardByPyId(String pyId)
    {
        return sysReBoardMapper.selectSysReBoardByPyId(pyId);
    }

    /**
     *
     * @param projectId
     * @return
     */
    @Override
    public SysReBoard selectSysReBoardByProjectId(String projectId) {
        return sysReBoardMapper.selectSysReBoardByProjectId(projectId);
    }

    /**
     * 查询项目阶段列表
     * 
     * @param sysReBoard 项目阶段
     * @return 项目阶段
     */
    @Override
    public List<SysReBoard> selectSysReBoardList(SysReBoard sysReBoard)
    {
        return sysReBoardMapper.selectSysReBoardList(sysReBoard);
    }

    /**
     *
     * @param sysReBoard
     * @return
     */
    @Override
    public List<SysReBoard> selectSysReBoardOnlyList(SysReBoard sysReBoard) {
        return sysReBoardMapper.selectSysReBoardOnlyList(sysReBoard);
    }

    /**
     * 新增项目阶段
     * 
     * @param sysReBoard 项目阶段
     * @return 结果
     */
    @Override
    public int insertSysReBoard(SysReBoard sysReBoard)
    {
        if (sysReBoard.getCreateTime() == null) {
            sysReBoard.setCreateTime(DateUtils.getNowDate());

        }
        return sysReBoardMapper.insertSysReBoard(sysReBoard);
    }

    /**
     * 修改项目阶段
     * 
     * @param sysReBoard 项目阶段
     * @return 结果
     */
    @Override
    public int updateSysReBoard(SysReBoard sysReBoard)
    {
        sysReBoard.setUpdateTime(DateUtils.getNowDate());
        return sysReBoardMapper.updateSysReBoard(sysReBoard);
    }

    /**
     * 批量删除项目阶段
     * 
     * @param pyIds 需要删除的项目阶段主键
     * @return 结果
     */
    @Override
    public int deleteSysReBoardByPyIds(String[] pyIds)
    {
        return sysReBoardMapper.deleteSysReBoardByPyIds(pyIds);
    }

    /**
     * 删除项目阶段信息
     * 
     * @param pyId 项目阶段主键
     * @return 结果
     */
    @Override
    public int deleteSysReBoardByPyId(String pyId)
    {
        return sysReBoardMapper.deleteSysReBoardByPyId(pyId);
    }
}
