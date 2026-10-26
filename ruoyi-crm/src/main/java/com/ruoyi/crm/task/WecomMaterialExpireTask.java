package com.ruoyi.crm.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.crm.service.ICrmWecomMaterialService;

/**
 * 企业微信素材过期状态更新定时任务。
 * 扫描 status = 'uploaded' 且 expires_at 已过期的记录，将 status 更新为 'expired'。
 * 在「定时任务」中配置调用目标：wecomMaterialExpireTask.updateExpiredStatus()，
 * Cron 示例：0 0 * * * ?（每小时执行一次）。
 */
@Component("wecomMaterialExpireTask")
public class WecomMaterialExpireTask
{
    private static final Logger log = LoggerFactory.getLogger(WecomMaterialExpireTask.class);

    @Autowired
    private ICrmWecomMaterialService crmWecomMaterialService;

    /**
     * 扫描并更新过期素材状态。
     * 建议每小时执行一次（Cron: 0 0 * * * ?）。
     */
    public void updateExpiredStatus()
    {
        try
        {
            crmWecomMaterialService.updateExpiredStatus();
        }
        catch (Exception e)
        {
            log.error("更新过期素材状态失败: {}", e.getMessage(), e);
        }
    }
}
