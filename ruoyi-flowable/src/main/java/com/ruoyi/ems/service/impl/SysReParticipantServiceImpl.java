package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReParticipantMapper;
import com.ruoyi.ems.domain.SysReParticipant;
import com.ruoyi.ems.service.ISysReParticipantService;

/**
 * 项目干系人Service业务层处理
 * 
 * @author luohd
 * @date 2023-05-19
 */
@Service
public class SysReParticipantServiceImpl implements ISysReParticipantService 
{
    @Autowired
    private SysReParticipantMapper sysReParticipantMapper;

    /**
     * 查询项目干系人
     * 
     * @param partId 项目干系人主键
     * @return 项目干系人
     */
    @Override
    public SysReParticipant selectSysReParticipantByPartId(String partId)
    {
        return sysReParticipantMapper.selectSysReParticipantByPartId(partId);
    }

    /**
     * 查询项目干系人列表
     * 
     * @param sysReParticipant 项目干系人
     * @return 项目干系人
     */
    @Override
    public List<SysReParticipant> selectSysReParticipantList(SysReParticipant sysReParticipant)
    {
        return sysReParticipantMapper.selectSysReParticipantList(sysReParticipant);
    }

    /**
     *
     * @param sysReParticipant
     * @return
     */
    @Override
    public List<SysReParticipant> selectAllTeamMemberList(SysReParticipant sysReParticipant) {
        return sysReParticipantMapper.selectAllTeamMemberList(sysReParticipant);
    }

    /**
     *
     * @param sysReParticipant
     * @return
     */
    @Override
    public List<SysReParticipant> selectOtherParticipantList(SysReParticipant sysReParticipant) {
        return sysReParticipantMapper.selectOtherParticipantList(sysReParticipant);
    }

    /**
     * 新增项目干系人
     * 
     * @param sysReParticipant 项目干系人
     * @return 结果
     */
    @Override
    public int insertSysReParticipant(SysReParticipant sysReParticipant)
    {
        sysReParticipant.setCreateTime(DateUtils.getNowDate());
        return sysReParticipantMapper.insertSysReParticipant(sysReParticipant);
    }

    /**
     * 修改项目干系人
     * 
     * @param sysReParticipant 项目干系人
     * @return 结果
     */
    @Override
    public int updateSysReParticipant(SysReParticipant sysReParticipant)
    {
        sysReParticipant.setUpdateTime(DateUtils.getNowDate());
        return sysReParticipantMapper.updateSysReParticipant(sysReParticipant);
    }

    /**
     * 批量删除项目干系人
     * 
     * @param partIds 需要删除的项目干系人主键
     * @return 结果
     */
    @Override
    public int deleteSysReParticipantByPartIds(String[] partIds)
    {
        return sysReParticipantMapper.deleteSysReParticipantByPartIds(partIds);
    }

    /**
     * 删除项目干系人信息
     * 
     * @param partId 项目干系人主键
     * @return 结果
     */
    @Override
    public int deleteSysReParticipantByPartId(String partId)
    {
        return sysReParticipantMapper.deleteSysReParticipantByPartId(partId);
    }
}
