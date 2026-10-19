package com.ruoyi.crm.event;


import org.springframework.context.ApplicationEvent;

import java.util.Map;

public class SurveySubmittedEvent extends ApplicationEvent {
    private final String surveyId;
    private final Map<String, Object> response;


    public SurveySubmittedEvent(Object source, String surveyId, Map<String, Object> response) {
        super(source);
        this.surveyId = surveyId;
        this.response = response;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public Map<String, Object> getResponse() {
        return response;
    }
}
