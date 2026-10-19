package com.ruoyi.crm.wecom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomContactWay;
import com.ruoyi.crm.domain.CrmWecomWelcomeLog;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomContactWayMapper;
import com.ruoyi.crm.mapper.CrmWecomWelcomeLogMapper;

/**
 * 新客户欢迎语：文案解析、腾讯 send_welcome_msg、审计日志。
 */
@Service
public class WecomWelcomeService
{
    private static final Logger log = LoggerFactory.getLogger(WecomWelcomeService.class);

    private static final int MAX_TEXT_LEN = 2048;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private CrmWecomContactWayMapper crmWecomContactWayMapper;

    @Autowired
    private CrmWecomWelcomeLogMapper crmWecomWelcomeLogMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    /**
     * 回调解密后的明文 XML：在落库外部联系人之前同步发送欢迎语。
     */
    public void handleFromCallbackXml(String plainXml)
    {
        if (plainXml == null || plainXml.isEmpty())
        {
            return;
        }
        String event = getCdataOrText(plainXml, "Event");
        if (event == null || !"change_external_contact".equalsIgnoreCase(event.trim()))
        {
            return;
        }
        String changeType = getCdataOrText(plainXml, "ChangeType");
        if (changeType == null)
        {
            return;
        }
        String ct = changeType.trim();
        if (!"add_external_contact".equalsIgnoreCase(ct) && !"add_half_external_contact".equalsIgnoreCase(ct))
        {
            return;
        }

        String externalUserId = firstNonEmpty(
                getCdataOrText(plainXml, "ExternalUserID"),
                getCdataOrText(plainXml, "ExternalUserId"));
        String userId = getCdataOrText(plainXml, "UserID");
        String state = getCdataOrText(plainXml, "State");
        String welcomeCode = firstNonEmpty(
                getCdataOrText(plainXml, "WelcomeCode"),
                getCdataOrText(plainXml, "Welcomecode"));

        CrmWecomConfig cfg = crmWecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            writeLog(externalUserId, userId, state, null, null, null, null, "disabled");
            return;
        }

        if (!Boolean.TRUE.equals(cfg.getWelcomeEnabled()))
        {
            writeLog(externalUserId, userId, state, null, null, null, null, "disabled");
            return;
        }
        if (welcomeCode == null || welcomeCode.trim().isEmpty())
        {
            log.info("WeCom welcome skip: no WelcomeCode externalUserId={} state={}", externalUserId, state);
            writeLog(externalUserId, userId, state, null, null, null, null, "no_welcome_code");
            return;
        }

        WelcomeTextResolved resolved = resolveWelcomeText(cfg, state);
        if (resolved.text == null || resolved.text.isEmpty())
        {
            writeLog(externalUserId, userId, state, null, null, null, null, "empty_text");
            return;
        }

        try
        {
            String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
            JSONObject res = wecomTencentApiClient.sendWelcomeMsg(accessToken, welcomeCode.trim(), resolved.text);
            int errcode = res.getIntValue("errcode");
            String errmsg = res.getString("errmsg");
            writeLog(externalUserId, userId, state, resolved.text, resolved.textSource, errcode, errmsg, null);
            if (errcode != 0)
            {
                log.warn("WeCom send_welcome_msg errcode={} errmsg={} externalUserId={}", errcode, errmsg, externalUserId);
            }
        }
        catch (Exception e)
        {
            log.warn("WeCom send_welcome_msg failed externalUserId={}: {}", externalUserId, e.getMessage());
            writeLog(externalUserId, userId, state, resolved.text, resolved.textSource, null,
                    truncate(e.getMessage(), 500), "api_error");
        }
    }

    WelcomeTextResolved resolveWelcomeText(CrmWecomConfig cfg, String state)
    {
        if (cfg == null || !Boolean.TRUE.equals(cfg.getWelcomeEnabled()))
        {
            return WelcomeTextResolved.empty();
        }
        if (state != null && !state.trim().isEmpty())
        {
            CrmWecomContactWay cw = crmWecomContactWayMapper.selectByState(state.trim());
            if (cw != null && cw.getWelcomeText() != null && !cw.getWelcomeText().trim().isEmpty())
            {
                return new WelcomeTextResolved(truncate(cw.getWelcomeText().trim(), MAX_TEXT_LEN), "contact_way");
            }
        }
        if (cfg.getWelcomeText() != null && !cfg.getWelcomeText().trim().isEmpty())
        {
            return new WelcomeTextResolved(truncate(cfg.getWelcomeText().trim(), MAX_TEXT_LEN), "tenant");
        }
        return WelcomeTextResolved.empty();
    }

    private void writeLog(String externalUserid, String followUserid, String state, String textUsed,
            String textSource, Integer errcode, String errmsg, String skippedReason)
    {
        CrmWecomWelcomeLog row = new CrmWecomWelcomeLog();
        row.setExternalUserid(externalUserid != null ? externalUserid : "");
        row.setFollowUserid(followUserid);
        row.setState(state);
        row.setWelcomeTextUsed(textUsed != null ? truncate(textUsed, MAX_TEXT_LEN) : null);
        row.setTextSource(textSource);
        row.setErrcode(errcode);
        row.setErrmsg(errmsg);
        row.setSkippedReason(skippedReason);
        row.setCreateTime(DateUtils.getNowDate());
        crmWecomWelcomeLogMapper.insert(row);
    }

    private static String getCdataOrText(String xml, String tag)
    {
        Pattern p = Pattern.compile(
                "<" + tag + "><!\\[CDATA\\[(.*?)\\]\\]></" + tag + ">|<" + tag + ">([^<]*)</" + tag + ">",
                Pattern.DOTALL);
        Matcher m = p.matcher(xml);
        if (m.find())
        {
            String v = m.group(1) != null ? m.group(1) : m.group(2);
            return v != null ? v.trim() : null;
        }
        return null;
    }

    private static String firstNonEmpty(String a, String b)
    {
        if (a != null && !a.isEmpty())
        {
            return a.trim();
        }
        if (b != null && !b.isEmpty())
        {
            return b.trim();
        }
        return null;
    }

    private static String truncate(String s, int max)
    {
        if (s == null)
        {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }

    static final class WelcomeTextResolved
    {
        final String text;
        final String textSource;

        WelcomeTextResolved(String text, String textSource)
        {
            this.text = text;
            this.textSource = textSource;
        }

        static WelcomeTextResolved empty()
        {
            return new WelcomeTextResolved(null, null);
        }
    }
}
