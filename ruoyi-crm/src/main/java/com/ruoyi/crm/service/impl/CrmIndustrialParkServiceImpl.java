package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
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
import com.ruoyi.crm.domain.CrmIndustrialPark;
import com.ruoyi.crm.domain.dto.IndustrialParkBatchRequest;
import com.ruoyi.crm.domain.dto.IndustrialParkBatchResult;
import com.ruoyi.crm.mapper.CrmIndustrialParkMapper;
import com.ruoyi.crm.service.ICrmIndustrialParkService;

/**
 * 工业园区 Service 实现
 */
@Service
public class CrmIndustrialParkServiceImpl implements ICrmIndustrialParkService
{
    private static final String ID_PREFIX = "vn-ip-";

    @Autowired
    private CrmIndustrialParkMapper crmIndustrialParkMapper;

    @Override
    public CrmIndustrialPark selectCrmIndustrialParkById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return null;
        }
        return crmIndustrialParkMapper.selectCrmIndustrialParkById(id.trim());
    }

    @Override
    public List<CrmIndustrialPark> selectCrmIndustrialParkList(CrmIndustrialPark query)
    {
        if (query == null)
        {
            query = new CrmIndustrialPark();
        }
        normalizeListFilters(query);
        return crmIndustrialParkMapper.selectCrmIndustrialParkList(query);
    }

    @Override
    public CrmIndustrialPark insertCrmIndustrialPark(CrmIndustrialPark row)
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
            CrmIndustrialPark existing = crmIndustrialParkMapper.selectCrmIndustrialParkById(row.getId());
            if (existing != null)
            {
                throw new ServiceException("工业园区 id 已存在: " + row.getId(), HttpStatus.BAD_REQUEST);
            }
        }
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmIndustrialParkMapper.insertCrmIndustrialPark(row);
        return crmIndustrialParkMapper.selectCrmIndustrialParkById(row.getId());
    }

    @Override
    public CrmIndustrialPark updateCrmIndustrialPark(CrmIndustrialPark row)
    {
        validateAndNormalize(row, true);
        CrmIndustrialPark existing = crmIndustrialParkMapper.selectCrmIndustrialParkById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("工业园区不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmIndustrialParkMapper.updateCrmIndustrialPark(row);
        if (rows <= 0)
        {
            throw new ServiceException("工业园区不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return crmIndustrialParkMapper.selectCrmIndustrialParkById(row.getId());
    }

    @Override
    public int deleteCrmIndustrialParkById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return 0;
        }
        return crmIndustrialParkMapper.logicalDeleteById(id.trim(), SecurityUtils.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IndustrialParkBatchResult batch(IndustrialParkBatchRequest request)
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

        for (CrmIndustrialPark row : request.getRows())
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
            CrmIndustrialPark existing = hasId
                    ? crmIndustrialParkMapper.selectCrmIndustrialParkById(row.getId().trim())
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
                crmIndustrialParkMapper.updateCrmIndustrialPark(row);
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
                crmIndustrialParkMapper.insertCrmIndustrialPark(row);
                inserted++;
            }
        }
        return new IndustrialParkBatchResult(inserted, updated, skipped);
    }

    private void normalizeListFilters(CrmIndustrialPark query)
    {
        if (StringUtils.isNotBlank(query.getSearchQuery()))
        {
            query.setSearchQuery(query.getSearchQuery().trim());
        }
        if (StringUtils.isNotBlank(query.getDominantIndustry()))
        {
            query.setDominantIndustry(query.getDominantIndustry().trim());
        }
        if (query.getMaxLandRent() != null && query.getMaxLandRent().compareTo(BigDecimal.ZERO) <= 0)
        {
            query.setMaxLandRent(null);
        }
        if (query.getMinOccupancy() != null && query.getMinOccupancy().compareTo(BigDecimal.ZERO) <= 0)
        {
            query.setMinOccupancy(null);
        }
        if (query.getMaxDistancePort() != null && query.getMaxDistancePort().compareTo(BigDecimal.ZERO) <= 0)
        {
            query.setMaxDistancePort(null);
        }
        // Avoid treating entity fields as filters when used only as list carrier
        if ("ALL".equalsIgnoreCase(query.getRegion()))
        {
            query.setRegion(null);
        }
        if ("ALL".equalsIgnoreCase(query.getProvince()))
        {
            query.setProvince(null);
        }
        if ("ALL".equalsIgnoreCase(query.getParkLevel()))
        {
            query.setParkLevel(null);
        }
        if ("ALL".equalsIgnoreCase(query.getVerificationStatus()))
        {
            query.setVerificationStatus(null);
        }
        if ("ALL".equalsIgnoreCase(query.getDominantIndustry()))
        {
            query.setDominantIndustry(null);
        }
    }

    private void validateAndNormalize(CrmIndustrialPark row, boolean requireId)
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

        String region = row.getRegion() == null ? "" : row.getRegion().trim();
        if (StringUtils.isBlank(region))
        {
            throw new ServiceException("region 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setRegion(region);

        if (row.getNameNative() != null)
        {
            row.setNameNative(row.getNameNative().trim());
        }
        if (row.getProvince() != null)
        {
            row.setProvince(row.getProvince().trim());
        }
        if (row.getDominantIndustries() == null)
        {
            row.setDominantIndustries(new ArrayList<>());
        }
        if (row.getEncouragedIndustries() == null)
        {
            row.setEncouragedIndustries(new ArrayList<>());
        }
        if (row.getAnchorTenants() == null)
        {
            row.setAnchorTenants(new ArrayList<>());
        }
        if (row.getPhotos() == null)
        {
            row.setPhotos(new ArrayList<>());
        }
        if (row.getIsSez() == null)
        {
            row.setIsSez(Boolean.FALSE);
        }
        if (row.getHasEmployeeDormitory() == null)
        {
            row.setHasEmployeeDormitory(Boolean.FALSE);
        }
        if (row.getHasVocationalSchool() == null)
        {
            row.setHasVocationalSchool(Boolean.FALSE);
        }
        if (row.getHasNationalGrid() == null)
        {
            row.setHasNationalGrid(Boolean.FALSE);
        }
        if (row.getHasStandbyPower() == null)
        {
            row.setHasStandbyPower(Boolean.FALSE);
        }
        if (StringUtils.isBlank(row.getVerificationStatus()))
        {
            row.setVerificationStatus("未核实");
        }
        else
        {
            row.setVerificationStatus(row.getVerificationStatus().trim());
        }
        if (StringUtils.isNotBlank(row.getLastUpdated()))
        {
            row.setLastUpdated(row.getLastUpdated().trim());
        }
        if (row.getLat() == null)
        {
            row.setLat(0d);
        }
        if (row.getLng() == null)
        {
            row.setLng(0d);
        }
    }
}
