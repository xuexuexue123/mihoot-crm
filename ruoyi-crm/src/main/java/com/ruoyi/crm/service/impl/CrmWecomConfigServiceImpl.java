package com.ruoyi.crm.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomConfigSaveRequest;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmWecomConfigService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.tenant.service.ISysWecomCorpTenantService;
import com.ruoyi.system.mapper.SysUserMapper;

@Service
public class CrmWecomConfigServiceImpl implements ICrmWecomConfigService
{
    private static final int MAX_WELCOME_TEXT_LEN = 2048;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private ISysWecomCorpTenantService sysWecomCorpTenantService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Map<String, Object> getConfigMasked()
    {
        CrmWecomConfig cfg = crmWecomConfigMapper.selectFirst();
        return buildConfigMap(cfg, sysUserMapper);
    }

    static Map<String, Object> buildConfigMap(CrmWecomConfig cfg)
    {
        return buildConfigMap(cfg, null);
    }

    static Map<String, Object> buildConfigMap(CrmWecomConfig cfg, SysUserMapper sysUserMapper)
    {
        Map<String, Object> m = new HashMap<>();
        if (cfg == null)
        {
            m.put("connected", false);
            m.put("secretConfigured", false);
            m.put("corpId", null);
            m.put("agentId", null);
            m.put("hasCallbackToken", false);
            m.put("hasEncodingAesKey", false);
            m.put("tokenConfigured", false);
            m.put("encodingAesKeyConfigured", false);
            m.put("lastError", null);
            m.put("callbackPathHint", null);
            m.put("callbackAliasPathHint", null);
            m.put("welcomeEnabled", false);
            m.put("welcomeText", null);
            m.put("welcomeConfigured", false);
            m.put("defaultLeadUserId", null);
            m.put("defaultLeadDeptId", null);
            m.put("defaultLeadOwnerName", null);
            m.put("defaultLeadOwnerConfigured", false);
            m.put("updatedAt", null);
            return m;
        }
        boolean hasToken = cfg.getCallbackToken() != null && !cfg.getCallbackToken().isEmpty();
        boolean hasAes = cfg.getEncodingAesKey() != null && !cfg.getEncodingAesKey().isEmpty();
        boolean welcomeEnabled = Boolean.TRUE.equals(cfg.getWelcomeEnabled());
        String welcomeText = cfg.getWelcomeText();
        boolean welcomeConfigured = welcomeEnabled && welcomeText != null && !welcomeText.trim().isEmpty();

        m.put("corpId", cfg.getCorpId());
        m.put("agentId", cfg.getAgentId());
        m.put("connected", Boolean.TRUE.equals(cfg.getConnected()));
        m.put("secretConfigured", cfg.getAgentSecretEnc() != null && !cfg.getAgentSecretEnc().isEmpty());
        m.put("hasCallbackToken", hasToken);
        m.put("hasEncodingAesKey", hasAes);
        m.put("tokenConfigured", hasToken);
        m.put("encodingAesKeyConfigured", hasAes);
        m.put("lastError", cfg.getLastError());
        m.put("callbackPathHint", "/crm/wecom/callback/" + cfg.getCorpId());
        m.put("callbackAliasPathHint", "/wecom/callback/" + cfg.getCorpId());
        m.put("welcomeEnabled", welcomeEnabled);
        m.put("welcomeText", welcomeText);
        m.put("welcomeConfigured", welcomeConfigured);
        Long defaultLeadUserId = cfg.getDefaultLeadUserId();
        Long defaultLeadDeptId = cfg.getDefaultLeadDeptId();
        m.put("defaultLeadUserId", defaultLeadUserId);
        m.put("defaultLeadDeptId", defaultLeadDeptId);
        m.put("defaultLeadOwnerConfigured", defaultLeadUserId != null);
        String defaultLeadOwnerName = null;
        if (defaultLeadUserId != null && sysUserMapper != null)
        {
            SysUser defaultUser = sysUserMapper.selectUserById(defaultLeadUserId);
            if (defaultUser != null)
            {
                defaultLeadOwnerName = pickDisplayName(defaultUser);
            }
        }
        m.put("defaultLeadOwnerName", defaultLeadOwnerName);
        if (cfg.getUpdateTime() != null)
        {
            m.put("updatedAt", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, cfg.getUpdateTime()));
        }
        else
        {
            m.put("updatedAt", null);
        }
        return m;
    }

    @Override
    public void saveOrUpdate(WecomConfigSaveRequest body, String tenantId)
    {
        if (body.getCorpId() == null || body.getCorpId().isEmpty() || body.getAgentId() == null || body.getAgentId().isEmpty())
        {
            throw new ServiceException("corpId 与 agentId 不能为空");
        }
        CrmWecomConfig existing = crmWecomConfigMapper.selectFirst();
        CrmWecomConfig row = existing != null ? existing : new CrmWecomConfig();
        row.setCorpId(body.getCorpId().trim());
        row.setAgentId(body.getAgentId().trim());
        if (body.getToken() != null)
        {
            row.setCallbackToken(body.getToken().trim());
        }
        if (body.getEncodingAesKey() != null)
        {
            row.setEncodingAesKey(body.getEncodingAesKey().trim());
        }

        applyWelcomeFields(body, existing, row);
        applyDefaultLeadOwnerFields(body, existing, row);

        boolean needSecret = existing == null || body.getAgentSecret() != null && !body.getAgentSecret().isEmpty();
        if (existing == null && (body.getAgentSecret() == null || body.getAgentSecret().isEmpty()))
        {
            throw new ServiceException("首次保存必须提供 agentSecret");
        }
        try
        {
            if (needSecret && body.getAgentSecret() != null && !body.getAgentSecret().isEmpty())
            {
                row.setAgentSecretEnc(AESUtil.encrypt(body.getAgentSecret().trim()));
            }
            else if (existing != null)
            {
                row.setAgentSecretEnc(existing.getAgentSecretEnc());
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("Secret 加密失败");
        }

        row.setUpdateTime(DateUtils.getNowDate());
        row.setUpdateBy(SecurityUtils.getUsername());

        try
        {
            wecomAccessTokenService.invalidate(existing != null ? existing.getCorpId() : null);
            wecomAccessTokenService.invalidate(row.getCorpId());
            String tokenVal = wecomAccessTokenService.getAgentAccessToken(row);
            if (tokenVal != null && !tokenVal.isEmpty())
            {
                row.setConnected(true);
                row.setLastError(null);
            }
        }
        catch (Exception e)
        {
            row.setConnected(false);
            row.setLastError(e.getMessage() != null ? e.getMessage().substring(0, Math.min(500, e.getMessage().length())) : "gettoken failed");
        }

        if (existing == null)
        {
            row.setCreateTime(DateUtils.getNowDate());
            row.setCreateBy(SecurityUtils.getUsername());
            crmWecomConfigMapper.insert(row);
        }
        else
        {
            row.setId(existing.getId());
            crmWecomConfigMapper.update(row);
        }

        sysWecomCorpTenantService.replaceMappingForTenant(tenantId, row.getCorpId());
    }

    private static void applyWelcomeFields(WecomConfigSaveRequest body, CrmWecomConfig existing, CrmWecomConfig row)
    {
        if (body.getWelcomeEnabled() != null)
        {
            row.setWelcomeEnabled(body.getWelcomeEnabled());
        }
        else if (existing != null)
        {
            row.setWelcomeEnabled(existing.getWelcomeEnabled());
        }
        else
        {
            row.setWelcomeEnabled(false);
        }

        if (body.getWelcomeText() != null)
        {
            String text = body.getWelcomeText().trim();
            if (text.length() > MAX_WELCOME_TEXT_LEN)
            {
                throw new ServiceException("欢迎语长度不能超过 2048 字符");
            }
            row.setWelcomeText(text.isEmpty() ? null : text);
        }
        else if (existing != null)
        {
            row.setWelcomeText(existing.getWelcomeText());
        }

        if (Boolean.TRUE.equals(row.getWelcomeEnabled()))
        {
            String effective = row.getWelcomeText();
            if (effective == null || effective.trim().isEmpty())
            {
                throw new ServiceException("启用欢迎语时必须填写默认文案");
            }
        }
    }

    private void applyDefaultLeadOwnerFields(WecomConfigSaveRequest body, CrmWecomConfig existing, CrmWecomConfig row)
    {
        if (Boolean.TRUE.equals(body.getClearDefaultLeadOwner()))
        {
            row.setDefaultLeadUserId(null);
            row.setDefaultLeadDeptId(null);
            row.setDefaultLeadOwnerDirty(true);
            return;
        }
        boolean userIdProvided = body.getDefaultLeadUserId() != null;
        boolean deptIdProvided = body.getDefaultLeadDeptId() != null;
        if (!userIdProvided && !deptIdProvided)
        {
            if (existing != null)
            {
                row.setDefaultLeadUserId(existing.getDefaultLeadUserId());
                row.setDefaultLeadDeptId(existing.getDefaultLeadDeptId());
            }
            return;
        }
        Long userId = userIdProvided ? body.getDefaultLeadUserId() : existing != null ? existing.getDefaultLeadUserId() : null;
        if (userId == null)
        {
            throw new ServiceException("设置默认线索部门前须先指定 defaultLeadUserId");
        }
        SysUser user = sysUserMapper.selectUserById(userId);
        if (user == null || !"0".equals(user.getDelFlag()))
        {
            throw new ServiceException("默认线索负责人不存在或已停用");
        }
        row.setDefaultLeadUserId(userId);
        if (deptIdProvided)
        {
            row.setDefaultLeadDeptId(body.getDefaultLeadDeptId());
        }
        else if (existing != null && existing.getDefaultLeadDeptId() != null)
        {
            row.setDefaultLeadDeptId(existing.getDefaultLeadDeptId());
        }
        else
        {
            row.setDefaultLeadDeptId(null);
        }
        row.setDefaultLeadOwnerDirty(true);
    }

    private static String pickDisplayName(SysUser user)
    {
        if (user.getNickName() != null && !user.getNickName().isEmpty())
        {
            return user.getNickName();
        }
        return user.getUserName();
    }
}
