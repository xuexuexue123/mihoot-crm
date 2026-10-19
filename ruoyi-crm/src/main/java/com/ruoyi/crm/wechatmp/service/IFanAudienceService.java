package com.ruoyi.crm.wechatmp.service;

import java.util.List;

import com.ruoyi.crm.wechatmp.audience.dto.FanAudiencePreviewRequest;
import com.ruoyi.crm.wechatmp.audience.vo.FanAudienceFieldMetaVo;
import com.ruoyi.crm.wechatmp.audience.vo.FanAudiencePreviewVo;

public interface IFanAudienceService
{
    List<FanAudienceFieldMetaVo> listFields();

    FanAudiencePreviewVo preview(FanAudiencePreviewRequest request);
}
