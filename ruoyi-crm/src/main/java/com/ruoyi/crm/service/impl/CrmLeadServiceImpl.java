package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAccountrelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmWecomExternalContactService;
import com.ruoyi.crm.service.support.LeadConvertTriggerService;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;
import com.ruoyi.crm.service.support.LeadDefaults;
import com.ruoyi.crm.wecom.WecomFollowUserResolver;
import com.ruoyi.crm.service.support.RecordTriggerCrudHookSupport;
import com.ruoyi.crm.service.followup.FollowupRuleHookSupport;
import com.ruoyi.crm.service.webhook.OutboundWebhookHookSupport;
import com.ruoyi.crm.service.nurture.support.EventNurtureHookSupport;
import com.ruoyi.crm.wecom.WecomLeadConvertSyncSupport;
import com.ruoyi.crm.wechatmp.service.MpLeadConvertSyncSupport;
import com.ruoyi.crm.rule.SmartPushEventExecutor;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 业务线索Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-30
 */
@Service
public class CrmLeadServiceImpl implements ICrmLeadService 
{
    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private RecordTriggerCrudHookSupport recordTriggerCrudHookSupport;

    @Autowired
    private EventNurtureHookSupport nurtureHookSupport;

    @Autowired
    private WecomLeadConvertSyncSupport wecomLeadConvertSyncSupport;

    @Autowired
    private MpLeadConvertSyncSupport mpLeadConvertSyncSupport;

    @Autowired
    private ICrmWecomExternalContactService wecomExternalContactService;

    @Autowired
    private LeadConvertTriggerService leadConvertTriggerService;

    @Autowired
    private WecomFollowUserResolver wecomFollowUserResolver;

    @Autowired
    private SmartPushEventExecutor smartPushEventExecutor;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private FollowupRuleHookSupport followupRuleHookSupport;

    @Autowired
    private OutboundWebhookHookSupport outboundWebhookHookSupport;

    @Autowired
    private com.ruoyi.crm.service.support.LeadFollowupAnchorSupport leadFollowupAnchorSupport;

    /**
     * 查询业务线索
     * 
     * @param leadId 业务线索主键
     * @return 业务线索
     */
    @Override
    public CrmLead selectCrmLeadByLeadId(String leadId)
    {
        return crmLeadMapper.selectCrmLeadByLeadId(leadId);
    }

    /**
     * 查询业务线索列表
     * 
     * @param crmLead 业务线索
     * @return 业务线索
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectCrmLeadList(CrmLead crmLead)
    {
        return crmLeadMapper.selectCrmLeadList(crmLead);
    }


    /**
     * 查询最所有业务线索列表导出
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectCrmLeadListExport(CrmLead crmLead) {
        return crmLeadMapper.selectCrmLeadListExport(crmLead);
    }
    /**
     * 查询跟基金相关的查询项目列表
     *
     * @param crmAccountrelation 业务线索
     * @return 业务线索集合
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectCrmLeadListByRelation(CrmAccountrelation crmAccountrelation){
        return crmLeadMapper.selectCrmLeadListByRelation(crmAccountrelation);
    }
    /**
     * 根据业务条件，查询业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectCrmLeadListByCriteria(CrmLead crmLead) {
        return crmLeadMapper.selectCrmLeadListByCriteria(crmLead);
    }
    /**
     * 查询本周业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectCrmLeadListWeekly(CrmLead crmLead)
    {
        return crmLeadMapper.selectCrmLeadListWeekly(crmLead);
    }

    /**
     * 查询本周已转化的业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectCrmLeadListWeeklyConverted(CrmLead crmLead)
    {
        return crmLeadMapper.selectCrmLeadListWeeklyConverted(crmLead);
    }

    /**
     * 查询业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectCrmLeadListQuarterly(CrmLead crmLead)
    {
        return crmLeadMapper.selectCrmLeadListQuarterly(crmLead);
    }


    /**
     * 查找所有没有被转化，也没有被合并的线索
     * @param crmLead
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmLead> selectCrmLeadPendingList(CrmLead crmLead) {
        return crmLeadMapper.selectCrmLeadPendingList(crmLead);
    }


    /**
     * 新增业务线索
     * 
     * @param crmLead 业务线索
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCrmLead(CrmLead crmLead)
    {
        crmLead.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(crmLead.getStage()))
        {
            crmLead.setStage(LeadDefaults.DEFAULT_STAGE);
        }
        if (crmLead.getUserId() == null)
        {
            Long uid = SecurityUtils.getUserId();
            // 企微/匿名回调等无登录态场景下 SecurityUtils 返回 99999，勿写入线索归属
            if (uid != null && uid.longValue() != 99999L)
            {
                crmLead.setUserId(uid);
            }
        }
        if (crmLead.getDeptId() == null && crmLead.getUserId() != null)
        {
            Long deptId = SecurityUtils.getDeptId();
            if (deptId != null)
            {
                crmLead.setDeptId(deptId);
            }
        }
        applyWecomOwnershipOnInsert(crmLead);
        int rows = crmLeadMapper.insertCrmLead(crmLead);
        if (rows > 0)
        {
            recordTriggerCrudHookSupport.afterCreate("lead", crmLead.getLeadId(), crmLead);
            outboundWebhookHookSupport.onLeadCreated(crmLead);
            if (crmLead.getWecomExternalUserid() != null && !crmLead.getWecomExternalUserid().isEmpty())
            {
                wecomExternalContactService.upsertFromCrmBinding(
                        crmLead.getWecomExternalUserid(),
                        crmLead.getWecomFollowUserid(),
                        crmLead.getContactId(),
                        crmLead.getLeadId());
            }
            maybeProduceAssessmentCompleted(null, crmLead);
        }
        smartPushEventExecutor.onEvent("lead_created", crmLead.getContactId(), DynamicDataSourceContextHolder.getDataSourceKey());
        return rows;
    }

    /**
     * 修改业务线索
     * 
     * @param crmLead 业务线索
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCrmLead(CrmLead crmLead)
    {
        CrmLead existing = crmLeadMapper.selectCrmLeadByLeadId(crmLead.getLeadId());
        applyMarketingReleaseToPool(existing, crmLead);
        crmLead.setUpdateTime(DateUtils.getNowDate());
        int rows = crmLeadMapper.updateCrmLead(crmLead);
        if (rows > 0)
        {
            CrmLead updated = crmLeadMapper.selectCrmLeadByLeadId(crmLead.getLeadId());
            recordTriggerCrudHookSupport.afterUpdate("lead", crmLead.getLeadId(), updated, existing);
            nurtureHookSupport.onLeadUpdated(existing, updated);
            if (WecomLeadConvertSyncSupport.isNewContactLink(existing, updated))
            {
                wecomLeadConvertSyncSupport.syncAfterLeadConverted(
                        updated.getLeadId(), updated.getContactId());
                mpLeadConvertSyncSupport.syncAfterLeadConverted(
                        updated.getLeadId(), updated.getContactId());
                followupRuleHookSupport.onLeadConvertedOrMerged(updated.getLeadId());
            }
            if (FollowupRuleHookSupport.ownerChanged(existing, updated)
                    && (updated.getUserId() != null || StringUtils.isNotEmpty(updated.getOwner())))
            {
                followupRuleHookSupport.onLeadAssigned(updated, existing);
                outboundWebhookHookSupport.onLeadAssigned(updated, existing);
            }
            leadConvertTriggerService.registerStageArrivedAfterCommit(existing, updated);
            maybeProduceAssessmentCompleted(existing, updated);
            if (existing != null && updated != null
                    && !java.util.Objects.equals(
                            existing.getStage() == null ? "" : existing.getStage().trim(),
                            updated.getStage() == null ? "" : updated.getStage().trim()))
            {
                leadFollowupAnchorSupport.onLeadStageChanged(existing, updated);
            }
        }
        return rows;
    }

    /**
     * 批量删除业务线索
     * 
     * @param leadIds 需要删除的业务线索主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCrmLeadByLeadIds(String[] leadIds)
    {
        if (leadIds == null || leadIds.length == 0)
        {
            return 0;
        }
        java.util.List<CrmLead> snapshots = new java.util.ArrayList<>();
        for (String leadId : leadIds)
        {
            CrmLead existing = crmLeadMapper.selectCrmLeadByLeadId(leadId);
            if (existing != null)
            {
                snapshots.add(existing);
            }
        }
        int rows = crmLeadMapper.deleteCrmLeadByLeadIds(leadIds);
        if (rows > 0)
        {
            for (CrmLead existing : snapshots)
            {
                recordTriggerCrudHookSupport.afterDelete("lead", existing.getLeadId(), existing);
            }
        }
        return rows;
    }

    /**
     * 删除业务线索信息
     * 
     * @param leadId 业务线索主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCrmLeadByLeadId(String leadId)
    {
        CrmLead existing = crmLeadMapper.selectCrmLeadByLeadId(leadId);
        int rows = crmLeadMapper.deleteCrmLeadByLeadId(leadId);
        if (rows > 0 && existing != null)
        {
            recordTriggerCrudHookSupport.afterDelete("lead", leadId, existing);
        }
        return rows;
    }

    private void maybeProduceAssessmentCompleted(CrmLead existing, CrmLead updated)
    {
        if (updated == null || isSurveyJsonBlank(updated.getSurveyJson()))
        {
            return;
        }
        if (existing != null && !isSurveyJsonBlank(existing.getSurveyJson()))
        {
            return;
        }
        activityEventProducer.produceAssessmentCompleted(
                updated.getLeadId(), updated.getLeadName(), DateUtils.getNowDate());
    }

    private static boolean isSurveyJsonBlank(String surveyJson)
    {
        return surveyJson == null || surveyJson.trim().isEmpty();
    }

    private void applyWecomOwnershipOnInsert(CrmLead crmLead)
    {
        if (StringUtils.isEmpty(crmLead.getWecomFollowUserid()))
        {
            return;
        }
        if (StringUtils.isNotEmpty(crmLead.getOwner()) || crmLead.getUserId() != null)
        {
            return;
        }
        wecomFollowUserResolver.applyLeadOwnership(crmLead, crmLead.getWecomFollowUserid());
    }

    private void applyMarketingReleaseToPool(CrmLead existing, CrmLead incoming)
    {
        if (existing == null || incoming == null)
        {
            return;
        }
        if (!shouldReleaseToOpenPool(existing, incoming))
        {
            return;
        }
        incoming.setStatus(AudienceLeadVisibility.STATUS_OPEN);
        incoming.setUserId(null);
        incoming.setOwner(null);
    }

    private static boolean shouldReleaseToOpenPool(CrmLead existing, CrmLead incoming)
    {
        if (Boolean.TRUE.equals(incoming.getReleaseToPool()))
        {
            return true;
        }
        boolean wasInitial = AudienceLeadVisibility.isInitialStatus(existing.getStatus());
        boolean nowNonInitial = !AudienceLeadVisibility.isInitialStatus(incoming.getStatus());
        if (wasInitial && nowNonInitial)
        {
            String source = firstNonBlank(incoming.getLeadSource(), existing.getLeadSource());
            if (source != null && source.toLowerCase().contains("marketing"))
            {
                return true;
            }
        }
        return false;
    }

    private static String firstNonBlank(String... values)
    {
        if (values == null)
        {
            return null;
        }
        for (String v : values)
        {
            if (StringUtils.isNotEmpty(v))
            {
                return v;
            }
        }
        return null;
    }
}
