package com.ruoyi.crm.mobile.push;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmMobilePushDevice;
import com.ruoyi.crm.mapper.InboxMobilePushBadgeMapper;

@Service
public class InboxMobilePushService
{
    private static final Logger log = LoggerFactory.getLogger(InboxMobilePushService.class);

    @Autowired
    private MobilePushProperties properties;

    @Autowired
    private MobilePushDeviceService deviceService;

    @Autowired
    private FcmPushClient fcmPushClient;

    @Autowired
    private InboxMobilePushBadgeMapper badgeMapper;

    public void dispatch(InboxMobilePushRequest request)
    {
        if (request == null || !properties.isEnabled())
        {
            return;
        }
        if (!StringUtils.hasText(request.getTenantId())
                || !StringUtils.hasText(request.getChannel())
                || !StringUtils.hasText(request.getConversationKey())
                || request.getOwnerUserId() == null)
        {
            return;
        }

        List<CrmMobilePushDevice> devices = deviceService.listByUser(
                request.getTenantId(), request.getOwnerUserId());
        if (devices == null || devices.isEmpty())
        {
            return;
        }

        if (!properties.isReady())
        {
            log.debug("Mobile push enabled but FCM not configured; skip tenant={}", request.getTenantId());
            return;
        }

        String title = InboxMobilePushPayloadBuilder.buildTitle(
                request.getChannel(), request.getPeerDisplayName());
        String body = InboxMobilePushPayloadBuilder.buildBody(
                request.getMessageType(), request.getMessageContent());
        int badge = resolveBadge(request);

        Map<String, String> data = new LinkedHashMap<>();
        data.put("channel", request.getChannel());
        data.put("conversationKey", request.getConversationKey());
        if (request.getConversationId() != null)
        {
            data.put("conversationId", String.valueOf(request.getConversationId()));
        }
        data.put("badge", String.valueOf(badge));

        for (CrmMobilePushDevice device : devices)
        {
            if (device == null || !StringUtils.hasText(device.getPushToken()))
            {
                continue;
            }
            try
            {
                fcmPushClient.sendNotification(
                        device.getPushToken(), title, body, data, badge, device.getPlatform());
            }
            catch (FcmPushClient.FcmUnregisteredTokenException e)
            {
                deviceService.deleteStaleToken(request.getTenantId(), e.getPushToken());
            }
            catch (Exception e)
            {
                log.warn("Mobile push dispatch failed tenant={} user={}: {}",
                        request.getTenantId(), request.getOwnerUserId(), e.getMessage());
            }
        }
    }

    private int resolveBadge(InboxMobilePushRequest request)
    {
        try
        {
            int sum = badgeMapper.sumMineUnread(request.getTenantId(), request.getOwnerUserId());
            if (sum > 0)
            {
                return sum;
            }
        }
        catch (Exception e)
        {
            log.debug("Badge sum failed tenant={}: {}", request.getTenantId(), e.getMessage());
        }
        if (request.getConversationUnread() != null && request.getConversationUnread() > 0)
        {
            return request.getConversationUnread();
        }
        return 1;
    }
}
