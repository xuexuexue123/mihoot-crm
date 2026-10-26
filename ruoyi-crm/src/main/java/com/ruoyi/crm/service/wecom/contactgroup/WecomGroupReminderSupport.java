package com.ruoyi.crm.service.wecom.contactgroup;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;

/**
 * 群提醒员工通知：就绪判定、文案、touser 解析（纯逻辑便于单测）。
 */
public final class WecomGroupReminderSupport
{
    private WecomGroupReminderSupport()
    {
    }

    /**
     * MVP 就绪：有非空 chat_id 且非 dismissed（不要求 Bot active）。
     */
    public static boolean isServiceGroupReady(CrmWecomContactGroup group)
    {
        if (group == null || StringUtils.isBlank(group.getChatId()))
        {
            return false;
        }
        return !WecomContactGroupConstants.GROUP_DISMISSED.equalsIgnoreCase(
                group.getStatus() == null ? "" : group.getStatus().trim());
    }

    public static String displayGroupName(CrmWecomContactGroup group)
    {
        if (group != null && StringUtils.isNotBlank(group.getGroupName()))
        {
            return group.getGroupName().trim();
        }
        return "未命名服务群";
    }

    public static String buildStaffNotifyContent(String contactName, String groupName, String chatId,
            String renderedText)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("【群提醒】请到客户服务群发送话术\n\n");
        sb.append("客户：").append(nullToEmpty(contactName)).append('\n');
        sb.append("服务群：").append(nullToEmpty(groupName)).append('\n');
        sb.append("chat_id：").append(nullToEmpty(chatId)).append("\n\n");
        sb.append("建议发送内容：\n");
        sb.append(nullToEmpty(renderedText)).append("\n\n");
        sb.append("请打开上述服务群，将「建议发送内容」发给客户。");
        return sb.toString();
    }

    public static String buildNoGroupNudgeContent(String contactName)
    {
        return "【群提醒】客户 " + nullToEmpty(contactName)
                + " 的复诊/预约提醒已到期，但尚未绑定服务群。请先完成一客一群建群后再发送话术。";
    }

    public static final String TEXTCARD_TITLE = "群提醒：请到服务群发送话术";
    public static final String TEXTCARD_BTN = "去发送";
    public static final int TEXTCARD_SNIPPET_MAX = 80;

    /**
     * textcard description：客户 / 服务群 / 话术截断。
     */
    public static String buildTextcardDescription(String contactName, String groupName, String renderedText)
    {
        String snippet = truncate(nullToEmpty(renderedText), TEXTCARD_SNIPPET_MAX);
        return "客户：" + nullToEmpty(contactName) + "\n服务群：" + nullToEmpty(groupName) + "\n" + snippet;
    }

    /**
     * H5 深链：{origin}/wecom/group-reminder-send?id={reminderId}
     */
    public static String buildSendPageUrl(String tenantPublicOrigin, String reminderId)
    {
        String origin = tenantPublicOrigin == null ? "" : tenantPublicOrigin.trim().replaceAll("/+$", "");
        if (origin.isEmpty() || StringUtils.isBlank(reminderId))
        {
            return "";
        }
        return origin + "/wecom/group-reminder-send?id=" + reminderId.trim();
    }

    public static String truncate(String text, int maxChars)
    {
        if (text == null)
        {
            return "";
        }
        String t = text.trim();
        if (t.length() <= maxChars)
        {
            return t;
        }
        return t.substring(0, maxChars) + "…";
    }

    /**
     * contact.userId → SysUser.snUserid，否则 wecomFollowUserid。
     */
    public static String resolveSnUserid(CrmContact contact, SysUserMapper sysUserMapper)
    {
        if (contact == null)
        {
            return null;
        }
        if (contact.getUserId() != null && sysUserMapper != null)
        {
            SysUser user = sysUserMapper.selectUserById(contact.getUserId());
            if (user != null && StringUtils.isNotBlank(user.getSnUserid()))
            {
                return user.getSnUserid().trim();
            }
        }
        if (StringUtils.isNotBlank(contact.getWecomFollowUserid()))
        {
            return contact.getWecomFollowUserid().trim();
        }
        return null;
    }

    static String nullToEmpty(String s)
    {
        return s == null ? "" : s;
    }
}
