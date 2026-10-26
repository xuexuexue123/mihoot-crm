package com.ruoyi.crm.wecom;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;

/**
 * 调用企微 {@code externalcontact/get} 并解析客户 profile（含 unionid）。
 */
@Component
public class WecomExternalContactProfileResolver
{
    private static final Logger log = LoggerFactory.getLogger(WecomExternalContactProfileResolver.class);

    private static final int MAX_RAW_JSON = 8000;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    /**
     * 拉取外部联系人详情；失败或配置缺失时返回 empty。
     */
    public Optional<WecomExternalContactProfile> fetch(String externalUserid)
    {
        if (!StringUtils.hasText(externalUserid))
        {
            return Optional.empty();
        }
        CrmWecomConfig cfg = crmWecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            return Optional.empty();
        }
        try
        {
            String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
            JSONObject res = wecomTencentApiClient.externalContactGet(accessToken, externalUserid.trim());
            int err = res.getIntValue("errcode");
            if (err != 0)
            {
                log.warn("WeCom externalcontact/get failed externalUserid={} errcode={} errmsg={}",
                        externalUserid, err, res.getString("errmsg"));
                return Optional.empty();
            }
            return Optional.of(parseResponse(externalUserid.trim(), res));
        }
        catch (Exception e)
        {
            log.warn("WeCom externalcontact/get error externalUserid={}: {}", externalUserid, e.getMessage());
            return Optional.empty();
        }
    }

    static WecomExternalContactProfile parseResponse(String externalUserid, JSONObject res)
    {
        WecomExternalContactProfile profile = new WecomExternalContactProfile();
        profile.setExternalUserid(externalUserid);
        String raw = res.toJSONString();
        profile.setRawJson(raw.length() <= MAX_RAW_JSON ? raw : raw.substring(0, MAX_RAW_JSON));

        fillFromExternalContact(profile, res.getJSONObject("external_contact"));
        return profile;
    }

    /**
     * 从 raw_json 解析客户 profile（兼容 get 响应与 batch 导入 pair）。
     */
    public static WecomExternalContactProfile parseProfileFromRaw(String rawJson, String externalUserid)
    {
        if (!StringUtils.hasText(rawJson))
        {
            return null;
        }
        try
        {
            JSONObject root = com.alibaba.fastjson2.JSONObject.parseObject(rawJson);
            if (root == null)
            {
                return null;
            }
            WecomExternalContactProfile profile = new WecomExternalContactProfile();
            profile.setExternalUserid(externalUserid);
            String raw = rawJson.length() <= MAX_RAW_JSON ? rawJson : rawJson.substring(0, MAX_RAW_JSON);
            profile.setRawJson(raw);
            fillFromExternalContact(profile, root.getJSONObject("external_contact"));
            return profile;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    static void fillFromExternalContact(WecomExternalContactProfile profile, JSONObject ec)
    {
        if (profile == null || ec == null)
        {
            return;
        }
        String name = ec.getString("name");
        if (StringUtils.hasText(name))
        {
            profile.setName(name.trim());
        }
        String unionId = ec.getString("unionid");
        if (StringUtils.hasText(unionId))
        {
            profile.setUnionId(unionId.trim());
        }
        profile.setAvatar(ec.getString("avatar"));
        profile.setGender(ec.getInteger("gender"));
        profile.setType(ec.getInteger("type"));
        String position = ec.getString("position");
        if (StringUtils.hasText(position))
        {
            profile.setPosition(position.trim());
        }
        String corpName = ec.getString("corp_name");
        if (StringUtils.hasText(corpName))
        {
            profile.setCorpName(corpName.trim());
        }
        String corpFullName = ec.getString("corp_full_name");
        if (StringUtils.hasText(corpFullName))
        {
            profile.setCorpFullName(corpFullName.trim());
        }
    }
}
