package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReWorkloadMapper;
import com.ruoyi.ems.domain.SysReWorkload;
import com.ruoyi.ems.service.ISysReWorkloadService;

/**
 * 工作量Service业务层处理
 * 
 * @author luohd
 * @date 2023-05-22
 */
@Service
public class SysReWorkloadServiceImpl implements ISysReWorkloadService 
{
    @Autowired
    private SysReWorkloadMapper sysReWorkloadMapper;

    /**
     * 查询工作量
     * 
     * @param workloadId 工作量主键
     * @return 工作量
     */
    @Override
    public SysReWorkload selectSysReWorkloadByWorkloadId(String workloadId)
    {
        return sysReWorkloadMapper.selectSysReWorkloadByWorkloadId(workloadId);
    }

    /**
     * 查询工作量列表
     * 
     * @param sysReWorkload 工作量
     * @return 工作量
     */
    @Override
    public List<SysReWorkload> selectSysReWorkloadList(SysReWorkload sysReWorkload)
    {
        return sysReWorkloadMapper.selectSysReWorkloadList(sysReWorkload);
    }

    /**
     * 新增工作量
     * 
     * @param sysReWorkload 工作量
     * @return 结果
     */
    @Override
    public int insertSysReWorkload(SysReWorkload sysReWorkload)
    {
        sysReWorkload.setCreateTime(DateUtils.getNowDate());
        return sysReWorkloadMapper.insertSysReWorkload(sysReWorkload);
    }

    /**
     * 修改工作量
     * 
     * @param sysReWorkload 工作量
     * @return 结果
     */
    @Override
    public int updateSysReWorkload(SysReWorkload sysReWorkload)
    {
        sysReWorkload.setUpdateTime(DateUtils.getNowDate());
        return sysReWorkloadMapper.updateSysReWorkload(sysReWorkload);
    }

    /**
     * 批量删除工作量
     * 
     * @param workloadIds 需要删除的工作量主键
     * @return 结果
     */
    @Override
    public int deleteSysReWorkloadByWorkloadIds(String[] workloadIds)
    {
        return sysReWorkloadMapper.deleteSysReWorkloadByWorkloadIds(workloadIds);
    }

    /**
     * 删除工作量信息
     * 
     * @param workloadId 工作量主键
     * @return 结果
     */
    @Override
    public int deleteSysReWorkloadByWorkloadId(String workloadId)
    {
        return sysReWorkloadMapper.deleteSysReWorkloadByWorkloadId(workloadId);
    }
}
