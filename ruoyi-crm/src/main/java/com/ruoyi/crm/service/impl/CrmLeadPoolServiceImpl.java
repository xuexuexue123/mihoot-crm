package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.CrmLeadConvertedQuery;
import com.ruoyi.crm.domain.dto.CrmLeadPoolQuery;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.dto.LeadAssignRequest;
import com.ruoyi.crm.domain.dto.LeadBatchAssignRequest;
import com.ruoyi.crm.domain.dto.LeadReleaseRequest;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.domain.vo.LeadBatchAssignResult;
import com.ruoyi.crm.domain.vo.LeadSourceLeadResponse;
import com.ruoyi.crm.domain.vo.LeadSourceSummaryVo;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.followup.FollowupRuleHookSupport;
import com.ruoyi.crm.service.webhook.OutboundWebhookHookSupport;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmLeadPoolService;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;
import com.ruoyi.crm.service.support.LeadAssignPatchApplier;

@Service
public class CrmLeadPoolServiceImpl implements ICrmLeadPoolService
{
    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private IAssignableUserService assignableUserService;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private FollowupRuleHookSupport followupRuleHookSupport;

    @Autowired
    private OutboundWebhookHookSupport outboundWebhookHookSupport;

    @Autowired
    private CrmLeadPoolConfigServiceImpl poolConfigService;

    @Autowired
    @Lazy
    private ICrmLeadPoolService self;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectPoolList(CrmLeadPoolQuery query)
    {
        if (query == null)
        {
            query = new CrmLeadPoolQuery();
        }
        if ("mine".equalsIgnoreCase(query.getPoolScope())
                || "converted".equalsIgnoreCase(query.getPoolScope()))
        {
            query.setCurrentUserId(SecurityUtils.getUserId());
        }
        return crmLeadMapper.selectCrmLeadPoolList(query);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectConvertedList(CrmLeadConvertedQuery query)
    {
        if (query == null)
        {
            query = new CrmLeadConvertedQuery();
        }
        query.setCurrentUserId(SecurityUtils.getUserId());
        return crmLeadMapper.selectCrmLeadConvertedList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmLead claim(String leadId)
    {
        CrmLead lead = lockAndGet(leadId);
        if (!AudienceLeadVisibility.isOpenStatus(lead.getStatus()))
        {
            throw new ServiceException("仅公海线索可领取", HttpStatus.BAD_REQUEST);
        }
        if (lead.getUserId() != null)
        {
            throw new ServiceException("线索已被领取", HttpStatus.CONFLICT);
        }

        Long currentUserId = SecurityUtils.getUserId();
        AssignableResolveResult resolve = assignableUserService.resolve(null, currentUserId, "lead_claim");
        if (!resolve.isResolved())
        {
            throw new ServiceException("当前用户不可领取线索", HttpStatus.FORBIDDEN);
        }

        poolConfigService.assertClaimAllowed(currentUserId);
        poolConfigService.assertHoldAllowed(currentUserId);

        assertNoMobileConflict(lead.getMobilePhone(), lead.getLeadId());

        LeadAssignPatch patch = resolve.getLeadAssignPatch();
        CrmLead before = crmLeadMapper.selectCrmLeadByLeadId(lead.getLeadId());
        applyPatchAndPersist(lead, patch);
        activityEventProducer.produceLeadAssigned(lead.getLeadId(), lead.getLeadName(),
                patch.getUserId(), patch.getOwner(), patch.getWecomFollowUserid(), "claim");
        CrmLead assigned = crmLeadMapper.selectCrmLeadByLeadId(lead.getLeadId());
        followupRuleHookSupport.onLeadAssigned(assigned, before);
        outboundWebhookHookSupport.onLeadAssigned(assigned, before);
        return assigned;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmLead release(String leadId, LeadReleaseRequest request, boolean forceReclaim)
    {
        CrmLead lead = lockAndGet(leadId);
        if (!AudienceLeadVisibility.isAssignedStatus(lead.getStatus()))
        {
            throw new ServiceException("仅私海线索可释放", HttpStatus.BAD_REQUEST);
        }
        Long currentUserId = SecurityUtils.getUserId();
        if (!forceReclaim && (lead.getUserId() == null || !lead.getUserId().equals(currentUserId)))
        {
            throw new ServiceException("仅可释放本人线索", HttpStatus.FORBIDDEN);
        }

        String reason = request != null && StringUtils.hasText(request.getReason())
                ? request.getReason() : "manual";
        LeadAssignPatchApplier.applyOpenPool(lead);
        persistLead(lead, null);
        activityEventProducer.produceLeadReleased(lead.getLeadId(), lead.getLeadName(), reason,
                forceReclaim ? "reclaim" : "release");
        followupRuleHookSupport.onLeadConvertedOrMerged(lead.getLeadId());
        return crmLeadMapper.selectCrmLeadByLeadId(lead.getLeadId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmLead assign(String leadId, LeadAssignRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("指派参数不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmLead lead = lockAndGet(leadId);
        String statusNorm = AudienceLeadVisibility.normalizeStatus(lead.getStatus());
        if ("converted".equals(statusNorm) || AudienceLeadVisibility.isInitialStatus(lead.getStatus()))
        {
            throw new ServiceException("当前状态不可指派", HttpStatus.BAD_REQUEST);
        }

        AssignableResolveResult resolve = assignableUserService.resolve(
                request.getSnUserid(), request.getUserId(), "lead_assign");
        if (!resolve.isResolved())
        {
            throw new ServiceException("目标用户不可分配: " + resolve.getReason(), HttpStatus.BAD_REQUEST);
        }

        poolConfigService.assertHoldAllowed(resolve.getLeadAssignPatch() != null
                ? resolve.getLeadAssignPatch().getUserId() : request.getUserId());

        assertNoMobileConflict(lead.getMobilePhone(), lead.getLeadId());

        LeadAssignPatch patch = resolve.getLeadAssignPatch();
        CrmLead before = crmLeadMapper.selectCrmLeadByLeadId(lead.getLeadId());
        applyPatchAndPersist(lead, patch);
        String trigger = StringUtils.hasText(request.getTrigger()) ? request.getTrigger() : "manager";
        activityEventProducer.produceLeadAssigned(lead.getLeadId(), lead.getLeadName(),
                patch.getUserId(), patch.getOwner(), patch.getWecomFollowUserid(), trigger);
        CrmLead assigned = crmLeadMapper.selectCrmLeadByLeadId(lead.getLeadId());
        followupRuleHookSupport.onLeadAssigned(assigned, before);
        outboundWebhookHookSupport.onLeadAssigned(assigned, before);
        return assigned;
    }

    @Override
    public LeadBatchAssignResult batchAssign(LeadBatchAssignRequest request)
    {
        LeadBatchAssignResult result = new LeadBatchAssignResult();
        if (request == null || request.getLeadIds() == null)
        {
            return result;
        }
        LeadAssignRequest single = new LeadAssignRequest();
        single.setUserId(request.getUserId());
        single.setSnUserid(request.getSnUserid());
        single.setTrigger("manager");
        for (String leadId : request.getLeadIds())
        {
            if (!StringUtils.hasText(leadId))
            {
                continue;
            }
            try
            {
                self.assign(leadId.trim(), single);
                result.getSuccess().add(leadId.trim());
            }
            catch (ServiceException ex)
            {
                String reason = StringUtils.hasText(ex.getReason()) ? ex.getReason() : ex.getMessage();
                result.getFailed().add(new LeadBatchAssignResult.LeadBatchAssignFailure(
                        leadId.trim(), reason));
            }
        }
        return result;
    }

    @Override
    public LeadSourceLeadResponse findSourceLeadByContactId(String contactId)
    {
        LeadSourceLeadResponse response = new LeadSourceLeadResponse();
        response.setFound(false);
        if (!StringUtils.hasText(contactId))
        {
            return response;
        }
        CrmContact contact = crmContactMapper.selectCrmContactByContactId(contactId.trim());
        CrmLead lead = null;
        if (contact != null && StringUtils.hasText(contact.getSourceLeadId()))
        {
            lead = crmLeadMapper.selectCrmLeadByLeadId(contact.getSourceLeadId());
        }
        if (lead == null)
        {
            lead = crmLeadMapper.selectConvertedLeadByContactId(contactId.trim());
        }
        if (lead == null)
        {
            return response;
        }
        response.setFound(true);
        response.setLead(toSourceSummary(lead));
        return response;
    }

    private CrmLead lockAndGet(String leadId)
    {
        if (!StringUtils.hasText(leadId))
        {
            throw new ServiceException("leadId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmLead lead = crmLeadMapper.selectByLeadIdForUpdate(leadId.trim());
        if (lead == null)
        {
            throw new ServiceException("Lead 不存在", HttpStatus.NOT_FOUND);
        }
        return lead;
    }

    private void assertNoMobileConflict(String mobilePhone, String excludeLeadId)
    {
        if (!StringUtils.hasText(mobilePhone))
        {
            return;
        }
        int count = crmLeadMapper.countAssignedLeadByMobileExcludeLead(mobilePhone.trim(), excludeLeadId);
        if (count > 0)
        {
            throw new ServiceException("同手机号线索已在他人私海", HttpStatus.CONFLICT);
        }
    }

    private void applyPatchAndPersist(CrmLead lead, LeadAssignPatch patch)
    {
        LeadAssignPatchApplier.apply(lead, patch);
        persistLead(lead, DateUtils.getNowDate());
    }

    private void persistLead(CrmLead lead)
    {
        persistLead(lead, lead.getAssignedAt());
    }

    private void persistLead(CrmLead lead, java.util.Date assignedAt)
    {
        CrmLead upd = new CrmLead();
        upd.setLeadId(lead.getLeadId());
        upd.setStatus(lead.getStatus());
        upd.setUserId(lead.getUserId());
        upd.setDeptId(lead.getDeptId());
        upd.setOwner(lead.getOwner());
        upd.setWecomFollowUserid(lead.getWecomFollowUserid());
        upd.setAssignedAt(assignedAt);
        upd.setUpdateTime(DateUtils.getNowDate());
        try
        {
            upd.setUpdateBy(SecurityUtils.getUsername());
        }
        catch (Exception ex)
        {
            upd.setUpdateBy("system");
        }
        crmLeadMapper.updateLeadPoolAssignment(upd);
    }

    private static LeadSourceSummaryVo toSourceSummary(CrmLead lead)
    {
        LeadSourceSummaryVo vo = new LeadSourceSummaryVo();
        vo.setLeadId(lead.getLeadId());
        vo.setLeadName(lead.getLeadName());
        vo.setLeadSource(lead.getLeadSource());
        vo.setOwner(lead.getOwner());
        vo.setConvertTime(lead.getConvertTime());
        vo.setCreateTime(lead.getCreateTime());
        vo.setContactId(lead.getContactId());
        return vo;
    }
}
