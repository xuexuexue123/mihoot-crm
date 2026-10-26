package com.ruoyi.tenant.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 租户公开子域名配置
 */
@Component
@ConfigurationProperties(prefix = "tenant.website")
public class TenantWebsiteProperties
{
    private static final Set<String> DEFAULT_RESERVED = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "www", "app", "api", "admin", "mail", "cdn", "static", "mcp")));

    /** 禁止注册的 reserved slug，逗号分隔 */
    private String reservedSlugs = "www,app,api,admin,mail,cdn,static,mcp";

    /** check-tenant-alias previewUrl 基域 */
    private String previewBaseDomain = "mihoot.com";

    /** Preview token HMAC 密钥 */
    private String previewTokenSecret = "change-me-in-production";

    /** Preview token TTL（分钟） */
    private int previewTokenTtlMinutes = 15;

    /** Schema JSON 最大字节数 */
    private int maxSchemaBytes = 1048576;

    /** 公开站基域（publicUrl）；为空时回退 previewBaseDomain */
    private String publicBaseDomain;

    /** 公开站协议 */
    private String publicBaseProtocol = "https";

    /** 素材库配置 */
    private Asset asset = new Asset();

    public static class Asset
    {
        /** 图片最大字节数，默认 10 MB */
        private long imageMaxBytes = 10485760L;

        /** 视频最大字节数，默认 50 MB */
        private long videoMaxBytes = 52428800L;

        public long getImageMaxBytes()
        {
            return imageMaxBytes;
        }

        public void setImageMaxBytes(long imageMaxBytes)
        {
            this.imageMaxBytes = imageMaxBytes;
        }

        public long getVideoMaxBytes()
        {
            return videoMaxBytes;
        }

        public void setVideoMaxBytes(long videoMaxBytes)
        {
            this.videoMaxBytes = videoMaxBytes;
        }
    }

    public String getEffectivePublicBaseDomain()
    {
        if (publicBaseDomain != null && !publicBaseDomain.trim().isEmpty())
        {
            return publicBaseDomain.trim();
        }
        return previewBaseDomain;
    }

    public String getPublicBaseDomain()
    {
        return publicBaseDomain;
    }

    public void setPublicBaseDomain(String publicBaseDomain)
    {
        this.publicBaseDomain = publicBaseDomain;
    }

    public String getPublicBaseProtocol()
    {
        return publicBaseProtocol;
    }

    public void setPublicBaseProtocol(String publicBaseProtocol)
    {
        this.publicBaseProtocol = publicBaseProtocol;
    }

    public Asset getAsset()
    {
        return asset;
    }

    public void setAsset(Asset asset)
    {
        this.asset = asset != null ? asset : new Asset();
    }

    /** 禁止创建的页面 path，逗号分隔 */
    private String reservedPagePaths = "/api,/sign-in,/sign-up,/register,/dashboard,/settings,/website,/mcp,/webhooks,/sites";

    public Set<String> getReservedSlugSet()
    {
        if (reservedSlugs == null || reservedSlugs.trim().isEmpty())
        {
            return DEFAULT_RESERVED;
        }
        Set<String> set = new HashSet<>();
        for (String part : reservedSlugs.split(","))
        {
            String slug = part.trim().toLowerCase();
            if (!slug.isEmpty())
            {
                set.add(slug);
            }
        }
        return set.isEmpty() ? DEFAULT_RESERVED : set;
    }

    public String getReservedSlugs()
    {
        return reservedSlugs;
    }

    public void setReservedSlugs(String reservedSlugs)
    {
        this.reservedSlugs = reservedSlugs;
    }

    public String getPreviewBaseDomain()
    {
        return previewBaseDomain;
    }

    public void setPreviewBaseDomain(String previewBaseDomain)
    {
        this.previewBaseDomain = previewBaseDomain;
    }

    public String getPreviewTokenSecret()
    {
        return previewTokenSecret;
    }

    public void setPreviewTokenSecret(String previewTokenSecret)
    {
        this.previewTokenSecret = previewTokenSecret;
    }

    public int getPreviewTokenTtlMinutes()
    {
        return previewTokenTtlMinutes;
    }

    public void setPreviewTokenTtlMinutes(int previewTokenTtlMinutes)
    {
        this.previewTokenTtlMinutes = previewTokenTtlMinutes;
    }

    public int getMaxSchemaBytes()
    {
        return maxSchemaBytes;
    }

    public void setMaxSchemaBytes(int maxSchemaBytes)
    {
        this.maxSchemaBytes = maxSchemaBytes;
    }

    public String getReservedPagePaths()
    {
        return reservedPagePaths;
    }

    public void setReservedPagePaths(String reservedPagePaths)
    {
        this.reservedPagePaths = reservedPagePaths;
    }

    public Set<String> getReservedPagePathSet()
    {
        if (reservedPagePaths == null || reservedPagePaths.trim().isEmpty())
        {
            return Collections.emptySet();
        }
        Set<String> set = new HashSet<>();
        for (String part : reservedPagePaths.split(","))
        {
            String path = part.trim();
            if (!path.isEmpty())
            {
                set.add(path.startsWith("/") ? path : "/" + path);
            }
        }
        return set;
    }
}
