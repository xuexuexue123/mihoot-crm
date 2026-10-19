package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysPmLogMapper;
import com.ruoyi.ems.domain.SysPmLog;
import com.ruoyi.ems.service.ISysPmLogService;

/**
 * 项目日志记录Service业务层处理
 * 
 * @author luohd
 * @date 2023-05-17
 */
@Service
public class SysPmLogServiceImpl implements ISysPmLogService 
{
    @Autowired
    private SysPmLogMapper sysPmLogMapper;

    /**
     * 查询项目日志记录
     * 
     * @param logId 项目日志记录主键
     * @return 项目日志记录
     */
    @Override
    public SysPmLog selectSysPmLogByLogId(Long logId)
    {
        return sysPmLogMapper.selectSysPmLogByLogId(logId);
    }

    /**
     * 查询项目日志记录列表
     * 
     * @param sysPmLog 项目日志记录
     * @return 项目日志记录
     */
    @Override
    public List<SysPmLog> selectSysPmLogList(SysPmLog sysPmLog)
    {
        return sysPmLogMapper.selectSysPmLogList(sysPmLog);
    }

    /**
     * 新增项目日志记录
     * 
     * @param sysPmLog 项目日志记录
     * @return 结果
     */
    @Override
    public int insertSysPmLog(SysPmLog sysPmLog)
    {
        sysPmLog.setCreateTime(DateUtils.getNowDate());
        return sysPmLogMapper.insertSysPmLog(sysPmLog);
    }

    /**
     * 修改项目日志记录
     * 
     * @param sysPmLog 项目日志记录
     * @return 结果
     */
    @Override
    public int updateSysPmLog(SysPmLog sysPmLog)
    {
        return sysPmLogMapper.updateSysPmLog(sysPmLog);
    }

    /**
     * 批量删除项目日志记录
     * 
     * @param logIds 需要删除的项目日志记录主键
     * @return 结果
     */
    @Override
    public int deleteSysPmLogByLogIds(Long[] logIds)
    {
        return sysPmLogMapper.deleteSysPmLogByLogIds(logIds);
    }

    /**
     * 删除项目日志记录信息
     * 
     * @param logId 项目日志记录主键
     * @return 结果
     */
    @Override
    public int deleteSysPmLogByLogId(Long logId)
    {
        return sysPmLogMapper.deleteSysPmLogByLogId(logId);
    }

    /**
     *
     * @param taskIds
     * @return
     */
    @Override
    public int deleteSysPmLogByTaskIds(String[] taskIds) {
        return sysPmLogMapper.deleteSysPmLogByTaskIds(taskIds);
    }
}
