package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量写入活动联系人结果
 */
public class BulkemailContactsBatchResult
{
    private int inserted;
    private int skipped;
    private List<SkippedReason> skippedReasons = new ArrayList<>();

    public int getInserted()
    {
        return inserted;
    }

    public void setInserted(int inserted)
    {
        this.inserted = inserted;
    }

    public int getSkipped()
    {
        return skipped;
    }

    public void setSkipped(int skipped)
    {
        this.skipped = skipped;
    }

    public List<SkippedReason> getSkippedReasons()
    {
        return skippedReasons;
    }

    public void setSkippedReasons(List<SkippedReason> skippedReasons)
    {
        this.skippedReasons = skippedReasons;
    }

    public void addSkipped(String email, String reason)
    {
        skipped++;
        SkippedReason item = new SkippedReason();
        item.setEmail(email != null ? email : "");
        item.setReason(reason);
        skippedReasons.add(item);
    }

    public static class SkippedReason
    {
        private String email;
        private String reason;

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public String getReason()
        {
            return reason;
        }

        public void setReason(String reason)
        {
            this.reason = reason;
        }
    }
}
