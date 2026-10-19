package com.ruoyi.crm.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmAiChatMessage;
import com.ruoyi.crm.domain.CrmAiChatSession;
import com.ruoyi.crm.domain.dto.AiChatMessageBatchRequest;
import com.ruoyi.crm.domain.dto.AiChatMessageBatchRequest.AiChatMessageItem;
import com.ruoyi.crm.domain.dto.AiChatSessionCreateRequest;
import com.ruoyi.crm.domain.vo.AiChatMessageVo;
import com.ruoyi.crm.domain.vo.AiChatSessionVo;
import com.ruoyi.crm.mapper.CrmAiChatMessageMapper;
import com.ruoyi.crm.mapper.CrmAiChatSessionMapper;
import com.ruoyi.crm.service.ICrmAiChatService;
import com.ruoyi.crm.support.CrmMailUserScopeSupport;

@Service
public class CrmAiChatServiceImpl implements ICrmAiChatService
{
    private static final Set<String> ALLOWED_ROLES = new HashSet<>();
    private static final int TITLE_MAX_LEN = 500;

    static
    {
        ALLOWED_ROLES.add("user");
        ALLOWED_ROLES.add("assistant");
        ALLOWED_ROLES.add("system");
    }

    @Autowired
    private CrmAiChatSessionMapper crmAiChatSessionMapper;

    @Autowired
    private CrmAiChatMessageMapper crmAiChatMessageMapper;

    @Override
    public List<AiChatSessionVo> listSessions(String tenantId)
    {
        tenantId = requireTenantId(tenantId);
        String userId = CrmMailUserScopeSupport.currentUserId();
        return crmAiChatSessionMapper.selectSessionList(tenantId, userId);
    }

    @Override
    public AiChatSessionVo createOrTouchSession(String tenantId, AiChatSessionCreateRequest request)
    {
        tenantId = requireTenantId(tenantId);
        String userId = CrmMailUserScopeSupport.currentUserId();
        String sessionId = request != null && StringUtils.isNotEmpty(request.getSessionId())
                ? request.getSessionId().trim() : IdUtils.fastUUID();
        String title = truncateTitle(request != null ? request.getTitle() : null);

        CrmAiChatSession existing = crmAiChatSessionMapper.selectSessionById(tenantId, userId, sessionId);
        if (existing != null)
        {
            crmAiChatSessionMapper.touchSession(tenantId, userId, sessionId, title);
        }
        else
        {
            CrmAiChatSession row = new CrmAiChatSession();
            row.setSessionId(sessionId);
            row.setTenantId(tenantId);
            row.setUserId(userId);
            row.setTitle(title);
            row.setDelFlag("0");
            row.setCreateTime(DateUtils.getNowDate());
            row.setUpdateTime(DateUtils.getNowDate());
            crmAiChatSessionMapper.insertSession(row);
        }

        CrmAiChatSession session = crmAiChatSessionMapper.selectSessionById(tenantId, userId, sessionId);
        return toSessionVo(session);
    }

    @Override
    public List<AiChatMessageVo> listMessages(String tenantId, String sessionId)
    {
        tenantId = requireTenantId(tenantId);
        String userId = CrmMailUserScopeSupport.currentUserId();
        requireOwnedSession(tenantId, userId, sessionId);
        return crmAiChatMessageMapper.selectMessagesBySession(tenantId, userId, sessionId);
    }

    @Override
    @Transactional
    public void appendMessages(String tenantId, String sessionId, AiChatMessageBatchRequest request)
    {
        tenantId = requireTenantId(tenantId);
        String userId = CrmMailUserScopeSupport.currentUserId();
        requireOwnedSession(tenantId, userId, sessionId);
        if (request == null || request.getMessages() == null || request.getMessages().isEmpty())
        {
            throw new ServiceException("messages 不能为空", HttpStatus.BAD_REQUEST);
        }

        String titleCandidate = null;
        for (AiChatMessageItem item : request.getMessages())
        {
            validateMessageItem(item);
            CrmAiChatMessage existing = crmAiChatMessageMapper.selectMessageById(item.getMessageId());
            if (existing != null)
            {
                if (!sessionId.equals(existing.getSessionId()) || !tenantId.equals(existing.getTenantId()))
                {
                    throw new ServiceException("messageId 已属于其他会话", HttpStatus.BAD_REQUEST);
                }
                CrmAiChatMessage update = new CrmAiChatMessage();
                update.setMessageId(item.getMessageId());
                update.setSessionId(sessionId);
                update.setTenantId(tenantId);
                update.setRole(item.getRole().trim().toLowerCase());
                update.setPartsJson(item.getPartsJson());
                update.setMetadataJson(item.getMetadataJson());
                crmAiChatMessageMapper.updateMessage(update);
            }
            else
            {
                CrmAiChatMessage row = new CrmAiChatMessage();
                row.setMessageId(item.getMessageId().trim());
                row.setSessionId(sessionId);
                row.setTenantId(tenantId);
                row.setRole(item.getRole().trim().toLowerCase());
                row.setPartsJson(item.getPartsJson());
                row.setMetadataJson(item.getMetadataJson());
                row.setCreateTime(DateUtils.getNowDate());
                crmAiChatMessageMapper.insertMessage(row);
            }
            if (titleCandidate == null && "user".equalsIgnoreCase(item.getRole()))
            {
                titleCandidate = deriveTitleFromParts(item.getPartsJson());
            }
        }

        CrmAiChatSession session = crmAiChatSessionMapper.selectSessionById(tenantId, userId, sessionId);
        String touchTitle = StringUtils.isEmpty(session.getTitle()) ? titleCandidate : null;
        crmAiChatSessionMapper.touchSession(tenantId, userId, sessionId, touchTitle);
    }

    @Override
    public void deleteSession(String tenantId, String sessionId)
    {
        tenantId = requireTenantId(tenantId);
        String userId = CrmMailUserScopeSupport.currentUserId();
        requireOwnedSession(tenantId, userId, sessionId);
        crmAiChatSessionMapper.logicalDeleteSession(tenantId, userId, sessionId);
    }

    private CrmAiChatSession requireOwnedSession(String tenantId, String userId, String sessionId)
    {
        if (StringUtils.isBlank(sessionId))
        {
            throw new ServiceException("sessionId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmAiChatSession session = crmAiChatSessionMapper.selectSessionById(tenantId, userId, sessionId.trim());
        if (session == null)
        {
            throw new ServiceException("会话不存在或无权访问", HttpStatus.FORBIDDEN);
        }
        return session;
    }

    private void validateMessageItem(AiChatMessageItem item)
    {
        if (item == null || StringUtils.isBlank(item.getMessageId()))
        {
            throw new ServiceException("messageId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(item.getRole()) || !ALLOWED_ROLES.contains(item.getRole().trim().toLowerCase()))
        {
            throw new ServiceException("role 必须为 user/assistant/system", HttpStatus.BAD_REQUEST);
        }
        validateJson(item.getPartsJson(), "partsJson");
        if (StringUtils.isNotEmpty(item.getMetadataJson()))
        {
            validateJson(item.getMetadataJson(), "metadataJson");
        }
    }

    private void validateJson(String json, String fieldName)
    {
        if (StringUtils.isBlank(json))
        {
            throw new ServiceException(fieldName + " 不能为空", HttpStatus.BAD_REQUEST);
        }
        try
        {
            JSON.parse(json);
        }
        catch (Exception ex)
        {
            throw new ServiceException(fieldName + " 不是合法 JSON", HttpStatus.BAD_REQUEST);
        }
    }

    private String deriveTitleFromParts(String partsJson)
    {
        try
        {
            Object parsed = JSON.parse(partsJson);
            String text = parsed != null ? parsed.toString() : partsJson;
            text = text.replaceAll("\\s+", " ").trim();
            if (text.length() > TITLE_MAX_LEN)
            {
                return text.substring(0, TITLE_MAX_LEN);
            }
            return text;
        }
        catch (Exception ex)
        {
            return truncateTitle(partsJson);
        }
    }

    private AiChatSessionVo toSessionVo(CrmAiChatSession session)
    {
        AiChatSessionVo vo = new AiChatSessionVo();
        vo.setSessionId(session.getSessionId());
        vo.setTitle(session.getTitle());
        vo.setCreateTime(session.getCreateTime());
        vo.setUpdateTime(session.getUpdateTime());
        return vo;
    }

    private String truncateTitle(String title)
    {
        if (StringUtils.isBlank(title))
        {
            return null;
        }
        title = title.trim();
        if (title.length() > TITLE_MAX_LEN)
        {
            return title.substring(0, TITLE_MAX_LEN);
        }
        return title;
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
