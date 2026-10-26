package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.dto.CreateTenantWebsitePageRequest;
import com.ruoyi.crm.domain.dto.SaveTenantWebsitePageDraftRequest;
import com.ruoyi.crm.domain.vo.PreviewTokenVo;
import com.ruoyi.crm.domain.vo.TenantWebsitePageDetailVo;
import com.ruoyi.crm.domain.vo.TenantWebsitePageSummaryVo;

public interface ICrmTenantWebsitePageService
{
    List<TenantWebsitePageSummaryVo> listSummaries();

    TenantWebsitePageDetailVo getDetail(String id);

    TenantWebsitePageDetailVo create(CreateTenantWebsitePageRequest request);

    void saveDraft(String id, SaveTenantWebsitePageDraftRequest request);

    Map<String, Object> publish(String id);

    Map<String, Object> unpublish(String id);

    void delete(String id);

    PreviewTokenVo issuePreviewToken(String id);
}
