package com.ruoyi.crm.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.ruoyi.crm.domain.CrmAccountValue;
import com.ruoyi.crm.domain.CrmPatientValue;
import com.ruoyi.crm.domain.dto.PatientClvRecomputeRequest;
import com.ruoyi.crm.domain.vo.CampaignRoiVo;
import com.ruoyi.crm.domain.vo.ContactClvAggregateVo;
import com.ruoyi.crm.domain.vo.ContactClvVo;
import com.ruoyi.crm.domain.vo.FamilyClvVo;
import com.ruoyi.crm.domain.vo.RecomputeResultVo;

public interface IPatientClvService
{
    FamilyClvVo getFamilyClv(String accountId);

    ContactClvVo getContactClv(String contactId);

    List<CrmPatientValue> listPatientValues(CrmPatientValue query);

    List<CrmAccountValue> listAccountValues(CrmAccountValue query);

    List<CampaignRoiVo> listCampaignRoi(String campaignType, String campaignId, Date dateFrom, Date dateTo);

    RecomputeResultVo recompute(PatientClvRecomputeRequest request);

    void enqueueRecomputeForContact(String contactId, String accountId);

    ContactClvAggregateVo aggregateContactClv(Set<String> contactIds);

    ContactClvAggregateVo aggregateContactClvWindow(String eventId, Set<String> contactIds,
            Map<String, Date> firstTouchByContact, int windowDays);
}
