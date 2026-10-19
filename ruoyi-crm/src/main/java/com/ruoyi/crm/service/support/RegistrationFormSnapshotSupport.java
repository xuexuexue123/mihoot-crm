package com.ruoyi.crm.service.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOutreachlead;
import com.ruoyi.crm.domain.dto.EventRegistrationCreateRequest;
import com.ruoyi.crm.domain.vo.EventRegistrationDetailVo;

/**
 * H5 报名表单快照构建与详情降级组装
 */
public final class RegistrationFormSnapshotSupport
{
    private static final Set<String> STANDARD_FIELD_NAMES;

    static
    {
        Set<String> names = new LinkedHashSet<>();
        names.add("leadName");
        names.add("name");
        names.add("mobilePhone");
        names.add("fullName");
        names.add("workEmail");
        names.add("shortdesc");
        names.add("shortDescription");
        names.add("description");
        names.add("workPhone");
        names.add("industry");
        names.add("website");
        STANDARD_FIELD_NAMES = Collections.unmodifiableSet(names);
    }

    private RegistrationFormSnapshotSupport()
    {
    }

    public static final class Snapshot
    {
        private final String formDataJson;
        private final String customField;

        public Snapshot(String formDataJson, String customField)
        {
            this.formDataJson = formDataJson;
            this.customField = customField;
        }

        public String getFormDataJson()
        {
            return formDataJson;
        }

        public String getCustomField()
        {
            return customField;
        }
    }

    public static Snapshot fromSubmission(CrmOutreachlead submission)
    {
        Map<String, Object> formData = buildFormDataFromSubmission(submission);
        if (submission instanceof com.ruoyi.crm.domain.dto.PublicWebsiteEventRegisterRequest)
        {
            Map<String, Object> requestFormData =
                    ((com.ruoyi.crm.domain.dto.PublicWebsiteEventRegisterRequest) submission).getFormData();
            if (requestFormData != null)
            {
                for (Map.Entry<String, Object> entry : requestFormData.entrySet())
                {
                    if (entry.getKey() == null || entry.getValue() == null)
                    {
                        continue;
                    }
                    formData.putIfAbsent(entry.getKey(), entry.getValue());
                }
            }
        }
        String customField = resolveCustomField(submission, formData);
        return new Snapshot(toJsonObject(formData), customField);
    }

    public static Snapshot fromManualRequest(EventRegistrationCreateRequest request)
    {
        Map<String, Object> formData = new LinkedHashMap<>();
        if (request == null)
        {
            return new Snapshot(null, null);
        }
        putIfNotBlank(formData, "leadName", request.getDisplayName());
        putIfNotBlank(formData, "mobilePhone", request.getMobilePhone());
        return new Snapshot(toJsonObject(formData), null);
    }

    public static void applyRedundantFields(EventRegistrationDetailVo vo)
    {
        if (vo == null || vo.getFormData() == null)
        {
            return;
        }
        Map<String, Object> formData = vo.getFormData();
        if (StringUtils.isBlank(vo.getWorkEmail()))
        {
            vo.setWorkEmail(stringValue(formData.get("workEmail")));
        }
        if (StringUtils.isBlank(vo.getFullName()))
        {
            vo.setFullName(stringValue(formData.get("fullName")));
        }
        if (StringUtils.isBlank(vo.getShortDescription()))
        {
            String shortDesc = stringValue(formData.get("shortDescription"));
            if (StringUtils.isBlank(shortDesc))
            {
                shortDesc = stringValue(formData.get("shortdesc"));
            }
            vo.setShortDescription(shortDesc);
        }
    }

    public static Map<String, Object> parseFormDataJson(String formDataJson)
    {
        if (StringUtils.isBlank(formDataJson))
        {
            return null;
        }
        try
        {
            JSONObject obj = JSON.parseObject(formDataJson);
            if (obj == null || obj.isEmpty())
            {
                return null;
            }
            return new LinkedHashMap<>(obj);
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    public static Map<String, Object> assembleFromLead(CrmLead lead)
    {
        if (lead == null)
        {
            return null;
        }
        Map<String, Object> formData = new LinkedHashMap<>();
        putIfNotBlank(formData, "leadName", lead.getLeadName());
        putIfNotBlank(formData, "mobilePhone", lead.getMobilePhone());
        putIfNotBlank(formData, "fullName", lead.getFullName());
        putIfNotBlank(formData, "workEmail", lead.getWorkEmail());
        putIfNotBlank(formData, "shortdesc", lead.getShortDescription());
        putIfNotBlank(formData, "shortDescription", lead.getShortDescription());
        putIfNotBlank(formData, "description", lead.getDescription());
        putIfNotBlank(formData, "workPhone", lead.getWorkPhone());
        putIfNotBlank(formData, "industry", lead.getIndustry());
        putIfNotBlank(formData, "website", lead.getWebsite());
        mergeCustomFieldIntoFormData(formData, lead.getCustomField());
        return formData.isEmpty() ? null : formData;
    }

    public static Map<String, Object> assembleFromContact(CrmContact contact)
    {
        if (contact == null)
        {
            return null;
        }
        Map<String, Object> formData = new LinkedHashMap<>();
        putIfNotBlank(formData, "leadName", contact.getName());
        putIfNotBlank(formData, "name", contact.getName());
        putIfNotBlank(formData, "mobilePhone", contact.getMobilePhone());
        putIfNotBlank(formData, "fullName", contact.getFullName());
        putIfNotBlank(formData, "workEmail", contact.getWorkEmail());
        putIfNotBlank(formData, "description", contact.getResponsibility());
        putIfNotBlank(formData, "workPhone", contact.getWorkPhone());
        putIfNotBlank(formData, "industry", contact.getTerritory());
        mergeCustomFieldIntoFormData(formData, contact.getCustomField());
        return formData.isEmpty() ? null : formData;
    }

    private static Map<String, Object> buildFormDataFromSubmission(CrmOutreachlead submission)
    {
        Map<String, Object> formData = new LinkedHashMap<>();
        if (submission == null)
        {
            return formData;
        }
        for (String fieldName : STANDARD_FIELD_NAMES)
        {
            String value = resolveStandardFieldValue(submission, fieldName);
            if (StringUtils.isNotBlank(value))
            {
                formData.put(fieldName, value);
            }
        }
        mergeCustomFieldIntoFormData(formData, submission.getCustomField());
        return formData;
    }

    private static String resolveCustomField(CrmOutreachlead submission, Map<String, Object> formData)
    {
        if (submission != null && StringUtils.isNotBlank(submission.getCustomField()))
        {
            return submission.getCustomField().trim();
        }
        return buildCustomFieldArrayFromFormData(formData);
    }

    private static String buildCustomFieldArrayFromFormData(Map<String, Object> formData)
    {
        if (formData == null || formData.isEmpty())
        {
            return null;
        }
        JSONArray array = new JSONArray();
        for (Map.Entry<String, Object> entry : formData.entrySet())
        {
            if (STANDARD_FIELD_NAMES.contains(entry.getKey()))
            {
                continue;
            }
            if (entry.getValue() == null || StringUtils.isBlank(String.valueOf(entry.getValue())))
            {
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("fieldName", entry.getKey());
            item.put("value", entry.getValue());
            array.add(item);
        }
        return array.isEmpty() ? null : array.toJSONString();
    }

    private static String resolveStandardFieldValue(CrmOutreachlead submission, String fieldName)
    {
        switch (fieldName)
        {
            case "leadName":
                return submission.getLeadName();
            case "name":
                return submission.getLeadName();
            case "mobilePhone":
                return submission.getMobilePhone();
            case "fullName":
                return submission.getFullName();
            case "workEmail":
                return submission.getWorkEmail();
            case "shortdesc":
            case "shortDescription":
                return submission.getShortDescription();
            case "description":
                return submission.getDescription();
            case "workPhone":
                return submission.getWorkPhone();
            case "industry":
                return submission.getIndustry();
            case "website":
                return submission.getWebsite();
            default:
                return null;
        }
    }

    private static void mergeCustomFieldIntoFormData(Map<String, Object> formData, String customFieldJson)
    {
        if (StringUtils.isBlank(customFieldJson) || formData == null)
        {
            return;
        }
        try
        {
            JSONArray array = JSON.parseArray(customFieldJson);
            if (array == null)
            {
                return;
            }
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject item = array.getJSONObject(i);
                if (item == null)
                {
                    continue;
                }
                String fieldName = item.getString("fieldName");
                Object value = item.get("value");
                if (StringUtils.isNotBlank(fieldName) && value != null
                        && StringUtils.isNotBlank(String.valueOf(value)))
                {
                    formData.putIfAbsent(fieldName, value);
                }
            }
        }
        catch (Exception ignored)
        {
            // ignore malformed custom_field
        }
    }

    private static void putIfNotBlank(Map<String, Object> target, String key, String value)
    {
        if (target != null && StringUtils.isNotBlank(value))
        {
            target.put(key, value.trim());
        }
    }

    private static String toJsonObject(Map<String, Object> formData)
    {
        if (formData == null || formData.isEmpty())
        {
            return null;
        }
        return JSON.toJSONString(formData);
    }

    private static String stringValue(Object value)
    {
        if (value == null)
        {
            return null;
        }
        String text = String.valueOf(value);
        return StringUtils.isBlank(text) ? null : text;
    }
}
