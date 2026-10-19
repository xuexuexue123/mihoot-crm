package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmPaymenttermMapper;
import com.ruoyi.crm.domain.CrmPaymentterm;
import com.ruoyi.crm.service.ICrmPaymenttermService;
import com.ruoyi.crm.service.IPatientClvService;
import com.ruoyi.crm.rule.SmartPushEventExecutor;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

import java.math.BigDecimal;

/**
 * 收款Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-15
 */
@Service
public class CrmPaymenttermServiceImpl implements ICrmPaymenttermService 
{
    @Autowired
    private CrmPaymenttermMapper crmPaymenttermMapper;

    @Autowired
    private IPatientClvService patientClvService;

    @Autowired
    private SmartPushEventExecutor smartPushEventExecutor;

    /**
     * 查询收款
     * 
     * @param termId 收款主键
     * @return 收款
     */
    @Override
    public CrmPaymentterm selectCrmPaymenttermByTermId(String termId)
    {
        return crmPaymenttermMapper.selectCrmPaymenttermByTermId(termId);
    }

    /**
     * 查询收款列表
     * 
     * @param crmPaymentterm 收款
     * @return 收款
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmPaymentterm> selectCrmPaymenttermList(CrmPaymentterm crmPaymentterm)
    {
        return crmPaymenttermMapper.selectCrmPaymenttermList(crmPaymentterm);
    }

    /**
     * 查询本周收款列表
     *
     * @param crmPaymentterm 收款
     * @return 收款
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmPaymentterm> selectCrmPaymenttermListWeekly(CrmPaymentterm crmPaymentterm)
    {
        return crmPaymenttermMapper.selectCrmPaymenttermListWeekly(crmPaymentterm);
    }

    /**
     * 新增收款
     * 
     * @param crmPaymentterm 收款
     * @return 结果
     */
    @Override
    public int insertCrmPaymentterm(CrmPaymentterm crmPaymentterm)
    {
        validateRefund(crmPaymentterm);
        crmPaymentterm.setCreateTime(DateUtils.getNowDate());
        int rows = crmPaymenttermMapper.insertCrmPaymentterm(crmPaymentterm);
        if (rows > 0)
        {
            patientClvService.enqueueRecomputeForContact(null, crmPaymentterm.getAccountId());
        }
        return rows;
    }

    /**
     * 修改收款
     * 
     * @param crmPaymentterm 收款
     * @return 结果
     */
    @Override
    public int updateCrmPaymentterm(CrmPaymentterm crmPaymentterm)
    {
        validateRefund(crmPaymentterm);
        crmPaymentterm.setUpdateTime(DateUtils.getNowDate());

        // 检测 isPaid 从未付到已付的转变，触发支付完成事件
        boolean wasPaid = false;
        CrmPaymentterm existing = crmPaymenttermMapper.selectCrmPaymenttermByTermId(crmPaymentterm.getTermId());
        if (existing != null && existing.getIsPaid() != null)
        {
            wasPaid = "Y".equalsIgnoreCase(existing.getIsPaid()) || "1".equals(existing.getIsPaid()) || "true".equalsIgnoreCase(existing.getIsPaid());
        }

        int rows = crmPaymenttermMapper.updateCrmPaymentterm(crmPaymentterm);
        if (rows > 0)
        {
            patientClvService.enqueueRecomputeForContact(null, crmPaymentterm.getAccountId());

            boolean nowPaid = false;
            if (crmPaymentterm.getIsPaid() != null)
            {
                nowPaid = "Y".equalsIgnoreCase(crmPaymentterm.getIsPaid()) || "1".equals(crmPaymentterm.getIsPaid()) || "true".equalsIgnoreCase(crmPaymentterm.getIsPaid());
            }
            if (nowPaid && !wasPaid)
            {
                // Paymentterm 无直接 contactId，通过 accountId 查找关联联系人
                // 此处仅记录事件，具体联系人匹配由 on_event 策略的 ruleMatcher 负责
                smartPushEventExecutor.onEvent("payment_completed", null, DynamicDataSourceContextHolder.getDataSourceKey());
            }
        }
        return rows;
    }

    private void validateRefund(CrmPaymentterm term)
    {
        if (term.getRefundAmount() == null)
        {
            return;
        }
        BigDecimal paid = term.getPaidAmount();
        if (paid == null)
        {
            CrmPaymentterm existing = crmPaymenttermMapper.selectCrmPaymenttermByTermId(term.getTermId());
            if (existing != null)
            {
                paid = existing.getPaidAmount();
            }
        }
        if (paid != null && term.getRefundAmount().compareTo(paid) > 0)
        {
            throw new ServiceException("退款金额不能超过已收金额");
        }
    }

    /**
     * 批量删除收款
     * 
     * @param termIds 需要删除的收款主键
     * @return 结果
     */
    @Override
    public int deleteCrmPaymenttermByTermIds(String[] termIds)
    {
        return crmPaymenttermMapper.deleteCrmPaymenttermByTermIds(termIds);
    }

    /**
     * 删除收款信息
     * 
     * @param termId 收款主键
     * @return 结果
     */
    @Override
    public int deleteCrmPaymenttermByTermId(String termId)
    {
        return crmPaymenttermMapper.deleteCrmPaymenttermByTermId(termId);
    }
}
