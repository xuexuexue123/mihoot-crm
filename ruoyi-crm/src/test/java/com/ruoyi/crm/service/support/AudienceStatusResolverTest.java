package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AudienceStatusResolverTest
{
    @Test
    public void registrationPendingForInitialLead()
    {
        AudienceStatusResolver.MergedAudienceState state = new AudienceStatusResolver.MergedAudienceState();
        state.setHasH5(true);
        state.setLeadId("lead-1");
        state.setLeadStatus("Initial");
        assertEquals(AudienceStatusResolver.REGISTRATION_PENDING, AudienceStatusResolver.resolve(state));
    }

    @Test
    public void leadActiveForReleasedLead()
    {
        AudienceStatusResolver.MergedAudienceState state = new AudienceStatusResolver.MergedAudienceState();
        state.setHasH5(true);
        state.setLeadId("lead-1");
        state.setLeadStatus("Converted");
        state.setLeadStage("New");
        assertEquals(AudienceStatusResolver.LEAD_ACTIVE, AudienceStatusResolver.resolve(state));
    }

    @Test
    public void convertedContactWhenLeadHasContact()
    {
        AudienceStatusResolver.MergedAudienceState state = new AudienceStatusResolver.MergedAudienceState();
        state.setHasH5(true);
        state.setLeadId("lead-1");
        state.setLeadStatus("Converted");
        state.setLeadHasContactId(true);
        state.setContactId("contact-1");
        assertEquals(AudienceStatusResolver.CONVERTED_CONTACT, AudienceStatusResolver.resolve(state));
    }

    @Test
    public void contactKnownWithoutLead()
    {
        AudienceStatusResolver.MergedAudienceState state = new AudienceStatusResolver.MergedAudienceState();
        state.setHasWecom(true);
        state.setContactId("contact-1");
        assertEquals(AudienceStatusResolver.CONTACT_KNOWN, AudienceStatusResolver.resolve(state));
    }

    @Test
    public void channelOnlyForWecomWithoutLeadOrContact()
    {
        AudienceStatusResolver.MergedAudienceState state = new AudienceStatusResolver.MergedAudienceState();
        state.setHasWecom(true);
        assertEquals(AudienceStatusResolver.CHANNEL_ONLY, AudienceStatusResolver.resolve(state));
    }

    @Test
    public void contactKnownBeatsChannelOnly()
    {
        AudienceStatusResolver.MergedAudienceState state = new AudienceStatusResolver.MergedAudienceState();
        state.setHasMp(true);
        state.setContactId("c1");
        assertEquals(AudienceStatusResolver.CONTACT_KNOWN, AudienceStatusResolver.resolve(state));
    }
}
