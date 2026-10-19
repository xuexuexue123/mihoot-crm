package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
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
import com.ruoyi.crm.domain.CrmGovernmentAgency;
import com.ruoyi.crm.domain.dto.GovernmentAgencyBatchRequest;
import com.ruoyi.crm.domain.dto.GovernmentAgencyBatchResult;
import com.ruoyi.crm.mapper.CrmGovernmentAgencyMapper;
import com.ruoyi.crm.service.ICrmGovernmentAgencyService;

/**
 * 政府招商机构 Service 实现
 */
@Service
public class CrmGovernmentAgencyServiceImpl implements ICrmGovernmentAgencyService
{
    private static final String ID_PREFIX = "gov-";

    @Autowired
    private CrmGovernmentAgencyMapper crmGovernmentAgencyMapper;

    @Override
    public CrmGovernmentAgency selectCrmGovernmentAgencyById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return null;
        }
        return crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById(id.trim());
    }

    @Override
    public List<CrmGovernmentAgency> selectCrmGovernmentAgencyList(CrmGovernmentAgency query)
    {
        if (query == null)
        {
            query = new CrmGovernmentAgency();
        }
        normalizeListFilters(query);
        return crmGovernmentAgencyMapper.selectCrmGovernmentAgencyList(query);
    }

    @Override
    public CrmGovernmentAgency insertCrmGovernmentAgency(CrmGovernmentAgency row)
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
            CrmGovernmentAgency existing = crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById(row.getId());
            if (existing != null)
            {
                throw new ServiceException("政府机构 id 已存在: " + row.getId(), HttpStatus.BAD_REQUEST);
            }
        }
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmGovernmentAgencyMapper.insertCrmGovernmentAgency(row);
        return crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById(row.getId());
    }

    @Override
    public CrmGovernmentAgency updateCrmGovernmentAgency(CrmGovernmentAgency row)
    {
        validateAndNormalize(row, true);
        CrmGovernmentAgency existing = crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("政府机构不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmGovernmentAgencyMapper.updateCrmGovernmentAgency(row);
        if (rows <= 0)
        {
            throw new ServiceException("政府机构不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById(row.getId());
    }

    @Override
    public int deleteCrmGovernmentAgencyById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return 0;
        }
        return crmGovernmentAgencyMapper.logicalDeleteById(id.trim(), SecurityUtils.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GovernmentAgencyBatchResult batch(GovernmentAgencyBatchRequest request)
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

        for (CrmGovernmentAgency row : request.getRows())
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
            CrmGovernmentAgency existing = hasId
                    ? crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById(row.getId().trim())
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
                crmGovernmentAgencyMapper.updateCrmGovernmentAgency(row);
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
                crmGovernmentAgencyMapper.insertCrmGovernmentAgency(row);
                inserted++;
            }
        }
        return new GovernmentAgencyBatchResult(inserted, updated, skipped);
    }

    private void normalizeListFilters(CrmGovernmentAgency query)
    {
        if (StringUtils.isNotBlank(query.getSearchQuery()))
        {
            query.setSearchQuery(query.getSearchQuery().trim());
        }
        if ("ALL".equalsIgnoreCase(query.getLevel()))
        {
            query.setLevel(null);
        }
        if ("ALL".equalsIgnoreCase(query.getCategory()))
        {
            query.setCategory(null);
        }
        if ("ALL".equalsIgnoreCase(query.getProvince()))
        {
            query.setProvince(null);
        }
    }

    private void validateAndNormalize(CrmGovernmentAgency row, boolean requireId)
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

        String nameCn = row.getNameCn() == null ? "" : row.getNameCn().trim();
        if (StringUtils.isBlank(nameCn))
        {
            throw new ServiceException("nameCn 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setNameCn(nameCn);

        String nameVn = row.getNameVn() == null ? "" : row.getNameVn().trim();
        if (StringUtils.isBlank(nameVn))
        {
            throw new ServiceException("nameVn 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setNameVn(nameVn);

        // abbreviation / coreResponsibilities 允许空串（DDL DEFAULT ''），但字段须非 null
        row.setAbbreviation(row.getAbbreviation() == null ? "" : row.getAbbreviation().trim());
        row.setCoreResponsibilities(row.getCoreResponsibilities() == null ? "" : row.getCoreResponsibilities().trim());

        String level = row.getLevel() == null ? "" : row.getLevel().trim();
        if (StringUtils.isBlank(level))
        {
            throw new ServiceException("level 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setLevel(level);

        String category = row.getCategory() == null ? "" : row.getCategory().trim();
        if (StringUtils.isBlank(category))
        {
            throw new ServiceException("category 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setCategory(category);

        if (row.getApprovalItems() == null)
        {
            row.setApprovalItems(new ArrayList<>());
        }

        if (row.getNameEn() != null)
        {
            row.setNameEn(row.getNameEn().trim());
        }
        if (row.getProvince() != null)
        {
            row.setProvince(row.getProvince().trim());
        }
        if (row.getContactPhone() != null)
        {
            row.setContactPhone(row.getContactPhone().trim());
        }
        if (row.getEmail() != null)
        {
            row.setEmail(row.getEmail().trim());
        }
        if (row.getWebsite() != null)
        {
            row.setWebsite(row.getWebsite().trim());
        }
        if (row.getWorkingHours() != null)
        {
            row.setWorkingHours(row.getWorkingHours().trim());
        }
        if (row.getCreatedAt() != null)
        {
            row.setCreatedAt(row.getCreatedAt().trim());
        }
    }
}
