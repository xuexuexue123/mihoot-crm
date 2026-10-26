package com.ruoyi.crm.service.impl;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.mapper.CrmRespondentsMapper;
import com.ruoyi.crm.domain.CrmRespondents;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmRespondentsService;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;

/**
 * 受访者Service业务层处理
 * 
 * @author luohd
 * @date 2025-03-29
 */
@Service
public class CrmRespondentsServiceImpl implements ICrmRespondentsService 
{
    private static final Logger log = LoggerFactory.getLogger(CrmRespondentsServiceImpl.class);

    @Autowired
    private CrmRespondentsMapper crmRespondentsMapper;

    @Autowired
    private WebsiteVisitorSessionService visitorSessionService;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    /**
     * 查询受访者
     * 
     * @param id 受访者主键
     * @return 受访者
     */
    @Override
    public CrmRespondents selectCrmRespondentsById(String id)
    {
        return crmRespondentsMapper.selectCrmRespondentsById(id);
    }

    /**
     * 查询受访者列表
     * 
     * @param crmRespondents 受访者
     * @return 受访者
     */
    @Override
    public List<CrmRespondents> selectCrmRespondentsList(CrmRespondents crmRespondents)
    {
        return crmRespondentsMapper.selectCrmRespondentsList(crmRespondents);
    }

    /**
     * 新增受访者
     * 
     * @param crmRespondents 受访者
     * @return 结果
     */
    @Override
    public int insertCrmRespondents(CrmRespondents crmRespondents)
    {
        crmRespondents.setCreateTime(DateUtils.getNowDate());
        int rows = crmRespondentsMapper.insertCrmRespondents(crmRespondents);

        // 处理 metadata 中的浏览历史（_sessionId / _browseHistory）
        tryProcessBrowseHistory(crmRespondents);

        return rows;
    }

    private void tryProcessBrowseHistory(CrmRespondents respondent)
    {
        String metadata = respondent.getMetadata();
        if (!StringUtils.hasText(metadata))
        {
            return;
        }

        try
        {
            JSONObject meta = JSONObject.parseObject(metadata);

            String sessionId = meta.getString("_sessionId");
            String browseHistory = meta.getString("_browseHistory");

            // 有 _sessionId 即可缝合（独立表历史 PV）；_browseHistory 仅作兜底补洞
            if (!StringUtils.hasText(sessionId))
            {
                return;
            }

            // 优先使用已绑定的 contactId
            if (StringUtils.hasText(respondent.getContactId()))
            {
                visitorSessionService.stitch(sessionId, browseHistory,
                        "Contact", respondent.getContactId());
                return;
            }

            // 尝试从 metadata 中获取手机号进行身份解析
            String mobilePhone = meta.getString("mobilePhone");
            if (mobilePhone == null)
            {
                mobilePhone = meta.getString("mobile_phone");
            }
            if (StringUtils.hasText(mobilePhone))
            {
                String contactId = identityService.findContactByIdentity("mobile_phone", mobilePhone.trim());
                if (StringUtils.hasText(contactId))
                {
                    visitorSessionService.stitch(sessionId, browseHistory, "Contact", contactId);
                    return;
                }
            }

            String leadId = meta.getString("leadId");
            if (leadId == null)
            {
                leadId = meta.getString("lead_id");
            }
            if (StringUtils.hasText(leadId))
            {
                visitorSessionService.stitch(sessionId, browseHistory, "Lead", leadId.trim());
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to process browse history from respondent metadata: respondentId={}",
                    respondent.getId(), e);
        }
    }

    /**
     * 修改受访者
     * 
     * @param crmRespondents 受访者
     * @return 结果
     */
    @Override
    public int updateCrmRespondents(CrmRespondents crmRespondents)
    {
        crmRespondents.setUpdateTime(DateUtils.getNowDate());
        return crmRespondentsMapper.updateCrmRespondents(crmRespondents);
    }

    /**
     * 批量删除受访者
     * 
     * @param ids 需要删除的受访者主键
     * @return 结果
     */
    @Override
    public int deleteCrmRespondentsByIds(String[] ids)
    {
        return crmRespondentsMapper.deleteCrmRespondentsByIds(ids);
    }

    /**
     * 删除受访者信息
     * 
     * @param id 受访者主键
     * @return 结果
     */
    @Override
    public int deleteCrmRespondentsById(String id)
    {
        return crmRespondentsMapper.deleteCrmRespondentsById(id);
    }
}
