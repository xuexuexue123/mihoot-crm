package com.ruoyi.crm.wechatmp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompileOptions;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompileResult;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompiler;
import com.ruoyi.crm.wechatmp.audience.FanAudienceFieldRegistry;
import com.ruoyi.crm.wechatmp.audience.dto.FanAudiencePreviewRequest;
import com.ruoyi.crm.wechatmp.audience.vo.FanAudienceFieldMetaVo;
import com.ruoyi.crm.wechatmp.audience.vo.FanAudiencePreviewVo;
import com.ruoyi.crm.wechatmp.service.IFanAudienceService;

@Service
public class FanAudienceServiceImpl implements IFanAudienceService
{
    private static final int SAMPLE_LIMIT = 10;

    @Autowired
    private FanAudienceCompiler fanAudienceCompiler;

    @Override
    public List<FanAudienceFieldMetaVo> listFields()
    {
        return FanAudienceFieldRegistry.allFieldMeta();
    }

    @Override
    public FanAudiencePreviewVo preview(FanAudiencePreviewRequest request)
    {
        if (request == null || !StringUtils.hasText(request.getAppId()) || request.getFilterJson() == null)
        {
            throw new ServiceException("appId 和 filterJson 不能为空", HttpStatus.BAD_REQUEST);
        }
        FanAudienceCompileResult result = fanAudienceCompiler.compile(
                request.getAppId(), request.getFilterJson(), FanAudienceCompileOptions.preview());
        Set<String> openIds = result.getOpenIds();
        List<String> sample = new ArrayList<>();
        for (String openId : openIds)
        {
            sample.add(openId);
            if (sample.size() >= SAMPLE_LIMIT)
            {
                break;
            }
        }
        return new FanAudiencePreviewVo(openIds.size(), sample);
    }
}
