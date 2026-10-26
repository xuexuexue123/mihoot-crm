package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReAcceptanceMapper;
import com.ruoyi.ems.domain.SysReAcceptance;
import com.ruoyi.ems.service.ISysReAcceptanceService;

/**
 * 任务验收Service业务层处理
 * 
 * @author luohd
 * @date 2023-05-19
 */
@Service
public class SysReAcceptanceServiceImpl implements ISysReAcceptanceService 
{
    @Autowired
    private SysReAcceptanceMapper sysReAcceptanceMapper;

    /**
     * 查询任务验收
     * 
     * @param acceptId 任务验收主键
     * @return 任务验收
     */
    @Override
    public SysReAcceptance selectSysReAcceptanceByAcceptId(String acceptId)
    {
        return sysReAcceptanceMapper.selectSysReAcceptanceByAcceptId(acceptId);
    }

    /**
     * 查询任务验收列表
     * 
     * @param sysReAcceptance 任务验收
     * @return 任务验收
     */
    @Override
    public List<SysReAcceptance> selectSysReAcceptanceList(SysReAcceptance sysReAcceptance)
    {
        return sysReAcceptanceMapper.selectSysReAcceptanceList(sysReAcceptance);
    }

    /**
     * 新增任务验收
     * 
     * @param sysReAcceptance 任务验收
     * @return 结果
     */
    @Override
    public int insertSysReAcceptance(SysReAcceptance sysReAcceptance)
    {
        if (sysReAcceptance.getCreateTime() == null) {

            sysReAcceptance.setCreateTime(DateUtils.getNowDate());
        }
        return sysReAcceptanceMapper.insertSysReAcceptance(sysReAcceptance);
    }

    /**
     * 修改任务验收
     * 
     * @param sysReAcceptance 任务验收
     * @return 结果
     */
    @Override
    public int updateSysReAcceptance(SysReAcceptance sysReAcceptance)
    {
        sysReAcceptance.setUpdateTime(DateUtils.getNowDate());
        return sysReAcceptanceMapper.updateSysReAcceptance(sysReAcceptance);
    }

    /**
     * 批量删除任务验收
     * 
     * @param acceptIds 需要删除的任务验收主键
     * @return 结果
     */
    @Override
    public int deleteSysReAcceptanceByAcceptIds(String[] acceptIds)
    {
        return sysReAcceptanceMapper.deleteSysReAcceptanceByAcceptIds(acceptIds);
    }

    /**
     * 删除任务验收信息
     * 
     * @param acceptId 任务验收主键
     * @return 结果
     */
    @Override
    public int deleteSysReAcceptanceByAcceptId(String acceptId)
    {
        return sysReAcceptanceMapper.deleteSysReAcceptanceByAcceptId(acceptId);
    }

    /**
     *
     * @param taskIds
     * @return
     */
    @Override
    public int deleteSysReAcceptanceByTaskIds(String[] taskIds) {
        return sysReAcceptanceMapper.deleteSysReAcceptanceByTaskIds(taskIds);
    }
}
