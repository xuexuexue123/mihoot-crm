package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 销售区域对象 crm_territory
 * 
 * @author luohd
 * @date 2024-02-28
 */
public class CrmTerritory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String territoryId;

    /** 名称 */
    @Excel(name = "名称")
    private String territoryName;

    /** 是否激活 （Y是 N否） */
    @Excel(name = "是否激活 ", readConverterExp = "Y=是,N=否")
    private String isActive;

    /** 父编号 */
    @Excel(name = "父编号")
    private String parentId;

    /** 父名称 */
    @Excel(name = "父名称")
    private String parentName;

    /** 经理 */
    @Excel(name = "经理")
    private String forecastManager;

    /** 经理ID */
    @Excel(name = "经理ID")
    private String forecastManagerid;

    /** 字典编号 */
    @Excel(name = "字典编号")
    private Long dictCode;

    public void setTerritoryId(String territoryId) 
    {
        this.territoryId = territoryId;
    }

    public String getTerritoryId() 
    {
        return territoryId;
    }
    public void setTerritoryName(String territoryName) 
    {
        this.territoryName = territoryName;
    }

    public String getTerritoryName() 
    {
        return territoryName;
    }
    public void setIsActive(String isActive) 
    {
        this.isActive = isActive;
    }

    public String getIsActive() 
    {
        return isActive;
    }
    public void setParentId(String parentId) 
    {
        this.parentId = parentId;
    }

    public String getParentId() 
    {
        return parentId;
    }
    public void setParentName(String parentName) 
    {
        this.parentName = parentName;
    }

    public String getParentName() 
    {
        return parentName;
    }
    public void setForecastManager(String forecastManager) 
    {
        this.forecastManager = forecastManager;
    }

    public String getForecastManager() 
    {
        return forecastManager;
    }
    public void setForecastManagerid(String forecastManagerid) 
    {
        this.forecastManagerid = forecastManagerid;
    }

    public String getForecastManagerid() 
    {
        return forecastManagerid;
    }
    public void setDictCode(Long dictCode) 
    {
        this.dictCode = dictCode;
    }

    public Long getDictCode() 
    {
        return dictCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("territoryId", getTerritoryId())
            .append("territoryName", getTerritoryName())
            .append("isActive", getIsActive())
            .append("parentId", getParentId())
            .append("parentName", getParentName())
            .append("forecastManager", getForecastManager())
            .append("forecastManagerid", getForecastManagerid())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("dictCode", getDictCode())
            .toString();
    }
}
