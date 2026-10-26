package com.ruoyi.crm.domain;

public class CrmCollectEmail extends CrmCollect{

    private static final long serialVersionUID = 1L;

    private String toAddress;
    private String subject;
    private String htmlBody;

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    @Override
    public String toString() {
        return "CrmCollectEmail{" +
                "toAddress='" + toAddress + '\'' +
                ", subject='" + subject + '\'' +
                ", htmlBody='" + htmlBody + '\'' +
                '}';
    }
}
