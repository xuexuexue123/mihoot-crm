package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.CrmEmailMessages;
import com.ruoyi.crm.domain.CrmOauthConnections;

/**
 * OAuth2Service接口
 * 
 * @author luohd
 * @date 2025-05-14
 */
public interface ICrmOauthConnectionsService 
{
    /**
     * 查询OAuth2
     * 
     * @param id OAuth2主键
     * @return OAuth2
     */
    public CrmOauthConnections selectCrmOauthConnectionsById(String id);


    /**
     * 查询OAuth2
     *
     * @param id OAuth2主键
     * @return OAuth2
     */
    public CrmOauthConnections selectCrmOauthConnectionsByUserId(Map<String, Object> params);

    /**
     * upsertConnection
     */
    public int upsertConnection(CrmOauthConnections crmOauthConnections);

    /**
     * 查询OAuth2列表
     * 
     * @param crmOauthConnections OAuth2
     * @return OAuth2集合
     */
    public List<CrmOauthConnections> selectCrmOauthConnectionsList(CrmOauthConnections crmOauthConnections);

    /**
     * 查询账号是否存在
     *
     * @param externalName 邮件主键
     * @return 邮件
     */
    public CrmEmailMessages existsByExternalName(String externalName);

    /**
     * 新增OAuth2
     * 
     * @param crmOauthConnections OAuth2
     * @return 结果
     */
    public int insertCrmOauthConnections(CrmOauthConnections crmOauthConnections);

    /**
     * 修改OAuth2
     * 
     * @param crmOauthConnections OAuth2
     * @return 结果
     */
    public int updateCrmOauthConnections(CrmOauthConnections crmOauthConnections);

    /**
     * 修改OAuth2 by email address
     *
     * @param crmOauthConnections OAuth2
     * @return 结果
     */
    public int updateCrmOauthConnectionsByExternalName(CrmOauthConnections crmOauthConnections);

    /**
     * 批量删除OAuth2
     * 
     * @param ids 需要删除的OAuth2主键集合
     * @return 结果
     */
    public int deleteCrmOauthConnectionsByIds(String[] ids);

    /**
     * 删除OAuth2信息
     * 
     * @param id OAuth2主键
     * @return 结果
     */
    public int deleteCrmOauthConnectionsById(String id);

    /**
     * Resolve a valid OAuth access token for Gmail/Outlook sync and API calls.
     */
    public String resolveValidAccessToken(String userId, String provider, String externalName);

    /**
     * Upsert an OAuth connection and remove stale manual entries for the same provider.
     */
    public void saveOAuthConnection(CrmOauthConnections connection);
}
