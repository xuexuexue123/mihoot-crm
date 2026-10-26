package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.crm.domain.CrmContactQuery;
import com.ruoyi.crm.domain.CrmInvestor;
import com.ruoyi.framework.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.support.RecordTriggerCrudHookSupport;
import com.ruoyi.crm.service.webhook.OutboundWebhookHookSupport;

/**
 * 联系人Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-08
 */
@Service
public class CrmContactServiceImpl implements ICrmContactService 
{
    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private RecordTriggerCrudHookSupport recordTriggerCrudHookSupport;

    @Autowired
    private OutboundWebhookHookSupport outboundWebhookHookSupport;

    /**
     * 查询联系人
     * 
     * @param contactId 联系人主键
     * @return 联系人
     */
    @Override
    public CrmContact selectCrmContactByContactId(String contactId)
    {
        return crmContactMapper.selectCrmContactByContactId(contactId);
    }

    /**
     * 查询联系人列表
     * 
     * @param crmContact 联系人
     *
     * 部门及用户权限注解
     * @return 联系人
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmContact> selectCrmContactList(CrmContact crmContact)
    {
        return crmContactMapper.selectCrmContactList(crmContact);
    }

    /**
     * 查询传入的联系人是否存在
     * @param crmContact
     * @return
     */
    public List<CrmContact> checkContactExisting(CrmContact crmContact) {
        return crmContactMapper.checkContactExisting(crmContact);
    }

    /**
     * 根据基金id查询投资人列表
     *
     * @param crmContact 投资人
     *
     * 部门及用户权限注解
     * @return 投资人
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmContact> selectCrmContactListByAccountId(CrmContact crmContact)
    {
        return crmContactMapper.selectCrmContactListByAccountId(crmContact);
    }

    /**
     * 根据Household Id查找关联的联系人
     * @param householdId
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmContact> selectCrmContactListByHouseholdId(String householdId){
        return crmContactMapper.selectCrmContactListByHouseholdId(householdId);
    }

    @Override
    public List<CrmContact> selectCrmContactFromJSON(CrmInvestor crmInvestor)
    {
        return crmContactMapper.selectCrmContactFromJSON(crmInvestor);
    }

    /**
     * 查询本周联系人列表
     *
     * @param crmContact 联系人
     *
     * 部门及用户权限注解
     * @return 联系人
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmContact> selectCrmContactListWeekly(CrmContact crmContact)
    {
        return crmContactMapper.selectCrmContactListWeekly(crmContact);
    }

    /**
     * 查询所有今天过生日的联系人
     * @param crmContact
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmContact> selectCrmContactBirthdayList(CrmContact crmContact) {
        return crmContactMapper.selectCrmContactBirthdayList(crmContact);
    }

    /**
     * 根据动态sql查询
     * @param crmContactQuery
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmContact> selectCrmContactListByTags(CrmContactQuery crmContactQuery) {
        return crmContactMapper.selectCrmContactListByTags(crmContactQuery);
    }

    /**
     * 新增联系人
     * 
     * @param crmContact 联系人
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCrmContact(CrmContact crmContact)
    {
//        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(crmContact.getOriginalSource()) && StringUtils.isNotEmpty(crmContact.getOrigin()))
        {
            crmContact.setOriginalSource(crmContact.getOrigin());
        }
        crmContact.setCreateTime(DateUtils.getNowDate());
//        crmContact.setDeptId(loginUser.getDeptId());
//        crmContact.setUserId(loginUser.getUserId());
        int rows = crmContactMapper.insertCrmContact(crmContact);
        if (rows > 0)
        {
            recordTriggerCrudHookSupport.afterCreate("contact", crmContact.getContactId(), crmContact);
            outboundWebhookHookSupport.onContactCreated(crmContact);
        }
        return rows;
    }

    /**
     * 修改联系人
     * 
     * @param crmContact 联系人
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCrmContact(CrmContact crmContact)
    {
        CrmContact existing = crmContactMapper.selectCrmContactByContactId(crmContact.getContactId());
        if (existing != null)
        {
            if (StringUtils.isNotEmpty(existing.getFirstVisitClinicId()))
            {
                crmContact.setFirstVisitClinicId(existing.getFirstVisitClinicId());
            }
        }
        crmContact.setUpdateTime(DateUtils.getNowDate());
        int rows = crmContactMapper.updateCrmContact(crmContact);
        if (rows > 0)
        {
            CrmContact updated = crmContactMapper.selectCrmContactByContactId(crmContact.getContactId());
            recordTriggerCrudHookSupport.afterUpdate("contact", crmContact.getContactId(), updated, existing);
        }
        return rows;
    }

    /**
     * 批量删除联系人
     * 
     * @param contactIds 需要删除的联系人主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCrmContactByContactIds(String[] contactIds)
    {
        if (contactIds == null || contactIds.length == 0)
        {
            return 0;
        }
        java.util.List<CrmContact> snapshots = new java.util.ArrayList<>();
        for (String contactId : contactIds)
        {
            CrmContact existing = crmContactMapper.selectCrmContactByContactId(contactId);
            if (existing != null)
            {
                snapshots.add(existing);
            }
        }
        int rows = crmContactMapper.deleteCrmContactByContactIds(contactIds);
        if (rows > 0)
        {
            for (CrmContact existing : snapshots)
            {
                recordTriggerCrudHookSupport.afterDelete("contact", existing.getContactId(), existing);
            }
        }
        return rows;
    }

    /**
     * 删除联系人信息
     * 
     * @param contactId 联系人主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCrmContactByContactId(String contactId)
    {
        CrmContact existing = crmContactMapper.selectCrmContactByContactId(contactId);
        int rows = crmContactMapper.deleteCrmContactByContactId(contactId);
        if (rows > 0 && existing != null)
        {
            recordTriggerCrudHookSupport.afterDelete("contact", contactId, existing);
        }
        return rows;
    }
}
