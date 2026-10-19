package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文件信息对象 sys_ems_corres
 * 
 * @author luohd
 * @date 2022-11-30
 */
public class SysEmsCorres extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文件id */
    private Long corresId;

    /** 文件类型 */
    @Excel(name = "文件类型")
    private String category;

    /** 是否属于 */
    @Excel(name = "是否属于")
    private String isbelongtocase;

    /** 文件夹id */
    @Excel(name = "文件夹id")
    private Long folderId;

    /** case id */
    @Excel(name = "case id")
    private String caseId;

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

    public void setCorresId(Long corresId) 
    {
        this.corresId = corresId;
    }

    public Long getCorresId() 
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
    public void setIsbelongtocase(String isbelongtocase) 
    {
        this.isbelongtocase = isbelongtocase;
    }

    public String getIsbelongtocase() 
    {
        return isbelongtocase;
    }
    public void setFolderId(Long folderId) 
    {
        this.folderId = folderId;
    }

    public Long getFolderId() 
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("corresId", getCorresId())
            .append("category", getCategory())
            .append("isbelongtocase", getIsbelongtocase())
            .append("folderId", getFolderId())
            .append("caseId", getCaseId())
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
