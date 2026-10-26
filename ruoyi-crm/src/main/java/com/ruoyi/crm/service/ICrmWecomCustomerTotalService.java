package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.wecom.WecomCustomerTotalResponse;

/**
 * 企业微信全企业客户总数拉取服务
 */
public interface ICrmWecomCustomerTotalService
{
    /**
     * 实时拉取企微API并聚合客户总数（10~50s）
     */
    WecomCustomerTotalResponse pullCustomerTotal();
}
