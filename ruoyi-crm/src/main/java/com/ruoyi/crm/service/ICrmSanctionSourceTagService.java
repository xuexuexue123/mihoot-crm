package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmSanctionSourceTag;
import com.ruoyi.crm.domain.dto.SanctionSourceTagBatchRequest;
import com.ruoyi.crm.domain.dto.SanctionSourceTagBatchResult;

/**
 * 制裁源标签 Service
 */
public interface ICrmSanctionSourceTagService
{
    CrmSanctionSourceTag selectCrmSanctionSourceTagById(String id);

    List<CrmSanctionSourceTag> selectCrmSanctionSourceTagList(CrmSanctionSourceTag query);

    CrmSanctionSourceTag insertCrmSanctionSourceTag(CrmSanctionSourceTag row);

    CrmSanctionSourceTag updateCrmSanctionSourceTag(CrmSanctionSourceTag row);

    int deleteCrmSanctionSourceTagById(String id);

    SanctionSourceTagBatchResult batch(SanctionSourceTagBatchRequest request);
}
