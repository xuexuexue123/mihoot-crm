package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmConversionHistoryMapper;
import com.ruoyi.crm.domain.CrmConversionHistory;
import com.ruoyi.crm.service.ICrmConversionHistoryService;
import com.ruoyi.crm.service.nurture.support.EventNurtureHookSupport;
import com.ruoyi.crm.wecom.WecomLeadConvertSyncSupport;
import com.ruoyi.crm.wechatmp.service.MpLeadConvertSyncSupport;

/**
 * 转化历史Service业务层处理
 * 
 * @author luohd
 * @date 2025-04-29
 */
@Service
public class CrmConversionHistoryServiceImpl implements ICrmConversionHistoryService 
{
    @Autowired
    private CrmConversionHistoryMapper crmConversionHistoryMapper;

    @Autowired
    private EventNurtureHookSupport nurtureHookSupport;

    @Autowired
    private WecomLeadConvertSyncSupport wecomLeadConvertSyncSupport;

    @Autowired
    private MpLeadConvertSyncSupport mpLeadConvertSyncSupport;

    /**
     * 查询转化历史
     * 
     * @param id 转化历史主键
     * @return 转化历史
     */
    @Override
    public CrmConversionHistory selectCrmConversionHistoryById(String id)
    {
        return crmConversionHistoryMapper.selectCrmConversionHistoryById(id);
    }

    /**
     * 查询转化历史列表
     * 
     * @param crmConversionHistory 转化历史
     * @return 转化历史
     */
    @Override
    public List<CrmConversionHistory> selectCrmConversionHistoryList(CrmConversionHistory crmConversionHistory)
    {
        return crmConversionHistoryMapper.selectCrmConversionHistoryList(crmConversionHistory);
    }

    /**
     * 新增转化历史
     * 
     * @param crmConversionHistory 转化历史
     * @return 结果
     */
    @Override
    public int insertCrmConversionHistory(CrmConversionHistory crmConversionHistory)
    {
        crmConversionHistory.setCreateTime(DateUtils.getNowDate());
        int rows = crmConversionHistoryMapper.insertCrmConversionHistory(crmConversionHistory);
        if (rows > 0)
        {
            nurtureHookSupport.onConversionHistoryCreated(crmConversionHistory);
            if (WecomLeadConvertSyncSupport.isLeadToContactConversion(crmConversionHistory))
            {
                wecomLeadConvertSyncSupport.syncAfterLeadConverted(
                        crmConversionHistory.getLeadId(), crmConversionHistory.getTargetId());
                mpLeadConvertSyncSupport.syncAfterLeadConverted(
                        crmConversionHistory.getLeadId(), crmConversionHistory.getTargetId());
            }
        }
        return rows;
    }

    /**
     * 修改转化历史
     * 
     * @param crmConversionHistory 转化历史
     * @return 结果
     */
    @Override
    public int updateCrmConversionHistory(CrmConversionHistory crmConversionHistory)
    {
        crmConversionHistory.setUpdateTime(DateUtils.getNowDate());
        return crmConversionHistoryMapper.updateCrmConversionHistory(crmConversionHistory);
    }

    /**
     * 批量删除转化历史
     * 
     * @param ids 需要删除的转化历史主键
     * @return 结果
     */
    @Override
    public int deleteCrmConversionHistoryByIds(String[] ids)
    {
        return crmConversionHistoryMapper.deleteCrmConversionHistoryByIds(ids);
    }

    /**
     * 删除转化历史信息
     * 
     * @param id 转化历史主键
     * @return 结果
     */
    @Override
    public int deleteCrmConversionHistoryById(String id)
    {
        return crmConversionHistoryMapper.deleteCrmConversionHistoryById(id);
    }
}
