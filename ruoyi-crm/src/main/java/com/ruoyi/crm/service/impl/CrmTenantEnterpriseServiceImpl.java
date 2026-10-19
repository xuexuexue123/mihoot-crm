package com.ruoyi.crm.service.impl;

import java.util.Arrays;
import java.util.Collections;
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
import com.ruoyi.crm.domain.CrmIndustrialPark;
import com.ruoyi.crm.domain.CrmTenantEnterprise;
import com.ruoyi.crm.domain.dto.TenantEnterpriseBatchRequest;
import com.ruoyi.crm.domain.dto.TenantEnterpriseBatchResult;
import com.ruoyi.crm.mapper.CrmIndustrialParkMapper;
import com.ruoyi.crm.mapper.CrmTenantEnterpriseMapper;
import com.ruoyi.crm.service.ICrmTenantEnterpriseService;

/**
 * 入驻企业 Service 实现
 */
@Service
public class CrmTenantEnterpriseServiceImpl implements ICrmTenantEnterpriseService
{
    private static final String ID_PREFIX = "ent-";

    /** 规范企业类型：外国直接投资（合并原 WFOE） */
    static final String ENTERPRISE_TYPE_FDI = "外国直接投资 (FDI)";
    /** 规范企业类型：合资企业（由「中外合资企业」更名） */
    static final String ENTERPRISE_TYPE_JV = "合资企业 (JV)";
    private static final String LEGACY_ENTERPRISE_TYPE_WFOE = "外商独资企业 (WFOE)";
    private static final String LEGACY_ENTERPRISE_TYPE_JV = "中外合资企业 (JV)";

    @Autowired
    private CrmTenantEnterpriseMapper crmTenantEnterpriseMapper;

    @Autowired
    private CrmIndustrialParkMapper crmIndustrialParkMapper;

    @Override
    public CrmTenantEnterprise selectCrmTenantEnterpriseById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return null;
        }
        return crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(id.trim());
    }

    @Override
    public List<CrmTenantEnterprise> selectCrmTenantEnterpriseList(CrmTenantEnterprise query)
    {
        if (query == null)
        {
            query = new CrmTenantEnterprise();
        }
        normalizeListFilters(query);
        return crmTenantEnterpriseMapper.selectCrmTenantEnterpriseList(query);
    }

    @Override
    public CrmTenantEnterprise insertCrmTenantEnterprise(CrmTenantEnterprise row)
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
            CrmTenantEnterprise existing = crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(row.getId());
            if (existing != null)
            {
                throw new ServiceException("入驻企业 id 已存在: " + row.getId(), HttpStatus.BAD_REQUEST);
            }
        }
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmTenantEnterpriseMapper.insertCrmTenantEnterprise(row);
        return crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(row.getId());
    }

    @Override
    public CrmTenantEnterprise updateCrmTenantEnterprise(CrmTenantEnterprise row)
    {
        validateAndNormalize(row, true);
        backfillParkNameCn(row);
        CrmTenantEnterprise existing = crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("入驻企业不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmTenantEnterpriseMapper.updateCrmTenantEnterprise(row);
        if (rows <= 0)
        {
            throw new ServiceException("入驻企业不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(row.getId());
    }

    @Override
    public int deleteCrmTenantEnterpriseById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return 0;
        }
        return crmTenantEnterpriseMapper.logicalDeleteById(id.trim(), SecurityUtils.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenantEnterpriseBatchResult batch(TenantEnterpriseBatchRequest request)
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

        for (CrmTenantEnterprise row : request.getRows())
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
            CrmTenantEnterprise existing = hasId
                    ? crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(row.getId().trim())
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
                crmTenantEnterpriseMapper.updateCrmTenantEnterprise(row);
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
                crmTenantEnterpriseMapper.insertCrmTenantEnterprise(row);
                inserted++;
            }
        }
        return new TenantEnterpriseBatchResult(inserted, updated, skipped);
    }

    private void normalizeListFilters(CrmTenantEnterprise query)
    {
        if (StringUtils.isNotBlank(query.getSearchQuery()))
        {
            query.setSearchQuery(query.getSearchQuery().trim());
        }
        if ("ALL".equalsIgnoreCase(query.getParkId()))
        {
            query.setParkId(null);
        }
        if ("ALL".equalsIgnoreCase(query.getIndustry()))
        {
            query.setIndustry(null);
        }
        if ("ALL".equalsIgnoreCase(query.getEnterpriseType()))
        {
            query.setEnterpriseType(null);
            query.setEnterpriseTypeIn(null);
        }
        else if (StringUtils.isNotBlank(query.getEnterpriseType()))
        {
            query.setEnterpriseTypeIn(expandEnterpriseTypeFilter(query.getEnterpriseType().trim()));
        }
        if ("ALL".equalsIgnoreCase(query.getCountryOfOrigin()))
        {
            query.setCountryOfOrigin(null);
        }
        if ("ALL".equalsIgnoreCase(query.getStatus()))
        {
            query.setStatus(null);
        }
    }

    private void validateAndNormalize(CrmTenantEnterprise row, boolean requireId)
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

        String parkId = row.getParkId() == null ? "" : row.getParkId().trim();
        if (StringUtils.isBlank(parkId))
        {
            throw new ServiceException("parkId 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setParkId(parkId);

        String name = row.getName() == null ? "" : row.getName().trim();
        if (StringUtils.isBlank(name))
        {
            throw new ServiceException("name 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setName(name);

        String nameCn = row.getNameCn() == null ? "" : row.getNameCn().trim();
        if (StringUtils.isBlank(nameCn))
        {
            throw new ServiceException("nameCn 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setNameCn(nameCn);

        if (row.getEstablishedYear() == null)
        {
            throw new ServiceException("establishedYear 不能为空", HttpStatus.BAD_REQUEST);
        }

        String registeredAddress = row.getRegisteredAddress() == null ? "" : row.getRegisteredAddress().trim();
        if (StringUtils.isBlank(registeredAddress))
        {
            throw new ServiceException("registeredAddress 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setRegisteredAddress(registeredAddress);

        String industry = row.getIndustry() == null ? "" : row.getIndustry().trim();
        if (StringUtils.isBlank(industry))
        {
            throw new ServiceException("industry 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setIndustry(industry);

        String enterpriseType = row.getEnterpriseType() == null ? "" : row.getEnterpriseType().trim();
        if (StringUtils.isBlank(enterpriseType))
        {
            throw new ServiceException("enterpriseType 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setEnterpriseType(normalizeEnterpriseType(enterpriseType));

        if (row.getParkNameCn() == null)
        {
            row.setParkNameCn("");
        }
        else
        {
            row.setParkNameCn(row.getParkNameCn().trim());
        }
        if (row.getTaxCode() != null)
        {
            row.setTaxCode(row.getTaxCode().trim());
        }
        if (row.getLegalRepresentative() != null)
        {
            row.setLegalRepresentative(row.getLegalRepresentative().trim());
        }
        if (row.getCountryOfOrigin() != null)
        {
            row.setCountryOfOrigin(row.getCountryOfOrigin().trim());
        }
        if (row.getStatus() != null)
        {
            row.setStatus(row.getStatus().trim());
        }
        if (row.getCreatedAt() != null)
        {
            row.setCreatedAt(row.getCreatedAt().trim());
        }
    }

    /**
     * 写入归一：WFOE/外商独资/FDI → 外国直接投资 (FDI)；合资企业新旧名 → 合资企业 (JV)。
     */
    static String normalizeEnterpriseType(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            return raw;
        }
        String t = raw.trim();
        if (containsAny(t, "WFOE", "外商独资", "FDI", "外国直接投资"))
        {
            return ENTERPRISE_TYPE_FDI;
        }
        if (containsAny(t, "JV", "合资企业"))
        {
            return ENTERPRISE_TYPE_JV;
        }
        return t;
    }

    /**
     * 查询兼容：FDI 筛选同时命中历史 WFOE；合资企业筛选同时命中「中外合资企业」。
     */
    static List<String> expandEnterpriseTypeFilter(String raw)
    {
        String canonical = normalizeEnterpriseType(raw);
        if (ENTERPRISE_TYPE_FDI.equals(canonical))
        {
            return Arrays.asList(ENTERPRISE_TYPE_FDI, LEGACY_ENTERPRISE_TYPE_WFOE);
        }
        if (ENTERPRISE_TYPE_JV.equals(canonical))
        {
            return Arrays.asList(ENTERPRISE_TYPE_JV, LEGACY_ENTERPRISE_TYPE_JV);
        }
        return Collections.singletonList(raw.trim());
    }

    private static boolean containsAny(String haystack, String... needles)
    {
        for (String needle : needles)
        {
            if (haystack.contains(needle))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 若 parkId 对应未删除园区，用园区 name_cn 覆盖 parkNameCn；查不到则保留前端传入值。
     */
    private void backfillParkNameCn(CrmTenantEnterprise row)
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
