package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmMarketEvent;
import com.ruoyi.crm.domain.dto.MarketEventBatchRequest;
import com.ruoyi.crm.domain.dto.MarketEventBatchResult;
import com.ruoyi.crm.mapper.CrmMarketEventMapper;
import com.ruoyi.crm.service.ICrmMarketEventService;

/**
 * 市场活动 Service 实现
 */
@Service
public class CrmMarketEventServiceImpl implements ICrmMarketEventService
{
    private static final String ID_PREFIX = "evt-";

    @Autowired
    private CrmMarketEventMapper crmMarketEventMapper;

    @Override
    public CrmMarketEvent selectCrmMarketEventById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return null;
        }
        return crmMarketEventMapper.selectCrmMarketEventById(id.trim());
    }

    @Override
    public List<CrmMarketEvent> selectCrmMarketEventList(CrmMarketEvent query)
    {
        if (query == null)
        {
            query = new CrmMarketEvent();
        }
        normalizeListFilters(query);
        return crmMarketEventMapper.selectCrmMarketEventList(query);
    }

    @Override
    public CrmMarketEvent insertCrmMarketEvent(CrmMarketEvent row)
    {
        validateAndNormalize(row, false);
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(row.getId()))
        {
            row.setId(ID_PREFIX + IdUtils.fastSimpleUUID());
        }
        else
        {
            row.setId(row.getId().trim());
            CrmMarketEvent existing = crmMarketEventMapper.selectCrmMarketEventById(row.getId());
            if (existing != null)
            {
                throw new ServiceException("市场活动 id 已存在: " + row.getId(), HttpStatus.BAD_REQUEST);
            }
        }
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmMarketEventMapper.insertCrmMarketEvent(row);
        return crmMarketEventMapper.selectCrmMarketEventById(row.getId());
    }

    @Override
    public CrmMarketEvent updateCrmMarketEvent(CrmMarketEvent row)
    {
        validateAndNormalize(row, true);
        CrmMarketEvent existing = crmMarketEventMapper.selectCrmMarketEventById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("市场活动不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmMarketEventMapper.updateCrmMarketEvent(row);
        if (rows <= 0)
        {
            throw new ServiceException("市场活动不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return crmMarketEventMapper.selectCrmMarketEventById(row.getId());
    }

    @Override
    public int deleteCrmMarketEventById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return 0;
        }
        return crmMarketEventMapper.logicalDeleteById(id.trim(), SecurityUtils.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketEventBatchResult batch(MarketEventBatchRequest request)
    {
        if (request == null || request.getRows() == null || request.getRows().isEmpty())
        {
            throw new ServiceException("rows 不能为空", HttpStatus.BAD_REQUEST);
        }
        String mode = StringUtils.isBlank(request.getMode()) ? "upsert" : request.getMode().trim().toLowerCase();
        if (!"upsert".equals(mode) && !"insert".equals(mode))
        {
            throw new ServiceException("mode 必须为 upsert 或 insert", HttpStatus.BAD_REQUEST);
        }

        int inserted = 0;
        int updated = 0;
        int skipped = 0;

        for (CrmMarketEvent row : request.getRows())
        {
            if (row == null)
            {
                skipped++;
                continue;
            }
            try
            {
                validateAndNormalize(row, false);
            }
            catch (ServiceException ex)
            {
                throw new ServiceException("批量导入校验失败: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
            }

            boolean hasId = StringUtils.isNotBlank(row.getId());
            CrmMarketEvent existing = hasId
                    ? crmMarketEventMapper.selectCrmMarketEventById(row.getId().trim())
                    : null;

            if (existing != null)
            {
                if ("insert".equals(mode))
                {
                    skipped++;
                    continue;
                }
                row.setId(existing.getId());
                row.setUpdateBy(SecurityUtils.getUsername());
                row.setUpdateTime(DateUtils.getNowDate());
                crmMarketEventMapper.updateCrmMarketEvent(row);
                updated++;
            }
            else
            {
                if (StringUtils.isBlank(row.getId()))
                {
                    row.setId(ID_PREFIX + IdUtils.fastSimpleUUID());
                }
                else
                {
                    row.setId(row.getId().trim());
                }
                String username = SecurityUtils.getUsername();
                row.setDelFlag("0");
                row.setCreateBy(username);
                row.setCreateTime(DateUtils.getNowDate());
                row.setUpdateBy(username);
                row.setUpdateTime(DateUtils.getNowDate());
                crmMarketEventMapper.insertCrmMarketEvent(row);
                inserted++;
            }
        }
        return new MarketEventBatchResult(inserted, updated, skipped);
    }

    private void normalizeListFilters(CrmMarketEvent query)
    {
        if (StringUtils.isNotBlank(query.getSearchQuery()))
        {
            query.setSearchQuery(query.getSearchQuery().trim());
        }
        if ("ALL".equalsIgnoreCase(query.getEventType()))
        {
            query.setEventType(null);
        }
        if ("ALL".equalsIgnoreCase(query.getStatus()))
        {
            query.setStatus(null);
        }
        if ("ALL".equalsIgnoreCase(query.getProvince()))
        {
            query.setProvince(null);
        }
        else if (StringUtils.isNotBlank(query.getProvince()))
        {
            query.setProvince(query.getProvince().trim());
        }
    }

    private void validateAndNormalize(CrmMarketEvent row, boolean requireId)
    {
        if (row == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (requireId && StringUtils.isBlank(row.getId()))
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isNotBlank(row.getId()))
        {
            row.setId(row.getId().trim());
        }

        String titleCn = row.getTitleCn() == null ? "" : row.getTitleCn().trim();
        if (StringUtils.isBlank(titleCn))
        {
            throw new ServiceException("titleCn 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setTitleCn(titleCn);

        String titleVn = row.getTitleVn() == null ? "" : row.getTitleVn().trim();
        if (StringUtils.isBlank(titleVn))
        {
            throw new ServiceException("titleVn 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setTitleVn(titleVn);

        if (row.getTitleEn() != null)
        {
            row.setTitleEn(row.getTitleEn().trim());
        }

        String eventType = row.getEventType() == null ? "" : row.getEventType().trim();
        if (StringUtils.isBlank(eventType))
        {
            throw new ServiceException("eventType 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setEventType(eventType);

        String status = row.getStatus() == null ? "" : row.getStatus().trim();
        if (StringUtils.isBlank(status))
        {
            throw new ServiceException("status 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setStatus(status);

        String organizer = row.getOrganizer() == null ? "" : row.getOrganizer().trim();
        if (StringUtils.isBlank(organizer))
        {
            throw new ServiceException("organizer 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setOrganizer(organizer);

        if (row.getIndustry() == null)
        {
            row.setIndustry("");
        }
        else
        {
            row.setIndustry(row.getIndustry().trim());
        }

        String startDate = row.getStartDate() == null ? "" : row.getStartDate().trim();
        if (StringUtils.isBlank(startDate))
        {
            throw new ServiceException("startDate 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setStartDate(startDate);

        String endDate = row.getEndDate() == null ? "" : row.getEndDate().trim();
        if (StringUtils.isBlank(endDate))
        {
            throw new ServiceException("endDate 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setEndDate(endDate);

        String province = row.getProvince() == null ? "" : row.getProvince().trim();
        if (StringUtils.isBlank(province))
        {
            throw new ServiceException("province 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setProvince(province);

        String venue = row.getVenue() == null ? "" : row.getVenue().trim();
        if (StringUtils.isBlank(venue))
        {
            throw new ServiceException("venue 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setVenue(venue);

        if (row.getTargetAudience() == null)
        {
            row.setTargetAudience("");
        }
        else
        {
            row.setTargetAudience(row.getTargetAudience().trim());
        }

        if (row.getScale() != null)
        {
            row.setScale(row.getScale().trim());
        }
        if (row.getEntryFee() != null)
        {
            row.setEntryFee(row.getEntryFee().trim());
        }
        if (row.getRegistrationLink() != null)
        {
            row.setRegistrationLink(row.getRegistrationLink().trim());
        }
        if (row.getContactPerson() != null)
        {
            row.setContactPerson(row.getContactPerson().trim());
        }

        String highlights = row.getHighlights() == null ? "" : row.getHighlights().trim();
        if (StringUtils.isBlank(highlights))
        {
            throw new ServiceException("highlights 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setHighlights(highlights);

        if (row.getNotes() != null)
        {
            row.setNotes(row.getNotes().trim());
        }
        if (row.getRelatedParkId() != null)
        {
            String parkId = row.getRelatedParkId().trim();
            row.setRelatedParkId(StringUtils.isBlank(parkId) ? null : parkId);
        }
        if (row.getCoverUrl() != null)
        {
            String coverUrl = row.getCoverUrl().trim();
            row.setCoverUrl(StringUtils.isBlank(coverUrl) ? null : coverUrl);
        }
    }
}
