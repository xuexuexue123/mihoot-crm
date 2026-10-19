package com.ruoyi.crm.wecom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;

/**
 * 从 externalcontact/get 或 KF API 的 raw_json 同步企微展示字段到 {@code crm_contact}。
 */
@Component
public class WecomContactInfoSyncSupport
{
    private static final Logger log = LoggerFactory.getLogger(WecomContactInfoSyncSupport.class);

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CrmWecomExternalContactMapper wecomExternalContactMapper;

    /**
     * 使用给定 raw_json 最佳努力更新 Contact 企微字段。
     */
    public boolean syncToContact(String externalUserid, String contactId, String rawJson)
    {
        if (!StringUtils.hasText(externalUserid) || !StringUtils.hasText(contactId))
        {
            return false;
        }
        try
        {
            CrmContact contact = crmContactMapper.selectCrmContactByContactId(contactId);
            if (contact == null)
            {
                log.warn("syncWecomInfo: contact not found contactId={}", contactId);
                return false;
            }

            CrmContact upd = new CrmContact();
            upd.setContactId(contactId);
            upd.setWecomExternalUserid(externalUserid.trim());
            if (StringUtils.hasText(rawJson))
            {
                fillWecomInfoFromRawJson(upd, rawJson);
            }
            upd.setUpdateTime(DateUtils.getNowDate());
            crmContactMapper.updateCrmContact(upd);
            return true;
        }
        catch (Exception e)
        {
            log.warn("syncWecomInfo failed externalUserid={} contactId={}: {}",
                    externalUserid, contactId, e.getMessage());
            return false;
        }
    }

    /**
     * 从 crm_wecom_external_contact 首条记录的 raw_json 同步。
     */
    public boolean syncFromExternalContactRow(String externalUserid, String contactId)
    {
        List<CrmWecomExternalContact> extList = wecomExternalContactMapper.selectByExternalUserid(externalUserid);
        if (extList.isEmpty() || extList.get(0).getRawJson() == null)
        {
            return syncToContact(externalUserid, contactId, null);
        }
        return syncToContact(externalUserid, contactId, extList.get(0).getRawJson());
    }

    /**
     * 兼容 externalcontact/get 与 kf/customer/batchget 两种返回格式。
     */
    public static void fillWecomInfoFromRawJson(CrmContact upd, String rawJson)
    {
        try
        {
            JSONObject root = JSONObject.parseObject(rawJson);

            JSONObject extContact = root.getJSONObject("external_contact");
            if (extContact != null)
            {
                upd.setWecomName(extContact.getString("name"));
                upd.setWecomAvatar(extContact.getString("avatar"));
                upd.setWecomGender(extContact.getInteger("gender"));
                upd.setWecomType(extContact.getInteger("type"));
            }
            else
            {
                String name = root.getString("nickname");
                if (name == null)
                {
                    name = root.getString("name");
                }
                upd.setWecomName(name);
                upd.setWecomAvatar(root.getString("avatar"));
                upd.setWecomGender(root.getInteger("gender"));
                upd.setWecomType(root.getInteger("type"));
            }
        }
        catch (Exception e)
        {
            log.warn("fillWecomInfoFromRawJson parse failed: {}", e.getMessage());
        }
    }
}
