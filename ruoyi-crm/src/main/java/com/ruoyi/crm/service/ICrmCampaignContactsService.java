package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmCampaignContacts;

/**
 * 活动联系人Service接口
 * 
 * @author luohd
 * @date 2025-04-06
 */
public interface ICrmCampaignContactsService 
{
    /**
     * 查询活动联系人
     * 
     * @param id 活动联系人主键
     * @return 活动联系人
     */
    public CrmCampaignContacts selectCrmCampaignContactsById(String id);

    /**
     * 查询活动联系人列表
     * 
     * @param crmCampaignContacts 活动联系人
     * @return 活动联系人集合
     */
    public List<CrmCampaignContacts> selectCrmCampaignContactsList(CrmCampaignContacts crmCampaignContacts);

    /**
     * 新增活动联系人
     * 
     * @param crmCampaignContacts 活动联系人
     * @return 结果
     */
    public int insertCrmCampaignContacts(CrmCampaignContacts crmCampaignContacts);

    /**
     * 修改活动联系人
     * 
     * @param crmCampaignContacts 活动联系人
     * @return 结果
     */
    public int updateCrmCampaignContacts(CrmCampaignContacts crmCampaignContacts);

    /**
     * 批量删除活动联系人
     * 
     * @param ids 需要删除的活动联系人主键集合
     * @return 结果
     */
    public int deleteCrmCampaignContactsByIds(String[] ids);

    /**
     * 删除活动联系人信息
     * 
     * @param id 活动联系人主键
     * @return 结果
     */
    public int deleteCrmCampaignContactsById(String id);
}
