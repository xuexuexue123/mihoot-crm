package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmQuote;
import com.ruoyi.crm.domain.CrmQuoteoption;

/**
 * 报价单Mapper接口
 * 
 * @author luohd
 * @date 2024-02-10
 */
public interface CrmQuoteMapper 
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
     * 删除报价单
     * 
     * @param quoteId 报价单主键
     * @return 结果
     */
    public int deleteCrmQuoteByQuoteId(String quoteId);

    /**
     * 批量删除报价单
     * 
     * @param quoteIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmQuoteByQuoteIds(String[] quoteIds);

    /**
     * 批量删除报价产品
     * 
     * @param quoteIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmQuoteoptionByQuoteIds(String[] quoteIds);
    
    /**
     * 批量新增报价产品
     * 
     * @param crmQuoteoptionList 报价产品列表
     * @return 结果
     */
    public int batchCrmQuoteoption(List<CrmQuoteoption> crmQuoteoptionList);
    

    /**
     * 通过报价单主键删除报价产品信息
     * 
     * @param quoteId 报价单ID
     * @return 结果
     */
    public int deleteCrmQuoteoptionByQuoteId(String quoteId);
}
