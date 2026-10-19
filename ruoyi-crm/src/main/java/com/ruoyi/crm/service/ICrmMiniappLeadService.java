package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.MiniappLeadUpsertRequest;
import com.ruoyi.crm.domain.vo.MiniappLeadByMobileVo;
import com.ruoyi.crm.domain.vo.MiniappLeadUpsertResultVo;

/**
 * 微信小程序 BFF 线索查建
 */
public interface ICrmMiniappLeadService
{
    /**
     * 按规范化手机号查活跃未转化 Lead；无则 null。
     */
    MiniappLeadByMobileVo findByMobile(String mobilePhone);

    /**
     * 按手机号幂等 upsert；同号不产生第二条活跃 Lead。
     */
    MiniappLeadUpsertResultVo upsert(MiniappLeadUpsertRequest request);
}
