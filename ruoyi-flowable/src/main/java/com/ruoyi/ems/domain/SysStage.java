package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * stage定义对象 sys_re_stage
 * 
 * @author luohd
 * @date 2022-12-13
 */
public class SysStage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** stage编码 */
    private Long stageId;

    /** 排序 */
    @Excel(name = "排序")
    private Long stageSort;

    /** 标签 */
    @Excel(name = "标签")
    private String stageLabel;

    /** stage名称 */
    @Excel(name = "stage名称")
    private String stageName;

    /** stage类型 */
    @Excel(name = "stage类型")
    private String stageType;

    /** case_type_id */
    @Excel(name = "case_type_id")
    private String caseTypeId;

    /** skipStage */
    @Excel(name = "skipStage")
    private String skipStage;

    /** 跳过条件 */
    @Excel(name = "跳过条件")
    private String skipCondition;

    /** 完成后行为 */
    @Excel(name = "完成后行为")
    private String actionAfterAllCompleted;

    /** stage状态 */
    @Excel(name = "stage状态")
    private String stageStatusWork;

    /** 流程定义信息 */
    private List<SysProcess> sysProcessList;

    public void setStageId(Long stageId) 
    {
        this.stageId = stageId;
    }

    public Long getStageId() 
    {
        return stageId;
    }
    public void setStageSort(Long stageSort) 
    {
        this.stageSort = stageSort;
    }

    public Long getStageSort() 
    {
        return stageSort;
    }
    public void setStageLabel(String stageLabel) 
    {
        this.stageLabel = stageLabel;
    }

    public String getStageLabel() 
    {
        return stageLabel;
    }
    public void setStageName(String stageName) 
    {
        this.stageName = stageName;
    }

    public String getStageName() 
    {
        return stageName;
    }
    public void setStageType(String stageType) 
    {
        this.stageType = stageType;
    }

    public String getStageType() 
    {
        return stageType;
    }
    public void setCaseTypeId(String caseTypeId) 
    {
        this.caseTypeId = caseTypeId;
    }

    public String getCaseTypeId() 
    {
        return caseTypeId;
    }
    public void setSkipStage(String skipStage) 
    {
        this.skipStage = skipStage;
    }

    public String getSkipStage() 
    {
        return skipStage;
    }
    public void setSkipCondition(String skipCondition) 
    {
        this.skipCondition = skipCondition;
    }

    public String getSkipCondition() 
    {
        return skipCondition;
    }
    public void setActionAfterAllCompleted(String actionAfterAllCompleted) 
    {
        this.actionAfterAllCompleted = actionAfterAllCompleted;
    }

    public String getActionAfterAllCompleted() 
    {
        return actionAfterAllCompleted;
    }
    public void setStageStatusWork(String stageStatusWork) 
    {
        this.stageStatusWork = stageStatusWork;
    }

    public String getStageStatusWork() 
    {
        return stageStatusWork;
    }

    public List<SysProcess> getSysProcessList()
    {
        return sysProcessList;
    }

    public void setSysProcessList(List<SysProcess> sysProcessList)
    {
        this.sysProcessList = sysProcessList;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("stageId", getStageId())
            .append("stageSort", getStageSort())
            .append("stageLabel", getStageLabel())
            .append("stageName", getStageName())
            .append("stageType", getStageType())
            .append("caseTypeId", getCaseTypeId())
            .append("skipStage", getSkipStage())
            .append("skipCondition", getSkipCondition())
            .append("actionAfterAllCompleted", getActionAfterAllCompleted())
            .append("stageStatusWork", getStageStatusWork())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
                .append("sysProcessList", getSysProcessList())
            .toString();
    }
}
