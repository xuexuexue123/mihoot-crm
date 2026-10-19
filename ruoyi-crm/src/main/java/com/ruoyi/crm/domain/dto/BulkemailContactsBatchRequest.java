package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量写入活动联系人请求
 */
public class BulkemailContactsBatchRequest
{
    private List<BulkemailContactItem> contacts = new ArrayList<>();
    private boolean replaceExisting;

    public List<BulkemailContactItem> getContacts()
    {
        return contacts;
    }

    public void setContacts(List<BulkemailContactItem> contacts)
    {
        this.contacts = contacts;
    }

    public boolean isReplaceExisting()
    {
        return replaceExisting;
    }

    public void setReplaceExisting(boolean replaceExisting)
    {
        this.replaceExisting = replaceExisting;
    }

    public static class BulkemailContactItem
    {
        private String id;
        private String contactId;
        private String contactName;
        private String email;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getContactId()
        {
            return contactId;
        }

        public void setContactId(String contactId)
        {
            this.contactId = contactId;
        }

        public String getContactName()
        {
            return contactName;
        }

        public void setContactName(String contactName)
        {
            this.contactName = contactName;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }
    }
}
