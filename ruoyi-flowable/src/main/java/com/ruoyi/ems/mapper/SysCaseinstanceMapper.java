package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysCaseinstance;

/**
 * case实例Mapper接口
 * 
 * @author luohd
 * @date 2022-12-29
 */
public interface SysCaseinstanceMapper 
{
    /**
     * 查询case实例
     * 
     * @param id case实例主键
     * @return case实例
     */
    public SysCaseinstance selectSysCaseinstanceById(Long id);

    /**
     * 查询case 实例 by caseId
     * @param pyCaseId
     * @return
     */
    public SysCaseinstance selectSysCaseinstanceByCaseId(String pyCaseId);

    /**
     * 查询case实例列表
     * 
     * @param sysCaseinstance case实例
     * @return case实例集合
     */
    public List<SysCaseinstance> selectSysCaseinstanceList(SysCaseinstance sysCaseinstance);

    /**
     * 新增case实例
     * 
     * @param sysCaseinstance case实例
     * @return 结果
     */
    public int insertSysCaseinstance(SysCaseinstance sysCaseinstance);

    /**
     * 修改case实例
     * 
     * @param sysCaseinstance case实例
     * @return 结果
     */
    public int updateSysCaseinstance(SysCaseinstance sysCaseinstance);

    /**
     * 删除case实例
     * 
     * @param id case实例主键
     * @return 结果
     */
    public int deleteSysCaseinstanceById(Long id);

    /**
     * 批量删除case实例
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysCaseinstanceByIds(Long[] ids);
}
