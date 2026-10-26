package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactImportResult;
import com.ruoyi.crm.domain.dto.wecom.WecomMyCustomersCrmLink;
import com.ruoyi.crm.domain.dto.wecom.WecomMyCustomersDetailItem;
import com.ruoyi.crm.domain.dto.wecom.WecomMyCustomersDetailRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMyCustomersDetailResult;
import com.ruoyi.crm.domain.dto.wecom.WecomMyCustomersListResult;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.service.ICrmWecomExternalContactService;
import com.ruoyi.crm.service.ICrmWecomMyCustomersService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomTencentApiClient;

@Service
public class CrmWecomMyCustomersServiceImpl implements ICrmWecomMyCustomersService
{
    private static final int MAX_DETAIL_BATCH = 20;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    @Autowired
    private CrmWecomExternalContactMapper wecomExternalContactMapper;

    @Autowired
    private ICrmWecomExternalContactService wecomExternalContactService;

    @Override
    public WecomMyCustomersListResult listMyCustomers()
    {
        String snUserid = requireCurrentSnUserid();
        CrmWecomConfig cfg = requireWecomConfig();
        String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);

        JSONObject tencentRes = wecomTencentApiClient.externalContactList(accessToken, snUserid);
        WecomMyCustomersListResult result = new WecomMyCustomersListResult();
        if (tencentRes == null)
        {
            result.setErrcode(-1);
            result.setErrmsg("empty response from wecom");
            return result;
        }

        result.setErrcode(tencentRes.getInteger("errcode"));
        result.setErrmsg(tencentRes.getString("errmsg"));
        JSONArray ids = tencentRes.getJSONArray("external_userid");
        List<String> externalUserids = new ArrayList<>();
        if (ids != null)
        {
            for (int i = 0; i < ids.size(); i++)
            {
                String id = ids.getString(i);
                if (StringUtils.isNotEmpty(id))
                {
                    externalUserids.add(id);
                }
            }
        }
        result.setExternalUserid(externalUserids);
        result.setCrmLinks(buildCrmLinks(externalUserids, snUserid));
        return result;
    }

    @Override
    public WecomMyCustomersDetailResult getCustomerDetails(WecomMyCustomersDetailRequest request)
    {
        if (request == null || request.getExternalUseridList() == null
                || request.getExternalUseridList().isEmpty())
        {
            throw new ServiceException("external_userid_list must not be empty");
        }
        if (request.getExternalUseridList().size() > MAX_DETAIL_BATCH)
        {
            throw new ServiceException("external_userid_list exceeds max " + MAX_DETAIL_BATCH);
        }

        WecomMyCustomersListResult allowed = listMyCustomers();
        Set<String> allowedSet = new HashSet<>(allowed.getExternalUserid());

        String snUserid = requireCurrentSnUserid();
        CrmWecomConfig cfg = requireWecomConfig();
        String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);

        Map<String, WecomMyCustomersCrmLink> crmLinks = buildCrmLinks(
                request.getExternalUseridList(), snUserid);

        WecomMyCustomersDetailResult result = new WecomMyCustomersDetailResult();
        for (String externalUserid : request.getExternalUseridList())
        {
            WecomMyCustomersDetailItem item = new WecomMyCustomersDetailItem();
            item.setExternalUserid(externalUserid);
            if (!allowedSet.contains(externalUserid))
            {
                item.setErrcode(403);
                item.setErrmsg("external_userid not in current user customer list");
                result.getCustomers().add(item);
                continue;
            }

            JSONObject detailRes = wecomTencentApiClient.externalContactGet(accessToken, externalUserid);
            if (detailRes != null)
            {
                item.setErrcode(detailRes.getInteger("errcode"));
                item.setErrmsg(detailRes.getString("errmsg"));
                item.setDetail(detailRes);
            }
            WecomMyCustomersCrmLink link = crmLinks.get(externalUserid);
            if (link != null)
            {
                item.setCrmContactId(link.getCrmContactId());
                item.setCrmLeadId(link.getCrmLeadId());
            }
            result.getCustomers().add(item);
        }
        return result;
    }

    @Override
    public WecomExternalContactImportResult importMine()
    {
        String snUserid = requireCurrentSnUserid();
        List<String> userids = new ArrayList<>();
        userids.add(snUserid);
        return wecomExternalContactService.importFromWecom(userids);
    }

    private Map<String, WecomMyCustomersCrmLink> buildCrmLinks(List<String> externalUserids, String followUserid)
    {
        Map<String, WecomMyCustomersCrmLink> links = new HashMap<>();
        if (externalUserids == null || externalUserids.isEmpty())
        {
            return links;
        }

        List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectByExternalUserids(externalUserids);
        if (rows == null)
        {
            return links;
        }

        for (CrmWecomExternalContact row : rows)
        {
            if (row.getExternalUserid() == null)
            {
                continue;
            }
            if (followUserid != null && row.getFollowUserid() != null
                    && !followUserid.equals(row.getFollowUserid()))
            {
                continue;
            }
            WecomMyCustomersCrmLink link = links.computeIfAbsent(
                    row.getExternalUserid(), k -> new WecomMyCustomersCrmLink());
            if (row.getCrmContactId() != null && !row.getCrmContactId().isEmpty())
            {
                link.setCrmContactId(row.getCrmContactId());
            }
            if (row.getCrmLeadId() != null && !row.getCrmLeadId().isEmpty())
            {
                link.setCrmLeadId(row.getCrmLeadId());
            }
        }
        return links;
    }

    private static String requireCurrentSnUserid()
    {
        try
        {
            String snUserid = SecurityUtils.getLoginUser().getUser().getSnUserid();
            if (StringUtils.isEmpty(snUserid))
            {
                throw new ServiceException("当前用户未绑定企微 userid（sn_userid）");
            }
            return snUserid;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("无法获取当前登录用户");
        }
    }

    private CrmWecomConfig requireWecomConfig()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            throw new ServiceException("wecom config not found for tenant");
        }
        return cfg;
    }
}
