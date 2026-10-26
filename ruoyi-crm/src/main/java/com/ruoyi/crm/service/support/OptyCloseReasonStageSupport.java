package com.ruoyi.crm.service.support;

import java.util.Locale;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmOpty;

/**
 * 结案原因（closeReason）→ 商机阶段（stage）映射。
 * 客户端关单时常带整单快照且 stage 仍为进行中值；以 closeReason 为准推导终态 stage。
 */
public final class OptyCloseReasonStageSupport
{
    public static final String CLOSE_REASON_WON = "closed_won";
    public static final String CLOSE_REASON_LOST = "closed_lost";
    public static final String CLOSE_REASON_SUSPENDED = "suspended";
    public static final String CLOSE_REASON_DUPLICATE = "duplicate";

    public static final String STAGE_CLOSED_WON = "closed_won";
    public static final String STAGE_CLOSED_LOST = "closed_lost";

    private OptyCloseReasonStageSupport()
    {
    }

    /**
     * 请求带有可识别的 closeReason 时，规范 closeReason 并写入对应 stage（及 forecastCategory）。
     */
    public static void applyStageFromCloseReason(CrmOpty opty)
    {
        if (opty == null || StringUtils.isEmpty(opty.getCloseReason()))
        {
            return;
        }
        String normalized = normalizeCloseReason(opty.getCloseReason());
        String stage = resolveStageFromCloseReason(normalized);
        if (stage == null)
        {
            return;
        }
        opty.setCloseReason(normalized);
        if (StringUtils.isEmpty(opty.getStage()) || !stage.equals(opty.getStage()))
        {
            opty.setStage(stage);
            if (StringUtils.isEmpty(opty.getForecastCategory()))
            {
                opty.setForecastCategory(stage);
            }
        }
    }

    /**
     * 将客户端可能传入的展示值（如 Won）规范为存库 code（closed_won）。
     */
    public static String normalizeCloseReason(String closeReason)
    {
        if (StringUtils.isEmpty(closeReason))
        {
            return closeReason;
        }
        String trimmed = closeReason.trim();
        switch (trimmed.toLowerCase(Locale.ROOT))
        {
            case "won":
                return CLOSE_REASON_WON;
            case "lost":
            case "lose":
                return CLOSE_REASON_LOST;
            case "closed_won":
                return CLOSE_REASON_WON;
            case "closed_lost":
                return CLOSE_REASON_LOST;
            case "suspended":
                return CLOSE_REASON_SUSPENDED;
            case "duplicate":
                return CLOSE_REASON_DUPLICATE;
            default:
                return trimmed;
        }
    }

    /**
     * @return 对应 stage code，无法识别时返回 null
     */
    public static String resolveStageFromCloseReason(String closeReason)
    {
        if (StringUtils.isEmpty(closeReason))
        {
            return null;
        }
        String normalized = normalizeCloseReason(closeReason);
        switch (normalized)
        {
            case CLOSE_REASON_WON:
                return STAGE_CLOSED_WON;
            case CLOSE_REASON_LOST:
            case CLOSE_REASON_SUSPENDED:
            case CLOSE_REASON_DUPLICATE:
                return STAGE_CLOSED_LOST;
            default:
                return null;
        }
    }
}
