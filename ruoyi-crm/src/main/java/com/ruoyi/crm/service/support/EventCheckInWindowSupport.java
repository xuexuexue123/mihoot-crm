package com.ruoyi.crm.service.support;

import java.util.Calendar;
import java.util.Date;

import com.ruoyi.crm.domain.CrmEventSession;

/**
 * 签到窗口：场次未配置时使用 startTime-2h 至 endTime+1h
 */
public final class EventCheckInWindowSupport
{
    private static final int OPEN_HOURS_BEFORE = 2;
    private static final int CLOSE_HOURS_AFTER = 1;

    private EventCheckInWindowSupport()
    {
    }

    public static Date resolveOpenAt(CrmEventSession session)
    {
        if (session.getCheckInOpenAt() != null)
        {
            return session.getCheckInOpenAt();
        }
        if (session.getStartTime() == null)
        {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(session.getStartTime());
        cal.add(Calendar.HOUR_OF_DAY, -OPEN_HOURS_BEFORE);
        return cal.getTime();
    }

    public static Date resolveCloseAt(CrmEventSession session)
    {
        if (session.getCheckInCloseAt() != null)
        {
            return session.getCheckInCloseAt();
        }
        if (session.getEndTime() == null)
        {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(session.getEndTime());
        cal.add(Calendar.HOUR_OF_DAY, CLOSE_HOURS_AFTER);
        return cal.getTime();
    }

    public static boolean isWithinWindow(CrmEventSession session, Date now)
    {
        if (now == null)
        {
            now = new Date();
        }
        Date open = resolveOpenAt(session);
        Date close = resolveCloseAt(session);
        if (open != null && now.before(open))
        {
            return false;
        }
        if (close != null && now.after(close))
        {
            return false;
        }
        return true;
    }
}
