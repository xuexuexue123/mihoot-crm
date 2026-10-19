package com.ruoyi.crm.wecom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import com.ruoyi.crm.wechatmp.mapper.ActivityUnmatchedMapper;
import com.ruoyi.crm.wechatmp.service.WechatMpIdentityResolutionSupport;

/**
 * 企微 bindUnionId 成功后，回填同 unionid 的公众号 unmatched Activity。
 */
@Component
public class UnmatchedActivityBackfillSupport
{
    private static final Logger log = LoggerFactory.getLogger(UnmatchedActivityBackfillSupport.class);

    @Autowired
    private ActivityUnmatchedMapper unmatchedMapper;

    @Autowired
    private WechatMpIdentityResolutionSupport mpIdentityResolutionSupport;

    /**
     * 扫描 pending unmatched 记录并挂到 Contact（用例 E）。
     */
    public void backfillForUnionId(String unionId, String contactId)
    {
        if (!StringUtils.hasText(unionId) || !StringUtils.hasText(contactId))
        {
            return;
        }
        try
        {
            List<ActivityUnmatched> pending = unmatchedMapper.selectPendingByUnionId(unionId.trim());
            if (pending == null || pending.isEmpty())
            {
                return;
            }
            for (ActivityUnmatched record : pending)
            {
                mpIdentityResolutionSupport.markUnmatchedMatched(
                        record, contactId.trim(), record.getPulseId(), false);
                log.info("WeCom unionid bind backfilled unmatched pulseId={} contactId={}",
                        record.getPulseId(), contactId);
            }
        }
        catch (Exception e)
        {
            log.warn("Unmatched backfill failed unionId={} contactId={}: {}",
                    unionId, contactId, e.getMessage());
        }
    }
}
