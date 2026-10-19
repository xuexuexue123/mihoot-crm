package com.ruoyi.crm.service.wecom.contactgroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomGroupReminderRule;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderRuleMapper;

/**
 * 幂等写入系统群提醒种子规则
 */
@Component
public class WecomGroupReminderRuleSystemSeeder
{
    @Autowired
    private CrmWecomGroupReminderRuleMapper ruleMapper;

    public synchronized void ensure(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            return;
        }
        seed(tenantId, WecomContactGroupConstants.SYS_BOOKING_1D, "预约前 1 天",
                "预约开始前 1 天 09:00 提醒", 10,
                WecomContactGroupConstants.ANCHOR_BOOKING_START, -1, WecomContactGroupConstants.OFFSET_DAYS, "09:00",
                "您好{{name}}，明天 {{time}} 您有预约（{{booking_name}}），请准时到院。如需改期请联系 {{staff}}。",
                null);
        seed(tenantId, WecomContactGroupConstants.SYS_BOOKING_2H, "预约前 2 小时",
                "预约开始前 2 小时提醒", 20,
                WecomContactGroupConstants.ANCHOR_BOOKING_START, -2, WecomContactGroupConstants.OFFSET_HOURS, null,
                "您好{{name}}，约 2 小时后（{{time}}）您有预约，请安排出行。有事请联系 {{staff}}。",
                null);
        seed(tenantId, WecomContactGroupConstants.SYS_POSTOP_7D, "术后 7 天复诊",
                "赢单后 7 天术后关怀提醒", 30,
                WecomContactGroupConstants.ANCHOR_OPPORTUNITY_WON, 7, WecomContactGroupConstants.OFFSET_DAYS, "09:00",
                "您好{{name}}，术后已满 {{offset_days}} 天，建议安排复诊复查。请联系 {{staff}} 预约。",
                "{\"treatmentItemCodes\":[\"种植牙\",\"implant\",\"种植\"]}");
        seed(tenantId, WecomContactGroupConstants.SYS_RECALL_DUE, "复诊到期提醒",
                "复诊日程到期日提醒患者", 40,
                WecomContactGroupConstants.ANCHOR_RECALL_DUE, 0, WecomContactGroupConstants.OFFSET_DAYS, "09:00",
                "您好{{name}}，今日起建议安排复诊（{{treatment}}）。请联系 {{staff}}。",
                null);
    }

    private void seed(String tenantId, String ruleId, String name, String description, int priority,
            String anchorType, int offsetValue, String offsetUnit, String sendTime, String template, String matchJson)
    {
        if (ruleMapper.selectById(tenantId, ruleId) != null)
        {
            return;
        }
        CrmWecomGroupReminderRule row = new CrmWecomGroupReminderRule();
        row.setRuleId(ruleId);
        row.setTenantId(tenantId);
        row.setName(name);
        row.setDescription(description);
        row.setEnabled(WecomContactGroupConstants.ENABLED);
        row.setIsSystem(WecomContactGroupConstants.IS_SYSTEM);
        row.setPriority(priority);
        row.setAnchorType(anchorType);
        row.setOffsetValue(offsetValue);
        row.setOffsetUnit(offsetUnit);
        row.setSendTime(sendTime);
        row.setTemplateBody(template);
        row.setMatchJson(matchJson);
        row.setRequireActiveGroup(WecomContactGroupConstants.ENABLED);
        row.setDelFlag("0");
        row.setCreateBy("system");
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy("system");
        row.setUpdateTime(DateUtils.getNowDate());
        try
        {
            ruleMapper.insert(row);
        }
        catch (Exception ignored)
        {
        }
    }
}
