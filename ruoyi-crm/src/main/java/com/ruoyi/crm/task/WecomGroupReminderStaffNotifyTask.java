package com.ruoyi.crm.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.crm.service.wecom.contactgroup.WecomContactGroupConstants;
import com.ruoyi.crm.service.wecom.contactgroup.WecomGroupReminderStaffNotifyService;

/**
 * 群提醒到期：应用消息通知员工到服务群手发话术。
 * invoke_target: wecomGroupReminderStaffNotifyTask.dispatchDue()
 */
@Component("wecomGroupReminderStaffNotifyTask")
public class WecomGroupReminderStaffNotifyTask
{
    private static final Logger log = LoggerFactory.getLogger(WecomGroupReminderStaffNotifyTask.class);

    @Autowired
    private WecomGroupReminderStaffNotifyService staffNotifyService;

    public void dispatchDue()
    {
        int n = staffNotifyService.dispatchDueAcrossTenants(WecomContactGroupConstants.DEFAULT_PENDING_LIMIT);
        if (n > 0)
        {
            log.info("wecom group reminder staff-notify processed={}", n);
        }
    }
}
