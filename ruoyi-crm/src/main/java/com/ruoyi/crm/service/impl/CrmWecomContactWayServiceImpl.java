package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomContactWay;
import com.ruoyi.crm.domain.dto.wecom.WecomContactWayCreateRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomContactWayUpdateRequest;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomContactWayMapper;
import com.ruoyi.crm.service.ICrmWecomContactWayService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomStateParseResult;
import com.ruoyi.crm.wecom.WecomStateParser;
import com.ruoyi.crm.wecom.WecomTencentApiClient;

@Service
public class CrmWecomContactWayServiceImpl implements ICrmWecomContactWayService
{
    /** 腾讯 add_contact_way remark/state 上限 */
    private static final int MAX_STATE_REMARK_LEN = 30;

    private static final int MAX_WELCOME_TEXT_LEN = 2048;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private CrmWecomContactWayMapper crmWecomContactWayMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    @Autowired
    private WecomStateParser wecomStateParser;

    @Override
    public JSONObject createContactWay(WecomContactWayCreateRequest req)
    {
        CrmWecomConfig cfg = crmWecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            throw new ServiceException("请先配置企业微信集成");
        }
        validateStateAndRemarkLength(req.getState(), req.getRemark());
        if (req.getState() != null && !req.getState().trim().isEmpty())
        {
            CrmWecomContactWay dup = crmWecomContactWayMapper.selectByState(req.getState().trim());
            if (dup != null)
            {
                throw new ServiceException("该 state 已存在活码");
            }
        }
        String welcomeTextStored = normalizeOptionalWelcomeText(req.getWelcomeText());

        String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
        JSONObject body = new JSONObject();
        if (req.getType() != null)
        {
            body.put("type", req.getType());
        }
        if (req.getScene() != null)
        {
            body.put("scene", req.getScene());
        }
        if (req.getRemark() != null)
        {
            body.put("remark", req.getRemark());
        }
        if (req.getState() != null)
        {
            body.put("state", req.getState());
        }
        if (req.getUser() != null && !req.getUser().isEmpty())
        {
            body.put("user", req.getUser());
        }
        if (req.getSkipVerify() != null)
        {
            body.put("skip_verify", req.getSkipVerify());
        }
        JSONObject res = wecomTencentApiClient.externalContactAddContactWay(accessToken, body);
        int err = res.getIntValue("errcode");
        if (err != 0)
        {
            throw new ServiceException("add_contact_way 失败 errcode=" + err + " errmsg=" + res.getString("errmsg"));
        }
        res.put("configId", res.getString("config_id"));
        res.put("qrCode", res.getString("qr_code"));
        res.put("state", req.getState());
        res.put("remark", req.getRemark());
        res.put("welcomeText", welcomeTextStored);

        CrmWecomContactWay row = new CrmWecomContactWay();
        row.setConfigId(res.getString("config_id"));
        row.setState(req.getState());
        row.setRemark(req.getRemark());
        row.setScene(req.getScene());
        row.setType(req.getType());
        row.setSkipVerify(req.getSkipVerify() != null ? req.getSkipVerify() : Boolean.TRUE);
        row.setUserListJson(req.getUser() != null ? JSON.toJSONString(req.getUser()) : null);
        row.setQrCode(res.getString("qr_code"));
        row.setWelcomeText(welcomeTextStored);
        WecomStateParseResult parsed = wecomStateParser.parse(req.getState());
        if (parsed.getCrmEventId() != null)
        {
            row.setCrmEventId(parsed.getCrmEventId());
        }
        row.setRawRequestJson(body.toJSONString());
        row.setRawResponseJson(res.toJSONString());
        row.setCreateTime(DateUtils.getNowDate());
        row.setCreateBy(SecurityUtils.getUsername());
        crmWecomContactWayMapper.insert(row);
        return res;
    }

    @Override
    public List<CrmWecomContactWay> selectList(CrmWecomContactWay query)
    {
        return crmWecomContactWayMapper.selectList(query);
    }

    @Override
    public CrmWecomContactWay selectById(Long id)
    {
        if (id == null)
        {
            return null;
        }
        return crmWecomContactWayMapper.selectById(id);
    }

    @Override
    public CrmWecomContactWay updateContactWay(Long id, WecomContactWayUpdateRequest req)
    {
        CrmWecomContactWay existing = crmWecomContactWayMapper.selectById(id);
        if (existing == null)
        {
            throw new ServiceException("活码不存在");
        }
        if (req == null || (req.getRemark() == null && !req.isWelcomeTextSet()))
        {
            throw new ServiceException("请提供要更新的字段");
        }
        CrmWecomContactWay upd = new CrmWecomContactWay();
        upd.setId(id);
        upd.setUpdateBy(SecurityUtils.getUsername());
        upd.setUpdateTime(DateUtils.getNowDate());
        if (req.getRemark() != null)
        {
            validateStateAndRemarkLength(null, req.getRemark());
            upd.setRemark(req.getRemark().trim());
        }
        if (req.isWelcomeTextSet())
        {
            upd.setWelcomeText(normalizeOptionalWelcomeText(req.getWelcomeText()));
        }
        crmWecomContactWayMapper.updatePartial(upd, req.isWelcomeTextSet());
        return crmWecomContactWayMapper.selectById(id);
    }

    @Override
    public void deleteContactWay(Long id)
    {
        CrmWecomContactWay existing = crmWecomContactWayMapper.selectById(id);
        if (existing == null)
        {
            throw new ServiceException("活码不存在");
        }
        CrmWecomConfig cfg = crmWecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            throw new ServiceException("请先配置企业微信集成");
        }
        String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
        JSONObject res = wecomTencentApiClient.externalContactDelContactWay(accessToken, existing.getConfigId());
        int err = res.getIntValue("errcode");
        if (err != 0)
        {
            throw new ServiceException("del_contact_way 失败 errcode=" + err + " errmsg=" + res.getString("errmsg"));
        }
        crmWecomContactWayMapper.deleteById(id);
    }

    private static void validateStateAndRemarkLength(String state, String remark)
    {
        if (state != null && !state.trim().isEmpty() && state.trim().length() > MAX_STATE_REMARK_LEN)
        {
            throw new ServiceException("state 不能超过 30 个字符");
        }
        if (remark != null && !remark.trim().isEmpty() && remark.trim().length() > MAX_STATE_REMARK_LEN)
        {
            throw new ServiceException("remark 不能超过 30 个字符");
        }
    }

    private static String normalizeOptionalWelcomeText(String welcomeText)
    {
        if (welcomeText == null)
        {
            return null;
        }
        String text = welcomeText.trim();
        if (text.isEmpty())
        {
            return null;
        }
        if (text.length() > MAX_WELCOME_TEXT_LEN)
        {
            throw new ServiceException("欢迎语长度不能超过 2048 字符");
        }
        return text;
    }
}
