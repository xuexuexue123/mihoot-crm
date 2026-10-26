package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysRePmchangeMapper;
import com.ruoyi.ems.domain.SysRePmchange;
import com.ruoyi.ems.service.ISysRePmchangeService;

/**
 * 项目变更Service业务层处理
 * 
 * @author luohd
 * @date 2023-05-19
 */
@Service
public class SysRePmchangeServiceImpl implements ISysRePmchangeService 
{
    @Autowired
    private SysRePmchangeMapper sysRePmchangeMapper;

    /**
     * 查询项目变更
     * 
     * @param changeId 项目变更主键
     * @return 项目变更
     */
    @Override
    public SysRePmchange selectSysRePmchangeByChangeId(String changeId)
    {
        return sysRePmchangeMapper.selectSysRePmchangeByChangeId(changeId);
    }

    /**
     * 查询项目变更列表
     * 
     * @param sysRePmchange 项目变更
     * @return 项目变更
     */
    @Override
    public List<SysRePmchange> selectSysRePmchangeList(SysRePmchange sysRePmchange)
    {
        return sysRePmchangeMapper.selectSysRePmchangeList(sysRePmchange);
    }

    /**
     * 新增项目变更
     * 
     * @param sysRePmchange 项目变更
     * @return 结果
     */
    @Override
    public int insertSysRePmchange(SysRePmchange sysRePmchange)
    {
        sysRePmchange.setCreateTime(DateUtils.getNowDate());
        return sysRePmchangeMapper.insertSysRePmchange(sysRePmchange);
    }

    /**
     * 修改项目变更
     * 
     * @param sysRePmchange 项目变更
     * @return 结果
     */
    @Override
    public int updateSysRePmchange(SysRePmchange sysRePmchange)
    {
        sysRePmchange.setUpdateTime(DateUtils.getNowDate());
        return sysRePmchangeMapper.updateSysRePmchange(sysRePmchange);
    }

    /**
     * 批量删除项目变更
     * 
     * @param changeIds 需要删除的项目变更主键
     * @return 结果
     */
    @Override
    public int deleteSysRePmchangeByChangeIds(String[] changeIds)
    {
        return sysRePmchangeMapper.deleteSysRePmchangeByChangeIds(changeIds);
    }

    /**
     * 删除项目变更信息
     * 
     * @param changeId 项目变更主键
     * @return 结果
     */
    @Override
    public int deleteSysRePmchangeByChangeId(String changeId)
    {
        return sysRePmchangeMapper.deleteSysRePmchangeByChangeId(changeId);
    }
}
