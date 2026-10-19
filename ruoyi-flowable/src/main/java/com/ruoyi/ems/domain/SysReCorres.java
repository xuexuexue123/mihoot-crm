package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文档对象 sys_re_corres
 * 
 * @author luohd
 * @date 2023-05-20
 */
public class SysReCorres extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文件id */
    private String corresId;

    /** 文件类型id */
    @Excel(name = "文件类型id")
    private String category;

    /** 是否属于case */
    @Excel(name = "是否属于case")
    private String belongsCase;

    /** 文件夹id */
    @Excel(name = "文件夹id")
    private String folderId;

    /** 项目ID */
    @Excel(name = "项目ID")
    private String caseId;

    /** 任务ID */
    @Excel(name = "任务ID")
    private String taskId;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileTitle;

    /** 文件系统名称 */
    @Excel(name = "文件系统名称")
    private String fileName;

    /** 文件类别 */
    @Excel(name = "文件类别")
    private String fileType;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Long orderNum;

    /** 类型状态 */
    @Excel(name = "类型状态")
    private String status;

    /** 删除标志 */
    private String delFlag;


    private Long userId;

    private Long deptId;


    /** 简介 */
    @Excel(name = "简介")
    private String summary;

    /** 目录 */
    @Excel(name = "目录")
    private String catalog;

    public void setCorresId(String corresId) 
    {
        this.corresId = corresId;
    }

    public String getCorresId() 
    {
        return corresId;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setBelongsCase(String belongsCase) 
    {
        this.belongsCase = belongsCase;
    }

    public String getBelongsCase() 
    {
        return belongsCase;
    }
    public void setFolderId(String folderId) 
    {
        this.folderId = folderId;
    }

    public String getFolderId() 
    {
        return folderId;
    }
    public void setCaseId(String caseId) 
    {
        this.caseId = caseId;
    }

    public String getCaseId() 
    {
        return caseId;
    }
    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setFileTitle(String fileTitle) 
    {
        this.fileTitle = fileTitle;
    }

    public String getFileTitle() 
    {
        return fileTitle;
    }
    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }
    public void setFileType(String fileType) 
    {
        this.fileType = fileType;
    }

    public String getFileType() 
    {
        return fileType;
    }
    public void setFileSize(Long fileSize) 
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize() 
    {
        return fileSize;
    }
    public void setOrderNum(Long orderNum) 
    {
        this.orderNum = orderNum;
    }

    public Long getOrderNum() 
    {
        return orderNum;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getSummary()
    {
        return summary;
    }
    public void setCatalog(String catalog)
    {
        this.catalog = catalog;
    }

    public String getCatalog()
    {
        return catalog;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("corresId", getCorresId())
            .append("category", getCategory())
            .append("belongsCase", getBelongsCase())
            .append("folderId", getFolderId())
            .append("caseId", getCaseId())
            .append("taskId", getTaskId())
            .append("fileTitle", getFileTitle())
            .append("fileName", getFileName())
            .append("fileType", getFileType())
            .append("fileSize", getFileSize())
            .append("orderNum", getOrderNum())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
