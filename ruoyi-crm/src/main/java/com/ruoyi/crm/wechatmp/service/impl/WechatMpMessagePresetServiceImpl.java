package com.ruoyi.crm.wechatmp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService;
import com.ruoyi.crm.service.support.SurveyOutreachUrlService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.service.support.TenantPublicAppUrlResolver;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderResult;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMessagePresetMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;
import com.ruoyi.crm.wechatmp.support.WechatMpMessagePresetRenderSupport;

@Service
public class WechatMpMessagePresetServiceImpl implements IWechatMpMessagePresetService
{
    @Autowired
    private CrmWechatMpMessagePresetMapper presetMapper;

    @Autowired
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    @Autowired
    private SurveyOutreachUrlService surveyOutreachUrlService;

    @Autowired
    private TenantPublicAppUrlResolver tenantPublicAppUrlResolver;

    /** 无租户上下文时的 H5 基址回退 */
    @Value("${record-trigger.next-app-url:http://localhost:3000}")
    private String nextAppUrl;

    @Override
    public List<CrmWechatMpMessagePreset> selectList(CrmWechatMpMessagePreset query)
    {
        return presetMapper.selectList(query);
    }

    @Override
    public CrmWechatMpMessagePreset getById(Long id)
    {
        CrmWechatMpMessagePreset row = presetMapper.selectById(id);
        if (row == null)
        {
            throw new ServiceException("预设不存在");
        }
        return row;
    }

    @Override
    public void add(CrmWechatMpMessagePreset row)
    {
        validateForWrite(row);
        if (row.getStatus() == null)
        {
            row.setStatus("0");
        }
        row.setCreateBy(SecurityUtils.getUsername());
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateTime(DateUtils.getNowDate());
        presetMapper.insert(row);
    }

    @Override
    public CrmWechatMpMessagePreset update(Long id, CrmWechatMpMessagePreset row)
    {
        if (presetMapper.selectById(id) == null)
        {
            throw new ServiceException("预设不存在");
        }
        row.setId(id);
        validateForWrite(row);
        row.setUpdateBy(SecurityUtils.getUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        int rows = presetMapper.update(row);
        if (rows <= 0)
        {
            throw new ServiceException("更新失败，未写入任何记录");
        }
        return getById(id);
    }

    @Override
    public void delete(Long id)
    {
        if (presetMapper.selectById(id) == null)
        {
            throw new ServiceException("预设不存在");
        }
        presetMapper.deleteById(id);
    }

    @Override
    public WechatMpMessagePresetRenderResult render(Long id, WechatMpMessagePresetRenderRequest request)
    {
        CrmWechatMpMessagePreset preset = getById(id);
        if (!"0".equals(preset.getStatus()))
        {
            throw new ServiceException("预设已停用");
        }
        if (request == null)
        {
            request = new WechatMpMessagePresetRenderRequest();
        }
        validateOpenIdAppIdPair(request);
        validateEventSurveyMutualExclusion(request);

        Map<String, String> vars = buildVariableMap(request);
        String renderedUrl = WechatMpMessagePresetRenderSupport.substitute(preset.getUrlTemplate(), vars);
        JSONObject renderedData = WechatMpMessagePresetRenderSupport.substituteDataJson(
                JSONObject.parseObject(preset.getDataJsonRaw()), vars);

        WechatMpMessagePresetRenderSupport.validateRenderedData(renderedData);

        WechatMpMessagePresetRenderResult result = new WechatMpMessagePresetRenderResult();
        result.setAppId(preset.getAppId());
        result.setTemplateId(preset.getWechatTemplateId());
        result.setUrl(renderedUrl);
        result.setData(renderedData);
        return result;
    }

    private void validateForWrite(CrmWechatMpMessagePreset row)
    {
        if (!StringUtils.hasText(row.getPresetName()))
        {
            throw new ServiceException("presetName 不能为空");
        }
        if (!StringUtils.hasText(row.getAppId()))
        {
            throw new ServiceException("appId 不能为空");
        }
        if (!StringUtils.hasText(row.getWechatTemplateId()))
        {
            throw new ServiceException("wechatTemplateId 不能为空");
        }
        if (wechatMpConfigMapper.selectByAppId(row.getAppId()) == null)
        {
            throw new ServiceException("公众号配置不存在: appId=" + row.getAppId());
        }
        JSONObject dataJson = row.getDataJson();
        if (dataJson == null || dataJson.isEmpty())
        {
            throw new ServiceException("dataJson 不能为空");
        }
        validateDataJsonStructure(dataJson);
    }

    private void validateDataJsonStructure(JSONObject dataJson)
    {
        WechatMpMessagePresetRenderSupport.validateDataJsonStructure(dataJson);
    }

    private Map<String, String> buildVariableMap(WechatMpMessagePresetRenderRequest request)
    {
        Map<String, String> vars = new HashMap<>();
        String eventId = request.getEventId();
        CrmEvent event = null;
        if (StringUtils.hasText(eventId))
        {
            event = crmEventMapper.selectCrmEventByEventId(eventId);
            if (event == null)
            {
                throw new ServiceException("活动不存在");
            }
            vars.put("eventId", eventId);
            vars.put("eventName", event.getSubject() != null ? event.getSubject() : "");
        }
        else
        {
            vars.put("eventId", "");
            vars.put("eventName", "");
        }

        String tenantId = resolveTenantId(request);
        String baseUrl = resolveBaseUrl(tenantId);
        vars.put("baseUrl", baseUrl);

        String registrationUrl = resolveRegistrationUrl(request, event);
        vars.put("registrationUrl", registrationUrl);

        String surveyUrl = resolveSurveyUrl(request);
        if (!StringUtils.hasText(surveyUrl) && StringUtils.hasText(registrationUrl))
        {
            surveyUrl = registrationUrl;
        }
        vars.put("surveyUrl", surveyUrl);

        if (request.getExtra() != null)
        {
            for (Map.Entry<String, String> e : request.getExtra().entrySet())
            {
                vars.put(e.getKey(), e.getValue() != null ? e.getValue() : "");
            }
        }
        return vars;
    }

    private String resolveBaseUrl(String tenantId)
    {
        if (StringUtils.hasText(tenantId))
        {
            String tenantBase = tenantPublicAppUrlResolver.resolve(tenantId);
            if (StringUtils.hasText(tenantBase))
            {
                return tenantBase;
            }
        }
        return normalizeBaseUrl(nextAppUrl);
    }

    private String resolveSurveyUrl(WechatMpMessagePresetRenderRequest request)
    {
        if (!StringUtils.hasText(request.getSurveyId()))
        {
            return "";
        }
        String tenantId = resolveTenantId(request);
        if (!StringUtils.hasText(tenantId))
        {
            return "";
        }
        return surveyOutreachUrlService.issueUrlForPreset(
                tenantId,
                request.getSurveyId(),
                request.getOpenId(),
                request.getAppId(),
                request.getContactId()).getSurveyUrl();
    }

    private void validateEventSurveyMutualExclusion(WechatMpMessagePresetRenderRequest request)
    {
        if (StringUtils.hasText(request.getEventId()) && StringUtils.hasText(request.getSurveyId()))
        {
            throw new ServiceException("eventId 与 surveyId 不能同时传入", HttpStatus.BAD_REQUEST);
        }
    }

    private String resolveRegistrationUrl(WechatMpMessagePresetRenderRequest request, CrmEvent event)
    {
        if (event == null || !outreachRegistrationTokenService.isSigningConfigured())
        {
            return "";
        }
        String tenantId = resolveTenantId(request);
        if (!StringUtils.hasText(tenantId) || !StringUtils.hasText(event.getEventId()))
        {
            return "";
        }
        long exp = outreachRegistrationTokenService.computeExp(event);
        String openId = request.getOpenId();
        String appId = request.getAppId();
        if (StringUtils.hasText(openId) && StringUtils.hasText(appId))
        {
            return outreachRegistrationTokenService.buildRegistrationUrl(
                    tenantId, event.getEventId(), exp, openId, appId);
        }
        return outreachRegistrationTokenService.buildRegistrationUrl(tenantId, event.getEventId(), exp);
    }

    private void validateOpenIdAppIdPair(WechatMpMessagePresetRenderRequest request)
    {
        boolean hasOpenId = StringUtils.hasText(request.getOpenId());
        boolean hasAppId = StringUtils.hasText(request.getAppId());
        if (hasOpenId != hasAppId)
        {
            throw new ServiceException("openId 与 appId 必须同时提供或同时省略");
        }
    }

    private String resolveTenantId(WechatMpMessagePresetRenderRequest request)
    {
        if (StringUtils.hasText(request.getTenantId()))
        {
            return request.getTenantId();
        }
        String fromDs = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.hasText(fromDs))
        {
            return fromDs;
        }
        try
        {
            return SecurityUtils.getLoginUser().getTenant();
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private static String firstNonBlank(String... values)
    {
        if (values == null)
        {
            return "";
        }
        for (String value : values)
        {
            if (StringUtils.hasText(value))
            {
                return value;
            }
        }
        return "";
    }

    private static String normalizeBaseUrl(String url)
    {
        if (!StringUtils.hasText(url))
        {
            return "";
        }
        return url.trim().replaceAll("/+$", "");
    }
}
