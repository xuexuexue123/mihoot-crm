package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 动态对象 crm_pulse
 * 
 * @author luohd
 * @date 2024-02-24
 */
public class CrmPulse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String pulseId;

    /** 父编号 */
    @Excel(name = "父编号")
    private String parentId;

    /** 顺序 */
    @Excel(name = "顺序")
    private Long orderId;

    /** 消息类型(消息、评论) */
    @Excel(name = "消息类型(消息、评论)")
    private String pulseType;

    /** 类型(公共、联系人、机构、机会） */
    @Excel(name = "类型(公共、联系人、机构、机会）")
    private String relateType;

    /** 关联编号 */
    @Excel(name = "关联编号")
    private String relateId;

    /** 关联名称 */
    @Excel(name = "关联名称")
    private String relateName;

    /** 消息内容 */
    @Excel(name = "消息内容")
    private String messageData;

    /** 创建者ID */
    @Excel(name = "创建者ID")
    private String creatorId;
    /** 发布人 */
    @Excel(name = "发布人")
    private String avatar;
    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    public void setPulseId(String pulseId) 
    {
        this.pulseId = pulseId;
    }

    public String getPulseId() 
    {
        return pulseId;
    }
    public void setParentId(String parentId) 
    {
        this.parentId = parentId;
    }

    public String getParentId() 
    {
        return parentId;
    }
    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }
    public void setPulseType(String pulseType) 
    {
        this.pulseType = pulseType;
    }

    public String getPulseType() 
    {
        return pulseType;
    }
    public void setRelateType(String relateType) 
    {
        this.relateType = relateType;
    }

    public String getRelateType() 
    {
        return relateType;
    }
    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setRelateName(String relateName) 
    {
        this.relateName = relateName;
    }

    public String getRelateName() 
    {
        return relateName;
    }
    public void setMessageData(String messageData) 
    {
        this.messageData = messageData;
    }

    public String getMessageData() 
    {
        return messageData;
    }
    public void setCreatorId(String creatorId) 
    {
        this.creatorId = creatorId;
    }

    public String getCreatorId() 
    {
        return creatorId;
    }
    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pulseId", getPulseId())
            .append("parentId", getParentId())
            .append("orderId", getOrderId())
            .append("pulseType", getPulseType())
            .append("relateType", getRelateType())
            .append("relateId", getRelateId())
            .append("relateName", getRelateName())
            .append("messageData", getMessageData())
            .append("creatorId", getCreatorId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
