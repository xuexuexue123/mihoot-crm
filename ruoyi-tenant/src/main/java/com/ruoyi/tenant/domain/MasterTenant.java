package com.ruoyi.tenant.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 租户管理对象 master_tenant
 *
 * @author luohd
 * @date 2024-01-05
 */
public class MasterTenant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenant;

    /** 数据库连接URL */
    @Excel(name = "数据库连接URL")
    private String url;

    /** 用户名 */
    @Excel(name = "用户名")
    private String username;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 数据库名 */
    @Excel(name = "数据库名")
    private String databaseName;

    /** 数据库主机名 */
    @Excel(name = "数据库主机名")
    private String hostName;

    /** 状态(1正常 2停止) */
    @Excel(name = "状态(1正常 2停止)")
    private String status;

    /** 到期日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "到期日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expirationDate;

    /** 公开子域名 slug */
    private String websiteSlug;

    /** 站点状态 active / suspended */
    private String websiteStatus;

    /** 子域记录创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date websiteCreatedAt;

    /** 注册公司名（公开 API 展示） */
    private String companyName;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public String getTenant()
    {
        return tenant;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
    public void setDatabaseName(String databaseName)
    {
        this.databaseName = databaseName;
    }

    public String getDatabaseName()
    {
        return databaseName;
    }
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    public String getHostName()
    {
        return hostName;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setExpirationDate(Date expirationDate)
    {
        this.expirationDate = expirationDate;
    }

    public Date getExpirationDate()
    {
        return expirationDate;
    }

    public void setWebsiteSlug(String websiteSlug)
    {
        this.websiteSlug = websiteSlug;
    }

    public String getWebsiteSlug()
    {
        return websiteSlug;
    }

    public void setWebsiteStatus(String websiteStatus)
    {
        this.websiteStatus = websiteStatus;
    }

    public String getWebsiteStatus()
    {
        return websiteStatus;
    }

    public void setWebsiteCreatedAt(Date websiteCreatedAt)
    {
        this.websiteCreatedAt = websiteCreatedAt;
    }

    public Date getWebsiteCreatedAt()
    {
        return websiteCreatedAt;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("tenant", getTenant())
                .append("url", getUrl())
                .append("username", getUsername())
                .append("password", getPassword())
                .append("databaseName", getDatabaseName())
                .append("hostName", getHostName())
                .append("createTime", getCreateTime())
                .append("status", getStatus())
                .append("expirationDate", getExpirationDate())
                .append("websiteSlug", getWebsiteSlug())
                .append("websiteStatus", getWebsiteStatus())
                .append("websiteCreatedAt", getWebsiteCreatedAt())
                .append("companyName", getCompanyName())
                .toString();
    }
}
