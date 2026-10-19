package com.ruoyi.crm.wecom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.domain.dto.wecom.WecomFollowUserSnapshot;
import com.ruoyi.ems.domain.SysReDatafield;
import com.ruoyi.ems.service.ISysReDatafieldService;

/**
 * 企微回调/导入新建 {@code crm_lead} 时，从 {@code externalcontact/get} profile 填充业务字段。
 */
@Component
public class WecomLeadEnrichSupport
{
    /** 与 leadTable / crmLeadForm 一致的线索动态字段 dataobj */
    private static final String LEAD_DATAOBJ_ID = "8ee91b1f-c6bd-41d9-a3a1-d14c9fd37444";

    @Autowired
    private ISysReDatafieldService sysReDatafieldService;

    /**
     * 填充新建 Lead 的企微客户详情字段（方案 A）。
     */
    public void enrichNewLead(CrmLead lead, WecomExternalContactProfile profile,
            WecomFollowUserSnapshot follow, String externalUserid, String followUserid,
            String displayNameFallback)
    {
        if (lead == null)
        {
            return;
        }

        Integer wecomType = profile != null ? profile.getType() : null;
        lead.setLeadType(resolveLeadType(wecomType));

        String remark = follow != null ? follow.getRemark() : null;
        String profileName = profile != null ? profile.getName() : null;
        lead.setLeadName(buildLeadDisplayName(externalUserid, firstNonBlank(remark, profileName, displayNameFallback)));

        if (StringUtils.hasText(remark))
        {
            lead.setContactName(truncate(remark.trim(), 80));
        }

        if (profile != null)
        {
            if (StringUtils.hasText(profile.getAvatar()))
            {
                lead.setAvatar(profile.getAvatar().trim());
            }
            String corp = firstNonBlank(profile.getCorpFullName(), profile.getCorpName());
            if (StringUtils.hasText(corp))
            {
                lead.setFullName(truncate(corp.trim(), 200));
            }
        }

        if (StringUtils.hasText(externalUserid))
        {
            lead.setWecomExternalUserid(externalUserid.trim());
        }
        if (StringUtils.hasText(followUserid))
        {
            lead.setWecomFollowUserid(followUserid.trim());
        }

        String tagJson = buildCrmLeadTagJson(follow != null ? follow.getTags() : null);
        if (tagJson != null)
        {
            lead.setTag(tagJson);
        }

        Integer gender = profile != null ? profile.getGender() : null;
        String position = profile != null ? profile.getPosition() : null;
        lead.setCustomField(buildLeadCustomField(externalUserid, gender, position));
    }

    /**
     * 将 follow 维度字段写入 {@code crm_wecom_external_contact} 行（原始企微 tags JSON）。
     */
    public void applyFollowToExternalContactRow(CrmWecomExternalContact row, WecomFollowUserSnapshot follow)
    {
        if (row == null || follow == null)
        {
            return;
        }
        if (StringUtils.hasText(follow.getRemark()))
        {
            row.setRemark(follow.getRemark().trim());
        }
        if (follow.getAddWay() != null)
        {
            row.setAddWay(follow.getAddWay());
        }
        if (StringUtils.hasText(follow.getDescription()))
        {
            row.setDescription(follow.getDescription().trim());
        }
        if (StringUtils.hasText(follow.getOperUserid()))
        {
            row.setOperUserid(follow.getOperUserid().trim());
        }
        if (follow.getCreatetime() != null && follow.getCreatetime() > 0)
        {
            row.setFollowCreateTime(new java.util.Date(follow.getCreatetime() * 1000L));
        }
        if (follow.getTags() != null && !follow.getTags().isEmpty())
        {
            row.setTags(follow.getTags().toJSONString());
        }
        if (follow.getRemarkMobiles() != null && !follow.getRemarkMobiles().isEmpty())
        {
            row.setRemarkMobiles(follow.getRemarkMobiles().toJSONString());
        }
    }

    /**
     * 企微 external_contact.type：2=企微用户→business，1=微信用户→individual。
     */
    public static String resolveLeadType(Integer wecomType)
    {
        if (wecomType != null && wecomType.intValue() == 2)
        {
            return "business";
        }
        return "business";
    }

    /**
     * 将企微 {@code follow_user.tags} 转为 crm_lead.tag 格式：[{id:5位随机,text:tag_name}]。
     */
    public static String buildCrmLeadTagJson(JSONArray wecomTags)
    {
        if (wecomTags == null || wecomTags.isEmpty())
        {
            return null;
        }
        JSONArray crmTags = new JSONArray();
        Set<String> seenNames = new HashSet<>();
        Set<String> usedIds = new HashSet<>();
        for (int i = 0; i < wecomTags.size(); i++)
        {
            JSONObject wecomTag = wecomTags.getJSONObject(i);
            if (wecomTag == null)
            {
                continue;
            }
            String tagName = wecomTag.getString("tag_name");
            if (!StringUtils.hasText(tagName))
            {
                continue;
            }
            String normalized = tagName.trim();
            if (!seenNames.add(normalized))
            {
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("id", randomFiveDigitId(usedIds));
            item.put("text", normalized);
            crmTags.add(item);
        }
        return crmTags.isEmpty() ? null : crmTags.toJSONString();
    }

    static String buildLeadDisplayName(String externalUserid, String displayName)
    {
        if (StringUtils.hasText(displayName))
        {
            return truncate(displayName.trim(), 80);
        }
        if (!StringUtils.hasText(externalUserid))
        {
            return "企微客户";
        }
        return externalUserid.length() > 32 ? externalUserid.substring(0, 32) : externalUserid;
    }

    private String buildLeadCustomField(String externalUserid, Integer gender, String position)
    {
        SysReDatafield query = new SysReDatafield();
        query.setDataobjId(LEAD_DATAOBJ_ID);
        query.setFieldCategory("dynamic");
        List<SysReDatafield> fields = sysReDatafieldService.selectSysReDatafieldList(query);
        if (fields == null || fields.isEmpty())
        {
            return null;
        }
        JSONArray arr = new JSONArray();
        for (SysReDatafield field : fields)
        {
            if (field == null || field.getFieldName() == null)
            {
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("id", field.getFieldId());
            item.put("fieldName", field.getFieldName());
            item.put("fieldLabel", field.getFieldLabel());
            if (field.getFieldType() != null)
            {
                item.put("fieldType", field.getFieldType());
            }
            if (field.getFieldDetail() != null && !field.getFieldDetail().isEmpty())
            {
                item.put("fieldDetail", field.getFieldDetail());
            }
            item.put("value", resolveCustomFieldValue(field.getFieldName(), externalUserid, gender, position));
            arr.add(item);
        }
        return arr.isEmpty() ? null : arr.toJSONString();
    }

    private static String resolveCustomFieldValue(String fieldName, String externalUserid,
            Integer gender, String position)
    {
        if (isExternalUseridField(fieldName))
        {
            return externalUserid != null ? externalUserid : "";
        }
        if (isWecomGenderField(fieldName) && gender != null)
        {
            return String.valueOf(gender);
        }
        if (isWecomPositionField(fieldName) && StringUtils.hasText(position))
        {
            return position.trim();
        }
        return "";
    }

    private static boolean isExternalUseridField(String fieldName)
    {
        return "externaluserid".equals(normalizeFieldName(fieldName));
    }

    private static boolean isWecomGenderField(String fieldName)
    {
        String n = normalizeFieldName(fieldName);
        return "wecomgender".equals(n) || "gender".equals(n);
    }

    private static boolean isWecomPositionField(String fieldName)
    {
        String n = normalizeFieldName(fieldName);
        return "wecomposition".equals(n) || "position".equals(n);
    }

    private static String normalizeFieldName(String fieldName)
    {
        if (fieldName == null)
        {
            return "";
        }
        return fieldName.trim().replace("_", "").replace("-", "").toLowerCase();
    }

    private static String randomFiveDigitId(Set<String> usedIds)
    {
        for (int attempt = 0; attempt < 20; attempt++)
        {
            int n = ThreadLocalRandom.current().nextInt(10000, 100000);
            String id = String.valueOf(n);
            if (usedIds.add(id))
            {
                return id;
            }
        }
        return String.valueOf(ThreadLocalRandom.current().nextInt(10000, 100000));
    }

    private static String firstNonBlank(String... values)
    {
        if (values == null)
        {
            return null;
        }
        for (String v : values)
        {
            if (StringUtils.hasText(v))
            {
                return v;
            }
        }
        return null;
    }

    private static String truncate(String s, int max)
    {
        if (s == null)
        {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
