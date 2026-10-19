package com.ruoyi.ems.domain;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 数据对象对象 sys_re_dataobj
 * 
 * @author luohd
 * @date 2023-03-02
 */
public class SysReDataobj extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 对象编码 */
    private String dataobjId;

    /** 排序 */
    @Excel(name = "排序")
    private Long dataobjSort;

    /** 对象中文名 */
    @Excel(name = "对象中文名")
    private String dataobjLabel;

    /** 对象名称 */
    @Excel(name = "对象名称")
    private String dataobjName;

    /** 表名 */
    @Excel(name = "表名")
    private String tableName;

    /** Schema名 */
    private String schemaName;

    /** 数据库名 */
    private String databaseName;

    /** case应用id */
    @Excel(name = "case应用id")
    private String caseApplicationId;

    /** 字段信息 */
    private List<SysReDatafield> sysReDatafieldList;

    public String getDataobjId() {
        return dataobjId;
    }

    public void setDataobjId(String dataobjId) {
        this.dataobjId = dataobjId;
    }

    public void setDataobjSort(Long dataobjSort)
    {
        this.dataobjSort = dataobjSort;
    }

    public Long getDataobjSort() 
    {
        return dataobjSort;
    }
    public void setDataobjLabel(String dataobjLabel) 
    {
        this.dataobjLabel = dataobjLabel;
    }

    public String getDataobjLabel() 
    {
        return dataobjLabel;
    }
    public void setDataobjName(String dataobjName) 
    {
        this.dataobjName = dataobjName;
    }

    public String getDataobjName() 
    {
        return dataobjName;
    }
    public void setTableName(String tableName) 
    {
        this.tableName = tableName;
    }

    public String getTableName() 
    {
        return tableName;
    }
    public void setSchemaName(String schemaName) 
    {
        this.schemaName = schemaName;
    }

    public String getSchemaName() 
    {
        return schemaName;
    }
    public void setDatabaseName(String databaseName) 
    {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() 
    {
        return databaseName;
    }
    public void setCaseApplicationId(String caseApplicationId) 
    {
        this.caseApplicationId = caseApplicationId;
    }

    public String getCaseApplicationId() 
    {
        return caseApplicationId;
    }

    public List<SysReDatafield> getSysReDatafieldList()
    {
        return sysReDatafieldList;
    }

    public void setSysReDatafieldList(List<SysReDatafield> sysReDatafieldList)
    {
        this.sysReDatafieldList = sysReDatafieldList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dataobjId", getDataobjId())
            .append("dataobjSort", getDataobjSort())
            .append("dataobjLabel", getDataobjLabel())
            .append("dataobjName", getDataobjName())
            .append("tableName", getTableName())
            .append("schemaName", getSchemaName())
            .append("databaseName", getDatabaseName())
            .append("caseApplicationId", getCaseApplicationId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("sysReDatafieldList", getSysReDatafieldList())
            .toString();
    }
}
