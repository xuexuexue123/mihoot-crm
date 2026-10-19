package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.UUID;

import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.domain.CrmHouseholdmember;
import com.ruoyi.crm.mapper.CrmHouseholdMapper;
import com.ruoyi.crm.domain.CrmHousehold;
import com.ruoyi.crm.service.ICrmHouseholdService;

/**
 * 群组Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-02
 */
@Service
public class CrmHouseholdServiceImpl implements ICrmHouseholdService 
{
    @Autowired
    private CrmHouseholdMapper crmHouseholdMapper;

    /**
     * 查询群组
     * 
     * @param householdId 群组主键
     * @return 群组
     */
    @Override
    public CrmHousehold selectCrmHouseholdByHouseholdId(String householdId)
    {
        return crmHouseholdMapper.selectCrmHouseholdByHouseholdId(householdId);
    }

    /**
     * 查询群组列表
     * 
     * @param crmHousehold 群组
     * @return 群组
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmHousehold> selectCrmHouseholdList(CrmHousehold crmHousehold)
    {
        return crmHouseholdMapper.selectCrmHouseholdList(crmHousehold);
    }

    /**
     * 新增群组
     * 
     * @param crmHousehold 群组
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCrmHousehold(CrmHousehold crmHousehold)
    {
        crmHousehold.setCreateTime(DateUtils.getNowDate());
        int rows = crmHouseholdMapper.insertCrmHousehold(crmHousehold);
        insertCrmHouseholdmember(crmHousehold);
        return rows;
    }

    /**
     * 修改群组
     * 
     * @param crmHousehold 群组
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCrmHousehold(CrmHousehold crmHousehold)
    {
        crmHousehold.setUpdateTime(DateUtils.getNowDate());
        crmHouseholdMapper.deleteCrmHouseholdmemberByHouseholdId(crmHousehold.getHouseholdId());
        insertCrmHouseholdmember(crmHousehold);
        return crmHouseholdMapper.updateCrmHousehold(crmHousehold);
    }

    /**
     * 批量删除群组
     * 
     * @param householdIds 需要删除的群组主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmHouseholdByHouseholdIds(String[] householdIds)
    {
        crmHouseholdMapper.deleteCrmHouseholdmemberByHouseholdIds(householdIds);
        return crmHouseholdMapper.deleteCrmHouseholdByHouseholdIds(householdIds);
    }

    /**
     * 删除群组信息
     * 
     * @param householdId 群组主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmHouseholdByHouseholdId(String householdId)
    {
        crmHouseholdMapper.deleteCrmHouseholdmemberByHouseholdId(householdId);
        return crmHouseholdMapper.deleteCrmHouseholdByHouseholdId(householdId);
    }

    /**
     * 新增群组成员信息
     * 
     * @param crmHousehold 群组对象
     */
    public void insertCrmHouseholdmember(CrmHousehold crmHousehold)
    {
        List<CrmHouseholdmember> crmHouseholdmemberList = crmHousehold.getCrmHouseholdmemberList();
        String householdId = crmHousehold.getHouseholdId();
        if (StringUtils.isNotNull(crmHouseholdmemberList))
        {
            List<CrmHouseholdmember> list = new ArrayList<CrmHouseholdmember>();
            for (CrmHouseholdmember crmHouseholdmember : crmHouseholdmemberList)
            {
                crmHouseholdmember.setMemberId(UUID.randomUUID().toString());
                crmHouseholdmember.setHouseholdName(crmHousehold.getName());
                crmHouseholdmember.setHouseholdId(householdId);
                list.add(crmHouseholdmember);
            }
            if (list.size() > 0)
            {
                crmHouseholdMapper.batchCrmHouseholdmember(list);
            }
        }
    }
}
