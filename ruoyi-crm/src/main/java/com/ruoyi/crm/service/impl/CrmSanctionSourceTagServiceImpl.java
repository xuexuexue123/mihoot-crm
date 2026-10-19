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
import com.ruoyi.crm.domain.CrmSanctionSourceTag;
import com.ruoyi.crm.domain.dto.SanctionSourceTagBatchRequest;
import com.ruoyi.crm.domain.dto.SanctionSourceTagBatchResult;
import com.ruoyi.crm.mapper.CrmSanctionSourceTagMapper;
import com.ruoyi.crm.service.ICrmSanctionSourceTagService;

/**
 * 制裁源标签 Service 实现
 */
@Service
public class CrmSanctionSourceTagServiceImpl implements ICrmSanctionSourceTagService
{
    private static final String ID_PREFIX = "tag-";

    @Autowired
    private CrmSanctionSourceTagMapper crmSanctionSourceTagMapper;

    @Override
    public CrmSanctionSourceTag selectCrmSanctionSourceTagById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return null;
        }
        return crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById(id.trim());
    }

    @Override
    public List<CrmSanctionSourceTag> selectCrmSanctionSourceTagList(CrmSanctionSourceTag query)
    {
        if (query == null)
        {
            query = new CrmSanctionSourceTag();
        }
        if (StringUtils.isNotBlank(query.getSearchQuery()))
        {
            query.setSearchQuery(query.getSearchQuery().trim());
        }
        return crmSanctionSourceTagMapper.selectCrmSanctionSourceTagList(query);
    }

    @Override
    public CrmSanctionSourceTag insertCrmSanctionSourceTag(CrmSanctionSourceTag row)
    {
        validateAndNormalize(row, false);
        ensureTagCodeUnique(row.getTagCode(), null);
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(row.getId()))
        {
            row.setId(ID_PREFIX + IdUtils.fastSimpleUUID());
        }
        else
        {
            row.setId(row.getId().trim());
            CrmSanctionSourceTag existing = crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById(row.getId());
            if (existing != null)
            {
                throw new ServiceException("制裁源标签 id 已存在: " + row.getId(), HttpStatus.BAD_REQUEST);
            }
        }
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmSanctionSourceTagMapper.insertCrmSanctionSourceTag(row);
        return crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById(row.getId());
    }

    @Override
    public CrmSanctionSourceTag updateCrmSanctionSourceTag(CrmSanctionSourceTag row)
    {
        validateAndNormalize(row, true);
        CrmSanctionSourceTag existing = crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("制裁源标签不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        ensureTagCodeUnique(row.getTagCode(), row.getId());
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmSanctionSourceTagMapper.updateCrmSanctionSourceTag(row);
        if (rows <= 0)
        {
            throw new ServiceException("制裁源标签不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById(row.getId());
    }

    @Override
    public int deleteCrmSanctionSourceTagById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return 0;
        }
        return crmSanctionSourceTagMapper.logicalDeleteById(id.trim(), SecurityUtils.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SanctionSourceTagBatchResult batch(SanctionSourceTagBatchRequest request)
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

        for (CrmSanctionSourceTag row : request.getRows())
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
            CrmSanctionSourceTag existing = hasId
                    ? crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById(row.getId().trim())
                    : null;

            if (existing != null)
            {
                if ("insert".equals(mode))
                {
                    skipped++;
                    continue;
                }
                ensureTagCodeUnique(row.getTagCode(), existing.getId());
                row.setId(existing.getId());
                row.setUpdateBy(SecurityUtils.getUsername());
                row.setUpdateTime(DateUtils.getNowDate());
                crmSanctionSourceTagMapper.updateCrmSanctionSourceTag(row);
                updated++;
            }
            else
            {
                ensureTagCodeUnique(row.getTagCode(), null);
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
                crmSanctionSourceTagMapper.insertCrmSanctionSourceTag(row);
                inserted++;
            }
        }
        return new SanctionSourceTagBatchResult(inserted, updated, skipped);
    }

    private void ensureTagCodeUnique(String tagCode, String excludeId)
    {
        CrmSanctionSourceTag byCode = crmSanctionSourceTagMapper.selectCrmSanctionSourceTagByTagCode(tagCode);
        if (byCode == null)
        {
            return;
        }
        if (excludeId != null && excludeId.equals(byCode.getId()))
        {
            return;
        }
        throw new ServiceException("tagCode 已存在: " + tagCode, HttpStatus.BAD_REQUEST);
    }

    private void validateAndNormalize(CrmSanctionSourceTag row, boolean requireId)
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

        String tagCode = row.getTagCode() == null ? "" : row.getTagCode().trim();
        if (StringUtils.isBlank(tagCode))
        {
            throw new ServiceException("tagCode 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setTagCode(tagCode);

        String tagName = row.getTagName() == null ? "" : row.getTagName().trim();
        if (StringUtils.isBlank(tagName))
        {
            throw new ServiceException("tagName 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setTagName(tagName);

        String authority = row.getAuthority() == null ? "" : row.getAuthority().trim();
        if (StringUtils.isBlank(authority))
        {
            throw new ServiceException("authority 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setAuthority(authority);

        String description = row.getDescription() == null ? "" : row.getDescription().trim();
        if (StringUtils.isBlank(description))
        {
            throw new ServiceException("description 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setDescription(description);
    }
}
