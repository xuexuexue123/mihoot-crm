package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysReParticipant;
import com.ruoyi.framework.web.domain.server.Sys;

/**
 * 项目干系人Mapper接口
 * 
 * @author luohd
 * @date 2023-05-19
 */
public interface SysReParticipantMapper 
{
    /**
     * 查询项目干系人
     * 
     * @param partId 项目干系人主键
     * @return 项目干系人
     */
    public SysReParticipant selectSysReParticipantByPartId(String partId);

    /**
     * 查询项目干系人列表
     * 
     * @param sysReParticipant 项目干系人
     * @return 项目干系人集合
     */
    public List<SysReParticipant> selectSysReParticipantList(SysReParticipant sysReParticipant);

    /**
     *
     * @param sysReParticipant
     * @return
     */
    public List<SysReParticipant> selectAllTeamMemberList(SysReParticipant sysReParticipant);

    /**
     *
     * @param sysReParticipant
     * @return
     */
    public List<SysReParticipant> selectOtherParticipantList(SysReParticipant sysReParticipant);
    /**
     * 新增项目干系人
     * 
     * @param sysReParticipant 项目干系人
     * @return 结果
     */
    public int insertSysReParticipant(SysReParticipant sysReParticipant);

    /**
     * 修改项目干系人
     * 
     * @param sysReParticipant 项目干系人
     * @return 结果
     */
    public int updateSysReParticipant(SysReParticipant sysReParticipant);

    /**
     * 删除项目干系人
     * 
     * @param partId 项目干系人主键
     * @return 结果
     */
    public int deleteSysReParticipantByPartId(String partId);

    /**
     * 批量删除项目干系人
     * 
     * @param partIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReParticipantByPartIds(String[] partIds);
}
