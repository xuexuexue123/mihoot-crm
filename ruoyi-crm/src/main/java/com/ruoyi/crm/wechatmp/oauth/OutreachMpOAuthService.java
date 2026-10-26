package com.ruoyi.crm.wechatmp.oauth;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService.VerifiedCheckInPayload;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;

@Service
public class OutreachMpOAuthService
{
    private static final int STATE_TTL_SECONDS = 600;

    @Autowired
    private OutreachCheckInTokenService checkInTokenService;

    @Autowired
    private OutreachMpSessionSupport sessionSupport;

    @Autowired
    private WechatMpTencentApiClient wechatMpApiClient;

    @Autowired
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Value("${outreach.registration.public-app-url:}")
    private String publicAppUrl;

    public CrmWechatMpConfig resolveConnectedConfig()
    {
        List<CrmWechatMpConfig> configs = wechatMpConfigMapper.selectAll();
        if (configs == null || configs.isEmpty())
        {
            return null;
        }
        for (CrmWechatMpConfig cfg : configs)
        {
            if (Boolean.TRUE.equals(cfg.getConnected()) && StringUtils.isNotBlank(cfg.getAppId())
                    && StringUtils.isNotBlank(cfg.getSecretEnc()))
            {
                return cfg;
            }
        }
        return null;
    }

    public boolean isWeChatUserAgent(String userAgent)
    {
        return userAgent != null && userAgent.contains("MicroMessenger");
    }

    public String buildAuthorizeRedirect(String checkInToken, String redirectUri, HttpServletRequest request)
    {
        VerifiedCheckInPayload payload = checkInTokenService.verify(checkInToken);
        CrmWechatMpConfig cfg = resolveConnectedConfig();
        if (cfg == null)
        {
            throw new ServiceException("WeChat MP not configured", HttpStatus.ERROR);
        }
        validateRedirectUri(redirectUri);
        String callbackUrl = resolveOAuthCallbackUrl(request);
        String state = encodeState(checkInToken, redirectUri);
        return wechatMpApiClient.buildOAuthAuthorizeUrl(cfg.getAppId(), callbackUrl, state);
    }

    public VerifiedCheckInPayload verifyStatePayload(String state)
    {
        OAuthState oauthState = decodeState(state);
        return checkInTokenService.verify(oauthState.checkInToken);
    }

    public String handleCallback(String code, String state, HttpServletRequest request, HttpServletResponse response)
    {
        if (StringUtils.isBlank(code) || StringUtils.isBlank(state))
        {
            throw new ServiceException("Wrong request", HttpStatus.BAD_REQUEST);
        }
        OAuthState oauthState = decodeState(state);
        VerifiedCheckInPayload payload = checkInTokenService.verify(oauthState.checkInToken);
        CrmWechatMpConfig cfg = resolveConnectedConfig();
        if (cfg == null)
        {
            throw new ServiceException("WeChat MP not configured", HttpStatus.ERROR);
        }
        String secret;
        try
        {
            secret = AESUtil.decrypt(cfg.getSecretEnc());
        }
        catch (Exception ex)
        {
            throw new ServiceException("Secret decrypt failed", HttpStatus.ERROR);
        }
        JSONObject tokenResp = wechatMpApiClient.exchangeOAuthCode(cfg.getAppId(), secret, code);
        if (tokenResp == null || (tokenResp.containsKey("errcode") && tokenResp.getIntValue("errcode") != 0))
        {
            throw new ServiceException("WeChat OAuth failed", HttpStatus.FORBIDDEN);
        }
        String openId = tokenResp.getString("openid");
        if (StringUtils.isBlank(openId))
        {
            throw new ServiceException("WeChat OAuth failed", HttpStatus.FORBIDDEN);
        }
        OutreachMpSessionPayload session = new OutreachMpSessionPayload();
        session.setOpenId(openId);
        session.setAppId(cfg.getAppId());
        session.setTenantId(payload.getTenantId());
        session.setEventId(payload.getEventId());
        session.setExp(payload.getExp());
        writeSessionCookie(response, session);
        return oauthState.redirectUri;
    }

    public OutreachMpSessionPayload readSession(HttpServletRequest request, String tenantId, String eventId)
    {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
        {
            return null;
        }
        for (Cookie cookie : cookies)
        {
            if (OutreachMpSessionSupport.cookieName().equals(cookie.getName()))
            {
                OutreachMpSessionPayload payload = sessionSupport.decode(cookie.getValue());
                if (payload != null && payload.isValidFor(tenantId, eventId))
                {
                    return payload;
                }
                return null;
            }
        }
        return null;
    }

    public String buildOAuthAuthorizeUrl(String checkInToken, String redirectUri)
    {
        try
        {
            return "/outreach/wechat/oauth/authorize?checkInToken="
                    + URLEncoder.encode(checkInToken, "UTF-8")
                    + "&redirectUri=" + URLEncoder.encode(redirectUri, "UTF-8");
        }
        catch (Exception ex)
        {
            throw new IllegalStateException(ex);
        }
    }

    private void writeSessionCookie(HttpServletResponse response, OutreachMpSessionPayload session)
    {
        Cookie cookie = new Cookie(OutreachMpSessionSupport.cookieName(), sessionSupport.encode(session));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
    }

    private String resolveOAuthCallbackUrl(HttpServletRequest request)
    {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String scheme = StringUtils.isNotBlank(forwardedProto) ? forwardedProto : request.getScheme();
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        String host = StringUtils.isNotBlank(forwardedHost)
                ? forwardedHost
                : request.getServerName() + (request.getServerPort() == 80 || request.getServerPort() == 443
                        ? "" : ":" + request.getServerPort());
        return scheme + "://" + host + "/outreach/wechat/oauth/callback";
    }

    private void validateRedirectUri(String redirectUri)
    {
        if (StringUtils.isBlank(redirectUri))
        {
            throw new ServiceException("redirectUri required", HttpStatus.BAD_REQUEST);
        }
        String base = normalizePublicAppUrl(publicAppUrl);
        if (StringUtils.isBlank(base))
        {
            throw new ServiceException("public app url not configured", HttpStatus.ERROR);
        }
        try
        {
            URI uri = URI.create(redirectUri);
            URI baseUri = URI.create(base);
            if (!baseUri.getHost().equalsIgnoreCase(uri.getHost()))
            {
                throw new ServiceException("redirectUri not allowed", HttpStatus.FORBIDDEN);
            }
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("redirectUri invalid", HttpStatus.BAD_REQUEST);
        }
    }

    private String encodeState(String checkInToken, String redirectUri)
    {
        try
        {
            long exp = Instant.now().getEpochSecond() + STATE_TTL_SECONDS;
            String raw = exp + "\u001f" + checkInToken + "\u001f" + redirectUri;
            return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception ex)
        {
            throw new IllegalStateException(ex);
        }
    }

    private OAuthState decodeState(String state)
    {
        try
        {
            String raw = new String(Base64.getUrlDecoder().decode(state), StandardCharsets.UTF_8);
            String[] parts = raw.split("\u001f", 3);
            if (parts.length != 3)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            long exp = Long.parseLong(parts[0]);
            if (Instant.now().getEpochSecond() > exp)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            OAuthState oauthState = new OAuthState();
            oauthState.checkInToken = parts[1];
            oauthState.redirectUri = parts[2];
            return oauthState;
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
    }

    private static String normalizePublicAppUrl(String url)
    {
        if (StringUtils.isBlank(url))
        {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static final class OAuthState
    {
        private String checkInToken;
        private String redirectUri;
    }
}
