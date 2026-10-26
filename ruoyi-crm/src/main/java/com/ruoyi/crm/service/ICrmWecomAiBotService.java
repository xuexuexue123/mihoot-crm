package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.dto.wecom.WecomAiBotConfigSaveRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomAiBotStatusReportRequest;
import com.ruoyi.crm.domain.dto.wecom.ResolveUserResult;
import com.ruoyi.crm.domain.dto.wecom.TenantBotCommand;

public interface ICrmWecomAiBotService
{
    Map<String, Object> getConfigMasked(String tenant);

    Map<String, Object> saveOrUpdateConfig(WecomAiBotConfigSaveRequest body, String tenant);

    Map<String, Object> getStatus(String tenant);

    Map<String, Object> requestConnect(String tenant);

    Map<String, Object> requestDisconnect(String tenant);

    /** 安全地将连接状态设为 connecting，仅当现状为 disconnected 或无记录时生效 */
    boolean trySetConnecting(String tenant, String botId);

    /** 获取当前连接状态字符串，无记录时返回 null */
    String getConnectionStatus(String tenant);

    List<TenantBotCommand> getPendingCommands();

    void reportStatus(WecomAiBotStatusReportRequest body);

    /**
     * 根据企业微信 userid 解析 CRM 用户
     * @param userid 企业微信 userid（对应 sys_user.sn_userid）
     * @param tenant 租户标识，传入时仅在指定租户下查找，不跨租户扫描
     * @return 用户信息；若未找到返回 null
     */
    ResolveUserResult resolveUser(String userid, String tenant);
}
