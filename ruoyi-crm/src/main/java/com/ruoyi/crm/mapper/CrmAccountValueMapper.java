package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmAccountValue;

public interface CrmAccountValueMapper
{
    CrmAccountValue selectByAccountId(String accountId);

    List<CrmAccountValue> selectCrmAccountValueList(CrmAccountValue query);

    int upsertCrmAccountValue(CrmAccountValue row);

    List<String> selectAllAccountIds();

    List<String> selectAccountIdsByContactIds(@Param("contactIds") List<String> contactIds);
}
