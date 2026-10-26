package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysAudittrailMapper;
import com.ruoyi.ems.domain.SysAudittrail;
import com.ruoyi.ems.service.ISysAudittrailService;

/**
 * case历史Service业务层处理
 * 
 * @author luohd
 * @date 2023-01-06
 */
@Service
public class SysAudittrailServiceImpl implements ISysAudittrailService 
{
    @Autowired
    private SysAudittrailMapper sysAudittrailMapper;

    /**
     * 查询case历史
     * 
     * @param id case历史主键
     * @return case历史
     */
    @Override
    public SysAudittrail selectSysAudittrailById(String id)
    {
        return sysAudittrailMapper.selectSysAudittrailById(id);
    }

    /**
     *
     * @param executionId
     * @return
     */
    @Override
    public String getCaseIdByExecutionId(String executionId) {
        return sysAudittrailMapper.getCaseIdByExecutionId(executionId);
    }

    @Override
    public List<SysAudittrail> selectSysAudittrailByCaseStatus(String pyCaseId) {
        return sysAudittrailMapper.selectSysAudittrailByCaseStatus(pyCaseId);

    }


    @Override
    public List<SysAudittrail> selectSysAudittrailByCaseId(String pyCaseId) {
        return sysAudittrailMapper.selectSysAudittrailByCaseId(pyCaseId);

    }

    /**
     * 查询case历史列表
     * 
     * @param sysAudittrail case历史
     * @return case历史
     */
    @Override
    public List<SysAudittrail> selectSysAudittrailList(SysAudittrail sysAudittrail)
    {
        return sysAudittrailMapper.selectSysAudittrailList(sysAudittrail);
    }

    /**
     * 新增case历史
     * 
     * @param sysAudittrail case历史
     * @return 结果
     */
    @Override
    public int insertSysAudittrail(SysAudittrail sysAudittrail)
    {
        sysAudittrail.setCreateTime(DateUtils.getNowDate());
        sysAudittrail.setId(UUID.randomUUID().toString());
        return sysAudittrailMapper.insertSysAudittrail(sysAudittrail);
    }

    /**
     * 修改case历史
     * 
     * @param sysAudittrail case历史
     * @return 结果
     */
    @Override
    public int updateSysAudittrail(SysAudittrail sysAudittrail)
    {
        return sysAudittrailMapper.updateSysAudittrail(sysAudittrail);
    }

    /**
     * 批量删除case历史
     * 
     * @param ids 需要删除的case历史主键
     * @return 结果
     */
    @Override
    public int deleteSysAudittrailByIds(String[] ids)
    {
        return sysAudittrailMapper.deleteSysAudittrailByIds(ids);
    }

    /**
     * 删除case历史信息
     * 
     * @param id case历史主键
     * @return 结果
     */
    @Override
    public int deleteSysAudittrailById(String id)
    {
        return sysAudittrailMapper.deleteSysAudittrailById(id);
    }
}
