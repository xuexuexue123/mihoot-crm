package com.ruoyi.crm.service.bulkemail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.HmacUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;

/**
 * 注入 1x1 打开追踪像素
 */
@Component
public class BulkemailTrackingInjector
{
    @Value("${bulkemail.tracking.base-url:http://localhost:9090}")
    private String trackingBaseUrl;

    public String inject(String html, CrmCampaignContacts contact, CrmBulkemail campaign, String tenantId)
    {
        if (StringUtils.isBlank(html) || contact == null || campaign == null || StringUtils.isBlank(tenantId))
        {
            return html;
        }
        String email = contact.getEmail();
        String bulkId = campaign.getBulkId();
        String token = HmacUtil.generateTrackingToken(email, bulkId, tenantId);
        String pixelUrl = buildTrackUrl(email, bulkId, tenantId, token, false);
        String pixel = "<img src=\"" + pixelUrl + "\" width=\"1\" height=\"1\" alt=\"\" style=\"display:none\" />";
        if (html.contains("</body>"))
        {
            return html.replace("</body>", pixel + "</body>");
        }
        return html + pixel;
    }

    public String injectForTest(String html)
    {
        return html;
    }

    private String buildTrackUrl(String email, String bulkId, String tenantId, String token, boolean test)
    {
        try
        {
            String base = StringUtils.stripEnd(trackingBaseUrl, "/");
            StringBuilder url = new StringBuilder(base)
                    .append("/track?e=").append(encode(email))
                    .append("&c=").append(encode(bulkId))
                    .append("&t=").append(encode(tenantId))
                    .append("&k=").append(encode(token));
            if (test)
            {
                url.append("&test=1");
            }
            return url.toString();
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new IllegalStateException("Failed to encode tracking URL", ex);
        }
    }

    private static String encode(String value) throws UnsupportedEncodingException
    {
        return URLEncoder.encode(value != null ? value : "", StandardCharsets.UTF_8.name());
    }
}
