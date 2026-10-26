package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmQuoteoption;

/**
 * 报价产品Mapper接口
 * 
 * @author luohd
 * @date 2024-02-14
 */
public interface CrmQuoteoptionMapper 
{
    /**
     * 查询报价产品
     * 
     * @param optionId 报价产品主键
     * @return 报价产品
     */
    public CrmQuoteoption selectCrmQuoteoptionByOptionId(String optionId);

    /**
     * 查询报价产品列表
     * 
     * @param crmQuoteoption 报价产品
     * @return 报价产品集合
     */
    public List<CrmQuoteoption> selectCrmQuoteoptionList(CrmQuoteoption crmQuoteoption);

    /**
     * 新增报价产品
     * 
     * @param crmQuoteoption 报价产品
     * @return 结果
     */
    public int insertCrmQuoteoption(CrmQuoteoption crmQuoteoption);

    /**
     * 修改报价产品
     * 
     * @param crmQuoteoption 报价产品
     * @return 结果
     */
    public int updateCrmQuoteoption(CrmQuoteoption crmQuoteoption);

    /**
     * 删除报价产品
     * 
     * @param optionId 报价产品主键
     * @return 结果
     */
    public int deleteCrmQuoteoptionByOptionId(String optionId);

    /**
     * 批量删除报价产品
     * 
     * @param optionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmQuoteoptionByOptionIds(String[] optionIds);
}
