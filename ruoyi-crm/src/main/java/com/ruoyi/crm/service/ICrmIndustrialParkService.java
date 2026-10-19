package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmIndustrialPark;
import com.ruoyi.crm.domain.dto.IndustrialParkBatchRequest;
import com.ruoyi.crm.domain.dto.IndustrialParkBatchResult;

/**
 * 工业园区 Service
 */
public interface ICrmIndustrialParkService
{
    CrmIndustrialPark selectCrmIndustrialParkById(String id);

    List<CrmIndustrialPark> selectCrmIndustrialParkList(CrmIndustrialPark query);

    CrmIndustrialPark insertCrmIndustrialPark(CrmIndustrialPark row);

    CrmIndustrialPark updateCrmIndustrialPark(CrmIndustrialPark row);

    int deleteCrmIndustrialParkById(String id);

    IndustrialParkBatchResult batch(IndustrialParkBatchRequest request);
}
