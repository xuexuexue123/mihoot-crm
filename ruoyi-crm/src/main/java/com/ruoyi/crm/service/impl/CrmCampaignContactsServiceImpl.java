package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmCampaignContactsMapper;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.service.ICrmCampaignContactsService;

/**
 * 活动联系人Service业务层处理
 * 
 * @author luohd
 * @date 2025-04-06
 */
@Service
public class CrmCampaignContactsServiceImpl implements ICrmCampaignContactsService 
{
    @Autowired
    private CrmCampaignContactsMapper crmCampaignContactsMapper;

    /**
     * 查询活动联系人
     * 
     * @param id 活动联系人主键
     * @return 活动联系人
     */
    @Override
    public CrmCampaignContacts selectCrmCampaignContactsById(String id)
    {
        return crmCampaignContactsMapper.selectCrmCampaignContactsById(id);
    }

    /**
     * 查询活动联系人列表
     * 
     * @param crmCampaignContacts 活动联系人
     * @return 活动联系人
     */
    @Override
    public List<CrmCampaignContacts> selectCrmCampaignContactsList(CrmCampaignContacts crmCampaignContacts)
    {
        return crmCampaignContactsMapper.selectCrmCampaignContactsList(crmCampaignContacts);
    }

    /**
     * 新增活动联系人
     * 
     * @param crmCampaignContacts 活动联系人
     * @return 结果
     */
    @Override
    public int insertCrmCampaignContacts(CrmCampaignContacts crmCampaignContacts)
    {
        crmCampaignContacts.setCreateTime(DateUtils.getNowDate());
        return crmCampaignContactsMapper.insertCrmCampaignContacts(crmCampaignContacts);
    }

    /**
     * 修改活动联系人
     * 
     * @param crmCampaignContacts 活动联系人
     * @return 结果
     */
    @Override
    public int updateCrmCampaignContacts(CrmCampaignContacts crmCampaignContacts)
    {
        crmCampaignContacts.setUpdateTime(DateUtils.getNowDate());
        return crmCampaignContactsMapper.updateCrmCampaignContacts(crmCampaignContacts);
    }

    /**
     * 批量删除活动联系人
     * 
     * @param ids 需要删除的活动联系人主键
     * @return 结果
     */
    @Override
    public int deleteCrmCampaignContactsByIds(String[] ids)
    {
        return crmCampaignContactsMapper.deleteCrmCampaignContactsByIds(ids);
    }

    /**
     * 删除活动联系人信息
     * 
     * @param id 活动联系人主键
     * @return 结果
     */
    @Override
    public int deleteCrmCampaignContactsById(String id)
    {
        return crmCampaignContactsMapper.deleteCrmCampaignContactsById(id);
    }
}
