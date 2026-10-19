package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmQuote;

/**
 * 报价单Service接口
 * 
 * @author luohd
 * @date 2024-02-10
 */
public interface ICrmQuoteService 
{
    /**
     * 查询报价单
     * 
     * @param quoteId 报价单主键
     * @return 报价单
     */
    public CrmQuote selectCrmQuoteByQuoteId(String quoteId);

    /**
     * 查询报价单列表
     * 
     * @param crmQuote 报价单
     * @return 报价单集合
     */
    public List<CrmQuote> selectCrmQuoteList(CrmQuote crmQuote);

    /**
     * 新增报价单
     * 
     * @param crmQuote 报价单
     * @return 结果
     */
    public int insertCrmQuote(CrmQuote crmQuote);

    /**
     * 修改报价单
     * 
     * @param crmQuote 报价单
     * @return 结果
     */
    public int updateCrmQuote(CrmQuote crmQuote);

    /**
     * 批量删除报价单
     * 
     * @param quoteIds 需要删除的报价单主键集合
     * @return 结果
     */
    public int deleteCrmQuoteByQuoteIds(String[] quoteIds);

    /**
     * 删除报价单信息
     * 
     * @param quoteId 报价单主键
     * @return 结果
     */
    public int deleteCrmQuoteByQuoteId(String quoteId);
}
