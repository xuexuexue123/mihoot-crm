package com.ruoyi.crm.wechatminiapp.service;

import java.util.Map;

import com.ruoyi.crm.wechatminiapp.dto.WechatMiniappConfigSaveRequest;

public interface ICrmWechatMiniappConfigService
{
    /**
     * 当前租户脱敏配置；无记录返回 null。
     */
    Map<String, Object> getConfigMasked();

    /**
     * 服务账号专用：返回含 AppSecret 明文的 runtime 配置；无记录返回 null。
     * 禁止对顾问 JWT 暴露（由权限 crm:wechat_miniapp:runtime 约束）。
     */
    Map<String, Object> getConfigRuntime();

    /**
     * Upsert 当前租户唯一配置；secret 缺省则保留原密文。
     */
    void saveOrUpdate(WechatMiniappConfigSaveRequest body);

    /**
     * 删除当前租户配置（单例行）。
     */
    void delete();
}
