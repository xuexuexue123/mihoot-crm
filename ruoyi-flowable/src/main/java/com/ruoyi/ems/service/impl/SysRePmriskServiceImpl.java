package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysRePmriskMapper;
import com.ruoyi.ems.domain.SysRePmrisk;
import com.ruoyi.ems.service.ISysRePmriskService;

/**
 * 项目风险Service业务层处理
 * 
 * @author luohd
 * @date 2023-05-19
 */
@Service
public class SysRePmriskServiceImpl implements ISysRePmriskService 
{
    @Autowired
    private SysRePmriskMapper sysRePmriskMapper;

    /**
     * 查询项目风险
     * 
     * @param riskId 项目风险主键
     * @return 项目风险
     */
    @Override
    public SysRePmrisk selectSysRePmriskByRiskId(String riskId)
    {
        return sysRePmriskMapper.selectSysRePmriskByRiskId(riskId);
    }

    /**
     * 查询项目风险列表
     * 
     * @param sysRePmrisk 项目风险
     * @return 项目风险
     */
    @Override
    public List<SysRePmrisk> selectSysRePmriskList(SysRePmrisk sysRePmrisk)
    {
        return sysRePmriskMapper.selectSysRePmriskList(sysRePmrisk);
    }

    /**
     * 新增项目风险
     * 
     * @param sysRePmrisk 项目风险
     * @return 结果
     */
    @Override
    public int insertSysRePmrisk(SysRePmrisk sysRePmrisk)
    {
        sysRePmrisk.setCreateTime(DateUtils.getNowDate());
        return sysRePmriskMapper.insertSysRePmrisk(sysRePmrisk);
    }

    /**
     * 修改项目风险
     * 
     * @param sysRePmrisk 项目风险
     * @return 结果
     */
    @Override
    public int updateSysRePmrisk(SysRePmrisk sysRePmrisk)
    {
        sysRePmrisk.setUpdateTime(DateUtils.getNowDate());
        return sysRePmriskMapper.updateSysRePmrisk(sysRePmrisk);
    }

    /**
     * 批量删除项目风险
     * 
     * @param riskIds 需要删除的项目风险主键
     * @return 结果
     */
    @Override
    public int deleteSysRePmriskByRiskIds(String[] riskIds)
    {
        return sysRePmriskMapper.deleteSysRePmriskByRiskIds(riskIds);
    }

    /**
     * 删除项目风险信息
     * 
     * @param riskId 项目风险主键
     * @return 结果
     */
    @Override
    public int deleteSysRePmriskByRiskId(String riskId)
    {
        return sysRePmriskMapper.deleteSysRePmriskByRiskId(riskId);
    }
}
