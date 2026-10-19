package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmOutreachlead;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.wechatmp.service.WechatMpIdentityResolutionSupport;

/**
 * Contact 已知分支：补全 Contact 字段并绑定公众号 openid
 */
@Service
public class OutreachRegistrationContactHandler
{
    @Autowired
    private ICrmContactService contactService;

    @Autowired
    private WechatMpIdentityResolutionSupport mpIdentityResolutionSupport;

    public void enrichAndBindIdentity(CrmOutreachlead submission, String contactId,
            String tokenOpenId, String tokenAppId)
    {
        CrmContact contact = contactService.selectCrmContactByContactId(contactId);
        if (contact == null)
        {
            throw new ServiceException("客户不存在", HttpStatus.NOT_FOUND);
        }

        CrmContact patch = new CrmContact();
        patch.setContactId(contactId);
        boolean needsUpdate = false;

        if (StringUtils.isBlank(contact.getMobilePhone()) && StringUtils.isNotBlank(submission.getMobilePhone()))
        {
            patch.setMobilePhone(PhoneNormalizer.normalize(submission.getMobilePhone()));
            needsUpdate = true;
        }
        if (StringUtils.isBlank(contact.getPersonalEmail()) && StringUtils.isNotBlank(submission.getWorkEmail()))
        {
            patch.setPersonalEmail(submission.getWorkEmail().trim());
            needsUpdate = true;
        }
        String displayName = resolveDisplayName(submission.getLeadName(), submission.getMobilePhone());
        if (StringUtils.isBlank(contact.getName()) && StringUtils.isNotBlank(displayName))
        {
            patch.setName(displayName);
            needsUpdate = true;
        }

        if (needsUpdate)
        {
            patch.setUpdateTime(DateUtils.getNowDate());
            contactService.updateCrmContact(patch);
        }

        if (StringUtils.isNotBlank(tokenOpenId) && StringUtils.isNotBlank(tokenAppId))
        {
            mpIdentityResolutionSupport.bindOpenIdIfNeeded(tokenOpenId, contactId, "event_registration");
        }
    }

    private static String resolveDisplayName(String leadName, String mobilePhone)
    {
        if (StringUtils.isNotBlank(leadName))
        {
            return leadName.trim();
        }
        String mobile = PhoneNormalizer.normalize(mobilePhone);
        if (StringUtils.isNotBlank(mobile))
        {
            return mobile;
        }
        return "活动现场客户";
    }
}
