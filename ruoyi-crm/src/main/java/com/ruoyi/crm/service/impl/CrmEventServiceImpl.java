package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventCheckInMapper;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmEventSessionMapper;
import com.ruoyi.crm.service.ICrmEventService;
import com.ruoyi.crm.service.ICrmEventSpeakerService;
import com.ruoyi.crm.service.support.OutreachLeadValidationSupport;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.service.EventWechatMpAppIdResolver;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;
import com.ruoyi.ems.service.ISysReTemplateService;

/**
 * 业务事件Service业务层处理
 * 
 * @author luohd
 * @date 2025-03-15
 */
@Service
public class CrmEventServiceImpl implements ICrmEventService 
{
    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private CrmEventCheckInMapper checkInMapper;

    @Autowired
    private CrmEventRegistrationMapper registrationMapper;

    @Autowired
    private CrmEventSessionMapper sessionMapper;

    @Autowired
    private ICrmEventSpeakerService speakerService;

    @Autowired
    private ISysReTemplateService sysReTemplateService;

    @Autowired
    private IWechatMpMessagePresetService wechatMpMessagePresetService;

    @Autowired
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Autowired
    private EventWechatMpAppIdResolver eventWechatMpAppIdResolver;

    /**
     * 查询业务事件
     * 
     * @param eventId 业务事件主键
     * @return 业务事件
     */
    @Override
    public CrmEvent selectCrmEventByEventId(String eventId)
    {
        return crmEventMapper.selectCrmEventByEventId(eventId);
    }

    /**
     * 查询业务事件列表
     * 
     * @param crmEvent 业务事件
     * @return 业务事件
     */
    @Override
    public List<CrmEvent> selectCrmEventList(CrmEvent crmEvent)
    {
        return crmEventMapper.selectCrmEventList(crmEvent);
    }

    /**
     * 新增业务事件
     * 
     * @param crmEvent 业务事件
     * @return 结果
     */
    @Override
    public int insertCrmEvent(CrmEvent crmEvent)
    {
        OutreachLeadValidationSupport.validateRegistrationFormConfig(crmEvent, sysReTemplateService);
        normalizeAndValidateWechatMpAppId(crmEvent);
        validateMpRegistrationAutomation(crmEvent);
        crmEvent.setCreateTime(DateUtils.getNowDate());
        return crmEventMapper.insertCrmEvent(crmEvent);
    }

    /**
     * 修改业务事件
     * 
     * @param crmEvent 业务事件
     * @return 结果
     */
    @Override
    public int updateCrmEvent(CrmEvent crmEvent)
    {
        OutreachLeadValidationSupport.validateRegistrationFormConfig(crmEvent, sysReTemplateService);
        normalizeAndValidateWechatMpAppId(crmEvent);
        validateMpRegistrationAutomation(crmEvent);
        crmEvent.setUpdateTime(DateUtils.getNowDate());
        return crmEventMapper.updateCrmEvent(crmEvent);
    }

    private void normalizeAndValidateWechatMpAppId(CrmEvent crmEvent)
    {
        if (crmEvent == null)
        {
            return;
        }
        if (crmEvent.getWechatMpAppId() != null && crmEvent.getWechatMpAppId().trim().isEmpty())
        {
            crmEvent.setWechatMpAppId(null);
        }
        String appId = crmEvent.getWechatMpAppId();
        if (StringUtils.isBlank(appId))
        {
            return;
        }
        CrmWechatMpConfig cfg = wechatMpConfigMapper.selectByAppId(appId.trim());
        if (cfg == null)
        {
            throw new ServiceException("wechat_mp_app_id_invalid", HttpStatus.BAD_REQUEST);
        }
        if (!Boolean.TRUE.equals(cfg.getConnected()) || StringUtils.isBlank(cfg.getSecretEnc()))
        {
            throw new ServiceException("wechat_mp_app_id_not_connected", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateMpRegistrationAutomation(CrmEvent crmEvent)
    {
        if (crmEvent == null || !Boolean.TRUE.equals(crmEvent.getMpRegistrationAutomationEnabled()))
        {
            return;
        }
        if (crmEvent.getMpRegistrationPresetId() == null)
        {
            throw new ServiceException("开启公众号报名自动化时须指定消息预设", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpMessagePreset preset;
        try
        {
            preset = wechatMpMessagePresetService.getById(crmEvent.getMpRegistrationPresetId());
        }
        catch (Exception ex)
        {
            throw new ServiceException("消息预设不存在", HttpStatus.BAD_REQUEST);
        }
        if (!"0".equals(preset.getStatus()))
        {
            throw new ServiceException("消息预设已停用", HttpStatus.BAD_REQUEST);
        }
        String resolvedAppId = eventWechatMpAppIdResolver.resolve(crmEvent);
        if (StringUtils.isBlank(resolvedAppId))
        {
            throw new ServiceException("请先配置活动默认公众号或创建推广码", HttpStatus.BAD_REQUEST);
        }
        if (!resolvedAppId.equals(preset.getAppId()))
        {
            throw new ServiceException("消息预设 appId 与活动公众号不一致", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 批量删除业务事件
     * 
     * @param eventIds 需要删除的业务事件主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteCrmEventByEventIds(String[] eventIds)
    {
        if (eventIds == null || eventIds.length == 0)
        {
            return 0;
        }
        checkInMapper.deleteByEventIds(eventIds);
        registrationMapper.deleteByEventIds(eventIds);
        sessionMapper.deleteByEventIds(eventIds);
        speakerService.deleteSpeakerByEventIds(eventIds);
        return crmEventMapper.deleteCrmEventByEventIds(eventIds);
    }

    /**
     * 删除业务事件信息
     * 
     * @param eventId 业务事件主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteCrmEventByEventId(String eventId)
    {
        return deleteCrmEventByEventIds(new String[] { eventId });
    }
}
