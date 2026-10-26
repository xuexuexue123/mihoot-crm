package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.dto.BulkemailAudienceFilter;
import com.ruoyi.crm.domain.dto.BulkemailAudiencePreviewRequest;
import com.ruoyi.crm.domain.dto.BulkemailAudiencePreviewResult;
import com.ruoyi.crm.domain.dto.BulkemailAudiencePreviewRow;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.mapper.BulkemailAudienceMapper;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.IEventAudienceService;

@ExtendWith(MockitoExtension.class)
class BulkemailAudienceServiceTest
{
    @Mock
    private BulkemailAudienceMapper bulkemailAudienceMapper;

    @Mock
    private CrmEventMapper crmEventMapper;

    @Mock
    private IEventAudienceService eventAudienceService;

    @InjectMocks
    private BulkemailAudienceService service;

    @Test
    void preview_rejectsOversizedPage()
    {
        BulkemailAudiencePreviewRequest request = new BulkemailAudiencePreviewRequest();
        request.setPageSize(201);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.preview("tenant-a", request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void preview_eventNotFound_returns404()
    {
        BulkemailAudienceFilter filter = new BulkemailAudienceFilter();
        filter.setEventId("missing-event");
        BulkemailAudiencePreviewRequest request = new BulkemailAudiencePreviewRequest();
        request.setFilter(filter);

        when(crmEventMapper.selectCrmEventByEventId("missing-event")).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.preview("tenant-a", request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
    }

    @Test
    void preview_combinesFilterAndEventAudience()
    {
        BulkemailAudienceFilter filter = new BulkemailAudienceFilter();
        filter.setHasWorkEmail(true);
        filter.setKeyword("张");
        filter.setEventId("event-1");
        BulkemailAudiencePreviewRequest request = new BulkemailAudiencePreviewRequest();
        request.setFilter(filter);
        request.setPageNum(1);
        request.setPageSize(50);

        when(crmEventMapper.selectCrmEventByEventId("event-1")).thenReturn(new CrmEvent());

        EventAudienceRowVo row = new EventAudienceRowVo();
        row.setContactId("c-1");
        when(eventAudienceService.listAudience(eq("tenant-a"), eq("event-1"), any()))
                .thenReturn(Collections.singletonList(row));

        when(bulkemailAudienceMapper.countFiltered(any())).thenReturn(10L);
        when(bulkemailAudienceMapper.countValidEmailFiltered(any())).thenReturn(8L);

        BulkemailAudiencePreviewRow previewRow = new BulkemailAudiencePreviewRow();
        previewRow.setContactId("c-1");
        previewRow.setTagJson("[\"高价值\"]");
        when(bulkemailAudienceMapper.selectFiltered(any(), eq(0), eq(50)))
                .thenReturn(Collections.singletonList(previewRow));

        BulkemailAudiencePreviewResult result = service.preview("tenant-a", request);

        assertEquals(10L, result.getTotal());
        assertEquals(8L, result.getValidEmailCount());
        assertEquals(2L, result.getInvalidCount());
        assertEquals(1, result.getRows().size());
        assertEquals(Collections.singletonList("高价值"), result.getRows().get(0).getTags());

        ArgumentCaptor<BulkemailAudienceQuery> queryCaptor = ArgumentCaptor.forClass(BulkemailAudienceQuery.class);
        verify(bulkemailAudienceMapper).countFiltered(queryCaptor.capture());
        BulkemailAudienceQuery captured = queryCaptor.getValue();
        assertEquals(Boolean.TRUE, captured.getHasWorkEmail());
        assertEquals("张", captured.getKeyword());
        assertEquals(Collections.singletonList("c-1"), captured.getEventContactIds());
    }

    @Test
    void preview_emptyEventAudience_returnsZeroRows()
    {
        BulkemailAudienceFilter filter = new BulkemailAudienceFilter();
        filter.setEventId("event-1");
        BulkemailAudiencePreviewRequest request = new BulkemailAudiencePreviewRequest();
        request.setFilter(filter);

        when(crmEventMapper.selectCrmEventByEventId("event-1")).thenReturn(new CrmEvent());
        when(eventAudienceService.listAudience(eq("tenant-a"), eq("event-1"), any()))
                .thenReturn(Collections.emptyList());
        when(bulkemailAudienceMapper.countFiltered(any())).thenReturn(0L);
        when(bulkemailAudienceMapper.countValidEmailFiltered(any())).thenReturn(0L);
        when(bulkemailAudienceMapper.selectFiltered(any(), eq(0), eq(50)))
                .thenReturn(Collections.emptyList());

        BulkemailAudiencePreviewResult result = service.preview("tenant-a", request);

        assertEquals(0L, result.getTotal());
        assertEquals(0, result.getRows().size());
    }
}
