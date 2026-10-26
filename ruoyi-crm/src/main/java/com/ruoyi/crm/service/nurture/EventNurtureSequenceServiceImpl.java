package com.ruoyi.crm.service.nurture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventNurtureSequence;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.domain.dto.EventNurtureSequenceSaveRequest;
import com.ruoyi.crm.domain.dto.EventNurtureStepsSaveRequest;
import com.ruoyi.crm.domain.vo.EventNurtureSequenceVo;
import com.ruoyi.crm.domain.vo.EventNurtureStepVo;
import com.ruoyi.crm.enums.EventNurtureActionType;
import com.ruoyi.crm.enums.EventNurtureTriggerType;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.EventNurtureEnrollmentMapper;
import com.ruoyi.crm.mapper.EventNurtureSequenceMapper;
import com.ruoyi.crm.mapper.EventNurtureStepMapper;
import com.ruoyi.crm.service.nurture.support.EventNurtureJsonSupport;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;

@Service
public class EventNurtureSequenceServiceImpl implements IEventNurtureSequenceService
{
    private static final int MAX_STEPS = 20;

    @Autowired
    private EventNurtureSequenceMapper sequenceMapper;

    @Autowired
    private EventNurtureStepMapper stepMapper;

    @Autowired
    private EventNurtureEnrollmentMapper enrollmentMapper;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private IWechatMpMessagePresetService presetService;

    @Override
    public EventNurtureSequenceVo getSequence(String tenantId, String eventId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = requireEvent(eventId);
        CrmEventNurtureSequence seq = sequenceMapper.selectByEventId(tenantId, eventId);
        if (seq == null)
        {
            EventNurtureSequenceVo empty = new EventNurtureSequenceVo();
            empty.setEventId(eventId);
            empty.setName("默认培育序列");
            empty.setEnabled(false);
            empty.setTriggerType(EventNurtureTriggerType.MP_SUBSCRIBE_ATTRIBUTED.getCode());
            empty.setTriggerConfig(Collections.emptyMap());
            empty.setExitOnContact(true);
            empty.setStepCount(0);
            empty.setActiveEnrollmentCount(0);
            empty.setLegacyMpAutomationConflict(false);
            return empty;
        }
        return toVo(seq, event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventNurtureSequenceVo saveSequence(String tenantId, String eventId,
            EventNurtureSequenceSaveRequest request, String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = requireEvent(eventId);
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!EventNurtureTriggerType.isValid(request.getTriggerType()))
        {
            throw new ServiceException("非法触发器类型", HttpStatus.BAD_REQUEST);
        }

        boolean enabling = Boolean.TRUE.equals(request.getEnabled());
        CrmEventNurtureSequence seq = sequenceMapper.selectByEventId(tenantId, eventId);
        boolean isNew = seq == null;
        if (isNew)
        {
            seq = new CrmEventNurtureSequence();
            seq.setTenantId(tenantId);
            seq.setEventId(eventId);
            seq.setDelFlag("0");
            applySaveRequest(seq, request, operator, true);
            sequenceMapper.insert(seq);
        }
        else
        {
            applySaveRequest(seq, request, operator, false);
        }

        int stepCount = stepMapper.selectBySequenceId(seq.getId()).size();
        if (enabling && stepCount < 1)
        {
            throw new ServiceException("请先配置培育步骤", HttpStatus.BAD_REQUEST);
        }

        if (enabling)
        {
            disableLegacyMpAutomation(eventId);
        }

        sequenceMapper.update(seq);
        CrmEvent refreshedEvent = crmEventMapper.selectCrmEventByEventId(eventId);
        return toVo(sequenceMapper.selectByEventId(tenantId, eventId),
                refreshedEvent != null ? refreshedEvent : event);
    }

    @Override
    public List<EventNurtureStepVo> listSteps(String tenantId, String eventId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        CrmEventNurtureSequence seq = sequenceMapper.selectByEventId(tenantId, eventId);
        if (seq == null)
        {
            return Collections.emptyList();
        }
        return toStepVos(stepMapper.selectBySequenceId(seq.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<EventNurtureStepVo> replaceSteps(String tenantId, String eventId,
            EventNurtureStepsSaveRequest request)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        List<EventNurtureStepVo> steps = request != null ? request.getSteps() : null;
        if (steps == null)
        {
            steps = Collections.emptyList();
        }
        if (steps.size() > MAX_STEPS)
        {
            throw new ServiceException("步骤数不能超过 " + MAX_STEPS, HttpStatus.BAD_REQUEST);
        }
        validateSteps(steps);

        CrmEventNurtureSequence seq = sequenceMapper.selectByEventId(tenantId, eventId);
        if (seq == null)
        {
            seq = new CrmEventNurtureSequence();
            seq.setTenantId(tenantId);
            seq.setEventId(eventId);
            seq.setName("默认培育序列");
            seq.setEnabled("0");
            seq.setTriggerType(EventNurtureTriggerType.MP_SUBSCRIBE_ATTRIBUTED.getCode());
            seq.setTriggerConfig("{}");
            seq.setExitOnContact("1");
            seq.setDelFlag("0");
            sequenceMapper.insert(seq);
        }

        stepMapper.deleteBySequenceId(seq.getId());
        if (!steps.isEmpty())
        {
            List<CrmEventNurtureStep> rows = new ArrayList<>();
            for (EventNurtureStepVo vo : steps)
            {
                CrmEventNurtureStep row = new CrmEventNurtureStep();
                row.setTenantId(tenantId);
                row.setSequenceId(seq.getId());
                row.setStepOrder(vo.getStepOrder());
                row.setActionType(vo.getActionType());
                row.setDelaySeconds(vo.getDelaySeconds() != null ? vo.getDelaySeconds() : 0);
                row.setActionJson(EventNurtureJsonSupport.toJsonString(vo.getActionJson()));
                rows.add(row);
            }
            stepMapper.insertBatch(rows);
        }
        return toStepVos(stepMapper.selectBySequenceId(seq.getId()));
    }

    @Override
    public boolean hasActiveMpSubscribeSequence(String tenantId, String eventId)
    {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(eventId))
        {
            return false;
        }
        CrmEventNurtureSequence seq = sequenceMapper.selectEnabledByEventAndTrigger(
                tenantId, eventId, EventNurtureTriggerType.MP_SUBSCRIBE_ATTRIBUTED.getCode());
        return seq != null && seq.isEnabledFlag();
    }

    private void applySaveRequest(CrmEventNurtureSequence seq, EventNurtureSequenceSaveRequest request,
            String operator, boolean isNew)
    {
        if (StringUtils.isNotEmpty(request.getName()))
        {
            seq.setName(request.getName());
        }
        else if (isNew)
        {
            seq.setName("默认培育序列");
        }
        if (request.getEnabled() != null)
        {
            seq.setEnabled(Boolean.TRUE.equals(request.getEnabled()) ? "1" : "0");
        }
        else if (isNew)
        {
            seq.setEnabled("0");
        }
        if (StringUtils.isNotEmpty(request.getTriggerType()))
        {
            seq.setTriggerType(request.getTriggerType());
        }
        else if (isNew)
        {
            seq.setTriggerType(EventNurtureTriggerType.MP_SUBSCRIBE_ATTRIBUTED.getCode());
        }
        if (request.getTriggerConfig() != null)
        {
            seq.setTriggerConfig(JSON.toJSONString(request.getTriggerConfig()));
        }
        else if (isNew)
        {
            seq.setTriggerConfig("{}");
        }
        if (request.getExitOnContact() != null)
        {
            seq.setExitOnContact(Boolean.TRUE.equals(request.getExitOnContact()) ? "1" : "0");
        }
        else if (isNew)
        {
            seq.setExitOnContact("1");
        }
        if (isNew)
        {
            seq.setCreateBy(operator);
        }
        seq.setUpdateBy(operator);
    }

    private void validateSteps(List<EventNurtureStepVo> steps)
    {
        int expected = 1;
        for (EventNurtureStepVo step : steps)
        {
            if (step == null || step.getStepOrder() == null || step.getStepOrder() != expected)
            {
                throw new ServiceException("stepOrder 必须从 1 连续递增", HttpStatus.BAD_REQUEST);
            }
            if (!EventNurtureActionType.isValidP0(step.getActionType()))
            {
                throw new ServiceException("非法步骤类型: " + step.getActionType(), HttpStatus.BAD_REQUEST);
            }
            if (step.getDelaySeconds() != null && step.getDelaySeconds() < 0)
            {
                throw new ServiceException("delaySeconds 不能为负数", HttpStatus.BAD_REQUEST);
            }
            if (EventNurtureActionType.MP_TEMPLATE.getCode().equals(step.getActionType()))
            {
                Map<String, Object> action = step.getActionJson();
                Object presetId = action != null ? action.get("presetId") : null;
                if (presetId == null)
                {
                    throw new ServiceException("mp_template 步骤需要 presetId", HttpStatus.BAD_REQUEST);
                }
                try
                {
                    presetService.getById(Long.valueOf(presetId.toString()));
                }
                catch (Exception ex)
                {
                    throw new ServiceException("预设不存在或未启用", HttpStatus.BAD_REQUEST);
                }
            }
            expected++;
        }
    }

    private void disableLegacyMpAutomation(String eventId)
    {
        CrmEvent update = new CrmEvent();
        update.setEventId(eventId);
        update.setMpRegistrationAutomationEnabledRaw("0");
        crmEventMapper.updateCrmEvent(update);
    }

    private EventNurtureSequenceVo toVo(CrmEventNurtureSequence seq, CrmEvent event)
    {
        EventNurtureSequenceVo vo = new EventNurtureSequenceVo();
        vo.setId(seq.getId());
        vo.setEventId(seq.getEventId());
        vo.setName(seq.getName());
        vo.setEnabled(seq.isEnabledFlag());
        vo.setTriggerType(seq.getTriggerType());
        vo.setTriggerConfig(EventNurtureJsonSupport.parseObject(seq.getTriggerConfig()));
        vo.setExitOnContact(seq.isExitOnContactFlag());
        vo.setStepCount(stepMapper.selectBySequenceId(seq.getId()).size());
        vo.setActiveEnrollmentCount(enrollmentMapper.countActiveBySequenceId(seq.getId()));
        boolean legacyOn = event != null && "1".equals(event.getMpRegistrationAutomationEnabledRaw());
        vo.setLegacyMpAutomationConflict(vo.getEnabled() && legacyOn);
        vo.setUpdateTime(seq.getUpdateTime() != null ? seq.getUpdateTime() : new Date());
        return vo;
    }

    private List<EventNurtureStepVo> toStepVos(List<CrmEventNurtureStep> steps)
    {
        List<EventNurtureStepVo> result = new ArrayList<>();
        if (steps == null)
        {
            return result;
        }
        for (CrmEventNurtureStep step : steps)
        {
            EventNurtureStepVo vo = new EventNurtureStepVo();
            vo.setId(step.getId());
            vo.setStepOrder(step.getStepOrder());
            vo.setActionType(step.getActionType());
            vo.setDelaySeconds(step.getDelaySeconds());
            vo.setActionJson(EventNurtureJsonSupport.parseObject(step.getActionJson()));
            result.add(vo);
        }
        return result;
    }

    private CrmEvent requireEvent(String eventId)
    {
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
        }
        return event;
    }
}
