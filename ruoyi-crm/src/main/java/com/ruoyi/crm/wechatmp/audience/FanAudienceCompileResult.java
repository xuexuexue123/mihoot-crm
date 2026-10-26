package com.ruoyi.crm.wechatmp.audience;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class FanAudienceCompileResult
{
    private final Set<String> openIds;
    private final int skipCount;

    public FanAudienceCompileResult(Set<String> openIds, int skipCount)
    {
        this.openIds = openIds == null ? Collections.emptySet() : new LinkedHashSet<>(openIds);
        this.skipCount = skipCount;
    }

    public Set<String> getOpenIds()
    {
        return Collections.unmodifiableSet(openIds);
    }

    public int getSkipCount()
    {
        return skipCount;
    }
}
