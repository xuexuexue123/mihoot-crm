package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysRePmrisk;

/**
 * 项目风险Mapper接口
 * 
 * @author luohd
 * @date 2023-05-19
 */
public interface SysRePmriskMapper 
{
    /**
     * 查询项目风险
     * 
     * @param riskId 项目风险主键
     * @return 项目风险
     */
    public SysRePmrisk selectSysRePmriskByRiskId(String riskId);

    /**
     * 查询项目风险列表
     * 
     * @param sysRePmrisk 项目风险
     * @return 项目风险集合
     */
    public List<SysRePmrisk> selectSysRePmriskList(SysRePmrisk sysRePmrisk);

    /**
     * 新增项目风险
     * 
     * @param sysRePmrisk 项目风险
     * @return 结果
     */
    public int insertSysRePmrisk(SysRePmrisk sysRePmrisk);

    /**
     * 修改项目风险
     * 
     * @param sysRePmrisk 项目风险
     * @return 结果
     */
    public int updateSysRePmrisk(SysRePmrisk sysRePmrisk);

    /**
     * 删除项目风险
     * 
     * @param riskId 项目风险主键
     * @return 结果
     */
    public int deleteSysRePmriskByRiskId(String riskId);

    /**
     * 批量删除项目风险
     * 
     * @param riskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysRePmriskByRiskIds(String[] riskIds);
}
