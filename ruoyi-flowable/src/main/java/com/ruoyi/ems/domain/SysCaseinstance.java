package com.ruoyi.ems.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * case实例对象 sys_ru_caseinstance
 * 
 * @author luohd
 * @date 2022-12-29
 */
public class SysCaseinstance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** caseId */
    @Excel(name = "caseId")
    private String pyCaseId;

    /** 标签 */
    @Excel(name = "标签")
    private String pyLabel;

    /** case名称 */
    @Excel(name = "case名称")
    private String pyName;

    /** case定义id */
    @Excel(name = "case定义id")
    private String caseTypeId;

    /** 默认的紧急度 */
    @Excel(name = "默认的紧急度")
    private Long pyUrgency;

    /** 当前阶段 */
    @Excel(name = "当前阶段")
    private String pyCurrentStage;

    /** 状态 */
    @Excel(name = "状态")
    private String pyStatusWork;

    /** case结构 */
    @Excel(name = "case结构")
    private String pyCaseStructure;

    /** 数据 */
    @Excel(name = "数据")
    private String pyWorkPage;

    /** 创建者 */
    @Excel(name = "创建者")
    private String pxCreateBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date pxCreateTime;

    /** 更新者 */
    @Excel(name = "更新者")
    private String pyUpdateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date pyUpdateTime;

    /** case描述 */
    @Excel(name = "case描述")
    private String pyDescription;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPyCaseId(String pyCaseId) 
    {
        this.pyCaseId = pyCaseId;
    }

    public String getPyCaseId() 
    {
        return pyCaseId;
    }
    public void setPyLabel(String pyLabel) 
    {
        this.pyLabel = pyLabel;
    }

    public String getPyLabel() 
    {
        return pyLabel;
    }
    public void setPyName(String pyName) 
    {
        this.pyName = pyName;
    }

    public String getPyName() 
    {
        return pyName;
    }
    public void setCaseTypeId(String caseTypeId) 
    {
        this.caseTypeId = caseTypeId;
    }

    public String getCaseTypeId() 
    {
        return caseTypeId;
    }
    public void setPyUrgency(Long pyUrgency) 
    {
        this.pyUrgency = pyUrgency;
    }

    public Long getPyUrgency() 
    {
        return pyUrgency;
    }
    public void setPyCurrentStage(String pyCurrentStage) 
    {
        this.pyCurrentStage = pyCurrentStage;
    }

    public String getPyCurrentStage() 
    {
        return pyCurrentStage;
    }
    public void setPyStatusWork(String pyStatusWork) 
    {
        this.pyStatusWork = pyStatusWork;
    }

    public String getPyStatusWork() 
    {
        return pyStatusWork;
    }
    public void setPyCaseStructure(String pyCaseStructure) 
    {
        this.pyCaseStructure = pyCaseStructure;
    }

    public String getPyCaseStructure() 
    {
        return pyCaseStructure;
    }
    public void setPyWorkPage(String pyWorkPage) 
    {
        this.pyWorkPage = pyWorkPage;
    }

    public String getPyWorkPage() 
    {
        return pyWorkPage;
    }
    public void setPxCreateBy(String pxCreateBy) 
    {
        this.pxCreateBy = pxCreateBy;
    }

    public String getPxCreateBy() 
    {
        return pxCreateBy;
    }
    public void setPxCreateTime(Date pxCreateTime) 
    {
        this.pxCreateTime = pxCreateTime;
    }

    public Date getPxCreateTime() 
    {
        return pxCreateTime;
    }
    public void setPyUpdateBy(String pyUpdateBy) 
    {
        this.pyUpdateBy = pyUpdateBy;
    }

    public String getPyUpdateBy() 
    {
        return pyUpdateBy;
    }
    public void setPyUpdateTime(Date pyUpdateTime) 
    {
        this.pyUpdateTime = pyUpdateTime;
    }

    public Date getPyUpdateTime() 
    {
        return pyUpdateTime;
    }
    public void setPyDescription(String pyDescription) 
    {
        this.pyDescription = pyDescription;
    }

    public String getPyDescription() 
    {
        return pyDescription;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("pyCaseId", getPyCaseId())
            .append("pyLabel", getPyLabel())
            .append("pyName", getPyName())
            .append("caseTypeId", getCaseTypeId())
            .append("pyUrgency", getPyUrgency())
            .append("pyCurrentStage", getPyCurrentStage())
            .append("pyStatusWork", getPyStatusWork())
            .append("pyCaseStructure", getPyCaseStructure())
            .append("pyWorkPage", getPyWorkPage())
            .append("pxCreateBy", getPxCreateBy())
            .append("pxCreateTime", getPxCreateTime())
            .append("pyUpdateBy", getPyUpdateBy())
            .append("pyUpdateTime", getPyUpdateTime())
            .append("pyDescription", getPyDescription())
            .toString();
    }
}
