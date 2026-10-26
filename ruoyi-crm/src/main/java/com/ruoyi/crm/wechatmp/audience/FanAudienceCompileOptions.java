package com.ruoyi.crm.wechatmp.audience;

public class FanAudienceCompileOptions
{
    private boolean applySubscribedDefaultForSend;

    public static FanAudienceCompileOptions preview()
    {
        FanAudienceCompileOptions options = new FanAudienceCompileOptions();
        options.setApplySubscribedDefaultForSend(false);
        return options;
    }

    public static FanAudienceCompileOptions forSend()
    {
        FanAudienceCompileOptions options = new FanAudienceCompileOptions();
        options.setApplySubscribedDefaultForSend(true);
        return options;
    }

    public boolean isApplySubscribedDefaultForSend()
    {
        return applySubscribedDefaultForSend;
    }

    public void setApplySubscribedDefaultForSend(boolean applySubscribedDefaultForSend)
    {
        this.applySubscribedDefaultForSend = applySubscribedDefaultForSend;
    }
}
