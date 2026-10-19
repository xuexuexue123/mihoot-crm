package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmHouseholdmemberMapper;
import com.ruoyi.crm.domain.CrmHouseholdmember;
import com.ruoyi.crm.service.ICrmHouseholdmemberService;

/**
 * 群组成员Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-02
 */
@Service
public class CrmHouseholdmemberServiceImpl implements ICrmHouseholdmemberService 
{
    @Autowired
    private CrmHouseholdmemberMapper crmHouseholdmemberMapper;

    /**
     * 查询群组成员
     * 
     * @param memberId 群组成员主键
     * @return 群组成员
     */
    @Override
    public CrmHouseholdmember selectCrmHouseholdmemberByMemberId(String memberId)
    {
        return crmHouseholdmemberMapper.selectCrmHouseholdmemberByMemberId(memberId);
    }

    /**
     * 查询群组成员列表
     * 
     * @param crmHouseholdmember 群组成员
     * @return 群组成员
     */
    @Override
    public List<CrmHouseholdmember> selectCrmHouseholdmemberList(CrmHouseholdmember crmHouseholdmember)
    {
        return crmHouseholdmemberMapper.selectCrmHouseholdmemberList(crmHouseholdmember);
    }

    /**
     * 新增群组成员
     * 
     * @param crmHouseholdmember 群组成员
     * @return 结果
     */
    @Override
    public int insertCrmHouseholdmember(CrmHouseholdmember crmHouseholdmember)
    {
        crmHouseholdmember.setCreateTime(DateUtils.getNowDate());
        return crmHouseholdmemberMapper.insertCrmHouseholdmember(crmHouseholdmember);
    }

    /**
     * 修改群组成员
     * 
     * @param crmHouseholdmember 群组成员
     * @return 结果
     */
    @Override
    public int updateCrmHouseholdmember(CrmHouseholdmember crmHouseholdmember)
    {
        crmHouseholdmember.setUpdateTime(DateUtils.getNowDate());
        return crmHouseholdmemberMapper.updateCrmHouseholdmember(crmHouseholdmember);
    }

    /**
     * 批量删除群组成员
     * 
     * @param memberIds 需要删除的群组成员主键
     * @return 结果
     */
    @Override
    public int deleteCrmHouseholdmemberByMemberIds(String[] memberIds)
    {
        return crmHouseholdmemberMapper.deleteCrmHouseholdmemberByMemberIds(memberIds);
    }

    /**
     * 删除群组成员信息
     * 
     * @param memberId 群组成员主键
     * @return 结果
     */
    @Override
    public int deleteCrmHouseholdmemberByMemberId(String memberId)
    {
        return crmHouseholdmemberMapper.deleteCrmHouseholdmemberByMemberId(memberId);
    }
}
