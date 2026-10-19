package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmEmailAnalysis;
import com.ruoyi.crm.domain.dto.EmailAnalysisEntityItemDto;
import com.ruoyi.crm.domain.dto.EmailAnalysisResultDto;
import com.ruoyi.crm.domain.dto.EmailAnalysisSentimentDto;
import com.ruoyi.crm.domain.dto.EmailAnalysisSuggestedLeadDto;
import com.ruoyi.crm.domain.dto.EmailAnalysisSuggestedTicketDto;
import com.ruoyi.crm.domain.dto.EmailAnalysisUpsertDto;
import com.ruoyi.crm.domain.vo.EmailAnalysisVo;
import com.ruoyi.crm.mapper.CrmEmailAnalysisMapper;
import com.ruoyi.crm.service.ICrmEmailAnalysisService;
import com.ruoyi.crm.service.support.EmailAnalysisValidator;

@Service
public class CrmEmailAnalysisServiceImpl implements ICrmEmailAnalysisService
{
    @Autowired
    private CrmEmailAnalysisMapper crmEmailAnalysisMapper;

    @Override
    public EmailAnalysisVo getLatest(String tenantId, String messageExternalId, String locale,
            String provider, String externalName, String bodyHash)
    {
        tenantId = requireTenantId(tenantId);
        CrmEmailAnalysis row = selectCachedRow(
                tenantId,
                messageExternalId,
                locale,
                provider,
                externalName);
        if (row == null)
        {
            return null;
        }
        if (StringUtils.isNotBlank(bodyHash) && !bodyHash.trim().equals(row.getBodyHash()))
        {
            return null;
        }
        return toVo(row);
    }

    @Override
    public EmailAnalysisVo upsert(String tenantId, EmailAnalysisUpsertDto dto)
    {
        tenantId = requireTenantId(tenantId);
        CrmEmailAnalysis existing = selectCachedRow(
                tenantId,
                dto.getMessageExternalId(),
                dto.getLocale(),
                dto.getProvider(),
                dto.getExternalName());
        if (existing != null
                && StringUtils.isNotBlank(dto.getBodyHash())
                && dto.getBodyHash().trim().equals(existing.getBodyHash()))
        {
            return toVo(existing);
        }

        EmailAnalysisValidator.validateUpsertDto(dto);

        EmailAnalysisResultDto analysis = dto.getAnalysis();
        String analysisJson = EmailAnalysisValidator.serializeAnalysisJson(analysis);
        String username = SecurityUtils.getUsername();
        String userId = StringUtils.isNotBlank(dto.getUserId())
                ? dto.getUserId().trim()
                : String.valueOf(SecurityUtils.getUserId());

        CrmEmailAnalysis row = new CrmEmailAnalysis();
        row.setAnalysisId(IdUtils.fastUUID());
        row.setTenantId(tenantId);
        row.setUserId(userId);
        row.setMessageExternalId(dto.getMessageExternalId().trim());
        row.setProvider(StringUtils.trim(dto.getProvider()));
        row.setExternalName(StringUtils.trim(dto.getExternalName()));
        row.setLocale(dto.getLocale().trim());
        row.setSubject(StringUtils.trim(dto.getSubject()));
        row.setSender(StringUtils.trim(dto.getSender()));
        row.setBodyHash(dto.getBodyHash().trim());
        row.setExecutiveSummary(analysis.getExecutiveSummary());
        row.setCategory(analysis.getCategory());
        row.setUrgency(analysis.getUrgency());
        if (analysis.getSentiment() != null)
        {
            row.setSentimentLabel(analysis.getSentiment().getLabel());
            row.setSentimentScore(analysis.getSentiment().getScore());
            row.setSentimentDescription(analysis.getSentiment().getDescription());
        }
        row.setDetectedIntent(analysis.getDetectedIntent());
        row.setSuggestedAction(analysis.getSuggestedAction());
        row.setEntitiesJson(JSON.toJSONString(analysis.getEntities()));
        row.setSuggestedTicketJson(JSON.toJSONString(analysis.getSuggestedTicket()));
        row.setSuggestedLeadJson(JSON.toJSONString(analysis.getSuggestedLead()));
        row.setAnalysisJson(analysisJson);
        row.setModel(StringUtils.trim(dto.getModel()));
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());

        crmEmailAnalysisMapper.upsert(row);

        return getLatest(tenantId, dto.getMessageExternalId(), dto.getLocale(),
                dto.getProvider(), dto.getExternalName(), null);
    }

    private CrmEmailAnalysis selectCachedRow(String tenantId, String messageExternalId, String locale,
            String provider, String externalName)
    {
        if (StringUtils.isBlank(messageExternalId) || StringUtils.isBlank(locale))
        {
            return null;
        }
        return crmEmailAnalysisMapper.selectLatestByKey(
                tenantId,
                messageExternalId.trim(),
                locale.trim(),
                StringUtils.trim(provider),
                StringUtils.trim(externalName));
    }

    @Override
    public void remove(String tenantId, String analysisId)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(analysisId))
        {
            return;
        }
        crmEmailAnalysisMapper.logicalDeleteById(analysisId.trim(), tenantId, SecurityUtils.getUsername());
    }

    private EmailAnalysisVo toVo(CrmEmailAnalysis row)
    {
        EmailAnalysisVo vo = new EmailAnalysisVo();
        vo.setAnalysisId(row.getAnalysisId());
        vo.setMessageExternalId(row.getMessageExternalId());
        vo.setProvider(row.getProvider());
        vo.setExternalName(row.getExternalName());
        vo.setLocale(row.getLocale());
        vo.setBodyHash(row.getBodyHash());
        vo.setAnalysis(resolveAnalysis(row));
        vo.setModel(row.getModel());
        vo.setCreateTime(row.getCreateTime());
        vo.setUpdateTime(row.getUpdateTime());
        return vo;
    }

    private EmailAnalysisResultDto resolveAnalysis(CrmEmailAnalysis row)
    {
        if (StringUtils.isNotBlank(row.getAnalysisJson()))
        {
            return JSON.parseObject(row.getAnalysisJson(), EmailAnalysisResultDto.class);
        }
        return assembleAnalysisFromColumns(row);
    }

    private EmailAnalysisResultDto assembleAnalysisFromColumns(CrmEmailAnalysis row)
    {
        EmailAnalysisResultDto analysis = new EmailAnalysisResultDto();
        analysis.setExecutiveSummary(row.getExecutiveSummary());
        analysis.setCategory(row.getCategory());
        analysis.setUrgency(row.getUrgency());
        analysis.setDetectedIntent(row.getDetectedIntent());
        analysis.setSuggestedAction(row.getSuggestedAction());

        EmailAnalysisSentimentDto sentiment = new EmailAnalysisSentimentDto();
        sentiment.setLabel(row.getSentimentLabel());
        sentiment.setScore(row.getSentimentScore());
        sentiment.setDescription(row.getSentimentDescription());
        analysis.setSentiment(sentiment);

        if (StringUtils.isNotBlank(row.getEntitiesJson()))
        {
            analysis.setEntities(JSON.parseObject(row.getEntitiesJson(),
                    new TypeReference<List<EmailAnalysisEntityItemDto>>() {}));
        }
        if (StringUtils.isNotBlank(row.getSuggestedTicketJson()))
        {
            analysis.setSuggestedTicket(JSON.parseObject(row.getSuggestedTicketJson(),
                    EmailAnalysisSuggestedTicketDto.class));
        }
        if (StringUtils.isNotBlank(row.getSuggestedLeadJson()))
        {
            analysis.setSuggestedLead(JSON.parseObject(row.getSuggestedLeadJson(),
                    EmailAnalysisSuggestedLeadDto.class));
        }
        return analysis;
    }

    private String requireTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
        }
        return tenantId.trim();
    }
}
