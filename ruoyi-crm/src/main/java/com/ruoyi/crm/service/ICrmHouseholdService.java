package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmHousehold;

/**
 * 群组Service接口
 * 
 * @author luohd
 * @date 2024-02-02
 */
public interface ICrmHouseholdService 
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
     * 批量删除群组
     * 
     * @param householdIds 需要删除的群组主键集合
     * @return 结果
     */
    public int deleteCrmHouseholdByHouseholdIds(String[] householdIds);

    /**
     * 删除群组信息
     * 
     * @param householdId 群组主键
     * @return 结果
     */
    public int deleteCrmHouseholdByHouseholdId(String householdId);
}
