package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;
import com.ruoyi.crm.domain.vo.WebsiteLeadSubmitResultVo;

public interface IWebsiteOnlineRegisterService
{
    WebsiteLeadSubmitResultVo submit(WebsiteLeadSubmitRequest request);
}
