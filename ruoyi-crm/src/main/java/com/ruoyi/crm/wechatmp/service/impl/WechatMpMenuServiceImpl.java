package com.ruoyi.crm.wechatmp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMenu;
import com.ruoyi.crm.wechatmp.dto.WechatMpMenuDraftRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMenuPublishRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMenuMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpQrcodeMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpMenuService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.crm.wechatmp.support.WechatMpMenuTemplates;

@Service
public class WechatMpMenuServiceImpl implements IWechatMpMenuService
{
    @Autowired
    private CrmWechatMpMenuMapper menuMapper;

    @Autowired
    private CrmWechatMpQrcodeMapper qrcodeMapper;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Override
    public List<Map<String, Object>> listTemplates()
    {
        return WechatMpMenuTemplates.listTemplates();
    }

    @Override
    public Map<String, Object> getMenuState(String appId)
    {
        Map<String, Object> data = new HashMap<>();
        CrmWechatMpMenu draft = menuMapper.selectDraftByAppId(appId);
        CrmWechatMpMenu published = menuMapper.selectLatestPublished(appId);
        data.put("draft", draft);
        data.put("published", published);
        try
        {
            String token = tokenService.getAccessToken(appId);
            JSONObject remote = tencentApiClient.getMenu(token);
            if (remote != null && !remote.containsKey("errcode"))
            {
                data.put("remoteMenu", remote);
            }
        }
        catch (Exception e)
        {
            data.put("remoteMenu", null);
        }
        return data;
    }

    @Override
    public void saveDraft(WechatMpMenuDraftRequest req)
    {
        if (req.getAppId() == null || req.getMenuJson() == null)
        {
            throw new ServiceException("appId 和 menuJson 不能为空");
        }
        WechatMpMenuTemplates.validateMenuJson(req.getMenuJson());
        menuMapper.deleteDraftByAppId(req.getAppId());
        CrmWechatMpMenu row = new CrmWechatMpMenu();
        row.setAppId(req.getAppId());
        row.setTemplateId(req.getTemplateId());
        row.setMenuJson(req.getMenuJson());
        row.setStatus("draft");
        row.setVersion(1);
        row.setCreateBy(SecurityUtils.getUsername());
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateTime(DateUtils.getNowDate());
        menuMapper.insert(row);
    }

    @Override
    public void publish(WechatMpMenuPublishRequest req)
    {
        CrmWechatMpMenu draft = req.getDraftId() != null
                ? menuMapper.selectById(req.getDraftId())
                : menuMapper.selectDraftByAppId(req.getAppId());
        if (draft == null)
        {
            throw new ServiceException("菜单草稿不存在");
        }
        WechatMpMenuTemplates.validateMenuJson(draft.getMenuJson());
        JSONObject body = JSON.parseObject(draft.getMenuJson());
        String token = tokenService.getAccessToken(draft.getAppId());
        JSONObject res = tencentApiClient.createMenu(token, body);
        if (res != null && res.containsKey("errcode") && res.getIntValue("errcode") != 0)
        {
            if (tencentApiClient.isTokenInvalid(res))
            {
                tokenService.invalidateCache(draft.getAppId());
            }
            throw new ServiceException("menu/create 失败 errcode=" + res.getIntValue("errcode")
                    + " errmsg=" + res.getString("errmsg"));
        }
        CrmWechatMpMenu published = menuMapper.selectLatestPublished(draft.getAppId());
        int nextVersion = published != null ? published.getVersion() + 1 : 1;
        draft.setVersion(nextVersion);
        draft.setStatus("published");
        draft.setPublishedAt(DateUtils.getNowDate());
        draft.setPublishedBy(SecurityUtils.getUsername());
        draft.setUpdateTime(DateUtils.getNowDate());
        menuMapper.markPublished(draft.getId(), SecurityUtils.getUsername());
        WechatMpMenuTemplates.syncMenuKeyLabels(draft.getAppId(), draft.getMenuJson(), qrcodeMapper);
    }
}
