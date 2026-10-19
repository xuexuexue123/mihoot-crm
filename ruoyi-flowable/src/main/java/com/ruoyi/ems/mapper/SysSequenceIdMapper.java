package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysSequenceId;

/**
 * sequenceidMapper接口
 * 
 * @author luohd
 * @date 2022-12-05
 */
public interface SysSequenceIdMapper 
{
    /**
     * 查询sequenceid
     * 
     * @param sequenceid sequenceid主键
     * @return sequenceid
     */
    public SysSequenceId selectSysSequenceIdBySequenceid(Long sequenceid);

    /**
     *
     * @param prefix
     * @return
     */
    public SysSequenceId selectSysSequenceIdByPrefix(String prefix);

    /**
     * 查询sequenceid列表
     * 
     * @param sysSequenceId sequenceid
     * @return sequenceid集合
     */
    public List<SysSequenceId> selectSysSequenceIdList(SysSequenceId sysSequenceId);

    /**
     * 新增sequenceid
     * 
     * @param sysSequenceId sequenceid
     * @return 结果
     */
    public int insertSysSequenceId(SysSequenceId sysSequenceId);

    /**
     * 修改sequenceid
     * 
     * @param sysSequenceId sequenceid
     * @return 结果
     */
    public int updateSysSequenceId(SysSequenceId sysSequenceId);

    /**
     * 删除sequenceid
     * 
     * @param sequenceid sequenceid主键
     * @return 结果
     */
    public int deleteSysSequenceIdBySequenceid(Long sequenceid);

    /**
     * 批量删除sequenceid
     * 
     * @param sequenceids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysSequenceIdBySequenceids(Long[] sequenceids);
}
