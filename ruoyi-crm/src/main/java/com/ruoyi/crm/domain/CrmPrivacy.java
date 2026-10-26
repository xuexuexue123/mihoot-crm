package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 专属信息对象 crm_privacy
 * 
 * @author luohd
 * @date 2024-02-22
 */
public class CrmPrivacy extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String privacyId;

    /** 类型(联系人、机构） */
    @Excel(name = "类型(联系人、机构）")
    private String privacyType;

    /** 关联编号 */
    @Excel(name = "关联编号")
    private String relateId;

    /** 关联名称 */
    @Excel(name = "关联名称")
    private String relateName;

    /** 专属信息 */
    @Excel(name = "专属信息")
    private String privacyData;

    /** 操作人 */
    @Excel(name = "操作人")
    private String owner;

    /** 操作人ID */
    @Excel(name = "操作人ID")
    private String ownerId;

    /** 共享人 */
    @Excel(name = "共享人")
    private String sharing;

    /** 是否公开 */
    @Excel(name = "是否公开")
    private String isPublic;

    public void setPrivacyId(String privacyId) 
    {
        this.privacyId = privacyId;
    }

    public String getPrivacyId() 
    {
        return privacyId;
    }
    public void setPrivacyType(String privacyType) 
    {
        this.privacyType = privacyType;
    }

    public String getPrivacyType() 
    {
        return privacyType;
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
    public void setPrivacyData(String privacyData) 
    {
        this.privacyData = privacyData;
    }

    public String getPrivacyData() 
    {
        return privacyData;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setOwnerId(String ownerId) 
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId() 
    {
        return ownerId;
    }
    public void setSharing(String sharing) 
    {
        this.sharing = sharing;
    }

    public String getSharing() 
    {
        return sharing;
    }
    public void setIsPublic(String isPublic) 
    {
        this.isPublic = isPublic;
    }

    public String getIsPublic() 
    {
        return isPublic;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("privacyId", getPrivacyId())
            .append("privacyType", getPrivacyType())
            .append("relateId", getRelateId())
            .append("relateName", getRelateName())
            .append("privacyData", getPrivacyData())
            .append("owner", getOwner())
            .append("ownerId", getOwnerId())
            .append("sharing", getSharing())
            .append("isPublic", getIsPublic())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
