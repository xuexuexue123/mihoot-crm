package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmPatientValue;

public interface CrmPatientValueMapper
{
    CrmPatientValue selectByContactId(String contactId);

    List<CrmPatientValue> selectByContactIds(@Param("contactIds") List<String> contactIds);

    List<CrmPatientValue> selectCrmPatientValueList(CrmPatientValue query);

    int upsertCrmPatientValue(CrmPatientValue row);

    List<CrmPatientValue> selectDueRecallPatients(@Param("limit") int limit);

    List<String> selectAllContactIds();

    List<String> selectContactIdsByAccountId(String accountId);

    List<String> selectContactIdsByAccountIds(@Param("accountIds") List<String> accountIds);
}
