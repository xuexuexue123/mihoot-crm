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
import com.ruoyi.crm.domain.CrmSanctionedEntity;
import com.ruoyi.crm.domain.dto.SanctionedEntityBatchRequest;
import com.ruoyi.crm.domain.dto.SanctionedEntityBatchResult;
import com.ruoyi.crm.mapper.CrmSanctionedEntityMapper;
import com.ruoyi.crm.service.ICrmSanctionedEntityService;

/**
 * 合规制裁主体 Service 实现
 */
@Service
public class CrmSanctionedEntityServiceImpl implements ICrmSanctionedEntityService
{
    private static final String ID_PREFIX = "sanc-";

    @Autowired
    private CrmSanctionedEntityMapper crmSanctionedEntityMapper;

    @Override
    public CrmSanctionedEntity selectCrmSanctionedEntityById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return null;
        }
        return crmSanctionedEntityMapper.selectCrmSanctionedEntityById(id.trim());
    }

    @Override
    public List<CrmSanctionedEntity> selectCrmSanctionedEntityList(CrmSanctionedEntity query)
    {
        if (query == null)
        {
            query = new CrmSanctionedEntity();
        }
        normalizeListFilters(query);
        return crmSanctionedEntityMapper.selectCrmSanctionedEntityList(query);
    }

    @Override
    public CrmSanctionedEntity insertCrmSanctionedEntity(CrmSanctionedEntity row)
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
            CrmSanctionedEntity existing = crmSanctionedEntityMapper.selectCrmSanctionedEntityById(row.getId());
            if (existing != null)
            {
                throw new ServiceException("制裁主体 id 已存在: " + row.getId(), HttpStatus.BAD_REQUEST);
            }
        }
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmSanctionedEntityMapper.insertCrmSanctionedEntity(row);
        return crmSanctionedEntityMapper.selectCrmSanctionedEntityById(row.getId());
    }

    @Override
    public CrmSanctionedEntity updateCrmSanctionedEntity(CrmSanctionedEntity row)
    {
        validateAndNormalize(row, true);
        CrmSanctionedEntity existing = crmSanctionedEntityMapper.selectCrmSanctionedEntityById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("制裁主体不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmSanctionedEntityMapper.updateCrmSanctionedEntity(row);
        if (rows <= 0)
        {
            throw new ServiceException("制裁主体不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return crmSanctionedEntityMapper.selectCrmSanctionedEntityById(row.getId());
    }

    @Override
    public int deleteCrmSanctionedEntityById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return 0;
        }
        return crmSanctionedEntityMapper.logicalDeleteById(id.trim(), SecurityUtils.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SanctionedEntityBatchResult batch(SanctionedEntityBatchRequest request)
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

        for (CrmSanctionedEntity row : request.getRows())
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
            CrmSanctionedEntity existing = hasId
                    ? crmSanctionedEntityMapper.selectCrmSanctionedEntityById(row.getId().trim())
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
                crmSanctionedEntityMapper.updateCrmSanctionedEntity(row);
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
                crmSanctionedEntityMapper.insertCrmSanctionedEntity(row);
                inserted++;
            }
        }
        return new SanctionedEntityBatchResult(inserted, updated, skipped);
    }

    private void normalizeListFilters(CrmSanctionedEntity query)
    {
        if (StringUtils.isNotBlank(query.getSearchQuery()))
        {
            query.setSearchQuery(query.getSearchQuery().trim());
        }
        if ("ALL".equalsIgnoreCase(query.getRiskCategory()))
        {
            query.setRiskCategory(null);
        }
        if ("ALL".equalsIgnoreCase(query.getRiskLevel()))
        {
            query.setRiskLevel(null);
        }
        if ("ALL".equalsIgnoreCase(query.getEntityType()))
        {
            query.setEntityType(null);
        }
    }

    private void validateAndNormalize(CrmSanctionedEntity row, boolean requireId)
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

        String entityType = row.getEntityType() == null ? "" : row.getEntityType().trim();
        if (StringUtils.isBlank(entityType))
        {
            throw new ServiceException("entityType 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setEntityType(entityType);

        String riskCategory = row.getRiskCategory() == null ? "" : row.getRiskCategory().trim();
        if (StringUtils.isBlank(riskCategory))
        {
            throw new ServiceException("riskCategory 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setRiskCategory(riskCategory);

        String riskReason = row.getRiskReason() == null ? "" : row.getRiskReason().trim();
        if (StringUtils.isBlank(riskReason))
        {
            throw new ServiceException("riskReason 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setRiskReason(riskReason);

        String issuingAgency = row.getIssuingAgency() == null ? "" : row.getIssuingAgency().trim();
        if (StringUtils.isBlank(issuingAgency))
        {
            throw new ServiceException("issuingAgency 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setIssuingAgency(issuingAgency);

        String riskLevel = row.getRiskLevel() == null ? "" : row.getRiskLevel().trim();
        if (StringUtils.isBlank(riskLevel))
        {
            throw new ServiceException("riskLevel 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setRiskLevel(riskLevel);

        String effectiveDate = row.getEffectiveDate() == null ? "" : row.getEffectiveDate().trim();
        if (StringUtils.isBlank(effectiveDate))
        {
            throw new ServiceException("effectiveDate 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setEffectiveDate(effectiveDate);

        if (row.getAka() == null)
        {
            row.setAka(new ArrayList<>());
        }

        if (row.getNameEn() != null)
        {
            row.setNameEn(row.getNameEn().trim());
        }
        if (row.getTaxCode() != null)
        {
            row.setTaxCode(row.getTaxCode().trim());
        }
        if (row.getIdCardOrPassport() != null)
        {
            row.setIdCardOrPassport(row.getIdCardOrPassport().trim());
        }
        if (row.getExpiryDate() != null)
        {
            row.setExpiryDate(row.getExpiryDate().trim());
        }
        if (row.getOfficialNoticeUrl() != null)
        {
            row.setOfficialNoticeUrl(row.getOfficialNoticeUrl().trim());
        }
        if (row.getMatchedEnterpriseCode() != null)
        {
            row.setMatchedEnterpriseCode(row.getMatchedEnterpriseCode().trim());
        }
        if (row.getCreatedAt() != null)
        {
            row.setCreatedAt(row.getCreatedAt().trim());
        }
    }
}
