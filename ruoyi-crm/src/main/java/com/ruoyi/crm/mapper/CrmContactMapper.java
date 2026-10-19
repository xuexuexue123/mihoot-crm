package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmContactQuery;
import com.ruoyi.crm.domain.CrmInvestor;

/**
 * 联系人Mapper接口
 * 
 * @author luohd
 * @date 2024-02-08
 */
public interface CrmContactMapper 
{
    /**
     * 查询联系人
     * 
     * @param contactId 联系人主键
     * @return 联系人
     */
    public CrmContact selectCrmContactByContactId(String contactId);

    /**
     * 查询联系人列表
     * 
     * @param crmContact 联系人
     * @return 联系人集合
     */
    public List<CrmContact> selectCrmContactList(CrmContact crmContact);

    /**
     * 查询传入的联系人是否存在
     * @param crmContact
     * @return
     */
    public List<CrmContact> checkContactExisting(CrmContact crmContact);

    /**
     * 根据基金id来选择相关联的投资人
     * @param crmContact
     * @return
     */
    public List<CrmContact> selectCrmContactListByAccountId(CrmContact crmContact);

    /**
     * 根据Household Id查找关联的联系人
     * @param householdId
     * @return
     */
    public List<CrmContact> selectCrmContactListByHouseholdId(String householdId);

    /**
     * 按家庭账户查询联系人主键列表（流程变量 accountId 展开）
     */
    public List<String> selectContactIdsByAccountId(String accountId);

    /**
     * 从custom field里搜索json
     * @param crmInvestor
     * @return
     */
    public List<CrmContact> selectCrmContactFromJSON(CrmInvestor crmInvestor);


    /**
     * 查询本周联系人列表
     *
     * @param crmContact 联系人
     * @return 联系人集合
     */
    public List<CrmContact> selectCrmContactListWeekly(CrmContact crmContact);

    /**
     * 查询所有今天过生日的联系人
     * @param crmContact
     * @return
     */
    public List<CrmContact> selectCrmContactBirthdayList(CrmContact crmContact);
    /**
     * 使用动态条件查询联系人列表
     * @param crmContactQuery
     * @return
     */
    public List<CrmContact> selectCrmContactListByTags(CrmContactQuery crmContactQuery);

    /**
     * 新增联系人
     * 
     * @param crmContact 联系人
     * @return 结果
     */
    public int insertCrmContact(CrmContact crmContact);

    /**
     * 修改联系人
     * 
     * @param crmContact 联系人
     * @return 结果
     */
    public int updateCrmContact(CrmContact crmContact);

    /**
     * 删除联系人
     * 
     * @param contactId 联系人主键
     * @return 结果
     */
    public int deleteCrmContactByContactId(String contactId);

    /**
     * 批量删除联系人
     * 
     * @param contactIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmContactByContactIds(String[] contactIds);

    /**
     * 查询最近一次到访超过指定天数的联系人（last_visit_at IS NULL 或 早于阈值）
     */
    List<CrmContact> selectContactsWithNoRecentVisit(@Param("daysThreshold") Integer daysThreshold);

    /**
     * 查询首次到访周年庆联系人（first_visit_at 月日与今天相同，但不是今年）
     */
    List<CrmContact> selectContactsByFirstVisitAnniversary();
}
