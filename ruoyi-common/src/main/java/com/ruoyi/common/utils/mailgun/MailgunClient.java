package com.ruoyi.common.utils.mailgun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ruoyi.common.utils.StringUtils;

/**
 * Mailgun REST 发信客户端（租户级 domain/key/region）
 */
public final class MailgunClient
{
    private static final String US_BASE = "https://api.mailgun.net";
    private static final String EU_BASE = "https://api.eu.mailgun.net";

    private MailgunClient()
    {
    }

    public static JsonNode sendHtml(String region, String domain, String apiKey,
            String from, String to, String subject, String html) throws UnirestException
    {
        return postMessage(region, domain, apiKey, from, to, subject, html, null);
    }

    public static JsonNode sendText(String region, String domain, String apiKey,
            String from, String to, String subject, String text) throws UnirestException
    {
        return postMessage(region, domain, apiKey, from, to, subject, null, text);
    }

    public static String formatFrom(String fromName, String fromEmail)
    {
        String email = fromEmail != null ? fromEmail.trim() : "";
        if (StringUtils.isBlank(email))
        {
            return email;
        }
        if (StringUtils.isNotBlank(fromName))
        {
            return fromName.trim() + " <" + email + ">";
        }
        return email;
    }

    public static String summarizeError(UnirestException ex)
    {
        if (ex == null)
        {
            return "Mailgun request failed";
        }
        String msg = ex.getMessage();
        if (StringUtils.isBlank(msg))
        {
            return "Mailgun request failed";
        }
        return msg.length() <= 500 ? msg : msg.substring(0, 500);
    }

    public static String summarizeError(HttpResponse<JsonNode> response)
    {
        if (response == null)
        {
            return "Mailgun returned empty response";
        }
        try
        {
            JsonNode body = response.getBody();
            if (body != null && body.getObject().has("message"))
            {
                String text = body.getObject().getString("message");
                if (StringUtils.isNotBlank(text))
                {
                    return text.length() <= 500 ? text : text.substring(0, 500);
                }
            }
        }
        catch (Exception ignored)
        {
            // fall through to HTTP status summary
        }
        return "Mailgun HTTP " + response.getStatus();
    }

    private static JsonNode postMessage(String region, String domain, String apiKey,
            String from, String to, String subject, String html, String text) throws UnirestException
    {
        String url = baseUrl(region) + "/v3/" + domain.trim() + "/messages";
        HttpResponse<JsonNode> response = Unirest.post(url)
                .basicAuth("api", apiKey)
                .field("from", from)
                .field("to", to)
                .field("subject", subject)
                .field(html != null ? "html" : "text", html != null ? html : text)
                .asJson();

        if (response.getStatus() < 200 || response.getStatus() >= 300)
        {
            throw new UnirestException(summarizeError(response));
        }
        return response.getBody();
    }

    static String baseUrl(String region)
    {
        if ("eu".equalsIgnoreCase(StringUtils.trim(region)))
        {
            return EU_BASE;
        }
        return US_BASE;
    }
}
