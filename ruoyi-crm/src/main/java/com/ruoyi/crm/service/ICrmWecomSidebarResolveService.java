package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.wecom.WecomSidebarGroupResolveResultDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto;

public interface ICrmWecomSidebarResolveService
{
    WecomSidebarResolveResultDto resolve(String externalUserid);

    /**
     * 群聊工具栏按 chatId 瘦 resolve。
     */
    WecomSidebarGroupResolveResultDto resolveGroup(String chatId);
}
