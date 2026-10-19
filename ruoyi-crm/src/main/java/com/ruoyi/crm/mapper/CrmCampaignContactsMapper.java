package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmCampaignContacts;

/**
 * 活动联系人Mapper接口
 * 
 * @author luohd
 * @date 2025-04-06
 */
public interface CrmCampaignContactsMapper 
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
     * 删除活动联系人
     * 
     * @param id 活动联系人主键
     * @return 结果
     */
    public int deleteCrmCampaignContactsById(String id);

    /**
     * 批量删除活动联系人
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmCampaignContactsByIds(String[] ids);

    List<CrmCampaignContacts> selectPendingByCampaignId(@Param("campaignId") String campaignId,
            @Param("limit") int limit);

    int batchInsert(@Param("list") List<CrmCampaignContacts> list);

    int deletePendingByCampaignId(@Param("campaignId") String campaignId);

    int countValidEmailByCampaignId(@Param("campaignId") String campaignId);

    int updateDeliveryResult(@Param("id") String id,
            @Param("deliveryStatus") String deliveryStatus,
            @Param("bounceReason") String bounceReason,
            @Param("sentAt") Date sentAt);

    int failAllPendingByCampaignId(@Param("campaignId") String campaignId,
            @Param("deliveryStatus") String deliveryStatus,
            @Param("bounceReason") String bounceReason,
            @Param("sentAt") Date sentAt);
}
