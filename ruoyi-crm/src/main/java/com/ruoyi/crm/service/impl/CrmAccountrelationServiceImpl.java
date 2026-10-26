package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.aliyun.SendEmail;
import com.ruoyi.crm.domain.CrmAccountrelationEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmAccountrelationMapper;
import com.ruoyi.crm.domain.CrmAccountrelation;
import com.ruoyi.crm.service.ICrmAccountrelationService;

/**
 * 客户关系Service业务层处理
 * 
 * @author luohd
 * @date 2024-05-19
 */
@Service
public class CrmAccountrelationServiceImpl implements ICrmAccountrelationService 
{
    @Autowired
    private CrmAccountrelationMapper crmAccountrelationMapper;

    /**
     * 查询客户关系
     * 
     * @param relateId 客户关系主键
     * @return 客户关系
     */
    @Override
    public CrmAccountrelation selectCrmAccountrelationByRelateId(String relateId)
    {
        return crmAccountrelationMapper.selectCrmAccountrelationByRelateId(relateId);
    }

    /**
     * 查询客户关系列表
     * 
     * @param crmAccountrelation 客户关系
     * @return 客户关系
     */
    @Override
    public List<CrmAccountrelation> selectCrmAccountrelationList(CrmAccountrelation crmAccountrelation)
    {
        return crmAccountrelationMapper.selectCrmAccountrelationList(crmAccountrelation);
    }

    /**
     * 新增客户关系
     * 
     * @param crmAccountrelation 客户关系
     * @return 结果
     */
    @Override
    public int insertCrmAccountrelation(CrmAccountrelation crmAccountrelation)
    {
        crmAccountrelation.setCreateTime(DateUtils.getNowDate());
        return crmAccountrelationMapper.insertCrmAccountrelation(crmAccountrelation);
    }

    /**
     * 修改客户关系
     * 
     * @param crmAccountrelation 客户关系
     * @return 结果
     */
    @Override
    public int updateCrmAccountrelation(CrmAccountrelationEmail crmAccountrelation) throws Exception {

        String status = crmAccountrelation.getStatus();
        if (status.equals("待推荐")) {
            //发送邮件
            SendEmail.SingleSendMailSample(
                    crmAccountrelation.getToAddress(),
                    crmAccountrelation.getSubject(),
                    crmAccountrelation.getHtmlBody());

        }

        crmAccountrelation.setUpdateTime(DateUtils.getNowDate());
        return crmAccountrelationMapper.updateCrmAccountrelation(crmAccountrelation);
    }

    /**
     * 根据基金信息 删除关系
     *
     * @param crmAccountrelation 客户关系主键
     * @return 结果
     */
    public int deleteCrmAccountrelationByAccountId(CrmAccountrelation crmAccountrelation) {
        return crmAccountrelationMapper.deleteCrmAccountrelationByAccountId(crmAccountrelation);
    }
    /**
     * 批量删除客户关系
     * 
     * @param relateIds 需要删除的客户关系主键
     * @return 结果
     */
    @Override
    public int deleteCrmAccountrelationByRelateIds(String[] relateIds)
    {
        return crmAccountrelationMapper.deleteCrmAccountrelationByRelateIds(relateIds);
    }

    /**
     * 删除客户关系信息
     * 
     * @param relateId 客户关系主键
     * @return 结果
     */
    @Override
    public int deleteCrmAccountrelationByRelateId(String relateId)
    {
        return crmAccountrelationMapper.deleteCrmAccountrelationByRelateId(relateId);
    }
}
