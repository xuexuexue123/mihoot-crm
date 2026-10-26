package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.WaitResumePayloadRequest;
import com.ruoyi.crm.domain.dto.WaitResumePreRegisterRequest;
import com.ruoyi.crm.domain.vo.WaitResumeVO;

/**
 * Wait 节点契约服务（预注册 / 按 key 查询 / 恢复）
 */
public interface IWWaitResumeContractService
{
    WaitResumeVO preRegister(WaitResumePreRegisterRequest request);

    WaitResumeVO getByResumeKey(String resumeKey);

    WaitResumeVO resume(String resumeKey, WaitResumePayloadRequest request);

    WaitResumeVO resumeWithPayloadJson(String resumeKey, String payloadJson);
}
