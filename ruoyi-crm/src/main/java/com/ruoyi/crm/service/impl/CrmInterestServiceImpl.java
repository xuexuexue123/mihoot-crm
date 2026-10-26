package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.LogUtils;
import com.ruoyi.crm.mapper.CrmFundpaidMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.UUID;

import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.domain.CrmFundpaid;
import com.ruoyi.crm.mapper.CrmInterestMapper;
import com.ruoyi.crm.domain.CrmInterest;
import com.ruoyi.crm.service.ICrmInterestService;

/**
 * 权益变更Service业务层处理
 * 
 * @author luohd
 * @date 2024-07-09
 */
@Service
public class CrmInterestServiceImpl implements ICrmInterestService 
{
    @Autowired
    private CrmInterestMapper crmInterestMapper;

    @Autowired
    private CrmFundpaidMapper crmFundpaidMapper;

    /**
     * 查询权益变更
     * 
     * @param interestId 权益变更主键
     * @return 权益变更
     */
    @Override
    public CrmInterest selectCrmInterestByInterestId(String interestId)
    {
        return crmInterestMapper.selectCrmInterestByInterestId(interestId);
    }

    /**
     * 查询权益变更列表
     * 
     * @param crmInterest 权益变更
     * @return 权益变更
     */
    @Override
    public List<CrmInterest> selectCrmInterestList(CrmInterest crmInterest)
    {
        List <CrmInterest> crmInterestList = crmInterestMapper.selectCrmInterestList(crmInterest);

        for (int i = 0; i < crmInterestList.size(); i++) {
            CrmInterest item = crmInterestList.get(i);

            List<CrmFundpaid> crmFundpaidList = crmFundpaidMapper.selectCrmFundpaidByInterestId(item.getInterestId());
            item.setCrmFundpaidList(crmFundpaidList);
        }

        return crmInterestList;
    }

    /**
     * 新增权益变更
     * 
     * @param crmInterest 权益变更
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCrmInterest(CrmInterest crmInterest)
    {
        crmInterest.setCreateTime(DateUtils.getNowDate());
        int rows = crmInterestMapper.insertCrmInterest(crmInterest);
        insertCrmFundpaid(crmInterest);
        return rows;
    }

    /**
     * 修改权益变更
     * 
     * @param crmInterest 权益变更
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCrmInterest(CrmInterest crmInterest)
    {
        crmInterest.setUpdateTime(DateUtils.getNowDate());
        crmInterestMapper.deleteCrmFundpaidByInterestId(crmInterest.getInterestId());
        insertCrmFundpaid(crmInterest);
        return crmInterestMapper.updateCrmInterest(crmInterest);
    }

    /**
     * 批量删除权益变更
     * 
     * @param interestIds 需要删除的权益变更主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmInterestByInterestIds(String[] interestIds)
    {
        crmInterestMapper.deleteCrmFundpaidByInterestIds(interestIds);
        return crmInterestMapper.deleteCrmInterestByInterestIds(interestIds);
    }

    /**
     * 删除权益变更信息
     * 
     * @param interestId 权益变更主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmInterestByInterestId(String interestId)
    {
        crmInterestMapper.deleteCrmFundpaidByInterestId(interestId);
        return crmInterestMapper.deleteCrmInterestByInterestId(interestId);
    }

    /**
     * 新增基金投资信息
     * 
     * @param crmInterest 权益变更对象
     */
    public void insertCrmFundpaid(CrmInterest crmInterest)
    {
        List<CrmFundpaid> crmFundpaidList = crmInterest.getCrmFundpaidList();
        String interestId = crmInterest.getInterestId();
        if (StringUtils.isNotNull(crmFundpaidList))
        {
            List<CrmFundpaid> list = new ArrayList<CrmFundpaid>();
            for (CrmFundpaid crmFundpaid : crmFundpaidList)
            {
                crmFundpaid.setPaidId(UUID.randomUUID().toString());
                crmFundpaid.setInterestId(interestId);
                list.add(crmFundpaid);
            }
            if (list.size() > 0)
            {
                crmInterestMapper.batchCrmFundpaid(list);
            }
        }
    }
}
