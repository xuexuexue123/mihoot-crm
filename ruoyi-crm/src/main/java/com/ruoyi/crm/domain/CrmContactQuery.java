package com.ruoyi.crm.domain;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 搜索联系人的动态sql条件
 */
public class CrmContactQuery extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private List<CrmTag> conditions;

    public List<CrmTag> getConditions() {
        return conditions;
    }

    public void setConditions(List<CrmTag> conditions) {
        this.conditions = conditions;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("conditions", getConditions())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}

