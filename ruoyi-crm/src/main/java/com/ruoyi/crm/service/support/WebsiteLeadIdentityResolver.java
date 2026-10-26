package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;

/**
 * 官网在线预约身份解析：Contact 优先，其次可 upsert 的 Lead（Initial / Assigned）
 */
@Service
public class WebsiteLeadIdentityResolver
{
    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private ICrmLeadConvertConfigService leadConvertConfigService;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    public OutreachRegistrationIdentityResolution resolve(String tenantKey, String mobilePhone)
    {
        String normalizedPhone = normalizePhone(mobilePhone);
        if (isMergeOnDuplicatePhone(tenantKey) && StringUtils.isNotBlank(normalizedPhone))
        {
            String contactFromPhone = identityService.findContactByIdentity("mobile_phone", normalizedPhone);
            if (StringUtils.isNotBlank(contactFromPhone))
            {
                return OutreachRegistrationIdentityResolution.contactKnown(contactFromPhone, "mobile");
            }
        }

        if (StringUtils.isNotBlank(normalizedPhone))
        {
            CrmLead initialLead = crmLeadMapper.selectInitialByMobilePhone(normalizedPhone);
            if (initialLead != null)
            {
                return OutreachRegistrationIdentityResolution.leadUpsert(initialLead);
            }

            CrmLead latest = crmLeadMapper.selectLatestByMobilePhone(normalizedPhone);
            if (latest != null)
            {
                throw new ServiceException("该预约已处理，请联系工作人员", HttpStatus.FORBIDDEN);
            }
        }

        return OutreachRegistrationIdentityResolution.createNewLead();
    }

    private boolean isMergeOnDuplicatePhone(String tenantKey)
    {
        LeadConvertConfigVo config = leadConvertConfigService.getConfig(tenantKey);
        return config != null && Boolean.TRUE.equals(config.getMergeOnDuplicatePhone());
    }

    private static String normalizePhone(String mobilePhone)
    {
        if (StringUtils.isBlank(mobilePhone))
        {
            return null;
        }
        String normalized = PhoneNormalizer.normalize(mobilePhone);
        return StringUtils.isNotBlank(normalized) ? normalized : mobilePhone.trim();
    }
}
