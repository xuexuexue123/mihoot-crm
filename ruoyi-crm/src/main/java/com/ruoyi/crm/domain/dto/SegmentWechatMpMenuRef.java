package com.ruoyi.crm.domain.dto;

/**
 * 分群规则：公众号菜单引用（field = wechatMpMenuArticleViewed）
 */
public class SegmentWechatMpMenuRef
{
    private String appId;

    private String menuKey;

    private String menuName;

    private String menuPath;

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getMenuKey()
    {
        return menuKey;
    }

    public void setMenuKey(String menuKey)
    {
        this.menuKey = menuKey;
    }

    public String getMenuName()
    {
        return menuName;
    }

    public void setMenuName(String menuName)
    {
        this.menuName = menuName;
    }

    public String getMenuPath()
    {
        return menuPath;
    }

    public void setMenuPath(String menuPath)
    {
        this.menuPath = menuPath;
    }
}
