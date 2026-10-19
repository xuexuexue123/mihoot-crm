package com.ruoyi.crm.wecom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.support.OutreachEventLeadResolveService;
import com.ruoyi.crm.service.support.OutreachLeadResolveSupport;
import com.ruoyi.crm.service.support.PhoneNormalizer;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;

/**
 * 企微加好友时跨渠道解析已有 Initial Lead（UnionID → fan → openid / 手机 / 企微 ID）。
 */
@Service
public class WecomEventLeadResolveService
{
    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmWechatMpFanMapper fanMapper;

    @Autowired
    private OutreachEventLeadResolveService outreachEventLeadResolveService;

    public Optional<CrmLead> resolveInitialLeadForWecomAdd(String crmEventId, String unionId,
            String externalUserid, String mobileFromRemark, String appIdHint)
    {
        List<CrmLead> candidates = new ArrayList<>();

        if (StringUtils.hasText(externalUserid))
        {
            CrmLead byWecom = crmLeadMapper.selectByWecomExternalUserid(externalUserid.trim());
            addIfInitial(candidates, byWecom);
        }

        if (StringUtils.hasText(crmEventId) && StringUtils.hasText(unionId))
        {
            List<CrmWechatMpFan> fans = fanMapper.selectByUnionId(unionId.trim());
            if (fans != null)
            {
                for (CrmWechatMpFan fan : fans)
                {
                    if (fan == null || !StringUtils.hasText(fan.getOpenId()))
                    {
                        continue;
                    }
                    String appId = StringUtils.hasText(fan.getAppId()) ? fan.getAppId()
                            : (StringUtils.hasText(appIdHint) ? appIdHint : null);
                    CrmLead byOpenId = outreachEventLeadResolveService.resolveLeadForEvent(
                            crmEventId, fan.getOpenId(), appId);
                    addIfInitial(candidates, byOpenId);
                }
            }
        }

        if (StringUtils.hasText(crmEventId) && StringUtils.hasText(mobileFromRemark))
        {
            String normalized = PhoneNormalizer.normalize(mobileFromRemark);
            String phone = StringUtils.hasText(normalized) ? normalized : mobileFromRemark.trim();
            CrmLead byPhone = crmLeadMapper.selectBySidAndMobilePhone(crmEventId, phone);
            addIfInitial(candidates, byPhone);
        }

        if (candidates.isEmpty())
        {
            return Optional.empty();
        }

        return Optional.of(pickBest(candidates, crmEventId));
    }

    private static void addIfInitial(List<CrmLead> candidates, CrmLead lead)
    {
        if (lead == null || !StringUtils.hasText(lead.getLeadId()))
        {
            return;
        }
        if (!OutreachLeadResolveSupport.isInitialLeadStatus(lead.getStatus()))
        {
            return;
        }
        for (CrmLead existing : candidates)
        {
            if (lead.getLeadId().equals(existing.getLeadId()))
            {
                return;
            }
        }
        candidates.add(lead);
    }

    private static CrmLead pickBest(List<CrmLead> candidates, String crmEventId)
    {
        Comparator<CrmLead> comparator = Comparator
                .comparing((CrmLead l) -> sameEvent(l, crmEventId) ? 0 : 1)
                .thenComparing(CrmLead::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder()));
        return candidates.stream().sorted(comparator).findFirst().orElse(candidates.get(0));
    }

    private static boolean sameEvent(CrmLead lead, String crmEventId)
    {
        if (!StringUtils.hasText(crmEventId) || lead == null)
        {
            return false;
        }
        return crmEventId.equals(lead.getSid());
    }
}
