package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmTicketMapper;
import com.ruoyi.crm.domain.CrmTicket;
import com.ruoyi.crm.service.ICrmTicketService;

/**
 * 工单Service业务层处理
 * 
 * @author luohd
 * @date 2025-04-08
 */
@Service
public class CrmTicketServiceImpl implements ICrmTicketService 
{
    @Autowired
    private CrmTicketMapper crmTicketMapper;

    /**
     * 查询工单
     * 
     * @param id 工单主键
     * @return 工单
     */
    @Override
    public CrmTicket selectCrmTicketById(String id)
    {
        return crmTicketMapper.selectCrmTicketById(id);
    }

    /**
     * 查询工单列表
     * 
     * @param crmTicket 工单
     * @return 工单
     */
    @Override
    public List<CrmTicket> selectCrmTicketList(CrmTicket crmTicket)
    {
        return crmTicketMapper.selectCrmTicketList(crmTicket);
    }

    /**
     * 新增工单
     * 
     * @param crmTicket 工单
     * @return 结果
     */
    @Override
    public int insertCrmTicket(CrmTicket crmTicket)
    {
        crmTicket.setCreateTime(DateUtils.getNowDate());
        return crmTicketMapper.insertCrmTicket(crmTicket);
    }

    /**
     * 修改工单
     * 
     * @param crmTicket 工单
     * @return 结果
     */
    @Override
    public int updateCrmTicket(CrmTicket crmTicket)
    {
        crmTicket.setUpdateTime(DateUtils.getNowDate());
        return crmTicketMapper.updateCrmTicket(crmTicket);
    }

    /**
     * 批量删除工单
     * 
     * @param ids 需要删除的工单主键
     * @return 结果
     */
    @Override
    public int deleteCrmTicketByIds(String[] ids)
    {
        return crmTicketMapper.deleteCrmTicketByIds(ids);
    }

    /**
     * 删除工单信息
     * 
     * @param id 工单主键
     * @return 结果
     */
    @Override
    public int deleteCrmTicketById(String id)
    {
        return crmTicketMapper.deleteCrmTicketById(id);
    }
}
