package com.ruoyi.web.controller.publicapi;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.crm.service.ICrmPublicTenantWebsiteAssetService;

@RestController
@RequestMapping("/api/public/tenant-sites/{slug}/assets")
public class PublicTenantSiteAssetController
{
    @Autowired
    private ICrmPublicTenantWebsiteAssetService publicTenantWebsiteAssetService;

    @RateLimiter(time = 60, count = 120, limitType = LimitType.IP)
    @GetMapping("/{assetId}/file")
    public void streamFile(
            @PathVariable String slug,
            @PathVariable UUID assetId,
            HttpServletResponse response) throws IOException
    {
        publicTenantWebsiteAssetService.streamActiveAsset(slug, assetId, response);
    }
}
