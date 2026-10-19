package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.domain.CrmQuoteoption;
import com.ruoyi.crm.mapper.CrmQuoteMapper;
import com.ruoyi.crm.domain.CrmQuote;
import com.ruoyi.crm.service.ICrmQuoteService;

/**
 * 报价单Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-10
 */
@Service
public class CrmQuoteServiceImpl implements ICrmQuoteService 
{
    private static final Logger logger = LoggerFactory.getLogger(CrmQuoteServiceImpl.class);

    @Autowired
    private CrmQuoteMapper crmQuoteMapper;

    /**
     * 查询报价单
     * 
     * @param quoteId 报价单主键
     * @return 报价单
     */
    @Override
    public CrmQuote selectCrmQuoteByQuoteId(String quoteId)
    {
        return crmQuoteMapper.selectCrmQuoteByQuoteId(quoteId);
    }

    /**
     * 查询报价单列表
     * 
     * @param crmQuote 报价单
     * @return 报价单
     */
    @Override
    public List<CrmQuote> selectCrmQuoteList(CrmQuote crmQuote)
    {
        return crmQuoteMapper.selectCrmQuoteList(crmQuote);
    }

    /**
     * 新增报价单
     * 
     * @param crmQuote 报价单
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCrmQuote(CrmQuote crmQuote)
    {
        crmQuote.setCreateTime(DateUtils.getNowDate());
        int rows = crmQuoteMapper.insertCrmQuote(crmQuote);
        insertCrmQuoteoption(crmQuote);
        return rows;
    }

    /**
     * 修改报价单
     * 
     * @param crmQuote 报价单
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCrmQuote(CrmQuote crmQuote)
    {
        crmQuote.setUpdateTime(DateUtils.getNowDate());
        crmQuoteMapper.deleteCrmQuoteoptionByQuoteId(crmQuote.getQuoteId());
        insertCrmQuoteoption(crmQuote);
        return crmQuoteMapper.updateCrmQuote(crmQuote);
    }

    /**
     * 批量删除报价单
     * 
     * @param quoteIds 需要删除的报价单主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmQuoteByQuoteIds(String[] quoteIds)
    {
        crmQuoteMapper.deleteCrmQuoteoptionByQuoteIds(quoteIds);
        return crmQuoteMapper.deleteCrmQuoteByQuoteIds(quoteIds);
    }

    /**
     * 删除报价单信息
     * 
     * @param quoteId 报价单主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmQuoteByQuoteId(String quoteId)
    {
        crmQuoteMapper.deleteCrmQuoteoptionByQuoteId(quoteId);
        return crmQuoteMapper.deleteCrmQuoteByQuoteId(quoteId);
    }

    /**
     * 新增报价产品信息
     * 
     * @param crmQuote 报价单对象
     */
    public void insertCrmQuoteoption(CrmQuote crmQuote)
    {
        List<CrmQuoteoption> crmQuoteoptionList = crmQuote.getCrmQuoteoptionList();
        String quoteId = crmQuote.getQuoteId();
        logger.info("insertCrmQuoteoption: quoteId={}, quoteOptionList size={}", quoteId,
            crmQuoteoptionList != null ? crmQuoteoptionList.size() : 0);
        if (StringUtils.isNotNull(crmQuoteoptionList))
        {
            List<CrmQuoteoption> list = new ArrayList<CrmQuoteoption>();
            for (CrmQuoteoption crmQuoteoption : crmQuoteoptionList)
            {
                crmQuoteoption.setQuoteId(quoteId);
                if (StringUtils.isEmpty(crmQuoteoption.getOptionId()))
                {
                    crmQuoteoption.setOptionId(UUID.randomUUID().toString());
                }
                list.add(crmQuoteoption);
            }
            if (list.size() > 0)
            {
                crmQuoteMapper.batchCrmQuoteoption(list);
            }
        }
    }
}
