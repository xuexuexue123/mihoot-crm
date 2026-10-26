package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.wecom.WecomMyCustomersDetailRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMyCustomersDetailResult;
import com.ruoyi.crm.domain.dto.wecom.WecomMyCustomersListResult;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactImportResult;

/**
 * 销售「我的企微客户」服务
 */
public interface ICrmWecomMyCustomersService
{
    /**
     * 获取当前登录销售成员的企微客户 external_userid 列表（强制 snUserid，不信任客户端 userid）。
     */
    WecomMyCustomersListResult listMyCustomers();

    /**
     * 批量获取客户详情（id 须属于当前用户客户列表子集，最多 20 条）。
     */
    WecomMyCustomersDetailResult getCustomerDetails(WecomMyCustomersDetailRequest request);

    /**
     * 同步当前用户企微客户到 crm_wecom_external_contact。
     */
    WecomExternalContactImportResult importMine();
}
