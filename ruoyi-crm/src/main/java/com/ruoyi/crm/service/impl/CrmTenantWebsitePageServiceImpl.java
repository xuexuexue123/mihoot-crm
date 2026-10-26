package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmTenantWebsitePage;
import com.ruoyi.crm.domain.dto.CreateTenantWebsitePageRequest;
import com.ruoyi.crm.domain.dto.SaveTenantWebsitePageDraftRequest;
import com.ruoyi.crm.domain.vo.PreviewTokenVo;
import com.ruoyi.crm.domain.vo.TenantWebsitePageDetailVo;
import com.ruoyi.crm.domain.vo.TenantWebsitePageSummaryVo;
import com.ruoyi.crm.mapper.CrmTenantWebsitePageMapper;
import com.ruoyi.crm.service.ICrmTenantWebsitePageService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.service.support.TenantWebsitePageVoSupport;
import com.ruoyi.crm.service.support.TenantWebsitePathUtils;
import com.ruoyi.crm.service.support.TenantWebsiteSchemaValidator;
import com.ruoyi.crm.service.support.WebsitePreviewTokenService;
import com.ruoyi.crm.service.support.WebsitePreviewTokenService.PreviewTokenIssueResult;

@Service
public class CrmTenantWebsitePageServiceImpl implements ICrmTenantWebsitePageService
{
    @Autowired
    private CrmTenantWebsitePageMapper pageMapper;

    @Autowired
    private TenantWebsitePathUtils pathUtils;

    @Autowired
    private TenantWebsiteSchemaValidator schemaValidator;

    @Autowired
    private WebsitePreviewTokenService previewTokenService;

    @Override
    public List<TenantWebsitePageSummaryVo> listSummaries()
    {
        return pageMapper.selectSummaryList();
    }

    @Override
    public TenantWebsitePageDetailVo getDetail(String id)
    {
        CrmTenantWebsitePage page = requirePage(id);
        return TenantWebsitePageVoSupport.toDetailVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenantWebsitePageDetailVo create(CreateTenantWebsitePageRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求不能为空", HttpStatus.BAD_REQUEST);
        }
        pathUtils.requireNonBlankTitle(request.getTitle());
        String path = pathUtils.normalizePath(request.getPath());
        pathUtils.validatePathForCreate(path);

        if (pageMapper.countByPath(path) > 0)
        {
            throw new ServiceException("path 已存在", HttpStatus.CONFLICT);
        }

        JsonNode schema = request.getSchema();
        if (schema != null && !schema.isNull())
        {
            schemaValidator.validateSchemaLight(schema, path);
        }

        Date now = DateUtils.getNowDate();
        CrmTenantWebsitePage page = new CrmTenantWebsitePage();
        page.setId(UUID.randomUUID().toString());
        page.setPath(path);
        page.setTitle(request.getTitle().trim());
        page.setDraftSchema(TenantWebsitePageVoSupport.schemaToJson(schema));
        page.setStatus(CrmTenantWebsitePage.STATUS_DRAFT);
        page.setSchemaVersion(TenantWebsiteSchemaValidator.SUPPORTED_SCHEMA_VERSION);
        page.setCreatedAt(now);
        page.setUpdatedAt(now);
        pageMapper.insert(page);
        return TenantWebsitePageVoSupport.toDetailVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDraft(String id, SaveTenantWebsitePageDraftRequest request)
    {
        if (request == null || request.getSchema() == null || request.getSchema().isNull())
        {
            throw new ServiceException("INVALID_SCHEMA", HttpStatus.BAD_REQUEST);
        }
        CrmTenantWebsitePage page = requirePage(id);
        JsonNode schema = request.getSchema();
        schemaValidator.validateSchemaLight(schema, page.getPath());
        page.setDraftSchema(TenantWebsitePageVoSupport.schemaToJson(schema));
        page.setUpdatedAt(DateUtils.getNowDate());
        pageMapper.update(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> publish(String id)
    {
        CrmTenantWebsitePage page = requirePage(id);
        if (StringUtils.isBlank(page.getDraftSchema()))
        {
            throw new ServiceException("无草稿可发布", HttpStatus.BAD_REQUEST);
        }
        JsonNode draft = TenantWebsitePageVoSupport.parseSchema(page.getDraftSchema());
        schemaValidator.validateSchemaLight(draft, page.getPath());

        Date now = DateUtils.getNowDate();
        page.setPublishedSchema(page.getDraftSchema());
        page.setStatus(CrmTenantWebsitePage.STATUS_PUBLISHED);
        page.setPublishedAt(now);
        page.setUpdatedAt(now);
        pageMapper.update(page);

        Map<String, Object> result = new HashMap<>();
        result.put("id", page.getId());
        result.put("status", page.getStatus());
        result.put("publishedAt", TenantWebsitePageVoSupport.toIso8601(page.getPublishedAt()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> unpublish(String id)
    {
        CrmTenantWebsitePage page = requirePage(id);
        page.setStatus(CrmTenantWebsitePage.STATUS_DRAFT);
        page.setUpdatedAt(DateUtils.getNowDate());
        pageMapper.update(page);

        Map<String, Object> result = new HashMap<>();
        result.put("id", page.getId());
        result.put("status", page.getStatus());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id)
    {
        CrmTenantWebsitePage page = requirePage(id);
        if ("/".equals(page.getPath()))
        {
            throw new ServiceException("不能删除首页", HttpStatus.CONFLICT);
        }
        pageMapper.deleteById(id);
    }

    @Override
    public PreviewTokenVo issuePreviewToken(String id)
    {
        CrmTenantWebsitePage page = requirePage(id);
        String tenantName = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenantName))
        {
            throw new ServiceException("tenant 不能为空", HttpStatus.BAD_REQUEST);
        }
        PreviewTokenIssueResult issued = previewTokenService.issue(tenantName, page.getId(), page.getPath());
        return new PreviewTokenVo(issued.getToken(), issued.getExpiresAt().toString());
    }

    private CrmTenantWebsitePage requirePage(String id)
    {
        if (StringUtils.isBlank(id))
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmTenantWebsitePage page = pageMapper.selectById(id);
        if (page == null)
        {
            throw new ServiceException("页面不存在", HttpStatus.NOT_FOUND);
        }
        return page;
    }
}
