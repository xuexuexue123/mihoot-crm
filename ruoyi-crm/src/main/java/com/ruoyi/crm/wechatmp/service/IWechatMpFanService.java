package com.ruoyi.crm.wechatmp.service;

import java.util.List;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.dto.WechatMpFanSyncRequest;

public interface IWechatMpFanService
{
    List<CrmWechatMpFan> selectFanList(CrmWechatMpFan query);

    void scheduleSync(WechatMpFanSyncRequest req);
}
