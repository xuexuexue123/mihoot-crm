package com.ruoyi.web.controller.crm;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.common.utils.HmacUtil;
import com.ruoyi.crm.domain.GmailTokenExchangeRequest;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmOauthConnections;
import com.ruoyi.crm.service.ICrmOauthConnectionsService;
import com.ruoyi.crm.support.CrmMailUserScopeSupport;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.client.RestTemplate;

/**
 * OAuth2Controller
 * 
 * @author luohd
 * @date 2025-05-14
 */
@RestController
@RequestMapping("/crm/oauthconnections")
public class CrmOauthConnectionsController extends BaseController
{
    @Autowired
    private ICrmOauthConnectionsService crmOauthConnectionsService;

    @PostMapping("/gmail/exchange")
    public AjaxResult exchangeGmailToken(@RequestBody GmailTokenExchangeRequest request) {
        try {
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            // 构建 POST 参数
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", request.getCode());
            params.add("client_id", "81asdfasdfom");
            params.add("client_secret", "GOasdfasdfN5");
            params.add("redirect_uri", request.getRedirectUri());
            params.add("grant_type", "authorization_code");

            // 发起请求
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body == null || body.get("access_token") == null) {
                return AjaxResult.error("Token exchange failed.");
            }

            // 获取 access_token 并查询用户信息
            String accessToken = (String) body.get("access_token");

            // 查询用户 Gmail 账户信息
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> userInfoReq = new HttpEntity<>(authHeaders);
            ResponseEntity<Map> userInfoRes = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    userInfoReq,
                    Map.class
            );

            Map<String, Object> userInfo = userInfoRes.getBody();
            String externalUserId = userInfo != null ? (String) userInfo.get("sub") : null;
            String email = userInfo != null ? (String) userInfo.get("email") : null;

            // 绑定到当前登录用户，忽略前端传入的 userId
            String userId = CrmMailUserScopeSupport.currentUserId();

            // 构建连接对象
            CrmOauthConnections connection = new CrmOauthConnections();
            connection.setUserId(userId);
            connection.setProvider("Gmail");
            connection.setExternalId(externalUserId != null ? externalUserId : email);
            connection.setExternalName(email);
            connection.setAccessToken(accessToken);
            connection.setRefreshToken((String) body.get("refresh_token"));
            connection.setScope((String) body.get("scope"));
            connection.setTokenType((String) body.get("token_type"));

            connection.setStatus("connected");

            // 设置过期时间
            Integer expiresIn = (Integer) body.get("expires_in");
            if (expiresIn != null) {
                connection.setExpiresAt(new java.util.Date(System.currentTimeMillis() + expiresIn * 1000L));
            }

            // 保存或更新
            crmOauthConnectionsService.saveOAuthConnection(connection);
            return AjaxResult.success("Token exchanged and saved.", email);

        } catch (Exception ex) {
            return AjaxResult.error("Error exchanging token: " + ex.getMessage());
        }
    }

    @PostMapping("/outlook/exchange")
    public AjaxResult exchangeOutlookToken(@RequestBody GmailTokenExchangeRequest request) {
        try{
            String tokenEndpoint = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", "123123b24b-42f41");
            params.add("client_secret", "N8asdfasdfHwZa2A");
            params.add("code", request.getCode());
            params.add("redirect_uri", request.getRedirectUri());
            params.add("grant_type", "authorization_code");

            // 保持和前端授权跳转中的 scope 一致！
            params.add("scope", "openid offline_access email User.Read Mail.Send Mail.Read Mail.ReadWrite IMAP.AccessAsUser.All");



            // 发起请求
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, entity, Map.class);
            Map<String, Object> body = response.getBody();

            String accessToken = (String) body.get("access_token");
            String refreshToken = (String) body.get("refresh_token");
            String tokenType = (String) body.get("token_type");
            String scope = (String) body.get("scope");


            // Step 2: Use access token to fetch user info
            String meUrl = "https://graph.microsoft.com/v1.0/me";
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> userRequest = new HttpEntity<>(authHeaders);

            ResponseEntity<JsonNode> userResponse = restTemplate.exchange(meUrl, HttpMethod.GET, userRequest, JsonNode.class);
            JsonNode userJson = userResponse.getBody();

            String externalUserId = userJson.get("id").asText();  // Outlook user ID
            String email = userJson.has("mail") && !userJson.get("mail").isNull()
                    ? userJson.get("mail").asText()
                    : userJson.get("userPrincipalName").asText();

            // 保存到 CrmOauthConnections 表
            CrmOauthConnections conn = new CrmOauthConnections();
            conn.setUserId(CrmMailUserScopeSupport.currentUserId());
            conn.setProvider("Outlook");
            conn.setAccessToken(accessToken);
            conn.setRefreshToken(refreshToken);
            conn.setScope(scope);
            conn.setTokenType(tokenType);

            conn.setExternalId(externalUserId != null ? externalUserId : email);
            conn.setExternalName(email);

            // 设置过期时间
            Integer expiresIn = (Integer) body.get("expires_in");
            if (expiresIn != null) {
                conn.setExpiresAt(new java.util.Date(System.currentTimeMillis() + expiresIn * 1000L));
            }

            conn.setScope((String) body.get("scope"));
            conn.setStatus("connected");
            crmOauthConnectionsService.saveOAuthConnection(conn);

            return AjaxResult.success("Connected to Outlook", email);
        } catch (Exception ex) {
            return AjaxResult.error("Error exchanging token: " + ex.getMessage());
        }
    }

    /**
     * 获取合法的AccessToken
     */
    @PreAuthorize("@ss.hasPermi('crm:oauthconnections:list')")
    @GetMapping(value = "/validtoken/{userId}/{provider}/{externalName}")
    public AjaxResult validtoken(@PathVariable("userId") String userId,
                                 @PathVariable("provider") String provider,
                                 @PathVariable("externalName") String externalName)
    {
        userId = CrmMailUserScopeSupport.applyCurrentUserId(userId);
        return success(crmOauthConnectionsService.resolveValidAccessToken(userId, provider, externalName));
    }

    /**
     * 查询OAuth2列表
     */
    @PreAuthorize("@ss.hasPermi('crm:oauthconnections:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmOauthConnections crmOauthConnections)
    {
        crmOauthConnections.setUserId(CrmMailUserScopeSupport.applyCurrentUserId(crmOauthConnections.getUserId()));
        startPage();
        List<CrmOauthConnections> list = crmOauthConnectionsService.selectCrmOauthConnectionsList(crmOauthConnections);
        return getDataTable(list);
    }

    /**
     * 获取OAuth2详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:oauthconnections:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        CrmOauthConnections connection = crmOauthConnectionsService.selectCrmOauthConnectionsById(id);
        CrmMailUserScopeSupport.assertOwnedByCurrentUser(connection);
        return success(connection);
    }

    /**
     * 获取OAuth2详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:oauthconnections:query')")
    @GetMapping(value = "/getconnection/{userId}/{provider}")
    public AjaxResult getConnection(@PathVariable("userId") String userId, @PathVariable("provider") String provider)
    {
        userId = CrmMailUserScopeSupport.applyCurrentUserId(userId);
        Map<String, Object> connParams = new HashMap<>();
        connParams.put("userId", userId);
        connParams.put("provider", provider);

        CrmOauthConnections conn = crmOauthConnectionsService.selectCrmOauthConnectionsByUserId(connParams);
        CrmMailUserScopeSupport.assertOwnedByCurrentUser(conn);

        return success(conn);
    }
    /**
     * 新增OAuth2
     */
    @PreAuthorize("@ss.hasPermi('crm:oauthconnections:add')")
    @Log(title = "OAuth2", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmOauthConnections crmOauthConnections)
    {
        crmOauthConnections.setUserId(CrmMailUserScopeSupport.currentUserId());
        int rows = crmOauthConnectionsService.insertCrmOauthConnections(crmOauthConnections);
        if (rows > 0 && "pending".equals(crmOauthConnections.getStatus()))
        {
            String provider = crmOauthConnections.getProvider();
            if (provider != null
                    && (provider.equalsIgnoreCase("Gmail") || provider.equalsIgnoreCase("Outlook")))
            {
                return AjaxResult.success("账号已创建，请继续完成 OAuth 授权后再同步邮件");
            }
        }
        return toAjax(rows);
    }

    /**
     * 修改OAuth2
     */
    @PreAuthorize("@ss.hasPermi('crm:oauthconnections:edit')")
    @Log(title = "OAuth2", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmOauthConnections crmOauthConnections)
    {
        CrmOauthConnections existing = crmOauthConnectionsService.selectCrmOauthConnectionsById(
                crmOauthConnections.getId() != null ? crmOauthConnections.getId().toString() : null);
        CrmMailUserScopeSupport.assertOwnedByCurrentUser(existing);
        crmOauthConnections.setUserId(CrmMailUserScopeSupport.currentUserId());
        return toAjax(crmOauthConnectionsService.updateCrmOauthConnections(crmOauthConnections));
    }


    /**
     * 导出OAuth2列表
     */
    @PreAuthorize("@ss.hasPermi('crm:oauthconnections:export')")
    @Log(title = "OAuth2", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmOauthConnections crmOauthConnections)
    {
        crmOauthConnections.setUserId(CrmMailUserScopeSupport.applyCurrentUserId(crmOauthConnections.getUserId()));
        List<CrmOauthConnections> list = crmOauthConnectionsService.selectCrmOauthConnectionsList(crmOauthConnections);
        ExcelUtil<CrmOauthConnections> util = new ExcelUtil<CrmOauthConnections>(CrmOauthConnections.class);
        util.exportExcel(response, list, "OAuth2数据");
    }
    /**
     * 删除OAuth2
     */
    @PreAuthorize("@ss.hasPermi('crm:oauthconnections:remove')")
    @Log(title = "OAuth2", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        for (String id : ids)
        {
            CrmMailUserScopeSupport.assertOwnedByCurrentUser(crmOauthConnectionsService.selectCrmOauthConnectionsById(id));
        }
        return toAjax(crmOauthConnectionsService.deleteCrmOauthConnectionsByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('crm:oauthconnections:remove')")
    @DeleteMapping("/disconnect/{userId}/{provider}")
    public AjaxResult disconnect(@PathVariable String userId, @PathVariable String provider) {
        userId = CrmMailUserScopeSupport.applyCurrentUserId(userId);
        Map<String, Object> connParams = new HashMap<>();
        connParams.put("userId", userId);
        connParams.put("provider", provider);

        CrmOauthConnections conn = crmOauthConnectionsService.selectCrmOauthConnectionsByUserId(connParams);

        if (conn != null) {

            String revokeEndpoint = "https://oauth2.googleapis.com/revoke?token=" + conn.getAccessToken();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity(revokeEndpoint, null, String.class);
            crmOauthConnectionsService.deleteCrmOauthConnectionsById(conn.getId().toString());
        }
        return AjaxResult.success("Disconnected");
    }
}
