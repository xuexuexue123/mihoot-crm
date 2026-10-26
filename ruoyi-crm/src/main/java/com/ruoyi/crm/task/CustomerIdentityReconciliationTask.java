package com.ruoyi.crm.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmCustomerIdentityMapper;

/**
 * 客户身份表与 crm_contact 表对账任务。
 * 检测 crm_contact 中有但 crm_customer_identity 中没有的身份记录，并补写。
 * 由 Quartz 定时调度触发。
 *
 * @author luohd
 * @date 2026-06-16
 */
@Component("customerIdentityReconciliationTask")
public class CustomerIdentityReconciliationTask
{
    private static final Logger log = LoggerFactory.getLogger(CustomerIdentityReconciliationTask.class);

    @Autowired
    private CrmContactMapper contactMapper;

    @Autowired
    private CrmCustomerIdentityMapper identityMapper;

    /**
     * 对账：扫描 crm_contact 中的身份字段，将缺失的记录补写到 crm_customer_identity。
     * 幂等安全，使用 insertOrIgnore 避免重复。
     */
    public void runReconciliation()
    {
        log.info("CustomerIdentityReconciliationTask started");

        int mobileCount = 0;
        int emailCount = 0;
        int wecomCount = 0;
        int wechatCount = 0;

        try
        {
            // 扫描所有 contact（分页避免一次加载过多）
            int offset = 0;
            int batchSize = 500;
            boolean hasMore = true;

            while (hasMore)
            {
                CrmContact query = new CrmContact();
                query.getParams().put("offset", offset);
                query.getParams().put("limit", batchSize);

                List<CrmContact> contacts = contactMapper.selectCrmContactList(query);
                if (contacts == null || contacts.isEmpty())
                {
                    hasMore = false;
                    break;
                }

                for (CrmContact c : contacts)
                {
                    String contactId = c.getContactId();
                    if (contactId == null)
                    {
                        continue;
                    }

                    // 检查并补写手机号
                    if (c.getMobilePhone() != null && !c.getMobilePhone().isEmpty())
                    {
                        CrmCustomerIdentity existing = identityMapper.selectByTypeValue("mobile_phone", c.getMobilePhone());
                        if (existing == null)
                        {
                            CrmCustomerIdentity id = new CrmCustomerIdentity();
                            id.setContactId(contactId);
                            id.setIdentityType("mobile_phone");
                            id.setIdentityValue(c.getMobilePhone());
                            id.setSource("reconciliation");
                            id.setIsPrimary(true);
                            identityMapper.insertOrIgnore(id);
                            mobileCount++;
                        }
                    }

                    // 检查并补写个人邮箱
                    if (c.getPersonalEmail() != null && !c.getPersonalEmail().isEmpty())
                    {
                        CrmCustomerIdentity existing = identityMapper.selectByTypeValue("email", c.getPersonalEmail());
                        if (existing == null)
                        {
                            CrmCustomerIdentity id = new CrmCustomerIdentity();
                            id.setContactId(contactId);
                            id.setIdentityType("email");
                            id.setIdentityValue(c.getPersonalEmail());
                            id.setSource("reconciliation");
                            identityMapper.insertOrIgnore(id);
                            emailCount++;
                        }
                    }

                    // 检查并补写工作邮箱
                    if (c.getWorkEmail() != null && !c.getWorkEmail().isEmpty()
                            && !c.getWorkEmail().equals(c.getPersonalEmail()))
                    {
                        CrmCustomerIdentity existing = identityMapper.selectByTypeValue("email", c.getWorkEmail());
                        if (existing == null)
                        {
                            CrmCustomerIdentity id = new CrmCustomerIdentity();
                            id.setContactId(contactId);
                            id.setIdentityType("email");
                            id.setIdentityValue(c.getWorkEmail());
                            id.setSource("reconciliation");
                            identityMapper.insertOrIgnore(id);
                            emailCount++;
                        }
                    }

                    // 检查并补写企微 external_userid
                    if (c.getWecomExternalUserid() != null && !c.getWecomExternalUserid().isEmpty())
                    {
                        CrmCustomerIdentity existing = identityMapper.selectByTypeValue(
                                "wecom_external_userid", c.getWecomExternalUserid());
                        if (existing == null)
                        {
                            CrmCustomerIdentity id = new CrmCustomerIdentity();
                            id.setContactId(contactId);
                            id.setIdentityType("wecom_external_userid");
                            id.setIdentityValue(c.getWecomExternalUserid());
                            id.setSource("reconciliation");
                            identityMapper.insertOrIgnore(id);
                            wecomCount++;
                        }
                    }

                    // 检查并补写微信
                    if (c.getWechat() != null && !c.getWechat().isEmpty())
                    {
                        CrmCustomerIdentity existing = identityMapper.selectByTypeValue("wechat_id", c.getWechat());
                        if (existing == null)
                        {
                            CrmCustomerIdentity id = new CrmCustomerIdentity();
                            id.setContactId(contactId);
                            id.setIdentityType("wechat_id");
                            id.setIdentityValue(c.getWechat());
                            id.setSource("reconciliation");
                            identityMapper.insertOrIgnore(id);
                            wechatCount++;
                        }
                    }
                }

                offset += batchSize;

                if (contacts.size() < batchSize)
                {
                    hasMore = false;
                }
            }
        }
        catch (Exception e)
        {
            log.error("CustomerIdentityReconciliationTask failed", e);
        }

        log.info("CustomerIdentityReconciliationTask completed: mobile={}, email={}, wecom={}, wechat={}",
                mobileCount, emailCount, wecomCount, wechatCount);
    }
}
