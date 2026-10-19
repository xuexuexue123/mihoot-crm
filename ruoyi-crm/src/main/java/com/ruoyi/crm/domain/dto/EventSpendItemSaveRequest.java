package com.ruoyi.crm.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 活动分项花费保存请求
 */
public class EventSpendItemSaveRequest
{
    private List<Item> items;

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public static class Item
    {
        private String channelKey;
        private String category;
        private BigDecimal amount;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date spentAt;

        private String remark;

        public String getChannelKey() { return channelKey; }
        public void setChannelKey(String channelKey) { this.channelKey = channelKey; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public Date getSpentAt() { return spentAt; }
        public void setSpentAt(Date spentAt) { this.spentAt = spentAt; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}
