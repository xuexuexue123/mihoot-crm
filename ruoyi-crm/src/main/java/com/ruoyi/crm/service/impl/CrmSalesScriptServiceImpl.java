package com.ruoyi.crm.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmSalesScript;
import com.ruoyi.crm.domain.CrmWecomMaterial;
import com.ruoyi.crm.knowledge.CrmKnowledgeIngestService;
import com.ruoyi.crm.mapper.CrmSalesScriptMapper;
import com.ruoyi.crm.mapper.CrmWecomMaterialMapper;
import com.ruoyi.crm.service.ICrmSalesScriptService;

/**
 * 话术库 Service 实现
 */
@Service
public class CrmSalesScriptServiceImpl implements ICrmSalesScriptService
{
    private static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList("marketing", "qa", "sop"));

    @Autowired
    private CrmSalesScriptMapper crmSalesScriptMapper;

    @Autowired
    private CrmWecomMaterialMapper crmWecomMaterialMapper;

    @Autowired
    private CrmKnowledgeIngestService knowledgeIngestService;

    @Override
    public CrmSalesScript selectCrmSalesScriptByScriptId(String scriptId)
    {
        if (StringUtils.isBlank(scriptId))
        {
            return null;
        }
        return crmSalesScriptMapper.selectCrmSalesScriptByScriptId(scriptId.trim());
    }

    @Override
    public List<CrmSalesScript> selectCrmSalesScriptList(CrmSalesScript query)
    {
        if (query == null)
        {
            query = new CrmSalesScript();
        }
        if (StringUtils.isNotBlank(query.getKeyword()))
        {
            query.setKeyword(query.getKeyword().trim());
        }
        return crmSalesScriptMapper.selectCrmSalesScriptList(query);
    }

    @Override
    public List<String> selectDistinctCategories()
    {
        return crmSalesScriptMapper.selectDistinctCategories();
    }

    @Override
    public int insertCrmSalesScript(CrmSalesScript row)
    {
        validateAndNormalize(row, false);
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(row.getScriptId()))
        {
            row.setScriptId(IdUtils.fastUUID());
        }
        else
        {
            row.setScriptId(row.getScriptId().trim());
        }
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmSalesScriptMapper.insertCrmSalesScript(row);
        if (rows > 0)
        {
            knowledgeIngestService.onSalesScriptChanged(row.getScriptId());
        }
        return rows;
    }

    @Override
    public int updateCrmSalesScript(CrmSalesScript row)
    {
        validateAndNormalize(row, true);
        CrmSalesScript existing = crmSalesScriptMapper.selectCrmSalesScriptByScriptId(row.getScriptId());
        if (existing == null)
        {
            throw new ServiceException("话术不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = crmSalesScriptMapper.updateCrmSalesScript(row);
        if (rows > 0)
        {
            knowledgeIngestService.onSalesScriptChanged(row.getScriptId());
        }
        return rows;
    }

    @Override
    public int deleteCrmSalesScriptByScriptId(String scriptId)
    {
        if (StringUtils.isBlank(scriptId))
        {
            return 0;
        }
        int rows = crmSalesScriptMapper.logicalDeleteByScriptId(scriptId.trim(), SecurityUtils.getUsername());
        if (rows > 0)
        {
            knowledgeIngestService.onSalesScriptDeleted(scriptId.trim());
        }
        return rows;
    }

    private void validateAndNormalize(CrmSalesScript row, boolean requireId)
    {
        if (row == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (requireId && StringUtils.isBlank(row.getScriptId()))
        {
            throw new ServiceException("scriptId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isNotBlank(row.getScriptId()))
        {
            row.setScriptId(row.getScriptId().trim());
        }

        String scriptType = row.getScriptType() == null ? "" : row.getScriptType().trim();
        if (!ALLOWED_TYPES.contains(scriptType))
        {
            throw new ServiceException("scriptType 必须为 marketing / qa / sop", HttpStatus.BAD_REQUEST);
        }
        row.setScriptType(scriptType);

        String title = row.getTitle() == null ? "" : row.getTitle().trim();
        if (StringUtils.isBlank(title))
        {
            throw new ServiceException("title 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (title.length() > 200)
        {
            throw new ServiceException("title 不能超过 200 字", HttpStatus.BAD_REQUEST);
        }
        row.setTitle(title);

        if (StringUtils.isBlank(row.getContent()))
        {
            throw new ServiceException("content 不能为空", HttpStatus.BAD_REQUEST);
        }

        if (row.getCategory() != null)
        {
            String category = row.getCategory().trim();
            if (category.length() > 100)
            {
                throw new ServiceException("category 不能超过 100 字", HttpStatus.BAD_REQUEST);
            }
            row.setCategory(category.isEmpty() ? null : category);
        }

        if (row.getSortOrder() == null)
        {
            row.setSortOrder(0);
        }

        if (StringUtils.isBlank(row.getStatus()))
        {
            row.setStatus("0");
        }
        else
        {
            String status = row.getStatus().trim();
            if (!"0".equals(status) && !"1".equals(status))
            {
                throw new ServiceException("status 必须为 0 或 1", HttpStatus.BAD_REQUEST);
            }
            row.setStatus(status);
        }

        if (row.getMaterialId() != null)
        {
            CrmWecomMaterial material = crmWecomMaterialMapper.selectById(row.getMaterialId());
            if (material == null)
            {
                throw new ServiceException("关联素材不存在: materialId=" + row.getMaterialId(), HttpStatus.BAD_REQUEST);
            }
        }
    }
}
