package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysAudittrail;

/**
 * case历史Mapper接口
 * 
 * @author luohd
 * @date 2023-01-06
 */
public interface SysAudittrailMapper 
{
    /**
     * 查询case历史
     * 
     * @param id case历史主键
     * @return case历史
     */
    public SysAudittrail selectSysAudittrailById(String id);

    /**
     *
     * @param executionId
     * @return
     */
    public String getCaseIdByExecutionId(String executionId);
    /**
     *
     * @param pyCaseId
     * @return
     */
    public List<SysAudittrail> selectSysAudittrailByCaseStatus(String pyCaseId);

    /**
     *
     * @param pyCaseId
     * @return
     */
    public List<SysAudittrail> selectSysAudittrailByCaseId(String pyCaseId);

    /**
     * 查询case历史列表
     * 
     * @param sysAudittrail case历史
     * @return case历史集合
     */
    public List<SysAudittrail> selectSysAudittrailList(SysAudittrail sysAudittrail);

    /**
     * 新增case历史
     * 
     * @param sysAudittrail case历史
     * @return 结果
     */
    public int insertSysAudittrail(SysAudittrail sysAudittrail);

    /**
     * 修改case历史
     * 
     * @param sysAudittrail case历史
     * @return 结果
     */
    public int updateSysAudittrail(SysAudittrail sysAudittrail);

    /**
     * 删除case历史
     * 
     * @param id case历史主键
     * @return 结果
     */
    public int deleteSysAudittrailById(String id);

    /**
     * 批量删除case历史
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysAudittrailByIds(String[] ids);
}
