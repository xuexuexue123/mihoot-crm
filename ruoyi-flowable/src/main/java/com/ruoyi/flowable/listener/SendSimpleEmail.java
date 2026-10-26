package com.ruoyi.flowable.listener;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.ruoyi.common.utils.mailgun.MGSamples;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.el.FixedValue;

public class SendSimpleEmail implements JavaDelegate {


    private Expression fromEmail;
    private Expression toEmail;
    private Expression subject;
    private Expression message;

    public void setFromEmail(Expression fromEmail) {
        this.fromEmail = fromEmail;
    }

    public void setToEmail(Expression toEmail) {
        this.toEmail = toEmail;
    }

    public void setSubject(Expression subject) {
        this.subject = subject;
    }

    public void setMessage(Expression message) {
        this.message = message;
    }

    @Override
    public void execute(DelegateExecution execution) {

        String fromStr = fromEmail.getValue(execution).toString();
        String toStr = toEmail.getValue(execution).toString();
        String subjectStr = subject.getValue(execution).toString();
        String bodyStr = message.getValue(execution).toString();

        try {
            MGSamples.sendSimpleMessage(toStr, subjectStr, bodyStr);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
