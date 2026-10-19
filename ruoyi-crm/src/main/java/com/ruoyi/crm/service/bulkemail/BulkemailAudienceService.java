package com.ruoyi.crm.service.bulkemail;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.BulkemailAudienceFilter;
import com.ruoyi.crm.domain.dto.BulkemailAudiencePreviewRequest;
import com.ruoyi.crm.domain.dto.BulkemailAudiencePreviewResult;
import com.ruoyi.crm.domain.dto.BulkemailAudiencePreviewRow;
import com.ruoyi.crm.domain.dto.EventAudienceQuery;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.mapper.BulkemailAudienceMapper;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.IEventAudienceService;

/**
 * 邮件营销受众筛选 preview
 */
@Service
public class BulkemailAudienceService
{
    private static final int MAX_PAGE_SIZE = 200;
    private static final int EVENT_PAGE_SIZE = 500;

    @Autowired
    private BulkemailAudienceMapper bulkemailAudienceMapper;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private IEventAudienceService eventAudienceService;

    public BulkemailAudiencePreviewResult preview(String tenantId, BulkemailAudiencePreviewRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求不能为空", HttpStatus.BAD_REQUEST);
        }
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 50;
        if (pageSize > MAX_PAGE_SIZE)
        {
            throw new ServiceException("pageSize 不能超过 " + MAX_PAGE_SIZE, HttpStatus.BAD_REQUEST);
        }
        int pageNum = request.getPageNum() != null && request.getPageNum() > 0 ? request.getPageNum() : 1;

        BulkemailAudienceFilter filter = request.getFilter() != null ? request.getFilter() : new BulkemailAudienceFilter();
        // P4 stub: excludeUnsubscribed 忽略

        boolean eventFilterActive = false;
        List<String> eventContactIds = new ArrayList<>();
        if (StringUtils.isNotBlank(filter.getEventId()))
        {
            if (crmEventMapper.selectCrmEventByEventId(filter.getEventId().trim()) == null)
            {
                throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
            }
            eventFilterActive = true;
            eventContactIds = resolveEventContactIds(tenantId, filter.getEventId().trim());
        }

        BulkemailAudienceQuery query = BulkemailAudienceQuery.fromFilter(filter, eventContactIds, eventFilterActive);
        long total = bulkemailAudienceMapper.countFiltered(query);
        long validEmailCount = bulkemailAudienceMapper.countValidEmailFiltered(query);

        int offset = (pageNum - 1) * pageSize;
        List<BulkemailAudiencePreviewRow> rows = bulkemailAudienceMapper.selectFiltered(query, offset, pageSize);
        if (rows != null)
        {
            rows.forEach(this::enrichTags);
        }

        BulkemailAudiencePreviewResult result = new BulkemailAudiencePreviewResult();
        result.setTotal(total);
        result.setValidEmailCount(validEmailCount);
        result.setInvalidCount(Math.max(0L, total - validEmailCount));
        result.setRows(rows != null ? rows : new ArrayList<>());
        return result;
    }

    private List<String> resolveEventContactIds(String tenantId, String eventId)
    {
        Set<String> ids = new LinkedHashSet<>();
        EventAudienceQuery query = new EventAudienceQuery();
        query.setPageSize(EVENT_PAGE_SIZE);
        int pageNum = 1;
        while (true)
        {
            query.setPageNum(pageNum);
            List<EventAudienceRowVo> batch = eventAudienceService.listAudience(tenantId, eventId, query);
            if (batch == null || batch.isEmpty())
            {
                break;
            }
            for (EventAudienceRowVo row : batch)
            {
                if (StringUtils.isNotBlank(row.getContactId()))
                {
                    ids.add(row.getContactId().trim());
                }
            }
            if (batch.size() < EVENT_PAGE_SIZE)
            {
                break;
            }
            pageNum++;
        }
        return new ArrayList<>(ids);
    }

    private void enrichTags(BulkemailAudiencePreviewRow row)
    {
        if (row == null || StringUtils.isBlank(row.getTagJson()))
        {
            return;
        }
        try
        {
            List<String> tags = JSON.parseArray(row.getTagJson(), String.class);
            if (tags != null)
            {
                row.setTags(tags);
            }
        }
        catch (Exception ignored)
        {
            // 非 JSON 数组时保留空 tags
        }
        row.setTagJson(null);
    }
}
