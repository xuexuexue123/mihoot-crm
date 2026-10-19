package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmProfessionalService;
import com.ruoyi.crm.domain.dto.ProfessionalServiceBatchRequest;
import com.ruoyi.crm.domain.dto.ProfessionalServiceBatchResult;

/**
 * 专业服务机构 Service
 */
public interface ICrmProfessionalServiceService
{
    CrmProfessionalService selectCrmProfessionalServiceById(String id);

    List<CrmProfessionalService> selectCrmProfessionalServiceList(CrmProfessionalService query);

    CrmProfessionalService insertCrmProfessionalService(CrmProfessionalService row);

    CrmProfessionalService updateCrmProfessionalService(CrmProfessionalService row);

    int deleteCrmProfessionalServiceById(String id);

    ProfessionalServiceBatchResult batch(ProfessionalServiceBatchRequest request);
}
