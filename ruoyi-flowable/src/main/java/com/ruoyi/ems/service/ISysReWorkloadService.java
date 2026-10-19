package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReWorkload;

/**
 * 工作量Service接口
 * 
 * @author luohd
 * @date 2023-05-22
 */
public interface ISysReWorkloadService 
{
    /**
     * 查询工作量
     * 
     * @param workloadId 工作量主键
     * @return 工作量
     */
    public SysReWorkload selectSysReWorkloadByWorkloadId(String workloadId);

    /**
     * 查询工作量列表
     * 
     * @param sysReWorkload 工作量
     * @return 工作量集合
     */
    public List<SysReWorkload> selectSysReWorkloadList(SysReWorkload sysReWorkload);

    /**
     * 新增工作量
     * 
     * @param sysReWorkload 工作量
     * @return 结果
     */
    public int insertSysReWorkload(SysReWorkload sysReWorkload);

    /**
     * 修改工作量
     * 
     * @param sysReWorkload 工作量
     * @return 结果
     */
    public int updateSysReWorkload(SysReWorkload sysReWorkload);

    /**
     * 批量删除工作量
     * 
     * @param workloadIds 需要删除的工作量主键集合
     * @return 结果
     */
    public int deleteSysReWorkloadByWorkloadIds(String[] workloadIds);

    /**
     * 删除工作量信息
     * 
     * @param workloadId 工作量主键
     * @return 结果
     */
    public int deleteSysReWorkloadByWorkloadId(String workloadId);
}
