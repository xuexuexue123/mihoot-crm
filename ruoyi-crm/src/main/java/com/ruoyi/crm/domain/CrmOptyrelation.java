package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机会关系对象 crm_optyrelation
 * 
 * @author luohd
 * @date 2024-02-07
 */
public class CrmOptyrelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String relateId;

    /** 销售机会id */
    @Excel(name = "销售机会id")
    private String sourceId;

    /** 机会名 */
    @Excel(name = "机会名")
    private String sourceName;

    /** 关联角色 */
    @Excel(name = "关联角色")
    private String relateRole;

    /** 关联目标ID */
    @Excel(name = "关联目标ID")
    private String targetId;

    /** 关联类型(竞争对手、产品、合同、报价单、收单计划) */
    @Excel(name = "关联类型(竞争对手、产品、合同、报价单、收单计划)")
    private String targetType;

    /** 关联对象名称 */
    @Excel(name = "关联对象名称")
    private String targetName;

    /** 描述 */
    @Excel(name = "描述")
    private String relationDesc;


    /** 自定义 */
    @Excel(name = "自定义")
    private String customField;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 优先级 */
    @Excel(name = "优先级")
    private Long priority;

    /** 对接人 */
    @Excel(name = "对接人")
    private String owner;

    /** 对接人ID */
    @Excel(name = "对接人ID")
    private Long userId;

    /** 最新更新 */
    @Excel(name = "最新更新")
    private String latestUpdate;

    /** 阶段 */
    @Excel(name = "阶段")
    private String stage;

    /** 打开次数 */
    @Excel(name = "打开次数")
    private Long openCount;

    /** 打开状态 */
    @Excel(name = "打开状态")
    private String openStatus;

    /** 反馈内容 */
    @Excel(name = "反馈内容")
    private String feedback;

    /** 验证码 */
    @Excel(name = "验证码")
    private String validationCode;

    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setSourceId(String sourceId) 
    {
        this.sourceId = sourceId;
    }

    public String getSourceId() 
    {
        return sourceId;
    }
    public void setSourceName(String sourceName) 
    {
        this.sourceName = sourceName;
    }

    public String getSourceName() 
    {
        return sourceName;
    }
    public void setRelateRole(String relateRole) 
    {
        this.relateRole = relateRole;
    }

    public String getRelateRole() 
    {
        return relateRole;
    }
    public void setTargetId(String targetId) 
    {
        this.targetId = targetId;
    }

    public String getTargetId() 
    {
        return targetId;
    }
    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }
    public void setTargetName(String targetName) 
    {
        this.targetName = targetName;
    }

    public String getTargetName() 
    {
        return targetName;
    }
    public void setRelationDesc(String relationDesc) 
    {
        this.relationDesc = relationDesc;
    }

    public String getRelationDesc() 
    {
        return relationDesc;
    }

    public void setCustomField(String customField)
    {
        this.customField = customField;
    }

    public String getCustomField()
    {
        return customField;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setPriority(Long priority)
    {
        this.priority = priority;
    }

    public Long getPriority()
    {
        return priority;
    }
    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getOwner()
    {
        return owner;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setLatestUpdate(String latestUpdate)
    {
        this.latestUpdate = latestUpdate;
    }

    public String getLatestUpdate()
    {
        return latestUpdate;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public String getStage()
    {
        return stage;
    }

    public void setOpenCount(Long openCount)
    {
        this.openCount = openCount;
    }

    public Long getOpenCount()
    {
        return openCount;
    }
    public void setOpenStatus(String openStatus)
    {
        this.openStatus = openStatus;
    }

    public String getOpenStatus()
    {
        return openStatus;
    }
    public void setFeedback(String feedback)
    {
        this.feedback = feedback;
    }

    public String getFeedback()
    {
        return feedback;
    }

    public void setValidationCode(String validationCode)
    {
        this.validationCode = validationCode;
    }

    public String getValidationCode()
    {
        return validationCode;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("relateId", getRelateId())
            .append("sourceId", getSourceId())
            .append("sourceName", getSourceName())
            .append("relateRole", getRelateRole())
            .append("targetId", getTargetId())
            .append("targetType", getTargetType())
            .append("targetName", getTargetName())
            .append("relationDesc", getRelationDesc())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
