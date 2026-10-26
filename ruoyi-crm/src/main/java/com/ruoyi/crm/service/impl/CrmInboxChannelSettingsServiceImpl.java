package com.ruoyi.crm.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmInboxChannelSettings;
import com.ruoyi.crm.domain.dto.InboxChannelSettingsUpdateRequest;
import com.ruoyi.crm.domain.dto.InboxChannelSettingsView;
import com.ruoyi.crm.mapper.CrmInboxChannelSettingsMapper;
import com.ruoyi.crm.service.IInboxChannelSettingsService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.wecom.realtime.WecomKfInboxSseHub;

@Service
public class CrmInboxChannelSettingsServiceImpl implements IInboxChannelSettingsService
{
    private static final DateTimeFormatter ISO_INSTANT = DateTimeFormatter.ISO_INSTANT;

    @Autowired
    private CrmInboxChannelSettingsMapper settingsMapper;

    @Autowired
    private InboxChannelGateService gateService;

    @Autowired
    private WecomKfInboxSseHub wecomKfInboxSseHub;

    @Override
    public InboxChannelSettingsView getView(String tenantId)
    {
        requireTenant(tenantId);
        return toView(gateService.loadSettings(tenantId));
    }

    @Override
    @Transactional
    public InboxChannelSettingsView updatePartial(String tenantId, InboxChannelSettingsUpdateRequest request,
            String operator)
    {
        requireTenant(tenantId);
        CrmInboxChannelSettings current = gateService.loadSettings(tenantId);
        boolean wasWecomKfEnabled = current.isWecomKfEnabled();
        mergePartial(current, request);
        if (!current.isWhatsappEnabled() && !current.isMessengerEnabled()
                && !current.isWechatMpEnabled() && !current.isWebchatEnabled()
                && !current.isWecomKfEnabled())
        {
            throw new ServiceException("inbox_channels_all_disabled", HttpStatus.BAD_REQUEST);
        }
        current.setUpdatedBy(StringUtils.isNotBlank(operator) ? operator.trim() : null);
        current.setUpdatedAt(new Date());
        settingsMapper.upsert(current);
        gateService.evict(tenantId);
        if (wasWecomKfEnabled && !current.isWecomKfEnabled())
        {
            wecomKfInboxSseHub.closeTenant(tenantId);
        }
        return toView(current);
    }

    static void mergePartial(CrmInboxChannelSettings current, InboxChannelSettingsUpdateRequest request)
    {
        if (request == null)
        {
            return;
        }
        if (request.getWhatsapp() != null)
        {
            current.setWhatsappEnabled(request.getWhatsapp().isEnabled());
        }
        if (request.getMessenger() != null)
        {
            current.setMessengerEnabled(request.getMessenger().isEnabled());
        }
        if (request.getWechatMp() != null)
        {
            current.setWechatMpEnabled(request.getWechatMp().isEnabled());
        }
        if (request.getWebchat() != null)
        {
            current.setWebchatEnabled(request.getWebchat().isEnabled());
        }
        if (request.getWecomKf() != null)
        {
            current.setWecomKfEnabled(request.getWecomKf().isEnabled());
        }
    }

    static InboxChannelSettingsView toView(CrmInboxChannelSettings settings)
    {
        InboxChannelSettingsView view = new InboxChannelSettingsView();
        view.setWhatsapp(new InboxChannelSettingsView.ChannelFlag(settings.isWhatsappEnabled()));
        view.setMessenger(new InboxChannelSettingsView.ChannelFlag(settings.isMessengerEnabled()));
        view.setWechatMp(new InboxChannelSettingsView.ChannelFlag(settings.isWechatMpEnabled()));
        view.setWebchat(new InboxChannelSettingsView.ChannelFlag(settings.isWebchatEnabled()));
        view.setWecomKf(new InboxChannelSettingsView.ChannelFlag(settings.isWecomKfEnabled()));
        if (settings.getUpdatedAt() != null)
        {
            view.setUpdatedAt(ISO_INSTANT.format(settings.getUpdatedAt().toInstant().atOffset(ZoneOffset.UTC)));
        }
        view.setUpdatedBy(settings.getUpdatedBy());
        return view;
    }

    private static void requireTenant(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("Missing tenant header", HttpStatus.BAD_REQUEST);
        }
    }
}
