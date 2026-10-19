package com.ruoyi.crm.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEmailMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.crm.mapper.CrmOauthConnectionsMapper;
import com.ruoyi.crm.domain.CrmOauthConnections;
import com.ruoyi.crm.service.ICrmOauthConnectionsService;

/**
 * OAuth2Service业务层处理
 * 
 * @author luohd
 * @date 2025-05-14
 */
@Service
public class CrmOauthConnectionsServiceImpl implements ICrmOauthConnectionsService 
{
    private static final String GMAIL_CLIENT_ID = "810266755269-l39gkus2tbrpsst1vht689bu7h630g3e.apps.googleusercontent.com";
    private static final String GMAIL_CLIENT_SECRET = "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";
    private static final String OUTLOOK_CLIENT_ID = "c0c69045-54ee-4bf0-b24b-4e60c4f12f41";
    private static final String OUTLOOK_CLIENT_SECRET = "8J58Q~gA7zavXpRbN8NARfWX-gSo~H2lZ8HwZa2A";

    @Autowired
    private CrmOauthConnectionsMapper crmOauthConnectionsMapper;

    /**
     * 查询OAuth2
     * 
     * @param id OAuth2主键
     * @return OAuth2
     */
    @Override
    public CrmOauthConnections selectCrmOauthConnectionsById(String id)
    {
        return crmOauthConnectionsMapper.selectCrmOauthConnectionsById(id);
    }

    /**
     *
     * @param params
     * @return
     */
    public CrmOauthConnections selectCrmOauthConnectionsByUserId(Map<String, Object> params ) {
        return crmOauthConnectionsMapper.selectCrmOauthConnectionsByUserId(params);
    }
    /**
     * upsertConnection
     */
    public int upsertConnection(CrmOauthConnections crmOauthConnections) {
        Long id = crmOauthConnections.getId();

        if (id != null && id != 0) {
            return crmOauthConnectionsMapper.updateCrmOauthConnections(crmOauthConnections);
        } else {
            return crmOauthConnectionsMapper.insertCrmOauthConnections(crmOauthConnections);
        }
    }


    /**
     * 查询OAuth2列表
     * 
     * @param crmOauthConnections OAuth2
     * @return OAuth2
     */
    @Override
    public List<CrmOauthConnections> selectCrmOauthConnectionsList(CrmOauthConnections crmOauthConnections)
    {
        return crmOauthConnectionsMapper.selectCrmOauthConnectionsList(crmOauthConnections);
    }

    /**
     * 查询账号是否存在
     *
     * @param externalName 邮件主键
     * @return 邮件
     */
    @Override
    public CrmEmailMessages existsByExternalName(String externalName){
        return crmOauthConnectionsMapper.existsByExternalName(externalName);
    }
    /**
     * 新增OAuth2
     * 
     * @param crmOauthConnections OAuth2
     * @return 结果
     */
    @Override
    public int insertCrmOauthConnections(CrmOauthConnections crmOauthConnections)
    {
        normalizeOAuthConnectionStatus(crmOauthConnections);
        return crmOauthConnectionsMapper.insertCrmOauthConnections(crmOauthConnections);
    }

    private static void normalizeOAuthConnectionStatus(CrmOauthConnections conn)
    {
        String provider = conn.getProvider();
        if (provider == null)
        {
            return;
        }
        if (!"Gmail".equalsIgnoreCase(provider) && !"Outlook".equalsIgnoreCase(provider))
        {
            return;
        }

        boolean hasRefresh = StringUtils.isNotEmpty(conn.getRefreshToken());
        String token = conn.getAccessToken();
        boolean looksLikeOAuth = StringUtils.isNotEmpty(token)
                && (token.startsWith("ya29.")
                || ("Outlook".equalsIgnoreCase(provider) && token.length() >= 50));

        if (!hasRefresh && !looksLikeOAuth)
        {
            conn.setStatus("pending");
        }
        else if (StringUtils.isEmpty(conn.getStatus()))
        {
            conn.setStatus("connected");
        }
    }

    /**
     * 修改OAuth2
     * 
     * @param crmOauthConnections OAuth2
     * @return 结果
     */
    @Override
    public int updateCrmOauthConnections(CrmOauthConnections crmOauthConnections)
    {
        return crmOauthConnectionsMapper.updateCrmOauthConnections(crmOauthConnections);
    }


    /**
     * 修改OAuth2 by email address
     *
     * @param crmOauthConnections OAuth2
     * @return 结果
     */
    @Override
    public int updateCrmOauthConnectionsByExternalName(CrmOauthConnections crmOauthConnections){
        return crmOauthConnectionsMapper.updateCrmOauthConnectionsByExternalName(crmOauthConnections);
    }


    /**
     * 批量删除OAuth2
     * 
     * @param ids 需要删除的OAuth2主键
     * @return 结果
     */
    @Override
    public int deleteCrmOauthConnectionsByIds(String[] ids)
    {
        return crmOauthConnectionsMapper.deleteCrmOauthConnectionsByIds(ids);
    }

    /**
     * 删除OAuth2信息
     * 
     * @param id OAuth2主键
     * @return 结果
     */
    @Override
    public int deleteCrmOauthConnectionsById(String id)
    {
        return crmOauthConnectionsMapper.deleteCrmOauthConnectionsById(id);
    }

    @Override
    public String resolveValidAccessToken(String userId, String provider, String externalName)
    {
        Map<String, Object> connParams = new HashMap<>();
        connParams.put("userId", userId);
        connParams.put("provider", provider);
        connParams.put("externalName", externalName);

        CrmOauthConnections conn = crmOauthConnectionsMapper.selectCrmOauthConnectionsByUserId(connParams);
        if (conn == null)
        {
            throw new ServiceException("未找到 OAuth 连接，请先完成授权绑定");
        }

        Date expiresAtDate = conn.getExpiresAt();
        if (expiresAtDate != null)
        {
            LocalDateTime expiresAt = expiresAtDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            if (expiresAt.isAfter(LocalDateTime.now().plusSeconds(30)))
            {
                assertOAuthAccessToken(conn, provider);
                return conn.getAccessToken();
            }
        }
        else if (conn.getRefreshToken() == null || conn.getRefreshToken().isEmpty())
        {
            assertOAuthAccessToken(conn, provider);
            return conn.getAccessToken();
        }

        if ("Gmail".equalsIgnoreCase(provider))
        {
            return refreshGoogleAccessToken(conn);
        }
        if ("Outlook".equalsIgnoreCase(provider))
        {
            return refreshOutlookAccessToken(conn);
        }
        throw new ServiceException("不支持的邮箱提供方: " + provider);
    }

    @Override
    public void saveOAuthConnection(CrmOauthConnections connection)
    {
        Map<String, Object> lookupParams = new HashMap<>();
        lookupParams.put("userId", connection.getUserId());
        lookupParams.put("provider", connection.getProvider());
        lookupParams.put("externalId", connection.getExternalId());

        CrmOauthConnections existing = crmOauthConnectionsMapper.selectCrmOauthConnectionsByUserProviderExternalId(lookupParams);
        if (existing != null)
        {
            connection.setId(existing.getId());
            crmOauthConnectionsMapper.updateCrmOauthConnections(connection);
        }
        else
        {
            crmOauthConnectionsMapper.insertCrmOauthConnections(connection);
        }

        Map<String, Object> cleanupParams = new HashMap<>();
        cleanupParams.put("userId", connection.getUserId());
        cleanupParams.put("provider", connection.getProvider());
        cleanupParams.put("externalName", connection.getExternalName());
        crmOauthConnectionsMapper.deleteManualOauthConnections(cleanupParams);
    }

    private static void assertOAuthAccessToken(CrmOauthConnections conn, String provider)
    {
        String token = conn.getAccessToken();
        if (StringUtils.isEmpty(token))
        {
            throw new ServiceException("OAuth 连接缺少 access_token，请重新授权");
        }

        boolean hasRefresh = StringUtils.isNotEmpty(conn.getRefreshToken());
        if ("Gmail".equalsIgnoreCase(provider) && !hasRefresh && !token.startsWith("ya29."))
        {
            throw new ServiceException("邮箱 " + conn.getExternalName()
                    + " 未通过 Gmail OAuth 授权，请删除该账号后重新连接，或选择已授权的邮箱地址");
        }
        if ("Outlook".equalsIgnoreCase(provider) && !hasRefresh && token.length() < 50)
        {
            throw new ServiceException("邮箱 " + conn.getExternalName()
                    + " 未通过 Outlook OAuth 授权，请删除该账号后重新连接");
        }
    }

    private String refreshGoogleAccessToken(CrmOauthConnections conn)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", GMAIL_CLIENT_ID);
        params.add("client_secret", GMAIL_CLIENT_SECRET);
        params.add("refresh_token", conn.getRefreshToken());
        params.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        try
        {
            ResponseEntity<Map> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful())
            {
                Map body = response.getBody();
                String newAccessToken = (String) body.get("access_token");
                Integer expiresIn = (Integer) body.get("expires_in");
                conn.setAccessToken(newAccessToken);
                if (expiresIn != null)
                {
                    conn.setExpiresAt(new Date(System.currentTimeMillis() + expiresIn * 1000L));
                }
                crmOauthConnectionsMapper.updateCrmOauthConnections(conn);
                return newAccessToken;
            }
            throw new ServiceException("Failed to refresh access token");
        }
        catch (HttpStatusCodeException e)
        {
            throw mapOAuthTokenHttpError(e, "Gmail");
        }
        catch (RestClientException e)
        {
            throw new ServiceException("刷新 Gmail 访问令牌失败，请检查网络后重试或重新授权");
        }
    }

    private String refreshOutlookAccessToken(CrmOauthConnections conn)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", OUTLOOK_CLIENT_ID);
        params.add("client_secret", OUTLOOK_CLIENT_SECRET);
        params.add("refresh_token", conn.getRefreshToken());
        params.add("grant_type", "refresh_token");
        params.add("scope", "https://graph.microsoft.com/.default offline_access");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        String tokenUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/token";

        try
        {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK)
            {
                Map body = response.getBody();
                String newAccessToken = (String) body.get("access_token");
                Integer expiresIn = (Integer) body.get("expires_in");
                conn.setAccessToken(newAccessToken);
                if (expiresIn != null)
                {
                    conn.setExpiresAt(new Date(System.currentTimeMillis() + expiresIn * 1000L));
                }
                crmOauthConnectionsMapper.updateCrmOauthConnections(conn);
                return newAccessToken;
            }
            throw new ServiceException("Failed to refresh access token");
        }
        catch (HttpStatusCodeException e)
        {
            throw mapOAuthTokenHttpError(e, "Outlook");
        }
        catch (RestClientException e)
        {
            throw new ServiceException("刷新 Outlook 访问令牌失败，请检查网络后重试或重新授权");
        }
    }

    private static ServiceException mapOAuthTokenHttpError(HttpStatusCodeException e, String provider)
    {
        String body = e.getResponseBodyAsString();
        if (e.getRawStatusCode() == 400 && body != null && body.contains("invalid_grant"))
        {
            return new ServiceException("授权已失效或 refresh_token 不可用，请重新连接 " + provider);
        }
        return new ServiceException("无法刷新 " + provider + " 访问令牌，请重新授权");
    }
}
