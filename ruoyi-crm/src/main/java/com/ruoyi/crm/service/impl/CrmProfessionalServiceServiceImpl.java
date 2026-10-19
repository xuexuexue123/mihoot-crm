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
import com.ruoyi.crm.domain.CrmProfessionalService;
import com.ruoyi.crm.domain.dto.ProfessionalServiceBatchRequest;
import com.ruoyi.crm.domain.dto.ProfessionalServiceBatchResult;
import com.ruoyi.crm.mapper.CrmProfessionalServiceMapper;
import com.ruoyi.crm.service.ICrmProfessionalServiceService;

/**
 * 专业服务机构 Service 实现
 */
@Service
public class CrmProfessionalServiceServiceImpl implements ICrmProfessionalServiceService
{
    private static final String ID_PREFIX = "srv-";

    @Autowired
    private CrmProfessionalServiceMapper crmProfessionalServiceMapper;

    @Override
    public CrmProfessionalService selectCrmProfessionalServiceById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return null;
        }
        return crmProfessionalServiceMapper.selectCrmProfessionalServiceById(id.trim());
    }

    @Override
    public List<CrmProfessionalService> selectCrmProfessionalServiceList(CrmProfessionalService query)
    {
        if (query == null)
        {
            query = new CrmProfessionalService();
        }
        normalizeListFilters(query);
        return crmProfessionalServiceMapper.selectCrmProfessionalServiceList(query);
    }

    @Override
    public CrmProfessionalService insertCrmProfessionalService(CrmProfessionalService row)
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
            CrmProfessionalService existing = crmProfessionalServiceMapper.selectCrmProfessionalServiceById(row.getId());
            if (existing != null)
            {
                throw new ServiceException("专业服务机构 id 已存在: " + row.getId(), HttpStatus.BAD_REQUEST);
            }
        }
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmProfessionalServiceMapper.insertCrmProfessionalService(row);
        return crmProfessionalServiceMapper.selectCrmProfessionalServiceById(row.getId());
    }

    @Override
    public CrmProfessionalService updateCrmProfessionalService(CrmProfessionalService row)
    {
        validateAndNormalize(row, true);
        CrmProfessionalService existing = crmProfessionalServiceMapper.selectCrmProfessionalServiceById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("专业服务机构不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmProfessionalServiceMapper.updateCrmProfessionalService(row);
        if (rows <= 0)
        {
            throw new ServiceException("专业服务机构不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return crmProfessionalServiceMapper.selectCrmProfessionalServiceById(row.getId());
    }

    @Override
    public int deleteCrmProfessionalServiceById(String id)
    {
        if (StringUtils.isBlank(id))
        {
            return 0;
        }
        return crmProfessionalServiceMapper.logicalDeleteById(id.trim(), SecurityUtils.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProfessionalServiceBatchResult batch(ProfessionalServiceBatchRequest request)
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

        for (CrmProfessionalService row : request.getRows())
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
            CrmProfessionalService existing = hasId
                    ? crmProfessionalServiceMapper.selectCrmProfessionalServiceById(row.getId().trim())
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
                crmProfessionalServiceMapper.updateCrmProfessionalService(row);
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
                crmProfessionalServiceMapper.insertCrmProfessionalService(row);
                inserted++;
            }
        }
        return new ProfessionalServiceBatchResult(inserted, updated, skipped);
    }

    private void normalizeListFilters(CrmProfessionalService query)
    {
        if (StringUtils.isNotBlank(query.getSearchQuery()))
        {
            query.setSearchQuery(query.getSearchQuery().trim());
        }
        if ("ALL".equalsIgnoreCase(query.getCategory()))
        {
            query.setCategory(null);
        }
        if ("ALL".equalsIgnoreCase(query.getBackground()))
        {
            query.setBackground(null);
        }
        if ("ALL".equalsIgnoreCase(query.getProvince()))
        {
            query.setProvince(null);
        }
    }

    private void validateAndNormalize(CrmProfessionalService row, boolean requireId)
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

        String category = row.getCategory() == null ? "" : row.getCategory().trim();
        if (StringUtils.isBlank(category))
        {
            throw new ServiceException("category 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setCategory(category);

        String background = row.getBackground() == null ? "" : row.getBackground().trim();
        if (StringUtils.isBlank(background))
        {
            throw new ServiceException("background 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setBackground(background);

        String keyCapabilities = row.getKeyCapabilities() == null ? "" : row.getKeyCapabilities().trim();
        if (StringUtils.isBlank(keyCapabilities))
        {
            throw new ServiceException("keyCapabilities 不能为空", HttpStatus.BAD_REQUEST);
        }
        row.setKeyCapabilities(keyCapabilities);

        if (row.getCoreServices() == null)
        {
            row.setCoreServices(new ArrayList<>());
        }

        if (row.getNameEn() != null)
        {
            row.setNameEn(row.getNameEn().trim());
        }
        if (row.getProvince() != null)
        {
            row.setProvince(row.getProvince().trim());
        }
        if (row.getTargetClients() != null)
        {
            row.setTargetClients(row.getTargetClients().trim());
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
        if (row.getCreatedAt() != null)
        {
            row.setCreatedAt(row.getCreatedAt().trim());
        }
    }
}
