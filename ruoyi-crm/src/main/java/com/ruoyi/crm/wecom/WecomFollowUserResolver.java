package com.ruoyi.crm.wecom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;
import com.ruoyi.crm.service.support.LeadAssignPatchApplier;

/**
 * 将企微跟进成员 userid（回调 XML 的 UserID）解析为 CRM 用户，用于线索归属。
 * 命中可分配用户 → Assigned；未命中 → Open 公海。
 */
@Component
public class WecomFollowUserResolver
{
    private static final Logger log = LoggerFactory.getLogger(WecomFollowUserResolver.class);

    @Autowired
    private IAssignableUserService assignableUserService;

    /**
     * 按 follow_userid resolve 可分配用户并设置 Lead 归属态。
     *
     * @param lead         待写入的线索
     * @param followUserid 企微回调中的 UserID（跟进成员）
     * @return resolve 结果（供 activity payload 使用）
     */
    public AssignableResolveResult applyLeadOwnership(CrmLead lead, String followUserid)
    {
        if (lead == null)
        {
            return null;
        }
        if (StringUtils.hasText(followUserid))
        {
            lead.setWecomFollowUserid(followUserid.trim());
        }

        if (!StringUtils.hasText(followUserid))
        {
            applyOpenPool(lead, followUserid);
            AssignableResolveResult empty = new AssignableResolveResult();
            empty.setResolved(false);
            empty.setReason("user_not_found");
            return empty;
        }

        AssignableResolveResult result = assignableUserService.resolve(
                followUserid.trim(), null, "wecom_follow_resolve");
        if (result.isResolved() && result.getLeadAssignPatch() != null)
        {
            LeadAssignPatchApplier.apply(lead, result.getLeadAssignPatch());
            log.debug("WeCom lead ownership: followUserid={} -> userId={} status=Assigned",
                    followUserid, lead.getUserId());
        }
        else
        {
            applyOpenPool(lead, followUserid);
            log.warn("WeCom follow user not assignable, lead to Open pool: followUserid={} reason={}",
                    followUserid, result.getReason());
        }
        return result;
    }

    private static void applyOpenPool(CrmLead lead, String followUserid)
    {
        lead.setStatus(AudienceLeadVisibility.STATUS_OPEN);
        lead.setUserId(null);
        lead.setOwner(null);
        if (StringUtils.hasText(followUserid))
        {
            lead.setWecomFollowUserid(followUserid.trim());
        }
    }
}
