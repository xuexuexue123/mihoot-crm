package com.ruoyi.ems.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysSequenceIdMapper;
import com.ruoyi.ems.domain.SysSequenceId;
import com.ruoyi.ems.service.ISysSequenceIdService;

/**
 * sequenceidService业务层处理
 * 
 * @author luohd
 * @date 2022-12-05
 */
@Service
public class SysSequenceIdServiceImpl implements ISysSequenceIdService 
{
    @Autowired
    private SysSequenceIdMapper sysSequenceIdMapper;

    /**
     * 查询sequenceid
     * 
     * @param sequenceid sequenceid主键
     * @return sequenceid
     */
    @Override
    public SysSequenceId selectSysSequenceIdBySequenceid(Long sequenceid)
    {
        return sysSequenceIdMapper.selectSysSequenceIdBySequenceid(sequenceid);
    }

    @Override
    public SysSequenceId selectSysSequenceIdByPrefix(String prefix) {
        return sysSequenceIdMapper.selectSysSequenceIdByPrefix(prefix);
    }


    /**
     * 查询sequenceid列表
     * 
     * @param sysSequenceId sequenceid
     * @return sequenceid
     */
    @Override
    public List<SysSequenceId> selectSysSequenceIdList(SysSequenceId sysSequenceId)
    {
        return sysSequenceIdMapper.selectSysSequenceIdList(sysSequenceId);
    }

    /**
     * 新增sequenceid
     * 
     * @param sysSequenceId sequenceid
     * @return 结果
     */
    @Override
    public int insertSysSequenceId(SysSequenceId sysSequenceId)
    {
        return sysSequenceIdMapper.insertSysSequenceId(sysSequenceId);
    }

    /**
     * 修改sequenceid
     * 
     * @param sysSequenceId sequenceid
     * @return 结果
     */
    @Override
    public int updateSysSequenceId(SysSequenceId sysSequenceId)
    {
        return sysSequenceIdMapper.updateSysSequenceId(sysSequenceId);
    }

    /**
     * 批量删除sequenceid
     * 
     * @param sequenceids 需要删除的sequenceid主键
     * @return 结果
     */
    @Override
    public int deleteSysSequenceIdBySequenceids(Long[] sequenceids)
    {
        return sysSequenceIdMapper.deleteSysSequenceIdBySequenceids(sequenceids);
    }

    /**
     * 删除sequenceid信息
     * 
     * @param sequenceid sequenceid主键
     * @return 结果
     */
    @Override
    public int deleteSysSequenceIdBySequenceid(Long sequenceid)
    {
        return sysSequenceIdMapper.deleteSysSequenceIdBySequenceid(sequenceid);
    }
}
