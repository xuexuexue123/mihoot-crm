package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysPmLog;

/**
 * 项目日志记录Service接口
 * 
 * @author luohd
 * @date 2023-05-17
 */
public interface ISysPmLogService 
{
    /**
     * 查询项目日志记录
     * 
     * @param logId 项目日志记录主键
     * @return 项目日志记录
     */
    public SysPmLog selectSysPmLogByLogId(Long logId);

    /**
     * 查询项目日志记录列表
     * 
     * @param sysPmLog 项目日志记录
     * @return 项目日志记录集合
     */
    public List<SysPmLog> selectSysPmLogList(SysPmLog sysPmLog);

    /**
     * 新增项目日志记录
     * 
     * @param sysPmLog 项目日志记录
     * @return 结果
     */
    public int insertSysPmLog(SysPmLog sysPmLog);

    /**
     * 修改项目日志记录
     * 
     * @param sysPmLog 项目日志记录
     * @return 结果
     */
    public int updateSysPmLog(SysPmLog sysPmLog);

    /**
     * 批量删除项目日志记录
     * 
     * @param logIds 需要删除的项目日志记录主键集合
     * @return 结果
     */
    public int deleteSysPmLogByLogIds(Long[] logIds);

    /**
     * 删除项目日志记录信息
     * 
     * @param logId 项目日志记录主键
     * @return 结果
     */
    public int deleteSysPmLogByLogId(Long logId);

    /**
     *
     * @param taskIds
     * @return
     */
    public int deleteSysPmLogByTaskIds(String[] taskIds);
}
