package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.aliyun.SendEmail;
import com.ruoyi.crm.domain.CrmOptyrelationEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmOptyrelationMapper;
import com.ruoyi.crm.domain.CrmOptyrelation;
import com.ruoyi.crm.service.ICrmOptyrelationService;

/**
 * 机会关系Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-07
 */
@Service
public class CrmOptyrelationServiceImpl implements ICrmOptyrelationService 
{
    @Autowired
    private CrmOptyrelationMapper crmOptyrelationMapper;

    /**
     * 查询机会关系
     * 
     * @param relateId 机会关系主键
     * @return 机会关系
     */
    @Override
    public CrmOptyrelation selectCrmOptyrelationByRelateId(String relateId)
    {
        return crmOptyrelationMapper.selectCrmOptyrelationByRelateId(relateId);
    }

    /**
     * 查询机会关系列表
     * 
     * @param crmOptyrelation 机会关系
     * @return 机会关系
     */
    @Override
    public List<CrmOptyrelation> selectCrmOptyrelationList(CrmOptyrelation crmOptyrelation)
    {
        return crmOptyrelationMapper.selectCrmOptyrelationList(crmOptyrelation);
    }

    /**
     * 新增机会关系
     * 
     * @param crmOptyrelation 机会关系
     * @return 结果
     */
    @Override
    public int insertCrmOptyrelation(CrmOptyrelation crmOptyrelation)
    {
        crmOptyrelation.setCreateTime(DateUtils.getNowDate());
        return crmOptyrelationMapper.insertCrmOptyrelation(crmOptyrelation);
    }

    /**
     * 修改机会关系
     * 
     * @param crmOptyrelation 机会关系
     * @return 结果
     */
    @Override
    public int updateCrmOptyrelation(CrmOptyrelationEmail crmOptyrelation) throws Exception {
        crmOptyrelation.setUpdateTime(DateUtils.getNowDate());

        String status = crmOptyrelation.getStatus();
        if (status.equals("待推荐")) {
            //发送邮件
            SendEmail.SingleSendMailSample(
                    crmOptyrelation.getToAddress(),
                    crmOptyrelation.getSubject(),
                    crmOptyrelation.getHtmlBody());

        }

        return crmOptyrelationMapper.updateCrmOptyrelation(crmOptyrelation);
    }

    /**
     * 批量删除机会关系
     * 
     * @param relateIds 需要删除的机会关系主键
     * @return 结果
     */
    @Override
    public int deleteCrmOptyrelationByRelateIds(String[] relateIds)
    {
        return crmOptyrelationMapper.deleteCrmOptyrelationByRelateIds(relateIds);
    }

    /**
     * 删除机会关系信息
     * 
     * @param relateId 机会关系主键
     * @return 结果
     */
    @Override
    public int deleteCrmOptyrelationByRelateId(String relateId)
    {
        return crmOptyrelationMapper.deleteCrmOptyrelationByRelateId(relateId);
    }
}
