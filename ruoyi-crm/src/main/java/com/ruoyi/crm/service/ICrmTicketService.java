package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmTicket;

/**
 * 工单Service接口
 * 
 * @author luohd
 * @date 2025-04-08
 */
public interface ICrmTicketService 
{
    /**
     * 查询工单
     * 
     * @param id 工单主键
     * @return 工单
     */
    public CrmTicket selectCrmTicketById(String id);

    /**
     * 查询工单列表
     * 
     * @param crmTicket 工单
     * @return 工单集合
     */
    public List<CrmTicket> selectCrmTicketList(CrmTicket crmTicket);

    /**
     * 新增工单
     * 
     * @param crmTicket 工单
     * @return 结果
     */
    public int insertCrmTicket(CrmTicket crmTicket);

    /**
     * 修改工单
     * 
     * @param crmTicket 工单
     * @return 结果
     */
    public int updateCrmTicket(CrmTicket crmTicket);

    /**
     * 批量删除工单
     * 
     * @param ids 需要删除的工单主键集合
     * @return 结果
     */
    public int deleteCrmTicketByIds(String[] ids);

    /**
     * 删除工单信息
     * 
     * @param id 工单主键
     * @return 结果
     */
    public int deleteCrmTicketById(String id);
}
