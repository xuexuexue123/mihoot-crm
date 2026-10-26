package com.ruoyi.crm.service;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

public interface ICrmPublicTenantWebsiteAssetService
{
    void streamActiveAsset(String slug, UUID assetId, HttpServletResponse response) throws IOException;
}
