package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventSpendItem;
import com.ruoyi.crm.domain.dto.EventSpendItemSaveRequest;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventSpendItemMapper;
import com.ruoyi.crm.service.IEventSpendItemService;
import com.ruoyi.crm.service.support.DataTableTenantSupport;

@Service
public class EventSpendItemServiceImpl implements IEventSpendItemService
{
    @Autowired
    private CrmEventSpendItemMapper spendItemMapper;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Override
    public List<CrmEventSpendItem> listByEvent(String tenantId, String eventId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        assertEventExists(eventId);
        return spendItemMapper.selectByEventId(tenantId, eventId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceSpendItems(String tenantId, String eventId, EventSpendItemSaveRequest request, String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
        }
        spendItemMapper.deleteByEventId(tenantId, eventId);
        List<CrmEventSpendItem> rows = new ArrayList<>();
        if (request != null && request.getItems() != null)
        {
            for (EventSpendItemSaveRequest.Item item : request.getItems())
            {
                if (item == null || StringUtils.isEmpty(item.getCategory()))
                {
                    continue;
                }
                CrmEventSpendItem row = new CrmEventSpendItem();
                row.setTenantId(tenantId);
                row.setEventId(eventId);
                row.setChannelKey(item.getChannelKey());
                row.setCategory(item.getCategory());
                row.setAmount(item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO);
                row.setSpentAt(item.getSpentAt());
                row.setRemark(item.getRemark());
                row.setCreateBy(operator);
                rows.add(row);
            }
        }
        if (!rows.isEmpty())
        {
            spendItemMapper.insertBatch(rows);
        }
        BigDecimal total = spendItemMapper.selectTotalAmount(tenantId, eventId);
        CrmEvent update = new CrmEvent();
        update.setEventId(eventId);
        update.setPaid(total != null ? total : BigDecimal.ZERO);
        crmEventMapper.updateCrmEvent(update);
    }

    private void assertEventExists(String eventId)
    {
        if (crmEventMapper.selectCrmEventByEventId(eventId) == null)
        {
            throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
        }
    }
}
