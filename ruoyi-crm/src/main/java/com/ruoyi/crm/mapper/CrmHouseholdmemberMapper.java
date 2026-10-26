package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmHouseholdmember;

/**
 * 群组成员Mapper接口
 * 
 * @author luohd
 * @date 2024-02-02
 */
public interface CrmHouseholdmemberMapper 
{
    /**
     * 查询群组成员
     * 
     * @param memberId 群组成员主键
     * @return 群组成员
     */
    public CrmHouseholdmember selectCrmHouseholdmemberByMemberId(String memberId);

    /**
     * 查询群组成员列表
     * 
     * @param crmHouseholdmember 群组成员
     * @return 群组成员集合
     */
    public List<CrmHouseholdmember> selectCrmHouseholdmemberList(CrmHouseholdmember crmHouseholdmember);

    /**
     * 新增群组成员
     * 
     * @param crmHouseholdmember 群组成员
     * @return 结果
     */
    public int insertCrmHouseholdmember(CrmHouseholdmember crmHouseholdmember);

    /**
     * 修改群组成员
     * 
     * @param crmHouseholdmember 群组成员
     * @return 结果
     */
    public int updateCrmHouseholdmember(CrmHouseholdmember crmHouseholdmember);

    /**
     * 删除群组成员
     * 
     * @param memberId 群组成员主键
     * @return 结果
     */
    public int deleteCrmHouseholdmemberByMemberId(String memberId);

    /**
     * 批量删除群组成员
     * 
     * @param memberIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmHouseholdmemberByMemberIds(String[] memberIds);
}
