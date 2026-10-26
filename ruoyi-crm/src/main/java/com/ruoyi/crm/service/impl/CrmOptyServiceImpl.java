package com.ruoyi.crm.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmOptyService;
import com.ruoyi.crm.service.IPatientClvService;
import com.ruoyi.crm.service.support.OptyCloseReasonStageSupport;
import com.ruoyi.crm.service.support.PatientWorkflowAutomationLauncher;
import com.ruoyi.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.service.support.RecordTriggerCrudHookSupport;
import com.ruoyi.crm.service.followup.FollowupRuleHookSupport;
import com.ruoyi.crm.service.webhook.OutboundWebhookHookSupport;
import com.ruoyi.crm.service.recall.RecallScheduleService;
import com.ruoyi.crm.service.wecom.contactgroup.WecomGroupReminderMaterializer;
import com.ruoyi.crm.rule.SmartPushEventExecutor;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 销售机会Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-30
 */
@Service
public class CrmOptyServiceImpl implements ICrmOptyService 
{
    private static final String DICT_CLOSED_WON_BASIS = "crm_closed_won_basis";
    private static final String DICT_CLOSED_WON_STAGES = "crm_opty_stage_closed_won";
    private static final String DICT_TREATMENT_ITEM = "crm_treatment_item";

    @Autowired
    private CrmOptyMapper crmOptyMapper;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @Autowired
    private IPatientClvService patientClvService;

    @Autowired
    private PatientWorkflowAutomationLauncher patientWorkflowAutomationLauncher;

    @Autowired
    private RecordTriggerCrudHookSupport recordTriggerCrudHookSupport;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private SmartPushEventExecutor smartPushEventExecutor;

    @Autowired
    private FollowupRuleHookSupport followupRuleHookSupport;

    @Autowired
    private OutboundWebhookHookSupport outboundWebhookHookSupport;

    @Autowired
    private RecallScheduleService recallScheduleService;

    @Autowired
    private WecomGroupReminderMaterializer wecomGroupReminderMaterializer;

    /** 当字典 crm_opty_stage_closed_won 无数据时，逗号分隔的赢单 stage，须与 crm_opty.stage 存库一致 */
    @Value("${crm.dental.closed-won-stages:}")
    private String closedWonStagesFallback;

    /**
     * 查询销售机会
     * 
     * @param optyId 销售机会主键
     * @return 销售机会
     */
    @Override
    public CrmOpty selectCrmOptyByOptyId(String optyId)
    {
        return crmOptyMapper.selectCrmOptyByOptyId(optyId);
    }


    /**
     * 根据基金ID查询基金投资的项目
     *
     * @param accountId
     * @return
     */
    @Override
    public List<CrmOpty> selectCrmOptyListByAccountId(String accountId) {
        return crmOptyMapper.selectCrmOptyListByAccountId(accountId);
    }
    /**
     * 查询销售机会列表
     * 
     * @param crmOpty 销售机会
     * @return 销售机会
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmOpty> selectCrmOptyList(CrmOpty crmOpty)
    {
        return crmOptyMapper.selectCrmOptyList(crmOpty);
    }

    /**
     * 查询指定销售阶段的销售机会列表
     *
     * @param crmOpty 销售机会阶段
     * @return 销售机会
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmOpty> selectCrmOptyListByStages(CrmOpty crmOpty)
    {
        return crmOptyMapper.selectCrmOptyListByStages(crmOpty);
    }

    /**
     * 查询本周销售机会列表
     *
     * @param crmOpty 销售机会
     * @return 销售机会
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmOpty> selectCrmOptyListWeekly(CrmOpty crmOpty)
    {
        return crmOptyMapper.selectCrmOptyListWeekly(crmOpty);
    }

    /**
     * 查询本周已赢单销售机会列表
     *
     * @param crmOpty 销售机会
     * @return 销售机会
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmOpty> selectWonCrmOptyListWeekly(CrmOpty crmOpty)
    {
        return crmOptyMapper.selectWonCrmOptyListWeekly(crmOpty);
    }

    /**
     * 新增销售机会
     * 
     * @param crmOpty 销售机会
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCrmOpty(CrmOpty crmOpty)
    {
        assertClosedWonBasisIfNeeded(crmOpty.getStage(), crmOpty.getClosedWonBasis(), null, null);
        assertTreatmentItemCodeIfNeeded(crmOpty.getStage(), crmOpty.getTreatmentItemCode(), null, null);
        crmOpty.setCreateTime(DateUtils.getNowDate());
        int rows = crmOptyMapper.insertCrmOpty(crmOpty);
        if (rows > 0)
        {
            patientClvService.enqueueRecomputeForContact(crmOpty.getContactId(), crmOpty.getAccountId());
            activityEventProducer.produceOpportunityCreated(crmOpty);
            recordTriggerCrudHookSupport.afterCreate("opportunity", crmOpty.getOptyId(), crmOpty);
        }
        return rows;
    }

    /**
     * 修改销售机会
     * 
     * @param crmOpty 销售机会
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCrmOpty(CrmOpty crmOpty)
    {
        CrmOpty existing = crmOptyMapper.selectCrmOptyByOptyId(crmOpty.getOptyId());
        if (existing == null)
        {
            throw new ServiceException("商机不存在");
        }
        OptyCloseReasonStageSupport.applyStageFromCloseReason(crmOpty);
        String stageEff = StringUtils.isNotEmpty(crmOpty.getStage()) ? crmOpty.getStage() : existing.getStage();
        String basisEff = crmOpty.getClosedWonBasis() != null ? crmOpty.getClosedWonBasis() : existing.getClosedWonBasis();
        String treatmentEff = crmOpty.getTreatmentItemCode() != null
                ? crmOpty.getTreatmentItemCode() : existing.getTreatmentItemCode();
        assertClosedWonBasisIfNeeded(stageEff, basisEff, existing.getStage(), existing.getClosedWonBasis());
        assertTreatmentItemCodeIfNeeded(stageEff, treatmentEff, existing.getStage(), existing.getTreatmentItemCode());
        crmOpty.setUpdateTime(DateUtils.getNowDate());
        int rows = crmOptyMapper.updateCrmOpty(crmOpty);
        if (rows > 0)
        {
            patientClvService.enqueueRecomputeForContact(
                    StringUtils.isNotEmpty(crmOpty.getContactId()) ? crmOpty.getContactId() : existing.getContactId(),
                    StringUtils.isNotEmpty(crmOpty.getAccountId()) ? crmOpty.getAccountId() : existing.getAccountId());
            if (isTreatmentCompleted(stageEff) && !isTreatmentCompleted(existing.getStage()))
            {
                patientWorkflowAutomationLauncher.triggerUpsell(
                        StringUtils.isNotEmpty(crmOpty.getContactId()) ? crmOpty.getContactId() : existing.getContactId(),
                        StringUtils.isNotEmpty(crmOpty.getAccountId()) ? crmOpty.getAccountId() : existing.getAccountId());
            }
            CrmOpty updated = crmOptyMapper.selectCrmOptyByOptyId(crmOpty.getOptyId());
            if (isClosedWonStage(updated.getStage()) && !isClosedWonStage(existing.getStage()))
            {
                activityEventProducer.produceOpportunityWon(updated);
                smartPushEventExecutor.onEvent("opty_won", updated.getContactId(), DynamicDataSourceContextHolder.getDataSourceKey());
                followupRuleHookSupport.onOpportunityWon(updated, existing);
                outboundWebhookHookSupport.onOpportunityWon(updated, existing);
                recallScheduleService.onOpportunityWon(updated);
                try
                {
                    wecomGroupReminderMaterializer.onOpportunityWon(updated);
                }
                catch (Exception ignored)
                {
                }
            }
            if (FollowupRuleHookSupport.isClosedLost(updated.getStage(), updated.getCloseReason())
                    && !FollowupRuleHookSupport.isClosedLost(existing.getStage(), existing.getCloseReason()))
            {
                followupRuleHookSupport.onOpportunityLost(updated);
            }
            recordTriggerCrudHookSupport.afterUpdate("opportunity", crmOpty.getOptyId(), updated, existing);
        }
        return rows;
    }

    /**
     * 批量删除销售机会
     * 
     * @param optyIds 需要删除的销售机会主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCrmOptyByOptyIds(String[] optyIds)
    {
        if (optyIds == null || optyIds.length == 0)
        {
            return 0;
        }
        java.util.List<CrmOpty> snapshots = new java.util.ArrayList<>();
        for (String optyId : optyIds)
        {
            CrmOpty existing = crmOptyMapper.selectCrmOptyByOptyId(optyId);
            if (existing != null)
            {
                snapshots.add(existing);
            }
        }
        int rows = crmOptyMapper.deleteCrmOptyByOptyIds(optyIds);
        if (rows > 0)
        {
            for (CrmOpty existing : snapshots)
            {
                recordTriggerCrudHookSupport.afterDelete("opportunity", existing.getOptyId(), existing);
            }
        }
        return rows;
    }

    /**
     * 删除销售机会信息
     * 
     * @param optyId 销售机会主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCrmOptyByOptyId(String optyId)
    {
        CrmOpty existing = crmOptyMapper.selectCrmOptyByOptyId(optyId);
        int rows = crmOptyMapper.deleteCrmOptyByOptyId(optyId);
        if (rows > 0 && existing != null)
        {
            recordTriggerCrudHookSupport.afterDelete("opportunity", optyId, existing);
        }
        return rows;
    }

    private boolean isClosedWonStage(String stage)
    {
        if (StringUtils.isEmpty(stage))
        {
            return false;
        }
        List<SysDictData> dict = dictTypeService.selectDictDataByType(DICT_CLOSED_WON_STAGES);
        if (dict != null && !dict.isEmpty())
        {
            for (SysDictData d : dict)
            {
                if ("0".equals(d.getStatus()) && stage.equals(d.getDictValue()))
                {
                    return true;
                }
            }
            return false;
        }
        if (StringUtils.isEmpty(closedWonStagesFallback))
        {
            return false;
        }
        for (String p : closedWonStagesFallback.split(","))
        {
            if (stage.equals(p.trim()))
            {
                return true;
            }
        }
        return false;
    }

    private void assertValidClosedWonBasis(String basis)
    {
        if (StringUtils.isEmpty(basis))
        {
            throw new ServiceException("赢单阶段须填写 closed_won_basis");
        }
        List<SysDictData> list = dictTypeService.selectDictDataByType(DICT_CLOSED_WON_BASIS);
        if (list == null || list.isEmpty())
        {
            if (!"down_payment_received".equals(basis) && !"treatment_agreement_signed".equals(basis))
            {
                throw new ServiceException("closed_won_basis 取值非法");
            }
            return;
        }
        Set<String> allowed = new HashSet<>();
        for (SysDictData d : list)
        {
            if ("0".equals(d.getStatus()))
            {
                allowed.add(d.getDictValue());
            }
        }
        if (!allowed.contains(basis))
        {
            throw new ServiceException("closed_won_basis 不在字典 crm_closed_won_basis 允许范围内");
        }
    }

    /**
     * 进入赢单终态时必须有合法 closed_won_basis；已是赢单且库中已有依据时允许本次不传（兼容旧前端）。
     */
    private void assertClosedWonBasisIfNeeded(String newStage, String newBasis, String oldStage, String oldBasis)
    {
        if (!isClosedWonStage(newStage))
        {
            return;
        }
        if (StringUtils.isNotEmpty(newBasis))
        {
            assertValidClosedWonBasis(newBasis);
            return;
        }
        if (isClosedWonStage(oldStage) && StringUtils.isNotEmpty(oldBasis))
        {
            return;
        }
        throw new ServiceException("赢单阶段须填写 closed_won_basis（down_payment_received 或 treatment_agreement_signed）");
    }

    /**
     * 进入赢单终态时必须有合法 treatment_item_code（字典 crm_treatment_item）；
     * 已是赢单且库中已有合法码时允许本次不传（兼容旧前端）。
     */
    private void assertTreatmentItemCodeIfNeeded(String newStage, String newCode, String oldStage, String oldCode)
    {
        if (!isClosedWonStage(newStage))
        {
            return;
        }
        if (StringUtils.isNotEmpty(newCode))
        {
            assertValidTreatmentItemCode(newCode.trim());
            return;
        }
        if (isClosedWonStage(oldStage) && StringUtils.isNotEmpty(oldCode))
        {
            assertValidTreatmentItemCode(oldCode.trim());
            return;
        }
        throw new ServiceException("treatment_item_code required for closed_won / 赢单阶段必须选择治疗项目");
    }

    private void assertValidTreatmentItemCode(String code)
    {
        List<SysDictData> list = dictTypeService.selectDictDataByType(DICT_TREATMENT_ITEM);
        if (list == null || list.isEmpty())
        {
            // 字典未种子时：仅允许已知标准 value，避免阻断部署中途
            if (!"implant".equals(code) && !"preventive".equals(code) && !"restorative".equals(code)
                    && !"ortho".equals(code) && !"pediatric".equals(code))
            {
                throw new ServiceException(
                        "treatment_item_code not in crm_treatment_item / 治疗项目不在字典允许范围内");
            }
            return;
        }
        Set<String> allowed = new HashSet<>();
        for (SysDictData d : list)
        {
            if ("0".equals(d.getStatus()) && StringUtils.isNotEmpty(d.getDictValue()))
            {
                allowed.add(d.getDictValue().trim());
            }
        }
        if (!allowed.contains(code))
        {
            throw new ServiceException(
                    "treatment_item_code not in crm_treatment_item / 治疗项目不在字典允许范围内");
        }
    }

    private boolean isTreatmentCompleted(String stage)
    {
        if (StringUtils.isEmpty(stage))
        {
            return false;
        }
        String s = stage.trim().toLowerCase();
        return s.contains("结案") || s.contains("completed") || s.contains("closed") || "treatment_completed".equals(s);
    }
}
