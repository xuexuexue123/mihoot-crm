package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmHousehold;
import com.ruoyi.crm.domain.CrmHouseholdmember;

/**
 * 群组Mapper接口
 * 
 * @author luohd
 * @date 2024-02-02
 */
public interface CrmHouseholdMapper 
{
    /**
     * 查询群组
     * 
     * @param householdId 群组主键
     * @return 群组
     */
    public CrmHousehold selectCrmHouseholdByHouseholdId(String householdId);

    /**
     * 查询群组列表
     * 
     * @param crmHousehold 群组
     * @return 群组集合
     */
    public List<CrmHousehold> selectCrmHouseholdList(CrmHousehold crmHousehold);

    /**
     * 新增群组
     * 
     * @param crmHousehold 群组
     * @return 结果
     */
    public int insertCrmHousehold(CrmHousehold crmHousehold);

    /**
     * 修改群组
     * 
     * @param crmHousehold 群组
     * @return 结果
     */
    public int updateCrmHousehold(CrmHousehold crmHousehold);

    /**
     * 删除群组
     * 
     * @param householdId 群组主键
     * @return 结果
     */
    public int deleteCrmHouseholdByHouseholdId(String householdId);

    /**
     * 批量删除群组
     * 
     * @param householdIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmHouseholdByHouseholdIds(String[] householdIds);

    /**
     * 批量删除群组成员
     * 
     * @param householdIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmHouseholdmemberByHouseholdIds(String[] householdIds);
    
    /**
     * 批量新增群组成员
     * 
     * @param crmHouseholdmemberList 群组成员列表
     * @return 结果
     */
    public int batchCrmHouseholdmember(List<CrmHouseholdmember> crmHouseholdmemberList);
    

    /**
     * 通过群组主键删除群组成员信息
     * 
     * @param householdId 群组ID
     * @return 结果
     */
    public int deleteCrmHouseholdmemberByHouseholdId(String householdId);
}
