package com.ruoyi.crm.wechatmp.automation;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.manager.AsyncManager;

@Component
public class EventMpRegistrationAutomationWorker
{
    private static final Logger log = LoggerFactory.getLogger(EventMpRegistrationAutomationWorker.class);

    @Autowired
    private EventMpRegistrationAutomationService automationService;

    public void scheduleAttributed(EventMpOutreachContext ctx)
    {
        if (ctx == null || ctx.getTenantId() == null)
        {
            return;
        }
        final String tenantKey = ctx.getTenantId();
        final EventMpOutreachContext copy = ctx;
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(tenantKey);
                    automationService.processAttributedSubscribe(copy);
                }
                catch (Exception ex)
                {
                    log.error("MP registration automation failed tenant={} eventId={} openId={}",
                            tenantKey, copy.getCrmEventId(), copy.getOpenId(), ex);
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
    }
}
