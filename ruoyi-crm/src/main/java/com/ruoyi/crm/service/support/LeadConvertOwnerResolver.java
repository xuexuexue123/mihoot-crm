package com.ruoyi.crm.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.system.mapper.SysUserMapper;

/**
 * 转化新建 Contact 时解析 owner / userId / deptId。合并已有 Contact 不调用。
 */
@Component
public class LeadConvertOwnerResolver
{
    private static final Logger log = LoggerFactory.getLogger(LeadConvertOwnerResolver.class);

    @Autowired
    private IAssignableUserService assignableUserService;

    @Autowired
    private SysUserMapper sysUserMapper;

    public void applyToNewContact(CrmContact contact, CrmLead lead, LeadConvertConfigVo config, String operator)
    {
        if (contact == null)
        {
            return;
        }
        String mode = config == null || !StringUtils.isNotEmpty(config.getContactOwnerMode())
                ? LeadRecyclePolicyDefaults.OWNER_KEEP_LEAD : config.getContactOwnerMode().trim();
        if (LeadRecyclePolicyDefaults.OWNER_SPECIFIED_USER.equals(mode))
        {
            if (!applySpecifiedUser(contact, config))
            {
                log.warn("specified_user invalid, fallback keep_lead_owner userId={}",
                        config == null ? null : config.getContactOwnerUserId());
                applyKeepLeadOwner(contact, lead, operator);
            }
            return;
        }
        if (LeadRecyclePolicyDefaults.OWNER_CURRENT_USER.equals(mode))
        {
            applyCurrentUser(contact, operator);
            return;
        }
        applyKeepLeadOwner(contact, lead, operator);
    }

    private boolean applySpecifiedUser(CrmContact contact, LeadConvertConfigVo config)
    {
        if (config == null || config.getContactOwnerUserId() == null)
        {
            return false;
        }
        AssignableResolveResult resolve = assignableUserService.resolve(null, config.getContactOwnerUserId(),
                "contact_owner_dropdown");
        if (!resolve.isResolved() || resolve.getLeadAssignPatch() == null)
        {
            return false;
        }
        contact.setUserId(resolve.getLeadAssignPatch().getUserId());
        contact.setDeptId(resolve.getLeadAssignPatch().getDeptId());
        contact.setOwner(resolve.getLeadAssignPatch().getOwner());
        return true;
    }

    private void applyCurrentUser(CrmContact contact, String operator)
    {
        try
        {
            LoginUser login = SecurityUtils.getLoginUser();
            if (login != null && login.getUser() != null)
            {
                SysUser user = login.getUser();
                contact.setUserId(user.getUserId());
                contact.setDeptId(user.getDeptId());
                contact.setOwner(firstNonBlank(user.getNickName(), user.getUserName()));
                return;
            }
        }
        catch (Exception ignored)
        {
        }
        SysUser byName = findByUsername(operator);
        if (byName != null)
        {
            contact.setUserId(byName.getUserId());
            contact.setDeptId(byName.getDeptId());
            contact.setOwner(firstNonBlank(byName.getNickName(), byName.getUserName()));
        }
    }

    private void applyKeepLeadOwner(CrmContact contact, CrmLead lead, String operator)
    {
        if (lead != null && (lead.getUserId() != null || StringUtils.isNotEmpty(lead.getOwner())))
        {
            if (lead.getUserId() != null)
            {
                contact.setUserId(lead.getUserId());
            }
            if (lead.getDeptId() != null)
            {
                contact.setDeptId(lead.getDeptId());
            }
            if (StringUtils.isNotEmpty(lead.getOwner()))
            {
                contact.setOwner(lead.getOwner());
            }
            return;
        }
        applyCurrentUser(contact, operator);
    }

    private SysUser findByUsername(String username)
    {
        if (!StringUtils.isNotEmpty(username))
        {
            return null;
        }
        try
        {
            return sysUserMapper.selectUserByUserName(username.trim());
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private static String firstNonBlank(String a, String b)
    {
        if (StringUtils.isNotEmpty(a))
        {
            return a;
        }
        return b;
    }
}
