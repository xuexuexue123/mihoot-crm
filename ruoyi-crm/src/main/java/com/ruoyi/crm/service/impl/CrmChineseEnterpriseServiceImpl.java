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
import com.ruoyi.crm.domain.CrmChineseEnterprise;
import com.ruoyi.crm.domain.CrmIndustrialPark;
import com.ruoyi.crm.domain.dto.ChineseEnterpriseBatchRequest;
import com.ruoyi.crm.domain.dto.ChineseEnterpriseBatchResult;
import com.ruoyi.crm.mapper.CrmChineseEnterpriseMapper;
import com.ruoyi.crm.mapper.CrmIndustrialParkMapper;
import com.ruoyi.crm.service.ICrmChineseEnterpriseService;

/**
 * 中资企业 Service 实现
 */
@Service
public class CrmChineseEnterpriseServiceImpl implements ICrmChineseEnterpriseService
{
    private static final String ID_PREFIX = "cn-ent-";
    private static final String DEFAULT_STATUS = "运营中";

    @Autowired
    private CrmChineseEnterpriseMapper crmChineseEnterpriseMapper;

    @Autowired
    private CrmIndustrialParkMapper crmIndustrialParkMapper;

    @Override
    public CrmChineseEnterprise selectCrmChineseEnterpriseById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return null;
        }
        return crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById(id.trim());
    }

    @Override
    public List<CrmChineseEnterprise> selectCrmChineseEnterpriseList(CrmChineseEnterprise query)
    {
        if (query == null)
        {
            query = new CrmChineseEnterprise();
        }
        normalizeListFilters(query);
        return crmChineseEnterpriseMapper.selectCrmChineseEnterpriseList(query);
    }

    @Override
    public CrmChineseEnterprise insertCrmChineseEnterprise(CrmChineseEnterprise row)
    {
        validateAndNormalize(row, false);
        backfillParkNameCn(row);
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(row.getId()))
        {
            row.setId(ID_PREFIX + IdUtils.fastSimpleUUID());
        }
        else
        {
            row.setId(row.getId().trim());
            CrmChineseEnterprise existing = crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById(row.getId());
            if (existing != null)
            {
                throw new ServiceException("中资企业 id 已存在: " + row.getId(), HttpStatus.BAD_REQUEST);
            }
        }
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmChineseEnterpriseMapper.insertCrmChineseEnterprise(row);
        return crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById(row.getId());
    }

    @Override
    public CrmChineseEnterprise updateCrmChineseEnterprise(CrmChineseEnterprise row)
    {
        validateAndNormalize(row, true);
        backfillParkNameCn(row);
        CrmChineseEnterprise existing = crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("中资企业不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmChineseEnterpriseMapper.updateCrmChineseEnterprise(row);
        if (rows <= 0)
        {
            throw new ServiceException("中资企业不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById(row.getId());
    }

    @Override
    public int deleteCrmChineseEnterpriseById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return 0;
        }
        return crmChineseEnterpriseMapper.logicalDeleteById(id.trim(), SecurityUtils.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChineseEnterpriseBatchResult batch(ChineseEnterpriseBatchRequest request)
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

        for (CrmChineseEnterprise row : request.getRows())
        {
            if (row == null)
            {
                skipped++;
                continue;
            }
            try
            {
                validateAndNormalize(row, false);
                backfillParkNameCn(row);
            }
            catch (ServiceException ex)
            {
                throw new ServiceException("批量导入校验失败: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
            }

            boolean hasId = StringUtils.isNotBlank(row.getId());
            CrmChineseEnterprise existing = hasId
                    ? crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById(row.getId().trim())
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
                crmChineseEnterpriseMapper.updateCrmChineseEnterprise(row);
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
                crmChineseEnterpriseMapper.insertCrmChineseEnterprise(row);
                inserted++;
            }
        }
        return new ChineseEnterpriseBatchResult(inserted, updated, skipped);
    }

    private void normalizeListFilters(CrmChineseEnterprise query)
    {
        if (StringUtils.isNotBlank(query.getSearchQuery()))
        {
            query.setSearchQuery(query.getSearchQuery().trim());
        }
        if ("ALL".equalsIgnoreCase(query.getParkId()))
        {
            query.setParkId(null);
        }
        if ("ALL".equalsIgnoreCase(query.getProvinceVietnam()))
        {
            query.setProvinceVietnam(null);
        }
        if ("ALL".equalsIgnoreCase(query.getParentProvinceChina()))
        {
            query.setParentProvinceChina(null);
        }
        if ("ALL".equalsIgnoreCase(query.getIndustry()))
        {
            query.setIndustry(null);
        }
        if ("ALL".equalsIgnoreCase(query.getStockListingStatus()))
        {
            query.setStockListingStatus(null);
        }
        if ("ALL".equalsIgnoreCase(query.getInvestmentMode()))
        {
            query.setInvestmentMode(null);
        }
        if ("ALL".equalsIgnoreCase(query.getStatus()))
        {
            query.setStatus(null);
        }
    }

    private void validateAndNormalize(CrmChineseEnterprise row, boolean requireId)
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

        String parentCompanyChina = row.getParentCompanyChina() == null ? "" : row.getParentCompanyChina().trim();
        if (StringUtils.isBlank(parentCompanyChina))
        {
            throw new ServiceException("parentCompanyChina 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setParentCompanyChina(parentCompanyChina);

        String parentProvinceChina = row.getParentProvinceChina() == null ? "" : row.getParentProvinceChina().trim();
        if (StringUtils.isBlank(parentProvinceChina))
        {
            throw new ServiceException("parentProvinceChina 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setParentProvinceChina(parentProvinceChina);

        String vietnamEntityName = row.getVietnamEntityName() == null ? "" : row.getVietnamEntityName().trim();
        if (StringUtils.isBlank(vietnamEntityName))
        {
            throw new ServiceException("vietnamEntityName 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setVietnamEntityName(vietnamEntityName);

        String vietnamEntityNameCn = row.getVietnamEntityNameCn() == null ? "" : row.getVietnamEntityNameCn().trim();
        if (StringUtils.isBlank(vietnamEntityNameCn))
        {
            throw new ServiceException("vietnamEntityNameCn 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setVietnamEntityNameCn(vietnamEntityNameCn);

        String parkId = row.getParkId() == null ? "" : row.getParkId().trim();
        if (StringUtils.isBlank(parkId))
        {
            throw new ServiceException("parkId 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setParkId(parkId);

        String provinceVietnam = row.getProvinceVietnam() == null ? "" : row.getProvinceVietnam().trim();
        if (StringUtils.isBlank(provinceVietnam))
        {
            throw new ServiceException("provinceVietnam 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setProvinceVietnam(provinceVietnam);

        String industry = row.getIndustry() == null ? "" : row.getIndustry().trim();
        if (StringUtils.isBlank(industry))
        {
            throw new ServiceException("industry 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setIndustry(industry);

        if (StringUtils.isBlank(row.getStatus()))
        {
            row.setStatus(DEFAULT_STATUS);
        }
        else
        {
            row.setStatus(row.getStatus().trim());
        }

        if (row.getParkNameCn() == null)
        {
            row.setParkNameCn("");
        }
        else
        {
            row.setParkNameCn(row.getParkNameCn().trim());
        }
        if (row.getStockCode() != null)
        {
            row.setStockCode(row.getStockCode().trim());
        }
        if (row.getStockListingStatus() != null)
        {
            row.setStockListingStatus(row.getStockListingStatus().trim());
        }
        if (row.getTaxCode() != null)
        {
            row.setTaxCode(row.getTaxCode().trim());
        }
        if (row.getInvestmentMode() != null)
        {
            row.setInvestmentMode(row.getInvestmentMode().trim());
        }
        if (row.getCreatedAt() != null)
        {
            row.setCreatedAt(row.getCreatedAt().trim());
        }
    }

    /**
     * 若 parkId 对应未删除园区，用园区 name_cn 覆盖 parkNameCn；查不到则保留前端传入值。
     */
    private void backfillParkNameCn(CrmChineseEnterprise row)
    {
        if (row == null || StringUtils.isBlank(row.getParkId()))
        {
            return;
        }
        CrmIndustrialPark park = crmIndustrialParkMapper.selectCrmIndustrialParkById(row.getParkId());
        if (park != null && StringUtils.isNotBlank(park.getNameCn()))
        {
            row.setParkNameCn(park.getNameCn().trim());
        }
    }
}
