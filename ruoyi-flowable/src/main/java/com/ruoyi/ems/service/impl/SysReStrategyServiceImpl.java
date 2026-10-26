package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReStrategyMapper;
import com.ruoyi.ems.domain.SysReStrategy;
import com.ruoyi.ems.service.ISysReStrategyService;

/**
 * 策略管理Service业务层处理
 * 
 * @author luohd
 * @date 2023-04-01
 */
@Service
public class SysReStrategyServiceImpl implements ISysReStrategyService 
{
    @Autowired
    private SysReStrategyMapper sysReStrategyMapper;

    /**
     * 查询策略管理
     * 
     * @param pyId 策略管理主键
     * @return 策略管理
     */
    @Override
    public SysReStrategy selectSysReStrategyByPyId(String pyId)
    {
        return sysReStrategyMapper.selectSysReStrategyByPyId(pyId);
    }

    /**
     * 查询策略管理列表
     * 
     * @param sysReStrategy 策略管理
     * @return 策略管理
     */
    @Override
    public List<SysReStrategy> selectSysReStrategyList(SysReStrategy sysReStrategy)
    {
        return sysReStrategyMapper.selectSysReStrategyList(sysReStrategy);
    }

    /**
     * 新增策略管理
     * 
     * @param sysReStrategy 策略管理
     * @return 结果
     */
    @Override
    public int insertSysReStrategy(SysReStrategy sysReStrategy)
    {
        sysReStrategy.setCreateTime(DateUtils.getNowDate());
        return sysReStrategyMapper.insertSysReStrategy(sysReStrategy);
    }

    /**
     * 修改策略管理
     * 
     * @param sysReStrategy 策略管理
     * @return 结果
     */
    @Override
    public int updateSysReStrategy(SysReStrategy sysReStrategy)
    {
        sysReStrategy.setUpdateTime(DateUtils.getNowDate());
        return sysReStrategyMapper.updateSysReStrategy(sysReStrategy);
    }

    /**
     * 批量删除策略管理
     * 
     * @param pyIds 需要删除的策略管理主键
     * @return 结果
     */
    @Override
    public int deleteSysReStrategyByPyIds(String[] pyIds)
    {
        return sysReStrategyMapper.deleteSysReStrategyByPyIds(pyIds);
    }

    /**
     * 删除策略管理信息
     * 
     * @param pyId 策略管理主键
     * @return 结果
     */
    @Override
    public int deleteSysReStrategyByPyId(String pyId)
    {
        return sysReStrategyMapper.deleteSysReStrategyByPyId(pyId);
    }
}
