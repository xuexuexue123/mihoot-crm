package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmEventSpendItem;
import com.ruoyi.crm.domain.dto.EventSpendItemSaveRequest;

public interface IEventSpendItemService
{
    List<CrmEventSpendItem> listByEvent(String tenantId, String eventId);

    void replaceSpendItems(String tenantId, String eventId, EventSpendItemSaveRequest request, String operator);
}
