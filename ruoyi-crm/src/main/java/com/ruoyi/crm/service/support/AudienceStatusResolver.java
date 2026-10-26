package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.List;
import com.ruoyi.common.utils.StringUtils;

/**
 * 活动受众 audienceStatus 计算
 */
public final class AudienceStatusResolver
{
    private AudienceStatusResolver()
    {
    }

    public static final String REGISTRATION_PENDING = "registration_pending";
    public static final String LEAD_ACTIVE = "lead_active";
    public static final String CONVERTED_CONTACT = "converted_contact";
    public static final String CONTACT_KNOWN = "contact_known";
    public static final String CHANNEL_ONLY = "channel_only";

    public static String resolve(MergedAudienceState state)
    {
        List<String> candidates = new ArrayList<>();
        if (state.isContactKnown())
        {
            candidates.add(CONTACT_KNOWN);
        }
        if (state.isConvertedContact())
        {
            candidates.add(CONVERTED_CONTACT);
        }
        if (state.isLeadActive())
        {
            candidates.add(LEAD_ACTIVE);
        }
        if (state.isRegistrationPending())
        {
            candidates.add(REGISTRATION_PENDING);
        }
        if (state.hasChannelOnly())
        {
            candidates.add(CHANNEL_ONLY);
        }
        return pickHighest(candidates);
    }

    private static String pickHighest(List<String> candidates)
    {
        if (candidates.contains(CONTACT_KNOWN))
        {
            return CONTACT_KNOWN;
        }
        if (candidates.contains(CONVERTED_CONTACT))
        {
            return CONVERTED_CONTACT;
        }
        if (candidates.contains(LEAD_ACTIVE))
        {
            return LEAD_ACTIVE;
        }
        if (candidates.contains(REGISTRATION_PENDING))
        {
            return REGISTRATION_PENDING;
        }
        return CHANNEL_ONLY;
    }

    /**
     * 合并后的状态判定输入
     */
    public static class MergedAudienceState
    {
        private boolean hasH5;
        private boolean hasWecom;
        private boolean hasMp;
        private String contactId;
        private String leadId;
        private String leadStatus;
        private String leadStage;
        private boolean leadHasContactId;

        public boolean isRegistrationPending()
        {
            return hasH5 && StringUtils.isNotEmpty(leadId)
                    && AudienceLeadVisibility.isInitialStatus(leadStatus);
        }

        public boolean isLeadActive()
        {
            return hasH5 && StringUtils.isNotEmpty(leadId)
                    && !AudienceLeadVisibility.isInitialStatus(leadStatus)
                    && !leadHasContactId
                    && !"converted".equals(AudienceLeadVisibility.normalizeStatus(leadStage));
        }

        public boolean isConvertedContact()
        {
            return StringUtils.isNotEmpty(leadId)
                    && (leadHasContactId
                    || "converted".equals(AudienceLeadVisibility.normalizeStatus(leadStage)));
        }

        public boolean isContactKnown()
        {
            return StringUtils.isNotEmpty(contactId) && StringUtils.isEmpty(leadId);
        }

        public boolean hasChannelOnly()
        {
            return (hasWecom || hasMp || hasH5)
                    && StringUtils.isEmpty(contactId)
                    && StringUtils.isEmpty(leadId);
        }

        public void setHasH5(boolean hasH5)
        {
            this.hasH5 = hasH5;
        }

        public void setHasWecom(boolean hasWecom)
        {
            this.hasWecom = hasWecom;
        }

        public void setHasMp(boolean hasMp)
        {
            this.hasMp = hasMp;
        }

        public void setContactId(String contactId)
        {
            this.contactId = contactId;
        }

        public void setLeadId(String leadId)
        {
            this.leadId = leadId;
        }

        public void setLeadStatus(String leadStatus)
        {
            this.leadStatus = leadStatus;
        }

        public void setLeadStage(String leadStage)
        {
            this.leadStage = leadStage;
        }

        public void setLeadHasContactId(boolean leadHasContactId)
        {
            this.leadHasContactId = leadHasContactId;
        }
    }
}
