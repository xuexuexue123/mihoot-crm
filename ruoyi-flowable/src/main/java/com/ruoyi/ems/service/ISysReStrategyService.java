package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReStrategy;

/**
 * 策略管理Service接口
 * 
 * @author luohd
 * @date 2023-04-01
 */
public interface ISysReStrategyService 
{
    /**
     * 查询策略管理
     * 
     * @param pyId 策略管理主键
     * @return 策略管理
     */
    public SysReStrategy selectSysReStrategyByPyId(String pyId);

    /**
     * 查询策略管理列表
     * 
     * @param sysReStrategy 策略管理
     * @return 策略管理集合
     */
    public List<SysReStrategy> selectSysReStrategyList(SysReStrategy sysReStrategy);

    /**
     * 新增策略管理
     * 
     * @param sysReStrategy 策略管理
     * @return 结果
     */
    public int insertSysReStrategy(SysReStrategy sysReStrategy);

    /**
     * 修改策略管理
     * 
     * @param sysReStrategy 策略管理
     * @return 结果
     */
    public int updateSysReStrategy(SysReStrategy sysReStrategy);

    /**
     * 批量删除策略管理
     * 
     * @param pyIds 需要删除的策略管理主键集合
     * @return 结果
     */
    public int deleteSysReStrategyByPyIds(String[] pyIds);

    /**
     * 删除策略管理信息
     * 
     * @param pyId 策略管理主键
     * @return 结果
     */
    public int deleteSysReStrategyByPyId(String pyId);
}
