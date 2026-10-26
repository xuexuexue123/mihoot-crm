package com.ruoyi.crm.mobile.push;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmMobilePushDevice;
import com.ruoyi.crm.domain.dto.MobileDeviceRegisterRequest;
import com.ruoyi.crm.mapper.CrmMobilePushDeviceMapper;

@Service
public class MobilePushDeviceService
{
    private static final Logger log = LoggerFactory.getLogger(MobilePushDeviceService.class);

    @Autowired
    private CrmMobilePushDeviceMapper deviceMapper;

    public void register(String tenantId, Long userId, MobileDeviceRegisterRequest body)
    {
        if (!StringUtils.hasText(tenantId))
        {
            throw new ServiceException("Missing tenant header", HttpStatus.BAD_REQUEST);
        }
        if (userId == null)
        {
            throw new ServiceException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (body == null || !StringUtils.hasText(body.getPushToken()))
        {
            throw new ServiceException("pushToken is required", HttpStatus.BAD_REQUEST);
        }
        String platform = normalizePlatform(body.getPlatform());
        if (platform == null)
        {
            throw new ServiceException("platform must be ios or android", HttpStatus.BAD_REQUEST);
        }

        Date now = new Date();
        CrmMobilePushDevice row = new CrmMobilePushDevice();
        row.setTenantId(tenantId.trim());
        row.setUserId(userId);
        row.setPlatform(platform);
        row.setPushToken(body.getPushToken().trim());
        if (StringUtils.hasText(body.getAppVersion()))
        {
            row.setAppVersion(body.getAppVersion().trim());
        }
        row.setCreateTime(now);
        row.setUpdateTime(now);
        deviceMapper.upsert(row);
        log.debug("Mobile push device registered tenant={} user={} platform={}", tenantId, userId, platform);
    }

    public void unregister(String tenantId, Long userId, String pushToken)
    {
        if (!StringUtils.hasText(tenantId))
        {
            throw new ServiceException("Missing tenant header", HttpStatus.BAD_REQUEST);
        }
        if (userId == null)
        {
            throw new ServiceException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (StringUtils.hasText(pushToken))
        {
            deviceMapper.deleteByToken(tenantId.trim(), userId, pushToken.trim());
        }
        else
        {
            deviceMapper.deleteAllByUser(tenantId.trim(), userId);
        }
    }

    public List<CrmMobilePushDevice> listByUser(String tenantId, Long userId)
    {
        return deviceMapper.selectByUser(tenantId, userId);
    }

    public void deleteStaleToken(String tenantId, String pushToken)
    {
        if (!StringUtils.hasText(tenantId) || !StringUtils.hasText(pushToken))
        {
            return;
        }
        try
        {
            deviceMapper.deleteByTokenOnly(tenantId.trim(), pushToken.trim());
        }
        catch (Exception e)
        {
            log.warn("Failed to delete stale push token tenant={}: {}", tenantId, e.getMessage());
        }
    }

    private static String normalizePlatform(String platform)
    {
        if (!StringUtils.hasText(platform))
        {
            return null;
        }
        String p = platform.trim().toLowerCase();
        if ("ios".equals(p) || "android".equals(p))
        {
            return p;
        }
        return null;
    }
}
